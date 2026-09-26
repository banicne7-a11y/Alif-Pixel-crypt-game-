package com.example.data.auth

import android.content.Context
import android.content.SharedPreferences
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject
import java.util.concurrent.TimeUnit

class RealtimeDatabaseManager(context: Context) {

    private val prefs: SharedPreferences = context.getSharedPreferences("pixel_crypt_rtdb", Context.MODE_PRIVATE)

    private val client = OkHttpClient.Builder()
        .connectTimeout(6, TimeUnit.SECONDS)
        .readTimeout(8, TimeUnit.SECONDS)
        .writeTimeout(8, TimeUnit.SECONDS)
        .build()

    private val defaultFirebaseUrl = "https://gen-lang-client-0700590538-default-rtdb.firebaseio.com"

    private val _config = MutableStateFlow(
        RealtimeDbConfig(
            databaseUrl = prefs.getString("rtdb_url", defaultFirebaseUrl) ?: defaultFirebaseUrl,
            authToken = prefs.getString("rtdb_auth_token", "") ?: "",
            autoSyncEnabled = prefs.getBoolean("rtdb_auto_sync", true),
            lastSyncTime = prefs.getLong("rtdb_last_sync", System.currentTimeMillis()),
            lastStatusMessage = prefs.getString("rtdb_status_msg", "Firebase Cloud Connected") ?: "Firebase Cloud Connected",
            isConnected = prefs.getBoolean("rtdb_is_connected", true)
        )
    )
    val config: StateFlow<RealtimeDbConfig> = _config.asStateFlow()

    private val _isBusy = MutableStateFlow(false)
    val isBusy: StateFlow<Boolean> = _isBusy.asStateFlow()

    fun updateConfig(databaseUrl: String, authToken: String, autoSync: Boolean) {
        val trimmedUrl = databaseUrl.trim().removeSuffix("/")
        val trimmedToken = authToken.trim()

        prefs.edit()
            .putString("rtdb_url", trimmedUrl)
            .putString("rtdb_auth_token", trimmedToken)
            .putBoolean("rtdb_auto_sync", autoSync)
            .apply()

        _config.value = _config.value.copy(
            databaseUrl = trimmedUrl,
            authToken = trimmedToken,
            autoSyncEnabled = autoSync
        )
    }

    private fun buildEndpointUrl(path: String): String? {
        val base = _config.value.databaseUrl.trim().removeSuffix("/")
        if (base.isEmpty()) return null

        val cleanPath = path.removePrefix("/")
        var url = if (base.endsWith(".firebaseio.com")) {
            "$base/$cleanPath.json"
        } else if (!base.endsWith(".json")) {
            "$base/$cleanPath"
        } else {
            base
        }

        val token = _config.value.authToken.trim()
        if (token.isNotEmpty()) {
            url += if (url.contains("?")) "&auth=$token" else "?auth=$token"
        }
        return url
    }

    suspend fun testConnection(testUrl: String? = null, testToken: String? = null): Pair<Boolean, String> {
        val targetBase = (testUrl ?: _config.value.databaseUrl).trim().removeSuffix("/")
        val token = (testToken ?: _config.value.authToken).trim()

        if (targetBase.isEmpty()) {
            return false to "Please enter a Database URL first."
        }

        if (!targetBase.startsWith("http://") && !targetBase.startsWith("https://")) {
            return false to "URL must begin with https:// or http://"
        }

        _isBusy.value = true
        return withContext(Dispatchers.IO) {
            try {
                val pingUrl = if (targetBase.contains("firebaseio.com")) {
                    var u = "$targetBase/.json?shallow=true"
                    if (token.isNotEmpty()) u += "&auth=$token"
                    u
                } else {
                    targetBase
                }

                val startTime = System.currentTimeMillis()
                val request = Request.Builder()
                    .url(pingUrl)
                    .get()
                    .build()

                client.newCall(request).execute().use { response ->
                    val elapsed = System.currentTimeMillis() - startTime
                    val code = response.code
                    if (code in 200..299) {
                        val msg = "Connected successfully! (${code} OK - ${elapsed}ms)"
                        prefs.edit()
                            .putBoolean("rtdb_is_connected", true)
                            .putString("rtdb_status_msg", msg)
                            .apply()
                        _config.value = _config.value.copy(isConnected = true, lastStatusMessage = msg)
                        true to msg
                    } else if (code == 401 || code == 403) {
                        val msg = "Permission Denied (${code}). Check your Auth Token / Rules."
                        prefs.edit()
                            .putBoolean("rtdb_is_connected", false)
                            .putString("rtdb_status_msg", msg)
                            .apply()
                        _config.value = _config.value.copy(isConnected = false, lastStatusMessage = msg)
                        false to msg
                    } else {
                        val msg = "Database responded with HTTP $code"
                        prefs.edit().putString("rtdb_status_msg", msg).apply()
                        _config.value = _config.value.copy(lastStatusMessage = msg)
                        false to msg
                    }
                }
            } catch (e: Exception) {
                val msg = "Connection failed: ${e.localizedMessage ?: "Network error"}"
                prefs.edit()
                    .putBoolean("rtdb_is_connected", false)
                    .putString("rtdb_status_msg", msg)
                    .apply()
                _config.value = _config.value.copy(isConnected = false, lastStatusMessage = msg)
                false to msg
            } finally {
                _isBusy.value = false
            }
        }
    }

    suspend fun initializeDatabase(): Pair<Boolean, String> {
        val targetUrl = buildEndpointUrl("pixel_crypt/game_info")
            ?: return false to "Realtime Database URL is not set."

        _isBusy.value = true
        return withContext(Dispatchers.IO) {
            try {
                val json = JSONObject().apply {
                    put("game_title", "Alif Pixel Crypt")
                    put("status", "online")
                    put("firebase_project", "gen-lang-client-0700590538")
                    put("last_ping_time", System.currentTimeMillis())
                }
                val body = json.toString().toRequestBody("application/json; charset=utf-8".toMediaType())
                val request = Request.Builder()
                    .url(targetUrl)
                    .put(body)
                    .build()

                client.newCall(request).execute().use { response ->
                    val code = response.code
                    if (code in 200..299) {
                        val msg = "Connected to Firebase successfully!"
                        prefs.edit()
                            .putBoolean("rtdb_is_connected", true)
                            .putString("rtdb_status_msg", msg)
                            .putLong("rtdb_last_sync", System.currentTimeMillis())
                            .apply()
                        _config.value = _config.value.copy(
                            isConnected = true,
                            lastStatusMessage = msg,
                            lastSyncTime = System.currentTimeMillis()
                        )
                        true to msg
                    } else if (code == 401 || code == 403) {
                        val msg = "Permission Denied: Firebase Rules must be set to read: true, write: true"
                        prefs.edit()
                            .putBoolean("rtdb_is_connected", false)
                            .putString("rtdb_status_msg", msg)
                            .apply()
                        _config.value = _config.value.copy(isConnected = false, lastStatusMessage = msg)
                        false to msg
                    } else {
                        val msg = "Firebase HTTP response code: $code"
                        _config.value = _config.value.copy(lastStatusMessage = msg)
                        false to msg
                    }
                }
            } catch (e: Exception) {
                val msg = "Firebase connection error: ${e.localizedMessage ?: "Network error"}"
                _config.value = _config.value.copy(isConnected = false, lastStatusMessage = msg)
                false to msg
            } finally {
                _isBusy.value = false
            }
        }
    }

    suspend fun syncToCloud(playerData: CloudPlayerData): Pair<Boolean, String> {
        val targetUrl = buildEndpointUrl("pixel_crypt/users/${playerData.userId}")
            ?: return false to "Realtime Database URL is not set."

        _isBusy.value = true
        return withContext(Dispatchers.IO) {
            try {
                val json = JSONObject().apply {
                    put("userId", playerData.userId)
                    put("username", playerData.username)
                    put("email", playerData.email)
                    put("goldCoins", playerData.goldCoins)
                    put("freeHints", playerData.freeHints)
                    put("selectedSkin", playerData.selectedSkin)
                    put("selectedTheme", playerData.selectedTheme)
                    put("dailyStreak", playerData.dailyStreak)
                    put("totalStars", playerData.totalStars)
                    put("lastSyncedAt", System.currentTimeMillis())

                    val skinsArr = JSONArray()
                    playerData.unlockedSkins.forEach { skinsArr.put(it) }
                    put("unlockedSkins", skinsArr)

                    val themesArr = JSONArray()
                    playerData.unlockedThemes.forEach { themesArr.put(it) }
                    put("unlockedThemes", themesArr)
                }

                val body = json.toString().toRequestBody("application/json; charset=utf-8".toMediaType())
                val request = Request.Builder()
                    .url(targetUrl)
                    .put(body)
                    .build()

                client.newCall(request).execute().use { response ->
                    if (response.isSuccessful) {
                        val now = System.currentTimeMillis()
                        val msg = "Synced to Realtime Database successfully!"
                        prefs.edit()
                            .putLong("rtdb_last_sync", now)
                            .putString("rtdb_status_msg", msg)
                            .putBoolean("rtdb_is_connected", true)
                            .apply()
                        _config.value = _config.value.copy(
                            lastSyncTime = now,
                            lastStatusMessage = msg,
                            isConnected = true
                        )
                        true to msg
                    } else if (response.code == 401 || response.code == 403) {
                        val msg = "Permission Denied: Please set Firebase Rules to .read: true, .write: true"
                        prefs.edit()
                            .putBoolean("rtdb_is_connected", false)
                            .putString("rtdb_status_msg", msg)
                            .apply()
                        _config.value = _config.value.copy(isConnected = false, lastStatusMessage = msg)
                        false to msg
                    } else {
                        val msg = "Cloud Sync failed: HTTP ${response.code}"
                        prefs.edit().putString("rtdb_status_msg", msg).apply()
                        _config.value = _config.value.copy(lastStatusMessage = msg)
                        false to msg
                    }
                }
            } catch (e: Exception) {
                val msg = "Sync failed: ${e.localizedMessage ?: "Network error"}"
                prefs.edit().putString("rtdb_status_msg", msg).apply()
                _config.value = _config.value.copy(lastStatusMessage = msg)
                false to msg
            } finally {
                _isBusy.value = false
            }
        }
    }

    suspend fun restoreFromCloud(userId: String): Pair<CloudPlayerData?, String> {
        val targetUrl = buildEndpointUrl("pixel_crypt/users/$userId")
            ?: return null to "Realtime Database URL is not set."

        _isBusy.value = true
        return withContext(Dispatchers.IO) {
            try {
                val request = Request.Builder()
                    .url(targetUrl)
                    .get()
                    .build()

                client.newCall(request).execute().use { response ->
                    if (!response.isSuccessful) {
                        return@withContext null to "Failed to fetch: HTTP ${response.code}"
                    }
                    val bodyStr = response.body?.string()
                    if (bodyStr.isNullOrEmpty() || bodyStr == "null") {
                        return@withContext null to "No cloud backup found for this account."
                    }

                    val obj = JSONObject(bodyStr)
                    val skinsList = mutableListOf<String>()
                    val skinsArr = obj.optJSONArray("unlockedSkins")
                    if (skinsArr != null) {
                        for (i in 0 until skinsArr.length()) {
                            skinsList.add(skinsArr.getString(i))
                        }
                    }
                    if (skinsList.isEmpty()) skinsList.add("knight_silver")

                    val themesList = mutableListOf<String>()
                    val themesArr = obj.optJSONArray("unlockedThemes")
                    if (themesArr != null) {
                        for (i in 0 until themesArr.length()) {
                            themesList.add(themesArr.getString(i))
                        }
                    }
                    if (themesList.isEmpty()) themesList.add("crypt_default")

                    val data = CloudPlayerData(
                        userId = obj.optString("userId", userId),
                        username = obj.optString("username", "Hero"),
                        email = obj.optString("email", ""),
                        goldCoins = obj.optInt("goldCoins", 150),
                        freeHints = obj.optInt("freeHints", 3),
                        selectedSkin = obj.optString("selectedSkin", "knight_silver"),
                        unlockedSkins = skinsList,
                        selectedTheme = obj.optString("selectedTheme", "crypt_default"),
                        unlockedThemes = themesList,
                        dailyStreak = obj.optInt("dailyStreak", 1),
                        totalStars = obj.optInt("totalStars", 0),
                        lastSyncedAt = obj.optLong("lastSyncedAt", System.currentTimeMillis())
                    )

                    val msg = "Cloud profile restored successfully!"
                    prefs.edit()
                        .putLong("rtdb_last_sync", System.currentTimeMillis())
                        .putString("rtdb_status_msg", msg)
                        .apply()
                    _config.value = _config.value.copy(
                        lastSyncTime = System.currentTimeMillis(),
                        lastStatusMessage = msg
                    )
                    data to msg
                }
            } catch (e: Exception) {
                null to "Restore failed: ${e.localizedMessage ?: "Unknown error"}"
            } finally {
                _isBusy.value = false
            }
        }
    }
}
