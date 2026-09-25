package com.example.model

enum class MirrorOrientation {
    SLASH,     // '/' reflects UP <-> RIGHT, DOWN <-> LEFT
    BACKSLASH  // '\' reflects UP <-> LEFT, DOWN <-> RIGHT
}

enum class TileType {
    WALL,
    FLOOR,
    TARGET,       // Socket for crates
    ICE,          // Slippery ice surface
    WATER,        // Pit/Water (crates turn into wooden bridge)
    SPIKE,        // Toggles active/inactive
    PORTAL_A,     // Teleport pair A
    PORTAL_B,     // Teleport pair B
    EMITTER,      // Shoots laser beam
    RECEIVER,     // Activates when laser hits it
    MIRROR,       // Reflects laser (has orientation)
    DOOR_LOCKED,  // Requires iron key
    DOOR_GOLD,    // Requires gold key
    KEY_IRON,     // Collectible iron key
    KEY_GOLD,     // Collectible gold key
    CHEST,        // Relic treasure chest
    EXIT_GATE     // Exit to next level / victory
}

enum class CrateType {
    WOOD,     // Standard pushable crate
    ICE,      // Slides continuously until blocked
    GEM_RUBY, // Crimson power gem
    GEM_CYAN  // Cyan mystic gem
}
