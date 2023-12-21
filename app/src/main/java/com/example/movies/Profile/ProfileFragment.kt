package com.example.movies.Profile

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import com.example.movies.Database.Firebase.Users
import com.example.movies.LoginRegister.LoginRegister
import com.example.movies.databinding.FragmentProfileBinding
import com.google.firebase.firestore.FirebaseFirestore

private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

class ProfileFragment : Fragment() {
    private lateinit var binding: FragmentProfileBinding
//    firebase
    private var firestore = FirebaseFirestore.getInstance()
    private var userCollection = firestore.collection("users")

    private var param1: String? = null
    private var param2: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentProfileBinding.inflate(inflater, container, false)
        var view = binding.root

        val sharedPref = activity?.getSharedPreferences("account_data", Context.MODE_PRIVATE)
        val userId = sharedPref?.getString("USER_ID", null)

        if (userId != null) {
            getUser(userId)
        }

        binding.btnLogout.setOnClickListener {
            val sharedPreferences = activity?.getSharedPreferences("account_data", AppCompatActivity.MODE_PRIVATE)
            with(sharedPreferences!!.edit()) {
                putString("USER_TOKEN", null)
                putString("USER_ROLE", null)
                putString("USER_NAMA", null)
                putString("USER_ID", null)
                commit()
            }

            val intent = Intent(requireContext(), LoginRegister::class.java)
            startActivity(intent)
            requireActivity().finish()
        }

        return view
    }

    private fun getUser(userId: String) {
        userCollection.document(userId).get()
            .addOnSuccessListener { doc ->
                if (doc.exists()) {
                    val user = doc.toObject(Users::class.java)

                    if (user != null) {
                        binding.txtName.text = user.nama
                        binding.txtUsername.text = user.username
                        binding.txtEmail.text = user.email
                        binding.txtRole.text = user.role
                    }
                }
            }
    }
}