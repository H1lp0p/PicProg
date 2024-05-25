package com.example.picprog

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.CheckBox
import android.widget.ImageButton
import android.widget.LinearLayout
import androidx.activity.ComponentActivity
import spline.Spline


class SplineActivity : ComponentActivity() {

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {


        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_spline_page)
        val lay1 = findViewById<View>(R.id.layout) as LinearLayout
        val splineView = Spline(this)
        lay1.addView(splineView)

        findViewById<CheckBox>(R.id.checkBox2).setOnCheckedChangeListener { _, isChecked ->
            splineView.polygonCheck(isChecked)
        }

        findViewById<CheckBox>(R.id.checkBox3).setOnCheckedChangeListener { _, isChecked ->
            splineView.splineCheck(isChecked)
        }

        findViewById<ImageButton>(R.id.clearButton).setOnClickListener { splineView.clearButton() }

        findViewById<ImageButton>(R.id.backButton).setOnClickListener {
            val intent = Intent(this, MenuActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP)
            startActivity(intent)
        }
    }
}