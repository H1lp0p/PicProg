import android.annotation.SuppressLint
import android.content.Context
import android.graphics.*
import android.os.Build
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.SeekBar
import android.widget.TextView
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.annotation.RequiresApi
import androidx.lifecycle.lifecycleScope
import com.example.picprog.R
import image.Image
import kotlinx.coroutines.async
import kotlin.math.exp
import kotlin.math.pow


@SuppressLint("ViewConstructor")
class Retouch(context: Context, private var source: Image) : View(context) {
    private var path = Path()
    private var paint = Paint().apply {
        isAntiAlias = true
        isDither = true
        style = Paint.Style.STROKE
        strokeJoin = Paint.Join.ROUND
        strokeCap = Paint.Cap.ROUND
    }
    private var bitmap: Bitmap
    private var retouchRadius = 10f
    private var retouchStrength = 0.1f
    private val width: Int
    private val height: Int
    private val wCoef: Float
    private val hCoef: Float
    private var flag = false

    init {
        bitmap = source.getBitmap()
        width = bitmap.width
        height = bitmap.height
        wCoef = width.toFloat() / source.width
        hCoef = height.toFloat() / source.height
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent): Boolean {
        if (!this.flag) return true
        when (event.action) {
            MotionEvent.ACTION_DOWN, MotionEvent.ACTION_MOVE -> {
                val touchX = event.x * wCoef
                val touchY = event.y * hCoef
                Log.i("[Ret]x, y ->", "[$touchX, $touchY, ${wCoef}, ${hCoef}]")
                // Применяем эффект ретуши
                retouch(touchX, touchY)
            }

            MotionEvent.ACTION_UP -> {
            }
        }
        return true
    }

    fun use(flag: Boolean) {
        this.flag = flag
    }

    private fun retouch(x: Float, y: Float) {
        val radiusSquared = retouchRadius.pow(2)
        val bitmapCanvas = Canvas(bitmap)
        val retouchPaint = Paint()

        var R = 0
        var G = 0
        var B = 0
        var count = 0

        for (i in -retouchRadius.toInt()..retouchRadius.toInt()) {
            for (j in -retouchRadius.toInt()..retouchRadius.toInt()) {
                if (i * i + j * j <= radiusSquared) {
                    val pixelX = (x + i).toInt()
                    val pixelY = (y + j).toInt()
                    if (pixelX in 0 until bitmap.width && pixelY in 0 until bitmap.height) {
                        val pixel = bitmap.getPixel(pixelX, pixelY)
                        R += Color.red(pixel)
                        G += Color.green(pixel)
                        B += Color.blue(pixel)
                        count += 1
                    }
                }
            }
        }
        R /= count
        G /= count
        B /= count
        for (i in -retouchRadius.toInt()..retouchRadius.toInt()) {
            for (j in -retouchRadius.toInt()..retouchRadius.toInt()) {
                if (i * i + j * j <= radiusSquared) {
                    val factor =
                        exp(-(((i * i + j * j) / radiusSquared)).toDouble()).toFloat() * retouchStrength
                    val pixelX = (x + i).toInt()
                    val pixelY = (y + j).toInt()
                    if (pixelX in 0 until bitmap.width && pixelY in 0 until bitmap.height) {
                        val pixel = bitmap.getPixel(pixelX, pixelY)
                        val r = Color.red(pixel)
                        val g = Color.green(pixel)
                        val b = Color.blue(pixel)
                        retouchPaint.color = Color.argb(
                            255,
                            (r + (R - r) * factor).toInt(),
                            (g + (G - g) * factor).toInt(),
                            (b + (B - b) * factor).toInt()
                        )
                        bitmap.setPixel(pixelX, pixelY, retouchPaint.color)

                    }
                }
            }
        }
        source.setBitMap(bitmap)
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    @RequiresApi(Build.VERSION_CODES.O)
    fun settings(layout: LinearLayout, context: Context, image: Image) {
        layout.removeAllViews()
        layout.orientation = LinearLayout.HORIZONTAL


        val seekBarSettingLayout = LinearLayout(context)
        seekBarSettingLayout.orientation = LinearLayout.VERTICAL

        val radiusText = TextView(context).apply {
            text = context.getString(R.string.settings_retouch_radius, retouchRadius.toInt())
        }
        val seekBarRadius = SeekBar(context).apply {
            min = 10
            max = 100

        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            seekBarRadius.minWidth = 400
        }

        seekBarRadius.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                retouchRadius = progress.toFloat()
                radiusText.text = context.getString(R.string.settings_retouch_radius, progress)
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {
            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {
            }
        })

        val strengthText = TextView(context).apply {
            text = context.getString(
                R.string.settings_retouch_strength,
                (retouchStrength * 10).toInt()
            )
        }
        val seekBarSrength = SeekBar(context).apply {
            min = 1
            max = 10
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            seekBarSrength.minWidth = 400
        }
        seekBarSrength.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                retouchStrength = progress.toFloat() / 10
                strengthText.text = context.getString(R.string.settings_retouch_strength, progress)
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {
            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {
            }
        })


        val revertBtn = ImageButton(context)
        revertBtn.setImageDrawable(context.getDrawable(R.drawable.clear_icon))
        revertBtn.background = null

        revertBtn.setOnClickListener {
            image.revert()
        }


        seekBarSettingLayout.addView(radiusText)
        seekBarSettingLayout.addView(seekBarRadius)
        seekBarSettingLayout.addView(strengthText)
        seekBarSettingLayout.addView(seekBarSrength)

        layout.addView(seekBarSettingLayout)
        layout.addView(revertBtn)
    }
}