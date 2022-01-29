package com.sg.social55.adapters

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.sg.social55.R
import com.sg.social55.activities.CommentsActivity
import com.sg.social55.fragments.ProfileFragment
import com.sg.social55.interfaces.LikeBtnInterface
import com.sg.social55.model.Post
import com.sg.social55.model.User
import com.sg.social55.uilities.*
import com.squareup.picasso.Picasso
import de.hdodenhof.circleimageview.CircleImageView

class PostAdapter(
    val posts: ArrayList<Post>, val likeBtnFun: LikeBtnInterface
) : RecyclerView.Adapter<PostAdapter.ViewHolder>() {

    private lateinit var context: Context
    private val util = Utility()
    private val simpleData = HashMap<String, Any>()
    private val currentUid = FirebaseAuth.getInstance().currentUser!!.uid


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        context = parent.context
        simpleData.put("bol", "true")
        val view =
            LayoutInflater.from(context).inflate(R.layout.posts_layout, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bindPost(posts[position])
    }

    override fun getItemCount() = posts.size

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val profileImage = itemView?.findViewById<CircleImageView>(R.id.user_profile_image_post)
        val postImage = itemView?.findViewById<ImageView>(R.id.post_image_home)
        val likeButton = itemView?.findViewById<ImageView>(R.id.post_image_like_btn)
        val commentButton = itemView?.findViewById<ImageView>(R.id.post_image_comment_btn)
        val saveButton = itemView?.findViewById<ImageView>(R.id.post_save_comment_btn)
        val userName = itemView?.findViewById<TextView>(R.id.user_name_post)
        val likesCounter = itemView?.findViewById<TextView>(R.id.likesCounter)
        val publisher = itemView?.findViewById<TextView>(R.id.publisher)
        val description = itemView?.findViewById<TextView>(R.id.description)
        val commentCounter = itemView?.findViewById<TextView>(R.id.comments)



        fun bindPost(post: Post) {
            Picasso.get().load(post.postImage).into(postImage)
            FirebaseFirestore.getInstance().collection(USER_REF).document(post.postPublisherId)
                .get()
                .addOnSuccessListener {
                    val user: User = util.convertToUser(it)
                    if (user.profileImage.isNotEmpty()) {
                        Picasso.get().load(user.profileImage).placeholder(R.drawable.profile)
                            .into(profileImage)
                    }
                    userName.text = user.userName
                    publisher.text = "Bublisher: " + user.fullName
                    description.text = "Decription: " + post.description
                }
          //checkSaveStatus(post.postId, saveButton, keyBTnSetuatin)
          checkSaveStatus(post.postId, saveButton)
            util.isLikes(post.postId, likeButton)
            util.operateLikeCounter(post.postId, likesCounter)
            util.commentsCounter(post, commentCounter)

           operatePressButton(post, saveButton)
        }


        private fun operatePressButton(post: Post, imageView: ImageView) {

            profileImage.setOnClickListener {
                val editor = context.getSharedPreferences(SHARPREF_REF, Context.MODE_PRIVATE).edit()
                editor.putString(PROFILE_ID_EXSTRA, post.postPublisherId)
                editor.putString(PUBLISHER_EXSTRA, post.publisher)
                editor.apply()
                (context as FragmentActivity).supportFragmentManager.beginTransaction()
                    .replace(R.id.fragment_container, ProfileFragment()).commit()
            }
            commentButton.setOnClickListener {

                val intentComment = Intent(context, CommentsActivity::class.java)
                intentComment.putExtra(POST_ID_EXSTRA, post.postId)
                intentComment.putExtra(USER_USERNAMEEXSRTA, post.postPublisherId)
                context.startActivity(intentComment)

            }
            likeButton.setOnClickListener {
                likeBtnFun.likePost(post, likeButton, likesCounter)
            }
            commentCounter.setOnClickListener {
                val intent = Intent(context, CommentsActivity::class.java)
                intent.putExtra(POST_ID_EXSTRA, post.postId)
                context.startActivity(intent)
            }

            saveButton.setOnClickListener {
               // util.logi("inside saveButtun 1 saveButton.tag=${saveButton.tag}")
                if (saveButton.tag == SAVE_EXSIST) {
                    saveButton.tag = SAVE_NOT_EXSIST
                    saveButton.setImageResource(R.drawable.save_unfilled_large_icon)
                    FirebaseFirestore.getInstance()
                        .collection(SAVE_REF).document(currentUid)
                      //  .collection("PostId").document(post.postId).delete()
                        .collection(POSTID_COLLECTION).document(post.postId).delete()

                } else {
                    FirebaseFirestore.getInstance()
                        .collection(SAVE_REF).document(currentUid)
                    //    .collection("PostId").document(post.postId).set(simpleData)
                        .collection(POSTID_COLLECTION).document(post.postId).set(simpleData)
                }

            }


        }

        private fun checkSaveStatus(postId: String, imageView: ImageView) {
            FirebaseFirestore.getInstance().collection(SAVE_REF).document(currentUid)
              //  .collection("PostId").document(postId)
                .collection(POSTID_COLLECTION).document(postId)
                .addSnapshotListener { value, error ->

                    if (value != null) {
                        if (value.exists()) {
                            imageView.setImageResource(R.drawable.save_large_icon)
                            imageView.tag = SAVE_EXSIST
                        } else {
                            imageView.setImageResource(R.drawable.save_unfilled_large_icon)
                            imageView.tag = SAVE_NOT_EXSIST
                        }
                    } else {
                        imageView.setImageResource(R.drawable.save_unfilled_large_icon)
                        imageView.tag = SAVE_NOT_EXSIST
                    }

                }

        }

    }
}










