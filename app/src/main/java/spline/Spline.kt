package spline

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.Log
import android.view.MotionEvent
import android.view.View

class Spline(context: Context?) : View(context) {
    private var dotCoordinates: MutableList<Pair<Float, Float>> = mutableListOf()
    private var linePaint = Paint().apply {
        strokeWidth = 10f
        style = Paint.Style.STROKE
        color = Color.argb(255, 0, 0, 0)
    }
    private var circlePaint = Paint().apply {
        color = Color.RED
        style = Paint.Style.FILL
    }
    val paint = Paint().apply {
        color = Color.argb(255, 0, 255, 0)
        isAntiAlias = true
        strokeWidth = 20f
        style = Paint.Style.STROKE
        strokeJoin = Paint.Join.ROUND
        strokeCap = Paint.Cap.ROUND
    }


    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent): Boolean {
        when (event.action) {
            MotionEvent.ACTION_DOWN, MotionEvent.ACTION_MOVE -> {
                /*val touchX = event.x
                val touchY = event.y
                Log.i("[Spline]x, y ->", "[$touchX, $touchY]")
                dotCoordinates.add(Pair(touchX, touchY))
                invalidate()*/
            }

            MotionEvent.ACTION_UP -> {
                val touchX = event.x
                val touchY = event.y
                Log.i("[Spline]x, y ->", "[$touchX, $touchY]")
                dotCoordinates.add(Pair(touchX, touchY))
                invalidate()
            }
        }
        return true
    }

    // from wiki
    private fun catmullRom(p0: Float, p1: Float, p2: Float, p3: Float, t: Float): Float {
        val t2 = t * t
        val t3 = t * t * t
        return 0.5f * ((2 * p1) +
                (-p0 + p2) * t +
                (2 * p0 - 5 * p1 + 4 * p2 - p3) * t2 +
                (-p0 + 3 * p1 - 3 * p2 + p3) * t3)
    }

    override fun onDraw(canvas: Canvas) {

        for (i in 1 until dotCoordinates.size) {
            val (prevX, prevY) = dotCoordinates[i - 1]
            val (currX, currY) = dotCoordinates[i]
            canvas.drawLine(prevX, prevY, currX, currY, linePaint)
        }
        for (i in 0 until dotCoordinates.size) {
            canvas.drawCircle(dotCoordinates[i].first, dotCoordinates[i].second, 20f, circlePaint)
        }

        for (i in 0 until dotCoordinates.size - 1) {
            val p0 =
                if (i == 0) dotCoordinates[dotCoordinates.size - 1] else dotCoordinates[i - 1] // 0
            val p1 = dotCoordinates[i]
            val p2 = dotCoordinates[i + 1]
            val p3 =
                if (i + 2 < dotCoordinates.size) dotCoordinates[i + 2] else dotCoordinates[0] // p2

            var t = 0f
            while (t <= 1f) {
                val x = catmullRom(p0.first,p1.first,p2.first,p3.first,t)
                val y = catmullRom(p0.second,p1.second,p2.second,p3.second,t)

                canvas.drawPoint(x, y, paint)
                t += 0.01f
            }
        }
        if (dotCoordinates.size > 2) {
            val p3 = dotCoordinates[dotCoordinates.size - 2]
            val p2 = dotCoordinates[dotCoordinates.size - 1]
            val p1 = dotCoordinates[0]
            val p0 = dotCoordinates[1]


            var t = 0f
            while (t <= 1f) {
                val tt = t * t
                val ttt = t * tt

                val q1 = -ttt + 2 * tt - t
                val q2 = 3 * ttt - 5 * tt + 2
                val q3 = -3 * ttt + 4 * tt + t
                val q4 = ttt - tt

                val x =
                    0.5f * ((p0.first * q1) + (p1.first * q2) + (p2.first * q3) + (p3.first * q4))
                val y =
                    0.5f * ((p0.second * q1) + (p1.second * q2) + (p2.second * q3) + (p3.second * q4))

                canvas.drawPoint(x, y, paint)
                t += 0.01f
            }
        }

    }
}