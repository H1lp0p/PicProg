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
class Retouch(context: Context, source: Image) : View(context) {
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
    private val wCoef: Int
    private val hCoef : Int

    init{
        bitmap = source.getBitmap()
        width = bitmap.width
        height = bitmap.height
        wCoef = if (width > getWidth()) (width / (getWidth()+1)) else 1
        hCoef = if (height > getHeight()) (height / (getHeight()+1)) else 1
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

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent): Boolean {
        when (event.action) {
            MotionEvent.ACTION_DOWN, MotionEvent.ACTION_MOVE -> {

                // Получаем координаты касания
                val touchX = event.x * wCoef        //this shit uses coordinates from view but drawing on image's coordinates. that's why it shifts on some images
                val touchY = event.y * hCoef
                Log.i("[Ret]x, y ->", "[$touchX, $touchY, $wCoef, $hCoef, ${getWidth()}, ${getHeight()}]")
                // Применяем эффект ретуши
                retouch(touchX, touchY)
                /*applyRetouchEffect(touchX, touchY)*/

                // Перерисовываем view
                invalidate()    //actually, with this line our code must update image, but it doesn't
            }
        }
        return true
    }

    private fun retouch(x: Float, y: Float) {
        /*Log.i("[Ret]x, y ->", "[$x, $y]")*/
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