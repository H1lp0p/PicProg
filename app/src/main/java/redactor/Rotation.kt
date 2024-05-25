package redactor
import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Color
import android.os.Build
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
import kotlin.math.*
class Rotation : Redactor() {

    private var angle : Double = 45.0

    private fun rotateImage(source: Bitmap): Bitmap {
        val radians = Math.toRadians(angle)
        val sin = sin(radians)
        val cos = cos(radians)
        val newWidth = (source.width * abs(cos) + source.height * abs(sin)).toInt()
        val newHeight = (source.height * abs(cos) + source.width * abs(sin)).toInt()
        val rotatedBitmap = Bitmap.createBitmap(newWidth, newHeight, source.config)

        val x0 = 0.5 * (source.width - 1)     // point to rotate about
        val y0 = 0.5 * (source.height - 1)    // center of image

        for (x in 0 until newWidth) {
            for (y in 0 until newHeight) {
                val a = x - newWidth * 0.5
                val b = y - newHeight * 0.5
                val xx = (a * cos + b * sin + x0).toFloat()
                val yy = (-a * sin + b * cos + y0).toFloat()
                val color = this.bilinearInterpolate(source, xx, yy)
                rotatedBitmap.setPixel(x, y, color)
            }
        }
        return rotatedBitmap
    }

    private fun bilinearInterpolate(source: Bitmap, x: Float, y: Float): Int {
        val x1 = floor(x).toInt()
        val y1 = floor(y).toInt()

        val x2 = ceil(x).toInt()
        val y2 = ceil(y).toInt()

        val pix11 = getPixelColor(source, x1, y1)
        val pix12 = getPixelColor(source, x1, y2)
        val pix21 = getPixelColor(source, x2, y1)
        val pix22 = getPixelColor(source, x2, y2)

        val kx = x - x1
        val ky = y - y1

        val res1 = interpolate(pix11, pix21, kx)
        val res2 = interpolate(pix12, pix22, kx)

        return interpolate(res1, res2, ky)
    }

    private fun getPixelColor(source: Bitmap, x: Int, y: Int): Int {
        if (x < 0 || x >= source.width || y < 0 || y >= source.height) {
            return 0 // default color
        }
        return source.getPixel(x, y)
    }

    private fun interpolate(pix1: Int, pix2: Int, k: Float): Int {
        val a = ((1 - k) * Color.alpha(pix1) + k * Color.alpha(pix2)).toInt()
        val r = ((1 - k) * Color.red(pix1) + k * Color.red(pix2)).toInt()
        val g = ((1 - k) * Color.green(pix1) + k * Color.green(pix2)).toInt()
        val b = ((1 - k) * Color.blue(pix1) + k * Color.blue(pix2)).toInt()
        return Color.argb(a, r, g, b)
    }
    /*private fun rotateImage(source: Bitmap): Bitmap {
        val width = source.width
        val height = source.height
        val newWidth = (width * abs(cos(Math.toRadians(angle))) +
                        height * abs(sin(Math.toRadians(angle)))).toInt()
        val newHeight = (height * abs(cos(Math.toRadians(angle))) +
                        width * abs(sin(Math.toRadians(angle)))).toInt()

        val rotatedBitmap = Bitmap.createBitmap(newWidth, newHeight, source.config)

        val centerX = width / 2.0
        val centerY = height / 2.0

        val newCenterX = newWidth / 2.0
        val newCenterY = newHeight / 2.0

        for (x in 0 until width) {
            for (y in 0 until height) {
                val deltaX = x - centerX
                val deltaY = y - centerY

                val newX = (deltaX * cos(Math.toRadians(angle)) -
                            deltaY * sin(Math.toRadians(angle)) +
                            newCenterX).toInt()
                val newY = (deltaX * sin(Math.toRadians(angle)) + deltaY * cos(Math.toRadians(angle)) + newCenterY).toInt()

                if (newX in 0..<newWidth && newY >= 0 && newY < newHeight) {
                    rotatedBitmap.setPixel(newX, newY, source.getPixel(x, y))
                }
            }
        }

        return rotatedBitmap
    }*/

    override suspend fun compile(source: Image) {
        /*val prevBitmap = source.getBitmap()
        var bInput = source.getBitmap()
        val bOutput: Bitmap
        val degrees = 45f

        val matrix = Matrix()
        matrix.postRotate(degrees)
        bOutput = Bitmap.createBitmap(bInput, 0, 0, bInput.getWidth(), bInput.getHeight(), matrix, true)
        Log.i("Rotate", "done")
        source.setBitMap(bOutput)*/

        source.setBitMap(rotateImage(source.getBitmap()))
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    @RequiresApi(Build.VERSION_CODES.O)
    override fun settings(layout: LinearLayout, context: Context, image: Image) {
        layout.removeAllViews()
        layout.orientation = LinearLayout.HORIZONTAL

        val radiusText = TextView(context).apply {
            text = context.getString(R.string.settings_rotation_angle, angle.toInt())
        }
        val seekBar = SeekBar(context).apply {
            min = -180
            max = 180

        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            seekBar.minWidth = 400
        }

        seekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener{
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                angle = progress.toDouble()
                radiusText.text = context.getString(R.string.settings_rotation_angle, progress)
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