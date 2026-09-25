package com.example.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.model.Achievement
import com.example.model.AchievementList
import com.example.ui.theme.*

@Composable
fun AchievementsDialog(
    claimedIds: Set<String>,
    totalLevelsCleared: Int,
    totalStars: Int,
    totalRelics: Int,
    currentHints: Int,
    currentCoins: Int,
    onClaim: (id: String, coins: Int, hints: Int) -> Boolean,
    onDismiss: () -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.85f)
                .padding(4.dp)
                .testTag("achievements_dialog"),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = DungeonSurface),
            border = BorderStroke(2.dp, PixelGold)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(text = "🏆", fontSize = 24.sp)
                        Spacer(modifier = Modifier.width(8.dp))
                        Column {
                            Text(
                                text = "TROPHY HALL",
                                color = PixelGold,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Black,
                                fontFamily = FontFamily.Monospace
                            )
                            Text(
                                text = "Earn coins & glory for heroic feats",
                                color = PixelCyan,
                                fontSize = 11.sp,
                                fontFamily = FontFamily.Monospace
                            )
                        }
                    }

                    IconButton(onClick = onDismiss, modifier = Modifier.size(32.dp)) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Close",
                            tint = RetroTextSecondary
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                    modifier = Modifier.fillMaxSize()
                ) {
                    items(AchievementList.allAchievements) { ach ->
                        val currentVal = when (ach.id) {
                            "first_step" -> totalLevelsCleared
                            "explorer_10" -> totalLevelsCleared
                            "master_50" -> totalLevelsCleared
                            "star_30" -> totalStars
                            "star_100" -> totalStars
                            "relic_finder" -> totalRelics
                            "hint_collector" -> currentHints
                            "coin_hoarder" -> currentCoins
                            else -> 0
                        }

                        val isCompleted = currentVal >= ach.target
                        val isClaimed = claimedIds.contains(ach.id)

                        AchievementRow(
                            achievement = ach,
                            currentVal = currentVal,
                            isCompleted = isCompleted,
                            isClaimed = isClaimed,
                            onClaim = { onClaim(ach.id, ach.rewardCoins, ach.rewardHints) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun AchievementRow(
    achievement: Achievement,
    currentVal: Int,
    isCompleted: Boolean,
    isClaimed: Boolean,
    onClaim: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(10.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isClaimed) Color(0xFF0F1E19) else DungeonCard
        ),
        border = BorderStroke(
            1.dp,
            when {
                isClaimed -> PixelEmerald.copy(alpha = 0.5f)
                isCompleted -> PixelGold
                else -> DungeonBorder
            }
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Icon box
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(
                        when {
                            isClaimed -> PixelEmerald.copy(alpha = 0.2f)
                            isCompleted -> PixelGold.copy(alpha = 0.2f)
                            else -> DungeonSurface
                        }
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(text = achievement.icon, fontSize = 20.sp)
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = achievement.title,
                    color = if (isClaimed) PixelEmerald else PixelGold,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.Monospace
                )
                Text(
                    text = achievement.description,
                    color = RetroTextSecondary,
                    fontSize = 11.sp,
                    fontFamily = FontFamily.Monospace
                )

                Spacer(modifier = Modifier.height(6.dp))

                // Progress Bar
                val progressFraction = (currentVal.toFloat() / achievement.target.toFloat()).coerceIn(0f, 1f)
                LinearProgressIndicator(
                    progress = { progressFraction },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(6.dp)
                        .clip(RoundedCornerShape(3.dp)),
                    color = if (isCompleted) PixelEmerald else PixelCyan,
                    trackColor = DungeonSurface
                )

                Spacer(modifier = Modifier.height(3.dp))
                Text(
                    text = "${minOf(currentVal, achievement.target)} / ${achievement.target}",
                    color = RetroTextDisabled,
                    fontSize = 9.sp,
                    fontFamily = FontFamily.Monospace
                )
            }

            Spacer(modifier = Modifier.width(10.dp))

            // Action or Status
            when {
                isClaimed -> {
                    Text(
                        text = "DONE ✓",
                        color = PixelEmerald,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Black,
                        fontFamily = FontFamily.Monospace
                    )
                }
                isCompleted -> {
                    Button(
                        onClick = onClaim,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = PixelGold,
                            contentColor = Color(0xFF1E1400)
                        ),
                        shape = RoundedCornerShape(8.dp),
                        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp)
                    ) {
                        Text(
                            text = "+${achievement.rewardCoins}🪙",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Black,
                            fontFamily = FontFamily.Monospace
                        )
                    }
                }
                else -> {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(DungeonSurface)
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = "+${achievement.rewardCoins}🪙",
                            color = RetroTextDisabled,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Monospace
                        )
                    }
                }
            }
        }
    }
}
