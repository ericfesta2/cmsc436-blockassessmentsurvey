package com.example.blockassessmentsurvey

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuItem
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.database.*
import java.util.*
import com.google.firebase.auth.FirebaseAuth

/**
 * The screen where users create and post reviews.
 */
class ReviewActivity : AppCompatActivity() {
    private lateinit var mAuth: FirebaseAuth
    private lateinit var mLayoutInflater: LayoutInflater
    private lateinit var mMainLayout: LinearLayout
    private lateinit var mSubmitButton: Button
    private lateinit var mResultsMap: MutableMap<String, Float>
    private lateinit var ref: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.review_page)
        title = getString(R.string.review_title)

        mAuth = FirebaseAuth.getInstance()

        // If the user is not logged in, return to the login screen since they must be logged in at this point
        // Adapted from https://firebase.google.com/docs/auth/android/manage-users#kotlin+ktx
        if (mAuth.currentUser == null) {
            toLoginActivity()
        }

        mLayoutInflater = getSystemService(LAYOUT_INFLATER_SERVICE) as LayoutInflater
        mMainLayout = findViewById(R.id.mainContentLayout)
        mSubmitButton = findViewById(R.id.submitButton)
        mResultsMap = mutableMapOf()

        val mBlockName = findViewById<TextView>(R.id.blockName)

        val state = intent.getStringExtra("State")
        val city = intent.getStringExtra("City")
        val street = intent.getStringExtra("Street")
        val userName = mAuth.uid

        if (state == null || city == null || street == null) {
            // If one or more of the extras are null, show an error message and send the user back to MainActivity
            // to choose a new location
            Toast.makeText(this, getString(R.string.location_inval), Toast.LENGTH_LONG).show()

            val mainIntent = Intent(this, MainActivity::class.java)

            mainIntent.resolveActivity(packageManager)?.let {
                startActivity(mainIntent)
            }
        }

        val path = "$state/$city/$street/$userName"

        val database = FirebaseDatabase.getInstance()
        ref = database.getReference(path)

        mBlockName.text = street

        for (dimension in ratingDimensions) {
            val view = mLayoutInflater.inflate(R.layout.rating_dimension_ratingbar_edit, null)

            view.findViewById<TextView>(R.id.heading).text = dimension.heading
            view.findViewById<TextView>(R.id.desc).text = dimension.desc

            val ratingBar = view.findViewById<RatingBar>(R.id.ratingBar)

            // RatingBar docs: https://developer.android.com/reference/android/widget/RatingBar
            ratingBar.setOnRatingBarChangeListener { _: RatingBar, fl: Float, _: Boolean ->
                if (fl > 0) {
                    mResultsMap[dimension.id] = fl
                } else {
                    mResultsMap.remove(dimension.id)
                }
            }

            mMainLayout.addView(view)
        }

        mSubmitButton.setOnClickListener {submitReview()}
    }

    private fun submitReview() {
        if (mResultsMap.isEmpty()) {
            // Users are allowed to leave some dimensions blank, but they must rate at least one.
            Toast.makeText(this, "Please rate at least one factor.", Toast.LENGTH_LONG).show()
            return
        }

        // Get the Other Comments. It is fine if this is empty.
        val comments = findViewById<EditText>(R.id.commentMultiline).text

        try {
            // Post the review.
            val review = Review(
                mAuth.currentUser!!.uid,
                mResultsMap,
                Date().toString(),
                comments.toString()
            )

            ref.setValue(review)
            Toast.makeText(this, getString(R.string.review_success), Toast.LENGTH_LONG).show()
            finish()
        } catch (e: Error) {
            Toast.makeText(this, getString(R.string.review_error), Toast.LENGTH_LONG).show()
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