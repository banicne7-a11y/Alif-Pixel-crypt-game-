package com.example.ui

import android.app.Application
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.audio.RetroSoundManager
import com.example.data.AppDatabase
import com.example.data.CustomLevelEntity
import com.example.data.GameRepository
import com.example.data.LevelProgressEntity
import com.example.data.RelicEntity
import com.example.model.CrateType
import com.example.model.Direction
import com.example.model.GameEngine
import com.example.model.GameLevel
import com.example.model.GameState
import com.example.model.LevelCatalog
import com.example.model.MoveResult
import com.example.model.Position
import com.example.model.Relic
import com.example.model.RelicRegistry
import com.example.model.TileType
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

enum class ScreenState {
    MENU,
    LEVEL_SELECT,
    PLAYING,
    MAKER,
    RELIC_GALLERY,
    HOW_TO_PLAY,
    SHOP
}

enum class MakerTool {
    WALL,
    FLOOR,
    HERO,
    TARGET,
    CRATE,
    ICE_CRATE,
    ICE_FLOOR,
    WATER,
    SPIKE,
    KEY_IRON,
    DOOR_IRON,
    CHEST,
    EXIT,
    MIRROR,
    EMITTER,
    RECEIVER
}

class GameViewModel(application: Application) : AndroidViewModel(application) {

    private val db = AppDatabase.getInstance(application)
    private val repository = GameRepository(db.levelProgressDao(), db.customLevelDao(), db.relicDao())
    val soundManager = RetroSoundManager()

    val authManager = com.example.data.auth.AuthManager(application)
    val rtdbManager = com.example.data.auth.RealtimeDatabaseManager(application)

    val currentUser = authManager.currentUser
    val authError = authManager.authError
    val rtdbConfig = rtdbManager.config
    val isRtdbBusy = rtdbManager.isBusy

    @Suppress("DEPRECATION")
    private val vibrator = application.getSystemService(Vibrator::class.java)

    // Screen navigation
    private val _currentScreen = MutableStateFlow(ScreenState.MENU)
    val currentScreen: StateFlow<ScreenState> = _currentScreen.asStateFlow()

    private val _selectedWorld = MutableStateFlow(1)
    val selectedWorld: StateFlow<Int> = _selectedWorld.asStateFlow()

    // Active Game State
    private val _currentLevel = MutableStateFlow<GameLevel?>(null)
    val currentLevel: StateFlow<GameLevel?> = _currentLevel.asStateFlow()

    private val _gameState = MutableStateFlow<GameState?>(null)
    val gameState: StateFlow<GameState?> = _gameState.asStateFlow()

    private val _hintPosition = MutableStateFlow<Position?>(null)
    val hintPosition: StateFlow<Position?> = _hintPosition.asStateFlow()

    // Settings
    private val _crtFilterEnabled = MutableStateFlow(true)
    val crtFilterEnabled: StateFlow<Boolean> = _crtFilterEnabled.asStateFlow()

    private val _soundEnabled = MutableStateFlow(true)
    val soundEnabled: StateFlow<Boolean> = _soundEnabled.asStateFlow()

    // Monetization & Player Economy State
    private val prefs = application.getSharedPreferences("pixel_crypt_prefs", android.content.Context.MODE_PRIVATE)

    private val _playerName = MutableStateFlow(prefs.getString("player_name", "Alif") ?: "Alif")
    val playerName: StateFlow<String> = _playerName.asStateFlow()

    fun updatePlayerName(newName: String) {
        val trimmed = newName.trim().take(20)
        if (trimmed.isNotEmpty()) {
            _playerName.value = trimmed
            prefs.edit().putString("player_name", trimmed).apply()
        }
    }

    private val _goldCoins = MutableStateFlow(prefs.getInt("gold_coins", 150)) // start with 150 bonus coins
    val goldCoins: StateFlow<Int> = _goldCoins.asStateFlow()

    private val _freeHints = MutableStateFlow(prefs.getInt("free_hints", 3))
    val freeHints: StateFlow<Int> = _freeHints.asStateFlow()

    private val _adsRemoved = MutableStateFlow(prefs.getBoolean("ads_removed", false))
    val adsRemoved: StateFlow<Boolean> = _adsRemoved.asStateFlow()

    private val _selectedSkin = MutableStateFlow(
        com.example.model.HeroSkin.entries.find { it.id == prefs.getString("selected_skin", com.example.model.HeroSkin.SILVER_KNIGHT.id) }
            ?: com.example.model.HeroSkin.SILVER_KNIGHT
    )
    val selectedSkin: StateFlow<com.example.model.HeroSkin> = _selectedSkin.asStateFlow()

    private val _unlockedSkinIds = MutableStateFlow(
        prefs.getStringSet("unlocked_skins", setOf(com.example.model.HeroSkin.SILVER_KNIGHT.id))?.toSet()
            ?: setOf(com.example.model.HeroSkin.SILVER_KNIGHT.id)
    )
    val unlockedSkinIds: StateFlow<Set<String>> = _unlockedSkinIds.asStateFlow()

    // Dungeon Theme (Walls & Bricks & Background)
    private val _selectedTheme = MutableStateFlow(
        com.example.model.DungeonTheme.entries.find { it.id == prefs.getString("selected_theme", com.example.model.DungeonTheme.DEFAULT_CRYPT.id) }
            ?: com.example.model.DungeonTheme.DEFAULT_CRYPT
    )
    val selectedTheme: StateFlow<com.example.model.DungeonTheme> = _selectedTheme.asStateFlow()

    private val _unlockedThemeIds = MutableStateFlow(
        prefs.getStringSet("unlocked_themes", setOf(com.example.model.DungeonTheme.DEFAULT_CRYPT.id))?.toSet()
            ?: setOf(com.example.model.DungeonTheme.DEFAULT_CRYPT.id)
    )
    val unlockedThemeIds: StateFlow<Set<String>> = _unlockedThemeIds.asStateFlow()

    private val _adRewardToast = MutableStateFlow<String?>(null)
    val adRewardToast: StateFlow<String?> = _adRewardToast.asStateFlow()

    fun dismissAdToast() {
        _adRewardToast.value = null
    }

    fun addCoins(amount: Int) {
        val updated = _goldCoins.value + amount
        _goldCoins.value = updated
        prefs.edit().putInt("gold_coins", updated).apply()
    }

    fun spendCoins(amount: Int): Boolean {
        if (_goldCoins.value >= amount) {
            val updated = _goldCoins.value - amount
            _goldCoins.value = updated
            prefs.edit().putInt("gold_coins", updated).apply()
            return true
        }
        return false
    }

    fun purchaseSkin(skin: com.example.model.HeroSkin): Boolean {
        if (_unlockedSkinIds.value.contains(skin.id)) {
            // Already owned, just equip
            equipSkin(skin)
            return true
        }
        if (spendCoins(skin.costCoins)) {
            val updatedSkins = _unlockedSkinIds.value + skin.id
            _unlockedSkinIds.value = updatedSkins
            _selectedSkin.value = skin
            prefs.edit()
                .putStringSet("unlocked_skins", updatedSkins)
                .putString("selected_skin", skin.id)
                .apply()
            soundManager.playChestOpen()
            return true
        }
        return false
    }

    fun equipSkin(skin: com.example.model.HeroSkin) {
        if (_unlockedSkinIds.value.contains(skin.id)) {
            _selectedSkin.value = skin
            prefs.edit().putString("selected_skin", skin.id).apply()
            soundManager.playMenuClick()
        }
    }

    fun purchaseTheme(theme: com.example.model.DungeonTheme): Boolean {
        if (_unlockedThemeIds.value.contains(theme.id)) {
            equipTheme(theme)
            return true
        }
        if (spendCoins(theme.costCoins)) {
            val updatedThemes = _unlockedThemeIds.value + theme.id
            _unlockedThemeIds.value = updatedThemes
            _selectedTheme.value = theme
            prefs.edit()
                .putStringSet("unlocked_themes", updatedThemes)
                .putString("selected_theme", theme.id)
                .apply()
            soundManager.playChestOpen()
            return true
        }
        return false
    }

    fun equipTheme(theme: com.example.model.DungeonTheme) {
        if (_unlockedThemeIds.value.contains(theme.id)) {
            _selectedTheme.value = theme
            prefs.edit().putString("selected_theme", theme.id).apply()
            soundManager.playMenuClick()
        }
    }

    fun buyHintsWithCoins(): Boolean {
        if (spendCoins(10)) {
            val newHints = _freeHints.value + 3
            _freeHints.value = newHints
            prefs.edit().putInt("free_hints", newHints).apply()
            soundManager.playKeyPickup()
            return true
        }
        return false
    }

    fun watchRewardedAdForCoins() {
        // Rewarded video gives exactly 5 coins
        addCoins(5)
        _adRewardToast.value = "Rewarded Ad Completed! +5 Gold Coins earned!"
        soundManager.playChestOpen()
    }

    fun watchRewardedAdForHints() {
        val newHints = _freeHints.value + 2
        _freeHints.value = newHints
        prefs.edit().putInt("free_hints", newHints).apply()
        _adRewardToast.value = "Rewarded Ad Completed! +2 Free Hints added!"
        soundManager.playKeyPickup()
    }

    fun purchaseRemoveAds() {
        _adsRemoved.value = true
        prefs.edit().putBoolean("ads_removed", true).apply()
        _adRewardToast.value = "Ads Removed Forever! Thank you for supporting the developer!"
        soundManager.playWinFanfare()
    }

    // ================= DAILY REWARDS (7-DAY LOGIN BONUS) =================
    private val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.US)
    private fun getTodayDateString(): String = dateFormat.format(Date())
    private fun getYesterdayDateString(): String {
        val cal = Calendar.getInstance()
        cal.add(Calendar.DAY_OF_YEAR, -1)
        return dateFormat.format(cal.time)
    }

    private val _dailyStreak = MutableStateFlow(prefs.getInt("daily_streak", 1))
    val dailyStreak: StateFlow<Int> = _dailyStreak.asStateFlow()

    private val _isDailyRewardClaimable = MutableStateFlow(
        prefs.getString("last_login_date", "") != getTodayDateString()
    )
    val isDailyRewardClaimable: StateFlow<Boolean> = _isDailyRewardClaimable.asStateFlow()

    fun claimDailyReward(): Pair<Int, Int> {
        val today = getTodayDateString()
        val yesterday = getYesterdayDateString()
        val lastDate = prefs.getString("last_login_date", "") ?: ""

        val currentStreak = when {
            lastDate == yesterday -> {
                val next = (_dailyStreak.value % 7) + 1
                next
            }
            lastDate == today -> _dailyStreak.value
            else -> 1 // missed days or brand new
        }

        // Rewards for Day 1..7 (balanced: 1-5 coins)
        val (rewardCoins, rewardHints) = when (currentStreak) {
            1 -> 1 to 0
            2 -> 2 to 0
            3 -> 0 to 1
            4 -> 3 to 0
            5 -> 0 to 2
            6 -> 4 to 0
            7 -> 5 to 2 // Grand Vault Reward
            else -> 1 to 0
        }

        if (rewardCoins > 0) addCoins(rewardCoins)
        if (rewardHints > 0) {
            val newHints = _freeHints.value + rewardHints
            _freeHints.value = newHints
            prefs.edit().putInt("free_hints", newHints).apply()
        }

        _dailyStreak.value = currentStreak
        _isDailyRewardClaimable.value = false
        prefs.edit()
            .putString("last_login_date", today)
            .putInt("daily_streak", currentStreak)
            .apply()

        soundManager.playChestOpen()
        triggerVibration(60)
        return rewardCoins to rewardHints
    }

    // ================= LUCKY FORTUNE SPIN WHEEL =================
    private val _isFreeSpinAvailable = MutableStateFlow(
        prefs.getString("last_free_spin_date", "") != getTodayDateString()
    )
    val isFreeSpinAvailable: StateFlow<Boolean> = _isFreeSpinAvailable.asStateFlow()

    // Sectors: 0: +1🪙, 1: +2🪙, 2: +1💡, 3: +3🪙, 4: +5🪙, 5: +2💡
    fun spinWheel(
        isRewardedAd: Boolean,
        onSpinResult: (sectorIndex: Int, coins: Int, hints: Int, desc: String) -> Unit
    ) {
        val sector = (0..5).random()
        val (coins, hints, desc) = when (sector) {
            0 -> Triple(1, 0, "+1 Gold Coin")
            1 -> Triple(2, 0, "+2 Gold Coins")
            2 -> Triple(0, 1, "+1 Free Hint")
            3 -> Triple(3, 0, "+3 Gold Coins")
            4 -> Triple(5, 0, "+5 JackPot Coins!")
            5 -> Triple(0, 2, "+2 Free Hints")
            else -> Triple(1, 0, "+1 Gold Coin")
        }

        if (coins > 0) addCoins(coins)
        if (hints > 0) {
            val newHints = _freeHints.value + hints
            _freeHints.value = newHints
            prefs.edit().putInt("free_hints", newHints).apply()
        }

        if (!isRewardedAd) {
            val today = getTodayDateString()
            prefs.edit().putString("last_free_spin_date", today).apply()
            _isFreeSpinAvailable.value = false
        }

        soundManager.playChestOpen()
        triggerVibration(70)
        onSpinResult(sector, coins, hints, desc)
    }

    // ================= ACHIEVEMENTS =================
    private val _claimedAchievementIds = MutableStateFlow(
        prefs.getStringSet("claimed_achievements", emptySet())?.toSet() ?: emptySet()
    )
    val claimedAchievementIds: StateFlow<Set<String>> = _claimedAchievementIds.asStateFlow()

    fun claimAchievement(id: String, rewardCoins: Int, rewardHints: Int): Boolean {
        if (_claimedAchievementIds.value.contains(id)) return false

        val updated = _claimedAchievementIds.value + id
        _claimedAchievementIds.value = updated
        prefs.edit().putStringSet("claimed_achievements", updated).apply()

        if (rewardCoins > 0) addCoins(rewardCoins)
        if (rewardHints > 0) {
            val newHints = _freeHints.value + rewardHints
            _freeHints.value = newHints
            prefs.edit().putInt("free_hints", newHints).apply()
        }

        soundManager.playWinFanfare()
        triggerVibration(80)
        return true
    }

    // Room DB Observables
    val levelProgressList: StateFlow<List<LevelProgressEntity>> = repository.allProgress
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val totalStarsCount: StateFlow<Int?> = repository.totalStars
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    val customLevels: StateFlow<List<CustomLevelEntity>> = repository.customLevels
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val unlockedRelics: StateFlow<List<RelicEntity>> = repository.unlockedRelics
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Maker Mode State
    val makerGridWidth = 8
    val makerGridHeight = 8
    private val _makerGrid = MutableStateFlow<List<MutableList<Char>>>(
        List(makerGridHeight) { y ->
            MutableList(makerGridWidth) { x ->
                if (x == 0 || x == makerGridWidth - 1 || y == 0 || y == makerGridHeight - 1) '#' else '.'
            }
        }
    )
    val makerGrid: StateFlow<List<List<Char>>> = _makerGrid.asStateFlow()

    private val _selectedMakerTool = MutableStateFlow(MakerTool.WALL)
    val selectedMakerTool: StateFlow<MakerTool> = _selectedMakerTool.asStateFlow()

    private val _makerParMoves = MutableStateFlow(15)
    val makerParMoves: StateFlow<Int> = _makerParMoves.asStateFlow()

    private val _makerLevelName = MutableStateFlow("Crypt of Secrets")
    val makerLevelName: StateFlow<String> = _makerLevelName.asStateFlow()

    private val _makerMessage = MutableStateFlow<String?>(null)
    val makerMessage: StateFlow<String?> = _makerMessage.asStateFlow()

    fun navigateTo(screen: ScreenState) {
        soundManager.playMenuClick()
        _currentScreen.value = screen
    }

    fun selectWorld(worldId: Int) {
        soundManager.playMenuClick()
        _selectedWorld.value = worldId
    }

    fun toggleCrtFilter() {
        _crtFilterEnabled.value = !_crtFilterEnabled.value
        soundManager.playMenuClick()
    }

    fun toggleSound() {
        val next = !_soundEnabled.value
        _soundEnabled.value = next
        soundManager.isSoundEnabled = next
    }

    fun startLevel(level: GameLevel) {
        _currentLevel.value = level
        _gameState.value = level.toInitialState()
        _hintPosition.value = null
        _currentScreen.value = ScreenState.PLAYING
        soundManager.playMenuClick()
    }

    fun restartLevel() {
        val lvl = _currentLevel.value ?: return
        _gameState.value = lvl.toInitialState()
        _hintPosition.value = null
        soundManager.playUndo()
    }

    fun startDailyDungeon() {
        val calendar = Calendar.getInstance()
        val dayOfYear = calendar.get(Calendar.DAY_OF_YEAR)
        val year = calendar.get(Calendar.YEAR)
        val dailyLevel = LevelCatalog.generateDailyLevel(dayOfYear, year)
        startLevel(dailyLevel)
    }

    fun onMove(direction: Direction) {
        val current = _gameState.value ?: return
        val (nextState, result) = GameEngine.executeMove(current, direction)
        _gameState.value = nextState
        _hintPosition.value = null // clear hint after move

        when (result) {
            is MoveResult.Moved -> {
                if (result.cratePushed) {
                    soundManager.playPush()
                    triggerVibration(30)
                } else if (result.keyCollected) {
                    soundManager.playKeyPickup()
                    triggerVibration(40)
                } else if (result.doorUnlocked) {
                    soundManager.playDoorUnlock()
                    triggerVibration(50)
                } else if (result.chestOpened) {
                    soundManager.playChestOpen()
                    triggerVibration(60)
                } else if (result.portalUsed) {
                    soundManager.playPortalWarp()
                } else if (result.iceSlid) {
                    soundManager.playIceSlide()
                } else {
                    soundManager.playStep()
                }
            }
            is MoveResult.Blocked -> {
                // do not vibrate or chime on wall bump
            }
            is MoveResult.Defeated -> {
                soundManager.playDefeat()
                triggerVibration(120)
            }
            is MoveResult.Won -> {
                soundManager.playWinFanfare()
                triggerVibration(80)
                // Award coins based on performance (1 to 5 coins max per level)
                val stars = nextState.calculateStars()
                val coinsEarned = when (stars) {
                    3 -> 5
                    2 -> 3
                    else -> 1
                }
                addCoins(coinsEarned)

                // Save progress to database
                viewModelScope.launch {
                    repository.saveLevelCompletion(
                        levelId = nextState.levelId,
                        worldId = nextState.worldId,
                        stars = stars,
                        moves = nextState.stepCount
                    )
                    // If relic was collected, unlock it in DB
                    nextState.relicAwarded?.let { relic ->
                        repository.unlockRelic(relic.id, relic.name)
                    }
                }
            }
        }
    }

    fun onRotateMirror() {
        val current = _gameState.value ?: return
        val nextState = GameEngine.rotateMirrorAtFacing(current)
        if (nextState != current) {
            _gameState.value = nextState
            soundManager.playMirrorRotate()
            triggerVibration(25)
        }
    }

    fun onUndo() {
        val current = _gameState.value ?: return
        val reverted = GameEngine.undo(current)
        if (reverted != current) {
            _gameState.value = reverted
            _hintPosition.value = null
            soundManager.playUndo()
        }
    }

    fun onNextLevel() {
        val currentLvl = _currentLevel.value ?: return
        val currentWorldLevels = LevelCatalog.getLevelsForWorld(currentLvl.worldId)
        val currentIndex = currentWorldLevels.indexOfFirst { it.id == currentLvl.id }

        if (currentIndex != -1 && currentIndex < currentWorldLevels.size - 1) {
            startLevel(currentWorldLevels[currentIndex + 1])
        } else if (currentLvl.worldId < 4) {
            // Move to next world
            val nextWorldLevels = LevelCatalog.getLevelsForWorld(currentLvl.worldId + 1)
            if (nextWorldLevels.isNotEmpty()) {
                _selectedWorld.value = currentLvl.worldId + 1
                startLevel(nextWorldLevels.first())
            }
        } else {
            navigateTo(ScreenState.LEVEL_SELECT)
        }
    }

    fun showHint() {
        val state = _gameState.value ?: return
        if (_freeHints.value <= 0 && _goldCoins.value < 20) {
            _adRewardToast.value = "Out of hints! Watch an ad or get more in the Shop!"
            return
        }

        if (_freeHints.value > 0) {
            val updated = _freeHints.value - 1
            _freeHints.value = updated
            prefs.edit().putInt("free_hints", updated).apply()
        } else {
            spendCoins(20)
        }

        soundManager.playKeyPickup()

        // Hint logic: find closest unfilled target or crate, or key
        if (state.ironKeys == 0 && state.baseTiles.values.contains(TileType.KEY_IRON) && !state.collectedKeys.any { state.baseTiles[it] == TileType.KEY_IRON }) {
            val keyPos = state.baseTiles.entries.find { it.value == TileType.KEY_IRON && !state.collectedKeys.contains(it.key) }?.key
            if (keyPos != null) {
                _hintPosition.value = keyPos
                return
            }
        }

        // Find an unfilled target
        val unfilledTarget = state.baseTiles.entries.find { it.value == TileType.TARGET && !state.crates.containsKey(it.key) }?.key
        if (unfilledTarget != null) {
            // Find closest crate not on target
            val freeCrate = state.crates.entries.find { state.baseTiles[it.key] != TileType.TARGET }?.key
            _hintPosition.value = freeCrate ?: unfilledTarget
        } else if (state.baseTiles.values.contains(TileType.EXIT_GATE)) {
            val exitPos = state.baseTiles.entries.find { it.value == TileType.EXIT_GATE }?.key
            _hintPosition.value = exitPos
        }
    }

    // ================= LEVEL MAKER ACTIONS =================

    fun selectMakerTool(tool: MakerTool) {
        _selectedMakerTool.value = tool
        soundManager.playMenuClick()
    }

    fun setMakerCell(x: Int, y: Int) {
        if (x !in 0 until makerGridWidth || y !in 0 until makerGridHeight) return
        val currentGrid = _makerGrid.value.map { it.toMutableList() }.toMutableList()
        val tool = _selectedMakerTool.value

        val ch = when (tool) {
            MakerTool.WALL -> '#'
            MakerTool.FLOOR -> '.'
            MakerTool.HERO -> {
                // Ensure only one hero on the grid
                for (r in currentGrid.indices) {
                    for (c in currentGrid[r].indices) {
                        if (currentGrid[r][c] == '@') currentGrid[r][c] = '.'
                    }
                }
                '@'
            }
            MakerTool.TARGET -> 'T'
            MakerTool.CRATE -> '$'
            MakerTool.ICE_CRATE -> 'I'
            MakerTool.ICE_FLOOR -> '_'
            MakerTool.WATER -> '~'
            MakerTool.SPIKE -> '^'
            MakerTool.KEY_IRON -> 'K'
            MakerTool.DOOR_IRON -> 'D'
            MakerTool.CHEST -> 'C'
            MakerTool.EXIT -> 'E'
            MakerTool.MIRROR -> '/'
            MakerTool.EMITTER -> '>'
            MakerTool.RECEIVER -> 'R'
        }

        currentGrid[y][x] = ch
        _makerGrid.value = currentGrid
        soundManager.playStep()
    }

    fun clearMakerGrid() {
        val emptyGrid = List(makerGridHeight) { y ->
            MutableList(makerGridWidth) { x ->
                if (x == 0 || x == makerGridWidth - 1 || y == 0 || y == makerGridHeight - 1) '#' else '.'
            }
        }
        emptyGrid[1][1] = '@'
        emptyGrid[2][2] = '$'
        emptyGrid[2][5] = 'T'
        _makerGrid.value = emptyGrid
        soundManager.playUndo()
    }

    fun updateMakerPar(par: Int) {
        _makerParMoves.value = par.coerceIn(3, 99)
    }

    fun updateMakerName(name: String) {
        _makerLevelName.value = name.take(24)
    }

    fun testPlayMakerLevel(): Boolean {
        val rows = _makerGrid.value.map { it.joinToString("") }
        val heroCount = rows.sumOf { r -> r.count { it == '@' || it == '+' } }
        if (heroCount == 0) {
            _makerMessage.value = "Error: Place a Hero (@) on the grid first!"
            return false
        }

        val level = GameLevel(
            id = "custom_test_${System.currentTimeMillis()}",
            worldId = 0,
            name = _makerLevelName.value.ifBlank { "Custom Dungeon" },
            description = "Maker Mode Creation",
            parMoves = _makerParMoves.value,
            mapRows = rows
        )
        startLevel(level)
        return true
    }

    fun saveMakerLevel() {
        val rows = _makerGrid.value.map { it.joinToString("") }
        val heroCount = rows.sumOf { r -> r.count { it == '@' || it == '+' } }
        if (heroCount == 0) {
            _makerMessage.value = "Error: Place a Hero (@) before saving!"
            return
        }

        val code = GameLevel.encodeToString(_makerLevelName.value, _makerParMoves.value, rows)
        val entity = CustomLevelEntity(
            id = "custom_${System.currentTimeMillis()}",
            name = _makerLevelName.value.ifBlank { "Custom Crypt" },
            author = "Player",
            width = makerGridWidth,
            height = makerGridHeight,
            levelData = code,
            parMoves = _makerParMoves.value
        )
        viewModelScope.launch {
            repository.saveCustomLevel(entity)
            soundManager.playChestOpen()
            _makerMessage.value = "Level saved successfully!"
        }
    }

    fun deleteCustomLevel(id: String) {
        viewModelScope.launch {
            repository.deleteCustomLevel(id)
            soundManager.playUndo()
        }
    }

    fun importLevelCode(code: String): Boolean {
        val level = GameLevel.decodeFromString(code.trim())
        if (level != null) {
            startLevel(level)
            return true
        }
        _makerMessage.value = "Invalid dungeon code!"
        return false
    }

    fun getExportCode(): String {
        val rows = _makerGrid.value.map { it.joinToString("") }
        return GameLevel.encodeToString(_makerLevelName.value, _makerParMoves.value, rows)
    }

    fun clearMakerMessage() {
        _makerMessage.value = null
    }

    // ================= AUTH & REALTIME CLOUD DB =================
    fun login(emailOrUser: String, pass: String, onResult: (Boolean, String?) -> Unit) {
        val success = authManager.login(emailOrUser, pass)
        if (success) {
            val user = authManager.currentUser.value
            if (user != null) {
                updatePlayerName(user.username)
                triggerAutoSync()
            }
            onResult(true, null)
        } else {
            onResult(false, authManager.authError.value)
        }
    }

    fun register(username: String, email: String, pass: String, onResult: (Boolean, String?) -> Unit) {
        val success = authManager.register(username, email, pass, _selectedSkin.value.id)
        if (success) {
            val user = authManager.currentUser.value
            if (user != null) {
                updatePlayerName(user.username)
                triggerAutoSync()
            }
            onResult(true, null)
        } else {
            onResult(false, authManager.authError.value)
        }
    }

    fun logout() {
        authManager.logout()
    }

    fun updateRtdbConfig(url: String, token: String, autoSync: Boolean) {
        rtdbManager.updateConfig(url, token, autoSync)
    }

    fun testRtdbConnection(url: String, token: String, onResult: (Boolean, String) -> Unit) {
        viewModelScope.launch {
            val res = rtdbManager.testConnection(url, token)
            onResult(res.first, res.second)
        }
    }

    fun syncNowToCloud(onResult: (Boolean, String) -> Unit) {
        val user = authManager.currentUser.value
        val userId = user?.id ?: "local_guest"
        val username = user?.username ?: _playerName.value
        val email = user?.email ?: ""

        val cloudData = com.example.data.auth.CloudPlayerData(
            userId = userId,
            username = username,
            email = email,
            goldCoins = _goldCoins.value,
            freeHints = _freeHints.value,
            selectedSkin = _selectedSkin.value.id,
            unlockedSkins = _unlockedSkinIds.value.toList(),
            selectedTheme = _selectedTheme.value.id,
            unlockedThemes = _unlockedThemeIds.value.toList(),
            dailyStreak = _dailyStreak.value,
            totalStars = totalStarsCount.value ?: 0,
            lastSyncedAt = System.currentTimeMillis()
        )

        viewModelScope.launch {
            val res = rtdbManager.syncToCloud(cloudData)
            onResult(res.first, res.second)
        }
    }

    fun restoreFromCloud(onResult: (Boolean, String) -> Unit) {
        val user = authManager.currentUser.value
        val userId = user?.id ?: "local_guest"

        viewModelScope.launch {
            val (data, msg) = rtdbManager.restoreFromCloud(userId)
            if (data != null) {
                _goldCoins.value = data.goldCoins
                prefs.edit().putInt("gold_coins", data.goldCoins).apply()

                _freeHints.value = data.freeHints
                prefs.edit().putInt("free_hints", data.freeHints).apply()

                val skin = com.example.model.HeroSkin.entries.find { it.id == data.selectedSkin }
                    ?: com.example.model.HeroSkin.SILVER_KNIGHT
                _selectedSkin.value = skin
                val newSkins = (_unlockedSkinIds.value + data.unlockedSkins).toSet()
                _unlockedSkinIds.value = newSkins
                prefs.edit()
                    .putString("selected_skin", skin.id)
                    .putStringSet("unlocked_skins", newSkins)
                    .apply()

                val theme = com.example.model.DungeonTheme.entries.find { it.id == data.selectedTheme }
                    ?: com.example.model.DungeonTheme.DEFAULT_CRYPT
                _selectedTheme.value = theme
                val newThemes = (_unlockedThemeIds.value + data.unlockedThemes).toSet()
                _unlockedThemeIds.value = newThemes
                prefs.edit()
                    .putString("selected_theme", theme.id)
                    .putStringSet("unlocked_themes", newThemes)
                    .apply()

                _dailyStreak.value = data.dailyStreak
                prefs.edit().putInt("daily_streak", data.dailyStreak).apply()

                updatePlayerName(data.username)

                onResult(true, msg)
            } else {
                onResult(false, msg)
            }
        }
    }

    fun triggerAutoSync() {
        if (rtdbManager.config.value.autoSyncEnabled && rtdbManager.config.value.databaseUrl.isNotEmpty()) {
            syncNowToCloud { _, _ -> }
        }
    }

    @Suppress("DEPRECATION")
    private fun triggerVibration(durationMs: Long) {
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                vibrator?.vibrate(VibrationEffect.createOneShot(durationMs, VibrationEffect.DEFAULT_AMPLITUDE))
            } else {
                vibrator?.vibrate(durationMs)
            }
        } catch (_: Exception) {}
    }
}
