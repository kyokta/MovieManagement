package com.example.movies.LoginRegister

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.core.app.NotificationCompat
import com.example.movies.Database.Firebase.Users
import com.example.movies.R
import com.example.movies.databinding.FragmentRegisterBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

class RegisterFragment : Fragment() {
    private lateinit var binding: FragmentRegisterBinding
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

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentRegisterBinding.inflate(inflater, container, false)
        var view = binding.root

        val notifManager = requireContext().getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

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

        with(binding) {
            val arrayString = arrayOf("Pilih role", "Admin", "Member")
            val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, arrayString)
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            roles.adapter = adapter

            var selectedRole = ""
            roles.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long) {
                    if (position > 0) {
                        selectedRole = parent.getItemAtPosition(position).toString()
                    }
                }

                override fun onNothingSelected(parent: AdapterView<*>) {
                    selectedRole = ""
                }
            }

            btnRegister.setOnClickListener {
                if (selectedRole == "") {
                    Toast.makeText(requireContext(), "Pilih role user", Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }

                val newUser = Users(
                    email = txtEmail.text.toString(),
                    username = txtUsername.text.toString(),
                    nama = txtName.text.toString(),
                    role = selectedRole,
                    password = txtPassword.text.toString()
                )
                register(newUser)
            }
        }

        return view
    }

    private fun register(user: Users) {
        val email = user.email
        val password = user.password

        if (!isValidEmail(email) || !isValidPassword(password)) {
            Toast.makeText(requireContext(), "Email atau password tidak valid", Toast.LENGTH_SHORT).show()
            return
        }

        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val firebaseUser = auth.currentUser
                    val userId = firebaseUser?.uid ?: ""

                    firebaseUser?.getIdToken(true)
                        ?.addOnCompleteListener { tokenTask ->
                            if (tokenTask.isSuccessful) {
                                val idToken = tokenTask.result?.token ?: ""

                                val userWithToken = user.copy(token = idToken)

                                userCollection.document(userId)
                                    .set(userWithToken.toMap())
                                    .addOnSuccessListener {
                                        val notifManager = requireContext().getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

                                        val builder = NotificationCompat.Builder(requireContext(), channelId)
                                            .setSmallIcon(R.drawable.baseline_notifications_active_24)
                                            .setContentTitle("Movie")
                                            .setContentText("Berhasil membuat akun. Silakan untuk melakukan login ulang untuk masuk ke aplikasi")
                                            .setAutoCancel(true)
                                            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                                        notifManager.notify(notifId, builder.build())
                                        Toast.makeText(requireContext(), "Berhasil membuat akun", Toast.LENGTH_SHORT).show()
                                        resetForm()
                                    }
                                    .addOnFailureListener { e ->
                                        Toast.makeText(requireContext(),"Gagal membuat akun", Toast.LENGTH_SHORT).show()
                                        Log.e("RegisterPage", "Error create account : ${e.message}")
                                    }
                            } else {
                                Toast.makeText(requireContext(),"Gagal membuat akun", Toast.LENGTH_SHORT).show()
                                Log.e("RegisterPage", "Error getting token user")
                            }
                        }
                } else {
                    Toast.makeText(requireContext(),"Gagal membuat akun", Toast.LENGTH_SHORT).show()
                    Log.e("RegisterPage","Pendaftaran gagal: ${task.exception?.message}")
                }
            }
    }

    private fun isValidEmail(email: String): Boolean {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }

    private fun isValidPassword(password: String): Boolean {
        return password.length >= 6
    }

    private fun resetForm() {
        binding.txtUsername.setText("")
        binding.txtEmail.setText("")
        binding.txtName.setText("")
        binding.txtPassword.setText("")
        binding.roles.setSelection(0)
    }
}