package com.example.webunihw003

import android.R.string
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.webunihw003.databinding.ActivityMainBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.messaging.FirebaseMessaging
import com.google.firebase.messaging.RemoteMessage
import java.lang.RuntimeException
import java.util.*


class MainActivity : AppCompatActivity() {
    lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.buttonLogin.setOnClickListener(){
            login()
        }

        binding.buttonRegister.setOnClickListener(){
            register()
        }

        //feliratkozás pushra
        FirebaseMessaging.getInstance().subscribeToTopic("placespushes")

    }//ONCREATE

    fun login(){
       // throw RuntimeException("HIBA!!!")
        if (!isFormValid()) {
            return
        }

        FirebaseAuth.getInstance().signInWithEmailAndPassword(
            binding.edittextEmail.text.toString(), binding.edittextPassword.text.toString()
        ).addOnSuccessListener {
            startActivity(Intent(this@MainActivity, PlacesActivity::class.java))
        }.addOnFailureListener{
            Toast.makeText(
                this@MainActivity,
                "Login error: ${it.message}",
                Toast.LENGTH_LONG
            ).show()
        }
    }

    fun register(){
        if (!isFormValid()){
            return
        }

        FirebaseAuth.getInstance().createUserWithEmailAndPassword(
            binding.edittextEmail.text.toString(), binding.edittextPassword.text.toString()
        ).addOnSuccessListener {
            Toast.makeText(
                this@MainActivity,
                "Registration OK",
                Toast.LENGTH_LONG
            ).show()
        }.addOnFailureListener{
            Toast.makeText(
                this@MainActivity,
                "Error: ${it.message}",
                Toast.LENGTH_LONG
            ).show()
        }
    }


    fun isFormValid(): Boolean {
        return when {

            binding.edittextEmail.text.isEmpty() -> {
                binding.edittextEmail.error = "Töltsd ki az email mezőt!"
                false
            }
            binding.edittextPassword.text.isEmpty() -> {
                binding.edittextPassword.error = "Töltsd ki a jelszó mezőt!"
                false
            }
            else -> true
        }

    }



}