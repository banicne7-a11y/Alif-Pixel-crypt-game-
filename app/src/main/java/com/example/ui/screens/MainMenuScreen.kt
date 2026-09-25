package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.HelpOutline
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Tv
import androidx.compose.material.icons.filled.VolumeMute
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import com.example.ui.components.DailyRewardDialog
import com.example.ui.components.LuckySpinDialog
import com.example.ui.components.AchievementsDialog
import com.example.ui.components.PlayerStatsDialog
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.R
import com.example.ui.GameViewModel
import com.example.ui.ScreenState
import com.example.ui.theme.DungeonBorder
import com.example.ui.theme.DungeonCard
import com.example.ui.theme.DungeonDarkBg
import com.example.ui.theme.DungeonSurface
import com.example.ui.theme.PixelCyan
import com.example.ui.theme.PixelEmerald
import com.example.ui.theme.PixelGold
import com.example.ui.theme.PixelPurple
import com.example.ui.theme.PixelRuby
import com.example.ui.theme.RetroTextPrimary
import com.example.ui.theme.RetroTextSecondary

@Composable
fun MainMenuScreen(
    viewModel: GameViewModel,
    modifier: Modifier = Modifier
) {
    val totalStars by viewModel.totalStarsCount.collectAsStateWithLifecycle()
    val unlockedRelics by viewModel.unlockedRelics.collectAsStateWithLifecycle()
    val coins by viewModel.goldCoins.collectAsStateWithLifecycle()
    val freeHints by viewModel.freeHints.collectAsStateWithLifecycle()
    val progressList by viewModel.levelProgressList.collectAsStateWithLifecycle()
    val crtEnabled by viewModel.crtFilterEnabled.collectAsStateWithLifecycle()
    val soundEnabled by viewModel.soundEnabled.collectAsStateWithLifecycle()
    val playerName by viewModel.playerName.collectAsStateWithLifecycle()
    val currentSkin by viewModel.selectedSkin.collectAsStateWithLifecycle()

    val dailyStreak by viewModel.dailyStreak.collectAsStateWithLifecycle()
    val isDailyRewardClaimable by viewModel.isDailyRewardClaimable.collectAsStateWithLifecycle()
    val isFreeSpinAvailable by viewModel.isFreeSpinAvailable.collectAsStateWithLifecycle()
    val claimedAchievements by viewModel.claimedAchievementIds.collectAsStateWithLifecycle()

    var showNameEditDialog by remember { mutableStateOf(false) }
    var editedNameText by remember { mutableStateOf(playerName) }
    var showDailyRewardDialog by remember { mutableStateOf(false) }
    var showLuckySpinDialog by remember { mutableStateOf(false) }
    var showAchievementsDialog by remember { mutableStateOf(false) }
    var showPlayerStatsDialog by remember { mutableStateOf(false) }

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
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp, vertical = 12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Player Profile Bar (Name: Alif, Title, Avatar)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(DungeonSurface)
                    .border(BorderStroke(1.dp, PixelGold.copy(alpha = 0.6f)), RoundedCornerShape(12.dp))
                    .clickable {
                        showPlayerStatsDialog = true
                    }
                    .padding(horizontal = 14.dp, vertical = 8.dp)
                    .testTag("player_profile_bar"),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(PixelGold.copy(alpha = 0.15f))
                            .border(BorderStroke(1.dp, PixelGold), RoundedCornerShape(8.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(text = "👑", fontSize = 18.sp)
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = playerName,
                                color = PixelGold,
                                fontWeight = FontWeight.Black,
                                fontSize = 16.sp,
                                fontFamily = FontFamily.Monospace
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Icon(
                                imageVector = Icons.Default.Edit,
                                contentDescription = "Edit Name",
                                tint = PixelGold.copy(alpha = 0.7f),
                                modifier = Modifier.size(14.dp)
                            )
                        }
                        Text(
                            text = "Dungeon Master • ${currentSkin.displayName}",
                            color = PixelCyan,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold,
                            fontFamily = FontFamily.Monospace
                        )
                    }
                }

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(PixelGold.copy(alpha = 0.2f))
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = "LVL 200 QUEST",
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold,
                        color = PixelGold,
                        fontFamily = FontFamily.Monospace
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Header Hero Banner Card
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp),
                shape = RoundedCornerShape(12.dp),
                border = BorderStroke(2.dp, DungeonBorder),
                colors = CardDefaults.cardColors(containerColor = DungeonSurface)
            ) {
                Box(modifier = Modifier.fillMaxSize()) {
                    Image(
                        painter = painterResource(id = R.drawable.img_hero_banner),
                        contentDescription = "Pixel Crypt Banner",
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                    // Dark gradient overlay for text readability
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(
                                Brush.verticalGradient(
                                    colors = listOf(
                                        Color.Transparent,
                                        Color(0xBB0A0C14),
                                        Color(0xF50A0C14)
                                    )
                                )
                            )
                    )

                    Column(
                        modifier = Modifier
                            .align(Alignment.BottomStart)
                            .padding(14.dp)
                    ) {
                        Text(
                            text = "ALIF • PIXEL CRYPT",
                            color = PixelGold,
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Black,
                            fontFamily = FontFamily.Monospace,
                            letterSpacing = 1.5.sp
                        )
                        Text(
                            text = "OFFICIAL 200 ROOMS • 16-BIT EXPEDITION",
                            color = PixelCyan,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Monospace,
                            letterSpacing = 1.sp
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Player Stats Quick Strip
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(10.dp))
                    .background(DungeonCard)
                    .border(BorderStroke(1.dp, DungeonBorder), RoundedCornerShape(10.dp))
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                horizontalArrangement = Arrangement.SpaceAround,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Star,
                        contentDescription = "Stars",
                        tint = PixelGold,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Column {
                        Text(
                            text = "${totalStars ?: 0} / 600",
                            color = RetroTextPrimary,
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp,
                            fontFamily = FontFamily.Monospace
                        )
                        Text(
                            text = "Stars Earned",
                            color = RetroTextSecondary,
                            fontSize = 10.sp
                        )
                    }
                }

                Box(
                    modifier = Modifier
                        .height(28.dp)
                        .width(1.dp)
                        .background(DungeonBorder)
                )

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "🏺",
                        fontSize = 18.sp
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Column {
                        Text(
                            text = "${unlockedRelics.size} / 8",
                            color = PixelPurple,
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp,
                            fontFamily = FontFamily.Monospace
                        )
                        Text(
                            text = "Relics Vault",
                            color = RetroTextSecondary,
                            fontSize = 10.sp
                        )
                    }
                }

                Box(
                    modifier = Modifier
                        .height(28.dp)
                        .width(1.dp)
                        .background(DungeonBorder)
                )

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "🪙",
                        fontSize = 18.sp
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Column {
                        Text(
                            text = "$coins",
                            color = PixelGold,
                            fontWeight = FontWeight.Black,
                            fontSize = 15.sp,
                            fontFamily = FontFamily.Monospace
                        )
                        Text(
                            text = "Gold Coins",
                            color = RetroTextSecondary,
                            fontSize = 10.sp
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Primary Big Button: Play Campaign
            Button(
                onClick = { viewModel.navigateTo(ScreenState.LEVEL_SELECT) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
                    .testTag("play_campaign_button"),
                colors = ButtonDefaults.buttonColors(
                    containerColor = PixelGold,
                    contentColor = Color(0xFF1E1400)
                ),
                shape = RoundedCornerShape(10.dp),
                border = BorderStroke(2.dp, Color(0xFFFFFAEB))
            ) {
                Icon(
                    imageVector = Icons.Default.PlayArrow,
                    contentDescription = null,
                    modifier = Modifier.size(26.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "PLAY CAMPAIGN",
                    fontWeight = FontWeight.Black,
                    fontSize = 17.sp,
                    fontFamily = FontFamily.Monospace,
                    letterSpacing = 1.sp
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Secondary: Daily Dungeon
            OutlinedButton(
                onClick = { viewModel.startDailyDungeon() },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
                    .testTag("daily_dungeon_button"),
                colors = ButtonDefaults.outlinedButtonColors(
                    containerColor = DungeonCard,
                    contentColor = PixelCyan
                ),
                border = BorderStroke(1.5.dp, PixelCyan),
                shape = RoundedCornerShape(10.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.CalendarToday,
                    contentDescription = null,
                    modifier = Modifier.size(20.dp),
                    tint = PixelCyan
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "DAILY CRYPT TRIAL",
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    fontFamily = FontFamily.Monospace
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Daily Rewards & Lucky Fortune Wheel Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Button(
                    onClick = { showDailyRewardDialog = true },
                    modifier = Modifier
                        .weight(1f)
                        .height(52.dp)
                        .testTag("daily_reward_button"),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (isDailyRewardClaimable) Color(0xFF332002) else DungeonCard,
                        contentColor = PixelGold
                    ),
                    border = BorderStroke(1.5.dp, if (isDailyRewardClaimable) PixelGold else DungeonBorder),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Text(text = "🎁", fontSize = 16.sp)
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = if (isDailyRewardClaimable) "DAILY GIFT • 🔴" else "DAILY GIFT",
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp,
                        fontFamily = FontFamily.Monospace
                    )
                }

                Button(
                    onClick = { showLuckySpinDialog = true },
                    modifier = Modifier
                        .weight(1f)
                        .height(52.dp)
                        .testTag("lucky_wheel_button"),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (isFreeSpinAvailable) Color(0xFF0F2618) else DungeonCard,
                        contentColor = PixelEmerald
                    ),
                    border = BorderStroke(1.5.dp, if (isFreeSpinAvailable) PixelEmerald else DungeonBorder),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Text(text = "🎡", fontSize = 16.sp)
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = if (isFreeSpinAvailable) "LUCKY SPIN • 🔴" else "LUCKY SPIN",
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp,
                        fontFamily = FontFamily.Monospace
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Crypt Bazaar & Skins Shop
            Button(
                onClick = { viewModel.navigateTo(ScreenState.SHOP) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
                    .testTag("shop_bazaar_button"),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF231633),
                    contentColor = PixelGold
                ),
                border = BorderStroke(1.5.dp, PixelGold),
                shape = RoundedCornerShape(10.dp)
            ) {
                Text(text = "🪙", fontSize = 18.sp)
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "CRYPT BAZAAR & SKINS",
                    fontWeight = FontWeight.Black,
                    fontSize = 14.sp,
                    fontFamily = FontFamily.Monospace,
                    letterSpacing = 1.sp
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Three Action Buttons: Maker Mode, Relic Vault, & Trophy Hall
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedButton(
                    onClick = { viewModel.navigateTo(ScreenState.MAKER) },
                    modifier = Modifier
                        .weight(1f)
                        .height(50.dp)
                        .testTag("maker_mode_button"),
                    colors = ButtonDefaults.outlinedButtonColors(
                        containerColor = DungeonCard,
                        contentColor = PixelPurple
                    ),
                    border = BorderStroke(1.dp, DungeonBorder),
                    shape = RoundedCornerShape(10.dp),
                    contentPadding = PaddingValues(horizontal = 4.dp)
                ) {
                    Text(
                        text = "🔨 MAKER",
                        fontWeight = FontWeight.Bold,
                        fontSize = 11.sp,
                        fontFamily = FontFamily.Monospace
                    )
                }

                OutlinedButton(
                    onClick = { viewModel.navigateTo(ScreenState.RELIC_GALLERY) },
                    modifier = Modifier
                        .weight(1f)
                        .height(50.dp)
                        .testTag("relic_vault_button"),
                    colors = ButtonDefaults.outlinedButtonColors(
                        containerColor = DungeonCard,
                        contentColor = PixelRuby
                    ),
                    border = BorderStroke(1.dp, DungeonBorder),
                    shape = RoundedCornerShape(10.dp),
                    contentPadding = PaddingValues(horizontal = 4.dp)
                ) {
                    Text(
                        text = "🏺 VAULT",
                        fontWeight = FontWeight.Bold,
                        fontSize = 11.sp,
                        fontFamily = FontFamily.Monospace
                    )
                }

                OutlinedButton(
                    onClick = { showAchievementsDialog = true },
                    modifier = Modifier
                        .weight(1f)
                        .height(50.dp)
                        .testTag("trophies_button"),
                    colors = ButtonDefaults.outlinedButtonColors(
                        containerColor = DungeonCard,
                        contentColor = PixelCyan
                    ),
                    border = BorderStroke(1.dp, DungeonBorder),
                    shape = RoundedCornerShape(10.dp),
                    contentPadding = PaddingValues(horizontal = 4.dp)
                ) {
                    Text(
                        text = "🏆 TROPHIES",
                        fontWeight = FontWeight.Bold,
                        fontSize = 11.sp,
                        fontFamily = FontFamily.Monospace
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // How To Play
            OutlinedButton(
                onClick = { viewModel.navigateTo(ScreenState.HOW_TO_PLAY) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
                    .testTag("how_to_play_button"),
                colors = ButtonDefaults.outlinedButtonColors(
                    containerColor = DungeonCard,
                    contentColor = RetroTextSecondary
                ),
                border = BorderStroke(1.dp, DungeonBorder),
                shape = RoundedCornerShape(10.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.HelpOutline,
                    contentDescription = null,
                    modifier = Modifier.size(18.dp),
                    tint = RetroTextSecondary
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "HOW TO PLAY & PUZZLE MECHANICS",
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 12.sp,
                    fontFamily = FontFamily.Monospace
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Retro Settings Toolbar (CRT toggle, Sound toggle)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(DungeonSurface)
                    .border(BorderStroke(1.dp, DungeonBorder), RoundedCornerShape(12.dp))
                    .padding(horizontal = 14.dp, vertical = 6.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(vertical = 4.dp)
                ) {
                    IconButton(
                        onClick = { viewModel.toggleCrtFilter() },
                        modifier = Modifier.testTag("toggle_crt_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Tv,
                            contentDescription = "CRT Filter",
                            tint = if (crtEnabled) PixelCyan else RetroTextSecondary
                        )
                    }
                    Column {
                        Text(
                            text = "CRT SCANLINES",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Monospace,
                            color = if (crtEnabled) PixelCyan else RetroTextSecondary
                        )
                        Text(
                            text = if (crtEnabled) "ACTIVE (RETRO ON)" else "OFF (FLAT)",
                            fontSize = 9.sp,
                            color = RetroTextSecondary
                        )
                    }
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(vertical = 4.dp)
                ) {
                    IconButton(
                        onClick = { viewModel.toggleSound() },
                        modifier = Modifier.testTag("toggle_sound_button")
                    ) {
                        Icon(
                            imageVector = if (soundEnabled) Icons.Default.VolumeUp else Icons.Default.VolumeMute,
                            contentDescription = "Sound FX",
                            tint = if (soundEnabled) PixelGold else RetroTextSecondary
                        )
                    }
                    Column {
                        Text(
                            text = "8-BIT AUDIO",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Monospace,
                            color = if (soundEnabled) PixelGold else RetroTextSecondary
                        )
                        Text(
                            text = if (soundEnabled) "CHIPTUNE ON" else "MUTED",
                            fontSize = 9.sp,
                            color = RetroTextSecondary
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Banner Ad at bottom of Main Menu
            com.example.ads.AdMobBanner(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp)
            )

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = "CREATOR: ALIF • PIXEL CRYPT ENGINE V2.0 (200 LEVELS)",
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = PixelGold.copy(alpha = 0.85f),
                fontFamily = FontFamily.Monospace
            )
            Text(
                text = "© 2026 ALL RIGHTS RESERVED",
                fontSize = 9.sp,
                color = RetroTextSecondary.copy(alpha = 0.5f),
                fontFamily = FontFamily.Monospace
            )
        }

        // Edit Player Name Dialog
        if (showNameEditDialog) {
            AlertDialog(
                onDismissRequest = { showNameEditDialog = false },
                containerColor = DungeonCard,
                titleContentColor = PixelGold,
                textContentColor = RetroTextPrimary,
                title = {
                    Text(
                        text = "CHAMPION PROFILE",
                        fontFamily = FontFamily.Monospace,
                        fontWeight = FontWeight.Black,
                        fontSize = 18.sp
                    )
                },
                text = {
                    Column {
                        Text(
                            text = "Enter your hero name displayed in the crypt hall of fame:",
                            fontSize = 12.sp,
                            color = RetroTextSecondary
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        OutlinedTextField(
                            value = editedNameText,
                            onValueChange = { if (it.length <= 20) editedNameText = it },
                            label = { Text("Hero Name", color = PixelGold) },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                },
                confirmButton = {
                    Button(
                        onClick = {
                            viewModel.updatePlayerName(editedNameText)
                            showNameEditDialog = false
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = PixelGold, contentColor = Color(0xFF1E1400))
                    ) {
                        Text("SAVE NAME", fontWeight = FontWeight.Bold, fontFamily = FontFamily.Monospace)
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showNameEditDialog = false }) {
                        Text("CANCEL", color = RetroTextSecondary, fontFamily = FontFamily.Monospace)
                    }
                }
            )
        }

        // Daily Login Bonus Dialog
        if (showDailyRewardDialog) {
            DailyRewardDialog(
                currentStreak = dailyStreak,
                isClaimable = isDailyRewardClaimable,
                onClaim = { viewModel.claimDailyReward() },
                onDismiss = { showDailyRewardDialog = false }
            )
        }

        // Lucky Fortune Spin Wheel Dialog
        if (showLuckySpinDialog) {
            LuckySpinDialog(
                isFreeSpinAvailable = isFreeSpinAvailable,
                onSpin = { isRewarded, onResult ->
                    viewModel.spinWheel(isRewarded) { idx, c, h, desc ->
                        onResult(idx, c, h, desc)
                    }
                },
                onDismiss = { showLuckySpinDialog = false }
            )
        }

        // Trophy & Achievement Hall Dialog
        if (showAchievementsDialog) {
            AchievementsDialog(
                claimedIds = claimedAchievements,
                totalLevelsCleared = progressList.count { it.completed },
                totalStars = totalStars ?: 0,
                totalRelics = unlockedRelics.size,
                currentHints = freeHints,
                currentCoins = coins,
                onClaim = { id, c, h -> viewModel.claimAchievement(id, c, h) },
                onDismiss = { showAchievementsDialog = false }
            )
        }

        // Player Stats & Dossier Dialog
        if (showPlayerStatsDialog) {
            PlayerStatsDialog(
                playerName = playerName,
                heroSkin = currentSkin,
                levelsCleared = progressList.count { it.completed },
                totalStars = totalStars ?: 0,
                relicsCount = unlockedRelics.size,
                dailyStreak = dailyStreak,
                coins = coins,
                hints = freeHints,
                onEditName = {
                    editedNameText = playerName
                    showNameEditDialog = true
                },
                onDismiss = { showPlayerStatsDialog = false }
            )
        }
    }
}
