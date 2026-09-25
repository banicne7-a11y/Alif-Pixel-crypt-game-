package com.example.ui.components

import android.content.Context
import androidx.compose.animation.core.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.ads.AdMobManager
import com.example.ui.theme.*
import kotlinx.coroutines.launch
import kotlin.math.cos
import kotlin.math.sin

@Composable
fun LuckySpinDialog(
    isFreeSpinAvailable: Boolean,
    onSpin: (isRewardedAd: Boolean, onResult: (sectorIndex: Int, coins: Int, hints: Int, desc: String) -> Unit) -> Unit,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    var isSpinning by remember { mutableStateOf(false) }
    var currentRotation by remember { mutableFloatStateOf(0f) }
    var wonResultText by remember { mutableStateOf<String?>(null) }

    val sectorLabels = listOf(
        "+1 🪙",
        "+2 🪙",
        "+1 💡",
        "+3 🪙",
        "+5 🪙",
        "+2 💡"
    )

    val sectorColors = listOf(
        PixelGoldDark,
        Color(0xFF0D5E6B),
        Color(0xFF4C1D95),
        PixelAmber,
        Color(0xFF047857),
        Color(0xFF1D4ED8)
    )

    fun startSpinSequence(isRewarded: Boolean) {
        if (isSpinning) return
        isSpinning = true
        wonResultText = null

        onSpin(isRewarded) { targetSector, _, _, desc ->
            // Wheel has 6 sectors (60 degrees each)
            // Needle points at 270 degrees (top).
            // Sector 0 is center at 30 deg, sector 1 at 90 deg, etc.
            val sectorAngle = 360f / 6f
            val targetCenter = (targetSector * sectorAngle) + (sectorAngle / 2f)
            // We want (currentRotation + totalRotations) % 360 to align targetCenter with 270 (top)
            val desiredAngle = (270f - targetCenter + 360f) % 360f
            val spins = 5 * 360f // 5 full rotations
            val finalTargetRotation = currentRotation + spins + ((desiredAngle - (currentRotation % 360f) + 360f) % 360f)

            coroutineScope.launch {
                val anim = Animatable(currentRotation)
                anim.animateTo(
                    targetValue = finalTargetRotation,
                    animationSpec = tween(
                        durationMillis = 3200,
                        easing = FastOutSlowInEasing
                    )
                ) {
                    currentRotation = value
                }
                isSpinning = false
                wonResultText = desc
            }
        }
    }

    Dialog(onDismissRequest = { if (!isSpinning) onDismiss() }) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(4.dp)
                .testTag("lucky_spin_dialog"),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = DungeonSurface),
            border = BorderStroke(2.dp, PixelGold)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(text = "🎡", fontSize = 24.sp)
                        Spacer(modifier = Modifier.width(8.dp))
                        Column {
                            Text(
                                text = "LUCKY CRYPT WHEEL",
                                color = PixelGold,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Black,
                                fontFamily = FontFamily.Monospace
                            )
                            Text(
                                text = "Spin to win Gold Coins & Free Hints",
                                color = PixelCyan,
                                fontSize = 11.sp,
                                fontFamily = FontFamily.Monospace
                            )
                        }
                    }

                    IconButton(
                        onClick = onDismiss,
                        enabled = !isSpinning,
                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Close",
                            tint = RetroTextSecondary
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Wheel Container with Top Needle Indicator
                Box(
                    modifier = Modifier.size(240.dp),
                    contentAlignment = Alignment.Center
                ) {
                    // Rotating Wheel Canvas
                    Canvas(
                        modifier = Modifier
                            .fillMaxSize()
                            .rotate(currentRotation)
                    ) {
                        val radius = size.minDimension / 2f
                        val center = Offset(size.width / 2f, size.height / 2f)
                        val sectorDegrees = 360f / 6f

                        for (i in 0 until 6) {
                            drawArc(
                                color = sectorColors[i],
                                startAngle = i * sectorDegrees,
                                sweepAngle = sectorDegrees,
                                useCenter = true,
                                topLeft = Offset(center.x - radius, center.y - radius),
                                size = Size(radius * 2, radius * 2)
                            )
                        }

                        // Outer golden border
                        drawCircle(
                            color = PixelGold,
                            radius = radius,
                            center = center,
                            style = Stroke(width = 8.dp.toPx())
                        )

                        // Divider lines
                        for (i in 0 until 6) {
                            val angleRad = Math.toRadians((i * sectorDegrees).toDouble())
                            val endX = center.x + radius * cos(angleRad).toFloat()
                            val endY = center.y + radius * sin(angleRad).toFloat()
                            drawLine(
                                color = DungeonDarkBg,
                                start = center,
                                end = Offset(endX, endY),
                                strokeWidth = 3.dp.toPx()
                            )
                        }

                        // Center inner hub
                        drawCircle(
                            color = Color(0xFF1E1400),
                            radius = radius * 0.28f,
                            center = center
                        )
                        drawCircle(
                            color = PixelGold,
                            radius = radius * 0.28f,
                            center = center,
                            style = Stroke(width = 3.dp.toPx())
                        )
                    }

                    // Sector Labels overlay
                    for (i in 0 until 6) {
                        val angle = (currentRotation + (i * 60f) + 30f) % 360f
                        val rad = Math.toRadians(angle.toDouble())
                        val dist = 72.dp.value
                        val x = dist * cos(rad).toFloat()
                        val y = dist * sin(rad).toFloat()

                        Box(
                            modifier = Modifier
                                .offset(x = x.dp, y = y.dp)
                                .clip(RoundedCornerShape(4.dp))
                                .background(Color(0xCC0A0C14))
                                .padding(horizontal = 4.dp, vertical = 2.dp)
                        ) {
                            Text(
                                text = sectorLabels[i],
                                color = Color.White,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Black,
                                fontFamily = FontFamily.Monospace
                            )
                        }
                    }

                    // Needle at TOP (Points DOWN toward wheel)
                    Box(
                        modifier = Modifier
                            .align(Alignment.TopCenter)
                            .offset(y = (-4).dp)
                    ) {
                        Text(
                            text = "🔻",
                            fontSize = 26.sp
                        )
                    }

                    // Center Hub Icon
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(PixelGold),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(text = "🪙", fontSize = 16.sp)
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Won Result Banner
                if (wonResultText != null) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp))
                            .background(PixelEmerald.copy(alpha = 0.2f))
                            .border(BorderStroke(1.dp, PixelEmerald), RoundedCornerShape(8.dp))
                            .padding(horizontal = 12.dp, vertical = 8.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "YOU WON: $wonResultText!",
                            color = PixelEmerald,
                            fontWeight = FontWeight.Black,
                            fontSize = 13.sp,
                            fontFamily = FontFamily.Monospace
                        )
                    }
                    Spacer(modifier = Modifier.height(10.dp))
                }

                // Action Buttons
                if (isFreeSpinAvailable) {
                    Button(
                        onClick = { startSpinSequence(isRewarded = false) },
                        enabled = !isSpinning,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                            .testTag("free_spin_button"),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = PixelGold,
                            contentColor = Color(0xFF1E1400)
                        ),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Text(
                            text = if (isSpinning) "SPINNING... 🎲" else "FREE DAILY SPIN 🎡",
                            fontWeight = FontWeight.Black,
                            fontSize = 13.sp,
                            fontFamily = FontFamily.Monospace
                        )
                    }
                } else {
                    Button(
                        onClick = {
                            if (!isSpinning) {
                                AdMobManager.showRewardedAd(
                                    context = context,
                                    onRewardEarned = {
                                        startSpinSequence(isRewarded = true)
                                    }
                                )
                            }
                        },
                        enabled = !isSpinning,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                            .testTag("rewarded_spin_button"),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = PixelAmber,
                            contentColor = Color(0xFF1E1400)
                        ),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Text(
                            text = if (isSpinning) "SPINNING... 🎲" else "SPIN WITH AD 🎬",
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
