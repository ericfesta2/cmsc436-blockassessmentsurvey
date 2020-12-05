package com.example.blockassessmentsurvey

import android.content.Intent
import android.os.Bundle
import android.util.Patterns
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth

class LoginRegisterActivity : AppCompatActivity() {
    private lateinit var welcomeHeading: TextView
    private lateinit var emailField: EditText
    private lateinit var passField: EditText
    private lateinit var submitButton: Button
    private lateinit var switchLabel: TextView
    private lateinit var mAuth: FirebaseAuth

    private var isLogin = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.login_register)

        welcomeHeading = findViewById(R.id.welcomeHeading)
        emailField = findViewById(R.id.editTextTextEmailAddress)
        passField = findViewById(R.id.editTextTextPassword)
        submitButton = findViewById(R.id.submitButton)
        switchLabel = findViewById(R.id.switchLabel)
        mAuth = FirebaseAuth.getInstance()

        toMainIfLoggedIn()

        submitButton.setOnClickListener {
            val email = emailField.text.toString()
            val pass = passField.text.toString()

            // Make sure the email address is well-formed.
            // From https://developer.android.com/reference/android/util/Patterns#EMAIL_ADDRESS
            // and https://developer.android.com/reference/java/util/regex/Pattern
            val validateEmail = Patterns.EMAIL_ADDRESS.matcher(email).matches()

            if (email.isEmpty() || pass.isEmpty()) {
                // Adapted from Lab 7 - Firebase
                Toast.makeText(this, getString(R.string.email_password_req), Toast.LENGTH_LONG).show()
            }
            else if (!validateEmail) {
                Toast.makeText(this, getString(R.string.email_not_valid), Toast.LENGTH_LONG).show()
            } else {
                if (isLogin) {
                    // Adapted from Lab 7 - Firebase
                    mAuth.signInWithEmailAndPassword(email, pass)
                            .addOnCompleteListener { task ->
                                if (task.isSuccessful) {
                                    Toast.makeText(this, getString(R.string.login_success), Toast.LENGTH_LONG).show()

                                    val mainIntent = Intent(this, MainActivity::class.java)

                                    mainIntent.resolveActivity(packageManager)?.let {
                                        startActivity(mainIntent)
                                    }
                                } else {
                                    Toast.makeText(this, getString(R.string.login_failed), Toast.LENGTH_LONG).show()
                                }
                            }
                } else {
                    // Adapted from Lab 7 - Firebase
                    mAuth.createUserWithEmailAndPassword(email, pass)
                            .addOnCompleteListener { task ->
                                if (task.isSuccessful) {
                                    isLogin = true
                                    Toast.makeText(this, getString(R.string.register_success), Toast.LENGTH_LONG).show()

                                    val mainIntent = Intent(this, MainActivity::class.java)

                                    mainIntent.resolveActivity(packageManager)?.let {
                                        startActivity(mainIntent)
                                    }
                                } else {
                                    Toast.makeText(this, getString(R.string.register_failed), Toast.LENGTH_LONG).show()
                                }
                            }
                }
            }
        }

        switchLabel.setOnClickListener {
            // Change the UI based on whether or not the user wants to log in or register.
            isLogin = !isLogin

            if (isLogin) {
                welcomeHeading.text = getString(R.string.login_heading)
                switchLabel.text = getString(R.string.switch_text_login)
                submitButton.text = "Log In"
            } else {
                welcomeHeading.text = getString(R.string.register_heading)
                switchLabel.text = getString(R.string.switch_text_register)
                submitButton.text = "Register"
            }
        }
    }

    override fun onResume() {
        super.onResume()
        // If a logged-in user hits the back arrow from MainActivity, redirect them back
        // since they are already logged in
        toMainIfLoggedIn()
    }

    private fun toMainIfLoggedIn() {
        if (mAuth.currentUser != null) {
            // If a user is already logged in on this device, redirect to the main screen
            val mainIntent = Intent(this, MainActivity::class.java)

            mainIntent.resolveActivity(packageManager)?.let {
                startActivity(mainIntent)
            }
        }
    }
}