package com.example.blockassessmentsurvey

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ListView
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.floatingactionbutton.FloatingActionButton

class ViewReviewsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.page_review)
        setTitle("Block Name") // TODO: getExtra

        findViewById<FloatingActionButton>(R.id.addReviewButton).setOnClickListener() {
            val newReviewIntent = Intent(this, ReviewActivity::class.java)

            newReviewIntent.resolveActivity(packageManager)?.let {
                startActivity(newReviewIntent)
            }
        }

        findViewById<FloatingActionButton>(R.id.viewCommentsBtn).setOnClickListener() {
            val commentsIntent = Intent(this, CommentsActivity::class.java)
            // TODO: putExtra block name

            commentsIntent.resolveActivity(packageManager)?.let {
                startActivity(commentsIntent)
            }
        }

        // TODO: Firebase integration
        /*val reviewList = listOf(
            Review("Eric", "11/11/2020", "Testing!"),
            Review("Test User", "5/12/2013", "Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat. Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur. Excepteur sint occaecat cupidatat non proident, sunt in culpa qui officia deserunt mollit anim id est laborum.")
        )

        val reviewAdapter = ReviewList(this, reviewList)
        findViewById<ListView>(R.id.reviewsList).adapter = reviewAdapter*/
    }
}