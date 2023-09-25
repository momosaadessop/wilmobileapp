package com.varsitycollege.htchurchmobile

import android.content.Intent
import android.os.Bundle
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity

class Events:AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.events)
        supportActionBar?.hide()
        val loginBack = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                val intent = Intent(this@Events, Home::class.java)

                startActivity(intent)
                overridePendingTransition(0, 0)
                finish()


            }
        }
        onBackPressedDispatcher.addCallback(this, loginBack)
    }
}