package com.technion.columbus.main

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.RadioButton
import android.widget.SeekBar
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.customview.customView
import com.afollestad.materialdialogs.customview.getCustomView
import com.robinhood.ticker.TickerUtils
import com.robinhood.ticker.TickerView
import com.technion.columbus.R
import com.technion.columbus.ScanListActivity
import com.technion.columbus.map.GdxLauncher
import com.technion.columbus.pojos.MapScanMode
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
            //            startActivity(Intent(this, GdxLauncher::class.java))
            displayConfigurationDialog()
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

    private fun displayConfigurationDialog() {
        val dialog = MaterialDialog(this@MainActivity)
        dialog.cornerRadius(20f)
            .title(R.string.scan_configuration_title)
            .noAutoDismiss()
            .customView(R.layout.fragment_scan_configuration, scrollable = true)

        val dialogView = dialog.getCustomView()

        val scanName = dialogView.findViewById<EditText>(R.id.scanName)

        val controlModeAutomatic = dialogView.findViewById<RadioButton>(R.id.controlModeAutomatic)
        val controlModeManual = dialogView.findViewById<RadioButton>(R.id.controlModeManual)

        val mapModeGame = dialogView.findViewById<RadioButton>(R.id.mapModeGame)
        val mapModeBlueprint = dialogView.findViewById<RadioButton>(R.id.mapModeBlueprint)

        val automaticModeOptions =
            dialogView.findViewById<ConstraintLayout>(R.id.automaticModeOptions)

        val scanRadiusPicker = dialogView.findViewById<TickerView>(R.id.scanRadiusPicker)
        val scanRadiusSeekBar = dialogView.findViewById<SeekBar>(R.id.scanRadiusSeekBar)

        dialog.positiveButton {
            val name = scanName.text.trim()
            if (name.isEmpty()) {
                Toast.makeText(
                    this@MainActivity,
                    getString(R.string.scan_error_no_name),
                    Toast.LENGTH_LONG
                ).show()
                return@positiveButton
            }

            var mapScanMode = MapScanMode.MODE_ILLEGAL
            if (controlModeAutomatic.isChecked && mapModeGame.isChecked) {
                mapScanMode = MapScanMode.MODE_AUTOMATIC_GAME
            } else if (controlModeAutomatic.isChecked && mapModeBlueprint.isChecked) {
                mapScanMode = MapScanMode.MODE_AUTOMATIC_BLUEPRINT
            } else if (controlModeManual.isChecked && mapModeGame.isChecked) {
                mapScanMode = MapScanMode.MODE_MANUAL_GAME
            } else if (controlModeManual.isChecked && mapModeGame.isChecked) {
                mapScanMode = MapScanMode.MODE_MANUAL_BLUEPRINT
            }

            if (mapScanMode == MapScanMode.MODE_ILLEGAL) {
                Toast.makeText(
                    this@MainActivity,
                    getString(R.string.scan_error_no_map_scan_mode),
                    Toast.LENGTH_LONG
                ).show()
                return@positiveButton
            }

            val scanRadius = scanRadiusSeekBar.progress.toFloat() / 100 + 1
            //TODO: dispatch to different maps based on mapScanMode
            dialog.dismiss()
        }

        dialog.negativeButton {
            dialog.dismiss()
        }

        dialog.show {

            scanRadiusPicker.setCharacterLists(TickerUtils.provideNumberList())
            scanRadiusSeekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
                override fun onProgressChanged(
                    seekBar: SeekBar?,
                    progress: Int,
                    fromUser: Boolean
                ) {
                    val distance = progress.toFloat() / 100 + 1
                    scanRadiusPicker.text = String.format("%.2f", distance)
                }

                override fun onStartTrackingTouch(seekBar: SeekBar?) {
                }

                override fun onStopTrackingTouch(seekBar: SeekBar?) {
                }

            })

            val distance = scanRadiusSeekBar.progress.toFloat() / 100 + 1
            scanRadiusPicker.text = String.format("%.2f", distance)

            controlModeAutomatic.setOnClickListener {
                automaticModeOptions.visibility = View.VISIBLE
            }

            controlModeManual.setOnClickListener {
                automaticModeOptions.visibility = View.GONE
            }
        }
    }
}