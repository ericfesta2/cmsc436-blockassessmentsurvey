package com.example.blockassessmentsurvey

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth

class LoginRegisterActivity : AppCompatActivity() {
    private lateinit var emailField: EditText
    private lateinit var passField: EditText
    private lateinit var submitButton: Button
    private lateinit var switchLabel: TextView
    private lateinit var mAuth: FirebaseAuth

    private var isLogin = true

    companion object {
        const val USER_ID = "USER_ID"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.login_register)

        emailField = findViewById(R.id.editTextTextEmailAddress)
        passField = findViewById(R.id.editTextTextPassword)
        submitButton = findViewById(R.id.submitButton)
        switchLabel = findViewById(R.id.switchLabel)
        mAuth = FirebaseAuth.getInstance()

        if (mAuth.currentUser != null) {
            val mainIntent = Intent(this, MainActivity::class.java)
            mainIntent.putExtra(USER_ID, mAuth.uid)

            mainIntent.resolveActivity(packageManager)?.let {
                startActivity(mainIntent)
            }
        }

        submitButton.setOnClickListener {
            val email = emailField.text.toString()
            val pass = passField.text.toString()

            if (email.isEmpty() || pass.isEmpty()) {
                Toast.makeText(this, "Please enter a username and password.", Toast.LENGTH_LONG).show()
            } else {
                if (isLogin) {
                    mAuth.signInWithEmailAndPassword(email, pass)
                            .addOnCompleteListener { task ->
                                if (task.isSuccessful) {
                                    Toast.makeText(this, "Login successful!", Toast.LENGTH_LONG).show()

                                    val mainIntent = Intent(this, MainActivity::class.java)
                                    mainIntent.putExtra(USER_ID, mAuth.uid)

                                    mainIntent.resolveActivity(packageManager)?.let {
                                        startActivity(mainIntent)
                                    }
                                } else {
                                    Toast.makeText(this, "Login failed! Please try again later", Toast.LENGTH_LONG).show()
                                }
                            }
                } else {
                    mAuth.createUserWithEmailAndPassword(email, pass)
                            .addOnCompleteListener { task ->
                                if (task.isSuccessful) {
                                    isLogin = true
                                    Toast.makeText(this, "Registration successful! Logging in...", Toast.LENGTH_LONG).show()

                                    val mainIntent = Intent(this, MainActivity::class.java)
                                    mainIntent.putExtra(USER_ID, mAuth.uid)

                                    mainIntent.resolveActivity(packageManager)?.let {
                                        startActivity(mainIntent)
                                    }
                                } else {
                                    Toast.makeText(applicationContext, "Registration failed! Please try again.", Toast.LENGTH_LONG).show()
                                }
                            }
                }
            }
        }

        switchLabel.setOnClickListener {
            isLogin = !isLogin

            if (isLogin) {
                switchLabel.text = getString(R.string.switch_text_login)
                submitButton.text = "Log In"
            } else {
                switchLabel.text = getString(R.string.switch_text_register)
                submitButton.text = "Register"
            }
        }
    }
}