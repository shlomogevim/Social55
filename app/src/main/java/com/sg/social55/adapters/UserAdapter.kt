package com.sg.social55.adapters


import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.sg.social55.R
import com.sg.social55.model.User
import com.squareup.picasso.Picasso
import de.hdodenhof.circleimageview.CircleImageView
import com.google.firebase.firestore.FirebaseFirestore
import com.sg.social55.activities.MainActivity
import com.sg.social55.fragments.ProfileFragment
import com.sg.social55.uilities.*

class UserAdapter(val users: List<User>, var isFragment: Boolean = false) :


    RecyclerView.Adapter<UserAdapter.ViewHolder>() {
    val util = Utility()
    private var currentUser: FirebaseUser? = FirebaseAuth.getInstance().currentUser
    private var currentUserUid = FirebaseAuth.getInstance().currentUser?.uid.toString()
    private var currentUserName = FirebaseAuth.getInstance().currentUser?.displayName.toString()
    private lateinit var context: Context


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        // bolListener()
        context = parent.context
        val view =
            LayoutInflater.from(context).inflate(R.layout.user_item_layout, parent, false)
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

        fun bindUser(user: User) {
            userName.text = user.userName
            userFullName.text = user.fullName
            Picasso.get().load(user.profileImage).placeholder(R.drawable.profile)
                .into(userProfileImage)

            checkFolloingStatus(user)

            followButton.setOnClickListener {
                if (followButton.text == "Follow") {
                    followToFollowing(user)
                  //  util.addUserNotification(user)
                } else {
                    followingToFollow(user)
                }
                checkFolloingStatus(user)
            }

            itemView.setOnClickListener {
                if (isFragment) {
                    val pref = context.getSharedPreferences(SHARPREF_REF, Context.MODE_PRIVATE).edit()
                    pref.putString(USER_IDEXSRTA, user.uid)
                    pref.putString(USER_USERNAMEEXSRTA, user.userName)
                    pref.apply()
                    (context as FragmentActivity).supportFragmentManager.beginTransaction()
                        .replace(R.id.fragment_container, ProfileFragment()).commit()
                } else {
                   // util.logi("UserAdapter11 || /n not fragment ")
                    val intent = Intent(context, MainActivity::class.java)
                    intent.putExtra(PUBLISHER_EXSTRA, user.uid)
                    context.startActivity(intent)
                }
            }
        }

        private fun followToFollowing(user: User) {
            val data = HashMap<String, Any>()
            data["bol"] = true
            val currentName = currentUser?.displayName.toString()
            FirebaseFirestore.getInstance().collection(FOLLOW_REF).document(currentName)
                .collection(FOLLOWING_REF).document(user.userName)
                .set(data) //current follow after  user
                .addOnSuccessListener {
                    FirebaseFirestore.getInstance().collection(FOLLOW_REF).document(user.userName)
                        .collection(FOLLOWER_REF).document(currentName)
                        .set(data)     //user being follow by current
                }
            util.addUserNotification(user)
        }

        private fun followingToFollow(user: User) {
            val currentName = currentUser?.displayName.toString()
            FirebaseFirestore.getInstance().collection(FOLLOW_REF).document(currentName)
                .collection(FOLLOWING_REF).document(user.userName)
                .delete()//current follow after  user
                .addOnSuccessListener {
                    // followButton.text="Follow"
                    FirebaseFirestore.getInstance().collection(FOLLOW_REF).document(user.userName)
                        .collection(FOLLOWER_REF).document(currentName)
                        .delete()     //user being follow by current
                }
        }


        private fun checkFolloingStatus(user: User) {
            val currentName = currentUser?.displayName.toString()
            val userName = user.userName
            FirebaseFirestore.getInstance().collection(FOLLOW_REF)
                .document(currentName).collection(FOLLOWING_REF).document(userName).get()
                .addOnSuccessListener {
                    if (it.exists()) {
                        followButton?.text = "Following"
                    } else {
                        followButton?.text = "Follow"
                    }
                }

        }
    }
}

 /*   private fun addNotification(user:User) {
        val data = HashMap<String, Any>()
        data[NOTIFICATION_PUBLISERID] =user.uid
        data[NOTIFICATION_PUBLISERNAME] =user.userName
        data[POST_PUBLISERID] = currentUserUid
        data[POST_PUBLISERNAME] =currentUserName
        data[NOTIFICATION_TEXT] = "start following you "
        data[NOTIFICATION_POSTID] =""
        data[NOTIFICATION_ISPOST] = NOTIFICATION_ISPOST_FALSE

        FirebaseFirestore.getInstance().collection(NOTIFICATION_REF)
            .document(POST_PUBLISHER_ID).collection(NOTIFICATION_LIST).add(data)
    }
}*/
/*const val NOTIFICATION_PUBLISERID="notification_pablisherId"
const val NOTIFICATION_PUBLISERNAME="notification_pablisherName"
const val POST_PUBLISERID="post_pablisherId"
const val POST_PUBLISERNAME="post_pablisherName"
const val NOTIFICATION_POSTID="notification_postid"
const val NOTIFICATION_TEXT="notification_text"
const val NOTIFICATION_ISPOST="notification_post_or_no"
const val NOTIFICATION_ISPOST_TRUE="Its post"
const val NOTIFICATION_ISPOST_FALSE="Its not post"*/

