import android.annotation.SuppressLint
import android.content.Context
import android.graphics.*
import android.util.Log
import android.view.MotionEvent
import android.view.View
import image.Image
import kotlin.math.exp
import kotlin.math.pow


//TODO: well, fit it in our structure???
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
    private var retouchRadius = 100f // Размер кисти
    private var retouchStrength = 0.9f // Коэффициент ретуши
    private val width : Int
    private val height : Int
    private val wCoef: Float
    private val hCoef : Float
    private var flag = false

    init{
        bitmap = source.getBitmap()
        width = bitmap.width
        height = bitmap.height
        wCoef = width.toFloat() / source.width
        hCoef = height.toFloat() / source.height
    }

    fun setBitmap(bmp: Bitmap) {
        bitmap = bmp.copy(Bitmap.Config.ARGB_8888, true)
    }

    fun setBrushSize(size: Float) {
        retouchRadius = size
        paint.strokeWidth = size
    }

    fun setRetouchStrength(strength: Float) {
        retouchStrength = strength
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)


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

    fun use(flag: Boolean){
        this.flag = flag
    }

    private fun retouch(x: Float, y: Float) {
        /*Log.i("[Ret]x, y ->", "[$x, $y]")*/
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
                    val factor = exp(-(((i * i + j * j) / radiusSquared)).toDouble()).toFloat() * retouchStrength
                    val pixelX = (x + i).toInt()
                    val pixelY = (y + j).toInt()
                    if (pixelX in 0 until bitmap.width && pixelY in 0 until bitmap.height) {
                        val pixel = bitmap.getPixel(pixelX, pixelY)
                        val r = Color.red(pixel)
                        val g = Color.green(pixel)
                        val b = Color.blue(pixel)
                        retouchPaint.color = Color.argb(255, (r+(R - r)*factor).toInt(), (g+(G - g)*factor).toInt(), (b+(B - b)*factor).toInt())
                        bitmap.setPixel(pixelX, pixelY, retouchPaint.color)

                       /* retouchPaint.color = Color.argb((255 * factor).toInt(), r, g, b)
                        bitmapCanvas.drawPoint(pixelX.toFloat(), pixelY.toFloat(), retouchPaint)*/
                    }
                }
            }
        }
        bitmap.let { bitmapCanvas.drawBitmap(it, 0f, 0f, null) }
        source.setBitMap(bitmap)
    }
}