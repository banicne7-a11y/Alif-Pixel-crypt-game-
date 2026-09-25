package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "level_progress")
data class LevelProgressEntity(
    @PrimaryKey val levelId: String,
    val worldId: Int,
    val stars: Int,
    val minMoves: Int,
    val completed: Boolean,
    val bestTimeSeconds: Long = 0L
)

@Entity(tableName = "custom_levels")
data class CustomLevelEntity(
    @PrimaryKey val id: String,
    val name: String,
    val author: String = "Crypt Builder",
    val width: Int,
    val height: Int,
    val levelData: String, // String representation e.g. PX1|...
    val parMoves: Int,
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "unlocked_relics")
data class RelicEntity(
    @PrimaryKey val id: String,
    val name: String,
    val unlockedAt: Long = System.currentTimeMillis()
)
