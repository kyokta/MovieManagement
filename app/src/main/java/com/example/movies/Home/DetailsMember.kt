package com.example.movies.Home

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.bumptech.glide.Glide
import com.example.movies.Database.Firebase.Movies
import com.example.movies.databinding.ActivityDetailsMemberBinding
import com.google.firebase.firestore.FirebaseFirestore

class DetailsMember : AppCompatActivity() {
    private lateinit var binding: ActivityDetailsMemberBinding
//    firebase
    private val firestore = FirebaseFirestore.getInstance()
    private val MoviesCollectionRef = firestore.collection("movies")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailsMemberBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val movieId = intent.getStringExtra("MOVIE_ID")

        with(binding) {
            getDetail(movieId)

            btnBack.setOnClickListener { finish() }
        }
    }

    private fun getDetail(movieId: String?) {
        if (movieId != null) {
            MoviesCollectionRef.document(movieId).get()
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
                                    Glide.with(this@DetailsMember)
                                        .load(it.image)
                                        .into(binding.imageMovies)
                                }
                            }
                        }
                    }
                }
        }
    }
}