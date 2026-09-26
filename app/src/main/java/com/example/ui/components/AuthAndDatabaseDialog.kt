package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.CloudDownload
import androidx.compose.material.icons.filled.CloudUpload
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.data.auth.RealtimeDbConfig
import com.example.data.auth.UserAccount
import com.example.ui.theme.*
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

enum class AuthTab {
    LOGIN,
    REGISTER,
    ACCOUNT
}

@Composable
fun AuthAndDatabaseDialog(
    currentUser: UserAccount?,
    rtdbConfig: RealtimeDbConfig,
    isRtdbBusy: Boolean,
    onLogin: (emailOrUser: String, pass: String, onResult: (Boolean, String?) -> Unit) -> Unit,
    onRegister: (username: String, email: String, pass: String, onResult: (Boolean, String?) -> Unit) -> Unit,
    onLogout: () -> Unit,
    onUpdateRtdbConfig: (url: String, token: String, autoSync: Boolean) -> Unit,
    onTestRtdbConnection: (url: String, token: String, onResult: (Boolean, String) -> Unit) -> Unit,
    onSyncToCloud: (onResult: (Boolean, String) -> Unit) -> Unit,
    onRestoreFromCloud: (onResult: (Boolean, String) -> Unit) -> Unit,
    onDismiss: () -> Unit
) {
    var selectedTab by remember {
        mutableStateOf(if (currentUser != null) AuthTab.ACCOUNT else AuthTab.LOGIN)
    }

    // Login Form State
    var loginIdentifier by remember { mutableStateOf("") }
    var loginPassword by remember { mutableStateOf("") }
    var loginPasswordVisible by remember { mutableStateOf(false) }
    var loginError by remember { mutableStateOf<String?>(null) }
    var isLoggingIn by remember { mutableStateOf(false) }

    // Register Form State
    var regUsername by remember { mutableStateOf("") }
    var regEmail by remember { mutableStateOf("") }
    var regPassword by remember { mutableStateOf("") }
    var regConfirmPassword by remember { mutableStateOf("") }
    var regPasswordVisible by remember { mutableStateOf(false) }
    var regError by remember { mutableStateOf<String?>(null) }
    var isRegistering by remember { mutableStateOf(false) }

    // Sync notification state
    var cloudStatusNotice by remember { mutableStateOf<String?>(null) }
    var isSyncingNow by remember { mutableStateOf(false) }
    var isRestoringNow by remember { mutableStateOf(false) }

    val focusManager = LocalFocusManager.current

    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 12.dp)
                .testTag("auth_dialog"),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = DungeonSurface),
            border = BorderStroke(2.dp, PixelCyan)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(18.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                // Header Row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "🔥",
                            fontSize = 20.sp
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = if (currentUser != null) "PLAYER PROFILE" else "PLAYER LOGIN",
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp,
                            color = RetroTextPrimary
                        )
                    }

                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier.size(32.dp).testTag("dialog_close_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Close",
                            tint = RetroTextSecondary,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Navigation Tabs (Only Login & Register when logged out, or Account when logged in)
                if (currentUser == null) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp))
                            .background(DungeonDarkBg)
                            .padding(4.dp),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        TabButton(
                            title = "LOG IN",
                            isSelected = selectedTab == AuthTab.LOGIN,
                            onClick = { selectedTab = AuthTab.LOGIN },
                            testTag = "tab_login",
                            modifier = Modifier.weight(1f)
                        )
                        TabButton(
                            title = "REGISTER",
                            isSelected = selectedTab == AuthTab.REGISTER,
                            onClick = { selectedTab = AuthTab.REGISTER },
                            testTag = "tab_register",
                            modifier = Modifier.weight(1f)
                        )
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                }

                // Tab Content
                when (selectedTab) {
                    AuthTab.LOGIN -> {
                        // --- LOGIN FORM ---
                        Text(
                            text = "Log in to save your score, coins & stars to Firebase Cloud.",
                            fontSize = 12.sp,
                            color = RetroTextSecondary,
                            modifier = Modifier.padding(bottom = 12.dp)
                        )

                        AnimatedVisibility(visible = loginError != null) {
                            Card(
                                modifier = Modifier.fillMaxWidth().padding(bottom = 10.dp),
                                colors = CardDefaults.cardColors(containerColor = PixelRuby.copy(alpha = 0.2f)),
                                border = BorderStroke(1.dp, PixelRuby)
                            ) {
                                Text(
                                    text = loginError ?: "",
                                    color = PixelRuby,
                                    fontSize = 12.sp,
                                    modifier = Modifier.padding(8.dp)
                                )
                            }
                        }

                        OutlinedTextField(
                            value = loginIdentifier,
                            onValueChange = {
                                loginIdentifier = it
                                loginError = null
                            },
                            label = { Text("Email or Username", fontSize = 12.sp) },
                            leadingIcon = {
                                Icon(Icons.Default.Person, contentDescription = null, tint = PixelCyan, modifier = Modifier.size(18.dp))
                            },
                            singleLine = true,
                            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
                            keyboardActions = KeyboardActions(onNext = { focusManager.moveFocus(FocusDirection.Down) }),
                            modifier = Modifier.fillMaxWidth().testTag("login_identifier_input"),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = PixelCyan,
                                unfocusedBorderColor = DungeonBorder,
                                focusedTextColor = RetroTextPrimary,
                                unfocusedTextColor = RetroTextPrimary
                            )
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        OutlinedTextField(
                            value = loginPassword,
                            onValueChange = {
                                loginPassword = it
                                loginError = null
                            },
                            label = { Text("Password", fontSize = 12.sp) },
                            leadingIcon = {
                                Icon(Icons.Default.Lock, contentDescription = null, tint = PixelCyan, modifier = Modifier.size(18.dp))
                            },
                            trailingIcon = {
                                IconButton(onClick = { loginPasswordVisible = !loginPasswordVisible }) {
                                    Icon(
                                        imageVector = if (loginPasswordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                                        contentDescription = "Toggle password",
                                        tint = RetroTextSecondary,
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                            },
                            visualTransformation = if (loginPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                            singleLine = true,
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password, imeAction = ImeAction.Done),
                            keyboardActions = KeyboardActions(onDone = { focusManager.clearFocus() }),
                            modifier = Modifier.fillMaxWidth().testTag("login_password_input"),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = PixelCyan,
                                unfocusedBorderColor = DungeonBorder,
                                focusedTextColor = RetroTextPrimary,
                                unfocusedTextColor = RetroTextPrimary
                            )
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        Button(
                            onClick = {
                                if (loginIdentifier.isBlank() || loginPassword.isBlank()) {
                                    loginError = "Please enter username/email and password."
                                    return@Button
                                }
                                isLoggingIn = true
                                onLogin(loginIdentifier, loginPassword) { success, errorMsg ->
                                    isLoggingIn = false
                                    if (success) {
                                        selectedTab = AuthTab.ACCOUNT
                                    } else {
                                        loginError = errorMsg ?: "Login failed."
                                    }
                                }
                            },
                            enabled = !isLoggingIn,
                            modifier = Modifier.fillMaxWidth().height(48.dp).testTag("login_submit_button"),
                            colors = ButtonDefaults.buttonColors(containerColor = PixelCyan),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            if (isLoggingIn) {
                                CircularProgressIndicator(modifier = Modifier.size(20.dp), color = Color.Black, strokeWidth = 2.dp)
                            } else {
                                Text(
                                    text = "LOG IN & SYNC",
                                    color = Color.Black,
                                    fontWeight = FontWeight.Bold,
                                    fontFamily = FontFamily.Monospace
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.Center,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("New here? ", fontSize = 12.sp, color = RetroTextSecondary)
                            Text(
                                text = "Create an account",
                                fontSize = 12.sp,
                                color = PixelGold,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.clickable { selectedTab = AuthTab.REGISTER }.testTag("switch_to_register")
                            )
                        }
                    }

                    AuthTab.REGISTER -> {
                        // --- REGISTER FORM ---
                        Text(
                            text = "Create an account to automatically sync coins, relics & progress with Firebase.",
                            fontSize = 12.sp,
                            color = RetroTextSecondary,
                            modifier = Modifier.padding(bottom = 12.dp)
                        )

                        AnimatedVisibility(visible = regError != null) {
                            Card(
                                modifier = Modifier.fillMaxWidth().padding(bottom = 10.dp),
                                colors = CardDefaults.cardColors(containerColor = PixelRuby.copy(alpha = 0.2f)),
                                border = BorderStroke(1.dp, PixelRuby)
                            ) {
                                Text(
                                    text = regError ?: "",
                                    color = PixelRuby,
                                    fontSize = 12.sp,
                                    modifier = Modifier.padding(8.dp)
                                )
                            }
                        }

                        OutlinedTextField(
                            value = regUsername,
                            onValueChange = {
                                regUsername = it
                                regError = null
                            },
                            label = { Text("Player Name / Username", fontSize = 12.sp) },
                            leadingIcon = {
                                Icon(Icons.Default.Person, contentDescription = null, tint = PixelGold, modifier = Modifier.size(18.dp))
                            },
                            singleLine = true,
                            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
                            keyboardActions = KeyboardActions(onNext = { focusManager.moveFocus(FocusDirection.Down) }),
                            modifier = Modifier.fillMaxWidth().testTag("register_username_input"),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = PixelGold,
                                unfocusedBorderColor = DungeonBorder,
                                focusedTextColor = RetroTextPrimary,
                                unfocusedTextColor = RetroTextPrimary
                            )
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        OutlinedTextField(
                            value = regEmail,
                            onValueChange = {
                                regEmail = it
                                regError = null
                            },
                            label = { Text("Email Address", fontSize = 12.sp) },
                            leadingIcon = {
                                Icon(Icons.Default.Email, contentDescription = null, tint = PixelGold, modifier = Modifier.size(18.dp))
                            },
                            singleLine = true,
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email, imeAction = ImeAction.Next),
                            keyboardActions = KeyboardActions(onNext = { focusManager.moveFocus(FocusDirection.Down) }),
                            modifier = Modifier.fillMaxWidth().testTag("register_email_input"),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = PixelGold,
                                unfocusedBorderColor = DungeonBorder,
                                focusedTextColor = RetroTextPrimary,
                                unfocusedTextColor = RetroTextPrimary
                            )
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        OutlinedTextField(
                            value = regPassword,
                            onValueChange = {
                                regPassword = it
                                regError = null
                            },
                            label = { Text("Password (min 4 chars)", fontSize = 12.sp) },
                            leadingIcon = {
                                Icon(Icons.Default.Lock, contentDescription = null, tint = PixelGold, modifier = Modifier.size(18.dp))
                            },
                            trailingIcon = {
                                IconButton(onClick = { regPasswordVisible = !regPasswordVisible }) {
                                    Icon(
                                        imageVector = if (regPasswordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                                        contentDescription = "Toggle password",
                                        tint = RetroTextSecondary,
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                            },
                            visualTransformation = if (regPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                            singleLine = true,
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password, imeAction = ImeAction.Next),
                            keyboardActions = KeyboardActions(onNext = { focusManager.moveFocus(FocusDirection.Down) }),
                            modifier = Modifier.fillMaxWidth().testTag("register_password_input"),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = PixelGold,
                                unfocusedBorderColor = DungeonBorder,
                                focusedTextColor = RetroTextPrimary,
                                unfocusedTextColor = RetroTextPrimary
                            )
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        OutlinedTextField(
                            value = regConfirmPassword,
                            onValueChange = {
                                regConfirmPassword = it
                                regError = null
                            },
                            label = { Text("Confirm Password", fontSize = 12.sp) },
                            leadingIcon = {
                                Icon(Icons.Default.Lock, contentDescription = null, tint = PixelGold, modifier = Modifier.size(18.dp))
                            },
                            visualTransformation = if (regPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                            singleLine = true,
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password, imeAction = ImeAction.Done),
                            keyboardActions = KeyboardActions(onDone = { focusManager.clearFocus() }),
                            modifier = Modifier.fillMaxWidth().testTag("register_confirm_password_input"),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = PixelGold,
                                unfocusedBorderColor = DungeonBorder,
                                focusedTextColor = RetroTextPrimary,
                                unfocusedTextColor = RetroTextPrimary
                            )
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        Button(
                            onClick = {
                                if (regUsername.isBlank() || regEmail.isBlank() || regPassword.isBlank()) {
                                    regError = "Please fill in all registration fields."
                                    return@Button
                                }
                                if (regPassword != regConfirmPassword) {
                                    regError = "Passwords do not match."
                                    return@Button
                                }
                                isRegistering = true
                                onRegister(regUsername, regEmail, regPassword) { success, errorMsg ->
                                    isRegistering = false
                                    if (success) {
                                        selectedTab = AuthTab.ACCOUNT
                                    } else {
                                        regError = errorMsg ?: "Registration failed."
                                    }
                                }
                            },
                            enabled = !isRegistering,
                            modifier = Modifier.fillMaxWidth().height(48.dp).testTag("register_submit_button"),
                            colors = ButtonDefaults.buttonColors(containerColor = PixelGold),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            if (isRegistering) {
                                CircularProgressIndicator(modifier = Modifier.size(20.dp), color = Color.Black, strokeWidth = 2.dp)
                            } else {
                                Text(
                                    text = "REGISTER & JOIN",
                                    color = Color.Black,
                                    fontWeight = FontWeight.Bold,
                                    fontFamily = FontFamily.Monospace
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.Center,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("Already have an account? ", fontSize = 12.sp, color = RetroTextSecondary)
                            Text(
                                text = "Log In",
                                fontSize = 12.sp,
                                color = PixelCyan,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.clickable { selectedTab = AuthTab.LOGIN }.testTag("switch_to_login")
                            )
                        }
                    }

                    AuthTab.ACCOUNT -> {
                        // --- LOGGED IN USER PROFILE ---
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(containerColor = DungeonCard),
                            border = BorderStroke(1.dp, DungeonBorder)
                        ) {
                            Column(modifier = Modifier.padding(14.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(48.dp)
                                            .clip(RoundedCornerShape(8.dp))
                                            .background(PixelEmerald.copy(alpha = 0.2f))
                                            .border(1.dp, PixelEmerald, RoundedCornerShape(8.dp)),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(
                                            Icons.Default.Person,
                                            contentDescription = null,
                                            tint = PixelEmerald,
                                            modifier = Modifier.size(28.dp)
                                        )
                                    }

                                    Spacer(modifier = Modifier.width(12.dp))

                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                            text = currentUser?.username ?: "Guest Player",
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 16.sp,
                                            color = RetroTextPrimary
                                        )
                                        Text(
                                            text = currentUser?.email ?: "Offline Account",
                                            fontSize = 12.sp,
                                            color = RetroTextSecondary
                                        )
                                    }

                                    Box(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(12.dp))
                                            .background(PixelEmerald.copy(alpha = 0.15f))
                                            .padding(horizontal = 8.dp, vertical = 4.dp)
                                    ) {
                                        Text(
                                            text = "ONLINE",
                                            fontSize = 10.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = PixelEmerald
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.height(14.dp))

                                Divider(color = DungeonBorder)

                                Spacer(modifier = Modifier.height(10.dp))

                                // Firebase Cloud Status Indicator
                                val isConnected = rtdbConfig.isConnected
                                val statusText = cloudStatusNotice ?: rtdbConfig.lastStatusMessage
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(if (isConnected) PixelEmerald.copy(alpha = 0.12f) else PixelRuby.copy(alpha = 0.12f))
                                        .border(1.dp, if (isConnected) PixelEmerald else PixelRuby, RoundedCornerShape(8.dp))
                                        .padding(8.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        imageVector = if (isConnected) Icons.Default.CheckCircle else Icons.Default.Close,
                                        contentDescription = null,
                                        tint = if (isConnected) PixelEmerald else PixelRuby,
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = if (isConnected) "Firebase: Active (gen-lang-client-0700590538) 🔥" else "Firebase: Setup Required",
                                        fontSize = 11.sp,
                                        color = if (isConnected) PixelEmerald else PixelRuby,
                                        fontWeight = FontWeight.SemiBold
                                    )
                                }

                                if (statusText.isNotEmpty()) {
                                    Spacer(modifier = Modifier.height(6.dp))
                                    Text(
                                        text = statusText,
                                        fontSize = 10.sp,
                                        color = if (isConnected) PixelGold else PixelRuby,
                                        lineHeight = 14.sp
                                    )
                                }

                                Spacer(modifier = Modifier.height(10.dp))

                                val sdf = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())
                                val joined = sdf.format(Date(currentUser?.createdAt ?: System.currentTimeMillis()))
                                Text(
                                    text = "Member Since: $joined",
                                    fontSize = 11.sp,
                                    color = RetroTextDisabled
                                )

                                Spacer(modifier = Modifier.height(14.dp))

                                // Cloud Sync & Restore Buttons
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Button(
                                        onClick = {
                                            isSyncingNow = true
                                            onSyncToCloud { success, msg ->
                                                isSyncingNow = false
                                                cloudStatusNotice = if (success) "Progress saved to Firebase!" else msg
                                            }
                                        },
                                        enabled = !isSyncingNow && !isRtdbBusy,
                                        modifier = Modifier.weight(1f).height(40.dp).testTag("sync_to_cloud_button"),
                                        shape = RoundedCornerShape(8.dp),
                                        colors = ButtonDefaults.buttonColors(containerColor = PixelEmerald)
                                    ) {
                                        if (isSyncingNow) {
                                            CircularProgressIndicator(modifier = Modifier.size(16.dp), color = Color.Black, strokeWidth = 2.dp)
                                        } else {
                                            Icon(Icons.Default.CloudUpload, contentDescription = null, tint = Color.Black, modifier = Modifier.size(16.dp))
                                            Spacer(modifier = Modifier.width(4.dp))
                                            Text("Save Cloud", color = Color.Black, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                        }
                                    }

                                    OutlinedButton(
                                        onClick = {
                                            isRestoringNow = true
                                            onRestoreFromCloud { success, msg ->
                                                isRestoringNow = false
                                                cloudStatusNotice = if (success) "Progress restored from Firebase!" else msg
                                            }
                                        },
                                        enabled = !isRestoringNow && !isRtdbBusy,
                                        modifier = Modifier.weight(1f).height(40.dp).testTag("restore_from_cloud_button"),
                                        shape = RoundedCornerShape(8.dp),
                                        border = BorderStroke(1.dp, PixelGold)
                                    ) {
                                        if (isRestoringNow) {
                                            CircularProgressIndicator(modifier = Modifier.size(16.dp), color = PixelGold, strokeWidth = 2.dp)
                                        } else {
                                            Icon(Icons.Default.CloudDownload, contentDescription = null, tint = PixelGold, modifier = Modifier.size(16.dp))
                                            Spacer(modifier = Modifier.width(4.dp))
                                            Text("Restore", color = PixelGold, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                        }
                                    }
                                }

                                Spacer(modifier = Modifier.height(10.dp))

                                Button(
                                    onClick = {
                                        onLogout()
                                        selectedTab = AuthTab.LOGIN
                                    },
                                    modifier = Modifier.fillMaxWidth().height(38.dp).testTag("logout_button"),
                                    shape = RoundedCornerShape(8.dp),
                                    colors = ButtonDefaults.buttonColors(containerColor = PixelRuby.copy(alpha = 0.85f))
                                ) {
                                    Text("LOG OUT", color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun TabButton(
    title: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    testTag: String,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(6.dp))
            .background(if (isSelected) DungeonCard else Color.Transparent)
            .clickable(onClick = onClick)
            .padding(horizontal = 12.dp, vertical = 8.dp)
            .testTag(testTag),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = title,
            fontSize = 12.sp,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
            fontFamily = FontFamily.Monospace,
            color = if (isSelected) PixelCyan else RetroTextSecondary
        )
    }
}
