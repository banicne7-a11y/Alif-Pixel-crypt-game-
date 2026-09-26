package com.example.data.auth

import android.content.Context
import android.content.SharedPreferences
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import org.json.JSONArray
import org.json.JSONObject
import java.security.MessageDigest
import java.util.UUID

class AuthManager(context: Context) {

    private val prefs: SharedPreferences = context.getSharedPreferences("pixel_crypt_auth", Context.MODE_PRIVATE)

    private val _currentUser = MutableStateFlow<UserAccount?>(null)
    val currentUser: StateFlow<UserAccount?> = _currentUser.asStateFlow()

    private val _authError = MutableStateFlow<String?>(null)
    val authError: StateFlow<String?> = _authError.asStateFlow()

    init {
        loadActiveUser()
    }

    fun clearError() {
        _authError.value = null
    }

    private fun hashPassword(password: String): String {
        val bytes = MessageDigest.getInstance("SHA-256").digest(password.toByteArray())
        return bytes.joinToString("") { "%02x".format(it) }
    }

    private fun loadActiveUser() {
        val currentUserId = prefs.getString("active_user_id", null) ?: return
        val users = getAllRegisteredUsers()
        _currentUser.value = users.find { it.id == currentUserId }
    }

    fun getAllRegisteredUsers(): List<UserAccount> {
        val jsonStr = prefs.getString("registered_users_list", "[]") ?: "[]"
        val list = mutableListOf<UserAccount>()
        try {
            val arr = JSONArray(jsonStr)
            for (i in 0 until arr.length()) {
                val obj = arr.getJSONObject(i)
                list.add(
                    UserAccount(
                        id = obj.getString("id"),
                        username = obj.getString("username"),
                        email = obj.getString("email"),
                        passwordHash = obj.getString("passwordHash"),
                        avatarSkinId = obj.optString("avatarSkinId", "knight_silver"),
                        createdAt = obj.optLong("createdAt", System.currentTimeMillis()),
                        lastLoginAt = obj.optLong("lastLoginAt", System.currentTimeMillis())
                    )
                )
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return list
    }

    private fun saveUsers(users: List<UserAccount>) {
        val arr = JSONArray()
        for (u in users) {
            val obj = JSONObject().apply {
                put("id", u.id)
                put("username", u.username)
                put("email", u.email)
                put("passwordHash", u.passwordHash)
                put("avatarSkinId", u.avatarSkinId)
                put("createdAt", u.createdAt)
                put("lastLoginAt", u.lastLoginAt)
            }
            arr.put(obj)
        }
        prefs.edit().putString("registered_users_list", arr.toString()).apply()
    }

    fun register(username: String, email: String, password: String, avatarSkinId: String = "knight_silver"): Boolean {
        clearError()
        val cleanName = username.trim()
        val cleanEmail = email.trim().lowercase()

        if (cleanName.length < 2) {
            _authError.value = "Username must be at least 2 characters."
            return false
        }
        if (!cleanEmail.contains("@") || !cleanEmail.contains(".")) {
            _authError.value = "Please enter a valid email address."
            return false
        }
        if (password.length < 4) {
            _authError.value = "Password must be at least 4 characters."
            return false
        }

        val existing = getAllRegisteredUsers()
        if (existing.any { it.email.equals(cleanEmail, ignoreCase = true) }) {
            _authError.value = "An account with this email already exists."
            return false
        }
        if (existing.any { it.username.equals(cleanName, ignoreCase = true) }) {
            _authError.value = "Username '$cleanName' is already taken."
            return false
        }

        val newUser = UserAccount(
            id = UUID.randomUUID().toString(),
            username = cleanName,
            email = cleanEmail,
            passwordHash = hashPassword(password),
            avatarSkinId = avatarSkinId,
            createdAt = System.currentTimeMillis(),
            lastLoginAt = System.currentTimeMillis()
        )

        val updated = existing + newUser
        saveUsers(updated)

        _currentUser.value = newUser
        prefs.edit().putString("active_user_id", newUser.id).apply()
        return true
    }

    fun login(emailOrUsername: String, password: String): Boolean {
        clearError()
        val query = emailOrUsername.trim()
        if (query.isEmpty() || password.isEmpty()) {
            _authError.value = "Please fill in all fields."
            return false
        }

        val users = getAllRegisteredUsers()
        val target = users.find {
            it.email.equals(query, ignoreCase = true) || it.username.equals(query, ignoreCase = true)
        }

        if (target == null) {
            _authError.value = "No account found with this username/email."
            return false
        }

        val hashed = hashPassword(password)
        if (target.passwordHash != hashed) {
            _authError.value = "Incorrect password. Please try again."
            return false
        }

        val updatedUser = target.copy(lastLoginAt = System.currentTimeMillis())
        val updatedList = users.map { if (it.id == updatedUser.id) updatedUser else it }
        saveUsers(updatedList)

        _currentUser.value = updatedUser
        prefs.edit().putString("active_user_id", updatedUser.id).apply()
        return true
    }

    fun logout() {
        _currentUser.value = null
        prefs.edit().remove("active_user_id").apply()
    }
}
