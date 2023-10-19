package com.varsitycollege.htchurchmobile

import android.content.ContentValues.TAG
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.FirebaseFirestore


class EventListOnHold : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.hide()
        setContentView(R.layout.event_list_on_hold)
        val date = intent.getStringExtra("date")
        Log.d(TAG, "Transfer Value: $date")
        val dateTextView: TextView = findViewById(R.id.dateTextView)
        dateTextView.text = date
        val db = FirebaseFirestore.getInstance()
        // Query the events collection for all events on the selected date
        val eventList = ArrayList<Event>()
        val adapter = EventAdapter(eventList)
        val recyclerView: RecyclerView = findViewById(R.id.event_recycler)
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(this)
        registerForContextMenu(recyclerView)
        db.collection("events")
            .whereEqualTo("date", date)
            .get()
            .addOnSuccessListener { documents ->
                for (document in documents) {
                    val name = document.getString("name") ?: ""
                    val description = document.getString("description") ?: ""
                    val church = document.getString("church") ?: ""
                    val location = document.getString("address") ?: ""
                    val eventdate = document.getString("date") ?: ""
                    val startTime = document.getString("start_time") ?: ""
                    val endTime = document.getString("end_time") ?: ""
                    Log.d(TAG, "Event Name: $name")
                    Log.d(TAG, "Event Name: $church")
                    Log.d(TAG, "Event Name: $location")
                    Log.d(TAG, "Event Name: $eventdate")
                    Log.d(TAG, "Start Time: $startTime")
                    Log.d(TAG, "End Time: $endTime")
                    Log.d(TAG, "Event Name: $description")
                    val event = Event(name, description, church, location, eventdate, startTime, endTime)
                    eventList.add(event)
                }
                adapter.notifyDataSetChanged()  // Refresh RecyclerView
            }
            .addOnFailureListener { exception ->
                Log.w(TAG, "Error getting documents: ", exception)
            }
        var back = findViewById<ImageButton>(R.id.event_list_back_btn)
        back.setOnClickListener()
        {
            val intent = Intent(this@EventListOnHold, Events::class.java)

            startActivity(intent)
            overridePendingTransition(0, 0)
            finish()
        }
    }
}


//Data class for the event
data class Event(
    val name: String?,
    val description: String?,
    val church: String?,
    val location: String?,
    val date: String?,
    val startTime: String?,
    val endTime: String?
)
