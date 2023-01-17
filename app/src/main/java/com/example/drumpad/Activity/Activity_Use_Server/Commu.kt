package com.example.drumpad.Activity_Use_Server

import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.android.volley.RequestQueue
import com.example.drumpad.Activity.Accueil
import com.example.drumpad.R
import kotlinx.android.synthetic.main.activity_commu.*


class Commu : AppCompatActivity() {

    val serverAPIURL: String = "http://lahoucine-hamsek.fr/Drumpad.php"
    lateinit var sharedPreferences: SharedPreferences
    var volleyRequestQueue: RequestQueue? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_commu)


        val fragement = Frag_Server_Musique()
        val fragement2 = Frag_Profil()

        supportFragmentManager.beginTransaction().apply {
            replace(R.id.fragment, fragement)
            commit()}

        button.setOnClickListener {
            supportFragmentManager.beginTransaction().apply {
                replace(R.id.fragment, fragement)
                commit()}
        }

        button2.setOnClickListener {
            supportFragmentManager.beginTransaction().apply {
                    replace(R.id.fragment, fragement2)
                    commit()}
        }
    }

    override fun onStart() {
        super.onStart()
    }

    override fun onBackPressed() {// bouton retour d'android
        super.onBackPressed()
        val intent = Intent(this, Accueil::class.java)
        startActivity(intent)
    }


}