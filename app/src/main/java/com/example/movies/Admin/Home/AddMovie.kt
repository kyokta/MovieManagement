package com.example.movies.Admin.Home

import android.app.DatePickerDialog
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import com.bumptech.glide.Glide
import com.example.movies.Database.Firebase.Movies
import com.example.movies.Database.Movie.Movie
import com.example.movies.Database.Movie.MovieDao
import com.example.movies.Database.Movie.MovieRoomDatabase
import com.example.movies.databinding.ActivityAddMovieBinding
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import java.util.Calendar
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class AddMovie : AppCompatActivity() {
    private lateinit var binding: ActivityAddMovieBinding
    // firestore
    private val firestore = FirebaseFirestore.getInstance()
    private val MoviesCollectionRef = firestore.collection("movies")
    private val storageRef = FirebaseStorage.getInstance().reference

    // dao
    private lateinit var mMovieDao: MovieDao
    private lateinit var executorService: ExecutorService
    private var updatedId: String = ""
    private var oldImage: String = ""

    private val PICK_IMAGE_REQUEST = 1
    private lateinit var selectedImageUri: Uri

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        executorService = Executors.newSingleThreadExecutor()
        val db = MovieRoomDatabase.getDatabase(this)
        mMovieDao = db!!.movieDao()!!

        binding = ActivityAddMovieBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val movieId = intent.getStringExtra("MOVIE_ID")

        with(binding) {
            var placeMovie = ""

            if (movieId != null) {
                titlePage.text = "EDIT MOVIE"
                btnTambah.text = "Simpan"
                showData(movieId)

                btnTambah.setOnClickListener {
                    val updatedMov = Movies(
                        title = judulFilm.text.toString(),
                        description = deskirpsiFilm.text.toString(),
                        place = placeMovie,
                        date = tanggalFilm.text.toString()
                    )
                    updateData(updatedMov)
                }
            } else {
                titlePage.text = "TAMBAH MOVIE"
                btnTambah.text = "Tambah"
                btnTambah.setOnClickListener{
                    val newMov = Movies(
                        title = judulFilm.text.toString(),
                        description = deskirpsiFilm.text.toString(),
                        place = placeMovie,
                        date = tanggalFilm.text.toString())
                    addMovie(newMov)
                    finish()
                }
            }

            btnUploadImg.setOnClickListener {
                getPhoto()
            }

            val arrayString = arrayOf("Pilih bioskop", "Ambarukmo Plaza", "Jogja City Mall", "Sleman City Hall", "Empire XXI")
            val adapter = ArrayAdapter(this@AddMovie, android.R.layout.simple_spinner_item, arrayString)
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            tempatFilm.adapter = adapter

            tempatFilm.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long) {
                    if (position > 0) {
                        placeMovie = parent.getItemAtPosition(position).toString()
                    }
                }

                override fun onNothingSelected(parent: AdapterView<*>) {
                    placeMovie = ""
                }
            }

            btnTanggal.setOnClickListener {
                val c = Calendar.getInstance()
                val year = c.get(Calendar.YEAR)
                val month = c.get(Calendar.MONTH)
                val day = c.get(Calendar.DAY_OF_MONTH)

                val dpd = DatePickerDialog(this@AddMovie, DatePickerDialog.OnDateSetListener { _, year, monthOfYear, dayOfMonth ->
                    val formattedDate = String.format("%02d/%02d/%04d", dayOfMonth, monthOfYear + 1, year)
                    tanggalFilm.text = formattedDate
                }, year, month, day)

                dpd.show()
            }


            btnBack.setOnClickListener{
                finish()
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null) {
            val selectedImage = data.data
            if (selectedImage != null) {
                selectedImageUri = selectedImage
            }

            binding.prevImage.setImageURI(selectedImage)
        }
    }

    private fun getPhoto() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        startActivityForResult(intent, PICK_IMAGE_REQUEST)
    }

    private fun showData(id: String) {
        MoviesCollectionRef.document(id).get()
            .addOnSuccessListener { doc ->
                if(doc.exists()) {
                    val movies = doc.toObject(Movies::class.java)

                    movies.let {
                        if (it != null) {
                            updatedId = it.id
                            binding.judulFilm.setText(it.title)
                            binding.tanggalFilm.setText(it.date)
                            binding.deskirpsiFilm.setText(it.description)
                            val placeIndex = getPlaceIndex(it.place)
                            binding.tempatFilm.setSelection(placeIndex)
                            if (it.image.isNotEmpty()) {
                                selectedImageUri = Uri.parse(it.image)
                                Glide.with(this)
                                    .load(it.image)
                                    .into(binding.prevImage)
                                oldImage = it.image
                            }
                        }
                    }
                }
            }
    }

    private fun getImageUri(callback: (String) -> Unit) {
        if (::selectedImageUri.isInitialized) {
            val imageRef = storageRef.child("images/${System.currentTimeMillis()}.jpg")

            imageRef.putFile(selectedImageUri)
                .addOnSuccessListener { taskSnapshot ->
                    imageRef.downloadUrl.addOnSuccessListener { uri ->
                        val imageUrl = uri.toString()
                        callback(imageUrl)
                    }
                }
                .addOnFailureListener {
                    Log.e("AddMovie", "Error uploading image", it)
                    callback("")
                }
        } else {
            callback("")
        }
    }


    private fun updateData(movie: Movies) {
        getImageUri { image ->
            if (image.isNotEmpty()) {
                movie.id = updatedId
                movie.image = image

                MoviesCollectionRef.document(updatedId).set(movie)
                    .addOnSuccessListener {
                        if(oldImage.isNotEmpty()) {
                            delOldImage(oldImage)
                        }
                        updatedId = ""
                        finish()
                    }
                    .addOnFailureListener {
                        Log.e("UpdateMovie", "Error updating movie", it)
                    }
            } else {
                movie.id = updatedId

                MoviesCollectionRef.document(updatedId).set(movie)
                    .addOnSuccessListener {
                        if(oldImage.isNotEmpty()) {
                            delOldImage(oldImage)
                        }
                        updatedId = ""
                        finish()
                    }
                    .addOnFailureListener {
                        Log.e("UpdateMovie", "Error updating movie", it)
                    }
            }
        }
    }

    private fun delOldImage(link: String) {
        val oldImageRef = FirebaseStorage.getInstance().getReferenceFromUrl(link)

        oldImageRef.delete()
            .addOnSuccessListener {
                Log.d("DeleteImage", "Old image deleted successfully")
            }
            .addOnFailureListener {
                Log.e("DeleteImage", "Error deleting old image", it)
            }
    }

    private fun getPlaceIndex(place: String?): Int {
        val arrayString = arrayOf("Pilih bioskop", "Ambarukmo Plaza", "Jogja City Mall", "Sleman City Hall", "Empire XXI")
        return arrayString.indexOf(place)
    }

    private fun addMovie(movie: Movies) {
        if (::selectedImageUri.isInitialized) {
            val imageRef = storageRef.child("images/${System.currentTimeMillis()}.jpg")

            imageRef.putFile(selectedImageUri)
                .addOnSuccessListener { taskSnapshot ->
                    imageRef.downloadUrl.addOnSuccessListener { uri ->
                        val imageUrl = uri.toString()

                        movie.image = imageUrl

                        MoviesCollectionRef.add(movie)
                            .addOnSuccessListener { docRef ->
                                val movieId = docRef.id
                                movie.id = movieId
                                docRef.set(movie)
                                    .addOnSuccessListener {
                                        val newMovie = Movie(
                                            movieId = movieId,
                                            title = movie.title,
                                            image = "image",
                                            description = movie.description,
                                            place = movie.place,
                                            date = movie.date
                                        )
                                        addToDao(newMovie)
                                    }
                                    .addOnFailureListener {
                                        Log.e("AddMovie", "Error adding data")
                                    }
                            }
                            .addOnFailureListener {
                                Log.e("AddMovie", "Error adding data", it)
                            }
                    }
                }
                .addOnFailureListener {
                    Log.e("AddMovie", "Error uploading image", it)
                }
        } else {
            MoviesCollectionRef.add(movie)
                .addOnSuccessListener { docRef ->
                    val movieId = docRef.id
                    movie.id = movieId
                    docRef.set(movie)
                        .addOnFailureListener {
                            Log.e("AddMovie", "Error adding data")
                        }
                }
        }
    }

    private fun addToDao(movie: Movie){
        executorService.execute { mMovieDao.insert(movie) }
    }
}