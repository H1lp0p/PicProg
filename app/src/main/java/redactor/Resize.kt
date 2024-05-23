package redactor

import android.graphics.Bitmap
import android.graphics.Color
import androidx.constraintlayout.widget.ConstraintLayout
import image.Image
import kotlin.math.ceil
import kotlin.math.floor


class Resize : Redactor() {

    private var k : Double = 0.5

    private fun bilinearForBitmap(source: Bitmap, c: Double): Bitmap{
        val newBitmap = Bitmap.createBitmap(
            Math.round(source.width * c).toInt(),
            Math.round(source.height * c).toInt(),
            source.config
        )

        for (j in 0 until newBitmap.height - 1) {
            for (i in 0 until newBitmap.width - 1) {
                val x = (i / c).toFloat()
                val y = (j / c).toFloat()
                
                val color = bilinearInterpolate(source, x, y)

                newBitmap.setPixel(i, j, color)
            }
        }
        return newBitmap
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
    private fun enlargement(source: Image) : Bitmap {
        /*val srcBitmap = source.getBitmap()
        val resized = Bitmap.createScaledBitmap(srcBitmap, Math.round(srcBitmap.width * k).toInt(), Math.round(srcBitmap.height * k).toInt(), true)
        return resized*/

        val srcBitmap = source.getBitmap()

        return bilinearForBitmap(srcBitmap, k)
    }


    private fun reduction(source: Image) : Bitmap
    {
        val firstMip = source.getBitmap()
        val c: Double = 1-((1-k)/2)
        val secondMip = bilinearForBitmap(firstMip, c)
        val firstResultBitmap = bilinearForBitmap(firstMip, k)
        val secondResultBitmap = bilinearForBitmap(secondMip, (k/c))
        val newBitmap = Bitmap.createBitmap(
            Math.round(firstMip.width * k).toInt(),
            Math.round(firstMip.height * k).toInt(),
            firstMip.config
        )



        var alpha: Int
        var red: Int
        var green: Int
        var blue: Int
        for (j in 0 until newBitmap.height - 1) {
            for (i in 0 until newBitmap.width - 1) {
                if (i < secondResultBitmap.width || j < secondResultBitmap.height){
                    val pix1 = firstResultBitmap.getPixel(i, j)
                    val pix2 = secondResultBitmap.getPixel(i, j)

                    alpha = (Color.alpha(pix1) + Color.alpha(pix2)) / 2
                    red = (Color.red(pix1) + Color.red(pix2)) / 2
                    green = (Color.green(pix1) + Color.green(pix2)) / 2
                    blue = (Color.blue(pix1) + Color.blue(pix2)) / 2

                    val newPixel: Int =
                        Color.argb(alpha, red, green, blue)


                    newBitmap.setPixel(i, j, newPixel)
                }
                else{
                    newBitmap.setPixel(i, j, firstResultBitmap.getPixel(i,j))
                }
            }
        }

        return newBitmap
    }

    override suspend fun compile(source: Image) {
        val newBitmap : Bitmap = if (k > 1) enlargement(source) else reduction(source)
        source.setBitMap(newBitmap)
    }

    override fun settings(layout: ConstraintLayout) {
        TODO("Not yet implemented")
    }
}