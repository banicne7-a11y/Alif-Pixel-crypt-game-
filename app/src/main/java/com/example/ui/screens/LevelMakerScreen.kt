package com.example.ui.screens

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
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
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.FileUpload
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.Save
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.model.CrateType
import com.example.model.Direction
import com.example.model.GameLevel
import com.example.model.MirrorOrientation
import com.example.model.TileType
import com.example.ui.GameViewModel
import com.example.ui.MakerTool
import com.example.ui.ScreenState
import com.example.ui.pixelart.PixelSprites
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
fun LevelMakerScreen(
    viewModel: GameViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val grid by viewModel.makerGrid.collectAsStateWithLifecycle()
    val selectedTool by viewModel.selectedMakerTool.collectAsStateWithLifecycle()
    val parMoves by viewModel.makerParMoves.collectAsStateWithLifecycle()
    val levelName by viewModel.makerLevelName.collectAsStateWithLifecycle()
    val makerMessage by viewModel.makerMessage.collectAsStateWithLifecycle()
    val customLevelsList by viewModel.customLevels.collectAsStateWithLifecycle()

    var showExportDialog by remember { mutableStateOf(false) }
    var showImportDialog by remember { mutableStateOf(false) }
    var importText by remember { mutableStateOf("") }

    val tools = listOf(
        Pair(MakerTool.WALL, "Wall"),
        Pair(MakerTool.FLOOR, "Floor"),
        Pair(MakerTool.HERO, "Hero"),
        Pair(MakerTool.TARGET, "Target"),
        Pair(MakerTool.CRATE, "Crate"),
        Pair(MakerTool.ICE_CRATE, "Ice Box"),
        Pair(MakerTool.ICE_FLOOR, "Ice Tile"),
        Pair(MakerTool.WATER, "Water"),
        Pair(MakerTool.SPIKE, "Spike"),
        Pair(MakerTool.KEY_IRON, "Key"),
        Pair(MakerTool.DOOR_IRON, "Door"),
        Pair(MakerTool.CHEST, "Chest"),
        Pair(MakerTool.MIRROR, "Mirror"),
        Pair(MakerTool.EMITTER, "Laser"),
        Pair(MakerTool.RECEIVER, "Receiver"),
        Pair(MakerTool.EXIT, "Exit")
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
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 14.dp, vertical = 6.dp)
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
                        modifier = Modifier.testTag("maker_back_button")
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = PixelGold
                        )
                    }
                    Column {
                        Text(
                            text = "MAKER MODE",
                            color = PixelGold,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Black,
                            fontFamily = FontFamily.Monospace,
                            letterSpacing = 1.sp
                        )
                        Text(
                            text = "ARCHITECT: ALIF",
                            color = PixelCyan,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Monospace
                        )
                    }
                }

                Row {
                    IconButton(
                        onClick = { showExportDialog = true },
                        modifier = Modifier.testTag("export_code_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.ContentCopy,
                            contentDescription = "Export Code",
                            tint = PixelCyan
                        )
                    }
                    IconButton(
                        onClick = { showImportDialog = true },
                        modifier = Modifier.testTag("import_code_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.FileUpload,
                            contentDescription = "Import Code",
                            tint = PixelPurple
                        )
                    }
                }
            }

            // Level Name & Par Config Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedTextField(
                    value = levelName,
                    onValueChange = { viewModel.updateMakerName(it) },
                    label = { Text("Dungeon Name", fontSize = 10.sp, color = RetroTextSecondary) },
                    modifier = Modifier
                        .weight(1f)
                        .testTag("maker_name_input"),
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = RetroTextPrimary,
                        unfocusedTextColor = RetroTextPrimary,
                        focusedBorderColor = PixelGold,
                        unfocusedBorderColor = DungeonBorder
                    )
                )

                // Par Moves Stepper
                Card(
                    colors = CardDefaults.cardColors(containerColor = DungeonCard),
                    border = BorderStroke(1.dp, DungeonBorder),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                    ) {
                        IconButton(
                            onClick = { viewModel.updateMakerPar(parMoves - 1) },
                            modifier = Modifier.size(32.dp)
                        ) {
                            Icon(Icons.Default.Remove, contentDescription = "Decrease Par", tint = PixelGold, modifier = Modifier.size(16.dp))
                        }
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("PAR", fontSize = 8.sp, color = RetroTextSecondary, fontFamily = FontFamily.Monospace)
                            Text("$parMoves", fontSize = 14.sp, fontWeight = FontWeight.Black, color = PixelGold, fontFamily = FontFamily.Monospace)
                        }
                        IconButton(
                            onClick = { viewModel.updateMakerPar(parMoves + 1) },
                            modifier = Modifier.size(32.dp)
                        ) {
                            Icon(Icons.Default.Add, contentDescription = "Increase Par", tint = PixelGold, modifier = Modifier.size(16.dp))
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Tool Palette Horizontal Scroll
            Text(
                text = "BRUSH TOOL:",
                color = RetroTextSecondary,
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = FontFamily.Monospace
            )
            Spacer(modifier = Modifier.height(4.dp))
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                items(tools) { (tool, label) ->
                    val isSelected = selectedTool == tool
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(if (isSelected) PixelGold.copy(alpha = 0.2f) else DungeonSurface)
                            .border(
                                BorderStroke(
                                    if (isSelected) 1.5.dp else 1.dp,
                                    if (isSelected) PixelGold else DungeonBorder
                                ),
                                RoundedCornerShape(8.dp)
                            )
                            .clickable { viewModel.selectMakerTool(tool) }
                            .padding(horizontal = 10.dp, vertical = 6.dp)
                            .testTag("maker_tool_${tool.name}"),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = label,
                            fontSize = 11.sp,
                            fontWeight = if (isSelected) FontWeight.Black else FontWeight.Normal,
                            color = if (isSelected) PixelGold else RetroTextSecondary,
                            fontFamily = FontFamily.Monospace
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // 8x8 Interactive Grid Editor
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(1f)
                    .clip(RoundedCornerShape(10.dp))
                    .background(Color(0xFF0A0C14))
                    .border(BorderStroke(2.dp, DungeonBorder), RoundedCornerShape(10.dp))
            ) {
                Canvas(
                    modifier = Modifier
                        .fillMaxSize()
                        .clickable {
                            // Handled by touch coordinates
                        }
                ) {
                    val tileSize = size.width / viewModel.makerGridWidth

                    for (y in 0 until viewModel.makerGridHeight) {
                        for (x in 0 until viewModel.makerGridWidth) {
                            val ch = grid.getOrNull(y)?.getOrNull(x) ?: '.'
                            val px = x * tileSize
                            val py = y * tileSize

                            when (ch) {
                                '#' -> PixelSprites.drawWall(this, px, py, tileSize)
                                '.' -> PixelSprites.drawFloor(this, px, py, tileSize)
                                '@' -> {
                                    PixelSprites.drawFloor(this, px, py, tileSize)
                                    PixelSprites.drawPlayer(this, px, py, tileSize, Direction.DOWN, 0)
                                }
                                'T' -> PixelSprites.drawTarget(this, px, py, tileSize, isFilled = false)
                                '$' -> {
                                    PixelSprites.drawFloor(this, px, py, tileSize)
                                    PixelSprites.drawCrate(this, px, py, tileSize, CrateType.WOOD)
                                }
                                'I' -> {
                                    PixelSprites.drawFloor(this, px, py, tileSize)
                                    PixelSprites.drawCrate(this, px, py, tileSize, CrateType.ICE)
                                }
                                '_' -> PixelSprites.drawIceFloor(this, px, py, tileSize)
                                '~' -> PixelSprites.drawWater(this, px, py, tileSize, 0f)
                                '^' -> PixelSprites.drawSpike(this, px, py, tileSize, isActive = true)
                                'K' -> PixelSprites.drawKey(this, px, py, tileSize, isGold = false)
                                'D' -> PixelSprites.drawDoor(this, px, py, tileSize, isGold = false)
                                'C' -> PixelSprites.drawChest(this, px, py, tileSize, isOpen = false)
                                'E' -> PixelSprites.drawExitGate(this, px, py, tileSize, 0.5f)
                                '/' -> PixelSprites.drawMirror(this, px, py, tileSize, MirrorOrientation.SLASH)
                                '>' -> PixelSprites.drawEmitter(this, px, py, tileSize, Direction.RIGHT)
                                'R' -> PixelSprites.drawReceiver(this, px, py, tileSize, isPowered = false)
                                else -> PixelSprites.drawFloor(this, px, py, tileSize)
                            }
                        }
                    }
                }

                // Transparent click grid overlay
                Column(modifier = Modifier.fillMaxSize()) {
                    for (y in 0 until viewModel.makerGridHeight) {
                        Row(modifier = Modifier.weight(1f)) {
                            for (x in 0 until viewModel.makerGridWidth) {
                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .fillMaxSize()
                                        .clickable { viewModel.setMakerCell(x, y) }
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Maker Action Buttons (Test Play, Save, Clear)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Button(
                    onClick = { viewModel.testPlayMakerLevel() },
                    modifier = Modifier
                        .weight(1.3f)
                        .height(48.dp)
                        .testTag("maker_test_play_button"),
                    colors = ButtonDefaults.buttonColors(containerColor = PixelGold, contentColor = Color(0xFF1E1400)),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Icon(Icons.Default.PlayArrow, contentDescription = null)
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("TEST PLAY", fontWeight = FontWeight.Black, fontSize = 13.sp, fontFamily = FontFamily.Monospace)
                }

                OutlinedButton(
                    onClick = { viewModel.saveMakerLevel() },
                    modifier = Modifier
                        .weight(1f)
                        .height(48.dp)
                        .testTag("maker_save_button"),
                    colors = ButtonDefaults.outlinedButtonColors(containerColor = DungeonCard, contentColor = PixelCyan),
                    border = BorderStroke(1.dp, PixelCyan),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Icon(Icons.Default.Save, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("SAVE", fontWeight = FontWeight.Bold, fontSize = 12.sp, fontFamily = FontFamily.Monospace)
                }

                OutlinedButton(
                    onClick = { viewModel.clearMakerGrid() },
                    modifier = Modifier
                        .weight(0.9f)
                        .height(48.dp)
                        .testTag("maker_clear_button"),
                    colors = ButtonDefaults.outlinedButtonColors(containerColor = DungeonCard, contentColor = RetroTextSecondary),
                    border = BorderStroke(1.dp, DungeonBorder),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text("RESET", fontSize = 11.sp, fontFamily = FontFamily.Monospace)
                }
            }

            // Saved Custom Levels List
            if (customLevelsList.isNotEmpty()) {
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "SAVED DUNGEONS (${customLevelsList.size})",
                    color = PixelGold,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.Monospace
                )
                Spacer(modifier = Modifier.height(6.dp))

                customLevelsList.forEach { customLvl ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        colors = CardDefaults.cardColors(containerColor = DungeonCard),
                        border = BorderStroke(1.dp, DungeonBorder),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 12.dp, vertical = 8.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = customLvl.name,
                                    color = RetroTextPrimary,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 13.sp
                                )
                                Text(
                                    text = "Par: ${customLvl.parMoves} moves • 8x8",
                                    color = RetroTextSecondary,
                                    fontSize = 10.sp,
                                    fontFamily = FontFamily.Monospace
                                )
                            }

                            Row {
                                IconButton(
                                    onClick = {
                                        val decoded = GameLevel.decodeFromString(customLvl.levelData)
                                        if (decoded != null) {
                                            viewModel.startLevel(decoded)
                                        }
                                    }
                                ) {
                                    Icon(Icons.Default.PlayArrow, contentDescription = "Play", tint = PixelEmerald)
                                }
                                IconButton(
                                    onClick = { viewModel.deleteCustomLevel(customLvl.id) }
                                ) {
                                    Icon(Icons.Default.Delete, contentDescription = "Delete", tint = PixelRuby)
                                }
                            }
                        }
                    }
                }
            }
        }

        // Notification Snackbar
        if (makerMessage != null) {
            AlertDialog(
                onDismissRequest = { viewModel.clearMakerMessage() },
                title = { Text("Maker Mode", color = PixelGold, fontFamily = FontFamily.Monospace) },
                text = { Text(makerMessage!!, color = RetroTextPrimary) },
                confirmButton = {
                    TextButton(onClick = { viewModel.clearMakerMessage() }) {
                        Text("OK", color = PixelGold)
                    }
                },
                containerColor = DungeonCard
            )
        }

        // Export Code Dialog
        if (showExportDialog) {
            val exportCode = viewModel.getExportCode()
            AlertDialog(
                onDismissRequest = { showExportDialog = false },
                title = { Text("Dungeon Code", color = PixelGold, fontFamily = FontFamily.Monospace) },
                text = {
                    Column {
                        Text("Share this level string with other players:", color = RetroTextSecondary, fontSize = 12.sp)
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = exportCode,
                            color = PixelCyan,
                            fontFamily = FontFamily.Monospace,
                            fontSize = 11.sp,
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(Color(0xFF0A0C14))
                                .padding(8.dp)
                        )
                    }
                },
                confirmButton = {
                    TextButton(
                        onClick = {
                            val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                            clipboard.setPrimaryClip(ClipData.newPlainText("Pixel Crypt Level", exportCode))
                            showExportDialog = false
                        }
                    ) {
                        Text("COPY TO CLIPBOARD", color = PixelGold)
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showExportDialog = false }) {
                        Text("CLOSE", color = RetroTextSecondary)
                    }
                },
                containerColor = DungeonCard
            )
        }

        // Import Code Dialog
        if (showImportDialog) {
            AlertDialog(
                onDismissRequest = { showImportDialog = false },
                title = { Text("Import Dungeon Code", color = PixelPurple, fontFamily = FontFamily.Monospace) },
                text = {
                    Column {
                        Text("Paste level code (starting with PX1|...):", color = RetroTextSecondary, fontSize = 12.sp)
                        Spacer(modifier = Modifier.height(6.dp))
                        OutlinedTextField(
                            value = importText,
                            onValueChange = { importText = it },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = false,
                            maxLines = 3,
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedTextColor = RetroTextPrimary,
                                unfocusedTextColor = RetroTextPrimary,
                                focusedBorderColor = PixelPurple,
                                unfocusedBorderColor = DungeonBorder
                            )
                        )
                    }
                },
                confirmButton = {
                    TextButton(
                        onClick = {
                            if (viewModel.importLevelCode(importText)) {
                                showImportDialog = false
                            }
                        }
                    ) {
                        Text("LOAD & PLAY", color = PixelPurple)
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showImportDialog = false }) {
                        Text("CANCEL", color = RetroTextSecondary)
                    }
                },
                containerColor = DungeonCard
            )
        }
    }
}
