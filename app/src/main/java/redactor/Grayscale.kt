package redactor

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Color
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.lifecycle.lifecycleScope
import com.example.picprog.R
import image.Image
import kotlinx.coroutines.async

class Grayscale : Redactor() {
    // no settings

    private fun grayscale(source: Image): Bitmap {
        val srcBitmap = source.getBitmap()
        var grey: Int

        for (y in 0 until srcBitmap.height) {
            for (x in 0 until srcBitmap.width) {
                val prevPix = srcBitmap.getPixel(x, y)
                grey = (Color.red(prevPix) + Color.blue(prevPix) + Color.green(prevPix)) / 3
                srcBitmap.setPixel(x, y, Color.argb(Color.alpha(prevPix), grey, grey, grey))
            }
        }

        return srcBitmap
    }

    override suspend fun compile(source: Image) {
        source.setBitMap(grayscale(source))
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    override fun settings(layout: LinearLayout, context: Context, image: Image) {
        layout.removeAllViews()
        layout.orientation = LinearLayout.HORIZONTAL
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

        layout.addView(compileBtn)
        layout.addView(revertBtn)
    }
}