package com.technion.columbus

import android.content.Intent
import android.graphics.drawable.Drawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.activity_scan_list.*
import kotlinx.android.synthetic.main.scan_card.*

class ScanListActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var emptyListView: View
    private lateinit var viewAdapter: ScanCardAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_scan_list)

        emptyListView = findViewById(R.id.empty_feed_view)
        val viewManager = LinearLayoutManager(this)
        viewManager.orientation = LinearLayoutManager.VERTICAL

        val dummyDataSet = createDummyDataSet()
        viewAdapter = ScanCardAdapter(dummyDataSet)

        recyclerView = findViewById<RecyclerView>(R.id.scan_list).apply {
            setHasFixedSize(true)
            layoutManager = viewManager
            adapter = viewAdapter
        }

        setSupportActionBar(previousScansToolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)
    }

    private fun createDummyDataSet(): List<ScanCardInfo> {
        val blackFloorTile = applicationContext.resources.getIdentifier("floor_tile", "drawable", applicationContext.packageName)
        val checkeredFloorTile = applicationContext.resources.getIdentifier("checkered_floor_tile", "drawable", applicationContext.packageName)
        val wallTile = applicationContext.resources.getIdentifier("wall_tile", "drawable", applicationContext.packageName)
        val dogTile = applicationContext.resources.getIdentifier("dog_tile", "drawable", applicationContext.packageName)
        val catTile = applicationContext.resources.getIdentifier("cat_tile", "drawable", applicationContext.packageName)
        val chickenTile = applicationContext.resources.getIdentifier("chicken_tile", "drawable", applicationContext.packageName)
        val list = mutableListOf<ScanCardInfo>()
        list.add(ScanCardInfo("Taub 9", "01/12/2019", "15:00", blackFloorTile, wallTile, dogTile))
        list.add(ScanCardInfo("Ullman 614", "31/11/2019", "16:30", checkeredFloorTile, wallTile, catTile))
        list.add(ScanCardInfo("Taub 313", "31/11/2019", "12:30", checkeredFloorTile, wallTile, chickenTile))
        list.add(ScanCardInfo("My big and magnificent dormitory room with astonishing view to the Haifa bay",
            "30/11/2019", "21:00", blackFloorTile, wallTile, catTile))
        return list
    }
}


// Temporary dummy classes for testing the Recycler View

data class ScanCardInfo(
    var scanTitle: String,
    var scanDate: String,
    var scanTime: String,
    var floorTile: Int,
    var wallTile: Int,
    var robotTile: Int)

class ScanCardViewHolder(v: View): RecyclerView.ViewHolder(v) {
    var scanTitle: TextView = v.findViewById(R.id.scan_title)
    var scanDate: TextView = v.findViewById(R.id.date_text)
    var scanTime: TextView = v.findViewById(R.id.time_text)
    var floorTile: ImageView = v.findViewById(R.id.floor_tile)
    var wallTile: ImageView = v.findViewById(R.id.wall_tile)
    var robotTile: ImageView = v.findViewById(R.id.robot_tile)
    var card: CardView = v.findViewById(R.id.scan_card)
}

class ScanCardAdapter(private val list: List<ScanCardInfo>): RecyclerView.Adapter<ScanCardViewHolder>() {
    override fun getItemCount(): Int  = list.size

    override fun onBindViewHolder(holder: ScanCardViewHolder, position: Int) {
        val item = list[position]
        holder.scanTitle.text = item.scanTitle
        holder.scanDate.text = item.scanDate
        holder.scanTime.text = item.scanTime
        holder.floorTile.setImageResource(item.floorTile)
        holder.wallTile.setImageResource(item.wallTile)
        holder.robotTile.setImageResource(item.robotTile)
        holder.card.setOnClickListener {
            val intent = Intent(it.context, ScanInfoActivity::class.java)
            it.context.startActivity(intent)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ScanCardViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.scan_card, parent, false)
        return ScanCardViewHolder(itemView)
    }
}
