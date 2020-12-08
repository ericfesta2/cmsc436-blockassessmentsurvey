package com.example.blockassessmentsurvey

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.util.Range
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.ListView
import android.widget.ProgressBar
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import java.lang.Exception
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.util.*

class CommentsActivity : AppCompatActivity() {
    private lateinit var mAuth: FirebaseAuth
    private lateinit var mDbRef: DatabaseReference
    private lateinit var mCommentsList: ListView
    private lateinit var mProgressBar: ProgressBar

    // The list of reviews, added to when reading from Firebase
    private val reviewList = mutableListOf<Review>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.comments_page)
        title = getString(R.string.comments_title)

        mAuth = FirebaseAuth.getInstance()

        // If the user is not logged in, return to the login screen since they must be logged in at this point
        // Adapted from https://firebase.google.com/docs/auth/android/manage-users#kotlin+ktx
        if (mAuth.currentUser == null) {
            toLoginActivity()
        }

        mProgressBar = findViewById(R.id.progressBar)
        mCommentsList = findViewById(R.id.reviewsList)

        val state = intent.getStringExtra("State")
        val city = intent.getStringExtra("City")
        val street = intent.getStringExtra("Street")

        if (state == null || city == null || street == null) {
            // If one or more of the extras are null, show an error message and send the user back to MainActivity
            // to choose a new location
            Toast.makeText(this, getString(R.string.location_inval), Toast.LENGTH_LONG).show()

            val mainIntent = Intent(this, MainActivity::class.java)

            mainIntent.resolveActivity(packageManager)?.let {
                startActivity(mainIntent)
            }
        }

        val path = "$state/$city/$street"

        val database = FirebaseDatabase.getInstance()
        mDbRef = database.getReference(path)
    }

    override fun onStart() {
        super.onStart()

        // Get the review data, and extract the comments.
        // Adapted from Lab 7 - Firebase
        mDbRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                reviewList.clear()

                Log.i("View Reviews", dataSnapshot.toString())

                for (postSnapshot in dataSnapshot.children) {
                    try {
                        val review = postSnapshot.getValue(Review::class.java)!!

                        if (review.comments.isNotEmpty()) {
                            reviewList.add(review)
                        }
                    } catch (e: Exception) {
                        Log.i("View Comments", e.toString())
                    }
                }
                val dF = SimpleDateFormat("E M dd HH:mm:ss z Y", Locale.ENGLISH)

                try {
                    reviewList.sortByDescending { dF.parse(it.posted) }
                } catch (e :Exception) {

                }

                update()

                if (reviewList.isEmpty()) {
                    // If there are no comments, display a footer view UI indicating that.
                    // Adapted from Lab 11 - Location
                    val noCommentsFooter = layoutInflater.inflate(R.layout.no_comments_footer, mCommentsList, false)
                    mCommentsList.addFooterView(noCommentsFooter)
                }

                mProgressBar.visibility = View.GONE
            }

            override fun onCancelled(databaseError: DatabaseError) {
                mProgressBar.visibility = View.GONE
                Toast.makeText(this@CommentsActivity, getString(R.string.comments_fetch_failed), Toast.LENGTH_LONG).show()
            }
        })
    }

    private fun update() {
        // Make reviewList the adapter for the ListView to show the comments.
        val reviewAdapter = ReviewList(this, reviewList)
        findViewById<ListView>(R.id.reviewsList).adapter = reviewAdapter
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
            // Clear the back stack so that logged-out users cannot press the back button
            // to return to activities that require logging in without first doing so
            // Adapted from https://stackoverflow.com/questions/46048316/combine-flags-and-clear-back-trace-in-kotlin
            loginIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
            startActivity(loginIntent)
        }
    }
}