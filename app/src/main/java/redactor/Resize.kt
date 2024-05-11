package redactor

import android.graphics.Bitmap
import android.graphics.Color
import image.Image


class Resize : Redactor() {

    private var k : Double = 0.5

    private fun bilinear(source: Bitmap, c: Double): Bitmap{
        val newBitmap = Bitmap.createBitmap(
            Math.round(source.width * c).toInt(),
            Math.round(source.height * c).toInt(),
            source.config
        )

        var alpha: Double
        var red: Double
        var green: Double
        var blue: Double
        for (j in 0 until newBitmap.height - 1) {
            for (i in 0 until newBitmap.width - 1) {
                val x = (i / c)
                val y = (j / c)
                /*Log.i("resize", "y = $y, k = $k, j = $j")*/

                val x1 = x.toInt()
                val x2 = minOf(x1 + 1, source.width - 1)

                val y1 = y.toInt()
                val y2 = minOf(y1 + 1, source.height - 1)

                val pix1 = source.getPixel(x1, y1)
                val pix2 = source.getPixel(x2, y1)
                val pix3 = source.getPixel(x1, y2)
                val pix4 = source.getPixel(x2, y2)

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
    private fun enlargement(source: Image) : Bitmap {
        /*val srcBitmap = source.getBitmap()
        val resized = Bitmap.createScaledBitmap(srcBitmap, Math.round(srcBitmap.width * k).toInt(), Math.round(srcBitmap.height * k).toInt(), true)
        return resized*/

        val srcBitmap = source.getBitmap()

        return bilinear(srcBitmap, k)
    }


    private fun reduction(source: Image) : Bitmap
    {
        val firstMip = source.getBitmap()
        val c: Double = 1-((1-k)/2)
        val secondMip = bilinear(firstMip, c)
        val firstResultBitmap = bilinear(firstMip, k)
        val secondResultBitmap = bilinear(secondMip, (k/c))
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

    override fun compile(source: Image) {
        val newBitmap : Bitmap = if (k > 1) enlargement(source) else reduction(source)
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