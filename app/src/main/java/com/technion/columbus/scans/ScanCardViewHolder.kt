package com.technion.columbus.scans

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.technion.columbus.R

class ScanCardViewHolder(v: View): RecyclerView.ViewHolder(v) {
    var scanTitle: TextView = v.findViewById(R.id.scan_title)
    var scanDate: TextView = v.findViewById(R.id.date_text)
    var scanTime: TextView = v.findViewById(R.id.time_text)
    var floorTile: ImageView = v.findViewById(R.id.floor_tile)
    var wallTile: ImageView = v.findViewById(R.id.wall_tile)
    var robotTile: ImageView = v.findViewById(R.id.robot_tile)
    var card: CardView = v.findViewById(R.id.scan_card)
}