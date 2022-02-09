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
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.StorageTask
import com.google.firebase.storage.UploadTask
import com.google.firestore.v1.DocumentTransform
import com.sg.social55.R
import com.sg.social55.databinding.ActivityAddStoryBinding
import com.sg.social55.uilities.*
import com.theartofdev.edmodo.cropper.CropImage
import io.grpc.Server

class AddStoryActivity : AppCompatActivity() {

    lateinit var binding:ActivityAddStoryBinding
    val util=Utility()
    private var myUrl = ""
    private var imageUri: Uri? = null
    private var storageStoryPicRef: StorageReference? = null
    private val currentUserUid=FirebaseAuth.getInstance().currentUser?.uid

    override fun onCreate(savedInstanceState: Bundle?) {
        binding= ActivityAddStoryBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        storageStoryPicRef = FirebaseStorage.getInstance().reference.child("Story Pictures")

        CropImage.activity()
            .setAspectRatio(9, 16)
            .start(this@AddStoryActivity)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE && resultCode == Activity.RESULT_OK && data != null) {
            val result = CropImage.getActivityResult(data)
            imageUri = result.uri
            uploadStory()
        }
    }

    private fun uploadStory() {
        when {
            imageUri == null -> sendToast("Please select image first.")
            else -> {
                val progressDialog = ProgressDialog(this)
                progressDialog.setTitle("Adding Story")
                progressDialog.setMessage("Please wait, we are adding your story...")
                progressDialog.show()

                val fileRef = storageStoryPicRef!!.child(System.currentTimeMillis().toString() + ".jpg")

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
                            data[STORY_ID] = "1"
                            data[STORY_IMAGE_URL]=myUrl
                            data[STORY_TIME_START] = System.currentTimeMillis()
                            data[STORY_TIME_END] =System.currentTimeMillis()+86400000  //one dat later
                            data[STORY_USER_ID]=currentUserUid.toString()
                         //   util.logi("AddStoryActivity11 || \n data=${data}")
                            val ref = FirebaseFirestore.getInstance().collection(STORY_REF).document( STORIES_USERS_LIST).collection(currentUserUid.toString() )
                            ref.add(data)

                                .addOnSuccessListener {
                                    sendToast("Story uploaded successfully.")
                                    data[STORY_ID] = it.id
                                    ref.document(it.id).update(data)
                                    util.logi("AddStoryActivity12|| \n it.id=${it.id},data=${data}")
                                    startActivity(Intent(this@AddStoryActivity, MainActivity::class.java))
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