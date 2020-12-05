package com.example.blockassessmentsurvey

import android.content.Intent
import android.os.Bundle
import android.util.Half.toFloat
import android.util.Log
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuItem
import android.widget.LinearLayout
import android.widget.RatingBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.database.*
import java.lang.Exception
import com.google.firebase.auth.FirebaseAuth

class ViewReviewsActivity : AppCompatActivity() {
    private lateinit var mAuth: FirebaseAuth
    private lateinit var mContentLayout: LinearLayout
    private lateinit var mLayoutInflater: LayoutInflater

    var safety: Float = 0f
    var air: Float = 0f
    var cleanliness: Float = 0f
    var parking: Float = 0f

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.page_review)

        mAuth = FirebaseAuth.getInstance()

        // If the user is not logged in, return to the login screen since they must be logged in at this point
        // Adapted from https://firebase.google.com/docs/auth/android/manage-users#kotlin+ktx
        if (mAuth.currentUser == null) {
            toLoginActivity()
        }

        val state = intent.getStringExtra("State")
        val city = intent.getStringExtra("City")
        val street = intent.getStringExtra("Street")
        title = "Reviews"

        mContentLayout = findViewById(R.id.mainContentLayout)
        mLayoutInflater = getSystemService(LAYOUT_INFLATER_SERVICE) as LayoutInflater

        val mBlockNameText = findViewById<TextView>(R.id.blockName)
        mBlockNameText.text = street

        val path = "$state/$city/$street"

        val database = FirebaseDatabase.getInstance()
        val ref = database.getReference(path)

        findViewById<FloatingActionButton>(R.id.addReviewButton).setOnClickListener() {
            val intent = Intent(this, ReviewActivity::class.java)
            intent.putExtra("State", state)
            intent.putExtra("City", city)
            intent.putExtra("Street", street)

            intent.resolveActivity(packageManager)?.let {
                startActivity(intent)
            }
        }

        findViewById<FloatingActionButton>(R.id.viewCommentsBtn).setOnClickListener {
            val intent = Intent(this, CommentsActivity::class.java)
            // TODO: putExtra block name
            intent.putExtra("State", state)
            intent.putExtra("City", city)
            intent.putExtra("Street", street)

            intent.resolveActivity(packageManager)?.let {
                startActivity(intent)
            }
        }

        ref.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                safety = 0f
                air= 0f
                cleanliness = 0f
                parking = 0f

                Log.i("View Reviews", dataSnapshot.toString())
                val reviews = dataSnapshot.value as Map<*, Map<String, *>>
                val reviews1 = reviews.values
                for (review in reviews1) {
                    try {
                        Log.i("View Reviews", review.get("safety").toString())
                        safety = safety.plus(review["safety"] as Long)
                        air = air.plus(review["air"] as Long)
                        cleanliness = cleanliness.plus(review["cleanliness"] as Long)
                        parking = parking.plus(review["parking"] as Long)
                    } catch (e: Exception) {
                        Log.i("View Reviews", e.toString())
                    } finally {

                    }
                }

                Log.i("Safety", safety.toString())
                safety /= reviews1.count()
                air /= reviews1.count()
                cleanliness /= reviews1.count()
                parking /= reviews1.count()
                getStars()
            }

            override fun onCancelled(databaseError: DatabaseError) {

            }
        })
    }

    // TODO: Get rating aggregates from Firebase
    fun getStars() {
        for (dimension in ratingDimensions) {
            val view = mLayoutInflater.inflate(R.layout.rating_dimension_ratingbar_view, null)

            view.findViewById<TextView>(R.id.heading).text = dimension.heading
            view.findViewById<TextView>(R.id.desc).text = dimension.desc

            var stars = when (dimension.id) {
                "d_safety" -> safety
                "d_air_quality" -> air
                "d_cleanliness" -> cleanliness
                "d_parking_spaces" -> parking
                else -> 0f
            }

            view.findViewById<RatingBar>(R.id.ratingBar).rating = stars

            mContentLayout.addView(view)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.logout, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.log_out_item -> {
                mAuth.signOut()
                toLoginActivity()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun toLoginActivity() {
        val loginIntent = Intent(this, LoginRegisterActivity::class.java)

        intent.resolveActivity(packageManager)?.let {
            startActivity(loginIntent)
        }
    }
}