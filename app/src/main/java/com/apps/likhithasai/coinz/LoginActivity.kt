package com.apps.likhithasai.coinz

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.widget.*
import android.content.Intent
import com.google.firebase.auth.FirebaseAuth
import android.text.TextUtils
import android.util.Log
import com.google.firebase.auth.UserProfileChangeRequest
import android.view.Window
import android.view.WindowManager


class LoginActivity: AppCompatActivity() {
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
        setContentView(R.layout.activity_login)
        initialise()
    }

    private fun initialise() {
        tvForgotPassword = findViewById(R.id.tv_forgot_password)
        etEmail = findViewById(R.id.et_email)
        etPassword = findViewById(R.id.et_password)
        btnLogin = findViewById(R.id.btn_login)
        btnCreateAccount = findViewById(R.id.btn_register_account)
        mProgressBar = ProgressBar(this)
        mAuth = FirebaseAuth.getInstance()
//        btnCreateAccount!!
//                .setOnClickListener { startActivity(Intent(this@LoginActivity,
//                        CreateAccountActivity::class.java)) }
        btnLogin!!.setOnClickListener { loginUser() }
    }


    private fun loginUser() {
        val email = etEmail?.text.toString()
        //var mName:String  = email.substringBefore("@")
        val split = email.split("@")
        val mName = split[0]
        Log.d(tag, mName)
        password = etPassword?.text.toString()
        if (!TextUtils.isEmpty(email) && !TextUtils.isEmpty(password)) {
            Log.d(tag, "Logging in user.")
            mAuth!!.signInWithEmailAndPassword(email, password!!)
                    .addOnCompleteListener(this) { task ->
                        if (task.isSuccessful) {
                            // Sign in success, update UI with signed-in user's information
                            val user = mAuth?.currentUser
                            val profileUpdates = UserProfileChangeRequest.Builder()
                                    .setDisplayName(mName).build()
                            user?.updateProfile(profileUpdates)

                            prefs = SharedPrefs(applicationContext)
                            prefs!!.currentUserName = mName
                            prefs!!.currentUser = user!!.uid
                            Log.d(tag, "signInWithEmail:success")
                            updateUI()
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.e(tag, "signInWithEmail:failure", task.exception)
                            Toast.makeText(this@LoginActivity, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show()
                        }
                    }
        } else {
            Toast.makeText(this, "Enter all details", Toast.LENGTH_SHORT).show()
        }

    }

    private fun updateUI() {
        val intent = Intent(this@LoginActivity, Navigator::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        startActivity(intent)
    }

}