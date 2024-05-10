package redactor

import android.graphics.Bitmap
import android.graphics.Color
import android.util.Log
import image.Image
import kotlin.math.floor


class Resize : Redactor() {

    private var k : Double = 0.5


    private fun enlargement(source: Image) : Bitmap {
        /*val srcBitmap = source.getBitmap()
        val resized = Bitmap.createScaledBitmap(srcBitmap, Math.round(srcBitmap.width * k).toInt(), Math.round(srcBitmap.height * k).toInt(), true)
        return resized*/

        val srcBitmap = source.getBitmap()
        Log.i("resize", "0: w = ${srcBitmap.width}, h = ${srcBitmap.height}")
        val newBitmap = Bitmap.createBitmap(
            Math.round(srcBitmap.width * k).toInt(),
            Math.round(srcBitmap.height * k).toInt(),
            srcBitmap.config
        )
        Log.i("resize", "1: w = ${newBitmap.width}, h = ${newBitmap.height}")

        var alpha: Double
        var red: Double
        var green: Double
        var blue: Double
        for (j in 0 until newBitmap.height - 1) {
            for (i in 0 until newBitmap.width - 1) {
                val x = (i / k)
                val y = (j / k)
                /*Log.i("resize", "y = $y, k = $k, j = $j")*/

                val x1 = x.toInt()
                val x2 = minOf(x1 + 1, srcBitmap.width - 1)

                val y1 = y.toInt()
                val y2 = minOf(y1 + 1, srcBitmap.height - 1)

                val pix1 = srcBitmap.getPixel(x1, y1)
                val pix2 = srcBitmap.getPixel(x2, y1)
                val pix3 = srcBitmap.getPixel(x1, y2)
                val pix4 = srcBitmap.getPixel(x2, y2)

                val kx = x - x1
                val ky = y - y1

                alpha = (Color.alpha(pix1) * (1 - kx) + Color.alpha(pix2) * kx) * (1 - ky) +
                        (Color.alpha(pix3) * (1 - kx) + Color.alpha(pix4) * kx) * (ky)
                red = (Color.red(pix1) * (1 - kx) + Color.red(pix2) * kx) * (1 - ky) +
                        (Color.red(pix3) * (1 - kx) + Color.red(pix4) * kx) * (ky)
                green = (Color.green(pix1) * (1 - kx) + Color.green(pix2) * kx) * (1 - ky) +
                        (Color.green(pix3) * (1 - kx) + Color.green(pix4) * kx) * (ky)
                blue = (Color.blue(pix1) * (1 - kx) + Color.blue(pix2) * kx) * (1 - ky) +
                        (Color.blue(pix3) * (1 - kx) + Color.blue(pix4) * kx) * (ky)

                val newPixel: Int =
                    Color.argb(alpha.toInt(), red.toInt(), green.toInt(), blue.toInt())


                newBitmap.setPixel(i, j, newPixel)
            }
        }
        return newBitmap
    }

    override fun compile(source: Image) {
       /* var newBitmap = reduction(source)*/
        val newBitmap = enlargement(source)
        source.setBitMap(newBitmap)
    }

    override fun settings(settings: Map<String, *>) {
        if ("px" in settings.keys){
            this.k = settings["new size"] as Double
        }
        else{
            this.k = 0.5
        }
    }
}