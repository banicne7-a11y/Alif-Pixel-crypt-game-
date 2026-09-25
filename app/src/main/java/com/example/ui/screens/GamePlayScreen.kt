package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.RotateRight
import androidx.compose.material.icons.automirrored.filled.Undo
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowLeft
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.model.Direction
import com.example.model.TileType
import com.example.ui.GameViewModel
import com.example.ui.ScreenState
import com.example.ui.pixelart.CrtOverlay
import com.example.ui.pixelart.DungeonGameBoard
import com.example.ui.theme.DungeonBorder
import com.example.ui.theme.DungeonCard
import com.example.ui.theme.DungeonDarkBg
import com.example.ui.theme.DungeonSurface
import com.example.ui.theme.PixelCyan
import com.example.ui.theme.PixelEmerald
import com.example.ui.theme.PixelGold
import com.example.ui.theme.PixelRuby
import com.example.ui.theme.RetroTextPrimary
import com.example.ui.theme.RetroTextSecondary

@Composable
fun GamePlayScreen(
    viewModel: GameViewModel,
    modifier: Modifier = Modifier
) {
    val state by viewModel.gameState.collectAsStateWithLifecycle()
    val hintPos by viewModel.hintPosition.collectAsStateWithLifecycle()
    val crtEnabled by viewModel.crtFilterEnabled.collectAsStateWithLifecycle()
    val coins by viewModel.goldCoins.collectAsStateWithLifecycle()
    val freeHints by viewModel.freeHints.collectAsStateWithLifecycle()
    val heroSkin by viewModel.selectedSkin.collectAsStateWithLifecycle()
    val adsRemoved by viewModel.adsRemoved.collectAsStateWithLifecycle()
    val adToast by viewModel.adRewardToast.collectAsStateWithLifecycle()
    val context = LocalContext.current

    if (state == null) {
        Box(modifier = modifier.fillMaxSize().background(DungeonDarkBg))
        return
    }

    val gameState = state!!

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(DungeonDarkBg)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .navigationBarsPadding()
                .padding(horizontal = 14.dp, vertical = 6.dp)
        ) {
            // Header Top Bar
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(
                        onClick = { viewModel.navigateTo(ScreenState.MENU) },
                        modifier = Modifier.testTag("home_game_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Home,
                            contentDescription = "Main Menu Home",
                            tint = PixelGold
                        )
                    }
                    IconButton(
                        onClick = { viewModel.navigateTo(ScreenState.LEVEL_SELECT) },
                        modifier = Modifier.testTag("exit_game_button")
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Exit to Levels",
                            tint = RetroTextSecondary
                        )
                    }
                    Column {
                        Text(
                            text = gameState.levelName,
                            color = PixelGold,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Monospace,
                            maxLines = 1
                        )
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "PAR ${gameState.parMoves}",
                                color = RetroTextSecondary,
                                fontSize = 11.sp,
                                fontFamily = FontFamily.Monospace
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "ALIF",
                                color = PixelCyan,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                fontFamily = FontFamily.Monospace
                            )
                            if (gameState.ironKeys > 0) {
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "🗝️ ${gameState.ironKeys}",
                                    fontSize = 11.sp,
                                    color = PixelCyan
                                )
                            }
                            if (gameState.goldKeys > 0) {
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "🔑 ${gameState.goldKeys}",
                                    fontSize = 11.sp,
                                    color = PixelGold
                                )
                            }
                        }
                    }
                }

                // Moves and coins counter badge
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(DungeonCard)
                            .border(BorderStroke(1.dp, PixelGold.copy(alpha = 0.5f)), RoundedCornerShape(8.dp))
                            .padding(horizontal = 8.dp, vertical = 6.dp)
                    ) {
                        Text(text = "🪙", fontSize = 12.sp)
                        Spacer(modifier = Modifier.width(3.dp))
                        Text(
                            text = "$coins",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = PixelGold,
                            fontFamily = FontFamily.Monospace
                        )
                    }

                    Spacer(modifier = Modifier.width(6.dp))

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(DungeonCard)
                            .border(BorderStroke(1.dp, DungeonBorder), RoundedCornerShape(8.dp))
                            .padding(horizontal = 10.dp, vertical = 6.dp)
                    ) {
                        Column(horizontalAlignment = Alignment.End) {
                            Text(
                                text = "MOVES",
                                fontSize = 9.sp,
                                color = RetroTextSecondary,
                                fontFamily = FontFamily.Monospace
                            )
                            Text(
                                text = "${gameState.stepCount}",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Black,
                                color = if (gameState.stepCount <= gameState.parMoves) PixelEmerald else PixelGold,
                                fontFamily = FontFamily.Monospace
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            // Action Tool Strip (Undo, Reset, Hint, Rotate Mirror)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(8.dp))
                    .background(DungeonSurface)
                    .border(BorderStroke(1.dp, DungeonBorder), RoundedCornerShape(8.dp))
                    .padding(horizontal = 6.dp, vertical = 2.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Undo
                IconButton(
                    onClick = { viewModel.onUndo() },
                    enabled = gameState.undoStack.isNotEmpty(),
                    modifier = Modifier.testTag("undo_button")
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.Undo,
                        contentDescription = "Undo Move",
                        tint = if (gameState.undoStack.isNotEmpty()) PixelGold else RetroTextSecondary.copy(alpha = 0.4f)
                    )
                }

                // Reset
                IconButton(
                    onClick = { viewModel.restartLevel() },
                    modifier = Modifier.testTag("restart_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.Refresh,
                        contentDescription = "Reset Level",
                        tint = PixelRuby
                    )
                }

                // Hint with count / coin cost badge
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.clickable { viewModel.showHint() }
                ) {
                    IconButton(
                        onClick = { viewModel.showHint() },
                        modifier = Modifier.testTag("hint_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Lightbulb,
                            contentDescription = "Hint",
                            tint = if (hintPos != null) PixelGold else PixelCyan
                        )
                    }
                    Text(
                        text = if (freeHints > 0) "$freeHints" else "20🪙",
                        color = if (freeHints > 0) PixelCyan else PixelGold,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace,
                        modifier = Modifier.padding(end = 4.dp)
                    )
                }

                // Rotate Mirror (if mirrors exist in level)
                if (gameState.mirrors.isNotEmpty()) {
                    IconButton(
                        onClick = { viewModel.onRotateMirror() },
                        modifier = Modifier.testTag("rotate_mirror_button")
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.RotateRight,
                            contentDescription = "Rotate Mirror",
                            tint = PixelCyan
                        )
                    }
                }

                // Stars rating projection
                Row(modifier = Modifier.padding(end = 6.dp)) {
                    val currentStars = gameState.calculateStars()
                    for (s in 1..3) {
                        Icon(
                            imageVector = Icons.Default.Star,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp),
                            tint = if (s <= currentStars) PixelGold else Color(0xFF33384D)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            // Main Game Board (Canvas)
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .border(BorderStroke(2.dp, DungeonBorder), RoundedCornerShape(12.dp))
            ) {
                DungeonGameBoard(
                    gameState = gameState,
                    onMove = { dir -> viewModel.onMove(dir) },
                    hintPos = hintPos,
                    heroSkin = heroSkin,
                    modifier = Modifier.fillMaxSize()
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Retro Arcade D-Pad & Controls
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 4.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Instructions / Tips
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "SWIPE OR TAP D-PAD",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = RetroTextSecondary,
                        fontFamily = FontFamily.Monospace
                    )
                    Text(
                        text = if (gameState.totalTargets > 0) {
                            "Goals: ${gameState.filledTargets}/${gameState.totalTargets} active"
                        } else if (gameState.emitterDirections.isNotEmpty()) {
                            if (gameState.isReceiverPowered) "Receiver powered! Head to exit!" else "Align mirrors to power receiver"
                        } else {
                            "Reach the golden stairs"
                        },
                        fontSize = 11.sp,
                        color = PixelCyan,
                        maxLines = 1
                    )
                }

                // D-Pad Cross Layout
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.padding(end = 8.dp)
                ) {
                    // UP
                    DpadButton(
                        icon = Icons.Default.KeyboardArrowUp,
                        contentDesc = "Move Up",
                        tag = "dpad_up",
                        onClick = { viewModel.onMove(Direction.UP) }
                    )

                    Row(
                        horizontalArrangement = Arrangement.spacedBy(4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // LEFT
                        DpadButton(
                            icon = Icons.Default.KeyboardArrowLeft,
                            contentDesc = "Move Left",
                            tag = "dpad_left",
                            onClick = { viewModel.onMove(Direction.LEFT) }
                        )

                        // Center action / Rotate
                        Box(
                            modifier = Modifier
                                .size(48.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(if (gameState.mirrors.isNotEmpty()) PixelCyan.copy(alpha = 0.2f) else DungeonSurface)
                                .clickable { viewModel.onRotateMirror() }
                                .border(BorderStroke(1.dp, DungeonBorder), RoundedCornerShape(8.dp)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.RotateRight,
                                contentDescription = "Action",
                                tint = if (gameState.mirrors.isNotEmpty()) PixelCyan else RetroTextSecondary.copy(alpha = 0.3f),
                                modifier = Modifier.size(20.dp)
                            )
                        }

                        // RIGHT
                        DpadButton(
                            icon = Icons.Default.KeyboardArrowRight,
                            contentDesc = "Move Right",
                            tag = "dpad_right",
                            onClick = { viewModel.onMove(Direction.RIGHT) }
                        )
                    }

                    // DOWN
                    DpadButton(
                        icon = Icons.Default.KeyboardArrowDown,
                        contentDesc = "Move Down",
                        tag = "dpad_down",
                        onClick = { viewModel.onMove(Direction.DOWN) }
                    )
                }
            }
        }

        // CRT Filter Overlay on top
        CrtOverlay(isEnabled = crtEnabled)

        // Victory Modal Dialog
        AnimatedVisibility(
            visible = gameState.isCompleted,
            enter = fadeIn() + scaleIn(),
            exit = fadeOut() + scaleOut()
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color(0xDD0A0C14))
                    .clickable(enabled = false) {},
                contentAlignment = Alignment.Center
            ) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth(0.88f)
                        .padding(16.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = DungeonCard),
                    border = BorderStroke(2.dp, PixelGold)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(20.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "DUNGEON CLEARED!",
                            color = PixelGold,
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Black,
                            fontFamily = FontFamily.Monospace,
                            letterSpacing = 1.sp
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        // Star ratings earned
                        val earnedStars = gameState.calculateStars()
                        Row(
                            horizontalArrangement = Arrangement.Center,
                            modifier = Modifier.padding(vertical = 4.dp)
                        ) {
                            for (s in 1..3) {
                                Icon(
                                    imageVector = Icons.Default.Star,
                                    contentDescription = null,
                                    modifier = Modifier.size(36.dp),
                                    tint = if (s <= earnedStars) PixelGold else Color(0xFF33384D)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        Text(
                            text = "COMPLETED IN ${gameState.stepCount} MOVES",
                            color = RetroTextPrimary,
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp,
                            fontFamily = FontFamily.Monospace
                        )
                        Text(
                            text = "Target Par: ${gameState.parMoves} moves",
                            color = RetroTextSecondary,
                            fontSize = 12.sp,
                            fontFamily = FontFamily.Monospace
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        // Gold coins reward display
                        val coinsWon = when (earnedStars) {
                            3 -> 50
                            2 -> 35
                            else -> 20
                        }
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(DungeonSurface)
                                .border(BorderStroke(1.dp, PixelGold.copy(alpha = 0.6f)), RoundedCornerShape(8.dp))
                                .padding(horizontal = 14.dp, vertical = 6.dp)
                        ) {
                            Text(text = "🪙", fontSize = 16.sp)
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "+$coinsWon Gold Coins",
                                color = PixelGold,
                                fontWeight = FontWeight.Black,
                                fontSize = 14.sp,
                                fontFamily = FontFamily.Monospace
                            )
                        }

                        // Relic award notification
                        if (gameState.relicAwarded != null) {
                            Spacer(modifier = Modifier.height(14.dp))
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(8.dp),
                                colors = CardDefaults.cardColors(containerColor = DungeonSurface),
                                border = BorderStroke(1.dp, PixelRuby)
                            ) {
                                Column(
                                    modifier = Modifier.padding(10.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    Text(
                                        text = "🏺 RELIC DISCOVERED!",
                                        color = PixelRuby,
                                        fontWeight = FontWeight.Black,
                                        fontSize = 13.sp,
                                        fontFamily = FontFamily.Monospace
                                    )
                                    Text(
                                        text = gameState.relicAwarded!!.name,
                                        color = PixelGold,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 12.sp
                                    )
                                    Text(
                                        text = gameState.relicAwarded!!.lore,
                                        color = RetroTextSecondary,
                                        fontSize = 10.sp,
                                        textAlign = TextAlign.Center
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(18.dp))

                        Button(
                            onClick = {
                                if (!adsRemoved) {
                                    com.example.ads.AdMobManager.showInterstitial(
                                        context = context,
                                        onDismiss = { viewModel.onNextLevel() }
                                    )
                                } else {
                                    viewModel.onNextLevel()
                                }
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(50.dp)
                                .testTag("next_level_button"),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = PixelGold,
                                contentColor = Color(0xFF1E1400)
                            ),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Text(
                                text = "NEXT LEVEL",
                                fontWeight = FontWeight.Black,
                                fontSize = 15.sp,
                                fontFamily = FontFamily.Monospace
                            )
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            OutlinedButton(
                                onClick = { viewModel.restartLevel() },
                                modifier = Modifier
                                    .weight(1f)
                                    .height(44.dp)
                                    .testTag("retry_level_button"),
                                shape = RoundedCornerShape(8.dp),
                                border = BorderStroke(1.dp, DungeonBorder)
                            ) {
                                Text(
                                    text = "REPLAY",
                                    fontSize = 12.sp,
                                    fontFamily = FontFamily.Monospace,
                                    color = RetroTextPrimary
                                )
                            }

                            OutlinedButton(
                                onClick = { viewModel.navigateTo(ScreenState.LEVEL_SELECT) },
                                modifier = Modifier
                                    .weight(1f)
                                    .height(44.dp)
                                    .testTag("victory_level_select_button"),
                                shape = RoundedCornerShape(8.dp),
                                border = BorderStroke(1.dp, DungeonBorder)
                            ) {
                                Text(
                                    text = "LEVELS",
                                    fontSize = 12.sp,
                                    fontFamily = FontFamily.Monospace,
                                    color = PixelCyan
                                )
                            }
                        }
                    }
                }
            }
        }

        // Defeat Modal Dialog (e.g. stepping onto active spikes)
        AnimatedVisibility(
            visible = gameState.isDefeated,
            enter = fadeIn(),
            exit = fadeOut()
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color(0xDD180005))
                    .clickable(enabled = false) {},
                contentAlignment = Alignment.Center
            ) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth(0.85f)
                        .padding(16.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = DungeonCard),
                    border = BorderStroke(2.dp, PixelRuby)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(20.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "TRAP SPRUNG!",
                            color = PixelRuby,
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Black,
                            fontFamily = FontFamily.Monospace
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "You stepped on an active spike trap. Spikes toggle every move!",
                            color = RetroTextSecondary,
                            fontSize = 12.sp,
                            textAlign = TextAlign.Center
                        )

                        Spacer(modifier = Modifier.height(18.dp))

                        Button(
                            onClick = { viewModel.onUndo() },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(48.dp)
                                .testTag("undo_defeat_button"),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = PixelGold,
                                contentColor = Color(0xFF1E1400)
                            ),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Icon(imageVector = Icons.AutoMirrored.Filled.Undo, contentDescription = null)
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "UNDO MOVE",
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp,
                                fontFamily = FontFamily.Monospace
                            )
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        OutlinedButton(
                            onClick = { viewModel.restartLevel() },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(44.dp)
                                .testTag("restart_defeat_button"),
                            shape = RoundedCornerShape(8.dp),
                            border = BorderStroke(1.dp, DungeonBorder)
                        ) {
                            Text(
                                text = "RESTART LEVEL",
                                fontSize = 12.sp,
                                fontFamily = FontFamily.Monospace,
                                color = RetroTextPrimary
                            )
                        }
                    }
                }
            }
        }

        // Ad or hint notification toast
        if (adToast != null) {
            Box(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .navigationBarsPadding()
                    .padding(bottom = 20.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(PixelGold)
                    .padding(horizontal = 16.dp, vertical = 8.dp)
                    .clickable { viewModel.dismissAdToast() }
            ) {
                Text(
                    text = adToast!!,
                    color = Color(0xFF1E1400),
                    fontWeight = FontWeight.Bold,
                    fontSize = 12.sp,
                    fontFamily = FontFamily.Monospace
                )
            }
        }
    }
}

@Composable
private fun DpadButton(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    contentDesc: String,
    tag: String,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .size(48.dp)
            .clip(RoundedCornerShape(8.dp))
            .background(DungeonCard)
            .border(BorderStroke(1.dp, DungeonBorder), RoundedCornerShape(8.dp))
            .clickable(onClick = onClick)
            .testTag(tag),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = icon,
            contentDescription = contentDesc,
            tint = PixelGold,
            modifier = Modifier.size(28.dp)
        )
    }
}
