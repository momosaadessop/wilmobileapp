package com.varsitycollege.htchurchmobile

import android.content.ContentValues
import android.content.ContentValues.TAG
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import de.keyboardsurfer.android.widget.crouton.Crouton
import de.keyboardsurfer.android.widget.crouton.Style
import org.checkerframework.common.returnsreceiver.qual.This

class ChurchDetails:AppCompatActivity() {
    private val e = Errors()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.church_details)
        supportActionBar?.hide()
        securGuard()
        IDload()

        val loginBack = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                val intent = Intent(this@ChurchDetails, Home::class.java)

                startActivity(intent)
                overridePendingTransition(0, 0)
                finish()


            }

        }
        onBackPressedDispatcher.addCallback(this, loginBack)
        var back = findViewById<ImageButton>(R.id.back_btn)
        back.setOnClickListener()
        {
            val intent = Intent(this@ChurchDetails, Home::class.java)

            startActivity(intent)
            overridePendingTransition(0, 0)
            finish()
        }
        var savedata = findViewById<Button>(R.id.confirm_btn)
        savedata.setOnClickListener()
        {
            editdata()
        }
    }

    private fun securGuard() {
// this checks user tokens
        // if invalid forces the user to login again
        // if deleted forces the user out of the app
        val tempusSecurity = FirebaseAuth.getInstance()
        tempusSecurity.addAuthStateListener { firebaseAuth ->
            when (firebaseAuth.currentUser) {
                null -> {
                    val intent = Intent(this, Login::class.java)

                    startActivity(intent)
                    overridePendingTransition(0, 0)
                    finish()

                }
            }
        }
    }
    class DataClass {
        var data: String = ""
    }
    fun IDload() {

        val churchid: EditText = findViewById(R.id.church_id)

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

                    churchid.setText(id)
                    Dataload(iddata)

                } else {
                    Log.d(ContentValues.TAG, "No such document")
                }
            }
            .addOnFailureListener { exception ->
                Log.d(ContentValues.TAG, "get failed with ", exception)
            }


    }
    fun Dataload(data:DataClass) {

        val churchid: EditText = findViewById(R.id.church_id)
        val churchname: EditText = findViewById(R.id.church_Name)

        val place: EditText = findViewById(R.id.location)

        val memberno:EditText = findViewById(R.id.member_number)

        val pastorno:EditText = findViewById(R.id.pastor_number)

        val user = FirebaseAuth.getInstance().currentUser

        val userEmail = user?.email


        val parts = userEmail!!.split('@', '.')
        val userID = parts[0] + parts[1]
        Log.d("userid", userID)
        val db = FirebaseFirestore.getInstance()
        val docRef = db.collection("churchs").document(data.data.toString())
        docRef.get()
            .addOnSuccessListener { document ->
                if (document != null) {
                    Log.d(ContentValues.TAG, "DocumentSnapshot data: ${document.data}")
                    val userDetails = document.get("churchDetails") as Map<String, Any>

                    val mno = userDetails["members"].toString()
                    val pno = userDetails["pastors"].toString()
                    val name = userDetails["churchname"].toString()
                    val location = userDetails["location"].toString()
memberno.setText(mno)
                    pastorno.setText(pno)
                    churchname.setText(name)
place.setText(location)
                } else {
                    Log.d(ContentValues.TAG, "No such document")
                }
            }
            .addOnFailureListener { exception ->
                Log.d(ContentValues.TAG, "get failed with ", exception)
            }


    }
    fun editdata() {
        val namefield: EditText = findViewById(R.id.church_Name)
        val place: EditText = findViewById(R.id.location)

        val memberno:EditText = findViewById(R.id.member_number)
        val churchid:EditText = findViewById(R.id.church_id)
        val pastorno:EditText = findViewById(R.id.pastor_number)


        val user = FirebaseAuth.getInstance().currentUser

        val userEmail = user?.email


        val parts = userEmail!!.split('@', '.')
        val userID = parts[0] + parts[1]
        Log.d("userid", userID)
        val db = FirebaseFirestore.getInstance()

        val name = namefield.text.toString().replace("\\s".toRegex(), "")
        val location = place.text.toString().replace("\\s".toRegex(), "")
        val members = memberno.text.toString().replace("\\s".toRegex(), "")
        val pastors = pastorno.text.toString().replace("\\s".toRegex(), "")


        val id = churchid.text.toString().replace("\\s".toRegex(), "")


        val users = churchdetail(
            id, name, members, pastors,location
        )
        val docRef = db.collection("churchs").document(id)
        docRef.set(
            mapOf("churchDetails" to users), SetOptions.merge()
        ).addOnSuccessListener {
            Crouton.makeText(this@ChurchDetails, "Church details saved successfully", Style.CONFIRM).show()
        }.addOnFailureListener { e ->
            Log.w(TAG, "Error writing document", e)
        }

    }
}