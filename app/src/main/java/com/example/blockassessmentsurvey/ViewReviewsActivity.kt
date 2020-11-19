package com.example.blockassessmentsurvey

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.floatingactionbutton.FloatingActionButton

class ViewReviewsActivity : AppCompatActivity() {
    private lateinit var mContentLayout: LinearLayout
    private lateinit var mLayoutInflater: LayoutInflater

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.page_review)
        title = "Block Reviews"

        mContentLayout = findViewById(R.id.mainContentLayout)
        mLayoutInflater = getSystemService(LAYOUT_INFLATER_SERVICE) as LayoutInflater

        findViewById<FloatingActionButton>(R.id.addReviewButton).setOnClickListener {
            val newReviewIntent = Intent(this, ReviewActivity::class.java)

            newReviewIntent.resolveActivity(packageManager)?.let {
                startActivity(newReviewIntent)
            }
        }

        findViewById<FloatingActionButton>(R.id.viewCommentsBtn).setOnClickListener {
            val commentsIntent = Intent(this, CommentsActivity::class.java)
            // TODO: putExtra block name

            commentsIntent.resolveActivity(packageManager)?.let {
                startActivity(commentsIntent)
            }
        }

        // TODO: Get rating aggregates from Firebase
        for (dimension in ratingDimensions) {
            val view = mLayoutInflater.inflate(R.layout.rating_dimension_ratingbar_view, null)

            view.findViewById<TextView>(R.id.heading).text = dimension.heading
            view.findViewById<TextView>(R.id.desc).text = dimension.desc

            mContentLayout.addView(view)
        }
    }
}