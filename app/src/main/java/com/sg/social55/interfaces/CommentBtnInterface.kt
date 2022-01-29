package com.sg.social55.interfaces

import android.widget.ImageView
import android.widget.TextView
import com.sg.social55.model.Post

interface CommentBtnInterface {
    fun commentPost(post: Post, commentImage: ImageView, commentCounter: TextView)
}