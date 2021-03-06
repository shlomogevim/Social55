package com.sg.social55.setting

import android.app.ProgressDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.sg.social55.activities.MainActivity
import com.sg.social55.databinding.ActivitySignInBinding
import com.sg.social55.uilities.Utility

class SignInActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySignInBinding
    val util=Utility()

    override fun onCreate(savedInstanceState: Bundle?) {
        binding = ActivitySignInBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        binding.signupLinkBtn.setOnClickListener {
            val intent = Intent(this, SignUpActivity::class.java)
            startActivity(intent)
        }
        binding.loginBtn.setOnClickListener {
            loginUser()
        }
        testBtn()
    }

    private fun loginUser() {
        val email = binding.emailLogin.text.toString()
        val password = binding.passwordLogin.text.toString()
        when {

            TextUtils.isEmpty(email) ->
                Toast.makeText(this, "Email is empty", Toast.LENGTH_SHORT).show()
            TextUtils.isEmpty(password) ->
                Toast.makeText(this, "Password is empty", Toast.LENGTH_SHORT).show()
            else -> {

                val progressDiallog = ProgressDialog(this)
                with(progressDiallog) {
                    setTitle("Login ....")
                    setMessage("Please wait, this may take a while ...")
                    setCanceledOnTouchOutside(false)
                    show()
                }
                val mAuth = FirebaseAuth.getInstance()
                mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        progressDiallog.dismiss()

                        val intent = Intent(this, MainActivity::class.java)
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
                        startActivity(intent)
                        finish()
                    } else {
                        val message = task.exception!!.toString()
                        Toast.makeText(this, "Errore :$message", Toast.LENGTH_SHORT).show()
                        mAuth.signOut()
                        progressDiallog.dismiss()
                    }
                }
            }
        }
    }

    override fun onStart() {
        super.onStart()
        if (FirebaseAuth.getInstance().currentUser != null) {
            val intent = Intent(this, MainActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
            finish()
        }
    }
    private fun testBtn() {
        binding.mr10Btn.setOnClickListener {
            signInBtn(1)
        }
        binding.mr20Btn.setOnClickListener {
            signInBtn(2)
        }
        binding.mr30Btn.setOnClickListener {
            signInBtn(3)
        }
        binding.mr40Btn.setOnClickListener {
            signInBtn(4)
        }
        binding.mr50Btn.setOnClickListener {
            signInBtn(5)
        }
    }

    private fun signInBtn(index: Int) {
        var email = ""
        var password = "111111"
        when (index) {
            1 -> email = "mr10@gmail.com"
            2 -> email = "mr20@gmail.com"
            3 -> email = "mr30@gmail.com"
            4 -> email = "mr40@gmail.com"
            5 -> email = "mr50@gmail.com"
        }
        FirebaseAuth.getInstance().signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val intent = Intent(this, MainActivity::class.java)
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
                    startActivity(intent)
                    finish()
                }
            }


    }
}