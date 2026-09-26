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
import androidx.compose.material.icons.filled.Cloud
import androidx.compose.material.icons.filled.CloudDownload
import androidx.compose.material.icons.filled.CloudSync
import androidx.compose.material.icons.filled.CloudUpload
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Key
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Storage
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material.icons.filled.Wifi
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
import androidx.compose.ui.text.style.TextAlign
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
    REALTIME_DB,
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

    // Realtime Database Form State
    var rtdbUrl by remember { mutableStateOf(rtdbConfig.databaseUrl) }
    var rtdbToken by remember { mutableStateOf(rtdbConfig.authToken) }
    var rtdbAutoSync by remember { mutableStateOf(rtdbConfig.autoSyncEnabled) }
    var rtdbStatusMsg by remember { mutableStateOf(rtdbConfig.lastStatusMessage) }
    var isTestingConnection by remember { mutableStateOf(false) }
    var isSyncingNow by remember { mutableStateOf(false) }
    var isRestoringNow by remember { mutableStateOf(false) }

    val focusManager = LocalFocusManager.current

    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 12.dp)
                .testTag("auth_and_database_dialog"),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = DungeonSurface),
            border = BorderStroke(2.dp, PixelCyan)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                // Header Row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.CloudSync,
                            contentDescription = "Cloud & Auth",
                            tint = PixelCyan,
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "PLAYER CLOUD & AUTH",
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp,
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

                Spacer(modifier = Modifier.height(12.dp))

                // Navigation Tabs
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(8.dp))
                        .background(DungeonDarkBg)
                        .padding(3.dp),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    if (currentUser == null) {
                        TabButton(
                            title = "LOGIN",
                            isSelected = selectedTab == AuthTab.LOGIN,
                            onClick = { selectedTab = AuthTab.LOGIN },
                            testTag = "tab_login"
                        )
                        TabButton(
                            title = "REGISTER",
                            isSelected = selectedTab == AuthTab.REGISTER,
                            onClick = { selectedTab = AuthTab.REGISTER },
                            testTag = "tab_register"
                        )
                    } else {
                        TabButton(
                            title = "PROFILE",
                            isSelected = selectedTab == AuthTab.ACCOUNT,
                            onClick = { selectedTab = AuthTab.ACCOUNT },
                            testTag = "tab_profile"
                        )
                    }

                    TabButton(
                        title = "REALTIME DB",
                        isSelected = selectedTab == AuthTab.REALTIME_DB,
                        onClick = { selectedTab = AuthTab.REALTIME_DB },
                        testTag = "tab_realtime_db"
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Tab Content
                when (selectedTab) {
                    AuthTab.LOGIN -> {
                        // --- LOGIN FORM ---
                        Text(
                            text = "Welcome Back, Dungeon Explorer!",
                            fontSize = 13.sp,
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
                                    text = "LOG IN",
                                    color = Color.Black,
                                    fontWeight = FontWeight.Bold,
                                    fontFamily = FontFamily.Monospace
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

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
                            text = "Create your Pixel Crypt account for cloud progress sync.",
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

                        Spacer(modifier = Modifier.height(12.dp))

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

                                Text(
                                    text = "Account ID: ${currentUser?.id?.take(8) ?: "offline"}...",
                                    fontSize = 11.sp,
                                    color = RetroTextDisabled,
                                    fontFamily = FontFamily.Monospace
                                )

                                val sdf = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())
                                val joined = sdf.format(Date(currentUser?.createdAt ?: System.currentTimeMillis()))
                                Text(
                                    text = "Member Since: $joined",
                                    fontSize = 11.sp,
                                    color = RetroTextDisabled
                                )

                                Spacer(modifier = Modifier.height(14.dp))

                                Row(modifier = Modifier.fillMaxWidth()) {
                                    OutlinedButton(
                                        onClick = { selectedTab = AuthTab.REALTIME_DB },
                                        modifier = Modifier.weight(1f).height(40.dp).testTag("go_to_rtdb_button"),
                                        shape = RoundedCornerShape(8.dp),
                                        border = BorderStroke(1.dp, PixelCyan)
                                    ) {
                                        Icon(Icons.Default.Storage, contentDescription = null, tint = PixelCyan, modifier = Modifier.size(16.dp))
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text("Cloud DB", color = PixelCyan, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                    }

                                    Spacer(modifier = Modifier.width(8.dp))

                                    Button(
                                        onClick = {
                                            onLogout()
                                            selectedTab = AuthTab.LOGIN
                                        },
                                        modifier = Modifier.weight(1f).height(40.dp).testTag("logout_button"),
                                        shape = RoundedCornerShape(8.dp),
                                        colors = ButtonDefaults.buttonColors(containerColor = PixelRuby)
                                    ) {
                                        Text("LOG OUT", color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                    }
                                }
                            }
                        }
                    }

                    AuthTab.REALTIME_DB -> {
                        // --- REALTIME DATABASE SETTINGS TAB ---
                        Column(modifier = Modifier.fillMaxWidth()) {
                            // Status Card
                            Card(
                                modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp),
                                colors = CardDefaults.cardColors(
                                    containerColor = if (rtdbConfig.isConnected) PixelEmerald.copy(alpha = 0.15f) else DungeonCard
                                ),
                                border = BorderStroke(
                                    1.dp,
                                    if (rtdbConfig.isConnected) PixelEmerald else DungeonBorder
                                )
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth().padding(10.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        imageVector = if (rtdbConfig.isConnected) Icons.Default.CheckCircle else Icons.Default.Cloud,
                                        contentDescription = null,
                                        tint = if (rtdbConfig.isConnected) PixelEmerald else RetroTextSecondary,
                                        modifier = Modifier.size(20.dp)
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                            text = if (rtdbConfig.isConnected) "REALTIME DB CONNECTED" else "DATABASE STATUS",
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 11.sp,
                                            fontFamily = FontFamily.Monospace,
                                            color = if (rtdbConfig.isConnected) PixelEmerald else RetroTextSecondary
                                        )
                                        Text(
                                            text = rtdbStatusMsg,
                                            fontSize = 11.sp,
                                            color = RetroTextPrimary
                                        )
                                    }
                                }
                            }

                            Text(
                                text = "Firebase Realtime Database / REST API URL",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = PixelCyan
                            )
                            Spacer(modifier = Modifier.height(4.dp))

                            OutlinedTextField(
                                value = rtdbUrl,
                                onValueChange = {
                                    rtdbUrl = it
                                    onUpdateRtdbConfig(it, rtdbToken, rtdbAutoSync)
                                },
                                placeholder = {
                                    Text("https://your-app-rtdb.firebaseio.com", fontSize = 11.sp, color = RetroTextDisabled)
                                },
                                singleLine = true,
                                modifier = Modifier.fillMaxWidth().testTag("rtdb_url_input"),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = PixelCyan,
                                    unfocusedBorderColor = DungeonBorder,
                                    focusedTextColor = RetroTextPrimary,
                                    unfocusedTextColor = RetroTextPrimary
                                )
                            )

                            // Quick Fill Demo / Example Button
                            Row(
                                modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                                horizontalArrangement = Arrangement.End
                            ) {
                                TextButton(
                                    onClick = {
                                        val sampleUrl = "https://pixel-crypt-default-rtdb.firebaseio.com"
                                        rtdbUrl = sampleUrl
                                        onUpdateRtdbConfig(sampleUrl, rtdbToken, rtdbAutoSync)
                                    },
                                    contentPadding = PaddingValues(horizontal = 6.dp, vertical = 2.dp)
                                ) {
                                    Text("Use Sample Firebase URL", fontSize = 11.sp, color = PixelGold)
                                }
                            }

                            Spacer(modifier = Modifier.height(8.dp))

                            Text(
                                text = "Database Auth Secret / API Key (Optional)",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = RetroTextSecondary
                            )
                            Spacer(modifier = Modifier.height(4.dp))

                            OutlinedTextField(
                                value = rtdbToken,
                                onValueChange = {
                                    rtdbToken = it
                                    onUpdateRtdbConfig(rtdbUrl, it, rtdbAutoSync)
                                },
                                placeholder = {
                                    Text("auth token / secret key", fontSize = 11.sp, color = RetroTextDisabled)
                                },
                                leadingIcon = {
                                    Icon(Icons.Default.Key, contentDescription = null, tint = RetroTextSecondary, modifier = Modifier.size(16.dp))
                                },
                                singleLine = true,
                                modifier = Modifier.fillMaxWidth().testTag("rtdb_token_input"),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = PixelCyan,
                                    unfocusedBorderColor = DungeonBorder,
                                    focusedTextColor = RetroTextPrimary,
                                    unfocusedTextColor = RetroTextPrimary
                                )
                            )

                            Spacer(modifier = Modifier.height(10.dp))

                            // Auto Sync Toggle
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(DungeonCard)
                                    .padding(horizontal = 12.dp, vertical = 6.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = "Auto-Sync to Cloud",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 12.sp,
                                        color = RetroTextPrimary
                                    )
                                    Text(
                                        text = "Sync coins & stars on level victory",
                                        fontSize = 10.sp,
                                        color = RetroTextSecondary
                                    )
                                }
                                Switch(
                                    checked = rtdbAutoSync,
                                    onCheckedChange = {
                                        rtdbAutoSync = it
                                        onUpdateRtdbConfig(rtdbUrl, rtdbToken, it)
                                    },
                                    colors = SwitchDefaults.colors(
                                        checkedThumbColor = PixelCyan,
                                        checkedTrackColor = PixelCyan.copy(alpha = 0.4f)
                                    ),
                                    modifier = Modifier.testTag("rtdb_autosync_switch")
                                )
                            }

                            Spacer(modifier = Modifier.height(14.dp))

                            // Action Buttons
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                // Test Connection Button
                                OutlinedButton(
                                    onClick = {
                                        isTestingConnection = true
                                        onTestRtdbConnection(rtdbUrl, rtdbToken) { success, msg ->
                                            isTestingConnection = false
                                            rtdbStatusMsg = msg
                                        }
                                    },
                                    enabled = !isTestingConnection && !isRtdbBusy,
                                    modifier = Modifier.weight(1f).height(44.dp).testTag("test_connection_button"),
                                    shape = RoundedCornerShape(8.dp),
                                    border = BorderStroke(1.dp, PixelCyan)
                                ) {
                                    if (isTestingConnection) {
                                        CircularProgressIndicator(modifier = Modifier.size(16.dp), color = PixelCyan, strokeWidth = 2.dp)
                                    } else {
                                        Icon(Icons.Default.Wifi, contentDescription = null, tint = PixelCyan, modifier = Modifier.size(16.dp))
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text("Test Ping", fontSize = 11.sp, color = PixelCyan, fontWeight = FontWeight.Bold)
                                    }
                                }

                                // Sync Now Button
                                Button(
                                    onClick = {
                                        isSyncingNow = true
                                        onSyncToCloud { success, msg ->
                                            isSyncingNow = false
                                            rtdbStatusMsg = msg
                                        }
                                    },
                                    enabled = !isSyncingNow && !isRtdbBusy,
                                    modifier = Modifier.weight(1f).height(44.dp).testTag("sync_to_cloud_button"),
                                    shape = RoundedCornerShape(8.dp),
                                    colors = ButtonDefaults.buttonColors(containerColor = PixelEmerald)
                                ) {
                                    if (isSyncingNow) {
                                        CircularProgressIndicator(modifier = Modifier.size(16.dp), color = Color.Black, strokeWidth = 2.dp)
                                    } else {
                                        Icon(Icons.Default.CloudUpload, contentDescription = null, tint = Color.Black, modifier = Modifier.size(16.dp))
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text("Sync Up", fontSize = 11.sp, color = Color.Black, fontWeight = FontWeight.Bold)
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(8.dp))

                            // Restore Button
                            OutlinedButton(
                                onClick = {
                                    isRestoringNow = true
                                    onRestoreFromCloud { success, msg ->
                                        isRestoringNow = false
                                        rtdbStatusMsg = msg
                                    }
                                },
                                enabled = !isRestoringNow && !isRtdbBusy,
                                modifier = Modifier.fillMaxWidth().height(42.dp).testTag("restore_from_cloud_button"),
                                shape = RoundedCornerShape(8.dp),
                                border = BorderStroke(1.dp, PixelGold)
                            ) {
                                if (isRestoringNow) {
                                    CircularProgressIndicator(modifier = Modifier.size(16.dp), color = PixelGold, strokeWidth = 2.dp)
                                } else {
                                    Icon(Icons.Default.CloudDownload, contentDescription = null, tint = PixelGold, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text("Restore from Cloud Backup", fontSize = 11.sp, color = PixelGold, fontWeight = FontWeight.Bold)
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
    testTag: String
) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(6.dp))
            .background(if (isSelected) DungeonCard else Color.Transparent)
            .clickable(onClick = onClick)
            .padding(horizontal = 10.dp, vertical = 8.dp)
            .testTag(testTag),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = title,
            fontSize = 11.sp,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
            fontFamily = FontFamily.Monospace,
            color = if (isSelected) PixelCyan else RetroTextSecondary
        )
    }
}
