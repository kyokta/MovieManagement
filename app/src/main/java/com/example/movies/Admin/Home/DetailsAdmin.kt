package com.example.movies.Admin.Home

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.core.app.NotificationCompat
import com.bumptech.glide.Glide
import com.example.movies.Database.Firebase.Movies
import com.example.movies.Database.Movie.MovieDao
import com.example.movies.Database.Movie.MovieRoomDatabase
import com.example.movies.R
import com.example.movies.databinding.ActivityDetailsAdminBinding
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class DetailsAdmin : AppCompatActivity() {
    private lateinit var binding: ActivityDetailsAdminBinding
//    firebase
    private val firestore = FirebaseFirestore.getInstance()
    private val MoviesCollectionRef = firestore.collection("movies")

//    dao
    private lateinit var mMovieDao: MovieDao
    private lateinit var executorService: ExecutorService

    private val channelId = "MOVIE_NOTIFICATION"
    private val notifId = 90

    override fun onCreate(savedInstanceState: Bundle?) {
        executorService = Executors.newSingleThreadScheduledExecutor()
        val db = MovieRoomDatabase.getDatabase(this)
        mMovieDao = db!!.movieDao()!!

        super.onCreate(savedInstanceState)
        binding = ActivityDetailsAdminBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val notifManager = getSystemService(Context.NOTIFICATION_SERVICE) as
                NotificationManager

        val movieId = intent.getStringExtra("MOVIE_ID")

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

            if (movieId != null) {
                getDetail(movieId)
            } else {
                Toast.makeText(this@DetailsAdmin, "Detail Movie tidak ditemukan", Toast.LENGTH_SHORT).show()
            }

            btnBack.setOnClickListener {
                finish()
            }

            btnTrash.setOnClickListener {
                deleteMovie(movieId)
            }
        }
    }

    private fun getDetail(id: String) {
        MoviesCollectionRef.document(id).get()
            .addOnSuccessListener { doc ->
                if(doc.exists()) {
                    val movies = doc.toObject(Movies::class.java)

                    movies.let {
                        if (it != null) {
                            binding.titleMovies.text = it.title
                            binding.dateMovies.text = it.date
                            binding.descriptionMovie.text = it.description
                            binding.placeMovies.text = it.place

                            if (!it.image.isNullOrEmpty()) {
                                Glide.with(this@DetailsAdmin)
                                    .load(it.image)
                                    .into(binding.imageMovies)
                            }
                        }
                    }
                }
            }
    }

    private fun deleteMovie(movieId: String?) {
        if (movieId != null) {
            MoviesCollectionRef.document(movieId).get()
                .addOnSuccessListener { doc ->
                    if (doc.exists()) {
                        val movie = doc.toObject(Movies::class.java)

                        movie?.let {
                            if (!it.image.isNullOrEmpty()) {
                                val imageRef = FirebaseStorage.getInstance().getReferenceFromUrl(it.image!!)
                                imageRef.delete()
                                    .addOnSuccessListener {
                                        Log.d("DeleteMovie", "Image deleted successfully")
                                    }
                                    .addOnFailureListener {
                                        Log.e("DeleteMovie", "Error deleting image", it)
                                    }
                            }
                        }
                    }

                    MoviesCollectionRef.document(movieId).delete()
                        .addOnSuccessListener {
                            val notifManager = getSystemService(Context.NOTIFICATION_SERVICE) as
                                    NotificationManager

                            val builder = NotificationCompat.Builder(this, channelId)
                                .setSmallIcon(R.drawable.baseline_notifications_active_24)
                                .setContentTitle("Movie")
                                .setContentText("Berhasil menghapus film")
                                .setAutoCancel(true)
                                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                            notifManager.notify(notifId, builder.build())

                            finish()
                        }
                        .addOnFailureListener {
                            Log.e("DeleteMovie", "Error deleting movie: ", it)
                        }
                }
        }
    }

}