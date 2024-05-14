package redactor

import android.graphics.Bitmap
import android.graphics.Color
import android.util.Log
import image.Image


class Mosaic : Redactor() {

    private var px : Int = 50

    private fun mosaic(source: Image) : Bitmap {
        val srcBitmap = source.getBitmap()

        var y = 0
        while (y < srcBitmap.height) {
            var x = 0
            while (x < srcBitmap.width) {
                val pxH : Int = if (px <= srcBitmap.height - y) px else srcBitmap.height - y
                val pxW : Int = if (px <= srcBitmap.width - x) px else srcBitmap.width - x

                var alpha = 0
                var red = 0
                var green = 0
                var blue = 0
                var pixelColor: Int

                for (i in 0 until pxH) {
                    for (j in 0 until pxW) {
                        pixelColor = srcBitmap.getPixel((x + j), (y+i))
                        alpha += Color.alpha(pixelColor)
                        red += Color.red(pixelColor)
                        green += Color.green(pixelColor)
                        blue += Color.blue(pixelColor)
                    }
                }
                alpha /= (pxW * pxH)
                red /= (pxW * pxH)
                green /= (pxW * pxH)
                blue /= (pxW * pxH)
                val newPixel: Int = Color.argb(alpha, red, green, blue)

                val newPixels = IntArray(pxH * pxW)
                for (i in 0 until pxH * pxW)
                    newPixels[i] = newPixel

                srcBitmap.setPixels(newPixels,0, pxW, x, y, pxW, pxH)

                x+=px
            }
            y += px
        }
        /*val a = Color.alpha(srcBitmap.getPixel(0, 0))
        Log.i("Mosaic", "Mosaic.check - alpha = $a")*/
        return srcBitmap
    }

    override suspend fun compile(source: Image) {
        val newBitmap = mosaic(source)
        source.setBitMap(newBitmap)
    }

    override fun settings(settings: Map<String, *>) {
        if ("px" in settings.keys){
            this.px = settings["radius"] as Int
        }
        else{
            this.px = 50
        }
    }
}