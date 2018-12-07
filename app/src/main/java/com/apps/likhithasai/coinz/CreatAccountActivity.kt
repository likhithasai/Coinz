package com.apps.likhithasai.coinz

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.Window
import android.view.WindowManager
import android.widget.*
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest

class CreatAccountActivity : AppCompatActivity() {

    private val tag = "LoginActivity"
    //global variables
    //private var email: String? = null
    private var password: String? = null
    //UI elements
    private var tvForgotPassword: TextView? = null
    private var etEmail: EditText? = null
    private var etPassword: EditText? = null
    private var btnLogin: Button? = null
    private var btnCreateAccount: Button? = null
    private var mProgressBar: ProgressBar? = null
    //Firebase references
    private var mAuth: FirebaseAuth? = null
    private var prefs: SharedPrefs? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        //Remove title bar
        this.requestWindowFeature(Window.FEATURE_NO_TITLE)

        //Remove notification bar
        this.window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN)
        setContentView(R.layout.activity_creat_account)
        initialise()
    }

    private fun initialise() {
        etEmail = findViewById(R.id.et_email)
        etPassword = findViewById(R.id.et_password)
        btnCreateAccount = findViewById(R.id.btn_create_account)
        mAuth = FirebaseAuth.getInstance()

        btnCreateAccount!!.setOnClickListener { createUser() }
    }



    private fun createUser() {
        val email = etEmail?.text.toString()
        val password = etPassword?.text.toString()

        if (!TextUtils.isEmpty(email) && !TextUtils.isEmpty(password)) {
            mAuth!!.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this) { task ->
                        if (task.isSuccessful) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(tag, "createUserWithEmail:success")
                            Log.d(tag, "Creating user $email $password")
                            updateUI()
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(tag, "createUserWithEmail:failure", task.exception)
                            Toast.makeText(baseContext, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show()
                        }

                        // ...
                    }
//            mAuth!!.signInWithEmailAndPassword(email, password!!)
//                    .addOnCompleteListener(this) { task ->
//                        if (task.isSuccessful) {
//                            // Sign in success, update UI with signed-in user's information
//                            val user = mAuth?.currentUser
//                            val profileUpdates = UserProfileChangeRequest.Builder()
//                                    .setDisplayName(mName).build()
//                            user?.updateProfile(profileUpdates)
//
//                            prefs = SharedPrefs(applicationContext)
//                            prefs!!.currentUserName = mName
//                            prefs!!.currentUser = user!!.uid
//                            Log.d(tag, "signInWithEmail:success")
//                            updateUI()
//                        } else {
//                            // If sign in fails, display a message to the user.
//                            Log.e(tag, "signInWithEmail:failure", task.exception)
//                            Toast.makeText(this@LoginActivity, "Authentication failed.",
//                                    Toast.LENGTH_SHORT).show()
//                        }
//                    }
        } else {
            Toast.makeText(this, "Enter all credentials", Toast.LENGTH_SHORT).show()
        }

    }

    private fun updateUI() {
        val intent = Intent(this@CreatAccountActivity, LoginActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        startActivity(intent)
        finish()
    }

}