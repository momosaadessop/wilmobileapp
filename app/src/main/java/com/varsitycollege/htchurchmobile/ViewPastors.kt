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
        val pastordisplay = findViewById<RecyclerView>(R.id.pastor_recyclerView)
        pastordisplay.layoutManager = LinearLayoutManager(this)
        val db = FirebaseFirestore.getInstance()

        db.collection("pastors")
            .get()
            .addOnSuccessListener { result ->
                val pastors = result.mapNotNull { document ->
                    val PastorProfiles = document.get("userDetails") as? HashMap<*, *>
                    if (PastorProfiles != null) {
                        Pastordata(
                            firstname = PastorProfiles["firstname"] as? String ?: "",
                            surname = PastorProfiles["surname"] as? String ?: "",
                            email = PastorProfiles["email"] as? String ?: "",
                            churchid = PastorProfiles["churchid"] as? String ?: "",
                        ).also {
                            Log.d("Firestore", "Fetched pastor: $it")
                        }
                    } else {
                        null
                    }
                }
                pastordisplay.adapter = pastoradaptor(pastors)
            }


    }
}

data class Pastordata(
    val firstname: String,
    val surname: String,
    val email: String,
    val churchid: String,

    )
class pastoradaptor(private val pdata: List<Pastordata>) : RecyclerView.Adapter<pastoradaptor.PastorHolder>() {

    inner class PastorHolder(view: View) : RecyclerView.ViewHolder(view) {
        val firstName: TextView = view.findViewById(R.id.pastor_name)
        val SurName: TextView = view.findViewById(R.id.pastor_surname)
        val email: TextView = view.findViewById(R.id.pastor_email)
        val churchid: TextView = view.findViewById(R.id.sec_churchname)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PastorHolder {
        val PastorLayout = LayoutInflater.from(parent.context).inflate(R.layout.pastor_cv, parent, false)
        return PastorHolder(PastorLayout)
    }

    override fun onBindViewHolder(pdisplay: PastorHolder, position: Int) {
        val psdata = pdata[position]

        pdisplay.firstName.text = psdata.firstname
        pdisplay.SurName.text = psdata.surname
        pdisplay.email.text = psdata.email
        pdisplay.churchid.text = psdata.churchid






    }

    override fun getItemCount() = pdata.size
}
