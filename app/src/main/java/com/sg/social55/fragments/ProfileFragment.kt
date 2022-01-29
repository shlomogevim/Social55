package com.sg.social55.fragments

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.Toast
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import com.sg.social55.R
import com.sg.social55.adapters.MyImagesAdapter
import com.sg.social55.model.Post
import com.sg.social55.setting.AccountSettingActivity
import com.sg.social55.setting.SignInActivity
import com.sg.social55.uilities.*
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.fragment_profile.*
import kotlinx.android.synthetic.main.fragment_profile.view.*
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap


class ProfileFragment : Fragment() {
    val util = Utility()

    private var currentUserName: String = ""
    private var currentUserUid: String = ""
    private var followingUserName = ""
    private var followingUserId = ""


    var postList = ArrayList<Post>()
    var postListSave = ArrayList<Post>()
    var mySaveImage: ArrayList<String>? = null

    private lateinit var adapter: MyImagesAdapter
    private lateinit var adapterSave: MyImagesAdapter
    private lateinit var recyclerViewUploadImage: RecyclerView
    private lateinit var recyclerViewSaveImage: RecyclerView


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val view = inflater.inflate(R.layout.fragment_profile, container, false)

        currentUserUid = FirebaseAuth.getInstance().currentUser?.uid.toString()
        currentUserName = FirebaseAuth.getInstance().currentUser?.displayName.toString()



        //recyclerView for uploadImage

        recyclerViewUploadImage = view.recycler_view_upload_pic
        recyclerViewUploadImage.setHasFixedSize(true)
        val linearManager = GridLayoutManager(context, 3)
        recyclerViewUploadImage.layoutManager = linearManager
        adapter = MyImagesAdapter(postList)
        recyclerViewUploadImage.adapter = adapter

         //recyclerview for saveImage
        recyclerViewSaveImage = view.recycler_view_saved_pic
        recyclerViewSaveImage.setHasFixedSize(true)
        val linearManager1 = GridLayoutManager(context, 3)
        recyclerViewSaveImage.layoutManager = linearManager1
        adapterSave = MyImagesAdapter(postListSave)
        recyclerViewSaveImage.adapter = adapterSave

        recyclerViewSaveImage.visibility = View.GONE
        recyclerViewUploadImage.visibility = View.VISIBLE

        var uploadImageBtn:ImageButton
        uploadImageBtn=view.findViewById(R.id.images_grid_view_btn)
        uploadImageBtn.setOnClickListener {
            recyclerViewSaveImage.visibility = View.GONE
            recyclerViewUploadImage.visibility = View.VISIBLE
        }

        var saveImagesBtn:ImageButton
        saveImagesBtn=view.findViewById(R.id.images_save_btn)
        saveImagesBtn.setOnClickListener {
            recyclerViewSaveImage.visibility = View.VISIBLE
            recyclerViewUploadImage.visibility = View.GONE

        }


        val pref = context?.getSharedPreferences(SHARPREF_REF, Context.MODE_PRIVATE)
        if (pref != null) {
            followingUserName = pref.getString(PUBLISHER_EXSTRA, "none").toString()
            followingUserId = pref.getString(PROFILE_ID_EXSTRA, "none").toString()
        }

        if (followingUserName == currentUserName) {
            view.edit_account_settings_btn.text = "Edit Profile"
        } else {
            checkFollowAndFollowingBtnStatus()
        }

        view.edit_account_settings_btn.setOnClickListener {
            val getBtnText = view.edit_account_settings_btn.text.toString()
            when (getBtnText) {
                "Edit Profile" -> startActivity(Intent(context, AccountSettingActivity::class.java))
                "Follow" -> {
                    val data = HashMap<String, Any>()
                    data.put("bol", true)
                    FirebaseFirestore.getInstance().collection(FOLLOW_REF).document(currentUserName)
                        .collection(FOLLOWING_REF).document(followingUserName)
                        .set(data) //current follow after  user
                        .addOnSuccessListener {
                            view.edit_account_settings_btn.text = "Following"
                            FirebaseFirestore.getInstance().collection(FOLLOW_REF)
                                .document(followingUserName)
                                .collection(FOLLOWER_REF).document(currentUserName)
                                .set(data)     //user being follow by current
                        }
                }
                "Following" -> {
                    FirebaseFirestore.getInstance().collection(FOLLOW_REF).document(currentUserName)
                        .collection(FOLLOWING_REF).document(followingUserName)
                        .delete()//current follow after  user
                        .addOnSuccessListener {
                            view.edit_account_settings_btn.text = "Follow"
                            FirebaseFirestore.getInstance().collection(FOLLOW_REF)
                                .document(followingUserName)
                                .collection(FOLLOWER_REF).document(currentUserName)
                                .delete()     //user being follow by current
                        }
                }
            }
        }
        getFollower()
        getFollowings()
        userInfo()
        myPhotos()
        getTotalNumOfPost()
        mySavesPostList()

        return view
    }

    private fun mySavesPostList() {
        mySaveImage = ArrayList()
        FirebaseFirestore.getInstance().collection(SAVE_REF).document(currentUserUid)
            .collection(POSTID_COLLECTION).addSnapshotListener { value, error ->
                if (value != null) {
                    for (doc in value.documents) {
                        mySaveImage!!.add(doc.id)
                        //  util.logi("ProfileFragment/mySavePostList  - doc.id=${doc.id}")
                    }
                    readSaveImageData()
                }
            }
    }

    private fun readSaveImageData() {
        postListSave.clear()
        FirebaseFirestore.getInstance().collection(POSTS_REF).addSnapshotListener { value, error ->
            if (value != null) {
                for (doc in value.documents) {
                    if (doc.exists()) {
                        val post = util.covertYoPost(doc)

                        if (mySaveImage!!.contains(post.postId)) {
                            //  util.logi("ProfileFragment/readSave  -post.postId=${post.postId}")
                            postListSave.add(post)
                        }
                    }
                }
            //   util.logi("ProfileFragment/readSave  -postListSave.size=${postListSave.size}")
             adapterSave.notifyDataSetChanged()
            }
        }
    }

    private fun checkFollowAndFollowingBtnStatus() {
        //util.logi(FOLLOW_REF,currentUserName,FOLLOWING_REF,followingUserName)

        FirebaseFirestore.getInstance().collection(FOLLOW_REF)
            .document(currentUserName).collection(FOLLOWING_REF).document(followingUserName).get()
            .addOnSuccessListener {
                if (it.exists()) {
                    edit_account_settings_btn.text = "Following"
                } else {
                    edit_account_settings_btn.text = "Follow"
                }
            }.addOnFailureListener {
                Toast.makeText(context, "You have no Folloing list", Toast.LENGTH_LONG).show()
            }
    }

    private fun getFollower() {
        FirebaseFirestore.getInstance().collection(FOLLOW_REF).document(followingUserName)
            .collection(FOLLOWER_REF).get()
            .addOnSuccessListener {
                total_followers.text = it.count().toString()
            }.addOnFailureListener {
                total_followers.text = "0"
            }
    }

    private fun getFollowings() {
        FirebaseFirestore.getInstance().collection(FOLLOW_REF).document(followingUserName)
            .collection(FOLLOWING_REF).get()
            .addOnSuccessListener {
                total_following.text = it.count().toString()
            }.addOnFailureListener {
                total_following.text = "0"
            }
    }

    private fun myPhotos() {
        postList.clear()
        val currentUid = FirebaseAuth.getInstance().currentUser?.displayName
        FirebaseFirestore.getInstance().collection(POSTS_REF).addSnapshotListener { value, error ->
            if (value != null) {
                for (doc in value.documents) {
                    val post = util.covertYoPost(doc)
                    if (post.publisher == followingUserName) {
                        postList.add(post)
                    }
                }
                Collections.reverse(postList)
                //   util.logi("in ProfileFragment  postList.size= ${postList.size}")
            adapter.notifyDataSetChanged()
            }
        }
    }

    private fun userInfo() {
        FirebaseFirestore.getInstance().collection(USER_REF).document(followingUserId).get()
            .addOnSuccessListener {
                val data = it.data
                if (data != null) {
                    if (data[USER_IMAGE] != null) {
                        val imageId = data[USER_IMAGE] as String
                        if (imageId != null) {
                            Picasso.get().load(imageId).placeholder(R.drawable.profile)
                                .into(view?.profile_image_profile_fragment)
                        }
                    }
                    val fullname = data[USER_FULLNAME] as String
                    view?.full_name_profile_fragment?.text = fullname
                    val bio = data[USER_BIO] as String
                    view?.bio_profile_fragment?.text = bio

                    val mainString = "Current:$currentUserName         following:$followingUserName"
                    //  val mainString = "Current:$currentUserName  "
                    view?.profile_fragment_username?.text = mainString
                }
            }.addOnFailureListener {
                // Log.d("fff", "Fail ->${it.localizedMessage}")
            }
    }

    override fun onStop() {
        super.onStop()
        val pref = context?.getSharedPreferences("PREFS", Context.MODE_PRIVATE)?.edit()
        pref?.putString("userName", followingUserName)
        pref?.apply()
    }

    override fun onPause() {
        super.onPause()
        val pref = context?.getSharedPreferences("PREFS", Context.MODE_PRIVATE)?.edit()
        pref?.putString("userName", followingUserName)
        pref?.apply()
    }

    override fun onDestroy() {
        super.onDestroy()
        val pref = context?.getSharedPreferences("PREFS", Context.MODE_PRIVATE)?.edit()
        pref?.putString("userName", followingUserName)
        pref?.apply()
    }

    private fun getTotalNumOfPost() {
        var counter = 0
        FirebaseFirestore.getInstance().collection(POSTS_REF).addSnapshotListener { value, error ->
            if (value != null) {
                for (doc in value.documents) {
                    val post = util.covertYoPost(doc)
                    if (post.postPublisherId == followingUserId) {
                        counter++
                    }
                }
                total_posts.text = counter.toString()
            }
        }
    }
}


/*package com.sg.social55.fragments

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import com.sg.social55.R
import com.sg.social55.adapters.MyImagesAdapter
import com.sg.social55.model.Post
import com.sg.social55.setting.AccountSettingActivity
import com.sg.social55.setting.SignInActivity
import com.sg.social55.uilities.*
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.fragment_profile.*
import kotlinx.android.synthetic.main.fragment_profile.view.*
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap


class ProfileFragment : Fragment() {
    val util = Utility()

    private var currentUserName: String = ""
    private var currentUserUid: String = ""
    private  var followingUserName=""
    private  var followingUserId=""

    var postList = ArrayList<Post>()
    var postListSave = ArrayList<Post>()
    var mySaveImage: ArrayList<String> ?= null

    private lateinit var adapter:MyImagesAdapter
    private lateinit var adapterSave:MyImagesAdapter
   private lateinit var recyclerViewUploadImage:RecyclerView
   private lateinit var recyclerViewSaveImage:RecyclerView


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val view = inflater.inflate(R.layout.fragment_profile, container, false)

        currentUserUid = FirebaseAuth.getInstance().currentUser?.uid.toString()
        currentUserName = FirebaseAuth.getInstance().currentUser?.displayName.toString()

        recyclerViewUploadImage = view.recycler_view_upload_pic
        recyclerViewUploadImage.setHasFixedSize(true)
        val linearManager = GridLayoutManager(context, 3)
        recyclerViewUploadImage.layoutManager = linearManager
        adapter= MyImagesAdapter(postList)
        recyclerViewUploadImage.adapter=adapter

        recyclerViewSaveImage = view.recycler_view_upload_pic
        recyclerViewSaveImage.setHasFixedSize(true)
        val linearManager1 = GridLayoutManager(context, 3)
        recyclerViewSaveImage.layoutManager = linearManager1
        adapterSave= MyImagesAdapter(postListSave)
        recyclerViewSaveImage.adapter=adapterSave



        val pref = context?.getSharedPreferences("PREFS", Context.MODE_PRIVATE)
        if (pref != null) {

            followingUserName = pref.getString(PUBLISHER_EXSTRA, "none").toString()
            followingUserId=pref.getString(PROFILE_ID_EXSTRA,"none").toString()

        }


        if (followingUserName == currentUserName) {
            view.edit_account_settings_btn.text = "Edit Profile"
        } else {
            checkFollowAndFollowingBtnStatus()
        }




        view.edit_account_settings_btn.setOnClickListener {
            val getBtnText = view.edit_account_settings_btn.text.toString()
            when (getBtnText) {
                "Edit Profile" -> startActivity(Intent(context, AccountSettingActivity::class.java))
                "Follow" -> {
                    val data = HashMap<String, Any>()
                    data.put("bol", true)
                    FirebaseFirestore.getInstance().collection(FOLLOW_REF).document(currentUserName)
                        .collection(FOLLOWING_REF).document(followingUserName)
                        .set(data) //current follow after  user
                        .addOnSuccessListener {
                            view.edit_account_settings_btn.text = "Following"
                            FirebaseFirestore.getInstance().collection(FOLLOW_REF)
                                .document(followingUserName)
                                .collection(FOLLOWER_REF).document(currentUserName)
                                .set(data)     //user being follow by current
                        }
                }
                "Following" -> {
                    FirebaseFirestore.getInstance().collection(FOLLOW_REF).document(currentUserName)
                        .collection(FOLLOWING_REF).document(followingUserName)
                        .delete()//current follow after  user
                        .addOnSuccessListener {
                            view.edit_account_settings_btn.text = "Follow"
                            FirebaseFirestore.getInstance().collection(FOLLOW_REF)
                                .document(followingUserName)
                                .collection(FOLLOWER_REF).document(currentUserName)
                                .delete()     //user being follow by current
                        }
                }
            }
        }
        getFollower()
        getFollowings()
        userInfo()
        myPhotos()
        getTotalNumOfPost()
        mySave()

        return view
    }

    private fun mySave() {
        mySaveImage= ArrayList()
       FirebaseFirestore.getInstance().collection(SAVE_REF).document(currentUserUid)
           .collection( POSTID_COLLECTION).addSnapshotListener { value, error ->
               if (value != null) {
                   for (doc in value.documents){
                       mySaveImage!!.add(doc.id)
                   }
                   readSaveImageData()
               }
           }



    }

    private fun readSaveImageData() {
        postListSave.clear()
        FirebaseFirestore.getInstance().collection(POSTS_REF).addSnapshotListener { value, error ->
            if (value != null) {
                for (doc in value.documents){
                    if (doc.exists()){
                        val post=util.covertYoPost(doc)
                        /*if (post.postId ){

                        }*/

                    }

                }
            }



        }
    }

    private fun checkFollowAndFollowingBtnStatus() {
       //util.logi(FOLLOW_REF,currentUserName,FOLLOWING_REF,followingUserName)

        FirebaseFirestore.getInstance().collection(FOLLOW_REF)
            .document(currentUserName).collection(FOLLOWING_REF).document(followingUserName).get()
            .addOnSuccessListener {
                if (it.exists()) {
                    edit_account_settings_btn.text = "Following"
                } else {
                    edit_account_settings_btn.text = "Follow"
                }
            }.addOnFailureListener {
                Toast.makeText(context,"You have no Folloing list",Toast.LENGTH_LONG).show()
            }
    }

    private fun getFollower() {
        FirebaseFirestore.getInstance().collection(FOLLOW_REF).document(followingUserName)
            .collection(FOLLOWER_REF).get()
            .addOnSuccessListener {
                total_followers.text = it.count().toString()
            }.addOnFailureListener {
                total_followers.text = "0"
            }
    }

    private fun getFollowings() {
        FirebaseFirestore.getInstance().collection(FOLLOW_REF).document(followingUserName)
            .collection(FOLLOWING_REF).get()
            .addOnSuccessListener {
                total_following.text = it.count().toString()
            }.addOnFailureListener {
                total_following.text = "0"
            }
    }

    private fun myPhotos() {
        postList.clear()
        val currentUid = FirebaseAuth.getInstance().currentUser?.displayName
        FirebaseFirestore.getInstance().collection(POSTS_REF).addSnapshotListener { value, error ->
            if (value != null) {
                for (doc in value.documents) {
                    val post = util.covertYoPost(doc)
                   if (post.publisher == followingUserName) {
                        postList.add(post)
                    }
                }
                Collections.reverse(postList)
                adapter.notifyDataSetChanged()
            }
        }
    }

    private fun userInfo() {
        FirebaseFirestore.getInstance().collection(USER_REF).document(followingUserId).get()
            .addOnSuccessListener {
                val data = it.data
                if (data != null) {
                    if (data[USER_IMAGE] != null) {
                        val imageId = data[USER_IMAGE] as String
                        if (imageId != null) {
                            Picasso.get().load(imageId).placeholder(R.drawable.profile)
                                .into(view?.profile_image_profile_fragment)
                        }
                    }
                    val fullname = data[USER_FULLNAME] as String
                    view?.full_name_profile_fragment?.text = fullname
                    val bio = data[USER_BIO] as String
                    view?.bio_profile_fragment?.text = bio

                     val mainString = "Current:$currentUserName         following:$followingUserName"
                  //  val mainString = "Current:$currentUserName  "
                    view?.profile_fragment_username?.text = mainString
                }
            }.addOnFailureListener {
               // Log.d("fff", "Fail ->${it.localizedMessage}")
            }
    }

    override fun onStop() {
        super.onStop()
        val pref = context?.getSharedPreferences("PREFS", Context.MODE_PRIVATE)?.edit()
        pref?.putString("userName", followingUserName)
        pref?.apply()
    }

    override fun onPause() {
        super.onPause()
        val pref = context?.getSharedPreferences("PREFS", Context.MODE_PRIVATE)?.edit()
        pref?.putString("userName", followingUserName)
        pref?.apply()
    }

    override fun onDestroy() {
        super.onDestroy()
        val pref = context?.getSharedPreferences("PREFS", Context.MODE_PRIVATE)?.edit()
        pref?.putString("userName", followingUserName)
        pref?.apply()
    }
    private fun getTotalNumOfPost(){
        var counter=0
        FirebaseFirestore.getInstance().collection(POSTS_REF).addSnapshotListener { value, error ->
            if (value != null) {
                for (doc in value.documents){
                    val post=util.covertYoPost(doc)
                    if (post.postPublisherId==followingUserId) {
                        counter++
                    }
                }
                total_posts.text=counter.toString()
            }
        }
    }
}




*/