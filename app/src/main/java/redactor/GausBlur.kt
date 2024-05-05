package redactor

import android.graphics.Bitmap
import image.Image

class GausBlur : Redactor() {

    private var radius : Int = 10


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

    override fun compile(source: Image) {
        var extrBitmap = extrapolite(source)
        source.setBitMap(extrBitmap)
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