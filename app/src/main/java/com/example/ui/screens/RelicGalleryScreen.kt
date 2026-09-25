package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.model.RelicRegistry
import com.example.ui.GameViewModel
import com.example.ui.ScreenState
import com.example.ui.theme.DungeonBorder
import com.example.ui.theme.DungeonCard
import com.example.ui.theme.DungeonDarkBg
import com.example.ui.theme.DungeonSurface
import com.example.ui.theme.PixelCyan
import com.example.ui.theme.PixelGold
import com.example.ui.theme.PixelRuby
import com.example.ui.theme.RetroTextPrimary
import com.example.ui.theme.RetroTextSecondary

@Composable
fun RelicGalleryScreen(
    viewModel: GameViewModel,
    modifier: Modifier = Modifier
) {
    val unlockedRelics by viewModel.unlockedRelics.collectAsStateWithLifecycle()
    val unlockedIds = unlockedRelics.map { it.id }.toSet()

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
                .padding(horizontal = 16.dp, vertical = 8.dp)
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(
                        onClick = { viewModel.navigateTo(ScreenState.MENU) },
                        modifier = Modifier.testTag("relic_gallery_back_button")
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = PixelGold
                        )
                    }
                    Column {
                        Text(
                            text = "RELIC VAULT",
                            color = PixelGold,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Black,
                            fontFamily = FontFamily.Monospace,
                            letterSpacing = 1.sp
                        )
                        Text(
                            text = "CURATOR: ALIF",
                            color = PixelCyan,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Monospace
                        )
                    }
                }

                Text(
                    text = "${unlockedIds.size} / ${RelicRegistry.ALL_RELICS.size} FOUND",
                    color = PixelCyan,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.Monospace
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Ancient artifacts recovered from secret crypt chests throughout the four realms.",
                color = RetroTextSecondary,
                fontSize = 12.sp,
                lineHeight = 16.sp
            )

            Spacer(modifier = Modifier.height(14.dp))

            LazyColumn(
                contentPadding = PaddingValues(bottom = 16.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                items(RelicRegistry.ALL_RELICS) { relic ->
                    val isUnlocked = unlockedIds.contains(relic.id)

                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(10.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = if (isUnlocked) DungeonCard else DungeonSurface
                        ),
                        border = BorderStroke(
                            1.dp,
                            if (isUnlocked) PixelGold.copy(alpha = 0.7f) else DungeonBorder
                        )
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(14.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            // Icon Box
                            Box(
                                modifier = Modifier
                                    .size(54.dp)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(if (isUnlocked) Color(0xFF2A2000) else Color(0xFF0F111A))
                                    .border(
                                        BorderStroke(
                                            1.dp,
                                            if (isUnlocked) PixelGold else DungeonBorder
                                        ),
                                        RoundedCornerShape(8.dp)
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                if (isUnlocked) {
                                    val emoji = when (relic.iconPixelType) {
                                        "ANKH" -> "☥"
                                        "SCARAB" -> "🪲"
                                        "CRYSTAL" -> "💎"
                                        "CROWN" -> "👑"
                                        "PRISM" -> "🔺"
                                        "CHALICE" -> "🏆"
                                        "COMPASS" -> "🧭"
                                        "BOOK" -> "📜"
                                        else -> "🏺"
                                    }
                                    Text(
                                        text = emoji,
                                        fontSize = 24.sp
                                    )
                                } else {
                                    Icon(
                                        imageVector = Icons.Default.Lock,
                                        contentDescription = "Locked",
                                        tint = RetroTextSecondary.copy(alpha = 0.5f),
                                        modifier = Modifier.size(22.dp)
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.width(14.dp))

                            Column(modifier = Modifier.weight(1f)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = if (isUnlocked) relic.name else "Sealed Crypt Artifact",
                                        color = if (isUnlocked) PixelGold else RetroTextSecondary,
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.Bold,
                                        fontFamily = FontFamily.Monospace
                                    )
                                    Text(
                                        text = "World ${relic.worldId}",
                                        color = PixelCyan,
                                        fontSize = 10.sp,
                                        fontFamily = FontFamily.Monospace
                                    )
                                }

                                Spacer(modifier = Modifier.height(4.dp))

                                Text(
                                    text = if (isUnlocked) relic.lore else "Explore World ${relic.worldId} catacombs and open the hidden treasure chest to uncover this relic.",
                                    color = if (isUnlocked) RetroTextPrimary else RetroTextSecondary.copy(alpha = 0.7f),
                                    fontSize = 11.sp,
                                    lineHeight = 15.sp
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
