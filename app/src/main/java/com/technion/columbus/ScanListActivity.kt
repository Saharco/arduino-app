package com.technion.columbus

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

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
    }

    private fun createDummyDataSet(): List<ScanCardInfo> {
        val list = mutableListOf<ScanCardInfo>()
        list.add(ScanCardInfo("Taub 9", "01/12/2019", "15:00"))
        list.add(ScanCardInfo("Ullman 614", "31/11/2019", "12:30"))
        list.add(ScanCardInfo("Taub 313", "31/11/2019", "16:30"))
        list.add(ScanCardInfo("My big and magnificent dormitory room with astonishing view to the Haifa bay",
            "30/11/2019", "21:00"))
        return list
    }
}


// Temporary dummy classes for testing the Recycler View

data class ScanCardInfo(
    var scanTitle: String,
    var scanDate: String,
    var scanTime: String)

class ScanCardViewHolder(v: View): RecyclerView.ViewHolder(v) {
    var scanTitle: TextView = v.findViewById(R.id.scan_title)
    var scanDate: TextView = v.findViewById(R.id.date_text)
    var scanTime: TextView = v.findViewById(R.id.time_text)
}

class ScanCardAdapter(private val list: List<ScanCardInfo>): RecyclerView.Adapter<ScanCardViewHolder>() {
    override fun getItemCount(): Int  = list.size

    override fun onBindViewHolder(holder: ScanCardViewHolder, position: Int) {
        val item = list[position]
        holder.scanTitle.text = item.scanTitle
        holder.scanDate.text = item.scanDate
        holder.scanTime.text = item.scanTime
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ScanCardViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.scan_card, parent, false)
        return ScanCardViewHolder(itemView)
    }
}
