package com.sg.social55.fragments

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.ktx.Firebase
import com.sg.social55.R
import com.sg.social55.adapters.UserAdapter
import com.sg.social55.databinding.FragmentSearchBinding
import com.sg.social55.model.User
import com.sg.social55.uilities.*
import java.util.*
import kotlin.collections.ArrayList

class SearchFragment : Fragment() {

    private lateinit var binding: FragmentSearchBinding

   private lateinit var userAdapter: UserAdapter
    private var users= arrayListOf<User>()



    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentSearchBinding.inflate(inflater, container, false)

        val recyclerView = binding.recyclerViewSearch
        recyclerView.setHasFixedSize(true)
        recyclerView.layoutManager = LinearLayoutManager(context)

        userAdapter = UserAdapter(users as ArrayList<User>, true)
        recyclerView.adapter = userAdapter

        retrieveUsers()

        binding.searchEditText.addTextChangedListener(object : TextWatcher {
            override fun onTextChanged(s: CharSequence?, p1: Int, p2: Int, p3: Int) {
                if (binding.searchEditText.text.toString() != "") {
                    recyclerView.visibility = View.VISIBLE
                    searchUser(s.toString().lowercase(Locale.getDefault()))
                }
            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
            override fun afterTextChanged(p0: Editable?) {}
        })
        return binding.root
    }

    private fun searchUser(input: String) {
        FirebaseFirestore.getInstance().collection(USER_REF)
           .orderBy(USER_FULLNAME)
            .startAt(input)
            .endAt(input+"\uf8ff")
            .addSnapshotListener { snapshot, exception->
                if (exception != null) {
                    Toast.makeText(context, "Error in downloadind users", Toast.LENGTH_LONG).show()
                }else{
                    if (snapshot!=null){
                        parseData(snapshot)
                    }
                }
            }
    }

    private fun retrieveUsers() {
        FirebaseFirestore.getInstance().collection(USER_REF)
           // .orderBy(USER_FULLNAME, Query.Direction.ASCENDING)
            .addSnapshotListener { snapshot, exception ->
                if (exception != null) {
                    Toast.makeText(context, "Error in downloadind users", Toast.LENGTH_LONG).show()
                }else{
                    if (snapshot!=null){
                        parseData(snapshot)
                    }
                }
            }
    }

    private fun parseData(snapshot: QuerySnapshot) {
        var name=""
        var fullName=""
        var email=""
        var image=""
        var bio=""
        var uid=""
        val auth=FirebaseAuth.getInstance().currentUser?.displayName
        users?.clear()
        for (document in snapshot.documents){
            name=document[USER_USERNAME] as String
            fullName=document[USER_FULLNAME] as String
            email=document[USER_EMAIL] as String
            image=document[USER_IMAGE] as String
            bio=document[USER_BIO] as String
            uid=document[USER_UID] as String

         //   if (name!=auth) {
                val newUser = User(name, fullName, email, image, bio, uid)
                users?.add(newUser)
           // }
        }
        userAdapter.notifyDataSetChanged()
    }


}

