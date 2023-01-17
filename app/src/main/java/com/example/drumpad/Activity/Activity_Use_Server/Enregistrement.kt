package com.example.drumpad.Activity_Use_Server

import android.app.ProgressDialog
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.preference.PreferenceManager
import com.android.volley.AuthFailureError
import com.android.volley.RequestQueue
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.example.drumpad.Activity.Accueil
import com.example.drumpad.R
import kotlinx.android.synthetic.main.activity_enregistrement.*
import kotlinx.coroutines.*
import java.util.HashMap



class Enregistrement : AppCompatActivity() {

    var reponseServer: String = ""
    var key1: String = "Login"
    var key2: String = "Pass"
    val serverAPIURL: String = "http://lahoucine-hamsek.fr/Drumpad.php"
    var volleyRequestQueue: RequestQueue? = null
    lateinit var progressDialog: ProgressDialog
    lateinit var sharedPreferences: SharedPreferences
    var login: String = ""
    var password: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_enregistrement)

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this)
        retour.setOnClickListener {
            val intent = Intent(this, Accueil::class.java)
            startActivity(intent)
        }

        boutonlog.setOnClickListener {
            Log.i("Login","Bouton Oui")
            login = pseudo.text.toString()//get le login
            password = mdp.text.toString()//get le mdp
            if(login != "" && password != ""){
                toServerLogin(pseudo.text.toString(),mdp.text.toString())//envoie tout ça pour faire une requete
                progressDialog = ProgressDialog(this)
                progressDialog.setTitle("Connection")
                progressDialog.setMessage("En cours de connexion")
                progressDialog.show()//lance la progressDialog
            }else{
                Toast.makeText(this, "Champ vide", Toast.LENGTH_SHORT).show()
            }
        }

    }

    override fun onStart() {
        super.onStart()
        Log.i("LogGVar",sharedPreferences.getString("Login","").toString())
        if (sharedPreferences.getString("Login","")?.isNotEmpty()!!){//Si a deja été log
            val pseudo: String = sharedPreferences.getString(key1,"")!!
            val motdepasse: String = sharedPreferences.getString(key2,"")!!
            toServerLogin(pseudo,motdepasse)
            progressDialog = ProgressDialog(this)
            progressDialog.setTitle("Connexion")
            progressDialog.setMessage("En cours de connexion")
            progressDialog.show()
            GlobalScope.launch {
                delay(1000)
                changeView()
            }
        }
    }

    fun registerFunc(view: View) {
        val intent = Intent(this, Inscription::class.java)
        startActivity(intent)
    }

    fun toServerLogin(pseudo: String, mdp: String){// requete HTTP en POST
        volleyRequestQueue = Volley.newRequestQueue(this)
        val parameters: MutableMap<String, String> = HashMap()
        parameters.put("pseudo",pseudo)
        parameters.put("mdp",mdp)
        parameters.put("fonction","login")
        val strReq: StringRequest = object : StringRequest(
            Method.POST,serverAPIURL,
            Response.Listener { response ->
                Log.i("toServeur", "Send")
                //Toast.makeText(this, "Reponse $response", Toast.LENGTH_SHORT).show()
                reponseServer = response
                changeView()
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

    fun changeView(){// change la vue en fonction de la reponse du serveur
        Log.i("ChanegView","Je suis la")
        if (reponseServer == "OK"){
            progressDialog.dismiss()
            val editor = sharedPreferences.edit()
            editor.putString(key1, login)//met dans le sharedPreferences
            editor.putString(key2, password)
            editor.apply()
            //Toast.makeText(this, "Connecté", Toast.LENGTH_SHORT).show()
            val intent = Intent(this, Commu::class.java)
            startActivity(intent)// change d'activity
        }else{
            progressDialog.dismiss()
            Toast.makeText(this, "Mot de passe ou pseudo incorrect", Toast.LENGTH_SHORT).show()
        }
    }

}