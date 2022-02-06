package com.sg.social55.setting

import android.app.ProgressDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import com.sg.social55.activities.MainActivity
import com.sg.social55.databinding.ActivitySignUpBinding
import com.sg.social55.uilities.*
import java.util.*
import kotlin.collections.HashMap

class SignUpActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySignUpBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var progressDialog: ProgressDialog
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignUpBinding.inflate(layoutInflater)
        setContentView(binding.root)
        auth = Firebase.auth

        binding.signinLinkBtn.setOnClickListener {
            startActivity(Intent(this, SignInActivity::class.java))
        }
        binding.signupBtn.setOnClickListener {
            CreateAcounte()
        }
    }

    private fun CreateAcounte() {

        val fullName = binding.fullnameSignup.text.toString()
        val userName = binding.usernameSignup.text.toString()
        val email = binding.emailSignup.text.toString()
        val password = binding.passwordSignup.text.toString()
        when {
            TextUtils.isEmpty(fullName) ->
                Toast.makeText(this, "Full name is empty", Toast.LENGTH_SHORT).show()
            TextUtils.isEmpty(userName) ->
                Toast.makeText(this, "User name is empty", Toast.LENGTH_SHORT).show()
            TextUtils.isEmpty(email) ->
                Toast.makeText(this, "Email is empty", Toast.LENGTH_SHORT).show()
            TextUtils.isEmpty(password) ->
                Toast.makeText(this, "Password is empty", Toast.LENGTH_SHORT).show()
            else -> {

                progressDialog = ProgressDialog(this)
                with(progressDialog) {
                    setTitle("SignUp")
                    setMessage("Please wait, this may take a while ...")
                    setCanceledOnTouchOutside(false)
                    show()
                }

               auth.createUserWithEmailAndPassword(email,password)
                   .addOnSuccessListener { result ->
                       val changeRequest=UserProfileChangeRequest.Builder()
                           .setDisplayName(userName)
                           .build()
                       result.user?.updateProfile(changeRequest)?.addOnFailureListener {
                           Toast.makeText(this, "Cannot update user account", Toast.LENGTH_LONG).show()

                       }
                       saveUserInfo(fullName, userName, email,password)
                   }
                   .addOnFailureListener {
                       Toast.makeText(this, "Cannot create User Account ...", Toast.LENGTH_LONG).show()

                   }

            }
        }
    }

    private fun saveUserInfo(fullName: String, userName: String, email: String,password:String) {
        val data=HashMap<String,Any>()
        val uid = FirebaseAuth.getInstance().currentUser?.uid
        data[USER_ID] = uid!!
        data[USER_FULLNAME] = fullName.lowercase(Locale.getDefault())
        data[USER_USERNAME] = userName.lowercase(Locale.getDefault())
        data[USER_EMAIL] = email
        data[USER_PASSWORD] = password
        data[USER_BIO] = "It's me man..."
        data[USER_TIME] = FieldValue.serverTimestamp()
        data[USER_IMAGE] = "https://firebasestorage.googleapis.com/v0/b/social55firestore.appspot.com/o/Default%20Images%2Fprofile.png?alt=media&token=4a02bf76-8cc4-43e7-9750-930176c9c9ee"
        FirebaseFirestore.getInstance().collection(USER_REF).document(uid).set(data)
            .addOnSuccessListener {
                Toast.makeText(this, "Account has been created ...", Toast.LENGTH_LONG).show()




                /* val currentName = currentUser?.displayName.toString()
            val userName = user.userName
            FirebaseFirestore.getInstance().collection(FOLLOW_REF)
                .document(currentName).collection(FOLLOWING_REF).document(userName).get()
                .addOnSuccessListener {
                    if (it.exists()) {
                        followButton?.text = "Following"
                    } else {
                        followButton?.text = "Follow"
                    }
                }*/





                val intent = Intent(this, MainActivity::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(intent)
                finish()
            }
            .addOnFailureListener {
                Toast.makeText(this, "Error->${it.localizedMessage}", Toast.LENGTH_LONG).show()
                FirebaseAuth.getInstance().signOut()
                progressDialog.dismiss()
            }
    }


}