package com.sg.social55.activities

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MotionEvent
import android.view.View
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.sg.social55.R
import com.sg.social55.databinding.ActivityStoryBinding
import com.sg.social55.uilities.*
import com.squareup.picasso.Picasso
import jp.shts.android.storiesprogressview.StoriesProgressView

class StoryActivity : AppCompatActivity(), StoriesProgressView.StoriesListener {
    lateinit var binding: ActivityStoryBinding
    var currentUserId: String? = null
    var userId = ""
    var counter = 0
    var pressTime = 0L
    var limit = 500L
    val util = Utility()
    var imageList = ArrayList<String>()
    var storyIdsList = ArrayList<String>()
    var storiesProgressView: StoriesProgressView? = null
    @SuppressLint("ClickableViewAccessibility")
    private var onTouchListenet = View.OnTouchListener { view, motionEvent ->
        when (motionEvent.action) {
            MotionEvent.ACTION_DOWN -> {
                pressTime = System.currentTimeMillis()
                storiesProgressView!!.pause()
                return@OnTouchListener false
            }
            MotionEvent.ACTION_UP -> {
                val now = System.currentTimeMillis()
                storiesProgressView!!.resume()
                return@OnTouchListener limit < now - pressTime
            }
        }
        false
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityStoryBinding.inflate(layoutInflater)
        setContentView(binding.root)
        currentUserId = FirebaseAuth.getInstance().currentUser?.uid
        userId = intent.getStringExtra(STORY_USER_ID).toString()

        storiesProgressView = binding.storiesProgress

        binding.layoutSeen.visibility = View.GONE
        binding.storyDelete.visibility = View.GONE

        if (userId == currentUserId) {
            binding.layoutSeen.visibility = View.VISIBLE
            binding.storyDelete.visibility = View.VISIBLE
        }
        getStories(userId)
        userInfo(userId)

        binding.reverse.setOnClickListener {
            storiesProgressView!!.reverse()
        }
        binding.reverse.setOnTouchListener(onTouchListenet)

        binding.skip.setOnClickListener {
            storiesProgressView!!.skip()
        }
        binding.skip.setOnTouchListener(onTouchListenet)

        binding.seenNumber.setOnClickListener {
            val intent = Intent(this, ShowUsersActivity::class.java)
            intent.putExtra(USER_ID,userId)
            intent.putExtra(STORY_ID,storyIdsList[counter])
            intent.putExtra(TITLE, TITLE_VIEW)
            startActivity(intent)
        }

        binding.storyDelete.setOnClickListener {
           val ref= FirebaseFirestore.getInstance().collection(STORY_REF).document(STORIES_USERS_LIST)
                .collection(userId).document(storyIdsList[counter])
            ref.delete().addOnCompleteListener { task->
                if (task.isSuccessful){
                    Toast.makeText(this,"Delete story ....",Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    private fun getStories(userId: String) {
        imageList.clear()
        storyIdsList.clear()
        FirebaseFirestore.getInstance().collection(STORY_REF).document(STORIES_USERS_LIST)
            .collection(userId).addSnapshotListener { value, error ->
                if (value != null) {
                    for (doc in value.documents) {
                        val story = util.covertToStory(doc)
                        val timeCurrent = System.currentTimeMillis()
                        if (timeCurrent > story.timeStart && timeCurrent < story.timeEnd) {
                            imageList.add(story.imageUrl)
                            storyIdsList.add(story.storyId)
                        }
                    }
                    storiesProgressView!!.setStoriesCount(imageList.size)
                    storiesProgressView!!.setStoryDuration(6000L)
                    storiesProgressView!!.setStoriesListener(this@StoryActivity)
                    storiesProgressView!!.startStories(counter)
                }
            }
    }

    private fun addViewtoStory(storyId: String) {
        val data = HashMap<String, Any>()
        data[STORY_VIEW_ID] = currentUserId.toString()
        val ref = FirebaseFirestore.getInstance().collection(STORY_REF).document(STORIES_USERS_LIST)
            .collection(userId).document(storyId).collection(STORY_VIEWS).add(data)
    }

    private fun seenNumber(storyId: String) {
        val ref = FirebaseFirestore.getInstance().collection(STORY_REF).document(STORIES_USERS_LIST)
            .collection(userId).document(storyId).collection(STORY_VIEWS)
        ref.addSnapshotListener { value, error ->
            if (value != null) {
                binding.seenNumber.text = value.documents.size.toString()
            }
        }
    }

    private fun userInfo(userId: String) {
        FirebaseFirestore.getInstance().collection(USER_REF).document(userId).get()
            .addOnSuccessListener {
                val user = util.convertToUser(it)
                Picasso.get().load(user.profileImage).placeholder(R.drawable.profile)
                    .into(binding.storyProfileImage)
                binding.storyUsername.text = user.userName
            }
    }

    override fun onNext() {
        Picasso.get().load(imageList[++counter]).placeholder(R.drawable.profile)
            .into(binding.imageStory)
        addViewtoStory(storyIdsList[counter])
        seenNumber(storyIdsList[counter])
    }

    override fun onPrev() {
        Picasso.get().load(imageList[--counter]).placeholder(R.drawable.profile)
            .into(binding.imageStory)
        seenNumber(storyIdsList[counter])
    }

    override fun onComplete() {
        finish()
    }

    override fun onDestroy() {
        super.onDestroy()
        storiesProgressView?.destroy()
    }

    override fun onResume() {
        super.onResume()
        storiesProgressView?.resume()
    }

    override fun onPause() {
        super.onPause()
        storiesProgressView?.pause()
    }
}
