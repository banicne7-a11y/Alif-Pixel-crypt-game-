package com.example.model

data class Achievement(
    val id: String,
    val title: String,
    val description: String,
    val icon: String,
    val target: Int,
    val rewardCoins: Int,
    val rewardHints: Int = 0
)

object AchievementList {
    val allAchievements = listOf(
        Achievement(
            id = "first_step",
            title = "First Steps",
            description = "Clear your very first dungeon room",
            icon = "🗝️",
            target = 1,
            rewardCoins = 1
        ),
        Achievement(
            id = "explorer_10",
            title = "Dungeon Explorer",
            description = "Clear 10 crypt dungeon rooms",
            icon = "🗺️",
            target = 10,
            rewardCoins = 3
        ),
        Achievement(
            id = "master_50",
            title = "Crypt Master",
            description = "Conquer 50 dungeon puzzles",
            icon = "👑",
            target = 50,
            rewardCoins = 5,
            rewardHints = 1
        ),
        Achievement(
            id = "star_30",
            title = "Starlight Voyager",
            description = "Earn 30 gold stars across puzzles",
            icon = "⭐",
            target = 30,
            rewardCoins = 3
        ),
        Achievement(
            id = "star_100",
            title = "Cosmic Constellation",
            description = "Collect 100 gold stars",
            icon = "🌟",
            target = 100,
            rewardCoins = 5,
            rewardHints = 2
        ),
        Achievement(
            id = "relic_finder",
            title = "Ancient Archaeologist",
            description = "Unearth at least 1 legendary relic",
            icon = "🏺",
            target = 1,
            rewardCoins = 3
        ),
        Achievement(
            id = "hint_collector",
            title = "Wise Tactician",
            description = "Hold 5 or more free hints",
            icon = "💡",
            target = 5,
            rewardCoins = 2
        ),
        Achievement(
            id = "coin_hoarder",
            title = "Royal Treasurer",
            description = "Hold 50 or more gold coins in vault",
            icon = "🪙",
            target = 50,
            rewardCoins = 5
        )
    )
}
