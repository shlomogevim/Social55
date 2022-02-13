package com.sg.social55.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.sg.social55.R
import com.sg.social55.adapters.PostAdapter
import com.sg.social55.adapters.StoryAdapter
import com.sg.social55.interfaces.LikeBtnInterface
import com.sg.social55.model.Post
import com.sg.social55.model.Story
import com.sg.social55.uilities.*
import kotlinx.android.synthetic.main.fragment_home.view.*


class HomeFragment : Fragment(), LikeBtnInterface {

    private lateinit var postAdapter: PostAdapter
    private lateinit var storyAtapter: StoryAdapter

    private var postList = ArrayList<Post>()
    private var storyList = ArrayList<Story>()

    private var folloingList = ArrayList<String>()

    private var util = Utility()
    val currentUserUid = FirebaseAuth.getInstance().currentUser?.uid.toString()
    val currentUserName = FirebaseAuth.getInstance().currentUser?.displayName.toString()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        var view = inflater.inflate(R.layout.fragment_home, container, false)

        view.currentUserTV.text = "Current: " + currentUserName


        var recyclerViewStory: RecyclerView? = null
        recyclerViewStory = view.findViewById(R.id.recycler_view_story)
        val linearLayoutManagerStory = LinearLayoutManager(context)
        linearLayoutManagerStory.reverseLayout = true
        linearLayoutManagerStory.stackFromEnd = true
        recyclerViewStory.layoutManager = linearLayoutManagerStory
        storyAtapter = StoryAdapter(storyList)
        recyclerViewStory.adapter = storyAtapter

        var recyclerView: RecyclerView? = null
        recyclerView = view.findViewById(R.id.recycler_view_home)
        val linearLayoutManager = LinearLayoutManager(context)
        linearLayoutManager.reverseLayout = true
        linearLayoutManager.stackFromEnd = true
        recyclerView.layoutManager = linearLayoutManager
        postAdapter = PostAdapter(postList, this)
        recyclerView.adapter = postAdapter


        checkFollowings()

        return view
    }

    private fun checkFollowings() {
        folloingList.clear()
        FirebaseFirestore.getInstance().collection(FOLLOW_REF).document(currentUserName)
            .collection(FOLLOWING_REF)
            .addSnapshotListener { value, error ->
                if (value != null) {
                    for (document in value.documents) {
                        folloingList.add(document.id)
                        // util.logi("Homefragment11|| \n document.id=${document.id}")
                    }
                    folloingList.add(currentUserName)
                    retrievePost()
                    retrieveStories()
                }
            }
    }

    private fun retrieveStories() {
        storyList.clear()
        val story = Story("", 0, 0, "", currentUserUid)
        storyList.add(story)
        val timeCurrent = System.currentTimeMillis()
        for (id in folloingList) {
        FirebaseFirestore.getInstance().collection(STORY_REF).document(STORIES_USERS_LIST)
            .collection(id).addSnapshotListener { value, error ->
//   util.logi("Homefragment12|| \n  timeCurrent=${ timeCurrent},  story=${story}\n")
//    util.logi("Homefragment12|| \n  id=${ id}")
                 //   var countStory = 0
                    if (value != null) {
                        // util.logi("Homefragment14|| \n value=${value}, value.documents=${value.documents},  story=${story}")
                        for (document in value.documents) {
                            var story = util.covertToStory(document)
                            if (timeCurrent > story.timeStart && timeCurrent < story.timeEnd) {
                                storyList.add(story)
                            }
                        }
                    }
                        //  util.logi("Homefragment16|| \n storyList=${storyList}")
                storyAtapter.notifyDataSetChanged()
                }
            }
    }

    private fun retrievePost() {
        postList.clear()
        FirebaseFirestore.getInstance().collection(POSTS_REF)
            .addSnapshotListener { value, error ->
                if (value != null) {
                    for (document in value.documents) {
                        val post = util.covertYoPost(document)
                        val publisher = post.publisher
                        if (publisher in folloingList) {  //see posts only if the belong to his foloowingList
                            postList.add(post)
                        }
                    }
                    postAdapter.notifyDataSetChanged()
                }
            }
    }

    override fun likePost(post: Post, likeBtn: ImageView, likeConter: TextView) {}
}


/*override fun likePost(post: Post, likeBtn: ImageView, likeConter: TextView) {
    *//*  val data = util.simpleData()
          val ref = FirebaseFirestore.getInstance().collection(LIKES_REF_NEW).document(post.postId)
              .collection(USERS_ID).document(currentUserUid)
              .collection(SIMPLE_ITEM)
          ref.document(BOL).get()
              .addOnSuccessListener {
                  if (it.exists()) {
                      likeBtn.setImageResource(R.drawable.heart_not_clicked)
                      it.reference.delete()
                  } else {
                      likeBtn.setImageResource(R.drawable.heart_clicked)
                      ref.document(BOL).set(data)
                  }
              }*//*
    }*/

/* override fun likePost(post: Post, likeBtn: ImageView,likeConter:TextView) {
      val data = HashMap<String, Any>()
      if (currentUser != null) {
          data["bol"] = currentUser.displayName.toString()
          val userRef = FirebaseFirestore.getInstance().collection(LIKES_REF)
              .document(post.postId).collection(currentUser.uid.toString()).document(SIMPLE_POST)
          userRef.get().addOnSuccessListener {
              if (it.exists()) {
                //  Log.d("gg", "exsist   ")
                  likeBtn.setImageResource(R.drawable.heart_not_clicked)
                  it.reference.delete()

              } else {
                 // Log.d("gg", "not exsist")
                  likeBtn.setImageResource(R.drawable.heart_clicked)
                  userRef.set(data)
              }
          }
      }
      operateLikeCounter(post,likeConter)
  }*/
/*  val data = HashMap<String, Any>()
  if (currentUser != null) {
      data["bol"] = currentUser.displayName.toString()
      val userRef = FirebaseFirestore.getInstance().collection(LIKES_REF)
          .document(post.postId).collection(currentUser.uid.toString()).document(SIMPLE_POST)
      userRef.get().addOnSuccessListener {
          if (it.exists()) {
              //  Log.d("gg", "exsist   ")
              likeBtn.setImageResource(R.drawable.heart_not_clicked)
              it.reference.delete()

          } else {
              // Log.d("gg", "not exsist")
              likeBtn.setImageResource(R.drawable.heart_clicked)
              userRef.set(data)
          }
      }
  }
  operateLikeCounter(post,likeConter)*/

/*private fun operateLikeCounter(post: Post, likeConter: TextView) {
    var counter = 0
    FirebaseFirestore.getInstance().collection(USER_REF).get()
        .addOnSuccessListener {
            for (doc in it.documents) {
                val currentId = doc.id.toString()

                FirebaseFirestore.getInstance().collection(LIKES_REF).document(post.postId)
                    .collection(currentId).document(SIMPLE_POST).get()
                    .addOnSuccessListener {
                        // Log.d("gg","currentId=$currentId, post.postId=${post.postId}")
                        // Log.d("gg","it.exists()==>${it.exists()}")
                        if (it.exists()) {
                            counter++
                            likeConter.text = "$counter Likes"
                            // Log.d("gg","counter inside= $counter")
                        } else {
                            likeConter.text = "$counter Likes"
                        }
                    }
            }
        }
}*/


/* private fun removeLike(post: Post) {
       val likeRef = FirebaseFirestore.getInstance().collection(LIKES_REF).document(post.postId)
       val currentUser = FirebaseAuth.getInstance().currentUser
       if (currentUser != null) {
           // Log.d("gg", " 2.Inside remove")
           likeRef.collection(currentUser.uid).document("bobo").delete()
       }

       var count = post.likeNumber.toInt()
       count--
       if (count < 0) {
           count = 0
       }
       FirebaseFirestore.getInstance().collection(POSTS_REF).document(post.postId)
           .update(POST_LIKECOUNTER, count.toString())
   }

   private fun addLike(post: Post) {
       val likeRef = FirebaseFirestore.getInstance().collection(LIKES_REF)
       val currentUser = FirebaseAuth.getInstance().currentUser
       val data = HashMap<String, Any>()
       data["bol"] = currentUser?.displayName.toString()
       if (currentUser != null) {
           // Log.d("gg", " 3.Inside add")
           likeRef.document(post.postId).collection(currentUser.uid)
               .document("bobo").set(data)
       }
       var count = post.likeNumber.toInt()
       count++
       FirebaseFirestore.getInstance().collection(POSTS_REF).document(post.postId)
           .update(POST_LIKECOUNTER, count.toString())
   }*/
