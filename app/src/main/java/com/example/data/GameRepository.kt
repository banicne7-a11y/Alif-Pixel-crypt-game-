package com.example.data

import kotlinx.coroutines.flow.Flow

class GameRepository(
    private val levelProgressDao: LevelProgressDao,
    private val customLevelDao: CustomLevelDao,
    private val relicDao: RelicDao
) {
    val allProgress: Flow<List<LevelProgressEntity>> = levelProgressDao.getAllProgress()
    val totalStars: Flow<Int?> = levelProgressDao.getTotalStars()
    val customLevels: Flow<List<CustomLevelEntity>> = customLevelDao.getAllCustomLevels()
    val unlockedRelics: Flow<List<RelicEntity>> = relicDao.getUnlockedRelics()

    suspend fun saveLevelCompletion(levelId: String, worldId: Int, stars: Int, moves: Int) {
        val existing = levelProgressDao.getProgressForLevel(levelId)
        val bestStars = if (existing != null) maxOf(existing.stars, stars) else stars
        val bestMoves = if (existing != null && existing.completed) minOf(existing.minMoves, moves) else moves

        levelProgressDao.saveProgress(
            LevelProgressEntity(
                levelId = levelId,
                worldId = worldId,
                stars = bestStars,
                minMoves = bestMoves,
                completed = true
            )
        )
    }

    suspend fun saveCustomLevel(level: CustomLevelEntity) {
        customLevelDao.saveCustomLevel(level)
    }

    suspend fun deleteCustomLevel(id: String) {
        customLevelDao.deleteCustomLevel(id)
    }

    suspend fun unlockRelic(relicId: String, name: String) {
        relicDao.unlockRelic(RelicEntity(id = relicId, name = name))
    }
}
