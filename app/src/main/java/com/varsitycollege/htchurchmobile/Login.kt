package com.varsitycollege.htchurchmobile

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase

class Login : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.login)
        supportActionBar?.hide()
        check()
        autologin()

    }
    private fun autologin() {

        val auth = FirebaseAuth.getInstance()
        val currentUser = auth.currentUser
        if (currentUser != null) {
            val intent = Intent(this, Home::class.java)

            startActivity(intent)
            overridePendingTransition(0, 0)
            finish()
        }

    }
    fun check() {
        var login = findViewById<Button>(R.id.login_button)
        login.setOnClickListener() {
            val emails: EditText = findViewById(R.id.usernametxt)
            val pass: EditText = findViewById(R.id.password)
            val parts = emails.text.split('@', '.')
            val result = parts[0] + parts[1]
            Log.d("resultemail", result)
            val db = FirebaseFirestore.getInstance()
            val docRef = db.collection("pastors").document(result.lowercase())
            docRef.get()
                .addOnSuccessListener { document ->
                    if (document.exists()) {
                        val text = "found you!!"
                        val duration = Toast.LENGTH_SHORT
                        val toast = Toast.makeText(this, text, duration)
                        toast.show()
                        val auth = FirebaseAuth.getInstance()
                        auth.createUserWithEmailAndPassword(
                            emails.text.toString().lowercase(),
                            pass.text.toString()
                        )
                            .addOnCompleteListener(this) { task ->
                                if (task.isSuccessful) {

                                    val user = auth.currentUser

                                } else {

                                    val security = Firebase.auth


                                    security.signInWithEmailAndPassword(
                                        emails?.text.toString().trim(),
                                        pass?.text.toString().trim()

                                    )
                                    val loginpage = Intent(this, Home::class.java)
                                    startActivity(loginpage)
                                    finish()
                                }
                            }

                    } else {
                        // The document was not found
                    }
                }

        }


    }
}