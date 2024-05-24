package com.example.picprog

import Retouch
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.view.MotionEvent
import android.view.View
import android.widget.Button
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.ComponentActivity
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.lifecycle.lifecycleScope
import image.Image
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import org.opencv.android.OpenCVLoader
import redactor.*
import java.io.File
import java.io.FileOutputStream


class MainActivity : ComponentActivity() {
    lateinit var loadBtn : ImageButton
    lateinit var imageView : ImageView
    lateinit var saveBtn : ImageButton
    lateinit var image: Image
    lateinit var retouch : Retouch

    private var retouchFlag = false

    var nowRedactor: Redactor = GausBlur()

    @RequiresApi(Build.VERSION_CODES.P)
    val selectImageIntent = registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        onImageGet(uri)
        saveBtn.visibility = View.VISIBLE
    }

    @SuppressLint("ClickableViewAccessibility")
    @RequiresApi(Build.VERSION_CODES.P)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main_page)

        OpenCVLoader.initLocal();
        val inputStream = resources.openRawResource(R.raw.lbpcascade_frontalface_improved)
        val weightsDir = applicationContext.getDir("cascade", Context.MODE_PRIVATE)
        val weightsFile = File(weightsDir, "lbpcascade_frontalface_improved.xml")
        val outputStream = FileOutputStream(weightsFile)
        val buffer = ByteArray(4096)
        var bytesRead: Int
        while (inputStream.read(buffer).also { bytesRead = it } != -1) {
            outputStream.write(buffer, 0, bytesRead)
        }
        inputStream.close()
        outputStream.close()

        loadBtn = findViewById(R.id.loadBtn)
        imageView = findViewById(R.id.img)
        saveBtn = findViewById(R.id.saveBtn)
        val loadTxt = findViewById<TextView>(R.id.loading)

        findViewById<Button>(R.id.GausBlur).setOnClickListener{
            nowRedactor = GausBlur()
            val job = lifecycleScope.launch {
                nowRedactor.compile(image)
            }
        }

        findViewById<Button>(R.id.Mosaic).setOnClickListener{
            nowRedactor = Mosaic()
            lifecycleScope.async { nowRedactor.compile(image) }

        }

        findViewById<Button>(R.id.Resize).setOnClickListener{
            nowRedactor = Resize()
            lifecycleScope.async { nowRedactor.compile(image) }
        }

        findViewById<Button>(R.id.faces).setOnClickListener{
            image.setBitMap(FindFaces.drawRectangles(image.getBitmap(), weightsFile))
        }

        findViewById<Button>(R.id.Grayscale).setOnClickListener{
            nowRedactor = Grayscale()
            lifecycleScope.async { nowRedactor.compile(image) }
        }


        findViewById<Button>(R.id.Rotation).setOnClickListener{
            nowRedactor = Rotation()
            lifecycleScope.async { nowRedactor.compile(image) }

        }

        imageView.setOnTouchListener(View.OnTouchListener { _: View, m: MotionEvent ->
            retouch.onTouchEvent(m)
        })

        findViewById<Button>(R.id.retouch).setOnClickListener {
            retouch.use(!retouchFlag)
            retouchFlag = !retouchFlag

        }

        saveBtn.setOnClickListener{
                image.save()
        }

        loadBtn.setOnClickListener{
            selectImageIntent.launch("image/*")
        }

        findViewById<Button>(R.id.escapeBtn).setOnClickListener {
            val intent = Intent(this, MenuActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP)
            startActivity(intent)
        }
    }

    @RequiresApi(Build.VERSION_CODES.P)
    fun onImageGet(imgUri: Uri?){
        if (imgUri != null){
            val srcBitmap = ImageDecoder.decodeBitmap(
                ImageDecoder.createSource(this.contentResolver, imgUri),
                ImageDecoder.OnHeaderDecodedListener { decoder, info, source ->
                    decoder.allocator = ImageDecoder.ALLOCATOR_SOFTWARE
                    decoder.isMutableRequired = true
                })
            image = Image(srcBitmap, "Result", imageView)
            retouch = Retouch(this.applicationContext, image)
            retouchFlag = false
        }
    }
}