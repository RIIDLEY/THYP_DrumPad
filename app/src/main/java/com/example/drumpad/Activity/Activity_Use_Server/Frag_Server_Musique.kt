package com.example.drumpad.Activity_Use_Server

import android.app.AlertDialog
import android.media.MediaPlayer
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioButton
import android.widget.SeekBar
import com.android.volley.AuthFailureError
import com.android.volley.RequestQueue
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.example.drumpad.R
import kotlinx.android.synthetic.main.alert_dialog_note.view.*
import kotlinx.android.synthetic.main.fragment_frag_server_musique.*
import kotlinx.android.synthetic.main.fragment_frag_server_musique.view.*
import kotlinx.coroutines.*
import java.util.HashMap


class Frag_Server_Musique : Fragment() {

    var volleyRequestQueue: RequestQueue? = null
    val serverAPIURL: String = "http://lahoucine-hamsek.fr/DrumpadFrag.php" //fichier à interroger dans le serveur pour avoir les infos dessus
    var serverFolder = "http://lahoucine-hamsek.fr/uploads/" //fichier où sont stocker les musique
    var URLfile = "" //url exemple : http://lahoucine-hamsek.site/uploads/Jean.mp3
    var file = "" // fichier exemple : Jean.mp3
    var titre: String = ""
    var nbMax: Int = 0 //Nombre de musique sur le serveur
    var mp: MediaPlayer? = null
    var nbmusique: Int = 0 //L'ID où on est dans la liste
    var seekbarcoroutine: Job? = null
    var nouvellemusique: Boolean = false //flag pour savoir si on passe à une nouvelle musique
    var artiste: String = "" //nom de l'artiste
    lateinit var radioButton: RadioButton
    var nbEtoileMoyenne: String =""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onStop() {
        super.onStop()
        if (mp!==null){
            mp?.stop()
            mp?.reset()
            mp?.release()
            mp = null
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        if (mp!==null){
            mp?.stop()
            mp?.reset()
            mp?.release()
            mp = null
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view: View = inflater.inflate(R.layout.fragment_frag_server_musique, container, false)

        toServerLogin(0,"getNbMax","","")
        Log.i("getNbMax",nbMax.toString())// get le nombre de musique dispo sur le serveur

       toServerLogin(nbmusique,"musique","","")
        //controlSound(firtmusique,"daft.mp3")
        view.skip_Co.setOnClickListener {
            Log.i("SKIP","skip")
            nbEtoileMoyenne=""
            SeekBarFrag.progress = 0
            nouvellemusique=true
            if (mp!==null){
                mp?.stop()
                mp?.reset()
                mp?.release()
                mp = null
            }
            if (nbmusique==nbMax-1){//fait bouger le curseur nbmusique en fonction de sa position. S'il est à la fin il passe à 0 pour faire une sorte de boucle
                nbmusique=0
            }else{
                nbmusique+=1
            }
            toServerLogin(nbmusique,"musique","","")//lance la musique suivante
            Log.i("nbMusique",nbmusique.toString())
        }

        view.back_Co.setOnClickListener {
            Log.i("BACK","back")
            SeekBarFrag.progress = 0
            nbEtoileMoyenne=""
            nouvellemusique=true
            if (mp!==null){
                mp?.stop()
                mp?.reset()
                mp?.release()
                mp = null
            }
            if (nbmusique==0){//fait bouger le curseur nbmusique en fonction de sa position. S'il est à la fin il passe à 0 pour faire une sorte de boucle
                nbmusique=nbMax-1
            }else{
                nbmusique-=1
            }
            toServerLogin(nbmusique,"musique","","")//lance la musique d'avant
        }
        return view
    }

    override fun onStart() {
        super.onStart()
        nbEtoileMoyenne=""
        if(nbmusique>=1){//si y a une musique sur le serveur
            toServerLogin(nbmusique,"musique","","")//lance la musique
        }
    }


    private fun controlSound(File: String, namefile: String) {// met a jour les boutons en fonction de la musique en cours
        FragNbMusique.text = (nbmusique+1).toString() + "/" + nbMax.toString()
        moyeEtoile.text = "Moyenne d'étoile : "+nbEtoileMoyenne// met le nombre d'étoile en moyenne
        view?.SeekBarFrag?.progress=0//init la seekbar à 0
        if (File != "oui"){
            Log.i("controlSound","tourne")
            for (i in 0..namefile.length - 5) {// passe de "Jean.mp3" à "Jean"
                titre += namefile[i]
            }
            view?.titre?.text = titre.replace("-"," ",true)//set le nom de la musique
            view?.FragArtiste?.text = "Artiste : " + artiste//set le nom de l'artiste
            Log.i("titre",titre)
            Log.i("filename",namefile)
            titre = ""
            artiste = ""
        }

        view?.start_Co?.setOnClickListener {
            nouvellemusique=false
            Log.i("START","start")
            if (mp == null) {
                mp = MediaPlayer()
                mp?.setDataSource(File)
                mp?.prepare()
            }
            initialiseSeekBar()

            mp?.start()
        }

        view?.pause_Co?.setOnClickListener {
            Log.i("PAUSE","pause")
            mp?.pause()
        }

        view?.SeekBarFrag?.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                if (fromUser) mp?.seekTo(progress)
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {
            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {
            }

        })

            view?.note?.setOnClickListener {
            val view: View = LayoutInflater.from(requireContext()).inflate(R.layout.alert_dialog_note,null)
            val dialog: AlertDialog.Builder = AlertDialog.Builder(requireContext())//set l'alertDialog
                .setTitle("Donne une note !")
                .setPositiveButton("Voter"){dialog, which ->
                    val selectedOption: Int = view?.radioGroup1.checkedRadioButtonId
                    radioButton = view?.findViewById(selectedOption)
                    Log.i("Envoyé","Oui")
                    Log.i("Envoyé",namefile)
                    toServerLogin(0,"etoile",radioButton.text.toString(),namefile)//envoie le vote
                    nbEtoileMoyenne=""
                    toServerLogin(nbmusique,"musique","","")
                }
                .setView(view)
            dialog.show()
        }
        URLfile = ""


    }

    fun initialiseSeekBar(){//fait progress la barre en fonction de la musique en cours
        view?.SeekBarFrag?.max = mp!!.duration
        var max = mp!!.duration
        var pos = 0
        var div:Long = (100/max*1000).toLong()
        seekbarcoroutine = GlobalScope.launch {
            while (pos != max){
                if(nouvellemusique==false){
                    try {
                        mp?.let {
                            view?.SeekBarFrag?.progress = it.currentPosition
                            pos = it.currentPosition
                        }
                    }catch (e: Exception){
                    }
                    delay(130)
                }
                if (nouvellemusique==true){// sort de la boucle et arrete le thread si une nouvelle musique est lancé
                    Log.i("BREAK", "BREAK")
                    view?.SeekBarFrag?.progress = 0
                    break
                }
            }
            view?.SeekBarFrag?.progress = 0
            //delay(130)
            view?.SeekBarFrag?.progress = 0
        }

    }

    fun toServerLogin(id: Int, fonction: String,etoile: String, namefile: String){//requete HTTP en POST
        volleyRequestQueue = Volley.newRequestQueue(requireContext())
        val parameters: MutableMap<String, String> = HashMap()
        parameters.put("fonction",fonction)
        parameters.put("id",id.toString())
        parameters.put("etoile",etoile)
        parameters.put("musique",namefile)
        val strReq: StringRequest = object : StringRequest(
            Method.POST,serverAPIURL,
            Response.Listener { response ->
                Log.i("toServeur", "Send")
                if (fonction == "getNbMax"){
                    Log.i("getNbMax",response)
                    nbMax = response.toInt()
                }
                if(fonction == "artiste"){
                    Log.i("Artiste",response)
                    artiste = response
                    URLfile = serverFolder + file
                    controlSound(URLfile,file)// lance les modifications des boutons et les textes
                }
                if(fonction == "musique"){
                    file = response
                    Log.i("MusiqueServer",file)
                    toServerLogin(0,"etoileMoyenne","",file)//vas cherche le nombre d'étoile en moyenne de la musique
                }
                if(fonction == "etoileMoyenne"){
                    nbEtoileMoyenne=""
                    var i: Int =0
                    if(response.length>3){
                        while(i<3){
                            nbEtoileMoyenne+=response[i]
                            i++
                        }
                    }else{
                        nbEtoileMoyenne=response
                    }

                    Log.i("JE SUIS LA","etoileMoyenne")
                    Log.i("etoileMoyenne",nbEtoileMoyenne)
                    toServerLogin(0,"artiste","",file)//vas cherche le nom de l'artiste
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

