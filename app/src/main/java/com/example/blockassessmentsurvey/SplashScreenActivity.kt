package com.example.blockassessmentsurvey

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth

class SplashScreenActivity : AppCompatActivity() {
    private lateinit var mAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.splash_screen)

        mAuth = FirebaseAuth.getInstance()

        val intent =
            Intent(this, if (mAuth.currentUser != null) MainActivity::class.java else LoginRegisterActivity::class.java)

        intent.resolveActivity(packageManager)?.let {
            startActivity(intent)
            finish()
        }
    }
}