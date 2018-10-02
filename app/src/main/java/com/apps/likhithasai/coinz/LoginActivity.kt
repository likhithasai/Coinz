package com.apps.likhithasai.coinz

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.widget.*;
import android.content.Intent;
import android.view.View;

class LoginActivity: AppCompatActivity() {
    private val TAG = "LoginActivity"
    //global variables
    private var email: String? = null
    private var password: String? = null
    //UI elements
    private var tvForgotPassword: TextView? = null
    private var etEmail: EditText? = null
    private var etPassword: EditText? = null
    private var btnLogin: Button? = null
    private var btnCreateAccount: Button? = null
    private var mProgressBar: ProgressBar? = null
    //Firebase references
    //private var mAuth: FirebaseAuth? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        initialise()
    }
    private fun initialise() {
        tvForgotPassword = findViewById<View>(R.id.tv_forgot_password) as TextView
        etEmail = findViewById<View>(R.id.et_email) as EditText
        etPassword = findViewById<View>(R.id.et_password) as EditText
        btnLogin = findViewById<View>(R.id.btn_login) as Button
        btnCreateAccount = findViewById<View>(R.id.btn_register_account) as Button
        mProgressBar = ProgressBar(this)
        //mAuth = FirebaseAuth.getInstance()
        /*tvForgotPassword!!
                .setOnClickListener { startActivity(Intent(this@LoginActivity,
                        ForgotPasswordActivity::class.java)) }
        btnCreateAccount!!
                .setOnClickListener { startActivity(Intent(this@LoginActivity,
                        CreateAccountActivity::class.java)) } */
        //btnLogin!!.setOnClickListener { loginUser() }
    }

}