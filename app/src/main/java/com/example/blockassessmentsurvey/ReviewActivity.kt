package com.example.blockassessmentsurvey

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuItem
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.database.*
import java.util.*
import com.google.firebase.auth.FirebaseAuth

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
        title = "New Review"

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

        var mBlockName = findViewById<TextView>(R.id.blockName)

        val state = intent.getStringExtra("State")
        val city = intent.getStringExtra("City")
        val street = intent.getStringExtra("Street")
        val userName = mAuth.uid

        val path = "$state/$city/$street/$userName"

        val database = FirebaseDatabase.getInstance()
        ref = database.getReference(path)

        ref.addValueEventListener( object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.

                //val value = dataSnapshot.getValue(String.class)
                Log.i("TAG", "HERE")
            }

            override fun onCancelled(error: DatabaseError) {

            }}
        )

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
            Toast.makeText(this, "Please rate at least one factor.", Toast.LENGTH_LONG).show()
            return
        }


        val comments = findViewById<EditText>(R.id.commentMultiline).text

        try {
            val review = Review(
                mAuth.currentUser!!.uid,
                mResultsMap,
                Date().toString(),
                comments.toString()
            )

            ref.setValue(review)
            Toast.makeText(this, "Your review has been posted!", Toast.LENGTH_LONG).show()
            finish()
        } catch (e: Error) {

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