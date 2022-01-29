package com.sg.social55.interfaces

import android.widget.ImageView
import android.widget.TextView
import com.sg.social55.model.Post

interface LikeBtnInterface {
    fun likePost(post:Post,likeImage:ImageView,likeCounter:TextView)
}