package com.example.ui.pixelart

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import com.example.model.Direction
import com.example.model.GameState
import com.example.model.Position
import com.example.model.TileType
import kotlin.math.abs
import kotlin.math.min

@Composable
fun DungeonGameBoard(
    gameState: GameState,
    onMove: (Direction) -> Unit,
    hintPos: Position? = null,
    heroSkin: com.example.model.HeroSkin = com.example.model.HeroSkin.SILVER_KNIGHT,
    dungeonTheme: com.example.model.DungeonTheme = com.example.model.DungeonTheme.DEFAULT_CRYPT,
    modifier: Modifier = Modifier
) {
    val infiniteTransition = rememberInfiniteTransition(label = "dungeon_anim")
    val animPhase by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "anim_phase"
    )

    var totalDragX by remember { mutableFloatStateOf(0f) }
    var totalDragY by remember { mutableFloatStateOf(0f) }
    val dragThreshold = 45f

    BoxWithConstraints(modifier = modifier.fillMaxSize()) {
        val availableW = constraints.maxWidth.toFloat()
        val availableH = constraints.maxHeight.toFloat()

        val tileW = availableW / gameState.width
        val tileH = availableH / gameState.height
        val tileSize = min(tileW, tileH).coerceAtLeast(24f)

        // Center the dungeon grid inside the canvas
        val gridWidth = tileSize * gameState.width
        val gridHeight = tileSize * gameState.height
        val startX = (availableW - gridWidth) / 2f
        val startY = (availableH - gridHeight) / 2f

        Canvas(
            modifier = Modifier
                .fillMaxSize()
                .pointerInput(gameState.levelId) {
                    detectDragGestures(
                        onDragStart = {
                            totalDragX = 0f
                            totalDragY = 0f
                        },
                        onDrag = { change, dragAmount ->
                            change.consume()
                            totalDragX += dragAmount.x
                            totalDragY += dragAmount.y
                        },
                        onDragEnd = {
                            if (abs(totalDragX) > dragThreshold || abs(totalDragY) > dragThreshold) {
                                if (abs(totalDragX) > abs(totalDragY)) {
                                    if (totalDragX > 0) onMove(Direction.RIGHT) else onMove(Direction.LEFT)
                                } else {
                                    if (totalDragY > 0) onMove(Direction.DOWN) else onMove(Direction.UP)
                                }
                            }
                            totalDragX = 0f
                            totalDragY = 0f
                        }
                    )
                }
        ) {
            // Draw background fill using theme board background
            drawRect(Color(dungeonTheme.boardBgColorHex), Offset.Zero, Size(size.width, size.height))

            // 1. Draw Static / Base Tiles
            for (y in 0 until gameState.height) {
                for (x in 0 until gameState.width) {
                    val pos = Position(x, y)
                    val px = startX + x * tileSize
                    val py = startY + y * tileSize

                    val effectiveTile = gameState.getEffectiveTile(pos)

                    when (effectiveTile) {
                        TileType.WALL -> PixelSprites.drawWall(
                            scope = this,
                            x = px,
                            y = py,
                            tileSize = tileSize,
                            darkStone = Color(dungeonTheme.wallDarkColorHex),
                            midStone = Color(dungeonTheme.wallMidColorHex),
                            lightStone = Color(dungeonTheme.wallLightColorHex),
                            highlight = Color(dungeonTheme.wallHighlightHex)
                        )
                        TileType.FLOOR -> PixelSprites.drawFloor(
                            scope = this,
                            x = px,
                            y = py,
                            tileSize = tileSize,
                            floorBg = Color(dungeonTheme.floorBgColorHex),
                            tileAccent = Color(dungeonTheme.floorAccentColorHex)
                        )
                        TileType.TARGET -> {
                            val isFilled = gameState.crates.containsKey(pos)
                            PixelSprites.drawTarget(this, px, py, tileSize, isFilled)
                        }
                        TileType.ICE -> PixelSprites.drawIceFloor(this, px, py, tileSize)
                        TileType.WATER -> PixelSprites.drawWater(this, px, py, tileSize, animPhase)
                        TileType.SPIKE -> PixelSprites.drawSpike(this, px, py, tileSize, gameState.spikesActive)
                        TileType.DOOR_LOCKED -> PixelSprites.drawDoor(this, px, py, tileSize, isGold = false)
                        TileType.DOOR_GOLD -> PixelSprites.drawDoor(this, px, py, tileSize, isGold = true)
                        TileType.KEY_IRON -> PixelSprites.drawKey(this, px, py, tileSize, isGold = false)
                        TileType.KEY_GOLD -> PixelSprites.drawKey(this, px, py, tileSize, isGold = true)
                        TileType.CHEST -> {
                            val isOpen = gameState.openedChests.contains(pos)
                            PixelSprites.drawChest(this, px, py, tileSize, isOpen)
                        }
                        TileType.EXIT_GATE -> PixelSprites.drawExitGate(this, px, py, tileSize, animPhase)
                        TileType.PORTAL_A -> PixelSprites.drawPortal(this, px, py, tileSize, isA = true, animPhase = animPhase)
                        TileType.PORTAL_B -> PixelSprites.drawPortal(this, px, py, tileSize, isA = false, animPhase = animPhase)
                        TileType.EMITTER -> {
                            val dir = gameState.emitterDirections[pos] ?: Direction.RIGHT
                            PixelSprites.drawEmitter(this, px, py, tileSize, dir)
                        }
                        TileType.RECEIVER -> {
                            PixelSprites.drawReceiver(this, px, py, tileSize, gameState.isReceiverPowered)
                        }
                        TileType.MIRROR -> {
                            val orientation = gameState.mirrors[pos] ?: com.example.model.MirrorOrientation.SLASH
                            PixelSprites.drawMirror(this, px, py, tileSize, orientation)
                        }
                    }
                }
            }

            // 2. Draw Dynamic Mirrors (if not in base tiles)
            gameState.mirrors.forEach { (pos, orientation) ->
                val px = startX + pos.x * tileSize
                val py = startY + pos.y * tileSize
                PixelSprites.drawMirror(this, px, py, tileSize, orientation)
            }

            // 3. Draw Laser Beams
            gameState.laserPaths.forEach { seg ->
                val fromX = startX + seg.from.x * tileSize + tileSize * 0.5f
                val fromY = startY + seg.from.y * tileSize + tileSize * 0.5f
                val toX = startX + seg.to.x * tileSize + tileSize * 0.5f
                val toY = startY + seg.to.y * tileSize + tileSize * 0.5f

                // Outer neon laser glow
                drawLine(
                    color = Color(0x66FF3366),
                    start = Offset(fromX, fromY),
                    end = Offset(toX, toY),
                    strokeWidth = tileSize * 0.28f
                )
                // Inner bright laser core
                drawLine(
                    color = Color(0xFFFF80AB),
                    start = Offset(fromX, fromY),
                    end = Offset(toX, toY),
                    strokeWidth = tileSize * 0.12f
                )
            }

            // 4. Draw Crates
            gameState.crates.forEach { (pos, crateType) ->
                val px = startX + pos.x * tileSize
                val py = startY + pos.y * tileSize
                PixelSprites.drawCrate(this, px, py, tileSize, crateType)
            }

            // 5. Draw Player Knight
            val playerPx = startX + gameState.playerPos.x * tileSize
            val playerPy = startY + gameState.playerPos.y * tileSize
            PixelSprites.drawPlayer(
                scope = this,
                x = playerPx,
                y = playerPy,
                tileSize = tileSize,
                facing = gameState.playerFacing,
                animStep = gameState.stepCount,
                skin = heroSkin
            )

            // 6. Draw Hint Indicator (if requested)
            if (hintPos != null) {
                val hx = startX + hintPos.x * tileSize
                val hy = startY + hintPos.y * tileSize
                val pulse = animPhase * tileSize * 0.15f
                drawCircle(
                    color = Color(0xFFFFD13B),
                    radius = tileSize * 0.35f + pulse,
                    center = Offset(hx + tileSize * 0.5f, hy + tileSize * 0.5f),
                    style = Stroke(width = tileSize * 0.08f)
                )
            }
        }
    }
}
