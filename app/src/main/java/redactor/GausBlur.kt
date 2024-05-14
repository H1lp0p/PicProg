package redactor

import android.graphics.Bitmap
import android.graphics.Color
import android.util.Log
import androidx.core.graphics.alpha
import androidx.core.graphics.blue
import androidx.core.graphics.green
import androidx.core.graphics.red
import image.Image
import kotlin.math.exp
import kotlin.math.pow
import kotlin.math.roundToInt
import kotlin.math.sqrt
import kotlinx.coroutines.*

class GausBlur : Redactor() {

    private var radius : Int = 7

    private val sigma = 3.0
    private val sigmaSquared = sigma.pow(2.0)
    private val mu = 0.0
    private val constK = 1.0 / (sqrt(2 * Math.PI) * sigma)

    private val xMinusMu = {x: Double -> (x - mu).pow(2.0) }
    private val expOfX = {x : Double -> exp(-xMinusMu(x) / (2 * sigmaSquared)) }
    private val gausina = {x: Double -> constK * expOfX(x)}

    private fun extrapolite(source: Image) : Bitmap{
        var srcBitmap = source.getBitmap()
        var extrBitmap = Bitmap.createBitmap(srcBitmap.width + this.radius - 1,
            srcBitmap.height + this.radius - 1, srcBitmap.config)

        var srcPixels: IntArray = IntArray(srcBitmap.width * srcBitmap.height)
        srcBitmap.getPixels(srcPixels, 0, srcBitmap.width, 0, 0, srcBitmap.width, srcBitmap.height)
        extrBitmap.setPixels(srcPixels, 0, srcBitmap.width, this.radius / 2, this.radius / 2, srcBitmap.width, srcBitmap.height)

        var topLine = srcPixels.copyOf(srcBitmap.width)
        var bottomLine = srcPixels.sliceArray(IntRange(srcPixels.size - srcBitmap.width, srcPixels.size - 1))
        for (y in IntRange(0, this.radius / 2)){
            extrBitmap.setPixels(topLine, 0, srcBitmap.width, this.radius / 2, y, srcBitmap.width, 1)
            extrBitmap.setPixels(bottomLine, 0, srcBitmap.width, this.radius / 2, extrBitmap.height - y - 1, srcBitmap.width, 1)
        }

        var leftCol = IntArray(extrBitmap.height)
        var rightCol = IntArray(extrBitmap.height)

        extrBitmap.getPixels(leftCol, 0, 1, this.radius / 2 + 1, 0, 1, extrBitmap.height)
        extrBitmap.getPixels(rightCol, 0, 1, extrBitmap.width - this.radius / 2 - 1, 0, 1, extrBitmap.height)

        for (x in IntRange(0, this.radius / 2)){
            extrBitmap.setPixels(leftCol, 0, 1, x, 0, 1, extrBitmap.height)
            extrBitmap.setPixels(rightCol, 0, 1, extrBitmap.width - x - 1, 0, 1, extrBitmap.height)
        }

        return extrBitmap
    }

    private fun setMatrix() : MutableList<Double>{

        var res = emptyList<Double>().toMutableList()
        var halfRadius = this.radius / 2
        var sum = 0.0

        for (y in IntRange(-halfRadius, halfRadius)){
            for (x in IntRange(-halfRadius, halfRadius)){
                val value = gausina(sqrt(((x*x) + (y*y)).toDouble()))
                sum += value
                res += value
            }
        }

        res = res.map { it / sum }.toMutableList()

        return res;
    }

    override suspend fun compile(source: Image) {
        val srcBitmap = source.getBitmap()
        val extrBitmap = extrapolite(source)
        val matrix = setMatrix()

        val halfRadius = this.radius / 2


            for (y in IntRange(halfRadius, srcBitmap.height + halfRadius - 1)) {
                for (x in IntRange(halfRadius, srcBitmap.width + halfRadius - 1)) {

                    GlobalScope.async {
                        var sumR = 0.0
                        var sumG = 0.0
                        var sumB = 0.0
                        val alpha = srcBitmap.getPixel(x - halfRadius, y - halfRadius).alpha
                        for (matY in IntRange(-halfRadius, halfRadius)) {
                            for (matX in IntRange(-halfRadius, halfRadius)) {
                                val matInd = (matY + halfRadius) * radius + matX + halfRadius;

                                val extrPixel = extrBitmap.getPixel(x + matX, y + matY)

                                sumR += extrPixel.red * matrix[matInd]
                                sumG += extrPixel.green * matrix[matInd]
                                sumB += extrPixel.blue * matrix[matInd]
                            }
                        }
                        val newColor = Color.argb(alpha, sumR.toInt(), sumG.toInt(), sumB.toInt())

                        srcBitmap.setPixel(x - halfRadius, y - halfRadius, newColor)
                    }

                }
            }
        source.setBitMap(srcBitmap)
    }

    override fun settings(settings: Map<String, *>) {
        if ("radius" in settings.keys){
            this.radius = settings["radius"] as Int
        }
        else{
            this.radius = 7
        }
    }
}