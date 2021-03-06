package com.sg.social55.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.sg.social55.R
import com.sg.social55.adapters.CommentAdapter
import com.sg.social55.databinding.ActivityCommentsBinding
import com.sg.social55.model.Comment
import com.sg.social55.uilities.*
import com.squareup.picasso.Picasso


class CommentsActivity() : AppCompatActivity() {


    lateinit var binding: ActivityCommentsBinding
    private var util = Utility()
    lateinit var recyclerView: RecyclerView

    lateinit var commentAdapter: CommentAdapter
    val comments = ArrayList<Comment>()
    var postId = ""
    var publisher = ""
    var publisherId = ""
    val currentUserUid = FirebaseAuth.getInstance().currentUser?.uid.toString()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCommentsBinding.inflate(layoutInflater)
        setContentView(binding.root)


        val intent = intent
        postId = intent.getStringExtra(COMMENT_POST_ID).toString()
        publisher = intent.getStringExtra(COMMENT_PUBLISHER).toString()
        publisherId = intent.getStringExtra(COMMENT_PUBLISHER_ID).toString()
        //     util.logi("postId=$postId ,  publisher=$publisher ,  publisherId=$publisherId")

        recyclerView = binding.recyclerViewComments
        commentAdapter = CommentAdapter(comments)
        val layoutManger = LinearLayoutManager(this)
        layoutManger.reverseLayout = true
        recyclerView.layoutManager = layoutManger
        recyclerView.adapter = commentAdapter



        userInfo()
        readComments()
        getPostImage()


        binding.postComment.setOnClickListener {
            val commentText = binding.addComment.text.toString()
          // util.logi("CommentsActivity||\n commentText11 =$commentText")
            if (commentText == "") {
                Toast.makeText(this, "Please write comment first ...", Toast.LENGTH_LONG).show()
            } else {
                if ((!postId.isNullOrBlank())
                    && (!publisher.isNullOrBlank())
                    && (!publisherId.isNullOrBlank())
                ) {
                 //   util.logi("CommentsActivity|| \n commentText22 =$commentText")
                    util.addCommentNotification(postId, commentText)
                    binding.addComment.text.clear()
                    finish()
                }
            }
        }
    }

    private fun userInfo() {
        FirebaseFirestore.getInstance().collection(USER_REF).document(currentUserUid).get()
            .addOnSuccessListener {
                val user = util.convertToUser(it)
                Picasso.get().load(user.profileImage).placeholder(R.drawable.profile)
                    .into(binding.profileImageComment)
            }
    }

    private fun readComments() {
        comments.clear()
        FirebaseFirestore.getInstance().collection(COMMENT_REF).document(postId)
            .collection(COMMENT_DOC).addSnapshotListener { value, error ->
                if (value != null) {
                    for (doc in value.documents) {
                        val comment = util.convertToComment(doc)
                        comments.add(comment)
                    }
                    commentAdapter.notifyDataSetChanged()
                }
            }
    }

    private fun getPostImage() {
        FirebaseFirestore.getInstance().collection(POSTS_REF).document(postId).get()
            .addOnSuccessListener {
                val post = util.covertYoPost(it)
                Picasso.get().load(post.postImage).placeholder(R.drawable.profile)
                    .into(binding.postImageComment)
            }
    }

}


