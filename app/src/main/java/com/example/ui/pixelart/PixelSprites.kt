package com.example.ui.pixelart

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import com.example.model.CrateType
import com.example.model.Direction
import com.example.model.MirrorOrientation
import com.example.model.TileType

object PixelSprites {

    fun drawWall(scope: DrawScope, x: Float, y: Float, tileSize: Float) {
        val darkStone = Color(0xFF1B1D2A)
        val midStone = Color(0xFF2E3248)
        val lightStone = Color(0xFF454B6B)
        val highlight = Color(0xFF656D94)

        scope.drawRect(midStone, Offset(x, y), Size(tileSize, tileSize))

        // Mortar lines & bricks
        val p = tileSize / 8f
        // Top brick line
        scope.drawRect(darkStone, Offset(x, y + p * 4), Size(tileSize, p * 0.5f))
        // Vertical mortar lines
        scope.drawRect(darkStone, Offset(x + p * 4, y), Size(p * 0.5f, p * 4))
        scope.drawRect(darkStone, Offset(x + p * 2, y + p * 4), Size(p * 0.5f, p * 4))
        scope.drawRect(darkStone, Offset(x + p * 6, y + p * 4), Size(p * 0.5f, p * 4))

        // Brick highlights
        scope.drawRect(highlight, Offset(x + p * 0.5f, y + p * 0.5f), Size(p * 3f, p * 0.8f))
        scope.drawRect(lightStone, Offset(x + p * 4.5f, y + p * 0.5f), Size(p * 3f, p * 0.8f))
        scope.drawRect(highlight, Offset(x + p * 2.5f, y + p * 4.5f), Size(p * 3f, p * 0.8f))

        // Outline
        scope.drawRect(darkStone, Offset(x, y), Size(tileSize, tileSize), style = Stroke(width = p * 0.5f))
    }

    fun drawFloor(scope: DrawScope, x: Float, y: Float, tileSize: Float) {
        val floorBg = Color(0xFF141724)
        val tileAccent = Color(0xFF1C2033)
        val crack = Color(0xFF0F111A)

        scope.drawRect(floorBg, Offset(x, y), Size(tileSize, tileSize))

        val p = tileSize / 8f
        // Inset flagstone
        scope.drawRect(tileAccent, Offset(x + p * 0.8f, y + p * 0.8f), Size(tileSize - p * 1.6f, tileSize - p * 1.6f))
        // Subtle crack
        scope.drawLine(crack, Offset(x + p * 2, y + p * 2), Offset(x + p * 3.5f, y + p * 4), strokeWidth = p * 0.4f)
    }

    fun drawIceFloor(scope: DrawScope, x: Float, y: Float, tileSize: Float) {
        val iceBg = Color(0xFF1E3A5F)
        val iceHighlight = Color(0xFF64B5F6)
        val iceGlint = Color(0xFFE1F5FE)

        scope.drawRect(iceBg, Offset(x, y), Size(tileSize, tileSize))

        val p = tileSize / 8f
        // Glint scratches
        scope.drawLine(iceHighlight, Offset(x + p * 1, y + p * 3), Offset(x + p * 5, y + p * 1), strokeWidth = p * 0.4f)
        scope.drawLine(iceGlint, Offset(x + p * 4, y + p * 6), Offset(x + p * 7, y + p * 4), strokeWidth = p * 0.4f)
        scope.drawRect(Color(0xFF0D253A), Offset(x, y), Size(tileSize, tileSize), style = Stroke(width = p * 0.3f))
    }

    fun drawWater(scope: DrawScope, x: Float, y: Float, tileSize: Float, animPhase: Float) {
        val waterDeep = Color(0xFF0A2540)
        val waterMid = Color(0xFF0077B6)
        val waterFoam = Color(0xFF90E0EF)

        scope.drawRect(waterDeep, Offset(x, y), Size(tileSize, tileSize))

        val p = tileSize / 8f
        val waveOffset = (animPhase * p * 2) % (p * 4)
        scope.drawRect(waterMid, Offset(x + p, y + p * 2 + waveOffset * 0.5f), Size(p * 6, p * 1.5f))
        scope.drawRect(waterFoam, Offset(x + p * 2, y + p * 2.2f + waveOffset * 0.5f), Size(p * 2.5f, p * 0.5f))
    }

    fun drawTarget(scope: DrawScope, x: Float, y: Float, tileSize: Float, isFilled: Boolean) {
        drawFloor(scope, x, y, tileSize)
        val p = tileSize / 8f
        val runeColor = if (isFilled) Color(0xFF00E676) else Color(0xFFFFD13B)
        val ringColor = if (isFilled) Color(0xFF004D25) else Color(0xFF7A5C00)

        // Outer pressure ring
        scope.drawCircle(ringColor, radius = tileSize * 0.35f, center = Offset(x + tileSize * 0.5f, y + tileSize * 0.5f))
        scope.drawCircle(runeColor, radius = tileSize * 0.28f, center = Offset(x + tileSize * 0.5f, y + tileSize * 0.5f))

        // Center diamond
        val path = Path().apply {
            moveTo(x + tileSize * 0.5f, y + p * 2.5f)
            lineTo(x + tileSize - p * 2.5f, y + tileSize * 0.5f)
            lineTo(x + tileSize * 0.5f, y + tileSize - p * 2.5f)
            lineTo(x + p * 2.5f, y + tileSize * 0.5f)
            close()
        }
        scope.drawPath(path, if (isFilled) Color(0xFFE8F5E9) else Color(0xFFFFF9C4))
    }

    fun drawSpike(scope: DrawScope, x: Float, y: Float, tileSize: Float, isActive: Boolean) {
        drawFloor(scope, x, y, tileSize)
        val p = tileSize / 8f
        if (!isActive) {
            // Holes in floor
            val holeColor = Color(0xFF0A0C14)
            for (gx in 0..1) {
                for (gy in 0..1) {
                    val hx = x + p * (2.5f + gx * 3f)
                    val hy = y + p * (2.5f + gy * 3f)
                    scope.drawCircle(holeColor, radius = p * 0.7f, center = Offset(hx, hy))
                }
            }
        } else {
            // Raised spikes
            val spikeMetal = Color(0xFFCFD8DC)
            val spikeTip = Color(0xFFFF5252)
            val spikeBase = Color(0xFF546E7A)

            for (gx in 0..1) {
                for (gy in 0..1) {
                    val sx = x + p * (2.5f + gx * 3f)
                    val sy = y + p * (2.5f + gy * 3f)
                    val spikePath = Path().apply {
                        moveTo(sx, sy - p * 1.8f)
                        lineTo(sx + p * 1.2f, sy + p * 0.8f)
                        lineTo(sx - p * 1.2f, sy + p * 0.8f)
                        close()
                    }
                    scope.drawPath(spikePath, spikeMetal)
                    scope.drawCircle(spikeTip, radius = p * 0.5f, center = Offset(sx, sy - p * 1.6f))
                    scope.drawCircle(spikeBase, radius = p * 0.9f, center = Offset(sx, sy + p * 0.7f))
                }
            }
        }
    }

    fun drawCrate(scope: DrawScope, x: Float, y: Float, tileSize: Float, crateType: CrateType) {
        val p = tileSize / 8f
        when (crateType) {
            CrateType.WOOD -> {
                val woodDark = Color(0xFF532E0D)
                val woodMid = Color(0xFF8B5A2B)
                val woodLight = Color(0xFFA67C52)
                val metalBracket = Color(0xFFCFD8DC)

                scope.drawRect(woodMid, Offset(x + p * 0.5f, y + p * 0.5f), Size(tileSize - p, tileSize - p))

                // Planks
                scope.drawRect(woodDark, Offset(x + p * 0.5f, y + p * 4), Size(tileSize - p, p * 0.4f))

                // X bracing
                scope.drawLine(woodLight, Offset(x + p * 1.5f, y + p * 1.5f), Offset(x + tileSize - p * 1.5f, y + tileSize - p * 1.5f), strokeWidth = p * 0.6f)
                scope.drawLine(woodLight, Offset(x + tileSize - p * 1.5f, y + p * 1.5f), Offset(x + p * 1.5f, y + tileSize - p * 1.5f), strokeWidth = p * 0.6f)

                // Corner metal brackets & studs
                val corners = listOf(
                    Offset(x + p * 0.5f, y + p * 0.5f),
                    Offset(x + tileSize - p * 1.5f, y + p * 0.5f),
                    Offset(x + p * 0.5f, y + tileSize - p * 1.5f),
                    Offset(x + tileSize - p * 1.5f, y + tileSize - p * 1.5f)
                )
                corners.forEach { c ->
                    scope.drawRect(metalBracket, c, Size(p, p))
                }
                scope.drawRect(woodDark, Offset(x + p * 0.5f, y + p * 0.5f), Size(tileSize - p, tileSize - p), style = Stroke(width = p * 0.4f))
            }
            CrateType.ICE -> {
                val iceDeep = Color(0xFF0288D1)
                val iceMid = Color(0xFF29B6F6)
                val iceBright = Color(0xFFE0F7FA)

                scope.drawRect(iceMid, Offset(x + p * 0.8f, y + p * 0.8f), Size(tileSize - p * 1.6f, tileSize - p * 1.6f))
                scope.drawRect(iceDeep, Offset(x + p * 2f, y + p * 2f), Size(tileSize - p * 4f, tileSize - p * 4f))
                scope.drawLine(iceBright, Offset(x + p * 1.5f, y + p * 1.5f), Offset(x + p * 4.5f, y + p * 1.5f), strokeWidth = p * 0.6f)
                scope.drawLine(iceBright, Offset(x + p * 1.5f, y + p * 1.5f), Offset(x + p * 1.5f, y + p * 4.5f), strokeWidth = p * 0.6f)
                scope.drawRect(iceBright, Offset(x + p * 0.8f, y + p * 0.8f), Size(tileSize - p * 1.6f, tileSize - p * 1.6f), style = Stroke(width = p * 0.4f))
            }
            CrateType.GEM_RUBY -> {
                val obsidian = Color(0xFF212121)
                val rubyGlow = Color(0xFFFF1744)
                val rubyCore = Color(0xFFFF80AB)

                scope.drawRect(obsidian, Offset(x + p * 0.8f, y + p * 0.8f), Size(tileSize - p * 1.6f, tileSize - p * 1.6f))
                val diamond = Path().apply {
                    moveTo(x + tileSize * 0.5f, y + p * 1.8f)
                    lineTo(x + tileSize - p * 1.8f, y + tileSize * 0.5f)
                    lineTo(x + tileSize * 0.5f, y + tileSize - p * 1.8f)
                    lineTo(x + p * 1.8f, y + tileSize * 0.5f)
                    close()
                }
                scope.drawPath(diamond, rubyGlow)
                scope.drawCircle(rubyCore, radius = p * 1.2f, center = Offset(x + tileSize * 0.5f, y + tileSize * 0.5f))
            }
            CrateType.GEM_CYAN -> {
                val obsidian = Color(0xFF1E293B)
                val cyanGlow = Color(0xFF00E5FF)
                val cyanCore = Color(0xFFE0F7FA)

                scope.drawRect(obsidian, Offset(x + p * 0.8f, y + p * 0.8f), Size(tileSize - p * 1.6f, tileSize - p * 1.6f))
                scope.drawCircle(cyanGlow, radius = tileSize * 0.3f, center = Offset(x + tileSize * 0.5f, y + tileSize * 0.5f))
                scope.drawCircle(cyanCore, radius = tileSize * 0.15f, center = Offset(x + tileSize * 0.5f, y + tileSize * 0.5f))
            }
        }
    }

    fun drawPlayer(
        scope: DrawScope,
        x: Float,
        y: Float,
        tileSize: Float,
        facing: Direction,
        animStep: Int,
        skin: com.example.model.HeroSkin = com.example.model.HeroSkin.SILVER_KNIGHT
    ) {
        val p = tileSize / 16f
        val armorSilver = Color(skin.primaryColorHex)
        val armorDark = Color(skin.primaryColorHex).copy(alpha = 0.7f)
        val tunicBlue = Color(skin.secondaryColorHex)
        val featherPlume = Color(skin.plumeColorHex)
        val bootsLeather = Color(0xFF4A2810)
        val visorGold = Color(skin.visorColorHex)

        val bob = if (animStep % 2 == 1) p * 0.6f else 0f
        val cx = x + tileSize * 0.5f
        val cy = y + tileSize * 0.5f + bob

        // Shadow under player
        scope.drawOval(
            color = Color(0x66000000),
            topLeft = Offset(cx - p * 6f, y + tileSize - p * 3.5f),
            size = Size(p * 12f, p * 3f)
        )

        when (facing) {
            Direction.DOWN -> {
                // Helmet Plume
                scope.drawRect(featherPlume, Offset(cx - p * 1.5f, cy - p * 7f), Size(p * 3f, p * 3f))
                // Helmet
                scope.drawRect(armorSilver, Offset(cx - p * 4.5f, cy - p * 4.5f), Size(p * 9f, p * 7f))
                // Visor slit
                scope.drawRect(Color(0xFF1E1B2E), Offset(cx - p * 3.5f, cy - p * 2.5f), Size(p * 7f, p * 1.8f))
                // Glowing eyes in slit
                scope.drawRect(visorGold, Offset(cx - p * 2.5f, cy - p * 2.2f), Size(p * 1.5f, p * 1.2f))
                scope.drawRect(visorGold, Offset(cx + p * 1f, cy - p * 2.2f), Size(p * 1.5f, p * 1.2f))

                // Tunic & Body
                scope.drawRect(tunicBlue, Offset(cx - p * 4f, cy + p * 2.5f), Size(p * 8f, p * 4f))
                // Gold belt buckle
                scope.drawRect(visorGold, Offset(cx - p * 1.2f, cy + p * 5f), Size(p * 2.4f, p * 1.2f))
                // Boots
                scope.drawRect(bootsLeather, Offset(cx - p * 3.5f, cy + p * 6.5f), Size(p * 3f, p * 2f))
                scope.drawRect(bootsLeather, Offset(cx + p * 0.5f, cy + p * 6.5f), Size(p * 3f, p * 2f))
            }
            Direction.UP -> {
                // Back of helmet & plume
                scope.drawRect(featherPlume, Offset(cx - p * 1.5f, cy - p * 7.5f), Size(p * 3f, p * 4f))
                scope.drawRect(armorSilver, Offset(cx - p * 4.5f, cy - p * 4.5f), Size(p * 9f, p * 7f))
                scope.drawRect(armorDark, Offset(cx - p * 3.5f, cy - p * 2f), Size(p * 7f, p * 3f))
                // Adventurer Backpack / Cape
                scope.drawRect(tunicBlue, Offset(cx - p * 4f, cy + p * 2.5f), Size(p * 8f, p * 4.5f))
                scope.drawRect(Color(0xFF8B5A2B), Offset(cx - p * 3f, cy + p * 2.8f), Size(p * 6f, p * 3f))
                // Boots
                scope.drawRect(bootsLeather, Offset(cx - p * 3.5f, cy + p * 6.5f), Size(p * 3f, p * 2f))
                scope.drawRect(bootsLeather, Offset(cx + p * 0.5f, cy + p * 6.5f), Size(p * 3f, p * 2f))
            }
            Direction.LEFT -> {
                // Side Profile Left
                scope.drawRect(featherPlume, Offset(cx - p * 1f, cy - p * 7f), Size(p * 4f, p * 2.5f))
                scope.drawRect(armorSilver, Offset(cx - p * 4.5f, cy - p * 4.5f), Size(p * 8f, p * 7f))
                // Visor slit left
                scope.drawRect(Color(0xFF1E1B2E), Offset(cx - p * 4.5f, cy - p * 2.5f), Size(p * 4f, p * 1.8f))
                scope.drawRect(visorGold, Offset(cx - p * 3.5f, cy - p * 2.2f), Size(p * 1.5f, p * 1.2f))
                // Shield on side
                scope.drawOval(tunicBlue, Offset(cx - p * 3f, cy + p * 2f), Size(p * 6f, p * 5f))
                scope.drawOval(visorGold, Offset(cx - p * 2f, cy + p * 3f), Size(p * 4f, p * 3f))
                // Boots
                scope.drawRect(bootsLeather, Offset(cx - p * 3.5f, cy + p * 6.5f), Size(p * 5f, p * 2f))
            }
            Direction.RIGHT -> {
                // Side Profile Right
                scope.drawRect(featherPlume, Offset(cx - p * 3f, cy - p * 7f), Size(p * 4f, p * 2.5f))
                scope.drawRect(armorSilver, Offset(cx - p * 3.5f, cy - p * 4.5f), Size(p * 8f, p * 7f))
                // Visor slit right
                scope.drawRect(Color(0xFF1E1B2E), Offset(cx + p * 0.5f, cy - p * 2.5f), Size(p * 4f, p * 1.8f))
                scope.drawRect(visorGold, Offset(cx + p * 2f, cy - p * 2.2f), Size(p * 1.5f, p * 1.2f))
                // Shield on side
                scope.drawOval(tunicBlue, Offset(cx - p * 3f, cy + p * 2f), Size(p * 6f, p * 5f))
                scope.drawOval(visorGold, Offset(cx - p * 2f, cy + p * 3f), Size(p * 4f, p * 3f))
                // Boots
                scope.drawRect(bootsLeather, Offset(cx - p * 1.5f, cy + p * 6.5f), Size(p * 5f, p * 2f))
            }
        }
    }

    fun drawDoor(scope: DrawScope, x: Float, y: Float, tileSize: Float, isGold: Boolean) {
        val p = tileSize / 8f
        val frameColor = if (isGold) Color(0xFFB8860B) else Color(0xFF374151)
        val barColor = if (isGold) Color(0xFFFFD700) else Color(0xFF9CA3AF)
        val lockColor = if (isGold) Color(0xFFFFE082) else Color(0xFFE5E7EB)

        scope.drawRect(frameColor, Offset(x, y), Size(tileSize, tileSize))

        // Vertical iron portcullis bars
        for (i in 1..3) {
            scope.drawRect(barColor, Offset(x + p * (i * 2 - 0.5f), y + p), Size(p, tileSize - p * 2))
        }

        // Center padlock
        scope.drawCircle(lockColor, radius = p * 1.4f, center = Offset(x + tileSize * 0.5f, y + tileSize * 0.5f))
        scope.drawRect(Color(0xFF111827), Offset(x + tileSize * 0.5f - p * 0.3f, y + tileSize * 0.5f - p * 0.5f), Size(p * 0.6f, p * 1.2f))
    }

    fun drawKey(scope: DrawScope, x: Float, y: Float, tileSize: Float, isGold: Boolean) {
        drawFloor(scope, x, y, tileSize)
        val p = tileSize / 8f
        val keyColor = if (isGold) Color(0xFFFFD13B) else Color(0xFFCBD5E1)
        val keyShadow = if (isGold) Color(0xFFB45309) else Color(0xFF475569)

        val cx = x + tileSize * 0.5f
        val cy = y + tileSize * 0.5f

        // Key head loop
        scope.drawCircle(keyColor, radius = p * 1.5f, center = Offset(cx - p * 1.2f, cy))
        scope.drawCircle(Color(0xFF141724), radius = p * 0.7f, center = Offset(cx - p * 1.2f, cy))

        // Shaft & teeth
        scope.drawRect(keyColor, Offset(cx - p * 0.2f, cy - p * 0.4f), Size(p * 2.8f, p * 0.8f))
        scope.drawRect(keyShadow, Offset(cx + p * 1.6f, cy + p * 0.4f), Size(p * 0.6f, p * 1f))
        scope.drawRect(keyShadow, Offset(cx + p * 2.4f, cy + p * 0.4f), Size(p * 0.6f, p * 0.8f))
    }

    fun drawChest(scope: DrawScope, x: Float, y: Float, tileSize: Float, isOpen: Boolean) {
        drawFloor(scope, x, y, tileSize)
        val p = tileSize / 8f
        val wood = Color(0xFF78350F)
        val goldTrim = Color(0xFFFFD13B)
        val gemRed = Color(0xFFFF1744)

        val cx = x + tileSize * 0.5f
        val cy = y + tileSize * 0.5f

        if (!isOpen) {
            // Chest body
            scope.drawRect(wood, Offset(x + p * 1.2f, y + p * 2f), Size(tileSize - p * 2.4f, tileSize - p * 3.2f))
            // Gold straps
            scope.drawRect(goldTrim, Offset(x + p * 2f, y + p * 2f), Size(p * 0.8f, tileSize - p * 3.2f))
            scope.drawRect(goldTrim, Offset(x + tileSize - p * 2.8f, y + p * 2f), Size(p * 0.8f, tileSize - p * 3.2f))
            // Center lock with red ruby
            scope.drawRect(goldTrim, Offset(cx - p * 1f, cy - p * 0.5f), Size(p * 2f, p * 1.6f))
            scope.drawCircle(gemRed, radius = p * 0.6f, center = Offset(cx, cy + p * 0.3f))
        } else {
            // Open chest glowing inside
            scope.drawRect(wood, Offset(x + p * 1.2f, y + p * 3.5f), Size(tileSize - p * 2.4f, tileSize - p * 4.7f))
            // Inner golden loot glow
            scope.drawRect(Color(0xFFFFF59D), Offset(x + p * 1.8f, y + p * 3.8f), Size(tileSize - p * 3.6f, p * 1.8f))
        }
    }

    fun drawExitGate(scope: DrawScope, x: Float, y: Float, tileSize: Float, animPhase: Float) {
        drawFloor(scope, x, y, tileSize)
        val p = tileSize / 8f
        val glowGold = Color(0xFFFFD54F)
        val darkHole = Color(0xFF07080E)

        // Dark stairway descending
        scope.drawRect(darkHole, Offset(x + p * 1.5f, y + p * 1.5f), Size(tileSize - p * 3f, tileSize - p * 3f))

        // Steps
        for (i in 0..2) {
            scope.drawRect(Color(0xFF2E3248), Offset(x + p * (2f + i * 0.5f), y + p * (2f + i * 1.3f)), Size(tileSize - p * (4f + i * 1f), p * 0.8f))
        }

        // Golden aura border with pulsation
        val pulse = (kotlin.math.sin(animPhase * 3.14f) * p * 0.3f).toFloat()
        scope.drawRect(glowGold, Offset(x + p * 1.2f - pulse, y + p * 1.2f - pulse), Size(tileSize - p * 2.4f + pulse * 2, tileSize - p * 2.4f + pulse * 2), style = Stroke(width = p * 0.6f))
    }

    fun drawPortal(scope: DrawScope, x: Float, y: Float, tileSize: Float, isA: Boolean, animPhase: Float) {
        drawFloor(scope, x, y, tileSize)
        val cx = x + tileSize * 0.5f
        val cy = y + tileSize * 0.5f

        val portalColor = if (isA) Color(0xFFA855F7) else Color(0xFF00E5FF)
        val coreColor = if (isA) Color(0xFFE9D5FF) else Color(0xFFE0F7FA)

        val r1 = tileSize * 0.38f
        val r2 = tileSize * 0.22f
        val r3 = tileSize * 0.10f

        scope.drawCircle(portalColor.copy(alpha = 0.4f), radius = r1, center = Offset(cx, cy))
        scope.drawCircle(portalColor, radius = r2, center = Offset(cx, cy), style = Stroke(width = tileSize * 0.08f))
        scope.drawCircle(coreColor, radius = r3, center = Offset(cx, cy))
    }

    fun drawEmitter(scope: DrawScope, x: Float, y: Float, tileSize: Float, dir: Direction) {
        drawWall(scope, x, y, tileSize)
        val p = tileSize / 8f
        val brass = Color(0xFFFFB300)
        val lens = Color(0xFFFF4081)

        val cx = x + tileSize * 0.5f
        val cy = y + tileSize * 0.5f

        // Base crystal mount
        scope.drawCircle(brass, radius = p * 2f, center = Offset(cx, cy))
        // Crystal nozzle pointing towards dir
        val nx = cx + dir.dx * p * 2.2f
        val ny = cy + dir.dy * p * 2.2f
        scope.drawCircle(lens, radius = p * 1.4f, center = Offset(nx, ny))
    }

    fun drawReceiver(scope: DrawScope, x: Float, y: Float, tileSize: Float, isPowered: Boolean) {
        drawWall(scope, x, y, tileSize)
        val p = tileSize / 8f
        val frame = Color(0xFF455A64)
        val crystal = if (isPowered) Color(0xFF00E5FF) else Color(0xFF37474F)
        val inner = if (isPowered) Color(0xFFE0F7FA) else Color(0xFF263238)

        val cx = x + tileSize * 0.5f
        val cy = y + tileSize * 0.5f

        scope.drawCircle(frame, radius = p * 2.4f, center = Offset(cx, cy))
        scope.drawCircle(crystal, radius = p * 1.8f, center = Offset(cx, cy))
        scope.drawCircle(inner, radius = p * 0.9f, center = Offset(cx, cy))
    }

    fun drawMirror(scope: DrawScope, x: Float, y: Float, tileSize: Float, orientation: MirrorOrientation) {
        drawFloor(scope, x, y, tileSize)
        val p = tileSize / 8f
        val standColor = Color(0xFF607D8B)
        val glassBright = Color(0xFFE0F7FA)
        val glassGleam = Color(0xFF00E5FF)

        val cx = x + tileSize * 0.5f
        val cy = y + tileSize * 0.5f

        // Rotating stand circle
        scope.drawCircle(standColor, radius = p * 2.5f, center = Offset(cx, cy), style = Stroke(width = p * 0.6f))

        // Diagonal glass blade
        if (orientation == MirrorOrientation.SLASH) {
            // '/' from bottom-left to top-right
            val start = Offset(x + p * 1.5f, y + tileSize - p * 1.5f)
            val end = Offset(x + tileSize - p * 1.5f, y + p * 1.5f)
            scope.drawLine(glassGleam, start, end, strokeWidth = p * 1.2f)
            scope.drawLine(glassBright, start, end, strokeWidth = p * 0.5f)
        } else {
            // '\' from top-left to bottom-right
            val start = Offset(x + p * 1.5f, y + p * 1.5f)
            val end = Offset(x + tileSize - p * 1.5f, y + tileSize - p * 1.5f)
            scope.drawLine(glassGleam, start, end, strokeWidth = p * 1.2f)
            scope.drawLine(glassBright, start, end, strokeWidth = p * 0.5f)
        }
    }
}
