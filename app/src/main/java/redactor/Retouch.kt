import android.content.Context
import android.graphics.*
import android.util.Log
import android.view.MotionEvent
import android.view.View
import image.Image
import kotlin.math.exp
import kotlin.math.pow

class Retouch(context: Context, var source: Image) : View(context) {
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

    init{
        bitmap = source.getBitmap()
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
        bitmap.let { canvas.drawBitmap(it, 0f, 0f, null) }
    }

/*    override fun onTouchEvent(event: MotionEvent): Boolean {
        val x = event.x
        val y = event.y

        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                path.moveTo(x, y)
                retouch(x, y)
                return true
            }
            MotionEvent.ACTION_MOVE -> {
                path.lineTo(x, y)
                retouch(x, y)
                invalidate()
            }
            MotionEvent.ACTION_UP -> {
                path.reset()
            }
        }
        return true
    }*/

    override fun onTouchEvent(event: MotionEvent): Boolean {
        when (event.action) {
            MotionEvent.ACTION_DOWN, MotionEvent.ACTION_MOVE -> {
                // Получаем координаты касания
                val touchX = event.x
                val touchY = event.y

                // Применяем эффект ретуши
                retouch(touchX, touchY)
                /*applyRetouchEffect(touchX, touchY)*/

                // Перерисовываем view
                invalidate()
            }
        }
        return true
    }

    fun retouch(x: Float, y: Float) {
        Log.i("[Ret]x, y ->", "[$x, $y]")
        val radiusSquared = retouchRadius.pow(2)
        val bitmapCanvas = Canvas(bitmap)
        val retouchPaint = Paint()

        for (i in -retouchRadius.toInt()..retouchRadius.toInt()) {
            for (j in -retouchRadius.toInt()..retouchRadius.toInt()) {
                if (i * i + j * j <= radiusSquared) {
                    val factor = exp(-((i * i + j * j) / radiusSquared).toDouble()).toFloat() * retouchStrength
                    val pixelX = (x + i).toInt()
                    val pixelY = (y + j).toInt()
                    if (pixelX in 0 until bitmap.width && pixelY in 0 until bitmap.height) {
                        val pixel = bitmap.getPixel(pixelX, pixelY)
                        val r = Color.red(pixel)
                        val g = Color.green(pixel)
                        val b = Color.blue(pixel)
                        retouchPaint.color = Color.argb((255 * factor).toInt(), r, g, b)
                        bitmapCanvas.drawPoint(pixelX.toFloat(), pixelY.toFloat(), retouchPaint)
                    }
                }
            }
        }
    }
}