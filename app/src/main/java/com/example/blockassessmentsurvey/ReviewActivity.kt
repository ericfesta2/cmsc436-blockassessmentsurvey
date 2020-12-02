package com.example.blockassessmentsurvey

import android.os.Bundle
import android.view.LayoutInflater
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.database.FirebaseDatabase
import org.json.JSONObject

class ReviewActivity : AppCompatActivity() {
    private lateinit var mLayoutInflater: LayoutInflater
    private lateinit var mMainLayout: LinearLayout
    private lateinit var mSubmitButton: Button
    private lateinit var mResultsMap: MutableMap<String, Float>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.review_page)
        title = "Review Block"

        mLayoutInflater = getSystemService(LAYOUT_INFLATER_SERVICE) as LayoutInflater
        mMainLayout = findViewById(R.id.mainContentLayout)
        mSubmitButton = findViewById(R.id.submitButton)
        mResultsMap = mutableMapOf()

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
        //how do I extract the data I need to push?
        var database = FirebaseDatabase.getInstance();
        var entries = database.getReference("newEntryLocationName")

        var id = entries.push().key

        val newEntry = JSONObject()
        newEntry.put("user","username")
        newEntry.put("aspect1","rating1")

        if (id != null) {
            entries.child(id).setValue(newEntry)
        }


        println(mResultsMap)
        val comments = findViewById<EditText>(R.id.commentMultiline).text
        Toast.makeText(this, "Your review has been posted!", Toast.LENGTH_LONG).show()
        finish()
    }
}