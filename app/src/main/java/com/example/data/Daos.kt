package com.example.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface LevelProgressDao {
    @Query("SELECT * FROM level_progress")
    fun getAllProgress(): Flow<List<LevelProgressEntity>>

    @Query("SELECT * FROM level_progress WHERE levelId = :levelId")
    suspend fun getProgressForLevel(levelId: String): LevelProgressEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveProgress(progress: LevelProgressEntity)

    @Query("SELECT SUM(stars) FROM level_progress")
    fun getTotalStars(): Flow<Int?>
}

@Dao
interface CustomLevelDao {
    @Query("SELECT * FROM custom_levels ORDER BY createdAt DESC")
    fun getAllCustomLevels(): Flow<List<CustomLevelEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveCustomLevel(level: CustomLevelEntity)

    @Query("DELETE FROM custom_levels WHERE id = :id")
    suspend fun deleteCustomLevel(id: String)
}

@Dao
interface RelicDao {
    @Query("SELECT * FROM unlocked_relics ORDER BY unlockedAt ASC")
    fun getUnlockedRelics(): Flow<List<RelicEntity>>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun unlockRelic(relic: RelicEntity)

    @Query("SELECT COUNT(*) FROM unlocked_relics WHERE id = :id")
    suspend fun isRelicUnlocked(id: String): Int
}
