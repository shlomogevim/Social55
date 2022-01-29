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
import com.sg.social55.activities.CommentsActivity
import com.sg.social55.fragments.PostDetailsFragment
import com.sg.social55.fragments.ProfileFragment
import com.sg.social55.interfaces.LikeBtnInterface
import com.sg.social55.model.Post
import com.sg.social55.model.User
import com.sg.social55.uilities.*
import com.squareup.picasso.Picasso
import de.hdodenhof.circleimageview.CircleImageView

class MyImagesAdapter(val posts:ArrayList<Post>):RecyclerView.Adapter<MyImagesAdapter.ViewHolder>(){
    private lateinit var context:Context
    val util=Utility()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        context=parent.context
        var view=LayoutInflater.from(context).inflate(R.layout.images_item_layout,parent,false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bindPost(posts[position])
    }

    override fun getItemCount()=posts.size

    inner class ViewHolder(itemView:View):RecyclerView.ViewHolder(itemView){

        val postImage=itemView?.findViewById<ImageView>(R.id.post_image)
        fun bindPost(post: Post) {
            Picasso.get().load(post.postImage).placeholder(R.drawable.profile).into(postImage)
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

