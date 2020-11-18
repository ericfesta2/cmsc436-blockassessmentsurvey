package com.example.blockassessmentsurvey

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.database.*
import java.util.*


class ReviewActivity : AppCompatActivity() {
    private lateinit var mLayoutInflater: LayoutInflater
    private lateinit var mMainLayout: LinearLayout
    private lateinit var mSubmitButton: Button
    private lateinit var mResultsMap: MutableMap<String, Float>
    private lateinit var ref: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.review_page)
        title = "Review Block"

        mLayoutInflater = getSystemService(LAYOUT_INFLATER_SERVICE) as LayoutInflater
        mMainLayout = findViewById(R.id.mainContentLayout)
        mSubmitButton = findViewById(R.id.submitButton)
        mResultsMap = mutableMapOf()


        var mBlockName = findViewById<TextView>(R.id.textView2)

        val state = intent.getStringExtra("State")
        val city = intent.getStringExtra("City")
        val street = intent.getStringExtra("Street")
        val userName = "Benny" //TODO: set this to the username once authentication system is setup

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
                TODO("Not yet implemented")
            }}
        )


        mBlockName.text = street


        for (dimension in ratingDimensions) {
            val view = mLayoutInflater.inflate(R.layout.rating_dimension_ratingbar_edit, null)

            view.findViewById<TextView>(R.id.heading).text = dimension.heading
            view.findViewById<TextView>(R.id.desc).text = dimension.desc

            val ratingBar = view.findViewById<RatingBar>(R.id.ratingBar)

            ratingBar.setOnRatingBarChangeListener { rb: RatingBar, fl: Float, b: Boolean ->
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

        // TODO: Push data to Firebase
        println(mResultsMap)
        val comments = findViewById<EditText>(R.id.commentMultiline).text

        try {
            val review = Review(
                "Enter reviewer name",
                mResultsMap["d_safety"]!!,
                mResultsMap["d_air_quality"]!!,
                mResultsMap["d_cleanliness"]!!,
                mResultsMap["d_parking_spaces"]!!,
                Date().toString(),
                comments.toString()
            )
            ref.setValue(review)
            Toast.makeText(this, "Your review has been posted!", Toast.LENGTH_LONG).show()
            finish()
        } catch (e: Error) {

        }
    }
}