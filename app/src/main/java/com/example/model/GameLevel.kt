package com.example.model

data class GameLevel(
    val id: String,
    val worldId: Int,
    val name: String,
    val description: String,
    val parMoves: Int,
    val mapRows: List<String>
) {
    fun toInitialState(): GameState {
        val height = mapRows.size
        val width = mapRows.maxOfOrNull { it.length } ?: 0

        val baseTiles = mutableMapOf<Position, TileType>()
        val crates = mutableMapOf<Position, CrateType>()
        val mirrors = mutableMapOf<Position, MirrorOrientation>()
        val emitterDirections = mutableMapOf<Position, Direction>()
        val portalAPairs = mutableListOf<Position>()
        val portalBPairs = mutableListOf<Position>()

        var playerPos = Position(1, 1)

        for (y in 0 until height) {
            val row = mapRows[y]
            for (x in 0 until width) {
                val ch = if (x < row.length) row[x] else '#'
                val pos = Position(x, y)

                when (ch) {
                    '#' -> baseTiles[pos] = TileType.WALL
                    '.' -> baseTiles[pos] = TileType.FLOOR
                    'T' -> baseTiles[pos] = TileType.TARGET
                    '@' -> {
                        baseTiles[pos] = TileType.FLOOR
                        playerPos = pos
                    }
                    '+' -> {
                        baseTiles[pos] = TileType.TARGET
                        playerPos = pos
                    }
                    'B', '$' -> {
                        baseTiles[pos] = TileType.FLOOR
                        crates[pos] = CrateType.WOOD
                    }
                    'X', '*' -> {
                        baseTiles[pos] = TileType.TARGET
                        crates[pos] = CrateType.WOOD
                    }
                    'I' -> {
                        baseTiles[pos] = TileType.FLOOR
                        crates[pos] = CrateType.ICE
                    }
                    '~' -> baseTiles[pos] = TileType.WATER
                    '_' -> baseTiles[pos] = TileType.ICE
                    '^' -> baseTiles[pos] = TileType.SPIKE
                    'K' -> baseTiles[pos] = TileType.KEY_IRON
                    'G' -> baseTiles[pos] = TileType.KEY_GOLD
                    'D' -> baseTiles[pos] = TileType.DOOR_LOCKED
                    'O' -> baseTiles[pos] = TileType.DOOR_GOLD
                    'C' -> baseTiles[pos] = TileType.CHEST
                    'E' -> baseTiles[pos] = TileType.EXIT_GATE
                    '1' -> {
                        baseTiles[pos] = TileType.PORTAL_A
                        portalAPairs.add(pos)
                    }
                    '2' -> {
                        baseTiles[pos] = TileType.PORTAL_B
                        portalBPairs.add(pos)
                    }
                    '>' -> {
                        baseTiles[pos] = TileType.EMITTER
                        emitterDirections[pos] = Direction.RIGHT
                    }
                    '<' -> {
                        baseTiles[pos] = TileType.EMITTER
                        emitterDirections[pos] = Direction.LEFT
                    }
                    'v' -> {
                        baseTiles[pos] = TileType.EMITTER
                        emitterDirections[pos] = Direction.DOWN
                    }
                    'V' -> {
                        baseTiles[pos] = TileType.EMITTER
                        emitterDirections[pos] = Direction.UP
                    }
                    'R' -> baseTiles[pos] = TileType.RECEIVER
                    '/' -> {
                        baseTiles[pos] = TileType.FLOOR
                        mirrors[pos] = MirrorOrientation.SLASH
                    }
                    '\\' -> {
                        baseTiles[pos] = TileType.FLOOR
                        mirrors[pos] = MirrorOrientation.BACKSLASH
                    }
                    else -> baseTiles[pos] = TileType.FLOOR
                }
            }
        }

        return GameState(
            width = width,
            height = height,
            baseTiles = baseTiles,
            emitterDirections = emitterDirections,
            portalAPairs = portalAPairs,
            portalBPairs = portalBPairs,
            playerPos = playerPos,
            crates = crates,
            mirrors = mirrors,
            parMoves = parMoves,
            levelId = id,
            levelName = name,
            worldId = worldId
        )
    }

    companion object {
        fun encodeToString(name: String, parMoves: Int, rows: List<String>): String {
            val content = rows.joinToString(";")
            return "PX1|$name|$parMoves|$content"
        }

        fun decodeFromString(code: String): GameLevel? {
            try {
                if (!code.startsWith("PX1|")) return null
                val parts = code.split("|")
                if (parts.size < 4) return null
                val name = parts[1]
                val par = parts[2].toIntOrNull() ?: 25
                val rows = parts[3].split(";")
                return GameLevel(
                    id = "custom_${System.currentTimeMillis()}",
                    worldId = 0,
                    name = name,
                    description = "Custom Player Dungeon",
                    parMoves = par,
                    mapRows = rows
                )
            } catch (e: Exception) {
                return null
            }
        }
    }
}
