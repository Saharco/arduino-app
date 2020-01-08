package com.technion.columbus

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.technion.columbus.map.GdxLauncher
import com.technion.columbus.utility.TileSlotPicker
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    companion object {
        const val TAG = "Columbus"
    }

    val floors = listOf(R.drawable.floor_tile, R.drawable.checkered_floor_tile)
    val walls = listOf(R.drawable.wall_tile)
    val robots = listOf(R.drawable.dog_tile, R.drawable.cat_tile, R.drawable.chicken_tile)

    lateinit var floorSlotPicker: TileSlotPicker
    lateinit var wallSlotPicker: TileSlotPicker
    lateinit var robotSlotPicker: TileSlotPicker

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        scanButton.setOnClickListener {
            startActivity(Intent(this, GdxLauncher::class.java))
        }
        previousScansButton.setOnClickListener {
            startActivity(Intent(this, ScanListActivity::class.java))
        }

        floorSlotPicker = TileSlotPicker(applicationContext, floorTileBox, floors)
        wallSlotPicker = TileSlotPicker(applicationContext, wallTileBox, walls)
        robotSlotPicker = TileSlotPicker(applicationContext, robotTileBox, robots)
    }
}
