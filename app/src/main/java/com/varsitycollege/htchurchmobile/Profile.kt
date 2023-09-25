package com.varsitycollege.htchurchmobile

import android.content.Intent
import android.os.Bundle
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity

class Profile:AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.profiles)
        supportActionBar?.hide()
        val loginBack = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                val intent = Intent(this@Profile, Home::class.java)

                startActivity(intent)
                overridePendingTransition(0, 0)
                finish()


            }
        }
        onBackPressedDispatcher.addCallback(this, loginBack)
    }
}