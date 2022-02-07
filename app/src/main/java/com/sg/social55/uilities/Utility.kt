package com.sg.social55.uilities

import android.util.Log
import android.widget.ImageView
import android.widget.TextView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.sg.social55.R
import com.sg.social55.model.Comment
import com.sg.social55.model.Notification
import com.sg.social55.model.Post
import com.sg.social55.model.User

class Utility {

    val currentUserUid = FirebaseAuth.getInstance().currentUser?.uid.toString()
    val currentUserName = FirebaseAuth.getInstance().currentUser?.displayName.toString()


    fun convertToUser(snap: DocumentSnapshot?): User {
        var userName = "no userName"
        var fullName = "no fullName"
        var email: String = "no email"
        var profileImage =
            "https://firebasestorage.googleapis.com/v0/b/social55firestore.appspot.com/o/Default%20Images%2Fprofile.png?alt=media&token=4a02bf76-8cc4-43e7-9750-930176c9c9ee"
        var dio: String = "no dio"
        var uid: String = "no uid"
        userName = snap?.getString(USER_USERNAME).toString()
        fullName = snap?.getString(USER_FULLNAME).toString()
        email = snap?.getString(USER_EMAIL).toString()
        profileImage = snap?.getString(USER_IMAGE).toString()
        dio = snap?.getString(USER_BIO).toString()
        uid = snap?.getString(FIRESTORE_USER_ID).toString()

        val newUser = User(userName, fullName, email, profileImage, dio, uid)
        return newUser
    }

  fun findUser(id: String): User {
        var user = User()
        FirebaseFirestore.getInstance().collection(USER_REF).document(id).get()
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    user = convertToUser(task.result)
                }
            }
        return user
    }

  /*  suspend fun getFirstValue() :Int {
        //delay(1000L)
        val value= Random.nextInt(1000)
        logi(" Rturn first num : $value")
        return  value
    }*/


    /*  fun findUser(id:String):User{
        var user=User()
    FirebaseFirestore.getInstance().collection(USER_REF).document(id).get()
    .addOnSuccessListener {
         user =convertToUser(it)
         }
       return user
    }*/
    fun covertYoPost(snap: DocumentSnapshot?): Post {
        var postId = "No postId"
        var postImage: String =
            "https://firebasestorage.googleapis.com/v0/b/social55firestore.appspot.com/o/Default%20Images%2Fprofile.png?alt=media&token=4a02bf76-8cc4-43e7-9750-930176c9c9ee"
        var publisher: String = "No Post Publisher"
        var publisherId: String = "No Post Publisher ID"
        var description: String = "No Post Description"
        var likeNumber: String = "0"


        postId = snap?.getString(POST_ID).toString()
        postImage = snap?.getString(POST_IMAGE).toString()
        publisher = snap?.getString(POST_PUBLISHER).toString()
        publisherId = snap?.getString(POST_PUBLISHER_ID).toString()
        description = snap?.getString(POST_DISCTIPTION).toString()
        likeNumber = snap?.getString(POST_LIKECOUNTER).toString()


       // logi("Utility || postId=$postId,postImage=?postImage,publisger=$publisher,publisherId=publisherId,description=$description,likeNumber=$likeNumber")



        val newPost = Post(postId, postImage, publisher, description, publisherId, likeNumber)
        return newPost
    }

    fun findPost(id: String): Post {
        var post = Post()
        FirebaseFirestore.getInstance().collection(POSTS_REF).document(id).get()
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    post = covertYoPost(task.result)
                }
            }
        return post
    }


    fun operateLikeCounterNew(postId: String, likesCounter: TextView) {
        var counter = 0
        FirebaseFirestore.getInstance().collection(USER_REF).get()
            .addOnSuccessListener {
                for (doc in it.documents) {
                    val currentId = doc.id
                    //   logi("currentId=$currentId , postId=$postId")
                    FirebaseFirestore.getInstance().collection(LIKES_REF_NEW).document(postId)
                        .collection(USERS_ID).document(currentId)
                        .collection(SIMPLE_ITEM).addSnapshotListener { value, error ->
                            if (value != null) {
                                //  logi("currentId11=$currentId , postId=$postId, value.documents.size=${value.documents.size}")
                                if (value.documents.size > 0) {
                                    // logi("currentId22=$currentId , postId=$postId, value.documents.size=${value.documents.size}")
                                    counter++
                                    //   likesCounter.text = counter.toString() + " Likes"
                                }
                                likesCounter.text = counter.toString() + " Likes"
                            }
                        }
                }
            }
    }


    fun likeBtn_Indicator(postId: String, likeBtn: ImageView, likeCounter: TextView) {
        FirebaseFirestore.getInstance().collection(LIKES_REF_NEW).document(postId)
            .collection(USERS_ID).document(currentUserUid)
            .collection(SIMPLE_ITEM).addSnapshotListener { value, error ->
                if (value != null) {
                    if (value.documents.size > 0) {
                        likeBtn.setImageResource(R.drawable.heart_clicked)
                        likeBtn.tag = LIKE_TAG
                    } else {
                        likeBtn.setImageResource(R.drawable.heart_not_clicked)
                        likeBtn.tag = NOT_LIKE_TAG
                    }
                    operateLikeCounterNew(postId, likeCounter)
                }
            }
    }

    fun likeBtn_Press(post: Post, likeBtn: ImageView) {
        val data = simpleData()
        val ref = FirebaseFirestore.getInstance().collection(LIKES_REF_NEW).document(post.postId)
            .collection(USERS_ID).document(currentUserUid)
            .collection(SIMPLE_ITEM)
        ref.document(BOL).get()
            .addOnSuccessListener {
                if (it.exists()) {
                    likeBtn.setImageResource(R.drawable.heart_not_clicked)
                    //    addNotification(post.postPublisherId,post.postId)
                    addPostNotification(post)
                    it.reference.delete()
                } else {
                    likeBtn.setImageResource(R.drawable.heart_clicked)
                    ref.document(BOL).set(data)
                }
            }
    }

    fun addCommentNotification(postID:String,commentText:String) {
        val data = HashMap<String, Any>()
        data[NOTIFICATION_USER_ID] = currentUserUid.toString()
        data[NOTIFICATION_TEXT] = "commented: " + commentText
        data[NOTIFICATION_POST_ID] =postID
        data[NOTIFICATION_IS_POST] = NOTIFICATION_ISPOST_TRUE
        FirebaseFirestore.getInstance().collection(NOTIFICATION_REF)
            .document(currentUserUid).collection(NOTIFICATION_LIST).add(data)
    }

    private fun addPostNotification(post: Post) {
        val data = HashMap<String, Any>()
        data[NOTIFICATION_USER_ID] =currentUserUid
        data[NOTIFICATION_TEXT] = "like your post "
        data[NOTIFICATION_POST_ID] = post.postId
        data[NOTIFICATION_IS_POST] = NOTIFICATION_ISPOST_TRUE
        FirebaseFirestore.getInstance()
            .collection(NOTIFICATION_REF) .document(post.postPublisherId)
            .collection(NOTIFICATION_LIST) . add(data)
 //logi("Utility || NOTIFICATION_REF11=$NOTIFICATION_REF ,post.postPublisherId=${post.postPublisherId}, post.postId=${post.postId}")
    }

     fun addUserNotification(user:User) {
      val data = HashMap<String, Any>()
        data[NOTIFICATION_USER_ID] =currentUserUid
         data[NOTIFICATION_TEXT] = "start following you "
         data[NOTIFICATION_POST_ID] = "No Post"
         data[NOTIFICATION_IS_POST] = NOTIFICATION_ISPOST_FALSE
        FirebaseFirestore.getInstance().collection(NOTIFICATION_REF)
            .document(user.uid).collection(NOTIFICATION_LIST).add(data)
      //------------
          val data1 = HashMap<String, Any>()
        data1[NOTIFICATION_USER_ID] =user.uid
        data1[NOTIFICATION_TEXT] = "start to follow "+user.userName
        data1[NOTIFICATION_POST_ID] = "No Post"
        data1[NOTIFICATION_IS_POST] = NOTIFICATION_ISPOST_FALSE
        FirebaseFirestore.getInstance().collection(NOTIFICATION_REF)
            .document(currentUserUid).collection(NOTIFICATION_LIST).add(data1)
       logi("Utility11 || \n currentUserUid=$currentUserUid,user.uid=${user.uid},user.userName=${user.userName}")

     }
    fun simpleData(): HashMap<String, Any> {
        val data = HashMap<String, Any>()
        data["Bol"] = "Exist"
        return data
    }


    fun convertToNotification(snap: DocumentSnapshot?): Notification {
        val userId = snap?.getString(NOTIFICATION_USER_ID).toString()
        val text = snap?.getString(NOTIFICATION_TEXT).toString()
        val postId = snap?.getString(NOTIFICATION_POST_ID).toString()
        val isPost = snap?.getString(NOTIFICATION_IS_POST).toString()
        val newNotification = Notification(userId,  text, postId, isPost)
        return newNotification
    }

    fun convertToComment(snap: DocumentSnapshot?): Comment {
        val text = snap?.getString(COMMENT_TEXT).toString()
        val publisher = snap?.getString(COMMENT_PUBLISHER).toString()
        val publisherId = snap?.getString(COMMENT_PUBLISHER_ID).toString()
        return Comment(text, publisher, publisherId)
    }

    fun addComment(addComment: TextView, postId: String, publisher: String, publisherI: String) {
        //               (binding.addComment, postId, publisher,publisherId)
        val data = HashMap<String, Any>()
        data[COMMENT_TEXT] = addComment.text.toString()
        data[COMMENT_PUBLISHER] = publisher
        data[COMMENT_PUBLISHER_ID] = publisherI
        FirebaseFirestore.getInstance().collection(COMMENT_REF).document(postId)
            .collection(COMMENT_DOC).add(data)
    }

    fun commentsCounter(post: Post, comments: TextView) {
        var counter = 0
        FirebaseFirestore.getInstance().collection(COMMENT_REF).document(post.postId)
            .collection(COMMENT_DOC)
            .addSnapshotListener { value, error ->
                if (value != null) {
                    counter = value.documents.size
                    comments.text = "view all the $counter comments"
                }
            }
    }

    fun logi(
        element1: String,
        element2: String = "",
        element3: String = "",
        element4: String = ""
    ) {
        if (element1 != "" && element2 == "" && element3 == "" && element4 == "") {
            Log.d("gg", "${element1}")
        }
        if (element1 != "" && element2 != "" && element3 == "" && element4 == "") {
            Log.d("gg", "${element1} ,${element2}")
        }
        if (element1 != "" && element2 != "" && element3 != "" && element4 == "") {
            Log.d("gg", "${element1} ,${element2} ,${element3}")
        }
        if (element1 != "" && element2 != "" && element3 != "" && element4 != "") {
            Log.d("gg", "${element1} ,${element2} ${element3},${element4}")
        }
    }

    fun seePost(post: Post) {
        logi("postId=${post.postId},publisher=${post.publisher},postPublisherId=${post.postPublisherId},description=${post.description}")
    }


    }
