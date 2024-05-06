package com.example.picprog

import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.widget.Button
import androidx.activity.ComponentActivity
import android.widget.ImageButton
import android.widget.ImageView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi

import image.Image
import redactor.GausBlur
import redactor.Mosaic
import redactor.Redactor

class MainActivity : ComponentActivity() {
    lateinit var loadBtn : ImageButton
    lateinit var imageView : ImageView
    lateinit var image: Image

    var nowRedactor: Redactor = GausBlur()

    @RequiresApi(Build.VERSION_CODES.P)
    val selectImageIntent = registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        onImageGet(uri)
    }

    @RequiresApi(Build.VERSION_CODES.P)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main_page)

        loadBtn = findViewById(R.id.loadBtn)
        imageView = findViewById(R.id.img)

        findViewById<Button>(R.id.GausBlur).setOnClickListener{
            nowRedactor.compile(image)
        }

        findViewById<Button>(R.id.Mosaic).setOnClickListener{
            nowRedactor = Mosaic()
            nowRedactor.compile(image)

        }

        loadBtn.setOnClickListener{
            selectImageIntent.launch("image/*")
        }

    }
    @RequiresApi(Build.VERSION_CODES.P)
    fun onImageGet(imgUri: Uri?){
        if (imgUri != null){
            val bitMap = ImageDecoder.decodeBitmap(ImageDecoder.createSource(this.contentResolver, imgUri),
                ImageDecoder.OnHeaderDecodedListener { decoder, info, source ->
                    decoder.allocator = ImageDecoder.ALLOCATOR_SOFTWARE
                    decoder.isMutableRequired = true
                })
            image = Image(bitMap, imageView)
        }
    }
}