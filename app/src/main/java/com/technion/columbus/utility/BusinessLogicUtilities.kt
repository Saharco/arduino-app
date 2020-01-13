package com.technion.columbus.utility

import com.technion.columbus.R

fun stringToTileResource(tileName: String): Int {
    return when (tileName) {
        PLAYER_DOG -> R.drawable.dog_front_idle
        PLAYER_CAT -> R.drawable.cat_front_idle
        PLAYER_CHICKEN -> R.drawable.chicken_front_idle
        PLAYER_HORSE -> R.drawable.horse_front_idle
        PLAYER_SHEEP -> R.drawable.sheep_front_idle

        WALL_WOOD_1 -> R.drawable.wall_tile

        FLOOR_BLACK -> R.drawable.floor_tile
        FLOOR_CHECKERED -> R.drawable.checkered_floor_tile

        else -> R.drawable.floor_tile
    }
}

fun tileResourceToString(tileResource: Int): String {
    return when (tileResource) {
        R.drawable.dog_spin_animation -> PLAYER_DOG
        R.drawable.cat_spin_animation -> PLAYER_CAT
        R.drawable.chicken_spin_animation -> PLAYER_CHICKEN
        R.drawable.horse_spin_animation -> PLAYER_HORSE
        R.drawable.sheep_spin_animation -> PLAYER_SHEEP

        R.drawable.dog_front_idle -> PLAYER_DOG
        R.drawable.cat_front_idle -> PLAYER_CAT
        R.drawable.chicken_front_idle -> PLAYER_CHICKEN
        R.drawable.horse_front_idle -> PLAYER_HORSE
        R.drawable.sheep_front_idle -> PLAYER_SHEEP

        R.drawable.wall_tile -> WALL_WOOD_1

        R.drawable.floor_tile -> FLOOR_BLACK
        R.drawable.checkered_floor_tile -> FLOOR_CHECKERED

        else -> FLOOR_BLACK
    }
}