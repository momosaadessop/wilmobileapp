package com.varsitycollege.htchurchmobile

import android.content.ContentValues
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.drawerlayout.widget.DrawerLayout
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CircleCrop
import com.bumptech.glide.request.RequestOptions
import com.github.clans.fab.FloatingActionButton
import com.github.clans.fab.FloatingActionMenu
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.ktx.storage
import java.io.ByteArrayOutputStream

class Home:AppCompatActivity() {
    private lateinit var drawerLayout: DrawerLayout
    private lateinit var actionBarDrawerToggle: ActionBarDrawerToggle
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.home)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        securGuard()
        details()
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

                    val intent = Intent(this, ViewPastors::class.java)

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
                    val intent = Intent(this, secratary::class.java)

                    startActivity(intent)
                    overridePendingTransition(0, 0)
                    finish()
                    true
                }

                R.id.menu_church_details-> {

                    val intent = Intent(this,ChurchDetails::class.java)

                    startActivity(intent)
                    overridePendingTransition(0, 0)
                    finish()
                    true
                }
                else -> false
            }


        }
        val menu: FloatingActionMenu = findViewById(R.id.menu)

        val profiles: FloatingActionButton = findViewById(R.id.profile_button)
        profiles.setOnClickListener {
            val intent = Intent(this, Profile::class.java)

            startActivity(intent)
            overridePendingTransition(0, 0)
            finish()
            menu.close(true)
        }

        val members: FloatingActionButton = findViewById(R.id.member)
        members.setOnClickListener {

            val intent = Intent(this, Members::class.java)

            startActivity(intent)
            overridePendingTransition(0, 0)
            finish()

            menu.close(true)
        }

        val exit: FloatingActionButton = findViewById(R.id.exit)
        exit.setOnClickListener {
            signout()
            val intent = Intent(this, Login::class.java)

            startActivity(intent)
            overridePendingTransition(0, 0)
            finish()
            menu.close(true)
        }
        val events: FloatingActionButton = findViewById(R.id.event)
        events.setOnClickListener {

            val intent = Intent(this, Events::class.java)

            startActivity(intent)
            overridePendingTransition(0, 0)
            finish()
            menu.close(true)
        }
        val pastors: FloatingActionButton = findViewById(R.id.pastor)
        pastors.setOnClickListener {

            val intent = Intent(this, ViewPastors::class.java)

            startActivity(intent)
            overridePendingTransition(0, 0)
            finish()
            menu.close(true)
        }

        val financials: FloatingActionButton = findViewById(R.id.finance)
        financials.setOnClickListener {

            val intent = Intent(this, Finances::class.java)

            startActivity(intent)
            overridePendingTransition(0, 0)
            finish()
            menu.close(true)
        }
        drawerLayout = findViewById(R.id.drawer_layout)
        actionBarDrawerToggle =
            ActionBarDrawerToggle(this, drawerLayout, R.string.nav_open, R.string.nav_close)

        drawerLayout.addDrawerListener(actionBarDrawerToggle)
        actionBarDrawerToggle.syncState()


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
    fun details() {
        val user = FirebaseAuth.getInstance().currentUser
        val userEmail = user?.email
        val parts = userEmail!!.split('@', '.')
        val userID = parts[0] + parts[1]
        val storage = FirebaseStorage.getInstance()
        val imageRef = storage.getReference().child(userID)
        val navigationView: NavigationView = findViewById(R.id.nav_view)
        val email = navigationView.getHeaderView(0).findViewById<TextView>(R.id.nav_header_email)
        val name = navigationView.getHeaderView(0).findViewById<TextView>(R.id.nav_header_name)
        val image = navigationView.getHeaderView(0).findViewById<ImageView>(R.id.nav_header_image)
        email.text = userEmail
        imageRef.downloadUrl.addOnSuccessListener { Uri ->
            val url = Uri.toString()

            val profileimage = RequestOptions().transform(CircleCrop())
            Glide.with(this)
                .load(url)
                .apply(profileimage)
                .into(image)
        }

        image.setOnClickListener()
        {


            val profileimage = AlertDialog.Builder(this)
            profileimage.setTitle("Choose an option")
            profileimage.setItems(
                arrayOf(
                    "Take a photo?",
                    "Pick from gallery?",

                    )
            ) { _, which ->
                when (which) {

                    0 -> camera.launch(null)
                    1 -> galleryContent.launch("imageURL/*")

                }
            }
            val actionshow = profileimage.create()
            actionshow.show()

        }



        Log.d("userid", userID)
        val userfiles = FirebaseFirestore.getInstance()
        val data = userfiles.collection("pastors").document(userID)
        data.get()
            .addOnSuccessListener { document ->
                if (document != null) {
                    Log.d(ContentValues.TAG, "DocumentSnapshot data: ${document.data}")
                    val profiledata = document.get("userDetails") as Map<String, Any>
                    val names = profiledata["firstname"].toString()
                    val surnames = profiledata["surname"].toString()
                    name.text = names + "" + surnames

                } else {
                    Log.d(ContentValues.TAG, "No such document")
                }
            }
            .addOnFailureListener { exception ->
                Log.d(ContentValues.TAG, "get failed with ", exception)
            }

    }
    private val galleryContent =
        registerForActivityResult(ActivityResultContracts.GetContent()) { url: Uri? ->


            when {
                url != null -> {
                    val user = FirebaseAuth.getInstance().currentUser

                    val userEmail = user?.email
                    val parts = userEmail!!.split('@', '.')
                    val userID = parts[0] + parts[1]
                    val navigationView: NavigationView = findViewById(R.id.nav_view)
                    var image = navigationView.getHeaderView(0).findViewById<ImageView>(R.id.nav_header_image)

                    image.setImageURI(url)

                    val store =
                        Firebase.storage.reference.child(userID.lowercase())

                    val choice = store.putFile(url)
                    choice.addOnSuccessListener {

                    }.addOnFailureListener {

                    }
                }
            }
        }
    private val camera =
        registerForActivityResult(ActivityResultContracts.TakePicturePreview()) { photo: Bitmap? ->
            val user = FirebaseAuth.getInstance().currentUser

            val userEmail = user?.email
            val parts = userEmail!!.split('@', '.')
            val userID = parts[0] + parts[1]
            val navigationView: NavigationView = findViewById(R.id.nav_view)
            var image = navigationView.getHeaderView(0).findViewById<ImageView>(R.id.nav_header_image)



            image.setImageBitmap(photo)


            val imageRef = Firebase.storage.reference.child(userID.lowercase())


            val imageStream = ByteArrayOutputStream()
            photo?.compress(Bitmap.CompressFormat.JPEG, 100, imageStream)
            val data = imageStream.toByteArray()

            val uploadDP = imageRef.putBytes(data)
            uploadDP.addOnSuccessListener {
                val message = "IMAGE UPLOADED "
                Toast.makeText(applicationContext, message, Toast.LENGTH_SHORT).show()
            }.addOnFailureListener {
                val message = "INVALID IMAGE!"
                Toast.makeText(applicationContext, message, Toast.LENGTH_SHORT).show()
            }
        }
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return if (actionBarDrawerToggle.onOptionsItemSelected(item)) {
            true
        } else super.onOptionsItemSelected(item)
    }
    private fun signout() {
        FirebaseAuth.getInstance().signOut()
    }
    fun IDload() {

        val churchid: EditText = findViewById(R.id.profile_church_id)

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

                    churchid.setText(id)

                } else {
                    Log.d(ContentValues.TAG, "No such document")
                }
            }
            .addOnFailureListener { exception ->
                Log.d(ContentValues.TAG, "get failed with ", exception)
            }


    }
}