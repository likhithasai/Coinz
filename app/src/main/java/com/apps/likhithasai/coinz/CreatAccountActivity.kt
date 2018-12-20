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
import com.google.firebase.firestore.FirebaseFirestore

/**
 * Activity for creating an account for a user
 *
 * The class creates a user in the FireBase database.
 *
 */
class CreatAccountActivity : AppCompatActivity() {

    private val tag = "LoginActivity"
    //global variables
    //UI elements
    private var etEmail: EditText? = null
    private var etPassword: EditText? = null
    private var btnCreateAccount: Button? = null
    //Fire Base references
    private var mAuth: FirebaseAuth? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        //Remove title bar
        this.requestWindowFeature(Window.FEATURE_NO_TITLE)

        //Remove notification bar
        this.window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN)
        setContentView(R.layout.activity_creat_account)
        initialise()
    }

    /**
     * The initialise function is used to initialise the UI elements to access the resources in other functions.
     */
    private fun initialise() {
        etEmail = findViewById(R.id.et_email)
        etPassword = findViewById(R.id.et_password)
        btnCreateAccount = findViewById(R.id.btn_create_account)
        mAuth = FirebaseAuth.getInstance()

        //On click listener for create account button
        btnCreateAccount!!.setOnClickListener { createUser() }
    }

    /**
     * The createUser function's purpose is to create a user using the credentials entered by the user.
     * The details will be added to the FireBase database.
     *
     */
    private fun createUser() {
        //Get email and password
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
                    }
        } else {
            Toast.makeText(this, "Enter all credentials", Toast.LENGTH_SHORT).show()
        }

    }

    /**
     * The updateUI function passes an intent to navigate to the Login activity which is the login page.
     * The user can login with their new details.
     */
    private fun updateUI() {
        val ref = FirebaseFirestore.getInstance().collection("UsersWallet")

        ref.document(mAuth?.currentUser!!.uid).set(mapOf("gold" to "0", "sparechange" to 0))
                .addOnSuccessListener {
                    Log.d(tag, "Things added")
                }

        val intent = Intent(this@CreatAccountActivity, LoginActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        startActivity(intent)
        finish()
    }


}