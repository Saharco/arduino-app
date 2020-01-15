package com.technion.columbus.scans

import android.app.ProgressDialog
import android.content.Intent
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.storage.FirebaseStorage
import com.technion.columbus.R
import com.technion.columbus.pojos.MapMatrix
import com.technion.columbus.pojos.Scan
import com.technion.columbus.utility.MAP_UPLOAD
import com.technion.columbus.utility.SCAN
import com.technion.columbus.utility.stringToTileResource
import java.text.SimpleDateFormat
import java.util.*

class ScanCardViewHolder(v: View) : RecyclerView.ViewHolder(v) {
    private var scanTitle: TextView = v.findViewById(R.id.scan_title)
    private var scanDate: TextView = v.findViewById(R.id.date_text)
    private var scanTime: TextView = v.findViewById(R.id.time_text)
    private var floorTile: ImageView = v.findViewById(R.id.floor_tile)
    private var wallTile: ImageView = v.findViewById(R.id.wall_tile)
    private var robotTile: ImageView = v.findViewById(R.id.robot_tile)
    private var card: CardView = v.findViewById(R.id.scan_card)

    fun populateUI(scan: Scan) {
        val dateFormat = SimpleDateFormat("dd.MM.yyyy")
        val hourFormat = SimpleDateFormat("HH:mm")
        val date = Date(scan.timestamp!!.time)

        scanTitle.text = scan.scanName
        scanDate.text = dateFormat.format(date)
        scanTime.text = hourFormat.format(date)
        floorTile.setImageResource(stringToTileResource(scan.floorTileName))
        wallTile.setImageResource(stringToTileResource(scan.wallTileName))
        robotTile.setImageResource(stringToTileResource(scan.robotTileName))
    }

    private fun setNavigationToInfoActivity(scan: Scan, scanListActivity: ScanListActivity?) {
        card.setOnClickListener { button ->
            if (scanListActivity != null)
                showLoadingDialog(scanListActivity)
            FirebaseStorage.getInstance()
                .reference
                .child("map_grids")
                .child(scan.mapGridId!!)
                .getBytes(8000000L)
                .addOnSuccessListener {
                    val mapMatrix = MapMatrix.deserialize(it)
                    val scanInfoIntent = Intent(button.context, ScanInfoActivity::class.java)
                    scanInfoIntent.putExtra(SCAN, scan)
                    scanInfoIntent.putExtra(MAP_UPLOAD, mapMatrix)
                    button.context.startActivity(scanInfoIntent)
                }
        }
    }

    private fun showLoadingDialog(scanListActivity: ScanListActivity) {
        scanListActivity.loadingGameProgressDialog = ProgressDialog.show(
            scanListActivity,
            scanListActivity.getString(R.string.loading_stored_scan_game_title),
            scanListActivity.getString(R.string.loading_stored_scan_game_message)
        )

        scanListActivity.loadingGameProgressDialog!!.isIndeterminate = true
    }

    fun bind(scan: Scan, scanListActivity: ScanListActivity? = null) {
        populateUI(scan)
        setNavigationToInfoActivity(scan, scanListActivity)
    }
}