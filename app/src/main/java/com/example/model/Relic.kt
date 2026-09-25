package com.example.model

data class Relic(
    val id: String,
    val name: String,
    val lore: String,
    val iconPixelType: String,
    val worldId: Int
)

object RelicRegistry {
    val ALL_RELICS = listOf(
        Relic(
            id = "relic_ankh",
            name = "Golden Ankh of Ra",
            lore = "Forged by ancient solar monks to channel light across eternal tombs.",
            iconPixelType = "ANKH",
            worldId = 1
        ),
        Relic(
            id = "relic_scarab",
            name = "Emerald Scarab",
            lore = "Carved from jadeite, legends say it points toward hidden dungeon passages.",
            iconPixelType = "SCARAB",
            worldId = 1
        ),
        Relic(
            id = "relic_frost_core",
            name = "Glacial Heart",
            lore = "A perpetual crystal of frost that never melts, salvaged from the ice cavern depths.",
            iconPixelType = "CRYSTAL",
            worldId = 2
        ),
        Relic(
            id = "relic_crown",
            name = "Frozen Crown",
            lore = "Worn by the Frost Sovereign who commanded labyrinthine subterranean glaciers.",
            iconPixelType = "CROWN",
            worldId = 2
        ),
        Relic(
            id = "relic_prism",
            name = "Prism of Refraction",
            lore = "Bends invisible aether beams into radiant spectra capable of awakening ancient machinery.",
            iconPixelType = "PRISM",
            worldId = 3
        ),
        Relic(
            id = "relic_chalice",
            name = "Ruby Chalice",
            lore = "Filled with fiery ambrosia that bestows boundless courage upon puzzle seekers.",
            iconPixelType = "CHALICE",
            worldId = 3
        ),
        Relic(
            id = "relic_compass",
            name = "Aether Void Compass",
            lore = "Spins toward cosmic rifts, aligning inter-dimensional doorways.",
            iconPixelType = "COMPASS",
            worldId = 4
        ),
        Relic(
            id = "relic_codex",
            name = "Cryptmaster's Codex",
            lore = "The master manuscript containing blueprints of all forgotten catacombs.",
            iconPixelType = "BOOK",
            worldId = 4
        )
    )

    fun getRelicById(id: String): Relic? = ALL_RELICS.find { it.id == id }
}
