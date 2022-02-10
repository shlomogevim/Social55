package com.sg.social55.adapters

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat.startActivities
import androidx.core.content.ContextCompat.startActivity
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.sg.social55.R
import com.sg.social55.activities.AddStoryActivity
import com.sg.social55.activities.MainActivity
import com.sg.social55.model.Story
import com.sg.social55.uilities.STORY_USER_ID
import com.sg.social55.uilities.USER_REF
import com.sg.social55.uilities.Utility
import com.squareup.picasso.Picasso
import de.hdodenhof.circleimageview.CircleImageView
import kotlinx.android.synthetic.main.fragment_profile.*
import kotlinx.android.synthetic.main.fragment_profile.view.*

class StoryAdapter (val stories:ArrayList<Story>):RecyclerView.Adapter<StoryAdapter.ViewHolder>(){
    private lateinit var context: Context
    val util= Utility()
    val currentUserId=FirebaseAuth.getInstance().currentUser?.uid

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        context=parent.context
        if (viewType==0){
            var view= LayoutInflater.from(context).inflate(R.layout.add_story_item,parent,false)
            return ViewHolder(view)
        }
        else{
            var view= LayoutInflater.from(context).inflate(R.layout.story_item,parent,false)
            return ViewHolder(view)
        }
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
      //  val story=stories[position]
        holder.bindStory(stories[position],position)

       // userInfo(holder,story.userId,position)
    }

    override fun getItemCount()=stories.size

    override fun getItemViewType(position: Int): Int {
        if (position==0)
        {
            return 0
        }
        return 1
    }

    inner class ViewHolder(itemView: View):RecyclerView.ViewHolder(itemView){

        val story_image_seen=itemView?.findViewById<CircleImageView>(R.id.story_image_seen)
        val story_image=itemView?.findViewById<CircleImageView>(R.id.story_image)
        val story_username=itemView?.findViewById<TextView>(R.id.story_username)
        val story_plus_btn=itemView?.findViewById<ImageView>(R.id.story_add)
        val addStory_text=itemView?.findViewById<TextView>(R.id.add_story_text)

        fun bindStory(story: Story,index:Int){
            FirebaseFirestore.getInstance().collection(USER_REF).document(story.userId).get()
                .addOnSuccessListener {
                    val user=util.convertToUser(it)
                    Picasso.get().load(user.profileImage).placeholder(R.drawable.profile)
                        .into(story_image)
                    if (index!=0){
                        Picasso.get().load(user.profileImage).placeholder(R.drawable.profile)
                            .into(story_image_seen)
                        story_username.text=user.userName
                    }
                }
            itemView.setOnClickListener {
                var intent=Intent(Intent(context, AddStoryActivity::class.java))
                intent.putExtra(STORY_USER_ID,story.userId)
               context.startActivities(arrayOf(intent))
            }
        }

       /* private fun userInfo(userId:String,position:Int) {
            FirebaseFirestore.getInstance().collection(USER_REF).document(currentUserId.toString()).get()
                .addOnSuccessListener {
                    val user=util.convertToUser(it)
                    Picasso.get().load(user.profileImage).placeholder(R.drawable.profile)
                        .into(story_image)
                    if (position!=0){
                        Picasso.get().load(user.profileImage).placeholder(R.drawable.profile)
                            .into(story_image_seen)
                        story_username.text=user.userName
                    }
                }
        }*/

    }

    private fun userInfo1(viewHolder:ViewHolder,userId:String,position:Int) {
        FirebaseFirestore.getInstance().collection(USER_REF).document(userId).get()
            .addOnSuccessListener {
                val user=util.convertToUser(it)
                Picasso.get().load(user.profileImage).placeholder(R.drawable.profile)
                    .into(viewHolder.story_image)
                if (position!=0){
                    Picasso.get().load(user.profileImage).placeholder(R.drawable.profile)
                        .into(viewHolder.story_image_seen)
                    viewHolder.story_username.text=user.userName
                }

            }
    }
/*   fun bindStory(story: Story){
            itemView.setOnClickListener {
                var intent=Intent(Intent(context, AddStoryActivity::class.java))
                intent.putExtra(STORY_USER_ID,story.userId)
               context.startActivities(arrayOf(intent))
            }
        }
    }

    private fun userInfo(viewHolder:ViewHolder,userId:String,position:Int) {
        FirebaseFirestore.getInstance().collection(USER_REF).document(userId).get()
            .addOnSuccessListener {
                val user=util.convertToUser(it)
                Picasso.get().load(user.profileImage).placeholder(R.drawable.profile)
                    .into(viewHolder.story_image)
                if (position!=0){
                    Picasso.get().load(user.profileImage).placeholder(R.drawable.profile)
                        .into(viewHolder.story_image_seen)
                    viewHolder.story_username.text=user.userName
                }

            }
    }*/

}

