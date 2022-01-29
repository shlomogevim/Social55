package com.sg.social55.uilities

import android.util.Log
import android.widget.ImageView
import android.widget.TextView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.sg.social55.R
import com.sg.social55.model.Comment
import com.sg.social55.model.Post
import com.sg.social55.model.User

class Utility {

    val currentUser = FirebaseAuth.getInstance().currentUser
    fun logi( element1:String,element2:String="",element3:String="",element4:String=""){

       /* if (element1!="" && element2=="" && element3=="" && element4==""){
            Log.d("gg"," element1=${element1}")
        }*/
        if (element1!="" && element2=="" && element3=="" && element4==""){
            Log.d("gg"," element1=${element1}")
        }
        if (element1!="" && element2!="" && element3=="" && element4==""){
            Log.d("gg"," element1=${element1} , element2=${element2}")
        }

        if (element1!="" && element2!="" && element3!="" && element4==""){
            Log.d("gg"," element1=${element1} ,element2=${element2} ,element3=${element3}")
        }
        if (element1!="" && element2!="" && element3!="" && element4!=""){
            Log.d("gg"," element1=${element1} ,element2=${element2} ,element3=${element3} ,element4=${element4}")
        }





    }


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
        uid = snap?.getString(USER_UID).toString()

        val newUser = User(userName, fullName, email, profileImage, dio, uid)
        return newUser
    }

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


       // Log.d("gg","postId=$postId,publisger=$publisher,description=$description")

       likeNumber = snap?.getString(POST_LIKECOUNTER).toString()

        val newPost = Post(postId, postImage, publisher, description, publisherId, likeNumber)
        return newPost
    }
   fun convertToComment(snap: DocumentSnapshot?) :Comment{
       val text=snap?.getString(COMMENT_TEXT).toString()
       val publisher=snap?.getString(COMMENT_PUBLISHER_USERNAME).toString()
    return  Comment(text,publisher)
   }

    fun operateLikeCounter(postId: String, likesCounter: TextView) {
        var counter = 0
        FirebaseFirestore.getInstance().collection(USER_REF).get()
            .addOnSuccessListener {
                for (doc in it.documents) {
                    val currentId = doc.id.toString()
                    FirebaseFirestore.getInstance().collection(LIKES_REF).document(postId)
                        .collection(currentId).document(SIMPLE_POST).get()
                        .addOnSuccessListener {
                            if (it.exists()) {
                                counter++
                                likesCounter.text = counter.toString() + " Likes"
                            } else {
                                likesCounter.text = counter.toString() + " Likes"
                            }
                        }
                }
            }
    }


    fun isLikes(postId: String, likeButton: ImageView) {
        val currentUserUid = FirebaseAuth.getInstance().currentUser?.uid.toString()
        FirebaseFirestore.getInstance().collection(LIKES_REF)
            .document(postId).collection(currentUserUid).document(SIMPLE_POST)
            .get().addOnSuccessListener {
                if (it.exists()) {
                    likeButton.setImageResource(R.drawable.heart_clicked)
                    likeButton.tag = LIKE_TAG
                } else
                    likeButton.setImageResource(R.drawable.heart_not_clicked)
                likeButton.tag = NOT_LIKE_TAG
            }
    }


    fun addNewComment(addComment: TextView, postId: String) {
        val data = HashMap<String, Any>()
        data[COMMENT_TEXT] = addComment.text.toString()
        data[COMMENT_PUBLISHER_USERNAME] = currentUser?.displayName.toString()

        FirebaseFirestore.getInstance().collection(COMMENT_REF).document("postId-" + postId)
            .collection(COMMENT_DOC).add(data)


    }


    fun commentsCounter(post: Post, comments: TextView) {
        val st = "postId-" + post.postId
        var counter = 0
        FirebaseFirestore.getInstance().collection(COMMENT_REF).document(st)
            .collection(COMMENT_DOC)
            .addSnapshotListener { value, error ->
                if (value != null) {
                    counter = value.documents.size
                    comments.text = "view all the $counter comments"
                }

            }
    }

}