package com.sg.social55.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import com.sg.social55.R
import com.sg.social55.model.User
import com.sg.social55.uilities.FOLLOWER_REF
import com.sg.social55.uilities.FOLLOWING_REF
import com.sg.social55.uilities.FOLLOW_REF
import com.squareup.picasso.Picasso
import de.hdodenhof.circleimageview.CircleImageView

class UserAdapter(val users: List<User>, var isFragment: Boolean = false) :
    RecyclerView.Adapter<UserAdapter.ViewHolder>() {

    private var currentUser: FirebaseUser? = FirebaseAuth.getInstance().currentUser
    lateinit var context1: Context


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
         context1=parent.context
        val view =
            LayoutInflater.from(parent?.context).inflate(R.layout.user_item_layout, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bindUser(users[position])
    }

    override fun getItemCount() = users.size

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        val userName = itemView?.findViewById<TextView>(R.id.user_name_search)
        val userFullName = itemView?.findViewById<TextView>(R.id.user_full_name_search)
        val userProfileImage =
            itemView?.findViewById<CircleImageView>(R.id.user_profile_image_search)
        val followButton = itemView?.findViewById<Button>(R.id.follow_btn_search)


        /*fun addCommentClick(view: View) {
        val commentText = enterCommentText.text.toString()
        val thoughrRef = FirebaseFirestore.getInstance()
            .collection(THOUGHT_REF).document(thoughtDocumentID)

        FirebaseFirestore.getInstance().runTransaction { transaction ->
            val thought = transaction.get(thoughrRef)
            val numComments = thought.getLong(NUM_COMMENTS)?.plus(1)
            transaction.update(thoughrRef, NUM_COMMENTS, numComments)

            val newCommentRef = FirebaseFirestore.getInstance().collection(THOUGHT_REF)
                .document(thoughtDocumentID).collection(COMMENTS_REF).document()

            val data = HashMap<String, Any>()
            data.put(COMMENTS_TXT, commentText)
            data.put(TIMESTAMP, FieldValue.serverTimestamp())
            data.put(USERNAME, FirebaseAuth.getInstance().currentUser?.displayName.toString())
            transaction.set(newCommentRef, data)

    }*/

        private fun operateFollow(user: User) {
            val data=HashMap<String,Any>()
            data.put("bol",true)
            val currentName=currentUser?.displayName.toString()
            /* FirebaseFirestore.getInstance().collection(FOLLOW_REF)
                 .document(currentUser?.uid.toString()).set(data)*/
           /* FirebaseFirestore.getInstance().collection(FOLLOW_REF)
                .document(currentUser?.displayName.toString()).set(data)*/
            FirebaseFirestore.getInstance().collection(FOLLOW_REF).document(currentName).
                collection(FOLLOWING_REF).document(user.userName).set(data) //current follow after  user
                .addOnSuccessListener {
                    followButton.text= "Remove"
                    FirebaseFirestore.getInstance().collection(FOLLOW_REF).document(user.userName)
                        .collection(FOLLOWER_REF).document(currentName).set(data)     //user being follow by current
                }
                        .addOnSuccessListener {

                        }
                        .addOnFailureListener {

                        }
     Toast.makeText(context1,"current->${currentUser?.displayName},"+
                                       "pointing to -> ${user.fullName}",Toast.LENGTH_LONG).show()

        }
        private fun operateRemove(user: User) {
            val currentName=currentUser?.displayName.toString()
            FirebaseFirestore.getInstance().collection(FOLLOW_REF).document(currentName).
            collection(FOLLOWING_REF).document(user.userName).delete()//current follow after  user
                .addOnSuccessListener {
                    followButton.text="Follow"
                    FirebaseFirestore.getInstance().collection(FOLLOW_REF).document(user.userName)
                        .collection(FOLLOWER_REF).document(currentName).delete()     //user being follow by current
                }
                .addOnSuccessListener {}
                .addOnFailureListener {}
        }
        fun bindUser(user: User) {
            userName.text = user.userName
            userFullName.text = user.fullName
            Picasso.get().load(user.profileImage).placeholder(R.drawable.profile)
                .into(userProfileImage)
            followButton.setOnClickListener {
                if (followButton.text.toString() == "Follow") {
                    operateFollow(user)
                       //addNotification(user.uid)
                } else {
                    operateRemove(user)

                }


                    /* val followRef=FirebaseFirestore.getInstance()
                         .collection(FOLLOW_REF).document(currentUser!!.uid)
                     FirebaseFirestore.getInstance().runTransaction { transition->
                         val follow=transition.get(followRef)
                         val data=HashMap<String,Any>()            // its  work
                         data["isFollowing"]="true"
                         val newFoloowing=FirebaseFirestore.getInstance().collection(FOLLOW_REF)
                             .document(currentUser!!.uid).collection(FOLLOWING_REF).document()
                         transition.set(newFoloowing,data)
                     }.addOnSuccessListener { }.addOnFailureListener {}*/


                    /* val data=HashMap<String,Any>()            // its  work
                     data["name"]=user.userName
                     data["full_name"]=user.fullName
                     currentUser?.let { it1 ->
                         FirebaseFirestore.getInstance().collection(FOLLOW_REF).document(
                             it1.uid).collection(FOLLOWING_REF).add(data)
                             .addOnSuccessListener { }
                             .addOnFailureListener { }
                     }*/


                    /*  currentUser?.uid.let { it1->
                         FirebaseFirestore.getInstance().collection(FOLLOW_REF).document(it1.toString())
                             .collection(FOLLOWING_REF).document(user.uid).set(true)
                             .addOnSuccessListener {
                                 currentUser?.uid.let { it1->
                                     FirebaseFirestore.getInstance().collection(FOLLOW_REF).document(it1.toString())


                                         *//*.
                                       .collection(FOLLOWING_REF).document(user.uid).set(true)
                                       .addOnSuccessListener {

                                       }.addOnFailureListener {

                                       }*//*
                               }

                           }.addOnFailureListener {

                           }
                    }*/

            }

        }




    }
}