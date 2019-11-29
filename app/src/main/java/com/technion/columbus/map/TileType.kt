package com.technion.columbus.map

enum class TileType(val id: Int, val colidable: Boolean) {
    PLAYER_DOWN(257, true),

    FLOOR(20, false),

    OBSTACLE(1, true),

    WALL_UP(4, true),
    WALL_LEFT(19, true),
    WALL_DOWN(36, true),
    WALL_RIGHT(21, true),
    WALL_LEFT_UP(3, true),
    WALL_LEFT_DOWN(35, true),
    WALL_RIGHT_DOWN(37, true),
    WALL_RIGHT_UP(5, true),
}