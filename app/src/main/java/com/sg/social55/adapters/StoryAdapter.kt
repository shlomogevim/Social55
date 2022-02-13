package com.sg.social55.adapters

import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.content.DialogInterface.BUTTON_NEUTRAL
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat.startActivities
import androidx.core.content.ContextCompat.startActivity
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.sg.social55.R
import com.sg.social55.activities.AddStoryActivity
import com.sg.social55.activities.MainActivity
import com.sg.social55.activities.StoryActivity
import com.sg.social55.model.Story
import com.sg.social55.uilities.*
import com.squareup.picasso.Picasso
import de.hdodenhof.circleimageview.CircleImageView
import kotlinx.android.synthetic.main.fragment_profile.*
import kotlinx.android.synthetic.main.fragment_profile.view.*

class StoryAdapter(val stories: ArrayList<Story>) :   RecyclerView.Adapter<StoryAdapter.ViewHolder>() {
    private lateinit var context: Context
    val util = Utility()
    val currentUserId = FirebaseAuth.getInstance().currentUser?.uid

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        context = parent.context
    //   util.logi("StoryAdapter11 \n viewType=$viewType")
        if (viewType == 0) {
            var view = LayoutInflater.from(context).inflate(R.layout.add_story_item, parent, false)
            return ViewHolder(view)
        } else {
            var view = LayoutInflater.from(context).inflate(R.layout.story_item, parent, false)
            return ViewHolder(view)
        }
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bindStory(stories[position], position)
    }

    override fun getItemCount() = stories.size

    override fun getItemViewType(position: Int): Int {
        if (position == 0)
            return 0
        return 1
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        val story_image_seen = itemView?.findViewById<CircleImageView>(R.id.story_image_seen)
        val story_image = itemView?.findViewById<CircleImageView>(R.id.story_image)
        val story_username = itemView?.findViewById<TextView>(R.id.story_username)
        val story_plus_btn = itemView?.findViewById<ImageView>(R.id.story_add)
        val addStory_text = itemView?.findViewById<TextView>(R.id.add_story_text)

        fun bindStory(story: Story, index: Int) {
            userInfo(story.userId,index)

            if (adapterPosition!==0){
                seenStory(story.storyId)
            }
            if (adapterPosition===0){
           //   util.logi("StoryAdapter12 \n adapterPosition=$adapterPosition")
                myStories(addStory_text,story_plus_btn,false)
            }

            itemView.setOnClickListener {
                if (adapterPosition===0){
                    myStories(addStory_text,story_plus_btn,true)
                }else{
                    var intent = Intent(Intent(context, StoryActivity::class.java))
                    intent.putExtra(STORY_USER_ID, story.userId)
                    context.startActivities(arrayOf(intent))
                }
            }
        }

        private fun userInfo(userId: String, index: Int) {
            FirebaseFirestore.getInstance().collection(USER_REF).document(userId).get()
                .addOnSuccessListener {
                    val user = util.convertToUser(it)
                    Picasso.get().load(user.profileImage).placeholder(R.drawable.profile)
                        .into(story_image)
                    if (index != 0) {
                        Picasso.get().load(user.profileImage).placeholder(R.drawable.profile)
                            .into(story_image_seen)
                        story_username.text = user.userName
                    }
                }
        }


        private fun myStories(textView: TextView, imageView: ImageView, click: Boolean) {

            //  util.logi("StoryAdapter13 /n currentUserId.toString()={currentUserId.toString()}")
            var counter = 0
            FirebaseFirestore.getInstance().collection(STORY_REF).document(STORIES_USERS_LIST)
                .collection(currentUserId.toString()).addSnapshotListener { value, error ->
                    if (value != null) {

                        val timeCurrent = System.currentTimeMillis()
                        for (doc in value.documents) {
                            val story = util.covertToStory(doc)
                            if (timeCurrent > story.timeStart && timeCurrent < story.timeEnd) {
                                counter++
                            }
                        }
                    }
                }

          //  util.logi("StoryAdapter14 /n  counter=${counter}, click=${click}")

                        if (click) {
                            if (counter > 0) {
                                val aleratDialog = AlertDialog.Builder(context).create()
                                aleratDialog.setButton(
                                    AlertDialog.BUTTON_NEUTRAL,
                                    "View Story"
                                ) { dialogInterface, which ->
                                    val intent = Intent(context, StoryActivity::class.java)
                                    intent.putExtra(STORY_USER_ID, currentUserId)
                                    context.startActivities(arrayOf(intent))
                                    dialogInterface.dismiss()
                                }
                                aleratDialog.setButton(AlertDialog.BUTTON_POSITIVE, "Add Story")
                                { dialogInterface, which ->
                                    val intent = Intent(context, AddStoryActivity::class.java)
                                    intent.putExtra(STORY_USER_ID, currentUserId)
                                    context.startActivities(arrayOf(intent))
                                    dialogInterface.dismiss()
                                }
                                aleratDialog.show()
                            }
                            else {
                                val intent = Intent(context, AddStoryActivity::class.java)
                           //    util.logi("StoryAdapter15 /n  currentUserId=${currentUserId}")
                                intent.putExtra(STORY_USER_ID, currentUserId)
                                context.startActivities(arrayOf(intent))
                            }
                        } else {                                        // click false
                            if (counter > 0) {
                                textView.text = "My Story"
                                imageView.visibility = View.GONE
                            } else {
                                textView.text = "Add Story"
                                imageView.visibility = View.VISIBLE
                            }

                        }

        }




      /*  private fun myStories(textView: TextView, imageView: ImageView, click: Boolean) {

          //  util.logi("StoryAdapter13 /n currentUserId.toString()={currentUserId.toString()}")



               FirebaseFirestore.getInstance().collection(STORY_REF).document(STORIES_USERS_LIST)
                    .collection(currentUserId.toString()).addSnapshotListener { value, error ->
                        if (value != null) {
                            var counter = 0
                            val timeCurrent = System.currentTimeMillis()
                            for (doc in value.documents) {
                                val story = util.covertToStory(doc)
                                if (timeCurrent > story.timeStart && timeCurrent < story.timeEnd) {
                                    counter++
                                }
                            }
                            if (click) {
                                if (counter > 0) {
                                    val aleratDialog = AlertDialog.Builder(context).create()
                                    aleratDialog.setButton(
                                        AlertDialog.BUTTON_NEUTRAL,
                                        "View Story"
                                    ) { dialogInterface, which ->
                                        val intent = Intent(context, StoryActivity::class.java)
                                        intent.putExtra(STORY_USER_ID, currentUserId)
                                        context.startActivities(arrayOf(intent))
                                        dialogInterface.dismiss()
                                    }
                                    aleratDialog.setButton(AlertDialog.BUTTON_POSITIVE, "Add Story")
                                    { dialogInterface, which ->
                                        val intent = Intent(context, AddStoryActivity::class.java)
                                        intent.putExtra(STORY_USER_ID, currentUserId)
                                        context.startActivities(arrayOf(intent))
                                        dialogInterface.dismiss()
                                    }
                                    aleratDialog.show()
                                }
                                else {
                                    val intent = Intent(context, AddStoryActivity::class.java)
                                    intent.putExtra(STORY_USER_ID, currentUserId)
                                    context.startActivities(arrayOf(intent))
                                }
                            } else {
                                if (counter > 0) {
                                    textView.text = "My Story"
                                    imageView.visibility = View.GONE
                                } else {
                                    textView.text = "Add Story"
                                    imageView.visibility = View.VISIBLE
                                }

                            }
                        }
                    }
        }*/

      /*  private fun seenStory(userId: String) {
            val storyRef =
                FirebaseFirestore.getInstance().collection(STORY_REF).document(STORIES_USERS_LIST)
                    .collection(userId)
            var i = 0
            storyRef.addSnapshotListener { value, error ->           // looking in stories list
                if (value != null) {
                    for (doc in value.documents) {
                        var story = util.covertToStory(doc)
                        storyRef.document(story.storyId).collection(STORY_VIEWS)
                            .addSnapshotListener { value1, error ->
                                if (value1 != null) {
                                    i = value1.documents.size
                                }
                            }
                    }
                    if (i > 0) {
                        story_image.visibility = View.VISIBLE
                        story_image_seen.visibility = View.GONE
                    } else {
                        story_image.visibility = View.GONE
                        story_image_seen.visibility = View.VISIBLE
                    }
                }
            }
        }
    }*/

        private fun seenStory(userId: String) {
          /*  FirebaseFirestore.getInstance().collection(STORY_REF).document(STORIES_USERS_LIST)
                .collection(userId).add(util.simpleData())*/


           /*  FirebaseFirestore.getInstance().collection(STORY_REF).document(STORIES_USERS_LIST)
                    .collection(userId)*/
         /*   var i = 0
            storyRef.addSnapshotListener { value, error ->           // looking in stories list
                if (value != null) {
                    for (doc in value.documents) {
                        var story = util.covertToStory(doc)
                        storyRef.document(story.storyId).collection(STORY_VIEWS)
                            .addSnapshotListener { value1, error ->
                                if (value1 != null) {
                                    i = value1.documents.size
                                }
                            }
                    }
                    if (i > 0) {
                        story_image.visibility = View.VISIBLE
                        story_image_seen.visibility = View.GONE
                    } else {
                        story_image.visibility = View.GONE
                        story_image_seen.visibility = View.VISIBLE
                    }
                }
            }*/
        }                        //end fun
    }




















    /* private stam() {
            val ref = FirebaseFirestore.getInstance().collection(STORY_REF).document(STORIES_USERS_LIST)
                    .collection(currentUserId.toString()).addSnapshotListener { value, error ->

                    if (value != null) {
                        for (doc in value.documents){
                            val story=util.covertToStory(doc)

                        }
                    }
                }
        }
        */
    /*  private fun userInfo1(viewHolder: ViewHolder, userId: String, position: Int) {
          FirebaseFirestore.getInstance().collection(USER_REF).document(userId).get()
              .addOnSuccessListener {
                  val user = util.convertToUser(it)
                  Picasso.get().load(user.profileImage).placeholder(R.drawable.profile)
                      .into(viewHolder.story_image)
                  if (position != 0) {
                      Picasso.get().load(user.profileImage).placeholder(R.drawable.profile)
                          .into(viewHolder.story_image_seen)
                      viewHolder.story_username.text = user.userName
                  }

              }
      }*/

}



