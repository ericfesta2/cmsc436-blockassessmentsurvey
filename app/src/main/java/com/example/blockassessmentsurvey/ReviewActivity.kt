package com.example.blockassessmentsurvey

import android.os.Bundle
import android.view.LayoutInflater
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
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

        mLayoutInflater = getSystemService(LAYOUT_INFLATER_SERVICE) as LayoutInflater
        mMainLayout = findViewById(R.id.mainContentLayout)
        mSubmitButton = findViewById(R.id.submitButton)
        mResultsMap = mutableMapOf()


        var mBlockName = findViewById<TextView>(R.id.textView2)

        val state = intent.getStringExtra("State")
        val city = intent.getStringExtra("City")
        val street = intent.getStringExtra("Street")

        val path = state + "/" + city + "/" + street

        val database = FirebaseDatabase.getInstance()
        ref = database.getReference("/test")

        mBlockName.text = street


        for (dimension in ratingDimensions) {
            val view = mLayoutInflater.inflate(R.layout.rating_dimension_ratingbar_edit, null)

            view.findViewById<TextView>(R.id.heading).text = dimension.heading
            view.findViewById<TextView>(R.id.desc).text = dimension.desc

            val ratingBar = view.findViewById<RatingBar>(R.id.ratingBar)

            ratingBar.setOnRatingBarChangeListener() { rb: RatingBar, fl: Float, b: Boolean ->
                if (fl > 0) {
                    mResultsMap[dimension.id] = fl
                } else {
                    mResultsMap.remove(dimension.id)
                }
            }

            mMainLayout.addView(view)
        }

        mSubmitButton.setOnClickListener() {submitReview()}
    }

    private fun submitReview() {
        // TODO: Change as necessary (add unique ids to RatingBars?) and add Firebase integration
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
            ref.setValue("test review")
            finish()
        } catch (e: Error) {

        }
    }
}