package com.example.model

data class WorldInfo(
    val id: Int,
    val name: String,
    val subtitle: String,
    val description: String,
    val accentColorHex: Long
)

object LevelCatalog {

    val WORLDS = listOf(
        WorldInfo(1, "The Catacombs", "Forgotten Crypt", "Push heavy stone crates onto ancient pressure plates and unlock sealed doors.", 0xFFFFD13B),
        WorldInfo(2, "Frost Glade", "Subzero Labyrinth", "Slide across frictionless ice tiles and bridge subterranean icy chasms.", 0xFF00E5FF),
        WorldInfo(3, "Crystal Foundry", "Sanctum of Light", "Align prismatic mirrors to guide radiant light beams into power crystals.", 0xFFFF3366),
        WorldInfo(4, "Void Sanctum", "Aether Citadel", "Navigate temporal spike traps and step through cosmic warp gates.", 0xFFA855F7),
        WorldInfo(5, "Lava Caldera", "Molten Bastion", "Cross cooling volcanic bedrock avoiding burning magma chasms.", 0xFFFF5722),
        WorldInfo(6, "Emerald Labyrinth", "Verdant Tomb", "Explore overgrown ruins filled with hidden switches and ancient glyphs.", 0xFF00E676),
        WorldInfo(7, "Celestial Spire", "Star Forge", "Harness dual laser arrays and prismatic splitters under starry skies.", 0xFF3B82F6),
        WorldInfo(8, "Shadow Necropolis", "Wraith Keep", "Master flickering temporal pressure pads in deep subterranean halls.", 0xFF8B5CF6),
        WorldInfo(9, "Obsidian Core", "Infernal Deep", "High-complexity sliding block puzzles with multi-tiered water bridges.", 0xFFEC4899),
        WorldInfo(10, "Aether Citadel", "Pinnacle of Mastery", "The supreme trial of 200 rooms. Prove your true cryptmaster status!", 0xFFFFD13B)
    )

    val CAMPAIGN_LEVELS: List<GameLevel> = listOf(
        // ================= WORLD 1: THE CATACOMBS =================
        GameLevel(
            id = "w1_l1",
            worldId = 1,
            name = "First Steps",
            description = "Push the wooden crate onto the glowing pressure plate.",
            parMoves = 4,
            mapRows = listOf(
                "######",
                "#@ BT#",
                "######"
            )
        ),
        GameLevel(
            id = "w1_l2",
            worldId = 1,
            name = "Dual Pillars",
            description = "Navigate both crates to activate the dungeon mechanisms.",
            parMoves = 8,
            mapRows = listOf(
                "#######",
                "#..T..#",
                "#.@B..#",
                "#..B..#",
                "#..T..#",
                "#######"
            )
        ),
        GameLevel(
            id = "w1_l3",
            worldId = 1,
            name = "The Turnabout",
            description = "Plan your angles carefully to avoid jamming the stone against the wall.",
            parMoves = 12,
            mapRows = listOf(
                "########",
                "#@...#T#",
                "#.##.#.#",
                "#.B..B.#",
                "#...#..#",
                "#T.....#",
                "########"
            )
        ),
        GameLevel(
            id = "w1_l4",
            worldId = 1,
            name = "Iron Keyhole",
            description = "Find the iron key to bypass the heavy dungeon gate.",
            parMoves = 14,
            mapRows = listOf(
                "#########",
                "#K#.....#",
                "#.#.#.#.#",
                "#@..DBT.#",
                "#########"
            )
        ),
        GameLevel(
            id = "w1_l5",
            worldId = 1,
            name = "Pharaoh's Cache",
            description = "Open the secret relic chest before pushing the final block.",
            parMoves = 16,
            mapRows = listOf(
                "#########",
                "#C#...#T#",
                "#K#.#.#.#",
                "#@..D.B.#",
                "#.#.#BT.#",
                "#########"
            )
        ),
        GameLevel(
            id = "w1_l6",
            worldId = 1,
            name = "Catacomb Vault",
            description = "Master the tight corridors to claim the sacred Golden Ankh.",
            parMoves = 22,
            mapRows = listOf(
                "##########",
                "#T...#..T#",
                "#.#..B..##",
                "#@B..#..B#",
                "#.##.##.T#",
                "#C.......#",
                "##########"
            )
        ),

        // ================= WORLD 2: FROST GLADE =================
        GameLevel(
            id = "w2_l1",
            worldId = 2,
            name = "Slippery Slope",
            description = "Step onto the smooth ice and glide toward the exit.",
            parMoves = 6,
            mapRows = listOf(
                "########",
                "#@____E#",
                "########"
            )
        ),
        GameLevel(
            id = "w2_l2",
            worldId = 2,
            name = "Chasm Bridge",
            description = "Push the stone block into the icy pool to create a safe bridge.",
            parMoves = 8,
            mapRows = listOf(
                "#########",
                "#@.B~.TE#",
                "#########"
            )
        ),
        GameLevel(
            id = "w2_l3",
            worldId = 2,
            name = "Glacial Slide",
            description = "Launch the ice block across the frosty cavern.",
            parMoves = 10,
            mapRows = listOf(
                "#########",
                "#@.I___T#",
                "#...#...#",
                "#...E...#",
                "#########"
            )
        ),
        GameLevel(
            id = "w2_l4",
            worldId = 2,
            name = "Frozen Pillars",
            description = "Use the solid rocks as stoppers to steer your slide.",
            parMoves = 14,
            mapRows = listOf(
                "#########",
                "#@____._#",
                "#_##__#_#",
                "#_____B_#",
                "#_#_T#_E#",
                "#########"
            )
        ),
        GameLevel(
            id = "w2_l5",
            worldId = 2,
            name = "Deep Trench",
            description = "Bridge two water gaps using calculated crate placement.",
            parMoves = 18,
            mapRows = listOf(
                "##########",
                "#@.B~..B~#",
                "#.##.##.T#",
                "#K.D...T.#",
                "#C......E#",
                "##########"
            )
        ),
        GameLevel(
            id = "w2_l6",
            worldId = 2,
            name = "Crown of Ice",
            description = "Overcome slippery momentum to retrieve the Frozen Crown.",
            parMoves = 24,
            mapRows = listOf(
                "###########",
                "#@__#___#T#",
                "#_I_#_B__##",
                "#___~___._#",
                "#_##_##_T_#",
                "#C______E_#",
                "###########"
            )
        ),

        // ================= WORLD 3: CRYSTAL FOUNDRY =================
        GameLevel(
            id = "w3_l1",
            worldId = 3,
            name = "Ray of Light",
            description = "Direct the light emitter into the solar receiver crystal.",
            parMoves = 5,
            mapRows = listOf(
                "#######",
                "#@....#",
                "#>.R.E#",
                "#######"
            )
        ),
        GameLevel(
            id = "w3_l2",
            worldId = 3,
            name = "First Reflection",
            description = "Turn the mirror to bend the light beam toward the target.",
            parMoves = 7,
            mapRows = listOf(
                "#######",
                "#@...R#",
                "#>../.#",
                "#....E#",
                "#######"
            )
        ),
        GameLevel(
            id = "w3_l3",
            worldId = 3,
            name = "Prismatic Zigzag",
            description = "Route the beam around heavy granite pillars.",
            parMoves = 12,
            mapRows = listOf(
                "#########",
                "#@...\\..#",
                "#>...#..#",
                "#..../..#",
                "#.....R.#",
                "#...E...#",
                "#########"
            )
        ),
        GameLevel(
            id = "w3_l4",
            worldId = 3,
            name = "Light & Shadow",
            description = "Blocks cast shadows that disrupt beams. Align your moves.",
            parMoves = 16,
            mapRows = listOf(
                "#########",
                "#@.B....#",
                "#>.../..#",
                "#..#.#..#",
                "#..T....#",
                "#....R.E#",
                "#########"
            )
        ),
        GameLevel(
            id = "w3_l5",
            worldId = 3,
            name = "The Crucible",
            description = "Collect the golden key while manipulating optical prisms.",
            parMoves = 20,
            mapRows = listOf(
                "##########",
                "#@..#G#..#",
                "#>.\\..O..#",
                "#..#..#..#",
                "#../..R..#",
                "#C......E#",
                "##########"
            )
        ),
        GameLevel(
            id = "w3_l6",
            worldId = 3,
            name = "Laser Foundry",
            description = "Synchronize crates and dual reflections to secure the Ruby Chalice.",
            parMoves = 26,
            mapRows = listOf(
                "###########",
                "#@..B..\\..#",
                "#>.#.#.T..#",
                "#../...#..#",
                "#..#...R..#",
                "#C.......E#",
                "###########"
            )
        ),

        // ================= WORLD 4: VOID SANCTUM =================
        GameLevel(
            id = "w4_l1",
            worldId = 4,
            name = "Warp Gate",
            description = "Step through the celestial portal to cross the void.",
            parMoves = 6,
            mapRows = listOf(
                "#########",
                "#@.1#1.TE#",
                "#########"
            )
        ),
        GameLevel(
            id = "w4_l2",
            worldId = 4,
            name = "Spike Timing",
            description = "Spikes toggle on every step. Time your passage carefully.",
            parMoves = 8,
            mapRows = listOf(
                "#########",
                "#@.^.^.T#",
                "#..B.#..#",
                "#########"
            )
        ),
        GameLevel(
            id = "w4_l3",
            worldId = 4,
            name = "Dimensional Transmit",
            description = "Teleport crates across dimensional rifts.",
            parMoves = 14,
            mapRows = listOf(
                "##########",
                "#@.B1#1..#",
                "#..#.##.T#",
                "#........#",
                "##########"
            )
        ),
        GameLevel(
            id = "w4_l4",
            worldId = 4,
            name = "Twin Rifts",
            description = "Coordinate Portal A and Portal B to solve the dual matrix.",
            parMoves = 18,
            mapRows = listOf(
                "###########",
                "#@.1#1..2.#",
                "#.##.##.#.#",
                "#.B..B.2T.#",
                "#T........#",
                "###########"
            )
        ),
        GameLevel(
            id = "w4_l5",
            worldId = 4,
            name = "Void Cavern",
            description = "Avoid active traps while maneuvering three power cubes.",
            parMoves = 24,
            mapRows = listOf(
                "############",
                "#@.^.B..#T.#",
                "#..#.1..#.1#",
                "#.B..#.B...#",
                "#T...^....T#",
                "#C........E#",
                "############"
            )
        ),
        GameLevel(
            id = "w4_l6",
            worldId = 4,
            name = "Cryptmaster Sanctum",
            description = "The ultimate retro trial. Prove your puzzle mastery!",
            parMoves = 30,
            mapRows = listOf(
                "############",
                "#@..B..^..T#",
                "#.1#.##.#..#",
                "#..#.B..#1.#",
                "#G.#.O..#.T#",
                "#K.D.C....E#",
                "############"
            )
        )
    )

    fun getLevelById(id: String): GameLevel? {
        val predefined = CAMPAIGN_LEVELS.find { it.id == id }
        if (predefined != null) return predefined

        // Check if matching w{worldId}_l{levelIndex}
        val match = Regex("""w(\d+)_l(\d+)""").find(id)
        if (match != null) {
            val (worldStr, levelStr) = match.destructured
            val w = worldStr.toIntOrNull() ?: 1
            val l = levelStr.toIntOrNull() ?: 1
            return LevelGenerator.generateLevel(w, l)
        }
        return null
    }

    fun getLevelsForWorld(worldId: Int): List<GameLevel> {
        val predefined = CAMPAIGN_LEVELS.filter { it.worldId == worldId }
        val generated = (1..20).map { lvlIdx ->
            predefined.find { it.id == "w${worldId}_l${lvlIdx}" } ?: LevelGenerator.generateLevel(worldId, lvlIdx)
        }
        return generated
    }

    fun generateDailyLevel(dayOfYear: Int, year: Int): GameLevel {
        val seed = dayOfYear * 31 + year
        val random = java.util.Random(seed.toLong())

        val dailyTemplates = listOf(
            listOf(
                "#########",
                "#@.B....#",
                "#..#.T..#",
                "#.B...T.#",
                "#...#...#",
                "#########"
            ),
            listOf(
                "##########",
                "#@._B__T.#",
                "#_##__##_#",
                "#..I...T.#",
                "#...E....#",
                "##########"
            ),
            listOf(
                "#########",
                "#@..\\...#",
                "#>..#..R#",
                "#.../...#",
                "#..B..T.#",
                "#...E...#",
                "#########"
            ),
            listOf(
                "##########",
                "#@.1#1..T#",
                "#..^.^...#",
                "#.B...B.T#",
                "#...E....#",
                "##########"
            )
        )

        val templateIndex = random.nextInt(dailyTemplates.size)
        val rows = dailyTemplates[templateIndex]

        return GameLevel(
            id = "daily_${year}_${dayOfYear}",
            worldId = 0,
            name = "Daily Crypt Trial #$dayOfYear",
            description = "Daily puzzle challenge for all crypt explorers.",
            parMoves = 15 + random.nextInt(10),
            mapRows = rows
        )
    }
}
