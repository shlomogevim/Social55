package com.sg.social55.ui.home

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.google.firebase.firestore.FirebaseFirestore
import com.sg.social55.R
import com.sg.social55.databinding.FragmentHomeBinding



class HomeFragment : Fragment() {
    lateinit var binding: FragmentHomeBinding


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentHomeBinding.inflate(inflater, container, false)
        binding.textHome.text = "this ia home"
        binding.btn1.setOnClickListener {
            val data = HashMap<String, Any>()
            data.put("username", "shlomoooo")
            data.put("lastnmme", "gevimoooo")
            FirebaseFirestore.getInstance().collection("JustChecking").add(data)
                .addOnSuccessListener {
                    Log.d("ff", "oooreaaaaa")
                }
                .addOnFailureListener {
                    Log.d("ff", "${it.localizedMessage}")
                }
        }

        return binding.root
    }
}

