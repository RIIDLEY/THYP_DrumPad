package com.example.drumpad.Activity

import android.app.AlertDialog
import android.content.Intent
import android.media.MediaPlayer
import android.media.MediaRecorder
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.drumpad.R
import kotlinx.android.synthetic.main.activity_drumpad.*
import java.io.File
import java.io.IOException


class DrumPad : AppCompatActivity() {


    var mediaPlayer1: MediaPlayer? = null
    var mediaPlayer2: MediaPlayer? = null
    var mediaPlayer3: MediaPlayer? = null
    var mediaPlayer4: MediaPlayer? = null
    var mediaPlayer5: MediaPlayer? = null
    var mediaPlayer6: MediaPlayer? = null
    var mediaPlayer7: MediaPlayer? = null
    var mediaPlayer8: MediaPlayer? = null
    var mediaPlayer9: MediaPlayer? = null

    var fichiermp3: String? = null
    var mediaRecorder: MediaRecorder? = null
    var recEnCours: Boolean = false
    val ListeFichier: ArrayList<String> = ArrayList<String>()
    var sizeListe: Int = 0
    var namefile: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {

        val wallpaperDirectory = File("/storage/emulated/0/DrumPadRec/")
        wallpaperDirectory.mkdirs()


        mediaPlayer1 = MediaPlayer.create(this, R.raw.clapanalog)//creation des mediaplayers
        mediaPlayer2 = MediaPlayer.create(this, R.raw.kickelectro01)
        mediaPlayer3 = MediaPlayer.create(this, R.raw.hihat808)
        mediaPlayer4 = MediaPlayer.create(this, R.raw.hihatacoustic01)
        mediaPlayer5 = MediaPlayer.create(this, R.raw.hihatanalog)
        mediaPlayer6 = MediaPlayer.create(this, R.raw.kicksofty)
        mediaPlayer7 = MediaPlayer.create(this, R.raw.kicktape)
        mediaPlayer8 = MediaPlayer.create(this, R.raw.snare808)
        mediaPlayer9 = MediaPlayer.create(this, R.raw.tomrototom)


        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_drumpad)


        retour.setOnClickListener {
            val intent = Intent(this, Accueil::class.java)
            startActivity(intent)
        }

        mamusique.setOnClickListener {
            val intent = Intent(this, MesCreations::class.java)
            startActivity(intent)
        }

        recbtn.setOnClickListener {
                getFichier()
            for (element in ListeFichier) {
                Log.i("Array",element)
            }
                val viewDialog: View = LayoutInflater.from(this).inflate(R.layout.alert_dialog_rec,null)//get la view du dialog
                val text: EditText = viewDialog.findViewById<EditText>(R.id.titre)//get l'id de l'edittext
                val dialog: AlertDialog.Builder = AlertDialog.Builder(this)//creation de l'alert dialog
                    .setPositiveButton("Commencer à enregistre"){dialog, which ->
                        Log.i("Nom fichier",text.text.toString())
                        Log.i("contains",ListeFichier.contains(text.text.toString()+".mp3").toString())
                        namefile = text.text.toString().replace(" ", "-",true)//transforme les " " en "-"
                        if(!ListeFichier.contains(namefile+".mp3")){//si le nom n'est pas deja pris
                            prepareRecording(namefile)//prepare le rec
                            startRecording()// lance le rec
                            recEnCours = true//flag pour savoir si c'est en cours d'enregistrement
                        }else{
                            Toast.makeText(this, "Ce nom est déjà utilisé", Toast.LENGTH_SHORT).show()//toast erreur
                        }
                    }
                    .setTitle("Merci d'entrer un nom pour\n l'enregistrement")
                    .setView(viewDialog)
                if(recEnCours==false){//si pas en cours de rec
                    dialog.show()// affiche le dialog
                    Log.i("recbtn","oui")
                }
                if (recEnCours){//si en cours de rec
                    Log.i("recbtn","non")
                    stopRecording()//stop le rec
                    recEnCours = false
                }
        }

    }


    fun playSound1(view: View) {
        mediaPlayer1?.start()
    }

    fun playSound2(view: View) {
        mediaPlayer2?.start()
    }

    fun playSound3(view: View) {
        mediaPlayer3?.start()
    }

    fun playSound4(view: View) {
        mediaPlayer4?.start()
    }

    fun playSound5(view: View) {
        mediaPlayer5?.start()
    }

    fun playSound6(view: View) {
        mediaPlayer6?.start()
    }

    fun playSound7(view: View) {
        mediaPlayer7?.start()
    }

    fun playSound8(view: View) {
        mediaPlayer8?.start()
    }

    fun playSound9(view: View) {
        mediaPlayer9?.start()
    }


    //--------------------------

    fun prepareRecording(nomfichier: String){
        Log.i("prepareRecording","ok")

        mediaRecorder = MediaRecorder()//creation du mediarecord

        fichiermp3 = Environment.getExternalStorageDirectory().absolutePath + "/DrumPadRec/" + nomfichier + ".mp3"//chemin du fichier mp3

        mediaRecorder?.setAudioSource(MediaRecorder.AudioSource.MIC)//set la source micro
        mediaRecorder?.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4)
        mediaRecorder?.setAudioEncoder(MediaRecorder.AudioEncoder.AAC)
        mediaRecorder?.setAudioEncodingBitRate(128000)
        mediaRecorder?.setAudioSamplingRate(44100)
        mediaRecorder?.setOutputFile(fichiermp3)
    }


     fun startRecording() {
        try {
            mediaRecorder?.prepare()
            mediaRecorder?.start()
            Toast.makeText(this, "A vous de jouer", Toast.LENGTH_SHORT).show()
        } catch (e: IllegalStateException) {
            e.printStackTrace()
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

     fun stopRecording(){
            mediaRecorder?.stop()
            mediaRecorder?.release()
            Toast.makeText(this, "C'est dans la boite", Toast.LENGTH_SHORT).show()
    }

    private fun getFichier(){// recupere tout les fichiers qu'il y a dans le dossier pour la comparaison
        ListeFichier.clear()
        File("/storage/emulated/0/DrumPadRec").list().forEach {
            ListeFichier.add(it)
        }
        sizeListe = ListeFichier.size
    }

}