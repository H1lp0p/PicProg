package com.example.picprog

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.os.Bundle
import android.view.View
import androidx.activity.ComponentActivity
import redactor.*


class SplineActivity : ComponentActivity() {
//    @RequiresApi(Build.VERSION_CODES.P)
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        setContentView(R.layout.activity_spline_page)
//    }
//
//
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(DrawView(this))
    }


    internal class DrawView(context: Context?) : View(context) {
        override fun onDraw(canvas: Canvas) {
            canvas.drawColor(Color.GREEN)
        }
    }

}