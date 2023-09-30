package com.varsitycollege.htchurchmobile

import android.content.ContentValues.TAG
import android.content.Intent
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.os.Bundle
import android.text.Layout
import android.text.style.LeadingMarginSpan
import android.util.Log
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.Spinner
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import com.jakewharton.threetenabp.AndroidThreeTen
import com.prolificinteractive.materialcalendarview.CalendarDay
import com.prolificinteractive.materialcalendarview.DayViewDecorator
import com.prolificinteractive.materialcalendarview.DayViewFacade
import com.prolificinteractive.materialcalendarview.MaterialCalendarView
import com.prolificinteractive.materialcalendarview.spans.DotSpan
import org.threeten.bp.format.DateTimeFormatter
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import org.threeten.bp.Instant
import org.threeten.bp.ZoneId



class Events:AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.events)
        supportActionBar?.hide()
        AndroidThreeTen.init(this)
        val calendarView: MaterialCalendarView = findViewById(R.id.calendarView)
        val eventDate: EditText = findViewById(R.id.event_date)
        // Get the events from Firestore
        val db = FirebaseFirestore.getInstance()
        db.collection("events")
            .get()
            .addOnSuccessListener { result ->
                for (document in result) {
                    // Get the event date as a string
                    val dateString = document.data["date"] as String
                    // Get the event name
                    val eventName = document.data["name"] as String
                    //Log.d("Firestore", "Date string: $dateString")
                    // Convert the date string to a Calendar object
                    val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.US)
                    val date = sdf.parse(dateString)
                    val calendar = Calendar.getInstance()
                    calendar.time = date
                    // Convert the Calendar object to a LocalDate object
                    val zonedDateTime = org.threeten.bp.ZonedDateTime.ofInstant(
                        org.threeten.bp.Instant.ofEpochMilli(calendar.timeInMillis),
                        org.threeten.bp.ZoneId.systemDefault()
                    )
                    val localDate = zonedDateTime.toLocalDate()
                   // Log.d("Firestore", "LocalDate: $localDate")
                    // Create a new CalendarDay object and add it to the list
                    val day = CalendarDay.from(localDate)
                    // Create a new EventDecorator and add it to the calendar
                    val decorator = EventDecorator(Color.RED, eventName, listOf(day))
                    calendarView.addDecorator(decorator)
                }
            }


        val loginBack = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                val intent = Intent(this@Events, Home::class.java)
                startActivity(intent)
                overridePendingTransition(0, 0)
                finish()
            }
        }
        var back = findViewById<ImageButton>(R.id.back_btn)
        back.setOnClickListener()
        {
            val intent = Intent(this@Events, Home::class.java)
            startActivity(intent)
            overridePendingTransition(0, 0)
            finish()
        }
        onBackPressedDispatcher.addCallback(this, loginBack)
        calendarView.setOnDateChangedListener { widget, date, selected ->
            // Extract the year, month, and day, and format them as a string
            val selectedDate = String.format("%04d-%02d-%02d", date.year, date.month, date.day)
            eventDate.setText(selectedDate)
            Log.d("Selected Date", selectedDate)
        }

        val finishButton: Button = findViewById(R.id.finish_btn)
        finishButton.setOnClickListener {
            addEvent()
        }
        populateSpinner()
        calendarView.setOnDateLongClickListener { widget, date ->
            // Get the selected date as a LocalDate
            val selectedLocalDate = date.date
            // Format the LocalDate to a String
            val dateString = selectedLocalDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))
            Log.d("Selected Date", dateString)  // Log the selected date
            // Start a new activity or fragment and pass the selected date as an extra
            val intent = Intent(this, EventListOnHold::class.java)
            intent.putExtra("date", dateString)
            startActivity(intent)
            finish()
        }
    }
    fun addEvent() {
        val eventName: EditText = findViewById(R.id.event_name)
        val eventTime: EditText = findViewById(R.id.event_time)
        val eventDate: EditText = findViewById(R.id.event_date)
        val churchSpinner: Spinner = findViewById(R.id.church_spinner)
        val eventDescription: EditText = findViewById(R.id.event_description)
        val eventAddress: EditText = findViewById(R.id.event_address)
        val calendarView: MaterialCalendarView = findViewById(R.id.calendarView)
        val name = eventName.text.toString()
        val time = eventTime.text.toString()
        val date = eventDate.text.toString()
        val description = eventDescription.text.toString()
        val address = eventAddress.text.toString()
        // Get the selected church from the spinner
        val church = churchSpinner.selectedItem.toString()
        val event = hashMapOf(
            "name" to name,
            "date" to date,
            "time" to time,
            "description" to description,
            "address" to address,
            "church" to church
        )
        val db = FirebaseFirestore.getInstance()
        db.collection("events")
            .document(name) //event name as the document name
            .set(event)
            .addOnSuccessListener {
                Log.d("DB upload", "DocumentSnapshot added with ID: $name")
                // Convert date string to a Calendar object
                val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.US)
                val dateObj = sdf.parse(date)
                val calendar = Calendar.getInstance()
                calendar.time = dateObj
                // Convert the Calendar object to a LocalDate object
                val zonedDateTime = org.threeten.bp.ZonedDateTime.ofInstant(
                    org.threeten.bp.Instant.ofEpochMilli(calendar.timeInMillis),
                    org.threeten.bp.ZoneId.systemDefault()
                )
                val localDate = zonedDateTime.toLocalDate()
                // Create CalendarDay object and add it to the list
                val day = CalendarDay.from(localDate)
                // Add the new event to the calendar
                calendarView.addDecorator(EventDecorator(Color.RED, name, listOf(day)))

            }
            .addOnFailureListener { e ->
                Log.w("DB upload", "Error adding document", e)
            }
    }
    fun populateSpinner() {
        val db = FirebaseFirestore.getInstance()
        val churchSpinner: Spinner = findViewById(R.id.church_spinner)
        db.collection("spinnerchurchIds")
            .get()
            .addOnSuccessListener { result ->
                val churches = ArrayList<String>()
                for (document in result) {
                    val churchCenter = document.getString("Spinnerchurchcenter")
                    if (churchCenter != null) {
                        churches.add(churchCenter)
                    }
                }
                Log.d(TAG, "Churches: $churches")  // Log the churches list

                // Create an ArrayAdapter using the string array and a default spinner layout
                val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, churches)
                // Specify the layout to use when the list of choices appears
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                // Apply the adapter to the spinner
                churchSpinner.adapter = adapter
            }
            .addOnFailureListener { exception ->
                Log.w(TAG, "Error getting documents.", exception)
            }
    }
}
class EventDecorator(private val color: Int, private val text: String, dates: Collection<CalendarDay>) : DayViewDecorator {
    private val dates: HashSet<CalendarDay> = HashSet(dates)
    override fun shouldDecorate(day: CalendarDay): Boolean {
        return dates.contains(day)
    }
    override fun decorate(view: DayViewFacade) {
        view.addSpan(TextSpan(text, color))  // You can set the text and color
    }
}



class TextSpan(private val text: String, private val color: Int) : LeadingMarginSpan {
    override fun getLeadingMargin(first: Boolean): Int {
        return 50  // space for the text
    }
    override fun drawLeadingMargin(
        c: Canvas, p: Paint, x: Int, dir: Int,
        top: Int, baseline: Int, bottom: Int,
        text: CharSequence, start: Int, end: Int,
        first: Boolean, layout: Layout
    ) {
        val style = p.style
        val color = p.color
        p.style = Paint.Style.FILL
        p.color = this.color
        c.drawText(this.text, x.toFloat(), baseline.toFloat(), p)
        p.style = style
        p.color = color
    }
}
