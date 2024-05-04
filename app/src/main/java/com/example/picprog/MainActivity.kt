package com.example.picprog

import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import android.widget.ImageButton
import android.widget.ImageView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi

import image.Image
import redactor.Redactor

class MainActivity : ComponentActivity() {
    lateinit var loadBtn : ImageButton
    lateinit var imageView : ImageView
    lateinit var image: Image

    lateinit var nowredactor: Redactor

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

        loadBtn.setOnClickListener{
            selectImageIntent.launch("image/*")
        }

    }
    @RequiresApi(Build.VERSION_CODES.P)
    fun onImageGet(imgUri: Uri?){
        if (imgUri != null){
            val bitMap = ImageDecoder.decodeBitmap(ImageDecoder.createSource(this.contentResolver, imgUri))
            image = Image(bitMap, imageView)
        }
    }
}