package com.sg.social55.adapters

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.FirebaseFirestore
import com.sg.social55.R
import com.sg.social55.activities.MainActivity
import com.sg.social55.fragments.PostDetailsFragment
import com.sg.social55.fragments.ProfileFragment
import com.sg.social55.model.Notification
import com.sg.social55.model.Post
import com.sg.social55.model.User
import com.sg.social55.uilities.*
import com.squareup.picasso.Picasso
import kotlinx.coroutines.async
import kotlinx.coroutines.runBlocking
import java.util.ArrayList

class NotificationAdapter(val notifications: ArrayList<Notification>) :
    RecyclerView.Adapter<NotificationAdapter.ViewHolder>() {
    private lateinit var context: Context
    val util = Utility()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        context = parent.context
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.notifications_item_layout, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bindNotification(notifications[position])
    }

    override fun getItemCount() = notifications.size

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        val postImage = itemView?.findViewById<ImageView>(R.id.notification_post_image)
        val profileImage = itemView?.findViewById<ImageView>(R.id.notification_profile_image)
        val userName = itemView?.findViewById<TextView>(R.id.username_notification)
        val text = itemView?.findViewById<TextView>(R.id.comment_notification)


        fun bindNotification(notification: Notification) {
            var user: User
            var post= Post()
            FirebaseFirestore.getInstance().collection(USER_REF).document(notification.userId).get()
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        //commented
                        user = util.convertToUser(task.result)
                        //util.logi("user=$user")
                        if (notification.text.contains(" follow ")) {
                            userName.text = "You"
                        } else {
                            userName.text = user.userName
                        }

                        text.text = notification.text
                        Picasso.get().load(user.profileImage).placeholder(R.drawable.profile)
                            .into(profileImage)
                    }
                }
            if (notification.ispost == NOTIFICATION_ISPOST_TRUE) {
            //    var post: Post
                FirebaseFirestore.getInstance().collection(POSTS_REF).document(notification.postId)
                    .get()
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            post = util.covertYoPost(task.result)
                            // util.logi("NitificationAdapter  || post=$post")
                            postImage.visibility = View.VISIBLE
                            Picasso.get().load(post.postImage).placeholder(R.drawable.profile)
                                .into(postImage)
                        } else {
                            postImage.visibility = View.GONE
                        }
                    }
            }
            profileImage.setOnClickListener {
                //util.logi("NotificationAdapter \n 11")
                val pref = context.getSharedPreferences(SHARPREF_REF, Context.MODE_PRIVATE).edit()
                 FirebaseFirestore.getInstance().collection(USER_REF).document(notification.userId).get()
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            val user = util.convertToUser(task.result)
                            pref.putString(POST_PUBLISHER_ID, user.uid)
                            pref.putString(POST_PUBLISHER, user.userName)
                            util.logi("NotificationAdapter12 \n user.uid=${user.uid}")
                            (context as FragmentActivity).supportFragmentManager.beginTransaction()
                            .replace(R.id.fragment_container, ProfileFragment()).commit()
                        }
                    }
            }

            postImage.setOnClickListener {
                val editor = context.getSharedPreferences(SHARPREF_REF, Context.MODE_PRIVATE).edit()
                editor.putString(POST_ID_EXSTRA, post.postId)
                editor.apply()
                (context as FragmentActivity).supportFragmentManager.beginTransaction()
                    .replace(R.id.fragment_container, PostDetailsFragment()).commit()

            }
        }
    }


}



