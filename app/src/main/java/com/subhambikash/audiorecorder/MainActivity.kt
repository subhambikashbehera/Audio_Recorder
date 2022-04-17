package com.subhambikash.audiorecorder

import android.content.ContentValues
import android.content.pm.PackageManager
import android.media.MediaRecorder
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import kotlinx.android.synthetic.main.activity_main.*
import java.io.File
import java.time.LocalDateTime

class MainActivity : AppCompatActivity() {


    lateinit var mp: MediaRecorder
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        if (ActivityCompat.checkSelfPermission(this,
                android.Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this,
                android.Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
        ) {
            resultLauncher.launch(arrayOf(android.Manifest.permission.RECORD_AUDIO,
                android.Manifest.permission.READ_EXTERNAL_STORAGE,
                android.Manifest.permission.WRITE_EXTERNAL_STORAGE))
        } else {
            recordAudio()
        }


    }

    private var resultLauncher =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) {

            it.entries.forEach { it1 ->
                if (it1.value) {
                    recordAudio()
                }
            }

        }


    private fun recordAudio() {
        recorded.setOnClickListener {
            createRecordingFolder("RecordedAudio")
            val path = Environment.getExternalStorageDirectory().absolutePath + "/RecordedAudio/" + LocalDateTime.now() + ".mp3"
            Toast.makeText(this, "recording started", Toast.LENGTH_SHORT).show()
            recorded.isEnabled = false
            stop.isEnabled = true
            mp = MediaRecorder()
            mp.setAudioSource(MediaRecorder.AudioSource.MIC)
            mp.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP)
            mp.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB)
            mp.setOutputFile(path)
            mp.prepare()
            mp.start()
        }
        stop.setOnClickListener {
            stop.isEnabled = false
            recorded.isEnabled = true
            Toast.makeText(this, "saved", Toast.LENGTH_SHORT).show()
            mp.pause()
            mp.stop()
            mp.release()
        }


        
    }


    private fun createRecordingFolder(FolderName: String): File? {
        var dir: File? = null
        dir = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
                .toString() + "/" + FolderName)
        } else {
            File(Environment.getExternalStorageDirectory().toString() + "/" + FolderName)
        }
        if (!dir.exists()) {
            val success = dir.mkdirs()
            if (!success) {
                dir = null
            }
        }
        return dir
    }


}