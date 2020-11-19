package com.example.blockassessmentsurvey

import android.os.Bundle
import android.widget.ListView
import androidx.appcompat.app.AppCompatActivity

class CommentsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.comments_page)
        title = "Comments"

        val reviewList = listOf(
                Review("Eric", 5f, 5f, 5f, 5f, "11/11/2020", "Testing!"),
                Review("Test User", 5f, 5f, 5f, 5f,"5/12/2013", "Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat. Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur. Excepteur sint occaecat cupidatat non proident, sunt in culpa qui officia deserunt mollit anim id est laborum."),
                Review("Test User", 5f, 5f, 5f, 5f,"5/12/2013", "Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat. Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur. Excepteur sint occaecat cupidatat non proident, sunt in culpa qui officia deserunt mollit anim id est laborum.")
        )

        val reviewAdapter = ReviewList(this, reviewList)
        findViewById<ListView>(R.id.reviewsList).adapter = reviewAdapter
    }
}