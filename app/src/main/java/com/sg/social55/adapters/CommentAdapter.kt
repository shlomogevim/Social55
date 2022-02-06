package com.sg.social55.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.FirebaseFirestore
import com.sg.social55.R
import com.sg.social55.model.Comment
import com.sg.social55.model.User
import com.sg.social55.uilities.USER_REF
import com.sg.social55.uilities.Utility
import com.squareup.picasso.Picasso
import de.hdodenhof.circleimageview.CircleImageView

class CommentAdapter(val comments: ArrayList<Comment>) :
    RecyclerView.Adapter<CommentAdapter.ViewHolder>() {
    private lateinit var context: Context
    val util = Utility()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        context = parent.context
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.comments_item_layout, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bindComment(comments[position])

    }

    override fun getItemCount() = comments.size

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        val imageProfile = itemView?.findViewById<CircleImageView>(R.id.user_profile_image_comment)
        val userNameTV = itemView?.findViewById<TextView>(R.id.user_name_comment)
        var commentTv = itemView?.findViewById<TextView>(R.id.comment_comment)

        fun bindComment(comment: Comment) {
            setCurrentUserImage(imageProfile, comment)
            userNameTV.text = comment.publisher
            commentTv.text = comment.text
        }

        private fun setCurrentUserImage(imageProfile: CircleImageView?, comment: Comment) {
            var uid = comment.publisherId
            FirebaseFirestore.getInstance().collection(USER_REF).document(uid).get()
                .addOnSuccessListener {
                    val user: User = util.convertToUser(it)
                    if (user.profileImage.isNotEmpty()) {
                        Picasso.get().load(user.profileImage).placeholder(R.drawable.profile)
                            .into(imageProfile)
                    }

                }

        }

    }
}

