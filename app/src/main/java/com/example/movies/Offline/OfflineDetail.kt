package com.example.movies.Offline

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.example.movies.Database.Movie.MovieDao
import com.example.movies.Database.Movie.MovieRoomDatabase
import com.example.movies.databinding.ActivityOfflineDetailBinding
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class OfflineDetail : AppCompatActivity() {
    private lateinit var binding: ActivityOfflineDetailBinding
    private lateinit var mMovieDao: MovieDao
    private lateinit var executorService: ExecutorService

    override fun onCreate(savedInstanceState: Bundle?) {
        executorService = Executors.newSingleThreadExecutor()
        val db = MovieRoomDatabase.getDatabase(this)
        mMovieDao = db!!.movieDao()!!

        super.onCreate(savedInstanceState)
        binding = ActivityOfflineDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val movieId = intent.getStringExtra("MOVIE_ID")

        with(binding) {
            if (movieId != null) {
                getData(movieId)
            } else {
                Toast.makeText(this@OfflineDetail, "Data film tidak ditemukan.", Toast.LENGTH_SHORT).show()
                finish()
            }

            btnBack.setOnClickListener {
                finish()
            }
        }
    }

    private fun getData(movieId: String) {
        val id = movieId.toInt()

        mMovieDao.getMovieById(id).observe(this) { movie ->
            binding.titleMovies.text = movie?.title
            binding.placeMovies.text = movie?.place
            binding.dateMovies.text = movie?.date
            binding.descriptionMovie.text = movie?.description
        }
    }
}