package redactor

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Color
import android.os.Build
import android.util.Log
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.SeekBar
import android.widget.TextView
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.annotation.RequiresApi
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.lifecycle.lifecycleScope
import com.example.picprog.R
import image.Image
import kotlinx.coroutines.async


class Mosaic : Redactor() {

    private var px: Int = 10

    private fun mosaic(source: Image): Bitmap {
        val srcBitmap = source.getBitmap()

        var y = 0
        while (y < srcBitmap.height) {
            var x = 0
            while (x < srcBitmap.width) {
                val pxH: Int = if (px <= srcBitmap.height - y) px else srcBitmap.height - y
                val pxW: Int = if (px <= srcBitmap.width - x) px else srcBitmap.width - x

                var alpha = 0
                var red = 0
                var green = 0
                var blue = 0
                var pixelColor: Int

                for (i in 0 until pxH) {
                    for (j in 0 until pxW) {
                        pixelColor = srcBitmap.getPixel((x + j), (y + i))
                        alpha += Color.alpha(pixelColor)
                        red += Color.red(pixelColor)
                        green += Color.green(pixelColor)
                        blue += Color.blue(pixelColor)
                    }
                }
                alpha /= (pxW * pxH)
                red /= (pxW * pxH)
                green /= (pxW * pxH)
                blue /= (pxW * pxH)
                val newPixel: Int = Color.argb(alpha, red, green, blue)

                val newPixels = IntArray(pxH * pxW)
                for (i in 0 until pxH * pxW)
                    newPixels[i] = newPixel

                srcBitmap.setPixels(newPixels, 0, pxW, x, y, pxW, pxH)

                x += px
            }
            y += px
        }
        /*val a = Color.alpha(srcBitmap.getPixel(0, 0))
        Log.i("Mosaic", "Mosaic.check - alpha = $a")*/
        return srcBitmap
    }

    override suspend fun compile(source: Image) {
        val newBitmap = mosaic(source)
        source.setBitMap(newBitmap)
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    @RequiresApi(Build.VERSION_CODES.O)
    override fun settings(layout: LinearLayout, context: Context, image: Image) {
        layout.removeAllViews()
        layout.orientation = LinearLayout.HORIZONTAL

        val radiusText = TextView(context).apply {
            text = context.getString(R.string.settings_mosaic_px, px)
        }
        val seekBar = SeekBar(context).apply {
            min = 1
            max = 6

        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            seekBar.minWidth = 400
        }

        seekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                px = progress * 10
                radiusText.text = context.getString(R.string.settings_mosaic_px, px)
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {
            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {
            }
        })

        val compileBtn = ImageButton(context)
        compileBtn.setImageDrawable(context.getDrawable(R.drawable.compile_ico))
        compileBtn.background = null
        compileBtn.setOnClickListener {
            ((context) as ComponentActivity).lifecycleScope.async {
                Toast.makeText(
                    context,
                    context.getText(R.string.system_filter_compiling),
                    Toast.LENGTH_LONG
                ).show()
                compile(image)
                Toast.makeText(
                    context,
                    context.getText(R.string.system_filter_complete),
                    Toast.LENGTH_SHORT
                ).show()
            }
        }

        val revertBtn = ImageButton(context)
        revertBtn.setImageDrawable(context.getDrawable(R.drawable.clear_icon))
        revertBtn.background = null

        revertBtn.setOnClickListener {
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