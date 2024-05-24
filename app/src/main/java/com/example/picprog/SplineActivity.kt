package com.example.picprog

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.View
import android.widget.LinearLayout
import androidx.activity.ComponentActivity
import spline.Spline


class SplineActivity : ComponentActivity() {
    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_spline_page)
        val lay1 = findViewById<View>(R.id.layout) as LinearLayout
        lay1.addView(Spline(this))
    }
}