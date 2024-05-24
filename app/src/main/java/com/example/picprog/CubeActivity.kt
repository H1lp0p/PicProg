package com.example.picprog

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.MotionEvent
import android.view.View
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.activity.ComponentActivity
import androidx.constraintlayout.widget.ConstraintSet.Layout
import cubeRender.Cube

class CubeActivity : ComponentActivity() {

    @SuppressLint("ClickableViewAccessibility")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cube_page)

        val layout = findViewById<LinearLayout>(R.id.layout)
        val backBtn = findViewById<ImageButton>(R.id.backBtn)


        layout.addView(Cube(this, resources))

       /* imgView.setOnTouchListener(View.OnTouchListener{ _: View, m: MotionEvent ->
            cube.onTouchEvent(m)
        })*/
    }
}