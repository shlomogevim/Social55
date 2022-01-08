package com.sg.social55.adapters


import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.sg.social55.R
import com.sg.social55.model.User
import com.sg.social55.uilities.FOLLOWER_REF
import com.sg.social55.uilities.FOLLOWING_REF
import com.sg.social55.uilities.FOLLOW_REF
import com.squareup.picasso.Picasso
import de.hdodenhof.circleimageview.CircleImageView
import com.google.firebase.firestore.FirebaseFirestore

class UserAdapter(val users: List<User>, var isFragment: Boolean = false) :
    RecyclerView.Adapter<UserAdapter.ViewHolder>() {

    private var currentUser: FirebaseUser? = FirebaseAuth.getInstance().currentUser
   // private  var folloingList=ArrayList<String>()



    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
       // bolListener()

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

        private fun followToFollowing(user: User) {
            val data=HashMap<String,Any>()
            data.put("bol",true)
            data.put("name",user.userName)
            val currentName=currentUser?.displayName.toString()

            FirebaseFirestore.getInstance().collection(FOLLOW_REF).document(currentName).
                collection(FOLLOWING_REF).document(user.userName).set(data) //current follow after  user
                .addOnSuccessListener {
                    FirebaseFirestore.getInstance().collection(FOLLOW_REF).document(user.userName)
                        .collection(FOLLOWER_REF).document(currentName).set(data)     //user being follow by current
                }
        }
        private fun followingToFollow(user: User) {
            val currentName=currentUser?.displayName.toString()
            FirebaseFirestore.getInstance().collection(FOLLOW_REF).document(currentName).
            collection(FOLLOWING_REF).document(user.userName).delete()//current follow after  user
                .addOnSuccessListener {
                   // followButton.text="Follow"
                    FirebaseFirestore.getInstance().collection(FOLLOW_REF).document(user.userName)
                        .collection(FOLLOWER_REF).document(currentName).delete()     //user being follow by current
                }
        }

        fun bindUser(user: User) {
            userName.text = user.userName
            userFullName.text = user.fullName
            Picasso.get().load(user.profileImage).placeholder(R.drawable.profile)
                .into(userProfileImage)

            checkFolloingStatus(user)

            followButton.setOnClickListener {
                if (followButton.text=="Follow"){
                    followToFollowing(user)
                }else{
                    followingToFollow(user)
                }
                checkFolloingStatus(user)
            }
        }


        private fun checkFolloingStatus(user: User) {
            val currentName=currentUser?.displayName.toString()
            val userName=user.userName

            FirebaseFirestore.getInstance().collection(FOLLOW_REF)
                .document(currentName).collection(FOLLOWING_REF).document(userName).get()

                .addOnSuccessListener {
                    if (it.exists()) {
                            followButton?.text = "Following"
                        }else {
                        followButton?.text = "Follow"
                    }
                }

        }

    }
}

