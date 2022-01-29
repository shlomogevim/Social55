package com.sg.social55.activities

import android.app.Activity
import android.app.ProgressDialog
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.widget.Toast

import com.google.android.gms.tasks.Continuation
import com.google.android.gms.tasks.OnCompleteListener

import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.StorageTask
import com.google.firebase.storage.UploadTask
import com.sg.social55.databinding.ActivityAddPostBinding
import com.sg.social55.uilities.*
import com.theartofdev.edmodo.cropper.CropImage
import kotlinx.android.synthetic.main.activity_add_post.*

class AddPostActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAddPostBinding
    private var myUrl = ""
    private var imageUri: Uri? = null
    private var storagePostPicRef: StorageReference? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddPostBinding.inflate(layoutInflater)
        setContentView(binding.root)

        storagePostPicRef = FirebaseStorage.getInstance().reference.child("Posts Pictures")
        binding.saveNewPostBtn.setOnClickListener { uploadImage() }
        CropImage.activity()
            .setAspectRatio(2, 1)
            .start(this@AddPostActivity)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE && resultCode == Activity.RESULT_OK && data != null) {
            val result = CropImage.getActivityResult(data)
            imageUri = result.uri
            binding.imagePost.setImageURI(imageUri)
        }
    }


    private fun uploadImage() {
        when {
            imageUri == null -> sendToast("Please select image first.")
            TextUtils.isEmpty(binding.descriptionPost.text.toString()) -> sendToast("Please write description.")

            else -> {
                val progressDialog = ProgressDialog(this)
                progressDialog.setTitle("Adding New Post")
                progressDialog.setMessage("Please wait, we are adding your picture post...")
                progressDialog.show()

                val fileRef =
                    storagePostPicRef!!.child(System.currentTimeMillis().toString() + ".jpg")

                var uploadTask: StorageTask<*>
                uploadTask = fileRef.putFile(imageUri!!)

                uploadTask.continueWithTask(Continuation<UploadTask.TaskSnapshot, Task<Uri>> { task ->
                    if (!task.isSuccessful) {
                        task.exception?.let {
                            throw it
                            progressDialog.dismiss()
                        }
                    }
                    return@Continuation fileRef.downloadUrl
                })
                    .addOnCompleteListener(OnCompleteListener<Uri> { task ->
                        if (task.isSuccessful) {
                            val downloadUrl = task.result
                            myUrl = downloadUrl.toString()

                            val data = HashMap<String, Any>()
                            data[POST_ID] = "1"
                            data[POST_DISCTIPTION] =
                                binding.descriptionPost.text.toString().toLowerCase()
                            data[POST_PUBLISHER] =
                                FirebaseAuth.getInstance().currentUser?.displayName.toString()
                            data[POST_PUBLISHER_ID] =
                                FirebaseAuth.getInstance().currentUser!!.uid
                            data[POST_IMAGE] = myUrl
                            data[POST_LIKECOUNTER]="0"

                            val ref = FirebaseFirestore.getInstance().collection(POSTS_REF)
                            ref.add(data)
                                .addOnSuccessListener {
                                    sendToast("Post uploaded successfully.")
                                    data[POST_ID] = it.id
                                    ref.document(it.id).update(data)
                                    startActivity(Intent(this, MainActivity::class.java))
                                    finish()
                                    progressDialog.dismiss()
                                }
                        } else {
                            progressDialog.dismiss()
                        }
                    })
            }
        }
    }

    fun sendToast(str: String) {
        Toast.makeText(this, str, Toast.LENGTH_LONG).show()
    }
}