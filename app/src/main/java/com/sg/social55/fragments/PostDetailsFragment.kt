package com.sg.social55.fragments

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.FirebaseFirestore
import com.sg.social55.R
import com.sg.social55.adapters.PostAdapter
import com.sg.social55.adapters.PostAdapter1
import com.sg.social55.model.Post
import com.sg.social55.uilities.*
import kotlinx.android.synthetic.main.fragment_post_details.view.*


class PostDetailsFragment : Fragment() {

    private lateinit var postAdapter: PostAdapter1
    private lateinit var postList: ArrayList<Post>
    private lateinit var util: Utility
    var postId = ""


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_post_details, container, false)
        postList = ArrayList()
        util= Utility()
        var recyclerView: RecyclerView = view.recycler_view_post_details
        val layoutMenager = LinearLayoutManager(context)
        recyclerView.layoutManager = layoutMenager
        recyclerView.setHasFixedSize(true)
        postAdapter = PostAdapter1(postList)
        recyclerView.adapter = postAdapter


        val pref = context?.getSharedPreferences("PREFS", Context.MODE_PRIVATE)
        if (pref != null) {
            postId=pref.getString(POST_ID_EXSTRA,"none").toString()

        }
        retrivePost()
        return view
    }

    private fun retrivePost() {
        postList.clear()
        FirebaseFirestore.getInstance().collection(POSTS_REF).document(postId).get()
            .addOnSuccessListener {
                val post = util.covertYoPost(it)
                postList.add(post)
                postAdapter.notifyDataSetChanged()
            }
    }
}