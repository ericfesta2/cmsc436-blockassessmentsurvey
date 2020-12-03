package com.example.blockassessmentsurvey

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.ListView
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth

class CommentsActivity : AppCompatActivity() {
    private lateinit var mAuth: FirebaseAuth
    private lateinit var mCommentsList: ListView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.comments_page)
        title = "Comments"

        mAuth = FirebaseAuth.getInstance()

        if (mAuth.currentUser == null) {
            toLoginActivity()
        }

        mCommentsList = findViewById(R.id.reviewsList)

        val reviewList = listOf(
                Review("Eric", 5f, 5f, 5f, 5f, "11/11/2020", "Testing!"),
                Review("Test User", 5f, 5f, 5f, 5f,"5/12/2013", "Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat. Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur. Excepteur sint occaecat cupidatat non proident, sunt in culpa qui officia deserunt mollit anim id est laborum."),
                Review("Test User", 5f, 5f, 5f, 5f,"5/12/2013", "Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat. Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur. Excepteur sint occaecat cupidatat non proident, sunt in culpa qui officia deserunt mollit anim id est laborum.")
        )

        if (reviewList.isEmpty()) {
            val noCommentsFooter = layoutInflater.inflate(R.layout.no_comments_footer, mCommentsList, false)
            mCommentsList.addFooterView(noCommentsFooter)
        }

        val reviewAdapter = ReviewList(this, reviewList)
        findViewById<ListView>(R.id.reviewsList).adapter = reviewAdapter
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