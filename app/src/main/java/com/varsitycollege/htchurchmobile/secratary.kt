package com.varsitycollege.htchurchmobile

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.ImageButton
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity

class secratary:AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.secretary)
        supportActionBar?.hide()

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
            Log.d("clicked","i am clicked")
        }
    }
}