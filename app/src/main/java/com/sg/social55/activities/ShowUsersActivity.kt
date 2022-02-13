package com.sg.social55.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.sg.social55.adapters.UserAdapter
import com.sg.social55.databinding.ActivityShowUsersBinding
import com.sg.social55.model.User
import com.sg.social55.uilities.*

class ShowUsersActivity : AppCompatActivity() {
    private lateinit var binding: ActivityShowUsersBinding
    var postId = ""
    var title = ""
    var userAdapter: UserAdapter? = null
    var userList = ArrayList<User>()
    var idList = ArrayList<String>()
    val util = Utility()
    var users = ArrayList<User>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityShowUsersBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val intent = intent
        postId = intent.getStringExtra(POST_ID).toString()
        title = intent.getStringExtra(SHOW_USER_TITLE).toString()
        // util.logi("id=$id, title=$title")
        val toolbar = binding.toolbar
        setSupportActionBar(toolbar)
        supportActionBar?.title = title
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        toolbar.setNavigationOnClickListener {
            finish()
        }
        val recyclerView = binding.recyclerView
        recyclerView.setHasFixedSize(true)
        recyclerView.layoutManager = LinearLayoutManager(this)
        userAdapter = UserAdapter(userList)
        recyclerView.adapter = userAdapter

        when (title) {
            TITLE_LIKES -> getLikes()
            TITLE_FOLLOWING -> getFollowing()
            TITLE_FOLLOERS -> getFollowers()
            TITLE_VIEW -> getViews()
        }
    }

    private fun getViews() {
        val userId=intent.getStringExtra(USER_ID)
      //  val storyId=intent.getStringExtra(STORY_ID)
       FirebaseFirestore.getInstance().collection(STORY_REF).document(STORIES_USERS_LIST)
           .collection(userId.toString()).document(postId).collection(STORY_VIEWS)
           .addSnapshotListener { value, error ->

               if (value != null) {
                   for (doc in value.documents){
                       val story=util.covertToStory(doc)
                   }
               }

           }

/* data[STORY_VIEW_ID] = currentUserId.toString()
        val ref = FirebaseFirestore.getInstance().collection(STORY_REF).document(STORIES_USERS_LIST)
            .collection(userId).document(storyId).collection(STORY_VIEWS).add(data)*/

        /*  intent.putExtra(USER_ID,userId)
            intent.putExtra(STORY_ID,storyIdsList[counter])
            intent.putExtra(TITLE, TITLE_VIEW)*/

    }

     private fun getLikes() {
    //   util.logi("11")
        FirebaseFirestore.getInstance().collection(USER_REF)
            .addSnapshotListener { value, error ->
                if (value != null) {
                    for (doc in value.documents) {
                        var user = util.convertToUser(doc)
                        users.add(user)
                    }
                }
                create_user_list()
            }

    }

    private fun create_user_list() {
        userList.clear()
        users.forEach { item ->
            FirebaseFirestore.getInstance().collection(LIKES_REF_NEW).document(postId)
                .collection(USERS_ID).document(item.uid).collection(SIMPLE_ITEM).document(BOL).get()
                .addOnSuccessListener {
                    if (it.exists()) {
                      //  util.logi("item.uid = ${item.uid}")
                        userList.add(item)
                        userAdapter?.notifyDataSetChanged()
                    }
                 //   util.logi("userlist1 = ${userList}")
                }
         //   util.logi("userlist2 = ${userList}")
        }
    }

    private fun getFollowers() {
        var idList1 = ArrayList<String>()
        idList.clear()
        FirebaseFirestore.getInstance().collection(FOLLOW_REF).document(postId)
            .collection(FOLLOWER_REF).get()
            .addOnSuccessListener {
                // util.logi("id=$postId")
                for (doc in it.documents) {
                    //util.logi("id=$postId, doc=${doc.id}")
                    idList1.add(doc.id)
                    create_IdList(idList1)
                }
              //  util.logi("idList=$idList")
                showUsers()
            }

    }

    private fun getFollowing() {
        var idList1 = ArrayList<String>()
        idList.clear()
        FirebaseFirestore.getInstance().collection(FOLLOW_REF).document(postId)
            .collection(FOLLOWING_REF).get()
            .addOnSuccessListener {
                for (doc in it.documents) {
                    //   util.logi("postId=$postId, doc=${doc.id}")
                    idList1.add(doc.id)
                    create_IdList(idList1)
                }
                //util.logi("idList=$idList")
                showUsers()
            }
    }

    private fun create_IdList(idList1: java.util.ArrayList<String>) {
        idList.clear()
        for (item in idList1) {
            //  util.logi("item=$item")
            FirebaseFirestore.getInstance().collection(USER_REF)
                .addSnapshotListener { value, error ->
                    if (value != null) {
                        for (doc in value.documents) {
                            var user = util.convertToUser(doc)
                            //        util.logi("user.userName=${user.userName}")
                            if (user.userName == item && !idList.contains(user.uid)) {
                                idList.add(user.uid)
                                //util.logi("idlist=$idList")
                            }
                        }
                    }
                }
        }
    }

/*  private fun getLikes() {
                       //likes of post id "id"
 //util.logi("current postId=$postId")
      idList.clear()
      FirebaseFirestore.getInstance().collection(USER_REF).get()
          .addOnSuccessListener {
              for (doc in it.documents) {
                  val currenUserId = doc.id
                 util.logi("outside currenUserId=$currenUserId")
                  FirebaseFirestore.getInstance().collection(LIKES_REF_NEW).document(postId)
                      .collection(USERS_ID).document(currenUserId)
                      .collection(SIMPLE_ITEM).addSnapshotListener { value, error ->
                          if (value != null) {
                              if (value.documents.size > 0) {
                                 util.logi("inside  currenUserId=$currenUserId")

                                  idList.add(currenUserId)                           // list of user that like this post
                              }
                          util.logi("idList=$idList")
                          }
                      }
              }
              showUsers()
          }

  }*/


/*  FirebaseFirestore.getInstance().collection(LIKES_REF_NEW).document(postId)
      .collection(USERS_ID).get()
      .addOnSuccessListener {
       util.logi("current postId=$postId")

       *//*   for (doc in it.documents){
                //  util.logi("current postId=$postId,doc=${doc}")
              }*//*

          }.addOnFailureListener {
              "22"
          }
*/


/*  FirebaseFirestore.getInstance().collection(LIKES_REF_NEW).document(postId)
      .collection(USERS_ID).addSnapshotListener { value, error ->
          if (error!=null){
              util.logi("error")
          }
     //   util.logi(" value=$value")

          if (value != null) {
              //util.logi(" value.size=${value.size()}")
              for (doc in value.documents){
                  util.logi("doc=$doc")
              }
          }
      }*/


/*     idList.clear()
     FirebaseFirestore.getInstance().collection(USER_REF).get()
         .addOnSuccessListener {
             for (doc in it.documents) {
                 var user=util.convertToUser(doc)
               //  val currenUserId = doc.id
               //  util.logi("outside currenUserId=${user.uid}")
                 FirebaseFirestore.getInstance().collection(LIKES_REF_NEW).document(postId)
                     .collection(USERS_ID).addSnapshotListener { value, error ->
                         if (value != null) {
                             for (doc in value.documents){
                                 util.logi("doc=$doc")
                               //  val post=util.covertYoPost()
                             }
                         }
                     }

                     *//*.collection(SIMPLE_ITEM).addSnapshotListener { value, error ->
                          if (value != null) {
                              if (value.documents.size > 0) {
                                  util.logi("inside  currenUserId=$currenUserId")

                                  idList.add(currenUserId)                           // list of user that like this post
                              }
                              util.logi("idList=$idList")
                          }
                      }*//*
              }
              showUsers()
          }
*/


/*private fun createUserList(): ArrayList<User> {

    FirebaseFirestore.getInstance().collection(USER_REF)
        .addSnapshotListener { value, error ->
            if (value != null) {
                for (doc in value.documents) {
                    var user = util.convertToUser(doc)
                    users.add(user)
                }
            }
        }
    return users
}*/


    private fun showUsers() {
        userList.clear()
        //util.logi("idList=$idList")
        FirebaseFirestore.getInstance().collection(USER_REF).addSnapshotListener { value, error ->
            if (value != null) {
                for (doc in value.documents) {
                    parseData(doc)
                }
                userAdapter?.notifyDataSetChanged()
            }
        }

    }


    private fun parseData(document: DocumentSnapshot) {
        var name = ""
        var fullName = ""
        var email = ""
        var image = ""
        var bio = ""
        var uid = ""

        name = document[USER_USERNAME] as String
        fullName = document[USER_FULLNAME] as String
        email = document[USER_EMAIL] as String
        image = document[USER_IMAGE] as String
        bio = document[USER_BIO] as String
        uid = document[USER_ID] as String

        if (idList.contains(uid)) {
            val newUser = User(name, fullName, email, image, bio, uid)
            userList.add(newUser)
        }
    }

/*

private fun parseData(snapshot: QuerySnapshot) {
    var name=""
    var fullName=""
    var email=""
    var image=""
    var bio=""
    var uid=""
    val auth=FirebaseAuth.getInstance().currentUser?.displayName
    users?.clear()
    for (document in snapshot.documents){
        name=document[USER_USERNAME] as String
        fullName=document[USER_FULLNAME] as String
        email=document[USER_EMAIL] as String
        image=document[USER_IMAGE] as String
        bio=document[USER_BIO] as String
        uid=document[USER_ID] as String

     //   if (name!=auth) {
            val newUser = User(name, fullName, email, image, bio, uid)
            users?.add(newUser)
       // }
    }
    userAdapter.notifyDataSetChanged()
}
 private fun retrieveUsers() {
    FirebaseFirestore.getInstance().collection(USER_REF)
       // .orderBy(USER_FULLNAME, Query.Direction.ASCENDING)
        .addSnapshotListener { snapshot, exception ->
            if (exception != null) {
                Toast.makeText(context, "Error in downloadind users", Toast.LENGTH_LONG).show()
            }else{
                if (snapshot!=null){
                    parseData(snapshot)
                }
            }
        }
}

*/


}