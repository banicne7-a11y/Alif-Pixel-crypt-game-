package com.example.data.auth

data class UserAccount(
    val id: String,
    val username: String,
    val email: String,
    val passwordHash: String,
    val avatarSkinId: String = "knight_silver",
    val createdAt: Long = System.currentTimeMillis(),
    val lastLoginAt: Long = System.currentTimeMillis()
)

data class RealtimeDbConfig(
    val databaseUrl: String = "",
    val authToken: String = "",
    val autoSyncEnabled: Boolean = true,
    val lastSyncTime: Long = 0L,
    val lastStatusMessage: String = "Not configured",
    val isConnected: Boolean = false
)

data class CloudPlayerData(
    val userId: String = "",
    val username: String = "",
    val email: String = "",
    val goldCoins: Int = 150,
    val freeHints: Int = 3,
    val selectedSkin: String = "knight_silver",
    val unlockedSkins: List<String> = listOf("knight_silver"),
    val selectedTheme: String = "crypt_default",
    val unlockedThemes: List<String> = listOf("crypt_default"),
    val dailyStreak: Int = 1,
    val totalStars: Int = 0,
    val lastSyncedAt: Long = System.currentTimeMillis()
)
