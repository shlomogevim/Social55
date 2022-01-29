package com.sg.social55.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import com.sg.social55.R
import com.sg.social55.adapters.CommentAdapter
import com.sg.social55.databinding.ActivityCommentsBinding
import com.sg.social55.model.Comment
import com.sg.social55.model.Post
import com.sg.social55.uilities.*
import com.squareup.picasso.Picasso


class CommentsActivity() : AppCompatActivity() {


    lateinit var binding: ActivityCommentsBinding
    private var util = Utility()
    lateinit var recyclerView: RecyclerView

    lateinit var commentAdapter: CommentAdapter
    lateinit var comments: ArrayList<Comment>
    var postId = ""


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCommentsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        comments = ArrayList()
        recyclerView = binding.recyclerViewComments
        postId = intent.getStringExtra(POST_ID_EXSTRA).toString()

        commentAdapter = CommentAdapter(comments)
        val layoutManger = LinearLayoutManager(this)
        layoutManger.reverseLayout = true
        recyclerView.layoutManager = layoutManger
        recyclerView.adapter = commentAdapter

        getComments()
        setPostImage()

        binding.postComment.setOnClickListener {
            if (binding.addComment.text.toString() == "") {
                Toast.makeText(this, "Please write comment first ...", Toast.LENGTH_LONG).show()
            } else {
                util.addNewComment(binding.addComment, postId)
                binding.addComment.text.clear()
                finish()
            }
        }
    }

    private fun setPostImage() {

        FirebaseFirestore.getInstance().collection(POSTS_REF).document(postId).get()
            .addOnSuccessListener {
                val post = util.covertYoPost(it)
                Picasso.get().load(post.postImage).placeholder(R.drawable.profile)
                    .into(binding.postImageComment)
            }
    }

    private fun getComments() {
        comments.clear()
        val adress = "postId-" + postId
        FirebaseFirestore.getInstance().collection(COMMENT_REF).document(adress)
            .collection(COMMENT_DOC).addSnapshotListener { value, error ->
                if (value != null) {
                    for (doc in value.documents) {

                        val comment = util.convertToComment(doc)
                        comments.add(comment)

                        // Log.d("gg","comment=$comment , comments.size=${comments.size}")
                      //  Log.d("gg", " comments.size inside=${comments.size}")
                    }
                    Log.d("gg", " comments.size outside=${comments.size}")
                    commentAdapter.notifyDataSetChanged()
                }

            }

    }

}