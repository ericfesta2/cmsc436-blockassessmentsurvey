package com.example.blockassessmentsurvey

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.database.*
import java.lang.Exception
import com.google.firebase.auth.FirebaseAuth

class ViewReviewsActivity : AppCompatActivity() {
    private lateinit var mAuth: FirebaseAuth
    private lateinit var mDbRef: DatabaseReference
    private lateinit var mContentLayout: LinearLayout
    private lateinit var mLayoutInflater: LayoutInflater
    private lateinit var mViewIds: MutableMap<String, Pair<Int, Int>>
    private lateinit var mRatingCounts: MutableMap<String, Pair<Long, Double>>
    private lateinit var mAddReviewButton: FloatingActionButton
    private lateinit var mHasReviewedTxt: TextView
    private lateinit var mProgressBar: ProgressBar

    private var hasReviewedBlock = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.page_review)
        title = getString(R.string.view_reviews_title)

        mAuth = FirebaseAuth.getInstance()

        // If the user is not logged in, return to the login screen since they must be logged in at this point
        // Adapted from https://firebase.google.com/docs/auth/android/manage-users#kotlin+ktx
        if (mAuth.currentUser == null) {
            toLoginActivity()
        }

        // ProgressBar: https://developer.android.com/reference/android/widget/ProgressBar
        mProgressBar = findViewById(R.id.progressBar)

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

        mContentLayout = findViewById(R.id.mainContentLayout)
        mLayoutInflater = getSystemService(LAYOUT_INFLATER_SERVICE) as LayoutInflater
        mViewIds = mutableMapOf()
        mRatingCounts = mutableMapOf()
        mHasReviewedTxt = findViewById(R.id.hasReviewedTxt)
        mAddReviewButton = findViewById(R.id.addReviewButton)
        // Hide the Add Review button while the data is gathered.
        // That way, no one can exploit the system to review a location twice.
        mAddReviewButton.visibility = View.GONE

        val mBlockNameText = findViewById<TextView>(R.id.blockName)
        mBlockNameText.text = street

        val path = "$state/$city/$street"

        val database = FirebaseDatabase.getInstance()
        mDbRef = database.getReference(path)

        findViewById<FloatingActionButton>(R.id.addReviewButton).setOnClickListener {
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
            intent.putExtra("State", state)
            intent.putExtra("City", city)
            intent.putExtra("Street", street)

            intent.resolveActivity(packageManager)?.let {
                startActivity(intent)
            }
        }

        for (dimension in ratingDimensions) {
            // For each rating dimension in RatingDimensions.kt, create an immutable rating bar
            // and review counter. They will be populated when the Firebase data is obtained below.
            val view = mLayoutInflater.inflate(R.layout.rating_dimension_ratingbar_view, null)

            view.findViewById<TextView>(R.id.heading).text = dimension.heading
            view.findViewById<TextView>(R.id.desc).text = dimension.desc

            val ratingBar = view.findViewById<RatingBar>(R.id.ratingBar)
            val reviewCounter = view.findViewById<TextView>(R.id.numReviews)
            // Generate unique IDs for each rating bar and rating counter
            // so they can be updated with the Firebase results below.
            // View.generateViewId() docs: https://developer.android.com/reference/android/view/View
            val ratingBarId = View.generateViewId()
            val counterId = View.generateViewId()
            mViewIds[dimension.id] = ratingBarId to counterId
            ratingBar.id = ratingBarId
            reviewCounter.id = counterId

            mContentLayout.addView(view)
        }
    }

    override fun onStart() {
        super.onStart()

        // Read the Firebase data from the given location (if any) and update the UI accordingly.
        // Adapted from Lab 7 - Firebase
        mDbRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                mRatingCounts.clear()
                hasReviewedBlock = false

                if (!dataSnapshot.exists()) {
                    // If there are no reviews, show the Add Review button,
                    // since the current user has not submitted one for this location either
                    toggleAddReviewBtn()
                }

                for (postSnapshot in dataSnapshot.children) {
                    try {
                        val review = postSnapshot.getValue(Review::class.java)!!

                        for ((k, v) in review.reviews) {
                            if (mRatingCounts[k] == null) {
                                mRatingCounts[k] = 1L to v.toDouble()
                            } else {
                                mRatingCounts[k] =
                                        mRatingCounts[k]!!.first + 1 to mRatingCounts[k]!!.second + v
                            }

                            if (review.reviewer == mAuth.uid) {
                                hasReviewedBlock = true
                            }
                        }

                        for ((k, v) in mRatingCounts) {
                            if (v.first > 0) {
                                findViewById<RatingBar>(mViewIds[k]!!.first).rating =
                                        (v.second / v.first).toFloat()
                                findViewById<TextView>(mViewIds[k]!!.second).text =
                                        "${v.first} Rating${if (v.first > 1) "s" else ""}"
                            }
                        }
                    } catch (e: Exception) {
                        Log.e("ViewReviewsActivity", e.toString())
                        Toast.makeText(this@ViewReviewsActivity, getString(R.string.review_fetch_failed), Toast.LENGTH_LONG).show()
                    } finally {
                        toggleAddReviewBtn()
                    }
                }

                mProgressBar.visibility = View.GONE
            }

            override fun onCancelled(databaseError: DatabaseError) {
                mProgressBar.visibility = View.GONE
                Toast.makeText(this@ViewReviewsActivity, getString(R.string.review_fetch_failed), Toast.LENGTH_LONG).show()
            }
        })
    }

    private fun toggleAddReviewBtn() {
        if (hasReviewedBlock) {
            mAddReviewButton.visibility = View.GONE
            mHasReviewedTxt.text = getString(R.string.has_reviewed)
        } else {
            mAddReviewButton.visibility = View.VISIBLE
            mHasReviewedTxt.text = getString(R.string.has_not_reviewed)
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
            // Clear the back stack so that logged-out users cannot press the back button
            // to return to activities that require logging in without first doing so
            // Adapted from https://stackoverflow.com/questions/46048316/combine-flags-and-clear-back-trace-in-kotlin
            loginIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
            startActivity(loginIntent)
        }
    }
}