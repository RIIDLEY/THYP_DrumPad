package com.example.drumpad.Activity

import android.app.ProgressDialog
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.preference.PreferenceManager
import com.android.volley.AuthFailureError
import com.android.volley.RequestQueue
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.example.drumpad.Activity_Use_Server.Commu
import com.example.drumpad.Activity_Use_Server.Enregistrement
import com.example.drumpad.R
import kotlinx.android.synthetic.main.activity_accueil.*
import java.util.HashMap

class Accueil : AppCompatActivity() {

    lateinit var sharedPreferences: SharedPreferences
    val serverAPIURL: String = "http://lahoucine-hamsek.fr/Drumpad.php"
    var volleyRequestQueue: RequestQueue? = null
    lateinit var progressDialog: ProgressDialog

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_accueil)

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this)

        Pad.setOnClickListener {
            val intent = Intent(this, DrumPad::class.java)
            startActivity(intent)
        }

        crea.setOnClickListener {
            val intent = Intent(this, MesCreations::class.java)
            startActivity(intent)
        }

        about.setOnClickListener {
            val intent = Intent(this, About::class.java)
            startActivity(intent)
        }

        commu.setOnClickListener {
            if (sharedPreferences.getString("Login","")?.isNotEmpty()!!){//Si a deja ete log
                progressDialog = ProgressDialog(this)
                progressDialog.setTitle("Connexion")
                progressDialog.setMessage("En cours de connexion")
                progressDialog.show()
                toServerLogin(sharedPreferences.getString("Login","")!!,sharedPreferences.getString("Pass","")!!)//prend les datas dans le sharedPreferences pour se connecter
            }else{//sinon il vas se log
                val intent = Intent(this, Enregistrement::class.java)
                startActivity(intent)
            }
        }

    }

    fun toServerLogin(pseudo: String, mdp: String){//requete HTTP en POST
        volleyRequestQueue = Volley.newRequestQueue(this)
        val parameters: MutableMap<String, String> = HashMap()
        parameters.put("pseudo",pseudo)
        parameters.put("mdp",mdp)
        parameters.put("fonction","login")
        val strReq: StringRequest = object : StringRequest(
            Method.POST,serverAPIURL,
            Response.Listener { response ->
                Log.i("toServeur", "Send")
                Log.i("reponse", response)
                if(response=="OK"){
                    progressDialog.dismiss()
                    val intent = Intent(this, Commu::class.java)
                    startActivity(intent)
                }else{
                    Toast.makeText(this, "Probleme de connexion", Toast.LENGTH_SHORT).show()
                }
            },
            Response.ErrorListener { volleyError -> // error occurred
                Log.i("toServeur", "Error")}) {

            override fun getParams(): MutableMap<String, String> {
                return parameters;
            }

            @Throws(AuthFailureError::class)
            override fun getHeaders(): Map<String, String> {
                val headers: MutableMap<String, String> = HashMap()
                // Add your Header paramters here
                return headers
            }
        }
        // Adding request to request queue
        volleyRequestQueue?.add(strReq)
    }
}

