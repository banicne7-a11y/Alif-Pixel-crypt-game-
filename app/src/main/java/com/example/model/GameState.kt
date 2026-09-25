package com.example.model

data class LaserSegment(
    val from: Position,
    val to: Position,
    val direction: Direction
)

data class UndoSnapshot(
    val playerPos: Position,
    val playerFacing: Direction,
    val crates: Map<Position, CrateType>,
    val mirrors: Map<Position, MirrorOrientation>,
    val ironKeys: Int,
    val goldKeys: Int,
    val unlockedDoors: Set<Position>,
    val waterBridges: Set<Position>,
    val openedChests: Set<Position>,
    val collectedKeys: Set<Position>,
    val stepCount: Int
)

data class GameState(
    val width: Int,
    val height: Int,
    val baseTiles: Map<Position, TileType>,
    val emitterDirections: Map<Position, Direction> = emptyMap(),
    val portalAPairs: List<Position> = emptyList(),
    val portalBPairs: List<Position> = emptyList(),

    // Dynamic state
    val playerPos: Position,
    val playerFacing: Direction = Direction.DOWN,
    val crates: Map<Position, CrateType> = emptyMap(),
    val mirrors: Map<Position, MirrorOrientation> = emptyMap(),
    val ironKeys: Int = 0,
    val goldKeys: Int = 0,
    val unlockedDoors: Set<Position> = emptySet(),
    val waterBridges: Set<Position> = emptySet(),
    val openedChests: Set<Position> = emptySet(),
    val collectedKeys: Set<Position> = emptySet(),
    val stepCount: Int = 0,
    val isCompleted: Boolean = false,
    val isDefeated: Boolean = false,
    val relicAwarded: Relic? = null,
    val parMoves: Int = 20,
    val levelId: String = "",
    val levelName: String = "",
    val worldId: Int = 1,
    val undoStack: List<UndoSnapshot> = emptyList()
) {
    // Spikes toggle active every 2 steps
    val spikesActive: Boolean
        get() = (stepCount % 2 == 1)

    // Targets count & filled count
    val totalTargets: Int by lazy {
        baseTiles.values.count { it == TileType.TARGET }
    }

    val filledTargets: Int
        get() = baseTiles.filter { (pos, tile) ->
            tile == TileType.TARGET && crates.containsKey(pos)
        }.size

    // Calculate laser paths from emitters
    val laserPaths: List<LaserSegment>
        get() {
            val segments = mutableListOf<LaserSegment>()
            val visited = mutableSetOf<Pair<Position, Direction>>()

            emitterDirections.forEach { (emitterPos, initDir) ->
                var currentPos = emitterPos.move(initDir)
                var currentDir = initDir

                while (isInBounds(currentPos) && visited.add(Pair(currentPos, currentDir))) {
                    // Check if stopped by wall, crate, or locked door
                    if (isLaserBlocked(currentPos)) {
                        segments.add(LaserSegment(currentPos.move(currentDir.opposite()), currentPos, currentDir))
                        break
                    }

                    // Check if mirror reflects
                    val mirror = mirrors[currentPos]
                    if (mirror != null) {
                        segments.add(LaserSegment(currentPos.move(currentDir.opposite()), currentPos, currentDir))
                        val reflected = reflect(currentDir, mirror)
                        if (reflected != null) {
                            currentDir = reflected
                            currentPos = currentPos.move(currentDir)
                            continue
                        } else {
                            break // Laser absorbed
                        }
                    }

                    // Check if receiver reached
                    val tile = baseTiles[currentPos]
                    if (tile == TileType.RECEIVER) {
                        segments.add(LaserSegment(currentPos.move(currentDir.opposite()), currentPos, currentDir))
                        break
                    }

                    segments.add(LaserSegment(currentPos.move(currentDir.opposite()), currentPos, currentDir))
                    currentPos = currentPos.move(currentDir)
                }
            }
            return segments
        }

    val isReceiverPowered: Boolean
        get() = laserPaths.any { seg ->
            baseTiles[seg.to] == TileType.RECEIVER
        }

    private fun isLaserBlocked(pos: Position): Boolean {
        if (crates.containsKey(pos)) return true
        val tile = baseTiles[pos]
        if (tile == TileType.WALL) return true
        if (tile == TileType.DOOR_LOCKED && !unlockedDoors.contains(pos)) return true
        if (tile == TileType.DOOR_GOLD && !unlockedDoors.contains(pos)) return true
        return false
    }

    private fun reflect(dir: Direction, mirror: MirrorOrientation): Direction? = when (mirror) {
        MirrorOrientation.SLASH -> when (dir) {
            Direction.UP -> Direction.RIGHT
            Direction.RIGHT -> Direction.UP
            Direction.DOWN -> Direction.LEFT
            Direction.LEFT -> Direction.DOWN
        }
        MirrorOrientation.BACKSLASH -> when (dir) {
            Direction.UP -> Direction.LEFT
            Direction.LEFT -> Direction.UP
            Direction.DOWN -> Direction.RIGHT
            Direction.RIGHT -> Direction.DOWN
        }
    }

    fun isInBounds(pos: Position): Boolean =
        pos.x in 0 until width && pos.y in 0 until height

    fun getEffectiveTile(pos: Position): TileType {
        if (waterBridges.contains(pos)) return TileType.FLOOR
        if (unlockedDoors.contains(pos)) return TileType.FLOOR
        if (collectedKeys.contains(pos)) return TileType.FLOOR
        if (openedChests.contains(pos)) return TileType.FLOOR
        return baseTiles[pos] ?: TileType.WALL
    }

    fun canWalkOn(pos: Position): Boolean {
        if (!isInBounds(pos)) return false
        if (crates.containsKey(pos)) return false
        val tile = getEffectiveTile(pos)
        return when (tile) {
            TileType.WALL -> false
            TileType.WATER -> false
            TileType.RECEIVER -> false
            TileType.EMITTER -> false
            TileType.DOOR_LOCKED -> ironKeys > 0
            TileType.DOOR_GOLD -> goldKeys > 0
            TileType.SPIKE -> !spikesActive
            else -> true
        }
    }

    fun calculateStars(): Int {
        if (!isCompleted) return 0
        return when {
            stepCount <= parMoves -> 3
            stepCount <= (parMoves * 1.5).toInt() -> 2
            else -> 1
        }
    }
}
