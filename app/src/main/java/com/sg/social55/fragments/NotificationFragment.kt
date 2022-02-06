package com.sg.social55.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.sg.social55.R
import com.sg.social55.adapters.NotificationAdapter
import com.sg.social55.model.Notification
import com.sg.social55.uilities.NOTIFICATION_LIST
import com.sg.social55.uilities.NOTIFICATION_REF
import com.sg.social55.uilities.Utility
import kotlinx.android.synthetic.main.fragment_notification.view.*
import java.util.*
import kotlin.collections.ArrayList


class NotificationFragment : Fragment() {

    val util = Utility()
    var notifications = ArrayList<Notification>()
    lateinit var adapter: NotificationAdapter
    val currentUserUid = FirebaseAuth.getInstance().currentUser?.uid.toString()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val view = inflater.inflate(R.layout.fragment_notification, container, false)
        var recyclerView = view.recycler_view_notifications
        var manager = LinearLayoutManager(context)
        recyclerView.layoutManager = manager
        adapter = NotificationAdapter(notifications)
        recyclerView.adapter = adapter
        getAllNoitication()
        adapter.notifyDataSetChanged()
        return view
    }

    private fun getAllNoitication() {
        notifications.clear()
        FirebaseFirestore.getInstance()
            .collection(NOTIFICATION_REF).document(currentUserUid).collection(NOTIFICATION_LIST)
            .get()          //list of posts that currentUserId creat
            .addOnCompleteListener { task ->
               //util.logi("NotifiicationFragment||  NOTIFICATION_REF======>$NOTIFICATION_REF,        currentUserUid====>$currentUserUid")
                if (task.isSuccessful) {
                    for (document in task.result!!) {
                        val noti = util.convertToNotification(document)
                        util.logi("Notif. Fragment || noti=$noti")
                        notifications.add(noti)
                    }
                    adapter.notifyDataSetChanged()
                }
            }
    }
}



