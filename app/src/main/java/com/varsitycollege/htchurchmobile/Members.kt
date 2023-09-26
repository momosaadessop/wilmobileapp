package com.varsitycollege.htchurchmobile

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class Members: AppCompatActivity(){
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.members)
        supportActionBar?.hide()
        print()
        val loginBack = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                val intent = Intent(this@Members, Home::class.java)

                startActivity(intent)
                overridePendingTransition(0, 0)
                finish()


            }
        }
        onBackPressedDispatcher.addCallback(this, loginBack)
        var back = findViewById<ImageButton>(R.id.back_btn)
        back.setOnClickListener()
        {
            val intent = Intent(this@Members, Home::class.java)

            startActivity(intent)
            overridePendingTransition(0, 0)
            finish()
        }
    }
    fun print()
    {
        val recyclerView = findViewById<RecyclerView>(R.id.member_recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)
        val db = FirebaseFirestore.getInstance()

        db.collection("pastors")
            .get()
            .addOnSuccessListener { result ->
                val pastors = result.map { document ->
                    Pastor(
                        firstname = document.getString("name") ?: "",
                        surname = document.getString("title") ?: "",
                        email = document.getString("email") ?: "",
                        churchid = document.getString("churchid") ?: "",
                    )
                }
                recyclerView.adapter = BirdAdapter(pastors)
            }
            .addOnFailureListener { exception ->
                Log.d("Firestore", "get failed with ", exception)
            }
    }
}

data class Pastor(
    val firstname: String,
    val surname: String,
    val email: String,
    val churchid: String,

)
class BirdAdapter(private val birds: List<Pastor>) : RecyclerView.Adapter<BirdAdapter.BirdViewHolder>() {

    inner class BirdViewHolder(view: View) : RecyclerView.ViewHolder(view) {


    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BirdViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.events, parent, false)
        return BirdViewHolder(view)
    }

    override fun onBindViewHolder(holder: BirdViewHolder, position: Int) {
        val bird = birds[position]







    }

    override fun getItemCount() = birds.size
}
