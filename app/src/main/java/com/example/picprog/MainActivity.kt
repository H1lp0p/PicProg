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
import android.widget.LinearLayout
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.lifecycle.lifecycleScope
import image.Image
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import org.opencv.android.OpenCVLoader
import redactor.*
import java.io.File
import java.io.FileOutputStream


class MainActivity : ComponentActivity() {
    private lateinit var loadBtn : ImageButton
    private lateinit var imageView : ImageView
    private lateinit var saveBtn : ImageButton
    private var image: Image? = null
    private lateinit var retouch : Retouch
    private lateinit var settingsLayout: LinearLayout

    private var retouchFlag = false

    private var nowRedactor: Redactor = GausBlur()

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
        settingsLayout = findViewById(R.id.settings)

        findViewById<ImageButton>(R.id.GausBlur).setOnClickListener{
            if (nowRedactor !is GausBlur) {
                nowRedactor = GausBlur()
            }
            if (image != null) nowRedactor.settings(settingsLayout, this, image!!)
        }

        findViewById<ImageButton>(R.id.Mosaic).setOnClickListener{
            if (nowRedactor !is Mosaic) {
                nowRedactor = Mosaic()
            }
            if (image != null) nowRedactor.settings(settingsLayout, this, image!!)

        }

        findViewById<ImageButton>(R.id.Resize).setOnClickListener{
            if (nowRedactor !is Resize) {
                nowRedactor = Resize()
            }
            if (image != null) nowRedactor.settings(settingsLayout, this, image!!)
        }

        findViewById<ImageButton>(R.id.Grayscale).setOnClickListener{
            if (nowRedactor !is Grayscale) {
                nowRedactor = Grayscale()
            }
            if (image != null) nowRedactor.settings(settingsLayout, this, image!!)
        findViewById<Button>(R.id.faces).setOnClickListener{
            image.setBitMap(FindFaces.drawRectangles(image.getBitmap(), weightsFile))
        }

        findViewById<Button>(R.id.Grayscale).setOnClickListener{
            nowRedactor = Grayscale()
            lifecycleScope.async { nowRedactor.compile(image) }
        }

        findViewById<ImageButton>(R.id.Rotation).setOnClickListener{
            if (nowRedactor !is Rotation) {
                nowRedactor = Rotation()
            }
            if (image != null) nowRedactor.settings(settingsLayout, this, image!!)

        }

        imageView.setOnTouchListener { _: View, m: MotionEvent ->
            retouch.onTouchEvent(m)
        }

        findViewById<ImageButton>(R.id.retouch).setOnClickListener {
            if (!retouchFlag){
                retouch.use(true)
                retouchFlag = true
                if (image != null) retouch.settings(settingsLayout, this, image!!)
            }
            else{
                settingsLayout.removeAllViews()
                retouchFlag = false
            }
        }

        saveBtn.setOnClickListener{

                image!!.save(this)
        }

        loadBtn.setOnClickListener{
            selectImageIntent.launch("image/*")
        }

        findViewById<ImageButton>(R.id.escapeBtn).setOnClickListener {
            val intent = Intent(this, MenuActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP)
            startActivity(intent)
        }
    }

    @RequiresApi(Build.VERSION_CODES.P)
    fun onImageGet(imgUri: Uri?){
        if (imgUri != null){
            val srcBitmap = ImageDecoder.decodeBitmap(
                ImageDecoder.createSource(this.contentResolver, imgUri)
            ) { decoder, _, _ ->
                decoder.allocator = ImageDecoder.ALLOCATOR_SOFTWARE
                decoder.isMutableRequired = true
            }
            image = Image(srcBitmap, "Result", imageView)
            retouch = Retouch(this.applicationContext, image!!)
            retouchFlag = false
        }
    }
}