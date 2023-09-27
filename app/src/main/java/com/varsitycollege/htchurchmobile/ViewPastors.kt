package com.varsitycollege.htchurchmobile

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
import com.google.firebase.firestore.FirebaseFirestore

class ViewPastors:AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.pastors)
        supportActionBar?.hide()
        print()
        val loginBack = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                val intent = Intent(this@ViewPastors, Home::class.java)

                startActivity(intent)
                overridePendingTransition(0, 0)
                finish()


            }
        }
        var back = findViewById<ImageButton>(R.id.back_btn)
        back.setOnClickListener()
        {
            val intent = Intent(this@ViewPastors, Home::class.java)

            startActivity(intent)
            overridePendingTransition(0, 0)
            finish()
        }
        onBackPressedDispatcher.addCallback(this, loginBack)
    }
    fun print()
    {
        val recyclerView = findViewById<RecyclerView>(R.id.pastor_recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)
        val db = FirebaseFirestore.getInstance()

        db.collection("pastors")
            .get()
            .addOnSuccessListener { result ->
                val pastors = result.mapNotNull { document ->
                    val memberDetails = document.get("userDetails") as? HashMap<*, *>
                    if (memberDetails != null) {
                        Pastordata(
                            firstname = memberDetails["firstname"] as? String ?: "",
                            surname = memberDetails["surname"] as? String ?: "",
                            email = memberDetails["email"] as? String ?: "",
                            churchid = memberDetails["churchid"] as? String ?: "",
                        ).also {
                            Log.d("Firestore", "Fetched pastor: $it")
                        }
                    } else {
                        null
                    }
                }
                recyclerView.adapter = pastoradaptor(pastors)
            }


    }
}

data class Pastordata(
    val firstname: String,
    val surname: String,
    val email: String,
    val churchid: String,

    )
class pastoradaptor(private val birds: List<Pastordata>) : RecyclerView.Adapter<pastoradaptor.BirdViewHolder>() {

    inner class BirdViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val firstName: TextView = view.findViewById(R.id.pastor_name)
        val SurName: TextView = view.findViewById(R.id.pastor_surname)
        val email: TextView = view.findViewById(R.id.pastor_email)
        val churchid: TextView = view.findViewById(R.id.pastor_churchid)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BirdViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.pastor_cv, parent, false)
        return BirdViewHolder(view)
    }

    override fun onBindViewHolder(holder: BirdViewHolder, position: Int) {
        val bird = birds[position]

        holder.firstName.text = bird.firstname
        holder.SurName.text = bird.surname
        holder.email.text = bird.email
        holder.churchid.text = bird.churchid






    }

    override fun getItemCount() = birds.size
}
