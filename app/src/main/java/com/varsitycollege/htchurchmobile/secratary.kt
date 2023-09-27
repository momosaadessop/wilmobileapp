package com.varsitycollege.htchurchmobile

import android.content.ContentValues
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class secratary : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.secretary)
        supportActionBar?.hide()
IDload()
        val loginBack = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                val intent = Intent(this@secratary, Home::class.java)

                startActivity(intent)
                overridePendingTransition(0, 0)
                finish()


            }
        }
        onBackPressedDispatcher.addCallback(this, loginBack)
        var back = findViewById<ImageButton>(R.id.sec_back_btn)
        back.setOnClickListener()
        {
            val intent = Intent(this@secratary, Home::class.java)

            startActivity(intent)
            overridePendingTransition(0, 0)
            finish()
        }
        var addnew = findViewById<ImageButton>(R.id.sec_add_btn)
        addnew.setOnClickListener()
        {
            val intent = Intent(this, AddSec::class.java)

            startActivity(intent)
            overridePendingTransition(0, 0)
            finish()
            Log.d("clicked", "i am clicked")
        }
    }

    class DataClass {
        var data: String = ""
    }

    fun IDload() {


        val user = FirebaseAuth.getInstance().currentUser

        val userEmail = user?.email


        val parts = userEmail!!.split('@', '.')
        val userID = parts[0] + parts[1]
        Log.d("userid", userID)
        val db = FirebaseFirestore.getInstance()
        val docRef = db.collection("pastors").document(userID)
        docRef.get()
            .addOnSuccessListener { document ->
                if (document != null) {
                    Log.d(ContentValues.TAG, "DocumentSnapshot data: ${document.data}")
                    val userDetails = document.get("userDetails") as Map<String, Any>

                    val id = userDetails["churchid"].toString()
                    val iddata = DataClass()
                    iddata.data = id
                    printDATA(iddata)


                } else {
                    Log.d(ContentValues.TAG, "No such document")
                }
            }
            .addOnFailureListener { exception ->
                Log.d(ContentValues.TAG, "get failed with ", exception)
            }


    }

    fun printDATA(data: DataClass) {
        val recyclerview = findViewById<RecyclerView>(R.id.sec_recyclerView)
        recyclerview.layoutManager = LinearLayoutManager(this)
        val db = FirebaseFirestore.getInstance()
        val user = FirebaseAuth.getInstance().currentUser

        val userEmail = user?.email

        val parts = userEmail!!.split('@', '.')
        val userID = parts[0] + parts[1]
        db.collection("churchs").document(data.data)
            .get()
            .addOnSuccessListener { document ->
                if (document != null) {
                    val secs = document.data?.get("secs") as? Map<String, Map<String, String>>
                    if (secs != null) {
                        val birdsList = secs.values.map {
                            Sec(
                                it["firstname"]!!,
                                it["surname"]!!,
                                it["email"]!!,
                                it["worshipname"]!!,
                                it["churchid"]!!,
                                it["date"]!!
                            )
                        }
                        recyclerview.adapter = SecAdapter(birdsList)
                        Log.d("Firestore", birdsList.toString())
                    } else {
                        Log.d("Firestore", "No birdObservations in document")
                    }
                } else {
                    Log.d("Firestore", "No such document")
                }
            }
            .addOnFailureListener { exception ->
                Log.d("Firestore", "get failed with ", exception)
            }

    }
}

data class Sec(
    val firstname: String,
    val surname: String,
    val email: String,
    val worshipname: String,
    val id: String,
    val date: String
)

class SecAdapter(private val birds: List<Sec>) : RecyclerView.Adapter<SecAdapter.BirdViewHolder>() {

    inner class BirdViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val firstNames: TextView = view.findViewById(R.id.sec_name)
        val surnames: TextView = view.findViewById(R.id.sec_surname)
        val emails: TextView = view.findViewById(R.id.sec_email)
        val worhshipname: TextView = view.findViewById(R.id.sec_churchname)
        val ids: TextView = view.findViewById(R.id.secid)
        val dates: TextView = view.findViewById(R.id.sec_date)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BirdViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.sec_cv, parent, false)
        return BirdViewHolder(view)
    }

    override fun onBindViewHolder(holder: BirdViewHolder, position: Int) {
        val bird = birds[position]
        holder.firstNames.text = bird.firstname
        holder.surnames.text = bird.surname
        holder.emails.text = bird.email
        holder.worhshipname.text = bird.worshipname
        holder.ids.text = bird.id
        holder.dates.text = bird.date




        holder.itemView.setOnClickListener {
            val clickedData = birds[position]
            val context = holder.itemView.context
            val intent = Intent(context, EditSec::class.java)
            intent.putExtra("birdid", clickedData.email)
            intent.putExtra("birdid", clickedData.id)


            context.startActivity(intent)

        }
    }

    override fun getItemCount() = birds.size
}
