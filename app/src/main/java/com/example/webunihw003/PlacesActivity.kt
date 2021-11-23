package com.example.webunihw003

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.webunihw003.adapter.PlacesAdapter
import com.example.webunihw003.databinding.ActivityPlacesBinding
import com.example.webunihw003.entity.Place
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.*


class PlacesActivity : AppCompatActivity() {
    lateinit var binding: ActivityPlacesBinding
    private lateinit var placesAdapter: PlacesAdapter

    //változásokat figyeli
    private lateinit var eventListener: EventListener<QuerySnapshot>

    //rámutat a places collectionre
    private lateinit var queryRef: CollectionReference

    //beregisztráltunk egy listenert egy collectionre, kapunk egy ListenerRegistration objektumot
    private var listenerReg: ListenerRegistration? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPlacesBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.fab.setOnClickListener { view ->
            startActivity(Intent(this, CreatePlaceActivity::class.java))
        }

        //adapter + recyclerview
        placesAdapter = PlacesAdapter(
            this,
            FirebaseAuth.getInstance().currentUser!!.uid
        )
        binding.recyclerPlaces.adapter = placesAdapter

        val searchValue = binding.edittextSearch.text

        binding.buttonSearch.setOnClickListener() {
            placesAdapter.clearPlaces()
            searchFirebaseQuery()
            binding.edittextSearch.setText("")
            binding.buttonFulllist.visibility = View.VISIBLE

        }

        binding.buttonFulllist.setOnClickListener(){
            placesAdapter.clearPlaces()
            initFirebaseQuery()
            binding.buttonFulllist.visibility = View.GONE
        }


        initFirebaseQuery()

    }//ONCREATE

    //keresési eredmény lekérése
    private fun searchFirebaseQuery() {

        FirebaseFirestore.getInstance().collection(CreatePlaceActivity.COLLECTION_POSTS)
            .whereEqualTo("city", binding.edittextSearch.text.toString())
            .get()
            .addOnSuccessListener { documents ->
                for (document in documents) {
                    val post = document.toObject(Place::class.java)
                    placesAdapter.addPlace(post, document.id)
                }
            }
            .addOnFailureListener { exception ->
                Toast.makeText(
                    this@PlacesActivity, "Error: ${exception.message}",
                    Toast.LENGTH_LONG
                ).show()
            }

        queryRef = FirebaseFirestore.getInstance().collection(CreatePlaceActivity.COLLECTION_POSTS)

        eventListener = object : EventListener<QuerySnapshot> {
            override fun onEvent(querySnapshot: QuerySnapshot?, e: FirebaseFirestoreException?) {
                if (e != null) {
                    Toast.makeText(
                        this@PlacesActivity, "Error: ${e.message}",
                        Toast.LENGTH_LONG
                    ).show()
                    return
                }
            }
        }

        listenerReg = queryRef.addSnapshotListener(eventListener)

    }

    //teljes lista betöltése
    private fun initFirebaseQuery() {

//        FirebaseFirestore.getInstance().collection(CreatePlaceActivity.COLLECTION_POSTS)
//            .whereEqualTo("city", "Tatabánya")
//            .get()
//            .addOnSuccessListener { documents ->
//                for (document in documents) {
//                    val post = document.toObject(Place::class.java)
//                    placesAdapter.addPlace(post, document.id)
//                }
//            }
//            .addOnFailureListener { exception ->
//                Toast.makeText(
//                    this@PlacesActivity, "Error: ${exception.message}",
//                    Toast.LENGTH_LONG
//                ).show()
//            }

        queryRef = FirebaseFirestore.getInstance().collection(CreatePlaceActivity.COLLECTION_POSTS)

        eventListener = object : EventListener<QuerySnapshot> {
            override fun onEvent(querySnapshot: QuerySnapshot?, e: FirebaseFirestoreException?) {
                if (e != null) {
                    Toast.makeText(
                        this@PlacesActivity, "Error: ${e.message}",
                        Toast.LENGTH_LONG
                    ).show()
                    return
                }

                for (docChange in querySnapshot?.getDocumentChanges()!!) {
                    if (docChange.type == DocumentChange.Type.ADDED) {
                        val place = docChange.document.toObject(Place::class.java)
                        placesAdapter.addPlace(place, docChange.document.id)
                    } else if (docChange.type == DocumentChange.Type.REMOVED) {
                        placesAdapter.removePlaceByKey(docChange.document.id)
                    } else if (docChange.type == DocumentChange.Type.MODIFIED) {

                    }
                }

            }
        }

        listenerReg = queryRef.addSnapshotListener(eventListener)

    }

    override fun onDestroy() {
        super.onDestroy()
        listenerReg?.remove()
    }


}