package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ui.GameViewModel
import com.example.ui.ScreenState
import com.example.ui.screens.GamePlayScreen
import com.example.ui.screens.HowToPlayScreen
import com.example.ui.screens.LevelMakerScreen
import com.example.ui.screens.LevelSelectScreen
import com.example.ui.screens.MainMenuScreen
import com.example.ui.screens.RelicGalleryScreen
import com.example.ui.screens.ShopScreen
import com.example.ui.theme.DungeonDarkBg
import com.example.ui.theme.MyApplicationTheme

class MainActivity : ComponentActivity() {

    private val viewModel: GameViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        com.example.ads.AdMobManager.initialize(this)

        setContent {
            MyApplicationTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = DungeonDarkBg
                ) {
                    PixelCryptApp(viewModel = viewModel)
                }
            }
        }
    }
}

@Composable
fun PixelCryptApp(viewModel: GameViewModel) {
    val currentScreen by viewModel.currentScreen.collectAsStateWithLifecycle()

    // Handle system back navigation
    BackHandler(enabled = currentScreen != ScreenState.MENU) {
        when (currentScreen) {
            ScreenState.PLAYING -> viewModel.navigateTo(ScreenState.LEVEL_SELECT)
            else -> viewModel.navigateTo(ScreenState.MENU)
        }
    }

    when (currentScreen) {
        ScreenState.MENU -> MainMenuScreen(viewModel = viewModel)
        ScreenState.LEVEL_SELECT -> LevelSelectScreen(viewModel = viewModel)
        ScreenState.PLAYING -> GamePlayScreen(viewModel = viewModel)
        ScreenState.MAKER -> LevelMakerScreen(viewModel = viewModel)
        ScreenState.RELIC_GALLERY -> RelicGalleryScreen(viewModel = viewModel)
        ScreenState.HOW_TO_PLAY -> HowToPlayScreen(viewModel = viewModel)
        ScreenState.SHOP -> ShopScreen(viewModel = viewModel)
    }
}
