package com.example.movies.Admin.Home

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.MutableLiveData
import com.example.movies.Adapter.ItemAdapterAdmin
import com.example.movies.Adapter.ItemAdapterMember
import com.example.movies.Database.Firebase.Movies
import com.example.movies.Database.Movie.MovieDao
import com.example.movies.Database.Movie.MovieRoomDatabase
import com.example.movies.databinding.FragmentHomeAdminBinding
import com.google.firebase.firestore.FirebaseFirestore
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

class HomeAdminFragment : Fragment() {
    private lateinit var binding: FragmentHomeAdminBinding
//    firebase
    private val firebase = FirebaseFirestore.getInstance()
    private val movieCollection = firebase.collection("movies")
    private val moviesListLiveData : MutableLiveData<List<Movies>> by lazy {
        MutableLiveData<List<Movies>>()
    }

//    dao
    private lateinit var mMovieDao: MovieDao
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
        executorService = Executors.newSingleThreadExecutor()
        val db = MovieRoomDatabase.getDatabase(requireContext())
        mMovieDao = db!!.movieDao()!!

        binding = FragmentHomeAdminBinding.inflate(inflater, container, false)
        var view = binding.root

        binding.addMovie.setOnClickListener {
            val intent = Intent(requireContext(), AddMovie::class.java)
            startActivity(intent)
        }

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        observeData()
        getData()
    }

    private fun getData(){
        moviesListLiveData.observe(viewLifecycleOwner) { movies ->
            binding.listData.adapter = ItemAdapterAdmin(requireContext(), movies)
        }
    }

    private fun observeData() {
        movieCollection.addSnapshotListener { value, error ->
            if (error != null) {
                Log.e("HomeAdminFragment", "Error listening for movie: ", error)
                return@addSnapshotListener
            }

            val movies = value?.toObjects(Movies::class.java)
            if (movies != null) {
                moviesListLiveData.postValue(movies)
            }
        }
    }
}