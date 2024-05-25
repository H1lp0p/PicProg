package spline

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.widget.CheckBox
import com.example.picprog.R

class Spline(context: Context?) : View(context) {
    private var polygon: Boolean = false
    private var spline: Boolean = false
    private var points: MutableList<Pair<Float, Float>> = mutableListOf()
    private var linePaint = Paint().apply {
        strokeWidth = 5f
        style = Paint.Style.STROKE
        color = resources.getColor(R.color.spline_line)
    }
    private var circlePaint = Paint().apply {
        color = resources.getColor(R.color.spline_point)
        style = Paint.Style.FILL
    }

    private val paint = Paint().apply {
        color = resources.getColor(R.color.spline)
        isAntiAlias = true
        strokeWidth = 10f
        style = Paint.Style.STROKE
        strokeJoin = Paint.Join.ROUND
        strokeCap = Paint.Cap.ROUND
    }


    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent): Boolean {

        when (event.action) {
            MotionEvent.ACTION_UP -> {
                val touchX = event.x
                val touchY = event.y
                points.add(Pair(touchX, touchY))
                invalidate()
            }
        }
        return true
    }
    fun polygonCheck(flag: Boolean){
        this.polygon = flag
        invalidate()
    }
    fun splineCheck(flag: Boolean){
        this.spline = flag
        invalidate()
    }

    fun clearButton(){
        points.clear()
        invalidate()
    }


    // from wiki
    private fun catmullRom(p0: Float, p1: Float, p2: Float, p3: Float, t: Float): Float {
        return 0.5f * ((2 * p1) + (-p0 + p2) * t + (2 * p0 - 5 * p1 + 4 * p2 - p3) * t * t +
                (-p0 + 3 * p1 - 3 * p2 + p3) * t * t * t)
    }


    override fun onDraw(canvas: Canvas) {
        // draw points
        for (i in 0 until points.size) {
            canvas.drawCircle(points[i].first, points[i].second, 10f, circlePaint)
        }


        // draw lines
        for (i in 1 until points.size) {
            val (prevX, prevY) = points[i - 1]
            val (currX, currY) = points[i]
            canvas.drawLine(prevX, prevY, currX, currY, linePaint)
        }


        // draw line for polygon
        if (polygon && points.size > 1)
            canvas.drawLine(points[0].first, points[0].second, points[points.size -1].first, points[points.size -1].second, linePaint)

        // draw spline
        if (spline){
            for (i in 0 until points.size - 1) {
                val k0 = if (polygon)points.size - 1 else 0
                val k1 = if (polygon) 0 else i + 1

                val p0 = if (i == 0) points[k0] else points[i - 1] // 0
                val p1 = points[i]
                val p2 = points[i + 1]
                val p3 = if (i + 2 < points.size) points[i + 2] else points[k1] // p2

                var t = 0f
                while (t <= 1f) {
                    val x = catmullRom(p0.first, p1.first, p2.first, p3.first, t)
                    val y = catmullRom(p0.second, p1.second, p2.second, p3.second, t)

                    canvas.drawPoint(x, y, paint)
                    t += 0.01f
                }
            }


            // draw spline for polygon
            if (polygon && points.size > 2) {
                val p3 = points[points.size - 2]
                val p2 = points[points.size - 1]
                val p1 = points[0]
                val p0 = points[1]

                var t = 0f
                while (t <= 1f) {
                    val x = catmullRom(p0.first, p1.first, p2.first, p3.first, t)
                    val y = catmullRom(p0.second, p1.second, p2.second, p3.second, t)

                    canvas.drawPoint(x, y, paint)
                    t += 0.01f
                }
            }
        }

    }
}