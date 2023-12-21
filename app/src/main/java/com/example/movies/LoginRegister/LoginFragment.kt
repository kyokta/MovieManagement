package com.example.movies.LoginRegister

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationCompat
import com.example.movies.Admin.MainAdmin
import com.example.movies.Database.Firebase.Users
import com.example.movies.MainActivity
import com.example.movies.Offline.OfflineMovie
import com.example.movies.R
import com.example.movies.databinding.FragmentLoginBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

class LoginFragment : Fragment() {
    private lateinit var binding: FragmentLoginBinding
//    firebase
    private var auth = FirebaseAuth.getInstance()
    private var firestore = FirebaseFirestore.getInstance()
    private var userCollection = firestore.collection("users")

    private val channelId = "MOVIE_NOTIFICATION"
    private val notifId = 90

    private var param1: String? = null
    private var param2: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentLoginBinding.inflate(inflater, container, false)
        val view = binding.root

        val notifManager = requireContext().getSystemService(Context.NOTIFICATION_SERVICE) as
                NotificationManager

        val sharedPreferences = requireContext().getSharedPreferences("account_data", AppCompatActivity.MODE_PRIVATE)
        val token = sharedPreferences.getString("USER_TOKEN", null)
        val role = sharedPreferences.getString("USER_ROLE", null)

        with(binding) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                PendingIntent.FLAG_IMMUTABLE
            } else {
                0
            }

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                val notifChannel = NotificationChannel(
                    channelId, "Notification Movie",
                    NotificationManager.IMPORTANCE_DEFAULT
                )
                notifManager.createNotificationChannel(notifChannel)
            }

            btnLogin.setOnClickListener {
                val email = email.text.toString()
                val password = password.text.toString()

                if (email.isEmpty() || password.isEmpty()) {
                    Toast.makeText(requireContext(), "Silakan masukkan email dan password", Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }

                if (context?.let { isNetworkAvailable(it) } == false) {
                    val builder = NotificationCompat.Builder(requireContext(), channelId)
                        .setSmallIcon(R.drawable.baseline_notifications_active_24)
                        .setContentTitle("Movie")
                        .setContentText("Anda sedang tidak terhubung dengan jaringan internet")
                        .setAutoCancel(true)
                        .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                    notifManager.notify(notifId, builder.build())

                    val offline = Intent(requireContext(), OfflineMovie::class.java)
                    startActivity(offline)
                    requireActivity().finish()
                } else {
                    login(email, password)
                }
            }

            btnLoginAdmin.setOnClickListener {
                login("admin@gmail.com", "11111111")
            }

            btnLoginMember.setOnClickListener {
                login("member@gmail.com", "11111111")
            }

            if (token != null && role != null) {
                when (role) {
                    "Admin" -> navigateToApp("Admin")
                    else -> navigateToApp("Member")
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
                        shareToPreferences(user, userId)
                        navigateToApp(user.role)
                    }
                }
            }
    }

    private fun shareToPreferences(user: Users, userId: String) {
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
    }

    private fun navigateToApp(role: String) {
        var intent: Intent = when (role) {
            "Admin" -> Intent(requireContext(), MainAdmin::class.java)
            else -> Intent(requireContext(), MainActivity::class.java)
        }

        startActivity(intent)
        requireActivity().finish()
    }

    @RequiresApi(Build.VERSION_CODES.M)
    private fun isNetworkAvailable(context: Context): Boolean {
        val connectivityManager =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

        val network = connectivityManager.activeNetwork
        val capabilities = connectivityManager.getNetworkCapabilities(network)

        return capabilities != null && (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)
                || capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR)
                || capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET))
    }
}