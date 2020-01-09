package com.technion.columbus.main

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.core.content.ContextCompat
import com.technion.columbus.R
import com.technion.columbus.ScanListActivity
import com.technion.columbus.map.GdxLauncher
import com.technion.columbus.utility.changeStatusBarColor
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    companion object {
        const val TAG = "Columbus"
    }

    private val floors = listOf(
        R.drawable.floor_tile,
        R.drawable.checkered_floor_tile
    )
    private val walls = listOf(R.drawable.wall_tile)
    private val robots = listOf(
        R.drawable.dog_spin_animation,
        R.drawable.cat_spin_animation,
        R.drawable.chicken_spin_animation,
        R.drawable.horse_spin_animation,
        R.drawable.sheep_spin_animation
    )

    private lateinit var floorSlotPicker: TileSlotPicker
    private lateinit var wallSlotPicker: TileSlotPicker
    private lateinit var robotSlotPicker: TileSlotPicker

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        changeStatusBarColor(this, ContextCompat.getColor(this, R.color.colorPrimary))

        scanButton.setOnClickListener {
            startActivity(Intent(this, GdxLauncher::class.java))
        }
        previousScansButton.setOnClickListener {
            startActivity(Intent(this, ScanListActivity::class.java))
        }

        floorSlotPicker = TileSlotPicker(
            floorTileBox,
            getString(R.string.floor_slot_picker),
            floors
        )
        wallSlotPicker = TileSlotPicker(
            wallTileBox,
            getString(R.string.wall_slot_picker),
            walls
        )
        robotSlotPicker = TileSlotPicker(
            robotTileBox,
            getString(R.string.robot_slot_picker),
            robots,
            isAnimated = true
        )
    }
}
