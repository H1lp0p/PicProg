package com.example.picprog

import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.ImageButton
import android.widget.ImageView
import androidx.activity.ComponentActivity
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import image.Image
import redactor.GausBlur
import redactor.Grayscale
import redactor.Mosaic
import redactor.Redactor
import redactor.Resize
import redactor.Rotation
import java.io.File
import java.io.FileOutputStream
import java.io.FileWriter
import java.io.IOException
import java.security.AccessController.getContext
import java.security.Permission


class MainActivity : ComponentActivity() {
    lateinit var loadBtn : ImageButton
    lateinit var imageView : ImageView
    lateinit var saveBtn : ImageButton
    lateinit var image: Image

    var nowRedactor: Redactor = GausBlur()

    @RequiresApi(Build.VERSION_CODES.P)
    val selectImageIntent = registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        onImageGet(uri)
        saveBtn.visibility = View.VISIBLE
    }

    @RequiresApi(Build.VERSION_CODES.P)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main_page)

        loadBtn = findViewById(R.id.loadBtn)
        imageView = findViewById(R.id.img)
        saveBtn = findViewById(R.id.saveBtn)

        findViewById<Button>(R.id.GausBlur).setOnClickListener{
            nowRedactor = GausBlur()
            nowRedactor.compile(image)
        }

        findViewById<Button>(R.id.Mosaic).setOnClickListener{
            nowRedactor = Mosaic()
            nowRedactor.compile(image)

        }

        findViewById<Button>(R.id.Resize).setOnClickListener{
            nowRedactor = Resize()
            nowRedactor.compile(image)
        }

        findViewById<Button>(R.id.Grayscale).setOnClickListener{
            nowRedactor = Grayscale()
            nowRedactor.compile(image)
        }

        findViewById<Button>(R.id.Rotation).setOnClickListener{
            nowRedactor = Rotation()
            nowRedactor.compile(image)

        }

        saveBtn.setOnClickListener{
                image.save()
        }

        loadBtn.setOnClickListener{
            selectImageIntent.launch("image/*")
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
        }
    }
}