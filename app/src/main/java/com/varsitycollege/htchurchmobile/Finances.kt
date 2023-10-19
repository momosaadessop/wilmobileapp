package com.varsitycollege.htchurchmobile

import android.os.Bundle
import android.util.Log
import android.view.ContextMenu
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.widget.AdapterView
import android.widget.Button
import android.widget.EditText
import android.widget.FrameLayout
import android.widget.ImageButton
import android.widget.ListView
import android.widget.RelativeLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class Finances : AppCompatActivity() {
    private val TAG = "FinancesActivity"
    private val financeHistoryList = ArrayList<FinanceData>() // Use FinanceData class
    private lateinit var financeHistoryAdapter: FinanceDataAdapter
    private var totalAmount: Int = 0

    data class DataClass(var data: String)

    private var iddata: DataClass? = null

    private fun IDload(callback: () -> Unit) {
        val LoggedinProfile = FirebaseAuth.getInstance().currentUser
        val regEmail = LoggedinProfile?.email
        val parts = regEmail!!.split('@', '.')
        val userID = parts[0] + parts[1]
        Log.d("userid", userID)

        val db = FirebaseFirestore.getInstance()
        val docRef = db.collection("pastors").document(userID)
        docRef.get()
            .addOnSuccessListener { document ->
                if (document != null) {
                    Log.d(TAG, "DocumentSnapshot data: ${document.data}")
                    val profiles = document.get("userDetails") as Map<String, Any>
                    val id = profiles["churchid"].toString()
                    Log.d("churchid", id)
                    iddata = DataClass(id)
                    callback() // Call the callback when IDload is complete
                } else {
                    Log.d(TAG, "No such document")
                }
            }
            .addOnFailureListener { exception ->
                Log.d(TAG, "get failed with ", exception)
            }
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.finances)
        supportActionBar?.hide()
        val currentUser = FirebaseAuth.getInstance().currentUser
        val userEmail = currentUser?.email
        IDload {
            Log.d(TAG, "Navigating to the finance page - User: $userEmail")

            financeHistoryAdapter =
                FinanceDataAdapter(this, R.layout.history_finance, financeHistoryList)
            val listView = findViewById<ListView>(R.id.finance_history)
            listView.adapter = financeHistoryAdapter
            registerForContextMenu(listView)


            val totalAmountTextView = findViewById<TextView>(R.id.totalAmountTextView)

            // Now that IDload is complete, call fetchAndDisplayFinanceHistory
            fetchAndDisplayFinanceHistory()

        }
        val toggleAddFinanceButton = findViewById<ImageButton>(R.id.showAddFinanceButton)
        val addFinanceOverlay = findViewById<FrameLayout>(R.id.addFinanceOverlay)

        toggleAddFinanceButton.setOnClickListener {
            if (addFinanceOverlay.visibility == View.VISIBLE) {
                addFinanceOverlay.visibility = View.GONE
            } else {
                addFinanceOverlay.visibility = View.VISIBLE
            }
        }

        val confirmButton = findViewById<Button>(R.id.confirmButtom)
        confirmButton.setOnClickListener {
            // Capture data from EditText fields
            val typeOfExpense = findViewById<EditText>(R.id.typeOfExpense).text.toString()
            val tithesValue = findViewById<EditText>(R.id.tighesInput).text.toString().toInt()
            val donationsValue = findViewById<EditText>(R.id.donationsInput).text.toString().toInt()
            val fundRaiserValue = findViewById<EditText>(R.id.fundInput).text.toString().toInt()
            val currentTime = Timestamp.now()
            val sdf = SimpleDateFormat("yyyy-MM-dd")
            val currentDate = sdf.format(currentTime.toDate())
            if (iddata != null) {
                val documentPath = "churchs/${iddata?.data}"

                // Create a map for the new entry
                val newEntry = mapOf(
                    "expenseInput" to typeOfExpense,
                    "tithes" to tithesValue,
                    "donations" to donationsValue,
                    "fundRaiser" to fundRaiserValue,
                    "confirmationTime" to currentTime,
                    "total" to tithesValue + donationsValue + fundRaiserValue
                )


                FirebaseFirestore.getInstance().document(documentPath).get()
                    .addOnSuccessListener { document ->
                        if (document != null) {
                            val existingFinanceData = document.get("finance") as Map<*, *>?

                            // Check if there's existing data and convert it to a list
                            val existingFinanceList: MutableList<Map<String, Any>> =
                                if (existingFinanceData != null && existingFinanceData["entries"] is List<*>) {
                                    (existingFinanceData["entries"] as List<*>).filterIsInstance<Map<String, Any>>()
                                        .toMutableList()
                                } else {
                                    mutableListOf()
                                }

                            // Add the new entry to the existing data
                            existingFinanceList.add(newEntry)

                            // Update the finance data with the new entry
                            val updatedFinanceData = mapOf("entries" to existingFinanceList)

                            FirebaseFirestore.getInstance()
                                .document(documentPath)
                                .set(
                                    mapOf("finance" to updatedFinanceData),
                                    SetOptions.merge()
                                )
                                .addOnSuccessListener {
                                    Log.d(
                                        TAG,
                                        "Data saved successfully for church ID: ${iddata?.data}"
                                    )
                                    Toast.makeText(
                                        this@Finances,
                                        "Data saved successfully",
                                        Toast.LENGTH_SHORT
                                    )
                                        .show()


                                    fetchAndDisplayFinanceHistory()
                                    findViewById<EditText>(R.id.typeOfExpense).text.clear()
                                      findViewById<EditText>(R.id.tighesInput).text.clear()
                                    findViewById<EditText>(R.id.donationsInput).text.clear()
                                findViewById<EditText>(R.id.fundInput).text.clear()

                                    val addFinanceOverlay =
                                        findViewById<FrameLayout>(R.id.addFinanceOverlay)
                                    addFinanceOverlay.visibility = View.GONE
                                    calculateTotalAmount()
                                }
                                .addOnFailureListener { e ->
                                    Log.e(TAG, "Error saving data: $e")
                                    Toast.makeText(
                                        this@Finances,
                                        "Error saving data: $e",
                                        Toast.LENGTH_SHORT
                                    )
                                        .show()
                                }
                        } else {
                            // If the document doesn't exist, create it with the initial entry
                            val initialFinanceData = mapOf("entries" to listOf(newEntry))
                            FirebaseFirestore.getInstance()
                                .document(documentPath)
                                .set(mapOf("finance" to initialFinanceData))
                                .addOnSuccessListener {
                                    Log.d(
                                        TAG,
                                        "Finance document created for church ID: ${iddata?.data}"
                                    )
                                    Toast.makeText(
                                        this@Finances,
                                        "Finance document created",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                                .addOnFailureListener { e ->
                                    Log.e(TAG, "Error creating finance document: $e")
                                    Toast.makeText(
                                        this@Finances,
                                        "Error creating finance document: $e",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                        }
                    }
                    .addOnFailureListener { e ->
                        Log.e(TAG, "Error retrieving existing data: $e")
                        Toast.makeText(
                            this@Finances,
                            "Error retrieving existing data: $e",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
            } else {
                Toast.makeText(this@Finances, "Error getting church ID", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun calculateTotalAmount() {
        financeHistoryList.forEach { it.calculateTotalAmount() }
    }

    private fun fetchAndDisplayFinanceHistory() {
        financeHistoryList.clear() // Clear the list before fetching new data

        if (iddata != null) {
            val documentPath = "churchs/${iddata?.data}"
            val docRef = FirebaseFirestore.getInstance().document(documentPath)

            docRef.get()
                .addOnSuccessListener { document ->
                    if (document != null) {
                        Log.d(TAG, "DocumentSnapshot data: ${document.data}")

                        val financeData = document.get("finance") as Map<String, Any>?
                        val entries = financeData?.get("entries") as List<Map<String, Any>>?

                        // Inside fetchAndDisplayFinanceHistory
                        if (entries != null) {
                            for (entryMap in entries) {
                                val typeOfExpense = entryMap["expenseInput"] as? String
                                val tithesValue = entryMap["tithes"] as? Long
                                val donationsValue = entryMap["donations"] as? Long
                                val fundRaiserValue = entryMap["fundRaiser"] as? Long
                                val confirmationTime = entryMap["confirmationTime"] as Timestamp

                                // Check the data for validity and handle it accordingly
                                if (typeOfExpense != null && confirmationTime != null) {
                                    val tithes = tithesValue?.toInt() ?: 0
                                    val donations = donationsValue?.toInt() ?: 0
                                    val fundRaiser = fundRaiserValue?.toInt() ?: 0

                                    val (month, date, time) = calculateMonthDateAndTime(
                                        confirmationTime
                                    ) // Calculate date and time

                                    // Create the FinanceData object
                                    val financeEntry = FinanceData(
                                        typeOfExpense,
                                        tithes,
                                        donations,
                                        fundRaiser,
                                        confirmationTime,
                                        month

                                    )

                                    financeHistoryList.add(financeEntry)
                                } else {
                                    // Handle or log the invalid data more gracefully
                                    Log.e(TAG, "Invalid data format: $entryMap")
                                }

                            }
                            // Notify the adapter that the data has changed
                            financeHistoryAdapter.notifyDataSetChanged()
                            Log.d(TAG, "Finance data retrieved and displayed: $financeHistoryList")
                            calculateTotalAmount()
                        } else {
                            Log.d(TAG, "No finance entries found")
                        }

                    } else {
                        Log.d(TAG, "No such document")
                    }
                }
                .addOnFailureListener { exception ->
                    Log.e(TAG, "get failed with ", exception)
                }
        } else {
            Toast.makeText(this@Finances, "Error getting church ID", Toast.LENGTH_SHORT).show()
        }
    }

    private fun calculateMonthDateAndTime(confirmationTime: Timestamp): Triple<Int, Int, String> {
        val calendar = Calendar.getInstance()
        calendar.time = confirmationTime.toDate()

        val month = calendar[Calendar.MONTH] + 1 // Adding 1 because months are 0-based
        val date = calendar[Calendar.DATE]
        val timeFormat = SimpleDateFormat("HH:mm", Locale.getDefault())
        val time = timeFormat.format(calendar.time)

        return Triple(month, date, time)
    }

    override fun onCreateContextMenu(
        menu: ContextMenu?,
        v: View?,
        menuInfo: ContextMenu.ContextMenuInfo?
    ) {
        super.onCreateContextMenu(menu, v, menuInfo)

        val listView = findViewById<ListView>(R.id.finance_history)

        if (v == listView) {
            menuInflater.inflate(R.menu.delete_item, menu)
        }
    }

    override fun onContextItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.delete_item) {
            val info = item.menuInfo as AdapterView.AdapterContextMenuInfo
            val position = info.position

            if (position >= 0 && position < financeHistoryList.size) {
                val selectedEntry = financeHistoryList[position]

                if (iddata != null) {
                    val documentPath = "churchs/${iddata?.data}"
                    val db = FirebaseFirestore.getInstance()

                    // Retrieve the current list of entries from Firestore
                    db.document(documentPath)
                        .get()
                        .addOnSuccessListener { documentSnapshot ->
                            if (documentSnapshot.exists()) {
                                val financeData =
                                    documentSnapshot.get("finance") as Map<String, Any>?
                                val entries = financeData?.get("entries") as List<Map<String, Any>>?

                                if (entries != null) {
                                    // Identify the entry to delete based on a unique identifier
                                    // For example, if you have a unique entry ID, you can use that
                                    // Here, I'll assume you're using confirmationTime as a unique identifier
                                    val uniqueIdentifier = selectedEntry.confirmationTime

                                    // Filter the list to exclude the entry with the matching unique identifier
                                    val updatedEntries = entries.filterNot { entry ->
                                        entry["confirmationTime"] == uniqueIdentifier
                                    }

                                    // Update the Firestore document with the modified list
                                    val updatedData =
                                        mapOf("finance" to mapOf("entries" to updatedEntries))
                                    db.document(documentPath)
                                        .set(updatedData)
                                        .addOnSuccessListener {
                                            Log.d(TAG, "Entry deleted from Firestore")
                                        }
                                        .addOnFailureListener { e ->
                                            Log.e(TAG, "Error deleting entry from Firestore: $e")
                                        }
                                }
                            }
                        }
                }

                // Remove the entry from the local list
                financeHistoryList.removeAt(position)
                financeHistoryAdapter.notifyDataSetChanged()

                return true
            }
        }
        return super.onContextItemSelected(item)
    }


    data class FinanceData(
        val typeOfExpense: String,
        val tithesValue: Int,
        val donationsValue: Int,
        val fundRaiserValue: Int,
        val confirmationTime: Timestamp,
        var totalAmount: Int = 0
    ) {
        val month: Int
        val date: Int
        val time: String

        init {
            val calendar = Calendar.getInstance()
            calendar.time = confirmationTime.toDate()

            this.month = calendar[Calendar.MONTH] + 1 // Adding 1 because months are 0-based
            this.date = calendar[Calendar.DATE]

            val timeFormat = SimpleDateFormat("HH:mm", Locale.getDefault())
            this.time = timeFormat.format(calendar.time)
        }

        companion object {
            fun fromMap(map: Map<String, Any>): FinanceData {
                return FinanceData(
                    map["expenseInput"] as String,
                    (map["tighes"] as Long).toInt(),
                    (map["donations"] as Long).toInt(),
                    (map["fundRaiser"] as Long).toInt(),
                    map["confirmationTime"] as Timestamp
                )
            }
        }

        fun toMap(): Map<String, Any> {
            return mapOf(
                "expenseInput" to typeOfExpense,
                "tighes" to tithesValue,
                "donations" to donationsValue,
                "fundRaiser" to fundRaiserValue,
                "confirmationTime" to confirmationTime
            )
        }

        fun calculateTotalAmount() {
            totalAmount = tithesValue + donationsValue + fundRaiserValue
        }

        override fun toString(): String {
            return "Type of Expense: $typeOfExpense\n" +
                    "Tithes: $tithesValue\n" +
                    "Donations: $donationsValue\n" +
                    "Fund Raiser: $fundRaiserValue\n" +
                    "Confirmation Time: $month/$date $time\n" +
                    "Total Amount: $totalAmount"
        }
    }
}
