package redactor

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.os.Build
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.SeekBar
import android.widget.TextView
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.graphics.alpha
import androidx.core.graphics.blue
import androidx.core.graphics.green
import androidx.core.graphics.red
import androidx.lifecycle.lifecycleScope
import com.example.picprog.R
import image.Image
import kotlin.math.exp
import kotlin.math.pow
import kotlin.math.sqrt
import kotlinx.coroutines.*

class GausBlur : Redactor() {

    private var radius : Int = 3

    private val sigma = 3.0
    private val sigmaSquared = sigma.pow(2.0)
    private val mu = 0.0
    private val constK = 1.0 / (sqrt(2 * Math.PI) * sigma)

    private val xMinusMu = {x: Double -> (x - mu).pow(2.0) }
    private val expOfX = {x : Double -> exp(-xMinusMu(x) / (2 * sigmaSquared)) }
    private val gausina = {x: Double -> constK * expOfX(x)}

    private fun extrapolite(source: Image) : Bitmap{
        var srcBitmap = source.getBitmap()
        var extrBitmap = Bitmap.createBitmap(srcBitmap.width + this.radius - 1,
            srcBitmap.height + this.radius - 1, srcBitmap.config)

        var srcPixels: IntArray = IntArray(srcBitmap.width * srcBitmap.height)
        srcBitmap.getPixels(srcPixels, 0, srcBitmap.width, 0, 0, srcBitmap.width, srcBitmap.height)
        extrBitmap.setPixels(srcPixels, 0, srcBitmap.width, this.radius / 2, this.radius / 2, srcBitmap.width, srcBitmap.height)

        var topLine = srcPixels.copyOf(srcBitmap.width)
        var bottomLine = srcPixels.sliceArray(IntRange(srcPixels.size - srcBitmap.width, srcPixels.size - 1))
        for (y in IntRange(0, this.radius / 2)){
            extrBitmap.setPixels(topLine, 0, srcBitmap.width, this.radius / 2, y, srcBitmap.width, 1)
            extrBitmap.setPixels(bottomLine, 0, srcBitmap.width, this.radius / 2, extrBitmap.height - y - 1, srcBitmap.width, 1)
        }

        var leftCol = IntArray(extrBitmap.height)
        var rightCol = IntArray(extrBitmap.height)

        extrBitmap.getPixels(leftCol, 0, 1, this.radius / 2 + 1, 0, 1, extrBitmap.height)
        extrBitmap.getPixels(rightCol, 0, 1, extrBitmap.width - this.radius / 2 - 1, 0, 1, extrBitmap.height)

        for (x in IntRange(0, this.radius / 2)){
            extrBitmap.setPixels(leftCol, 0, 1, x, 0, 1, extrBitmap.height)
            extrBitmap.setPixels(rightCol, 0, 1, extrBitmap.width - x - 1, 0, 1, extrBitmap.height)
        }

        return extrBitmap
    }

    private fun setMatrix() : MutableList<Double>{

        var res = emptyList<Double>().toMutableList()
        var halfRadius = this.radius / 2
        var sum = 0.0

        for (y in IntRange(-halfRadius, halfRadius)){
            for (x in IntRange(-halfRadius, halfRadius)){
                val value = gausina(sqrt(((x*x) + (y*y)).toDouble()))
                sum += value
                res += value
            }
        }

        res = res.map { it / sum }.toMutableList()

        return res;
    }

    override suspend fun compile(source: Image) {
        val srcBitmap = source.getBitmap()
        val extrBitmap = extrapolite(source)
        val matrix = setMatrix()

        val halfRadius = this.radius / 2


            for (y in IntRange(halfRadius, srcBitmap.height + halfRadius - 1)) {
                for (x in IntRange(halfRadius, srcBitmap.width + halfRadius - 1)) {

                    GlobalScope.async {
                        var sumR = 0.0
                        var sumG = 0.0
                        var sumB = 0.0
                        val alpha = srcBitmap.getPixel(x - halfRadius, y - halfRadius).alpha
                        for (matY in IntRange(-halfRadius, halfRadius)) {
                            for (matX in IntRange(-halfRadius, halfRadius)) {
                                val matInd = (matY + halfRadius) * radius + matX + halfRadius;

                                val extrPixel = extrBitmap.getPixel(x + matX, y + matY)

                                sumR += extrPixel.red * matrix[matInd]
                                sumG += extrPixel.green * matrix[matInd]
                                sumB += extrPixel.blue * matrix[matInd]
                            }
                        }
                        val newColor = Color.argb(alpha, sumR.toInt(), sumG.toInt(), sumB.toInt())

                        srcBitmap.setPixel(x - halfRadius, y - halfRadius, newColor)
                    }

                }
            }
        source.setBitMap(srcBitmap)
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    @RequiresApi(Build.VERSION_CODES.O)
    override fun settings(layout: LinearLayout, context: Context, image: Image) {
        layout.removeAllViews()
        layout.orientation = LinearLayout.HORIZONTAL

        val radiusText = TextView(context).apply {
            text = context.getString(R.string.settings_gaus_radius, radius)
        }
        val seekBar = SeekBar(context).apply {
            min = 1
            max = 6

        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            seekBar.minWidth = 400
        }

        seekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener{
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                radius = progress * 2 + 1
                radiusText.text = context.getString(R.string.settings_gaus_radius, radius)
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {
            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {
            }
        })

        val compileBtn = ImageButton(context)
        compileBtn.setImageDrawable(context.getDrawable(R.drawable.compile_ico))
        compileBtn.background = null
        compileBtn.setOnClickListener{
            ((context)as ComponentActivity).lifecycleScope.async {
                Toast.makeText(context, context.getText(R.string.system_filter_compiling), Toast.LENGTH_LONG).show()
                compile(image)
                Toast.makeText(context, context.getText(R.string.system_filter_complete), Toast.LENGTH_SHORT).show()
            }
        }

        val revertBtn = ImageButton(context)
        revertBtn.setImageDrawable(context.getDrawable(R.drawable.clear_icon))
        revertBtn.background = null

        revertBtn.setOnClickListener{
            image.revert()
        }

        val radiusSettingLayout = LinearLayout(context)
        radiusSettingLayout.orientation = LinearLayout.VERTICAL

        radiusSettingLayout.addView(radiusText)
        radiusSettingLayout.addView(seekBar)

        layout.addView(radiusSettingLayout)
        layout.addView(compileBtn)
        layout.addView(revertBtn)

    }
}