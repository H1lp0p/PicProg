package cubeRender

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Point
import android.graphics.drawable.BitmapDrawable
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.core.graphics.drawable.toBitmap

@SuppressLint("ClickableViewAccessibility")
class Cube(context: Context) : View(context) {

    private val coef = 100

    private val distance = 100

    private var rotateVector : MutableList<Point> = mutableListOf()

    private var vertices : MutableList<MutableList<Float>> = mutableListOf(
        mutableListOf(-1f,-1f,0f), mutableListOf(1f,1f,0f), mutableListOf(-1f,1f,0f), mutableListOf(1f,-1f,0f),
        mutableListOf(-1f,-1f,1f), mutableListOf(1f,1f,1f), mutableListOf(-1f,1f,1f), mutableListOf(1f,-1f,1f)
    )

    private val verticesPaint = Paint().apply {
        style = Paint.Style.STROKE
        strokeWidth = 10f
        color = Color.argb(255, 0,0,0)
    }
    private val vectorPaint = Paint().apply {
        style = Paint.Style.STROKE
        strokeWidth = 10f
        color = Color.argb(255, 255, 0,0)
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        val center = listOf(width / 2, height / 2)
        for (point in vertices){
            canvas.drawPoint(center[0]+point[0] * coef,
                center[1] + point[1] * coef, verticesPaint)
        }
        if (rotateVector.size == 2){
            val begin = rotateVector[0]
            val end = rotateVector[1]

            canvas.drawPoint(begin.x.toFloat(), begin.y.toFloat(), vectorPaint)
            canvas.drawPoint(end.x.toFloat(), end.y.toFloat(), vectorPaint)
            canvas.drawLine(begin.x.toFloat(), begin.y.toFloat(),
                end.x.toFloat(), end.y.toFloat(), vectorPaint)
        }
    }

    private fun rotate() {
        val vectorX = rotateVector[1].x - rotateVector[0].x
        val vectorY = rotateVector[1].y - rotateVector[0].y

        
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent): Boolean {
        when(event.action){
            MotionEvent.ACTION_DOWN ->{
                rotateVector.add(Point(event.x.toInt(), event.y.toInt()))
                rotateVector.add(Point(event.x.toInt(), event.y.toInt()))
            }
            MotionEvent.ACTION_MOVE ->{
                rotateVector[1] = Point(event.x.toInt(), event.y.toInt())

            }
            MotionEvent.ACTION_UP -> {
                rotateVector.clear()
            }
        }
        invalidate()
        return true
    }

}