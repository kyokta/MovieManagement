package com.example.movies.LoginRegister

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.movies.Admin.MainAdmin
import com.example.movies.Database.Firebase.Users
import com.example.movies.Database.User.UserDao
import com.example.movies.Database.User.UserRoomDatabase
import com.example.movies.MainActivity
import com.example.movies.databinding.FragmentLoginBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

class LoginFragment : Fragment() {
    private lateinit var binding: FragmentLoginBinding
//    firebase
    private var auth = FirebaseAuth.getInstance()
    private var firestore = FirebaseFirestore.getInstance()
    private var userCollection = firestore.collection("users")

//    dao
    private lateinit var mUserDao: UserDao
    private lateinit var executorService: ExecutorService

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
        binding = FragmentLoginBinding.inflate(inflater, container, false)
        val view = binding.root

        val sharedPreferences = requireContext().getSharedPreferences("account_data", AppCompatActivity.MODE_PRIVATE)
        val token = sharedPreferences.getString("USER_TOKEN", null)
        val role = sharedPreferences.getString("USER_ROLE", null)

        with(binding) {
            executorService = Executors.newSingleThreadExecutor()
            val db = UserRoomDatabase.getDatabase(requireContext())
            mUserDao = db!!.userDao()!!

            btnLogin.setOnClickListener {
                val email = email.text.toString()
                val password = password.text.toString()

                if (email.isEmpty() || password.isEmpty()) {
                    Toast.makeText(requireContext(), "Silakan masukkan email dan password", Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }
                login(email, password)
            }

            btnLoginAdmin.setOnClickListener {
                login("admin@gmail.com", "11111111")
            }

            btnLoginMember.setOnClickListener {
                login("member@gmail.com", "11111111")
            }

            if (token != null && role != null) {
                when (role) {
                    "Admin" -> forceLogin("Admin")
                    else -> forceLogin("Member")
                }
            }
        }

        return view
    }

    private fun login(email: String, password: String) {
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(requireActivity()) { task ->
                if (task.isSuccessful) {
                    val currentUser = auth.currentUser
                    if (currentUser != null) {
                        getUser(currentUser.uid)
                    }
                } else {
                    Log.w("LoginFragment", "Gagal login. Periksa kembali email dan password Anda", task.exception)
                    Toast.makeText(
                        requireContext(), "Akun tidak ditemukan. Silakan periksa kembali email dan password Anda",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
    }

    private fun getUser(userId: String) {
        userCollection.document(userId).get()
            .addOnSuccessListener { doc ->
                if (doc.exists()) {
                    val user = doc.toObject(Users::class.java)

                    if (user != null) {
                        val sharedPreferences = requireActivity().getSharedPreferences("account_data",
                            AppCompatActivity.MODE_PRIVATE
                        )
                        with(sharedPreferences.edit()) {
                            putString("USER_TOKEN", user.token)
                            putString("USER_ROLE", user.role)
                            putString("USER_ID", userId)
                            putString("USER_NAMA", user.nama)
                            commit()
                        }

                        if (user.role == "Admin") {
                            val intent = Intent(requireContext(), MainAdmin::class.java)
                            startActivity(intent)
                            requireActivity().finish()
                        } else {
                            val intent = Intent(requireContext(), MainActivity::class.java)
                            startActivity(intent)
                            requireActivity().finish()
                        }
                    }
                }
            }
    }

    private fun forceLogin(role: String) {
        var intent: Intent = when (role) {
            "Admin" -> Intent(requireContext(), MainAdmin::class.java)
            else -> Intent(requireContext(), MainActivity::class.java)
        }

        startActivity(intent)
        requireActivity().finish()
    }
}