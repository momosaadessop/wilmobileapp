package com.varsitycollege.htchurchmobile

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class EventAdapter(private val eventList: List<Event>) : RecyclerView.Adapter<EventAdapter.EventViewHolder>() {

    class EventViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val date: TextView = itemView.findViewById(R.id.date)
        val churchName: TextView = itemView.findViewById(R.id.churchName)
        val eventName: TextView = itemView.findViewById(R.id.eventName)
        val description: TextView = itemView.findViewById(R.id.description)
        val startTime: TextView = itemView.findViewById(R.id.starttime)
        val endTime: TextView = itemView.findViewById(R.id.endtime)
        val location: TextView = itemView.findViewById(R.id.location)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EventViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.event_recycler_item, parent, false)
        return EventViewHolder(view)
    }

    override fun onBindViewHolder(holder: EventViewHolder, position: Int) {
        val event = eventList[position]
        holder.date.text = event.date
        holder.churchName.text = event.church
        holder.eventName.text = event.name
        holder.description.text = event.description
        holder.startTime.text = event.startTime
        holder.endTime.text = event.endTime
        holder.location.text = event.location

    }
    override fun getItemCount() = eventList.size
}
