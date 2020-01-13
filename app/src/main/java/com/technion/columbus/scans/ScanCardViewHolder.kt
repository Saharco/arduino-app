package com.technion.columbus.scans

import android.content.Intent
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.technion.columbus.R
import com.technion.columbus.pojos.Scan
import com.technion.columbus.utility.stringToTileResource
import java.text.SimpleDateFormat
import java.util.*

class ScanCardViewHolder(v: View): RecyclerView.ViewHolder(v) {
    var scanTitle: TextView = v.findViewById(R.id.scan_title)
    var scanDate: TextView = v.findViewById(R.id.date_text)
    var scanTime: TextView = v.findViewById(R.id.time_text)
    var floorTile: ImageView = v.findViewById(R.id.floor_tile)
    var wallTile: ImageView = v.findViewById(R.id.wall_tile)
    var robotTile: ImageView = v.findViewById(R.id.robot_tile)
    var card: CardView = v.findViewById(R.id.scan_card)

    fun bind(scan: Scan) {
        val dateFormat = SimpleDateFormat("dd.MM.yyyy")
        val hourFormat = SimpleDateFormat("HH:mm")
        val date = Date(scan.timestamp!!.time)

        scanTitle.text = scan.scanName
        scanDate.text = dateFormat.format(date)
        scanTime.text = hourFormat.format(date)
        floorTile.setImageResource(stringToTileResource(scan.floorTileName))
        wallTile.setImageResource(stringToTileResource(scan.wallTileName))
        robotTile.setImageResource(stringToTileResource(scan.robotTileName))
        card.setOnClickListener {
            val intent = Intent(it.context, ScanInfoActivity::class.java)
            it.context.startActivity(intent)
        }
    }
}