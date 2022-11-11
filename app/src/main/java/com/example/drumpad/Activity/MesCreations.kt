package com.example.drumpad.Activity

import android.app.AlertDialog
import android.content.Intent
import android.content.SharedPreferences
import android.media.MediaPlayer
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.EditText
import android.widget.SeekBar
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.preference.PreferenceManager
import com.android.volley.AuthFailureError
import com.android.volley.RequestQueue
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.example.drumpad.R
import kotlinx.android.synthetic.main.activity_mes_creations.retour
import kotlinx.android.synthetic.main.activity_mes_creations.*
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.io.File
import java.util.*
import kotlin.collections.ArrayList


class MesCreations : AppCompatActivity() {

    var seekbarcoroutine: Job? = null
    var mp: MediaPlayer? = null
    val ListeFichier: ArrayList<String> = ArrayList<String>()
    var sizeListe: Int = 0
    var idOnPlay: Int = 0
    var titre: String = ""
    var titreActuel: String = ""
    val serverAPIURL: String = "http://lahoucine-hamsek.fr/Drumpad.php"
    var nouvellemusique: Boolean = false
    var volleyRequestQueue: RequestQueue? = null
    var rep: String = ""
    var isInDB: Boolean = false
    lateinit var sharedPreferences: SharedPreferences
    var namefile: String = ""

    override fun onStop() {
        super.onStop()
        if (mp!==null){
            mp?.stop()
            mp?.reset()
            mp?.release()
            mp = null
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        if (mp!==null){
            mp?.stop()
            mp?.reset()
            mp?.release()
            mp = null
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_mes_creations)

        val wallpaperDirectory = File("/storage/emulated/0/DrumPadRec/")
        wallpaperDirectory.mkdirs()// cree le repertoire

        retour.setOnClickListener {
            val intent = Intent(this, Accueil::class.java)
            startActivity(intent)
        }

        getFichier()// get les fichiers present dans le repertoire

        if(!ListeFichier.isEmpty()){//si le repertoire est pas vide
            Log.i("Fichier", ListeFichier[0])
            controlSound(ListeFichier[0])//lance la premier musique
        }

        skip.setOnClickListener {
            SeekBar.progress = 0
            nouvellemusique=true
            if (mp!==null){
                SeekBar.progress = 0
                mp?.stop()
                mp?.reset()
                mp?.release()
                mp = null
            }
            if (idOnPlay==sizeListe-1){//fait bouger le curseur idOnPlay en fonction de sa position. S'il est à la fin il passe à 0 pour faire une sorte de boucl
                idOnPlay=0
            }else{
                idOnPlay+=1
            }
            SeekBar.progress = 0
            if(!ListeFichier.isEmpty()) {
                controlSound(ListeFichier[idOnPlay])
            }
        }

        back.setOnClickListener {
            SeekBar.progress = 0
            nouvellemusique=true
            if (mp!==null){
                mp?.stop()
                mp?.reset()
                mp?.release()
                mp = null
            }
            if (idOnPlay==0){
                idOnPlay=sizeListe-1
            }else{
                idOnPlay-=1
            }
            SeekBar.progress = 0
            if(!ListeFichier.isEmpty()) {
                controlSound(ListeFichier[idOnPlay])
            }
        }

    }

    private fun getFichier(){// Obtien les fichiers qu'il y a dans le repertoire sous forme de tableau
        ListeFichier.clear()
        File("/storage/emulated/0/DrumPadRec").list().forEach {
            ListeFichier.add("/storage/emulated/0/DrumPadRec/" + it)
        }
        sizeListe = ListeFichier.size
    }

    private fun controlSound(File: String){// change l'action des boutons en fonction de la musique en cours

        nbmusique.text = (idOnPlay+1).toString() +"/"+sizeListe.toString()
        SeekBar.progress = 0
        for(i in 31..File.length-5){
            titre+=File[i]
        }
        if (sharedPreferences.getString("Login", "")?.isNotEmpty()!!) {//verifie si la musique est pas deja dans le serveur s'il y a un compte
            toServerLogin(titre+".mp3",sharedPreferences.getString("Login", "")!!,"isInDBMusique")
        }
        titreMusique.text = titre.replace("-"," ",true)
        titreActuel = titre
        titre = ""

        start.setOnClickListener {
            nouvellemusique=false
            if (mp==null) {
                mp = MediaPlayer()
                mp?.setDataSource(File)
                Log.i("DataSource", File)
                mp?.prepare()
                Log.d("MesCreations", "ID:${mp!!.audioSessionId}")
            }
                mp?.start()
                initialiseSeekBar()

            Log.d("MesCreations", "Durée: ${mp!!.duration / 1000} seconds")
        }

        pause.setOnClickListener {
            mp?.pause()
            Log.d("MesCreations", "Je suis en pause: ${mp!!.currentPosition / 1000} seconds")
        }

        upload.setOnClickListener {
            Log.i("Upload","send")
        }

        remove.setOnClickListener {
            val monfichier: File = File(File)
                if(monfichier.delete()){//supprime le fichier
                    Toast.makeText(this, "Musique supprimé", Toast.LENGTH_SHORT).show()
                    getFichier()//get la nouvelle liste
                    SeekBar.progress = 0
                    nouvellemusique=true
                    if (mp!==null){//arrete la musique en cours
                        SeekBar.progress = 0
                        mp?.stop()
                        mp?.reset()
                        mp?.release()
                        mp = null
                    }
                    getFichier()
                    idOnPlay=0
                    SeekBar.progress = 0
                    if(!ListeFichier.isEmpty()) {//relance la liste depuis le debut
                        controlSound(ListeFichier[idOnPlay])
                    }
                }

        }

        rename.setOnClickListener {
            val monfichier: File = File(File)//fichier dont on veut changer le nom
            val viewDialog: View = LayoutInflater.from(this).inflate(R.layout.alert_dialog_rename,null)
            val text: EditText = viewDialog.findViewById<EditText>(R.id.titre)

            val dialog: AlertDialog.Builder = AlertDialog.Builder(this)
                .setPositiveButton("Renommer"){dialog, which ->// lorsque le bouton OK est pressé
                    Log.i("Nom fichier",text.text.toString())
                    Log.i("liste fichier",ListeFichier.toString())
                    Log.i("contains",ListeFichier.contains(text.text.toString()+".mp3").toString())
                    namefile = text.text.toString().replace(" ", "-",true)// remplace les " " par "-"
                    if(!ListeFichier.contains("/storage/emulated/0/DrumPadRec/"+    namefile+".mp3")){// si le nom est pas utilise
                        var nouveauFichier: File = File("/storage/emulated/0/DrumPadRec/"+namefile+".mp3")
                        if(monfichier.renameTo(nouveauFichier)){//change le nom
                            Log.i("RENAME","OUI")
                        }else{
                            Log.i("RENAME","NON")
                        }
                        getFichier()//get la nouvelle liste
                        idOnPlay=0
                        SeekBar.progress = 0
                        if(!ListeFichier.isEmpty()) {//lance la liste au debut
                            controlSound(ListeFichier[idOnPlay])
                        }
                    }else{
                        Toast.makeText(this, "Ce nom est déjà utilisé", Toast.LENGTH_SHORT).show()
                    }
                }
                .setTitle("Merci d'entrer un nouveau titre")
                .setView(viewDialog)
            dialog.show()
        }

        SeekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                if (fromUser) mp?.seekTo(progress)
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {
            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {
            }

        })

        }

    fun initialiseSeekBar(){//fait progress la barre en fonction de la musique en cours
        SeekBar.max = mp!!.duration
        var max = mp!!.duration
        var pos = 0
        var div:Long = (100/max*1000).toLong()
        seekbarcoroutine = GlobalScope.launch {
           while (pos != max){
               if(nouvellemusique==false){
                   try {
                       mp?.let {
                           SeekBar.progress = it.currentPosition
                           pos = it.currentPosition
                       }
                   }catch (e: Exception){
                   }
                   delay(130)
               }
               if (nouvellemusique==true){// sort de la boucle et arrete le thread si une nouvelle musique est lancé
                   Log.i("BREAK", "BREAK")
                   SeekBar.progress = 0
                   break
               }
                }
            SeekBar.progress = 0
            //delay(130)
            SeekBar.progress = 0
            }

        }

    fun toServerLogin(musique: String, createur: String, fonction: String){/// requete HTTP en POST
        volleyRequestQueue = Volley.newRequestQueue(this)
        val parameters: MutableMap<String, String> = HashMap()
        parameters.put("musique", musique)
        parameters.put("createur", createur)
        parameters.put("fonction", fonction)
        val strReq: StringRequest = object : StringRequest(
            Method.POST, serverAPIURL,
            Response.Listener { response ->
                Log.i("toServeur", "Send")
                if(fonction=="isInDBMusique"){
                    Log.i("DEBUG","JE SUIS LA")
                    isInDB = response.toBoolean()
                    Log.i("isInDBMusique", response)
                }else{
                    rep = response
                }
            },
            Response.ErrorListener { volleyError -> // error occurred
                Log.i("toServeur", "Error")
            }) {

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
