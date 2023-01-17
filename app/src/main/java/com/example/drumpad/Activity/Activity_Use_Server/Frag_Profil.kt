package com.example.drumpad.Activity_Use_Server


import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.preference.PreferenceManager
import com.android.volley.AuthFailureError
import com.android.volley.RequestQueue
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.example.drumpad.Activity.Accueil
import com.example.drumpad.R
import kotlinx.android.synthetic.main.fragment_frag_profil.view.*
import java.util.HashMap


class Frag_Profil : Fragment() {

    lateinit var sharedPreferences: SharedPreferences
    var volleyRequestQueue: RequestQueue? = null
    val serverAPIURL: String = "http://lahoucine-hamsek.fr/Drumpad.php"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

     override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(requireContext())
        val view: View = inflater!!.inflate(R.layout.fragment_frag_profil, container, false)
        val pseudo: String = sharedPreferences.getString("Login","")!!
         view.pseudo.text = pseudo

         view.deco.setOnClickListener {//vide ce qu'il y a dans le sharedPreferences et met à l'accueil
             Log.i("JE SUIS LA","coucou")
             val editor = sharedPreferences.edit()
             editor.clear()
             editor.apply()
             Toast.makeText(requireContext(), "Vous etes deconnecté", Toast.LENGTH_SHORT).show()
             val intent = Intent(requireContext(), Accueil::class.java)
             startActivity(intent)
         }

        return view
    }

    override fun onStart() {
        super.onStart()
        toServerLogin("NbMusique")//Avoir le nombre de musique deja upload par l'utilisateur
        toServerLogin("1etoile")//Avoir le nombre d'étoiel collecté par l'utilisateur
        toServerLogin("2etoile")//Avoir le nombre d'étoiel collecté par l'utilisateur
        toServerLogin("3etoile")//Avoir le nombre d'étoiel collecté par l'utilisateur
        toServerLogin("4etoile")//Avoir le nombre d'étoiel collecté par l'utilisateur
        toServerLogin("5etoile")//Avoir le nombre d'étoiel collecté par l'utilisateur
    }

    fun toServerLogin(fonction: String){//requete HTTP en POST
        volleyRequestQueue = Volley.newRequestQueue(requireContext())
        val parameters: MutableMap<String, String> = HashMap()
        parameters.put("fonction",fonction)
        parameters.put("id",sharedPreferences.getString("Login","")!!)
        val strReq: StringRequest = object : StringRequest(
            Method.POST,serverAPIURL,
            Response.Listener { response ->
                Log.i("toServeur", "Send")
                Log.i("Reponse serveur",response )
                val editor = sharedPreferences.edit()
                if(fonction=="NbMusique"){
                    editor.putString("NbMusique", response)
                    Log.i("NbMusique",response )
                    if(response==""){
                        view?.textView?.text = "Nombre de musique en ligne : 0"
                    }else{
                        view?.textView?.text = "Nombre de musique en ligne : " + response
                        Log.i("1etoile",response )
                    }
                }
                if(fonction=="1etoile"){
                    if(response==""){
                        view?.etoile1?.text = "1 etoile : 0"
                    }else{
                        view?.etoile1?.text = "1 etoile : " + response
                        Log.i("1etoile",response )
                    }
                }
                if(fonction=="2etoile"){
                    if(response==""){
                        view?.etoile2?.text = "2 etoile : 0"
                    }else{
                        view?.etoile2?.text = "2 etoile : " + response
                        Log.i("2etoile",response )
                    }
                }
                if(fonction=="3etoile"){
                    if(response==""){
                        view?.etoile3?.text = "3 etoile : 0"
                    }else{
                        view?.etoile3?.text = "3 etoile : " + response
                        Log.i("3etoile",response )
                    }
                }
                if(fonction=="4etoile"){
                    if(response==""){
                        view?.etoile4?.text = "4 etoile : 0"
                    }else{
                        view?.etoile4?.text = "4 etoile : " + response
                        Log.i("4etoile",response )
                    }
                }
                if(fonction=="5etoile"){
                    if(response==""){
                        view?.etoile5?.text = "5 etoile : 0"
                    }else{
                        view?.etoile5?.text = "5 etoile : " + response
                        Log.i("5etoile",response )
                    }
                }
                editor.apply()
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

