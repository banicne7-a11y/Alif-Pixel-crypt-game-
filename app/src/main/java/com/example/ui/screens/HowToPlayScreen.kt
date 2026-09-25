package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
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
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
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
import com.example.ui.GameViewModel
import com.example.ui.ScreenState
import com.example.ui.theme.DungeonBorder
import com.example.ui.theme.DungeonCard
import com.example.ui.theme.DungeonDarkBg
import com.example.ui.theme.PixelCyan
import com.example.ui.theme.PixelGold
import com.example.ui.theme.PixelPurple
import com.example.ui.theme.PixelRuby
import com.example.ui.theme.RetroTextPrimary
import com.example.ui.theme.RetroTextSecondary

data class MechanicGuide(
    val title: String,
    val icon: String,
    val summary: String,
    val tip: String
)

@Composable
fun HowToPlayScreen(
    viewModel: GameViewModel,
    modifier: Modifier = Modifier
) {
    val guides = listOf(
        MechanicGuide(
            title = "Crates & Pressure Plates",
            icon = "📦",
            summary = "Push wooden crates onto golden pressure plates. Once all plates are covered, the dungeon is cleared!",
            tip = "Never push a crate flush into a corner unless you have planned an exit, as you can only push, never pull!"
        ),
        MechanicGuide(
            title = "Slippery Ice & Sliding Blocks",
            icon = "❄️",
            summary = "Ice floors have zero friction. Walking onto ice will slide you forward until you strike solid ground or an obstacle.",
            tip = "Ice blocks keep sliding in the direction pushed until they hit a wall or drop into water."
        ),
        MechanicGuide(
            title = "Water Chasms & Bridges",
            icon = "🌊",
            summary = "Deep water blocks passage. Push a wooden crate into the water to create a safe wooden bridge!",
            tip = "Bridging is essential to connect disconnected island chambers in the Frost Glade."
        ),
        MechanicGuide(
            title = "Laser Beams & Optical Mirrors",
            icon = "⚡",
            summary = "Emitters fire radiant beams. Face a mirror and tap the Rotate button (or center D-Pad action) to flip diagonal orientation between / and \\.",
            tip = "Route the light into the blue solar receiver to power up locked mechanisms and open the exit stairs!"
        ),
        MechanicGuide(
            title = "Keys & Heavy Gates",
            icon = "🗝️",
            summary = "Walk over iron or golden keys to collect them. Stepping into locked portcullis doors will consume a matching key and permanently open the gate.",
            tip = "Guard your keys wisely — some paths lead to optional secret relic chests!"
        ),
        MechanicGuide(
            title = "Spike Traps & Portals",
            icon = "🌀",
            summary = "Spike traps alternate active and inactive with each step you make. Cosmic portals instantly teleport you between paired vortexes.",
            tip = "If you step onto an active spike, use the Undo button to rewind and adjust your step rhythm."
        ),
        MechanicGuide(
            title = "Maker Mode & Controls",
            icon = "🛠️",
            summary = "You can swipe anywhere on screen or use the retro D-Pad. Use Maker Mode to design your own puzzles and export string codes to share.",
            tip = "Toggle the CRT Scanlines and 8-bit Audio settings anytime from the main menu!"
        )
    )

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
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = { viewModel.navigateTo(ScreenState.MENU) },
                    modifier = Modifier.testTag("how_to_play_back_button")
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back",
                        tint = PixelGold
                    )
                }
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = "HOW TO PLAY",
                    color = PixelGold,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Black,
                    fontFamily = FontFamily.Monospace,
                    letterSpacing = 1.sp
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            LazyColumn(
                contentPadding = PaddingValues(bottom = 16.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                items(guides) { guide ->
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(10.dp),
                        colors = CardDefaults.cardColors(containerColor = DungeonCard),
                        border = BorderStroke(1.dp, DungeonBorder)
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = guide.icon,
                                    fontSize = 20.sp
                                )
                                Spacer(modifier = Modifier.width(10.dp))
                                Text(
                                    text = guide.title,
                                    color = PixelGold,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold,
                                    fontFamily = FontFamily.Monospace
                                )
                            }
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = guide.summary,
                                color = RetroTextPrimary,
                                fontSize = 12.sp,
                                lineHeight = 16.sp
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(Color(0xFF0F111A))
                                    .padding(horizontal = 8.dp, vertical = 6.dp)
                            ) {
                                Text(
                                    text = "💡 Tip: ${guide.tip}",
                                    color = PixelCyan,
                                    fontSize = 11.sp,
                                    lineHeight = 15.sp
                                )
                            }
                        }
                    }
                }

                // Creator & Credits Card
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(10.dp),
                        colors = CardDefaults.cardColors(containerColor = DungeonCard),
                        border = BorderStroke(1.dp, PixelGold.copy(alpha = 0.6f))
                    ) {
                        Column(
                            modifier = Modifier.padding(14.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = "🎮 CREATED BY ALIF",
                                color = PixelGold,
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Black,
                                fontFamily = FontFamily.Monospace
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "Lead Game Architect & Cryptmaster",
                                color = PixelCyan,
                                fontSize = 12.sp,
                                fontFamily = FontFamily.Monospace
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = "Designed with 200 handmade & procedural Sokoban puzzles, 8-bit chiptune audio, and dynamic character skins.",
                                color = RetroTextSecondary,
                                fontSize = 11.sp,
                                textAlign = androidx.compose.ui.text.style.TextAlign.Center
                            )
                        }
                    }
                }
            }
        }
    }
}
