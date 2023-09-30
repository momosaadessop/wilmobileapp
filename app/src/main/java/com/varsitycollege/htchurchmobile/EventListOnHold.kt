package com.varsitycollege.htchurchmobile

import android.content.ContentValues.TAG
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore


class EventListOnHold : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.event_list_on_hold)
        // Get the date from the intent
        val date = intent.getStringExtra("date")
        Log.d(TAG, "Transfer Value: $date") // This will log the selected date
        val db = FirebaseFirestore.getInstance()
        // Query the events collection for all events on the selected date
        db.collection("events")
            .whereEqualTo("date", date)
            .get()
            .addOnSuccessListener { documents ->
                val eventList = ArrayList<String>()
                for (document in documents) {
                    val name = document.getString("name")
                    val description = document.getString("description")
                    val church = document.getString("church")
                    val address = document.getString("address")
                    val date = document.getString("date")
                    val time = document.getString("time")
                    Log.d(TAG, "Event Name: $name")
                    Log.d(TAG, "Event Name: $church")
                    Log.d(TAG, "Event Name: $address")
                    Log.d(TAG, "Event Name: $date")
                    Log.d(TAG, "Event Name: $time")
                    Log.d(TAG, "Event Name: $description")
                    // This will log the event name
                }

            }
            .addOnFailureListener { exception ->
                Log.w(TAG, "Error getting documents: ", exception)
            }
    }
}



