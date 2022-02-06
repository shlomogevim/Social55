package com.sg.social55.fragments

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
import com.sg.social55.adapters.UserAdapter
import com.sg.social55.databinding.FragmentSearchBinding
import com.sg.social55.model.User
import com.sg.social55.uilities.*
import kotlinx.coroutines.launch
import java.util.*
import kotlin.collections.ArrayList

class SearchFragment : Fragment() {

    private lateinit var binding: FragmentSearchBinding
    val util = Utility()

    private lateinit var userAdapter: UserAdapter
    private var users = arrayListOf<User>()

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

    private fun retrieveUsers() {
        FirebaseFirestore.getInstance().collection(USER_REF)
            .addSnapshotListener { value, error ->
                if (value != null) {
                    for (doc in value.documents) {
                        var user = util.convertToUser(doc)
                        users.add(user)
                    }
                }
                util.logi("users11=$users")
                userAdapter.notifyDataSetChanged()
            }
    }

    private fun searchUser(input: String) {
        FirebaseFirestore.getInstance().collection(USER_REF)
            .orderBy(USER_FULLNAME)
            .startAt(input)
            .endAt(input + "\uf8ff")
            .addSnapshotListener { snapshot, exception ->
                if (exception != null) {
                    Toast.makeText(context, "Error in downloadind users", Toast.LENGTH_LONG).show()
                } else {
                    if (snapshot != null) {
                        parseData1(snapshot)
                    }
                }
            }
    }

    private fun parseData1(snapshot: QuerySnapshot) {
        users?.clear()
        for (document in snapshot.documents) {
          val user=util.convertToUser(document)
            users?.add(user)
        }
        userAdapter.notifyDataSetChanged()
    }

}

