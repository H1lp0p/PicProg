package cubeRender

import android.annotation.SuppressLint
import android.content.Context
import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Path
import android.graphics.Point
import android.graphics.drawable.BitmapDrawable
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.core.graphics.drawable.toBitmap
import com.example.picprog.R
import kotlin.math.atan
import kotlin.math.cos
import kotlin.math.sin

@SuppressLint("ClickableViewAccessibility")
class Cube(context: Context, res: Resources) : View(context) {

    private val coef = 200

    private val distance = 10000
    private val speed = 0.5f

    private var rotateVector: MutableList<Point> = mutableListOf()

    private var vertices: MutableList<MutableList<Float>> = mutableListOf(
        mutableListOf(-1f, -1f, -1f),
        mutableListOf(1f, 1f, -1f),
        mutableListOf(-1f, 1f, -1f),
        mutableListOf(1f, -1f, -1f),
        mutableListOf(-1f, -1f, 1f),
        mutableListOf(1f, 1f, 1f),
        mutableListOf(-1f, 1f, 1f),
        mutableListOf(1f, -1f, 1f)
    )

    private val sides = (1..2).map {
        var point = vertices[0]
        if (it == 2) {
            point = vertices.filter { (1..2).all { i -> it[i] != point[i] } }[0]
        }
        (0..2).map { i ->
            (0 until vertices.size).filter { ind ->
                point[i] == vertices[ind][i]
            }
        }
    }.flatten()

    private val colors = listOf(
        res.getColor(R.color.cube_red),
        res.getColor(R.color.cube_orange),
        res.getColor(R.color.cube_white),
        res.getColor(R.color.cube_yelow),
        res.getColor(R.color.cube_green),
        res.getColor(R.color.cube_blue)
    )

    private var verticesToSide:
            MutableMap<Int, List<List<Int>>> = mutableMapOf()

    init {
        //Log.i("sides", "$sides")
        for (ind in 0 until vertices.size) {
            verticesToSide.put(ind, sides.filter { side ->
                ind in side
            })
        }

    }

    private val verticesPaint = Paint().apply {
        style = Paint.Style.STROKE
        strokeWidth = 10f
        color = res.getColor(R.color.vertex_color)
    }
    private val vectorPaint = Paint().apply {
        style = Paint.Style.STROKE
        strokeWidth = 10f
        color = res.getColor(R.color.vector_color)
    }

    @SuppressLint("DrawAllocation")
    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        val center = listOf(width / 2, height / 2)
        var bottomPoint = 0
        for (ind in 0 until vertices.size) {
            if (vertices[bottomPoint][2] > vertices[ind][2]) {
                bottomPoint = ind
            }
        }
        Log.i("BottmPoint", "$bottomPoint, ${verticesToSide[bottomPoint]}")
        for (side in verticesToSide[bottomPoint]!!) {
            val path = Path()
            val paint = Paint().apply { color = colors[sides.indexOf(side)] }
            path.moveTo(
                center[0] + vertices[side[0]][0] * coef,
                center[1] + vertices[side[0]][1] * coef
            )
            for (ind in side) {
                path.lineTo(
                    center[0] + vertices[ind][0] * coef,
                    center[1] + vertices[ind][1] * coef
                )
                canvas.drawPoint(
                    center[0] + vertices[ind][0] * coef,
                    center[1] + vertices[ind][1] * coef,
                    verticesPaint
                )
            }
            path.close()
            canvas.drawPath(path, paint)
        }

        if (rotateVector.size == 2) {
            val begin = rotateVector[0]
            val end = rotateVector[1]

            canvas.drawPoint(begin.x.toFloat(), begin.y.toFloat(), vectorPaint)
            canvas.drawPoint(end.x.toFloat(), end.y.toFloat(), vectorPaint)
            canvas.drawLine(
                begin.x.toFloat(), begin.y.toFloat(),
                end.x.toFloat(), end.y.toFloat(), vectorPaint
            )
        }
    }

    private fun xRotate(vertex: MutableList<Float>, angle: Float): MutableList<Float> {
        if (vertex.size != 3) return vertex
        val res = mutableListOf(0f, 0f, 0f)
        val matrix = listOf(
            listOf(1f, 0f, 0f),
            listOf(0f, cos(angle), -sin(angle)),
            listOf(0f, sin(angle), cos(angle))
        )

        for (i in 0 until 3) {
            for (j in 0 until 3) {
                res[i] += matrix[i][j] * vertex[j]
            }
        }
        return res
    }

    private fun yRotate(vertex: MutableList<Float>, angle: Float): MutableList<Float> {
        if (vertex.size != 3) return vertex
        val res = mutableListOf(0f, 0f, 0f)
        val matrix = listOf(
            listOf(cos(angle), 0f, sin(angle)),
            listOf(0f, 1f, 0f),
            listOf(-sin(angle), 0f, cos(angle))
        )

        for (i in 0 until 3) {
            for (j in 0 until 3) {
                res[i] += matrix[i][j] * vertex[j]
            }
        }
        return res
    }

    private fun rotate() {
        val vectorX = -(rotateVector[1].x - rotateVector[0].x).toFloat()
        val vectorY = (rotateVector[1].y - rotateVector[0].y).toFloat()

        val angleX = atan(vectorY / distance)
        val angleY = atan(vectorX / distance)

        for (i in 0 until vertices.size) {
            vertices[i] = xRotate(yRotate(vertices[i], angleY), angleX)
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent): Boolean {
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                if (rotateVector.size == 0) {
                    rotateVector.add(Point(event.x.toInt(), event.y.toInt()))
                    rotateVector.add(Point(event.x.toInt(), event.y.toInt()))
                } else {
                    rotateVector[1] = Point(event.x.toInt(), event.y.toInt())
                    rotate()
                }
            }

            MotionEvent.ACTION_MOVE -> {
                rotateVector[1] = Point(event.x.toInt(), event.y.toInt())
                rotate()
            }

            MotionEvent.ACTION_UP -> {
                rotateVector.clear()
            }
        }
        invalidate()
        return true
    }

}