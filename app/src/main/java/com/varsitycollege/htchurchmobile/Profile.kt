package com.varsitycollege.htchurchmobile

import android.content.ContentValues
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import de.keyboardsurfer.android.widget.crouton.Crouton
import de.keyboardsurfer.android.widget.crouton.Style

class Profile:AppCompatActivity() {
    private val e = Errors()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.profiles)
        supportActionBar?.hide()
        securGuard()
        val loginBack = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                val intent = Intent(this@Profile, Home::class.java)

                startActivity(intent)
                overridePendingTransition(0, 0)
                finish()


            }
        }
        val forgotpassword:Button = findViewById(R.id.forgot)
        forgotpassword.setOnClickListener(){
            forgotpassword()
        }
        val save: Button = findViewById(R.id.confirm_btn)
        save.setOnClickListener()
        {

            val namefield: EditText = findViewById(R.id.profile_Name)
            val surname: EditText = findViewById(R.id.profile_Surname)
            val email: EditText = findViewById(R.id.profile_Email)
            val phone: EditText = findViewById(R.id.phone_number)
          val church:EditText = findViewById(R.id.church)
            val churchid:EditText = findViewById(R.id.church_id)
            val centersize:EditText = findViewById(R.id.center_size)
            val country:EditText = findViewById(R.id.country)

            //action statements tp check fields if empty
            when {
                namefield.text.toString().isEmpty() -> {
                    Snackbar.make(email, e.noFName, Snackbar.LENGTH_SHORT).show()
                }

                surname.text.toString().isEmpty() -> {
                    Snackbar.make(email, e.noSName, Snackbar.LENGTH_SHORT).show()
                }
                email.text.toString().isEmpty() -> {
                    Snackbar.make(email, e.emailValidationEmptyError, Snackbar.LENGTH_SHORT).show()
                }

                phone.text.isEmpty() -> {

                    Snackbar.make(email, e.nophonenumber, Snackbar.LENGTH_SHORT).show()

                }
                church.text.toString().isEmpty() ->
                {

                    Snackbar.make(email, e.emptychurch, Snackbar.LENGTH_SHORT).show()
                }
                centersize.text.isEmpty() ->
                {

                    Snackbar.make(email, e.emptysize, Snackbar.LENGTH_SHORT).show()
                }
                country.text.isEmpty() ->
                {

                    Snackbar.make(email, e.emptycountry, Snackbar.LENGTH_SHORT).show()
                }
                else -> { // move to the next screen if filled


                    editdata()
                }
            }
        }
        onBackPressedDispatcher.addCallback(this, loginBack)
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
    fun forgotpassword() {
        var email: EditText = findViewById(R.id.profile_Email)
        val auth = FirebaseAuth.getInstance()
        auth.sendPasswordResetEmail(email.text.toString())
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val crouton = Crouton.makeText(this, "forgot email sent", Style.ALERT)
                    crouton.show()
                } else {
                    // There was an error. Handle it here.
                }
            }

    }
    fun editdata() {
        val namefield: EditText = findViewById(R.id.profile_Name)
        val surname: EditText = findViewById(R.id.profile_Surname)
        val email: EditText = findViewById(R.id.profile_Email)
        val phone: EditText = findViewById(R.id.phone_number)
        val church:EditText = findViewById(R.id.church)
        val churchid:EditText = findViewById(R.id.church_id)
        val centersize:EditText = findViewById(R.id.center_size)
        val country:EditText = findViewById(R.id.country)

        val user = FirebaseAuth.getInstance().currentUser

        val userEmail = user?.email


        val parts = userEmail!!.split('@', '.')
        val userID = parts[0] + parts[1]
        Log.d("userid", userID)
        val db = FirebaseFirestore.getInstance()

        val name = namefield.text.toString().replace("\\s".toRegex(), "")
        val surnames = surname.text.toString().replace("\\s".toRegex(), "")
        val churchname = church.text.toString().replace("\\s".toRegex(), "")
        val centeramount = centersize.text.toString().replace("\\s".toRegex(), "")
        val place= country.text.toString().replace("\\s".toRegex(), "")
        val emails = email.text.toString().replace("\\s".toRegex(), "")
val id = churchid.text.toString().replace("\\s".toRegex(), "")

        val phones = phone.text.toString()
        val users = User(
            name, surnames, emails, churchname,centeramount,place,id, userID, phones.toInt()
        )
        val docRef = db.collection("aves").document(userID)
        docRef.set(
            mapOf("userDetails" to users), SetOptions.merge()
        )
    }
    fun dataload() {
        val namefield: EditText = findViewById(R.id.profile_Name)
        val surname: EditText = findViewById(R.id.profile_Surname)
        val email: EditText = findViewById(R.id.profile_Email)
        val phone: EditText = findViewById(R.id.phone_number)
        val church:EditText = findViewById(R.id.church)
        val churchid:EditText = findViewById(R.id.church_id)
        val centersize:EditText = findViewById(R.id.center_size)
        val country:EditText = findViewById(R.id.country)
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
                    val name = userDetails["firstname"].toString()
                    val surnames = userDetails["surname"].toString()
                    val emails = userDetails["email"].toString()
                    val phonenumber = userDetails["phonenumber"].toString()

                    phone.setText(phonenumber)
                    namefield.setText(name)
                    surname.setText(surnames)
                    email.setText(emails)

                } else {
                    Log.d(ContentValues.TAG, "No such document")
                }
            }
            .addOnFailureListener { exception ->
                Log.d(ContentValues.TAG, "get failed with ", exception)
            }


    }
}