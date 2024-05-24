package com.example.picprog

import android.os.Bundle
import android.widget.ImageButton
import android.widget.ImageView
import androidx.activity.ComponentActivity

class CubeActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cube_page)

        val imgView = findViewById<ImageView>(R.id.imgView)
        val backBtn = findViewById<ImageButton>(R.id.backBtn)


    }
}