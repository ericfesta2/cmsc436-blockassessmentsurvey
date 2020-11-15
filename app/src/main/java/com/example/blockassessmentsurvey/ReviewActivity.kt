package com.example.blockassessmentsurvey

import android.os.Bundle
import android.view.LayoutInflater
import android.widget.*
import androidx.appcompat.app.AppCompatActivity

class ReviewActivity : AppCompatActivity() {
    private lateinit var mLayoutInflater: LayoutInflater
    private lateinit var mMainLayout: LinearLayout
    private lateinit var mSubmitButton: Button
    private lateinit var mResultsMap: MutableMap<String, Float>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.review_page)

        mLayoutInflater = getSystemService(LAYOUT_INFLATER_SERVICE) as LayoutInflater
        mMainLayout = findViewById(R.id.mainContentLayout)
        mSubmitButton = findViewById(R.id.submitButton)
        mResultsMap = mutableMapOf()

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
    }
}