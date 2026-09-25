package com.example.model

/**
 * Procedural Crypt Generator that procedurally creates playable Sokoban puzzle rooms
 * with guaranteed features according to World theme and level index.
 */
object LevelGenerator {

    fun generateLevel(worldId: Int, levelIndex: Int): GameLevel {
        val levelNumber = (worldId - 1) * 20 + levelIndex
        val seed = (worldId * 1000L + levelIndex * 37L + 2026L)
        val rng = java.util.Random(seed)

        val name = when (worldId) {
            1 -> listOf(
                "Stone Corridor", "Chamber of Dust", "Ancient Pillars", "Crypt Passage",
                "Forgotten Vestibule", "Tomb of Kings", "Scribe's Archive", "Shadow Hall",
                "Granite Crypt", "Pillar of Ages", "Rune Keep", "Iron Gate",
                "The Vault", "Silent Sentry", "Old Catacomb", "Sunken Room",
                "Relic Arch", "Cobblestone Maze", "Royal Antechamber", "King's Crypt"
            ).getOrElse(levelIndex - 1) { "Catacomb Depths $levelIndex" }

            2 -> listOf(
                "Glacier Entry", "Frozen Walkway", "Ice Spire", "Subzero Cavern",
                "Slippery Chasm", "Chill Basin", "Permafrost Gate", "Crystal Ice",
                "Frozen Crossing", "Cold River", "Ice Stalactite", "Frost Barrier",
                "Glacial Rift", "Deep Frost", "Blizzard Chamber", "Winter Hold",
                "Icebound Vault", "Shivering Halls", "Polar Sanctum", "Valkyrie Throne"
            ).getOrElse(levelIndex - 1) { "Frost Cavern $levelIndex" }

            3 -> listOf(
                "Beam Portal", "Prism Chamber", "Mirror Path", "Solar Core",
                "Light Matrix", "Crystal Hall", "Spectral Vault", "Radiant Corridor",
                "Reflector Arch", "Luminous Nave", "Light Conduit", "Prismatic Step",
                "Laser Foundry", "Optical Gate", "Radiant Focus", "Crystal Forge",
                "Sunfire Keep", "Prism Matrix", "Solar Forge", "Apex Crystal"
            ).getOrElse(levelIndex - 1) { "Light Foundry $levelIndex" }

            4 -> listOf(
                "Void Threshold", "Spike Corridor", "Portal Rift", "Shadow Nexus",
                "Aether Chamber", "Temporal Gate", "Cosmic Rift", "Warp Vestibule",
                "Void Cascade", "Dark Antechamber", "Singularity", "Spike Gauntlet",
                "Twin Vortex", "Aether Citadel", "Chaos Sanctum", "Astral Core",
                "Rift Sanctuary", "Abyssal Gate", "Cosmic Crucible", "Cryptmaster Spire"
            ).getOrElse(levelIndex - 1) { "Void Domain $levelIndex" }

            else -> "Lost Crypt Floor $levelNumber"
        }

        val rows = generateMapRows(worldId, levelIndex, rng)

        val basePar = 6 + (levelIndex * 1.5).toInt() + (worldId * 2)

        return GameLevel(
            id = "w${worldId}_l${levelIndex}",
            worldId = worldId,
            name = name,
            description = "World $worldId - Level $levelIndex. Solve the ancient mechanism to advance.",
            parMoves = basePar,
            mapRows = rows
        )
    }

    private fun generateMapRows(worldId: Int, levelIndex: Int, rng: java.util.Random): List<String> {
        val width = 7 + (levelIndex % 4) // 7 to 10 wide
        val height = 6 + (levelIndex / 5).coerceAtMost(3) // 6 to 8 tall

        val grid = Array(height) { CharArray(width) { '.' } }

        // Outer walls
        for (x in 0 until width) {
            grid[0][x] = '#'
            grid[height - 1][x] = '#'
        }
        for (y in 0 until height) {
            grid[y][0] = '#'
            grid[y][width - 1] = '#'
        }

        // Add some inner walls (pillars)
        val numPillars = 1 + (levelIndex % 3)
        for (i in 0 until numPillars) {
            val px = 2 + rng.nextInt(width - 4)
            val py = 2 + rng.nextInt(height - 4)
            grid[py][px] = '#'
        }

        // Player starting pos
        grid[1][1] = '@'

        when (worldId) {
            1 -> {
                // Catacombs: Crates, Targets, Keys, Doors
                val numCrates = if (levelIndex <= 5) 1 else if (levelIndex <= 14) 2 else 3
                placeCratesAndTargets(grid, width, height, numCrates, rng)

                if (levelIndex >= 6 && levelIndex % 3 == 0) {
                    // Add an iron key & door
                    placeKeyAndDoor(grid, width, height, rng)
                }
                if (levelIndex % 7 == 0) {
                    grid[height - 2][width - 2] = 'C' // Chest
                } else {
                    grid[height - 2][width - 2] = 'E' // Exit
                }
            }

            2 -> {
                // Frost: Ice floor patches, Crates, Water chasms
                for (y in 2 until height - 2) {
                    for (x in 2 until width - 2) {
                        if (grid[y][x] == '.') {
                            grid[y][x] = '_' // Ice floor
                        }
                    }
                }
                if (levelIndex % 2 == 0) {
                    // Water tile
                    val wx = width / 2
                    val wy = height / 2
                    if (grid[wy][wx] == '_') grid[wy][wx] = '~'
                }
                placeCratesAndTargets(grid, width, height, if (levelIndex < 10) 1 else 2, rng)
                grid[height - 2][width - 2] = if (levelIndex % 8 == 0) 'C' else 'E'
            }

            3 -> {
                // Light & Mirrors: Emitter '>' on left, Mirrors '\' or '/', Receiver 'R'
                grid[2][1] = '>'
                grid[2][width - 2] = '\\'
                grid[height - 2][width - 2] = 'R'
                // Player at bottom left
                grid[height - 2][1] = '.'
                grid[1][1] = '@'
                // A crate to push as well
                placeCratesAndTargets(grid, width, height, 1, rng)
                grid[height - 2][2] = 'E'
            }

            4 -> {
                // Void: Spikes and portals
                grid[2][2] = '^' // Spike
                if (width >= 8) {
                    grid[height - 3][width - 3] = '^'
                }
                if (levelIndex >= 5) {
                    // Portal 1 pair
                    grid[1][width - 2] = '1'
                    grid[height - 2][2] = '1'
                }
                placeCratesAndTargets(grid, width, height, if (levelIndex < 10) 1 else 2, rng)
                grid[height - 2][width - 2] = if (levelIndex % 6 == 0) 'C' else 'E'
            }

            else -> {
                // Worlds 5 to 10: Mixed dungeon puzzles with crates, targets, and exits
                val crateCount = if (levelIndex < 8) 1 else if (levelIndex < 15) 2 else 3
                placeCratesAndTargets(grid, width, height, crateCount, rng)
                grid[height - 2][width - 2] = if (levelIndex % 5 == 0) 'C' else 'E'
            }
        }

        // Return rows
        return grid.map { String(it) }
    }

    private fun placeCratesAndTargets(
        grid: Array<CharArray>,
        width: Int,
        height: Int,
        count: Int,
        rng: java.util.Random
    ) {
        var placed = 0
        var attempts = 0
        while (placed < count && attempts < 50) {
            attempts++
            val cx = 2 + rng.nextInt(width - 4)
            val cy = 2 + rng.nextInt(height - 4)
            val tx = 2 + rng.nextInt(width - 4)
            val ty = 2 + rng.nextInt(height - 4)

            if (grid[cy][cx] == '.' || grid[cy][cx] == '_') {
                if ((grid[ty][tx] == '.' || grid[ty][tx] == '_') && (cx != tx || cy != ty)) {
                    grid[cy][cx] = 'B'
                    grid[ty][tx] = 'T'
                    placed++
                }
            }
        }
        if (placed == 0) {
            // Fallback safe placement
            grid[2][3] = 'B'
            grid[3][width - 3] = 'T'
        }
    }

    private fun placeKeyAndDoor(grid: Array<CharArray>, width: Int, height: Int, rng: java.util.Random) {
        if (grid[1][width - 3] == '.') grid[1][width - 3] = 'K'
        if (grid[height - 2][width - 3] == '.') grid[height - 2][width - 3] = 'D'
    }
}
