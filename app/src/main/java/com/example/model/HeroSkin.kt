package com.example.model

enum class HeroSkin(
    val id: String,
    val displayName: String,
    val title: String,
    val costCoins: Int,
    val primaryColorHex: Long,
    val secondaryColorHex: Long,
    val visorColorHex: Long,
    val plumeColorHex: Long,
    val lore: String
) {
    SILVER_KNIGHT(
        id = "skin_silver",
        displayName = "Silver Knight",
        title = "Crypt Explorer",
        costCoins = 0,
        primaryColorHex = 0xFFD6D6E6,
        secondaryColorHex = 0xFF2563EB,
        visorColorHex = 0xFFFFD13B,
        plumeColorHex = 0xFFFF3366,
        lore = "The standard-issue shining platemail of the Royal Cryptguard."
    ),
    GOLDEN_PALADIN(
        id = "skin_gold",
        displayName = "Golden Paladin",
        title = "Sun Sanctum",
        costCoins = 30,
        primaryColorHex = 0xFFFFD13B,
        secondaryColorHex = 0xFFFF9E2C,
        visorColorHex = 0xFF00E5FF,
        plumeColorHex = 0xFFFFFFFF,
        lore = "Forged in consecrated solar fire, immune to dark dungeon curses."
    ),
    SHADOW_STALKER(
        id = "skin_shadow",
        displayName = "Shadow Stalker",
        title = "Void Shinobi",
        costCoins = 60,
        primaryColorHex = 0xFF1E2337,
        secondaryColorHex = 0xFFA855F7,
        visorColorHex = 0xFFFF3366,
        plumeColorHex = 0xFFA855F7,
        lore = "A master of spatial voids who moves silently through pitch black crypts."
    ),
    FROST_VALKYRIE(
        id = "skin_frost",
        displayName = "Frost Valkyrie",
        title = "Glacier Champion",
        costCoins = 90,
        primaryColorHex = 0xFFE0F7FA,
        secondaryColorHex = 0xFF00E5FF,
        visorColorHex = 0xFF00E676,
        plumeColorHex = 0xFF80D8FF,
        lore = "Armor harvested from permafrost glaciers of World 2 Frost Glade."
    ),
    CRIMSON_WARLORD(
        id = "skin_crimson",
        displayName = "Crimson Warlord",
        title = "Blood Sigil",
        costCoins = 130,
        primaryColorHex = 0xFFFF3366,
        secondaryColorHex = 0xFF4A0E17,
        visorColorHex = 0xFFFFD13B,
        plumeColorHex = 0xFFFFD13B,
        lore = "Ancient conqueror of the catacombs whose strike echoes through stone."
    ),
    CELESTIAL_MAGE(
        id = "skin_celestial",
        displayName = "Celestial Archon",
        title = "Star Weaver",
        costCoins = 180,
        primaryColorHex = 0xFF3B82F6,
        secondaryColorHex = 0xFF8B5CF6,
        visorColorHex = 0xFF00E5FF,
        plumeColorHex = 0xFFEC4899,
        lore = "Blessed by the astral heavens to decipher every ancient puzzle mechanism."
    )
}
