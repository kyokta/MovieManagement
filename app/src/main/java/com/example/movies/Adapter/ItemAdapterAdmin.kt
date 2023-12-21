package com.example.movies.Adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.movies.Admin.Home.AddMovie
import com.example.movies.Admin.Home.DetailsAdmin
import com.example.movies.Database.Firebase.Movies
import com.example.movies.R

class ItemAdapterAdmin (
    private val context: Context,
    private val data: List<Movies>
) : RecyclerView.Adapter<ItemAdapterAdmin.ItemViewHolder>() {
    class ItemViewHolder(private val view: View) : RecyclerView.ViewHolder(view) {
        val title: TextView = view.findViewById(R.id.title_movie_admin)
        val date: TextView = view.findViewById(R.id.date_movie_admin)
        val image: ImageView = view.findViewById(R.id.img_movie_admin)
        val detailCard: View = view.findViewById(R.id.detail_movie_admin)
        val editCard: View = view.findViewById(R.id.edit_movie_admin)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        val adapterLayout = LayoutInflater.from(parent.context)
            .inflate(R.layout.list_movie_admin, parent, false)

        return ItemViewHolder(adapterLayout)
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        val item = data[position]

        Glide.with(context)
            .load(item.image)
            .into(holder.image)
        holder.title.text = item.title
        holder.date.text = item.date
        holder.detailCard.setOnClickListener {
            val intent = Intent(context, DetailsAdmin::class.java)
            intent.putExtra("MOVIE_ID", item.id)
            context.startActivity(intent)
        }
        holder.editCard.setOnClickListener{
            val intent = Intent(context, AddMovie::class.java)
            intent.putExtra("MOVIE_ID", item.id)
            context.startActivity(intent)
        }
    }

    override fun getItemCount(): Int {
        return data.size
    }
}