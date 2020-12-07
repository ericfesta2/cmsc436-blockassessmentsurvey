package com.example.blockassessmentsurvey

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth

/**
 * This splash screen acts as a loading screen of sorts that hides the interface as the app figures out
 * which screen should be displayed first based on whether or not the user is logged in.
 * For instance, if the user is logged in, without this screen the login screen would flash at first,
 * which is undesirable from a UI perspective.
 */
class SplashScreenActivity : AppCompatActivity() {
    private lateinit var mAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.splash_screen)

        mAuth = FirebaseAuth.getInstance()

        // Go to MainActivity if already logged in, LoginRegisterActivity otherwise
        val intent =
            Intent(this, if (mAuth.currentUser != null)
                MainActivity::class.java
            else LoginRegisterActivity::class.java)

        intent.resolveActivity(packageManager)?.let {
            startActivity(intent)
            finish()
        }
    }
}