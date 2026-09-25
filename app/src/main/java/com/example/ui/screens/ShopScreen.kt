package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CardGiftcard
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Diamond
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.material.icons.filled.Stars
import androidx.compose.material.icons.filled.Tv
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults.SecondaryIndicator
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.model.DungeonTheme
import com.example.model.HeroSkin
import com.example.ui.components.DailyRewardDialog
import com.example.ui.components.LuckySpinDialog
import com.example.ui.GameViewModel
import com.example.ui.ScreenState
import com.example.ui.pixelart.PixelSprites
import com.example.ui.theme.DungeonBorder
import com.example.ui.theme.DungeonCard
import com.example.ui.theme.DungeonDarkBg
import com.example.ui.theme.DungeonSurface
import com.example.ui.theme.PixelAmber
import com.example.ui.theme.PixelCyan
import com.example.ui.theme.PixelEmerald
import com.example.ui.theme.PixelGold
import com.example.ui.theme.PixelPurple
import com.example.ui.theme.PixelRuby
import com.example.ui.theme.RetroTextPrimary
import com.example.ui.theme.RetroTextSecondary

enum class ShopTab(val title: String, val icon: String) {
    ARMORY("ARMORY", "🛡️"),
    BRICKS("BRICKS", "🧱"),
    SUPPLIES("SUPPLIES", "💡"),
    ADS_REWARDS("REWARDS", "🎁")
}

@Composable
fun ShopScreen(
    viewModel: GameViewModel,
    modifier: Modifier = Modifier
) {
    val coins by viewModel.goldCoins.collectAsStateWithLifecycle()
    val hints by viewModel.freeHints.collectAsStateWithLifecycle()
    val selectedSkin by viewModel.selectedSkin.collectAsStateWithLifecycle()
    val unlockedSkins by viewModel.unlockedSkinIds.collectAsStateWithLifecycle()
    val selectedTheme by viewModel.selectedTheme.collectAsStateWithLifecycle()
    val unlockedThemes by viewModel.unlockedThemeIds.collectAsStateWithLifecycle()
    val adToast by viewModel.adRewardToast.collectAsStateWithLifecycle()
    val dailyStreak by viewModel.dailyStreak.collectAsStateWithLifecycle()
    val isDailyRewardClaimable by viewModel.isDailyRewardClaimable.collectAsStateWithLifecycle()
    val isFreeSpinAvailable by viewModel.isFreeSpinAvailable.collectAsStateWithLifecycle()
    val context = LocalContext.current

    var selectedTabIndex by remember { mutableIntStateOf(0) }
    var inspectSkin by remember { mutableStateOf<HeroSkin?>(null) }
    var inspectTheme by remember { mutableStateOf<DungeonTheme?>(null) }
    var showDailyRewardDialog by remember { mutableStateOf(false) }
    var showLuckySpinDialog by remember { mutableStateOf(false) }
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(adToast) {
        adToast?.let { msg ->
            snackbarHostState.showSnackbar(msg)
            viewModel.dismissAdToast()
        }
    }

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
        ) {
            // ================= HEADER TOP BAR =================
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 10.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(
                        onClick = { viewModel.navigateTo(ScreenState.MENU) },
                        modifier = Modifier.testTag("back_from_shop_button")
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back to Menu",
                            tint = PixelGold
                        )
                    }
                    Spacer(modifier = Modifier.width(4.dp))
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "CRYPT BAZAAR",
                                color = PixelGold,
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Black,
                                fontFamily = FontFamily.Monospace,
                                letterSpacing = 1.sp
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(4.dp))
                                    .background(PixelRuby.copy(alpha = 0.25f))
                                    .border(1.dp, PixelRuby, RoundedCornerShape(4.dp))
                                    .padding(horizontal = 6.dp, vertical = 2.dp)
                            ) {
                                Text(
                                    text = "ALIF",
                                    color = PixelRuby,
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.Bold,
                                    fontFamily = FontFamily.Monospace
                                )
                            }
                        }
                        Text(
                            text = "ARMORY • BRICKS • SUPPLIES",
                            color = PixelCyan,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Monospace
                        )
                    }
                }

                // Balance Badges Row
                Row(verticalAlignment = Alignment.CenterVertically) {
                    // Hints badge
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(DungeonCard)
                            .border(BorderStroke(1.dp, PixelCyan.copy(alpha = 0.4f)), RoundedCornerShape(8.dp))
                            .padding(horizontal = 8.dp, vertical = 6.dp)
                    ) {
                        Text(text = "💡", fontSize = 12.sp)
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "$hints",
                            color = PixelCyan,
                            fontWeight = FontWeight.Black,
                            fontSize = 13.sp,
                            fontFamily = FontFamily.Monospace
                        )
                    }

                    Spacer(modifier = Modifier.width(6.dp))

                    // Gold Coins Badge
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(
                                Brush.horizontalGradient(
                                    listOf(Color(0xFF2E2200), Color(0xFF1E1400))
                                )
                            )
                            .border(BorderStroke(1.5.dp, PixelGold), RoundedCornerShape(8.dp))
                            .padding(horizontal = 10.dp, vertical = 6.dp)
                    ) {
                        Text(text = "🪙", fontSize = 14.sp)
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "$coins",
                            color = PixelGold,
                            fontWeight = FontWeight.Black,
                            fontSize = 14.sp,
                            fontFamily = FontFamily.Monospace
                        )
                    }
                }
            }

            // ================= TAB NAVIGATION =================
            TabRow(
                selectedTabIndex = selectedTabIndex,
                containerColor = DungeonCard,
                contentColor = PixelGold,
                indicator = { tabPositions ->
                    SecondaryIndicator(
                        modifier = Modifier.tabIndicatorOffset(tabPositions[selectedTabIndex]),
                        color = PixelGold,
                        height = 3.dp
                    )
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .border(BorderStroke(1.dp, DungeonBorder), RoundedCornerShape(10.dp))
            ) {
                ShopTab.entries.forEachIndexed { index, tab ->
                    val isSelected = selectedTabIndex == index
                    Tab(
                        selected = isSelected,
                        onClick = { selectedTabIndex = index },
                        modifier = Modifier.testTag("shop_tab_${tab.name.lowercase()}"),
                        text = {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.Center
                            ) {
                                Text(
                                    text = tab.icon,
                                    fontSize = 11.sp
                                )
                                Spacer(modifier = Modifier.width(3.dp))
                                Text(
                                    text = tab.title,
                                    fontSize = 10.sp,
                                    fontWeight = if (isSelected) FontWeight.Black else FontWeight.Bold,
                                    fontFamily = FontFamily.Monospace,
                                    color = if (isSelected) PixelGold else RetroTextSecondary
                                )
                            }
                        }
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // ================= TAB CONTENT =================
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
            ) {
                when (ShopTab.entries[selectedTabIndex]) {
                    ShopTab.ARMORY -> ArmoryTabContent(
                        selectedSkin = selectedSkin,
                        unlockedSkins = unlockedSkins,
                        coins = coins,
                        onSelectSkin = { skin -> inspectSkin = skin },
                        onEquipSkin = { skin -> viewModel.equipSkin(skin) },
                        onBuySkin = { skin -> viewModel.purchaseSkin(skin) }
                    )
                    ShopTab.BRICKS -> BricksTabContent(
                        selectedTheme = selectedTheme,
                        unlockedThemes = unlockedThemes,
                        coins = coins,
                        onSelectTheme = { theme -> inspectTheme = theme },
                        onEquipTheme = { theme -> viewModel.equipTheme(theme) },
                        onBuyTheme = { theme -> viewModel.purchaseTheme(theme) }
                    )
                    ShopTab.SUPPLIES -> SuppliesTabContent(
                        coins = coins,
                        hints = hints,
                        onBuyHints = { viewModel.buyHintsWithCoins() },
                        onWatchAdForHints = {
                            com.example.ads.AdMobManager.showRewardedAd(
                                context = context,
                                onRewardEarned = { viewModel.watchRewardedAdForHints() },
                                onAdNotReady = { viewModel.watchRewardedAdForHints() }
                            )
                        }
                    )
                    ShopTab.ADS_REWARDS -> AdsAndRewardsTabContent(
                        isDailyClaimable = isDailyRewardClaimable,
                        isFreeSpinAvailable = isFreeSpinAvailable,
                        onOpenDailyGift = { showDailyRewardDialog = true },
                        onOpenLuckySpin = { showLuckySpinDialog = true },
                        onWatchAdForCoins = {
                            com.example.ads.AdMobManager.showRewardedAd(
                                context = context,
                                onRewardEarned = { viewModel.watchRewardedAdForCoins() },
                                onAdNotReady = { viewModel.watchRewardedAdForCoins() }
                            )
                        },
                        onWatchAdForHints = {
                            com.example.ads.AdMobManager.showRewardedAd(
                                context = context,
                                onRewardEarned = { viewModel.watchRewardedAdForHints() },
                                onAdNotReady = { viewModel.watchRewardedAdForHints() }
                            )
                        }
                    )
                }
            }

            // Banner Ad at bottom of ShopScreen (Always running)
            Spacer(modifier = Modifier.height(4.dp))
            com.example.ads.AdMobBanner(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp)
            )
            Spacer(modifier = Modifier.height(4.dp))
        }

        // ================= HERO SKIN INSPECT MODAL =================
        inspectSkin?.let { skin ->
            val isOwned = unlockedSkins.contains(skin.id)
            val isEquipped = selectedSkin.id == skin.id

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.75f))
                    .clickable { inspectSkin = null },
                contentAlignment = Alignment.Center
            ) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth(0.9f)
                        .clickable(enabled = false) {},
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = DungeonCard),
                    border = BorderStroke(2.dp, Color(skin.primaryColorHex))
                ) {
                    Column(
                        modifier = Modifier.padding(20.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        // Large Pixel Art Avatar
                        PixelHeroBigPreview(
                            primaryColor = Color(skin.primaryColorHex),
                            secondaryColor = Color(skin.secondaryColorHex),
                            visorColor = Color(skin.visorColorHex),
                            plumeColor = Color(skin.plumeColorHex),
                            modifier = Modifier.size(100.dp)
                        )

                        Spacer(modifier = Modifier.height(14.dp))

                        Text(
                            text = skin.displayName.uppercase(),
                            color = Color(skin.primaryColorHex),
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Black,
                            fontFamily = FontFamily.Monospace
                        )
                        Text(
                            text = skin.title,
                            color = PixelCyan,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Monospace
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        Text(
                            text = skin.lore,
                            color = RetroTextPrimary,
                            fontSize = 12.sp,
                            textAlign = TextAlign.Center,
                            lineHeight = 18.sp
                        )

                        Spacer(modifier = Modifier.height(18.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            OutlinedButton(
                                onClick = { inspectSkin = null },
                                modifier = Modifier.weight(1f),
                                border = BorderStroke(1.dp, DungeonBorder),
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Text("CLOSE", color = RetroTextSecondary, fontFamily = FontFamily.Monospace)
                            }

                            if (isEquipped) {
                                Button(
                                    onClick = { inspectSkin = null },
                                    enabled = false,
                                    modifier = Modifier.weight(1f),
                                    colors = ButtonDefaults.buttonColors(containerColor = PixelEmerald),
                                    shape = RoundedCornerShape(8.dp)
                                ) {
                                    Text("EQUIPPED ✓", fontWeight = FontWeight.Bold, fontFamily = FontFamily.Monospace)
                                }
                            } else if (isOwned) {
                                Button(
                                    onClick = {
                                        viewModel.equipSkin(skin)
                                        inspectSkin = null
                                    },
                                    modifier = Modifier.weight(1f),
                                    colors = ButtonDefaults.buttonColors(containerColor = PixelGold, contentColor = Color(0xFF1E1400)),
                                    shape = RoundedCornerShape(8.dp)
                                ) {
                                    Text("EQUIP NOW", fontWeight = FontWeight.Black, fontFamily = FontFamily.Monospace)
                                }
                            } else {
                                Button(
                                    onClick = {
                                        viewModel.purchaseSkin(skin)
                                        inspectSkin = null
                                    },
                                    enabled = coins >= skin.costCoins,
                                    modifier = Modifier.weight(1f),
                                    colors = ButtonDefaults.buttonColors(containerColor = PixelGold, contentColor = Color(0xFF1E1400)),
                                    shape = RoundedCornerShape(8.dp)
                                ) {
                                    Text("BUY ${skin.costCoins} 🪙", fontWeight = FontWeight.Black, fontFamily = FontFamily.Monospace)
                                }
                            }
                        }
                    }
                }
            }
        }

        // ================= BRICKS / THEME INSPECT MODAL =================
        inspectTheme?.let { theme ->
            val isOwned = unlockedThemes.contains(theme.id)
            val isEquipped = selectedTheme.id == theme.id

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.75f))
                    .clickable { inspectTheme = null },
                contentAlignment = Alignment.Center
            ) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth(0.9f)
                        .clickable(enabled = false) {},
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = DungeonCard),
                    border = BorderStroke(2.dp, Color(theme.wallHighlightHex))
                ) {
                    Column(
                        modifier = Modifier.padding(20.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        // Big Brick & Floor Sample Preview
                        Box(
                            modifier = Modifier
                                .size(110.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .border(2.dp, Color(theme.wallHighlightHex), RoundedCornerShape(12.dp))
                        ) {
                            Canvas(modifier = Modifier.fillMaxSize()) {
                                val s = size.width / 2f
                                // Top-Left: Wall
                                PixelSprites.drawWall(
                                    scope = this,
                                    x = 0f,
                                    y = 0f,
                                    tileSize = s,
                                    darkStone = Color(theme.wallDarkColorHex),
                                    midStone = Color(theme.wallMidColorHex),
                                    lightStone = Color(theme.wallLightColorHex),
                                    highlight = Color(theme.wallHighlightHex)
                                )
                                // Top-Right: Floor
                                PixelSprites.drawFloor(
                                    scope = this,
                                    x = s,
                                    y = 0f,
                                    tileSize = s,
                                    floorBg = Color(theme.floorBgColorHex),
                                    tileAccent = Color(theme.floorAccentColorHex)
                                )
                                // Bottom-Left: Floor
                                PixelSprites.drawFloor(
                                    scope = this,
                                    x = 0f,
                                    y = s,
                                    tileSize = s,
                                    floorBg = Color(theme.floorBgColorHex),
                                    tileAccent = Color(theme.floorAccentColorHex)
                                )
                                // Bottom-Right: Wall
                                PixelSprites.drawWall(
                                    scope = this,
                                    x = s,
                                    y = s,
                                    tileSize = s,
                                    darkStone = Color(theme.wallDarkColorHex),
                                    midStone = Color(theme.wallMidColorHex),
                                    lightStone = Color(theme.wallLightColorHex),
                                    highlight = Color(theme.wallHighlightHex)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        Text(
                            text = theme.displayName.uppercase(),
                            color = Color(theme.wallHighlightHex),
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Black,
                            fontFamily = FontFamily.Monospace
                        )
                        Text(
                            text = theme.title,
                            color = PixelCyan,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Monospace
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        Text(
                            text = theme.lore,
                            color = RetroTextPrimary,
                            fontSize = 12.sp,
                            textAlign = TextAlign.Center,
                            lineHeight = 18.sp
                        )

                        Spacer(modifier = Modifier.height(18.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            OutlinedButton(
                                onClick = { inspectTheme = null },
                                modifier = Modifier.weight(1f),
                                border = BorderStroke(1.dp, DungeonBorder),
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Text("CLOSE", color = RetroTextSecondary, fontFamily = FontFamily.Monospace)
                            }

                            if (isEquipped) {
                                Button(
                                    onClick = { inspectTheme = null },
                                    enabled = false,
                                    modifier = Modifier.weight(1f),
                                    colors = ButtonDefaults.buttonColors(containerColor = PixelEmerald),
                                    shape = RoundedCornerShape(8.dp)
                                ) {
                                    Text("EQUIPPED ✓", fontWeight = FontWeight.Bold, fontFamily = FontFamily.Monospace)
                                }
                            } else if (isOwned) {
                                Button(
                                    onClick = {
                                        viewModel.equipTheme(theme)
                                        inspectTheme = null
                                    },
                                    modifier = Modifier.weight(1f),
                                    colors = ButtonDefaults.buttonColors(containerColor = PixelGold, contentColor = Color(0xFF1E1400)),
                                    shape = RoundedCornerShape(8.dp)
                                ) {
                                    Text("EQUIP NOW", fontWeight = FontWeight.Black, fontFamily = FontFamily.Monospace)
                                }
                            } else {
                                Button(
                                    onClick = {
                                        viewModel.purchaseTheme(theme)
                                        inspectTheme = null
                                    },
                                    enabled = coins >= theme.costCoins,
                                    modifier = Modifier.weight(1f),
                                    colors = ButtonDefaults.buttonColors(containerColor = PixelGold, contentColor = Color(0xFF1E1400)),
                                    shape = RoundedCornerShape(8.dp)
                                ) {
                                    Text("BUY ${theme.costCoins} 🪙", fontWeight = FontWeight.Black, fontFamily = FontFamily.Monospace)
                                }
                            }
                        }
                    }
                }
            }
        }

        SnackbarHost(
            hostState = snackbarHostState,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .navigationBarsPadding()
        )

        if (showDailyRewardDialog) {
            DailyRewardDialog(
                currentStreak = dailyStreak,
                isClaimable = isDailyRewardClaimable,
                onClaim = { viewModel.claimDailyReward() },
                onDismiss = { showDailyRewardDialog = false }
            )
        }

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
    }
}

// ================= TAB 1: ARMORY (HERO SKINS - 3x2 BOX GRID) =================
@Composable
private fun ArmoryTabContent(
    selectedSkin: HeroSkin,
    unlockedSkins: Set<String>,
    coins: Int,
    onSelectSkin: (HeroSkin) -> Unit,
    onEquipSkin: (HeroSkin) -> Unit,
    onBuySkin: (HeroSkin) -> Unit
) {
    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        // Banner Hero Preview
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(14.dp),
            colors = CardDefaults.cardColors(
                containerColor = Color(0xFF151824)
            ),
            border = BorderStroke(1.5.dp, Color(selectedSkin.primaryColorHex))
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                PixelHeroBigPreview(
                    primaryColor = Color(selectedSkin.primaryColorHex),
                    secondaryColor = Color(selectedSkin.secondaryColorHex),
                    visorColor = Color(selectedSkin.visorColorHex),
                    plumeColor = Color(selectedSkin.plumeColorHex),
                    modifier = Modifier.size(54.dp)
                )

                Spacer(modifier = Modifier.width(12.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "CURRENTLY EQUIPPED",
                            color = PixelEmerald,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Monospace,
                            letterSpacing = 1.sp
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Icon(
                            imageVector = Icons.Default.Check,
                            contentDescription = null,
                            tint = PixelEmerald,
                            modifier = Modifier.size(14.dp)
                        )
                    }
                    Text(
                        text = selectedSkin.displayName,
                        color = Color(selectedSkin.primaryColorHex),
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Black,
                        fontFamily = FontFamily.Monospace
                    )
                    Text(
                        text = selectedSkin.title,
                        color = RetroTextSecondary,
                        fontSize = 11.sp
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "ARMOR VAULT (3x2 BOX GRID)",
                color = PixelCyan,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = FontFamily.Monospace,
                letterSpacing = 1.sp
            )
            Text(
                text = "${unlockedSkins.size}/${HeroSkin.entries.size} UNLOCKED",
                color = PixelGold,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = FontFamily.Monospace
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        // 3 items per row, 2 rows (6 total skins) in Box System
        LazyVerticalGrid(
            columns = GridCells.Fixed(3),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.weight(1f),
            contentPadding = PaddingValues(bottom = 16.dp)
        ) {
            items(HeroSkin.entries) { skin ->
                val isOwned = unlockedSkins.contains(skin.id)
                val isEquipped = selectedSkin.id == skin.id

                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .clickable { onSelectSkin(skin) },
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = if (isEquipped) Color(0xFF1E2337) else DungeonCard
                    ),
                    border = BorderStroke(
                        if (isEquipped) 2.dp else 1.dp,
                        if (isEquipped) Color(skin.primaryColorHex) else DungeonBorder
                    )
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(8.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        // Badge at top of Box
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(16.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            if (isEquipped) {
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(3.dp))
                                        .background(PixelEmerald)
                                        .padding(horizontal = 4.dp, vertical = 1.dp)
                                ) {
                                    Text(
                                        text = "ACTIVE",
                                        color = Color.Black,
                                        fontSize = 8.sp,
                                        fontWeight = FontWeight.Black,
                                        fontFamily = FontFamily.Monospace
                                    )
                                }
                            } else if (isOwned) {
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(3.dp))
                                        .background(PixelCyan.copy(alpha = 0.2f))
                                        .border(0.5.dp, PixelCyan, RoundedCornerShape(3.dp))
                                        .padding(horizontal = 4.dp, vertical = 1.dp)
                                ) {
                                    Text(
                                        text = "OWNED",
                                        color = PixelCyan,
                                        fontSize = 8.sp,
                                        fontWeight = FontWeight.Bold,
                                        fontFamily = FontFamily.Monospace
                                    )
                                }
                            } else {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Lock,
                                        contentDescription = null,
                                        tint = RetroTextSecondary,
                                        modifier = Modifier.size(10.dp)
                                    )
                                    Spacer(modifier = Modifier.width(2.dp))
                                    Text(
                                        text = "${skin.costCoins}🪙",
                                        color = PixelGold,
                                        fontSize = 8.sp,
                                        fontWeight = FontWeight.Bold,
                                        fontFamily = FontFamily.Monospace
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(4.dp))

                        // Character Pixel Box Swatch Preview
                        Box(
                            modifier = Modifier
                                .size(46.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(Color(skin.primaryColorHex))
                                .border(2.dp, Color(skin.secondaryColorHex), RoundedCornerShape(8.dp)),
                            contentAlignment = Alignment.Center
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(16.dp)
                                    .clip(RoundedCornerShape(3.dp))
                                    .background(Color(skin.visorColorHex))
                            )
                        }

                        Spacer(modifier = Modifier.height(6.dp))

                        Text(
                            text = skin.displayName,
                            color = Color(skin.primaryColorHex),
                            fontWeight = FontWeight.Black,
                            fontSize = 11.sp,
                            fontFamily = FontFamily.Monospace,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            textAlign = TextAlign.Center
                        )

                        Text(
                            text = skin.title,
                            color = RetroTextSecondary,
                            fontSize = 9.sp,
                            fontFamily = FontFamily.Monospace,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            textAlign = TextAlign.Center
                        )

                        Spacer(modifier = Modifier.height(6.dp))

                        // Box Action Button
                        if (isEquipped) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(PixelEmerald.copy(alpha = 0.2f))
                                    .padding(vertical = 4.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "USED",
                                    color = PixelEmerald,
                                    fontWeight = FontWeight.Black,
                                    fontSize = 10.sp,
                                    fontFamily = FontFamily.Monospace
                                )
                            }
                        } else if (isOwned) {
                            Button(
                                onClick = { onEquipSkin(skin) },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(28.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = DungeonSurface,
                                    contentColor = PixelGold
                                ),
                                border = BorderStroke(1.dp, PixelGold),
                                shape = RoundedCornerShape(6.dp),
                                contentPadding = PaddingValues(horizontal = 2.dp, vertical = 0.dp)
                            ) {
                                Text(
                                    text = "EQUIP",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    fontFamily = FontFamily.Monospace
                                )
                            }
                        } else {
                            Button(
                                onClick = { onBuySkin(skin) },
                                enabled = coins >= skin.costCoins,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(28.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = PixelGold,
                                    contentColor = Color(0xFF1E1400)
                                ),
                                shape = RoundedCornerShape(6.dp),
                                contentPadding = PaddingValues(horizontal = 2.dp, vertical = 0.dp)
                            ) {
                                Text(
                                    text = "${skin.costCoins}🪙",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Black,
                                    fontFamily = FontFamily.Monospace
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

// ================= TAB 2: BRICKS & BACKGROUNDS (3x2 BOX GRID) =================
@Composable
private fun BricksTabContent(
    selectedTheme: DungeonTheme,
    unlockedThemes: Set<String>,
    coins: Int,
    onSelectTheme: (DungeonTheme) -> Unit,
    onEquipTheme: (DungeonTheme) -> Unit,
    onBuyTheme: (DungeonTheme) -> Unit
) {
    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        // Banner Current Theme Preview
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(14.dp),
            colors = CardDefaults.cardColors(
                containerColor = Color(selectedTheme.boardBgColorHex)
            ),
            border = BorderStroke(1.5.dp, Color(selectedTheme.wallHighlightHex))
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Live sample tile
                Box(
                    modifier = Modifier
                        .size(54.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .border(1.5.dp, Color(selectedTheme.wallHighlightHex), RoundedCornerShape(8.dp))
                ) {
                    Canvas(modifier = Modifier.fillMaxSize()) {
                        val s = size.width / 2f
                        PixelSprites.drawWall(
                            scope = this,
                            x = 0f,
                            y = 0f,
                            tileSize = s,
                            darkStone = Color(selectedTheme.wallDarkColorHex),
                            midStone = Color(selectedTheme.wallMidColorHex),
                            lightStone = Color(selectedTheme.wallLightColorHex),
                            highlight = Color(selectedTheme.wallHighlightHex)
                        )
                        PixelSprites.drawFloor(
                            scope = this,
                            x = s,
                            y = 0f,
                            tileSize = s,
                            floorBg = Color(selectedTheme.floorBgColorHex),
                            tileAccent = Color(selectedTheme.floorAccentColorHex)
                        )
                        PixelSprites.drawFloor(
                            scope = this,
                            x = 0f,
                            y = s,
                            tileSize = s,
                            floorBg = Color(selectedTheme.floorBgColorHex),
                            tileAccent = Color(selectedTheme.floorAccentColorHex)
                        )
                        PixelSprites.drawWall(
                            scope = this,
                            x = s,
                            y = s,
                            tileSize = s,
                            darkStone = Color(selectedTheme.wallDarkColorHex),
                            midStone = Color(selectedTheme.wallMidColorHex),
                            lightStone = Color(selectedTheme.wallLightColorHex),
                            highlight = Color(selectedTheme.wallHighlightHex)
                        )
                    }
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "CURRENT DUNGEON BRICK",
                            color = PixelEmerald,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Monospace,
                            letterSpacing = 1.sp
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Icon(
                            imageVector = Icons.Default.Check,
                            contentDescription = null,
                            tint = PixelEmerald,
                            modifier = Modifier.size(14.dp)
                        )
                    }
                    Text(
                        text = selectedTheme.displayName,
                        color = Color(selectedTheme.wallHighlightHex),
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Black,
                        fontFamily = FontFamily.Monospace
                    )
                    Text(
                        text = selectedTheme.title,
                        color = RetroTextSecondary,
                        fontSize = 11.sp
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "BRICKS & BACKGROUNDS (3x2 BOX GRID)",
                color = PixelCyan,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = FontFamily.Monospace,
                letterSpacing = 1.sp
            )
            Text(
                text = "${unlockedThemes.size}/${DungeonTheme.entries.size} UNLOCKED",
                color = PixelGold,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = FontFamily.Monospace
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        // 3 items per row, 2 rows (6 total themes) in Box System
        LazyVerticalGrid(
            columns = GridCells.Fixed(3),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.weight(1f),
            contentPadding = PaddingValues(bottom = 16.dp)
        ) {
            items(DungeonTheme.entries) { theme ->
                val isOwned = unlockedThemes.contains(theme.id)
                val isEquipped = selectedTheme.id == theme.id

                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .clickable { onSelectTheme(theme) },
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = if (isEquipped) Color(0xFF1E2337) else DungeonCard
                    ),
                    border = BorderStroke(
                        if (isEquipped) 2.dp else 1.dp,
                        if (isEquipped) Color(theme.wallHighlightHex) else DungeonBorder
                    )
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(8.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        // Badge at top of Box
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(16.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            if (isEquipped) {
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(3.dp))
                                        .background(PixelEmerald)
                                        .padding(horizontal = 4.dp, vertical = 1.dp)
                                ) {
                                    Text(
                                        text = "ACTIVE",
                                        color = Color.Black,
                                        fontSize = 8.sp,
                                        fontWeight = FontWeight.Black,
                                        fontFamily = FontFamily.Monospace
                                    )
                                }
                            } else if (isOwned) {
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(3.dp))
                                        .background(PixelCyan.copy(alpha = 0.2f))
                                        .border(0.5.dp, PixelCyan, RoundedCornerShape(3.dp))
                                        .padding(horizontal = 4.dp, vertical = 1.dp)
                                ) {
                                    Text(
                                        text = "OWNED",
                                        color = PixelCyan,
                                        fontSize = 8.sp,
                                        fontWeight = FontWeight.Bold,
                                        fontFamily = FontFamily.Monospace
                                    )
                                }
                            } else {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Lock,
                                        contentDescription = null,
                                        tint = RetroTextSecondary,
                                        modifier = Modifier.size(10.dp)
                                    )
                                    Spacer(modifier = Modifier.width(2.dp))
                                    Text(
                                        text = "${theme.costCoins}🪙",
                                        color = PixelGold,
                                        fontSize = 8.sp,
                                        fontWeight = FontWeight.Bold,
                                        fontFamily = FontFamily.Monospace
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(4.dp))

                        // Mini Brick Canvas Swatch
                        Box(
                            modifier = Modifier
                                .size(46.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .border(1.5.dp, Color(theme.wallHighlightHex), RoundedCornerShape(8.dp))
                        ) {
                            Canvas(modifier = Modifier.fillMaxSize()) {
                                val s = size.width / 2f
                                PixelSprites.drawWall(
                                    scope = this,
                                    x = 0f,
                                    y = 0f,
                                    tileSize = s,
                                    darkStone = Color(theme.wallDarkColorHex),
                                    midStone = Color(theme.wallMidColorHex),
                                    lightStone = Color(theme.wallLightColorHex),
                                    highlight = Color(theme.wallHighlightHex)
                                )
                                PixelSprites.drawFloor(
                                    scope = this,
                                    x = s,
                                    y = 0f,
                                    tileSize = s,
                                    floorBg = Color(theme.floorBgColorHex),
                                    tileAccent = Color(theme.floorAccentColorHex)
                                )
                                PixelSprites.drawFloor(
                                    scope = this,
                                    x = 0f,
                                    y = s,
                                    tileSize = s,
                                    floorBg = Color(theme.floorBgColorHex),
                                    tileAccent = Color(theme.floorAccentColorHex)
                                )
                                PixelSprites.drawWall(
                                    scope = this,
                                    x = s,
                                    y = s,
                                    tileSize = s,
                                    darkStone = Color(theme.wallDarkColorHex),
                                    midStone = Color(theme.wallMidColorHex),
                                    lightStone = Color(theme.wallLightColorHex),
                                    highlight = Color(theme.wallHighlightHex)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(6.dp))

                        Text(
                            text = theme.displayName,
                            color = Color(theme.wallHighlightHex),
                            fontWeight = FontWeight.Black,
                            fontSize = 11.sp,
                            fontFamily = FontFamily.Monospace,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            textAlign = TextAlign.Center
                        )

                        Text(
                            text = theme.title,
                            color = RetroTextSecondary,
                            fontSize = 9.sp,
                            fontFamily = FontFamily.Monospace,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            textAlign = TextAlign.Center
                        )

                        Spacer(modifier = Modifier.height(6.dp))

                        // Box Action Button
                        if (isEquipped) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(PixelEmerald.copy(alpha = 0.2f))
                                    .padding(vertical = 4.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "USED",
                                    color = PixelEmerald,
                                    fontWeight = FontWeight.Black,
                                    fontSize = 10.sp,
                                    fontFamily = FontFamily.Monospace
                                )
                            }
                        } else if (isOwned) {
                            Button(
                                onClick = { onEquipTheme(theme) },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(28.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = DungeonSurface,
                                    contentColor = PixelGold
                                ),
                                border = BorderStroke(1.dp, PixelGold),
                                shape = RoundedCornerShape(6.dp),
                                contentPadding = PaddingValues(horizontal = 2.dp, vertical = 0.dp)
                            ) {
                                Text(
                                    text = "EQUIP",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    fontFamily = FontFamily.Monospace
                                )
                            }
                        } else {
                            Button(
                                onClick = { onBuyTheme(theme) },
                                enabled = coins >= theme.costCoins,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(28.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = PixelGold,
                                    contentColor = Color(0xFF1E1400)
                                ),
                                shape = RoundedCornerShape(6.dp),
                                contentPadding = PaddingValues(horizontal = 2.dp, vertical = 0.dp)
                            ) {
                                Text(
                                    text = "${theme.costCoins}🪙",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Black,
                                    fontFamily = FontFamily.Monospace
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

// ================= TAB 3: SUPPLIES (HINTS & PACKAGES) =================
@Composable
private fun SuppliesTabContent(
    coins: Int,
    hints: Int,
    onBuyHints: () -> Unit,
    onWatchAdForHints: () -> Unit
) {
    LazyColumn(
        verticalArrangement = Arrangement.spacedBy(12.dp),
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(bottom = 20.dp)
    ) {
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF132338)),
                border = BorderStroke(1.dp, PixelCyan.copy(alpha = 0.6f))
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "PUZZLE GUIDANCE SCROLLS",
                            color = PixelCyan,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Monospace
                        )
                        Text(
                            text = "Currently Available: $hints Hints",
                            color = RetroTextPrimary,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Black,
                            fontFamily = FontFamily.Monospace
                        )
                        Text(
                            text = "Never get stuck on difficult crypt puzzles. Highlights optimal crate placements.",
                            color = RetroTextSecondary,
                            fontSize = 11.sp
                        )
                    }
                    Text(text = "💡", fontSize = 32.sp)
                }
            }
        }

        item {
            Text(
                text = "BUY SUPPLIES WITH COINS",
                color = PixelGold,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = FontFamily.Monospace,
                letterSpacing = 1.sp
            )
        }

        // 3x Hints Pack
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = DungeonCard),
                border = BorderStroke(1.dp, DungeonBorder)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(text = "📜", fontSize = 24.sp)
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = "3X HINT SCROLLS",
                                color = RetroTextPrimary,
                                fontWeight = FontWeight.Black,
                                fontSize = 14.sp,
                                fontFamily = FontFamily.Monospace
                            )
                            Text(
                                text = "Instant step-by-step puzzle hints",
                                color = RetroTextSecondary,
                                fontSize = 11.sp
                            )
                        }
                    }

                    Button(
                        onClick = onBuyHints,
                        enabled = coins >= 10,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = PixelGold,
                            contentColor = Color(0xFF1E1400)
                        ),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text(
                            text = "10 🪙",
                            fontWeight = FontWeight.Black,
                            fontSize = 12.sp,
                            fontFamily = FontFamily.Monospace
                        )
                    }
                }
            }
        }

        // Free Hint via Video Ad
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = DungeonCard),
                border = BorderStroke(1.dp, PixelCyan.copy(alpha = 0.5f))
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(text = "📺", fontSize = 24.sp)
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = "WATCH VIDEO FOR HINTS",
                                color = PixelCyan,
                                fontWeight = FontWeight.Black,
                                fontSize = 14.sp,
                                fontFamily = FontFamily.Monospace
                            )
                            Text(
                                text = "Earn +2 Free Hints instantly",
                                color = RetroTextSecondary,
                                fontSize = 11.sp
                            )
                        }
                    }

                    Button(
                        onClick = onWatchAdForHints,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = PixelCyan,
                            contentColor = Color(0xFF002233)
                        ),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text(
                            text = "+2 💡 FREE",
                            fontWeight = FontWeight.Black,
                            fontSize = 11.sp,
                            fontFamily = FontFamily.Monospace
                        )
                    }
                }
            }
        }
    }
}

// ================= TAB 4: ADS & REWARDS =================
@Composable
private fun AdsAndRewardsTabContent(
    isDailyClaimable: Boolean,
    isFreeSpinAvailable: Boolean,
    onOpenDailyGift: () -> Unit,
    onOpenLuckySpin: () -> Unit,
    onWatchAdForCoins: () -> Unit,
    onWatchAdForHints: () -> Unit
) {
    LazyColumn(
        verticalArrangement = Arrangement.spacedBy(12.dp),
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(bottom = 20.dp)
    ) {
        // Daily Login Gift Card
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = if (isDailyClaimable) Color(0xFF332002) else DungeonCard),
                border = BorderStroke(1.5.dp, if (isDailyClaimable) PixelGold else DungeonBorder)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(44.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(PixelGold.copy(alpha = 0.2f))
                                .border(1.dp, PixelGold, RoundedCornerShape(8.dp)),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(text = "🎁", fontSize = 22.sp)
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = "DAILY LOGIN VAULT",
                                color = PixelGold,
                                fontWeight = FontWeight.Black,
                                fontSize = 14.sp,
                                fontFamily = FontFamily.Monospace
                            )
                            Text(
                                text = if (isDailyClaimable) "Gift ready to claim! 🔴" else "Claimed today • Resets tomorrow",
                                color = if (isDailyClaimable) PixelAmber else RetroTextSecondary,
                                fontWeight = FontWeight.Bold,
                                fontSize = 11.sp,
                                fontFamily = FontFamily.Monospace
                            )
                        }
                    }

                    Button(
                        onClick = onOpenDailyGift,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = PixelGold,
                            contentColor = Color(0xFF1E1400)
                        ),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text(
                            text = if (isDailyClaimable) "CLAIM 🎁" else "VIEW 🎁",
                            fontWeight = FontWeight.Black,
                            fontSize = 11.sp,
                            fontFamily = FontFamily.Monospace
                        )
                    }
                }
            }
        }

        // Lucky Spin Wheel Card
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = if (isFreeSpinAvailable) Color(0xFF0F2618) else DungeonCard),
                border = BorderStroke(1.5.dp, if (isFreeSpinAvailable) PixelEmerald else DungeonBorder)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(44.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(PixelEmerald.copy(alpha = 0.2f))
                                .border(1.dp, PixelEmerald, RoundedCornerShape(8.dp)),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(text = "🎡", fontSize = 22.sp)
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = "LUCKY FORTUNE WHEEL",
                                color = PixelEmerald,
                                fontWeight = FontWeight.Black,
                                fontSize = 14.sp,
                                fontFamily = FontFamily.Monospace
                            )
                            Text(
                                text = if (isFreeSpinAvailable) "Free daily spin ready! 🔴" else "Spin with video ad for prizes",
                                color = if (isFreeSpinAvailable) PixelEmerald else RetroTextSecondary,
                                fontWeight = FontWeight.Bold,
                                fontSize = 11.sp,
                                fontFamily = FontFamily.Monospace
                            )
                        }
                    }

                    Button(
                        onClick = onOpenLuckySpin,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = PixelEmerald,
                            contentColor = Color(0xFF002410)
                        ),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text(
                            text = "SPIN 🎡",
                            fontWeight = FontWeight.Black,
                            fontSize = 11.sp,
                            fontFamily = FontFamily.Monospace
                        )
                    }
                }
            }
        }

        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF2B2005)),
                border = BorderStroke(1.dp, PixelAmber)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(text = "🎬", fontSize = 24.sp)
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = "VIDEO REWARD CHESTS",
                                color = PixelAmber,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Black,
                                fontFamily = FontFamily.Monospace
                            )
                            Text(
                                text = "Support ALIF • PIXEL CRYPT by watching quick video ads and claim unlimited rewards!",
                                color = RetroTextSecondary,
                                fontSize = 11.sp
                            )
                        }
                    }
                }
            }
        }

        // Watch Ad for 75 Coins
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = DungeonCard),
                border = BorderStroke(1.dp, DungeonBorder)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(44.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(PixelGold.copy(alpha = 0.15f))
                                .border(1.dp, PixelGold, RoundedCornerShape(8.dp)),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(text = "🪙", fontSize = 20.sp)
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = "REWARD VIDEO CHEST",
                                color = RetroTextPrimary,
                                fontWeight = FontWeight.Black,
                                fontSize = 14.sp,
                                fontFamily = FontFamily.Monospace
                            )
                            Text(
                                text = "Instant +5 Gold Coins",
                                color = PixelGold,
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp,
                                fontFamily = FontFamily.Monospace
                            )
                        }
                    }

                    Button(
                        onClick = onWatchAdForCoins,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = PixelAmber,
                            contentColor = Color(0xFF1E1400)
                        ),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text(
                            text = "CLAIM +5 🪙",
                            fontWeight = FontWeight.Black,
                            fontSize = 11.sp,
                            fontFamily = FontFamily.Monospace
                        )
                    }
                }
            }
        }

        // Watch Ad for 2 Hints
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = DungeonCard),
                border = BorderStroke(1.dp, DungeonBorder)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(44.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(PixelCyan.copy(alpha = 0.15f))
                                .border(1.dp, PixelCyan, RoundedCornerShape(8.dp)),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(text = "💡", fontSize = 20.sp)
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = "ORACLE VIDEO VISION",
                                color = RetroTextPrimary,
                                fontWeight = FontWeight.Black,
                                fontSize = 14.sp,
                                fontFamily = FontFamily.Monospace
                            )
                            Text(
                                text = "Instant +2 Puzzle Hints",
                                color = PixelCyan,
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp,
                                fontFamily = FontFamily.Monospace
                            )
                        }
                    }

                    Button(
                        onClick = onWatchAdForHints,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = PixelCyan,
                            contentColor = Color(0xFF002233)
                        ),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text(
                            text = "CLAIM +2 💡",
                            fontWeight = FontWeight.Black,
                            fontSize = 11.sp,
                            fontFamily = FontFamily.Monospace
                        )
                    }
                }
            }
        }
    }
}

// 16-Bit Pixel Art Preview Box for Knight
@Composable
private fun PixelHeroBigPreview(
    primaryColor: Color,
    secondaryColor: Color,
    visorColor: Color,
    plumeColor: Color,
    modifier: Modifier = Modifier
) {
    Canvas(
        modifier = modifier
            .clip(RoundedCornerShape(8.dp))
            .background(Color(0xFF0F121C))
            .border(1.5.dp, primaryColor, RoundedCornerShape(8.dp))
    ) {
        val w = size.width
        val h = size.height
        val unit = w / 12f

        // Plume (top)
        drawRect(plumeColor, Offset(5 * unit, 1 * unit), Size(2 * unit, 2 * unit))
        drawRect(plumeColor, Offset(4 * unit, 2 * unit), Size(4 * unit, 1 * unit))

        // Helmet
        drawRect(primaryColor, Offset(3 * unit, 3 * unit), Size(6 * unit, 4 * unit))
        // Visor slit
        drawRect(visorColor, Offset(4 * unit, 4.5f * unit), Size(4 * unit, 1.2f * unit))

        // Armor body
        drawRect(secondaryColor, Offset(2.5f * unit, 7 * unit), Size(7 * unit, 3 * unit))
        drawRect(primaryColor, Offset(4 * unit, 7 * unit), Size(4 * unit, 3 * unit))

        // Boots
        drawRect(secondaryColor, Offset(3 * unit, 10 * unit), Size(2.5f * unit, 1.5f * unit))
        drawRect(secondaryColor, Offset(6.5f * unit, 10 * unit), Size(2.5f * unit, 1.5f * unit))
    }
}
