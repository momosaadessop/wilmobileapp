package com.varsitycollege.htchurchmobile

import android.content.Intent
import android.os.Bundle
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth

class Home:AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.home)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        val navigationView: NavigationView = findViewById(R.id.nav_view)
        navigationView.setNavigationItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.menu_profile -> {
                    val intent = Intent(this, Profile::class.java)

                    startActivity(intent)
                    overridePendingTransition(0, 0)
                    finish()
                    true
                }

                R.id.menu_event -> {
                    val intent = Intent(this, Events::class.java)

                    startActivity(intent)
                    overridePendingTransition(0, 0)
                    finish()
                    true
                }

                R.id.menu_logout -> {
                    signout()
                    val intent = Intent(this, Login::class.java)

                    startActivity(intent)
                    overridePendingTransition(0, 0)
                    finish()
                    true
                }

                R.id.menu_pastor-> {

                    val intent = Intent(this, Pastors::class.java)

                    startActivity(intent)
                    overridePendingTransition(0, 0)
                    finish()
                    true
                }

                R.id.menu_member-> {

                    val intent = Intent(this, Members::class.java)

                    startActivity(intent)
                    overridePendingTransition(0, 0)
                    finish()
                    true
                }

                R.id.menu_finance -> {

                    val intent = Intent(this, Finances::class.java)

                    startActivity(intent)
                    overridePendingTransition(0, 0)
                    finish()
                    true
                }

                R.id.menu_secretary -> {

                    true
                }

                else -> false
            }

        }

    }
    private fun signout() {
        FirebaseAuth.getInstance().signOut()
    }
}