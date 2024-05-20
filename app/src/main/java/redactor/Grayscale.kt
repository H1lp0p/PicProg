package redactor

import android.graphics.Bitmap
import android.graphics.Color
import androidx.constraintlayout.widget.ConstraintLayout
import image.Image

class Grayscale : Redactor() {
    // no settings

    private fun grayscale(source: Image) : Bitmap {
        val srcBitmap = source.getBitmap()
        var grey: Int

        for (y in 0 until srcBitmap.height) {
            for (x in 0 until srcBitmap.width){
                val prevPix = srcBitmap.getPixel(x,y)
                grey = (Color.red(prevPix) + Color.blue(prevPix) + Color.green(prevPix))/3
                srcBitmap.setPixel(x, y, Color.argb(Color.alpha(prevPix),grey,grey,grey))
            }
        }

        return srcBitmap
    }

    override suspend fun compile(source: Image) {
        source.setBitMap(grayscale(source))
    }

    override fun settings(layout: ConstraintLayout) {
        TODO("Not yet implemented")
    }
}