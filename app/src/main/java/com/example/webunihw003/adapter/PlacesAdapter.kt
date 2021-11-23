package com.example.webunihw003.adapter

import android.R.attr.data
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.webunihw003.databinding.ItemPlaceBinding
import com.example.webunihw003.entity.Place
import com.google.firebase.firestore.FirebaseFirestore


class PlacesAdapter: RecyclerView.Adapter<PlacesAdapter.ViewHolder> {

    lateinit var context: Context
    var  placesList = mutableListOf<Place>()
    var  placeKey = mutableListOf<String>()

    lateinit var currentUid: String

    constructor(context: Context, uid: String) : super() {
        this.context = context
        this.currentUid = uid
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemPlaceBinding
            .inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return placesList.size
    }


    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        var place = placesList.get(holder.adapterPosition)

        holder.tvAuthor.text = place.author
        holder.tvTitle.text = place.title
        holder.tvCity.text = place.city
        holder.tvStreet.text = place.street
        holder.tvInfo.text = place.info

        if (currentUid == place.uid) {
            holder.btnDelete.visibility = View.VISIBLE
        } else {
            holder.btnDelete.visibility = View.GONE
        }

        holder.btnDelete.setOnClickListener {
            removePlace(holder.adapterPosition)
        }

        if (place.imgUrl.isNotEmpty()){
            holder.ivPhoto.visibility = View.VISIBLE
            Glide.with(context).load(place.imgUrl).into(holder.ivPhoto)
        }
    }


    fun addPlace(place: Place, key: String) {
        placesList.add(place)
        placeKey.add(key)
        //notifyDataSetChanged()
        notifyItemInserted(placesList.lastIndex)
    }

    // when I remove the place object
    private fun removePlace(index: Int) {
        FirebaseFirestore.getInstance().collection("places").document(placeKey[index]).delete()
        placesList.removeAt(index)
        placeKey.removeAt(index)
        notifyItemRemoved(index)
    }

    // when somebody else removes an object
    fun removePlaceByKey(key: String) {
        val index = placeKey.indexOf(key)
        if (index != -1) {
            placesList.removeAt(index)
            placeKey.removeAt(index)
            notifyItemRemoved(index)
        }
    }

    fun clearPlaces() {
//        val size: Int = placesList.size
//        if (size > 0) {
//            for (i in 0 until size) {
//                placesList.removeAt(i)
//            }
//            notifyItemRangeRemoved(0, size)
//        }
        val size: Int = placesList.size
        placesList.clear()
        notifyItemRangeRemoved(0, size)
    }



    inner class ViewHolder(val binding: ItemPlaceBinding) : RecyclerView.ViewHolder(binding.root){
        var tvAuthor = binding.textviewAuthor
        var tvTitle = binding.textviewTitle
        var tvCity = binding.textviewCity
        var tvStreet = binding.textviewStreet
        var tvInfo = binding.textviewInfo
        var btnDelete = binding.buttonDelete
        var ivPhoto = binding.imageviewPhoto
    }
}