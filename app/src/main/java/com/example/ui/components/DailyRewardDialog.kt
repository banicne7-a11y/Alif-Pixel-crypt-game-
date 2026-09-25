package com.example.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.ui.theme.*

@Composable
fun DailyRewardDialog(
    currentStreak: Int,
    isClaimable: Boolean,
    onClaim: () -> Pair<Int, Int>,
    onDismiss: () -> Unit
) {
    var claimedReward by remember { mutableStateOf<Pair<Int, Int>?>(null) }

    val daysRewards = listOf(
        Triple(1, "1 🪙", "Day 1"),
        Triple(2, "2 🪙", "Day 2"),
        Triple(3, "1 💡", "Day 3"),
        Triple(4, "3 🪙", "Day 4"),
        Triple(5, "2 💡", "Day 5"),
        Triple(6, "4 🪙", "Day 6"),
        Triple(7, "5 🪙+2💡", "Day 7")
    )

    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(4.dp)
                .testTag("daily_reward_dialog"),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = DungeonSurface),
            border = BorderStroke(2.dp, PixelGold)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(18.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(text = "🎁", fontSize = 24.sp)
                        Spacer(modifier = Modifier.width(8.dp))
                        Column {
                            Text(
                                text = "DAILY LOGIN VAULT",
                                color = PixelGold,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Black,
                                fontFamily = FontFamily.Monospace
                            )
                            Text(
                                text = "Streak: $currentStreak / 7 Days",
                                color = PixelCyan,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
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

                Spacer(modifier = Modifier.height(14.dp))

                // Days Row 1..6 (2 rows of 3) + Day 7 Big Card
                Column(
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    // Row 1: Days 1, 2, 3
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        for (i in 0..2) {
                            val (dayNum, rewardText, dayLabel) = daysRewards[i]
                            val isClaimed = dayNum < currentStreak || (!isClaimable && dayNum <= currentStreak)
                            val isToday = dayNum == currentStreak && isClaimable

                            DayCard(
                                dayLabel = dayLabel,
                                rewardText = rewardText,
                                isClaimed = isClaimed,
                                isToday = isToday,
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }

                    // Row 2: Days 4, 5, 6
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        for (i in 3..5) {
                            val (dayNum, rewardText, dayLabel) = daysRewards[i]
                            val isClaimed = dayNum < currentStreak || (!isClaimable && dayNum <= currentStreak)
                            val isToday = dayNum == currentStreak && isClaimable

                            DayCard(
                                dayLabel = dayLabel,
                                rewardText = rewardText,
                                isClaimed = isClaimed,
                                isToday = isToday,
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }

                    // Day 7: Grand Vault Mystery Card
                    val isDay7Claimed = 7 < currentStreak || (!isClaimable && 7 <= currentStreak)
                    val isDay7Today = 7 == currentStreak && isClaimable
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(10.dp))
                            .background(
                                Brush.horizontalGradient(
                                    colors = if (isDay7Today) listOf(Color(0xFF4A2800), Color(0xFF784500))
                                    else listOf(Color(0xFF1E2337), Color(0xFF28233C))
                                )
                            )
                            .border(
                                BorderStroke(
                                    if (isDay7Today) 2.dp else 1.dp,
                                    if (isDay7Today) PixelGold else DungeonBorder
                                ),
                                RoundedCornerShape(10.dp)
                            )
                            .padding(horizontal = 14.dp, vertical = 10.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(text = "👑", fontSize = 22.sp)
                                Spacer(modifier = Modifier.width(10.dp))
                                Column {
                                    Text(
                                        text = "DAY 7: GRAND CRYPT CHEST",
                                        color = PixelGold,
                                        fontWeight = FontWeight.Black,
                                        fontSize = 12.sp,
                                        fontFamily = FontFamily.Monospace
                                    )
                                    Text(
                                        text = "5 Gold Coins + 2 Free Hints",
                                        color = PixelAmber,
                                        fontSize = 11.sp,
                                        fontFamily = FontFamily.Monospace
                                    )
                                }
                            }

                            if (isDay7Claimed) {
                                Text(
                                    text = "CLAIMED ✓",
                                    color = PixelEmerald,
                                    fontWeight = FontWeight.Black,
                                    fontSize = 11.sp,
                                    fontFamily = FontFamily.Monospace
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                if (claimedReward != null) {
                    val (c, h) = claimedReward!!
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp))
                            .background(PixelEmerald.copy(alpha = 0.2f))
                            .border(BorderStroke(1.dp, PixelEmerald), RoundedCornerShape(8.dp))
                            .padding(10.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "CLAIMED! ${if (c > 0) "+$c 🪙 " else ""}${if (h > 0) "+$h 💡" else ""}",
                            color = PixelEmerald,
                            fontWeight = FontWeight.Black,
                            fontSize = 13.sp,
                            fontFamily = FontFamily.Monospace
                        )
                    }
                } else {
                    Button(
                        onClick = {
                            if (isClaimable) {
                                claimedReward = onClaim()
                            }
                        },
                        enabled = isClaimable,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                            .testTag("claim_daily_button"),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = PixelGold,
                            contentColor = Color(0xFF1E1400),
                            disabledContainerColor = Color(0xFF22273D),
                            disabledContentColor = RetroTextDisabled
                        ),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Text(
                            text = if (isClaimable) "CLAIM TODAY'S GIFT 🎁" else "COME BACK TOMORROW ⏳",
                            fontWeight = FontWeight.Black,
                            fontSize = 13.sp,
                            fontFamily = FontFamily.Monospace
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun DayCard(
    dayLabel: String,
    rewardText: String,
    isClaimed: Boolean,
    isToday: Boolean,
    modifier: Modifier = Modifier
) {
    val borderColor = when {
        isToday -> PixelGold
        isClaimed -> PixelEmerald
        else -> DungeonBorder
    }

    val bgColor = when {
        isToday -> Color(0xFF33260A)
        isClaimed -> Color(0xFF0F2618)
        else -> DungeonCard
    }

    Box(
        modifier = modifier
            .clip(RoundedCornerShape(8.dp))
            .background(bgColor)
            .border(BorderStroke(if (isToday) 2.dp else 1.dp, borderColor), RoundedCornerShape(8.dp))
            .padding(vertical = 10.dp, horizontal = 4.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = dayLabel,
                color = if (isToday) PixelGold else RetroTextSecondary,
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = FontFamily.Monospace
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = rewardText,
                color = if (isClaimed) PixelEmerald else RetroTextPrimary,
                fontSize = 12.sp,
                fontWeight = FontWeight.Black,
                fontFamily = FontFamily.Monospace
            )
            if (isClaimed) {
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = "✓",
                    color = PixelEmerald,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Black
                )
            }
        }
    }
}
