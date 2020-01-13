package com.technion.columbus.scans

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.firebase.ui.firestore.FirestoreRecyclerAdapter
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.technion.columbus.R
import com.technion.columbus.pojos.Scan
import kotlinx.android.synthetic.main.activity_scan_list.*
import java.text.SimpleDateFormat
import java.util.*

class ScanListActivity : AppCompatActivity() {

    companion object {
        const val TAG = "Columbus/ScanList"
    }

    private lateinit var recyclerView: RecyclerView
    private lateinit var emptyListView: View

    private val db = FirebaseFirestore.getInstance()
    private lateinit var allScansAdapter: FirestoreRecyclerAdapter<Scan, ScanCardViewHolder>
    private lateinit var searchAdapter: RecyclerView.Adapter<ScanCardViewHolder>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_scan_list)

        emptyListView = findViewById(R.id.empty_feed_view)
        val viewManager = LinearLayoutManager(this)
        viewManager.orientation = LinearLayoutManager.VERTICAL

        recyclerView = findViewById<RecyclerView>(R.id.scan_list).apply {
            setHasFixedSize(true)
        }

        setSupportActionBar(previousScansToolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)
    }

    override fun onStart() {
        super.onStart()
        showAllScans()
    }

    override fun onStop() {
        super.onStop()
        //searchView.closeSearch()
        allScansAdapter.stopListening()
    }

    private fun showAllScans() {
        Log.d(TAG, "Showing all previous scans")

        val adapterQuery = db.collection("scans")
            .orderBy("timestamp", Query.Direction.DESCENDING)
        val options = FirestoreRecyclerOptions.Builder<Scan>()
            .setQuery(adapterQuery, Scan::class.java)
            .build()
        allScansAdapter = createFirebaseAdapter(options)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = allScansAdapter
        allScansAdapter.startListening()
    }

    private fun createFirebaseAdapter(options: FirestoreRecyclerOptions<Scan>): FirestoreRecyclerAdapter<Scan, ScanCardViewHolder> {
        return object : FirestoreRecyclerAdapter<Scan, ScanCardViewHolder>(options) {
            override fun onCreateViewHolder(
                parent: ViewGroup,
                viewType: Int
            ): ScanCardViewHolder {
                val itemView =
                    LayoutInflater.from(parent.context).inflate(R.layout.scan_card, parent, false)
                return ScanCardViewHolder(itemView)
            }

            @SuppressLint("SimpleDateFormat")
            override fun onBindViewHolder(
                holder: ScanCardViewHolder,
                position: Int,
                scan: Scan
            ) {
                val dateFormat = SimpleDateFormat("dd.MM.yyyy")
                val hourFormat = SimpleDateFormat("HH:mm")
                val date = Date(scan.timestamp!!.time)

                holder.scanTitle.text = scan.scanName
                holder.scanDate.text = dateFormat.format(date)
                holder.scanTime.text = hourFormat.format(date)
                holder.floorTile.setImageResource(scan.floorResId)
                holder.wallTile.setImageResource(scan.wallResId)
                holder.robotTile.setImageResource(scan.robotResId)
                holder.card.setOnClickListener {
                    val intent = Intent(it.context, ScanInfoActivity::class.java)
                    it.context.startActivity(intent)
                }
            }

        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home ->
                onBackPressed()
        }
        return super.onOptionsItemSelected(item)
    }
}
