package redactor
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.Matrix
import android.util.Log
import image.Image
import kotlin.math.*
class Rotation : Redactor() {

    private var angle : Double = 30.0

    fun rotateImage(source: Bitmap): Bitmap {
        val radians = Math.toRadians(angle)
        val sin = sin(radians)
        val cos = cos(radians)
        val newWidth = (source.width * abs(cos) + source.height * abs(sin)).toInt()
        val newHeight = (source.height * abs(cos) + source.width * abs(sin)).toInt()
        val rotatedBitmap = Bitmap.createBitmap(newWidth, newHeight, source.config)

        val x0 = 0.5 * (source.width - 1)     // point to rotate about
        val y0 = 0.5 * (source.height - 1)    // center of image

        for (x in 0 until newWidth) {
            for (y in 0 until newHeight) {
                val a = x - newWidth * 0.5
                val b = y - newHeight * 0.5
                val xx = (a * cos + b * sin + x0).toFloat()
                val yy = (-a * sin + b * cos + y0).toFloat()
                val color = bilinearInterpolate(source, xx, yy)
                rotatedBitmap.setPixel(x, y, color)
            }
        }
        return rotatedBitmap
    }

    fun bilinearInterpolate(source: Bitmap, x: Float, y: Float): Int {
        val x1 = floor(x).toInt()
        val y1 = floor(y).toInt()
        val x2 = ceil(x).toInt()
        val y2 = ceil(y).toInt()

        val Q11 = getPixelColor(source, x1, y1)
        val Q12 = getPixelColor(source, x1, y2)
        val Q21 = getPixelColor(source, x2, y1)
        val Q22 = getPixelColor(source, x2, y2)


        val R1 = interpolate(Q11, Q21, x - x1)
        val R2 = interpolate(Q12, Q22, x - x1)

        return interpolate(R1, R2, y - y1)
    }

    fun getPixelColor(source: Bitmap, x: Int, y: Int): Int {
        if (x < 0 || x >= source.width || y < 0 || y >= source.height) {
            return 0 // Or any default color
        }
        return source.getPixel(x, y)
    }

    fun interpolate(Q1: Int, Q2: Int, fraction: Float): Int {
        val a = ((1 - fraction) * Color.alpha(Q1) + fraction * Color.alpha(Q2)).toInt()
        val r = ((1 - fraction) * Color.red(Q1) + fraction * Color.red(Q2)).toInt()
        val g = ((1 - fraction) * Color.green(Q1) + fraction * Color.green(Q2)).toInt()
        val b = ((1 - fraction) * Color.blue(Q1) + fraction * Color.blue(Q2)).toInt()
        return Color.argb(a, r, g, b)
    }
    /*private fun rotateImage(source: Bitmap): Bitmap {
        val width = source.width
        val height = source.height
        val newWidth = (width * abs(cos(Math.toRadians(angle))) +
                        height * abs(sin(Math.toRadians(angle)))).toInt()
        val newHeight = (height * abs(cos(Math.toRadians(angle))) +
                        width * abs(sin(Math.toRadians(angle)))).toInt()

        val rotatedBitmap = Bitmap.createBitmap(newWidth, newHeight, source.config)

        val centerX = width / 2.0
        val centerY = height / 2.0

        val newCenterX = newWidth / 2.0
        val newCenterY = newHeight / 2.0

        for (x in 0 until width) {
            for (y in 0 until height) {
                val deltaX = x - centerX
                val deltaY = y - centerY

                val newX = (deltaX * cos(Math.toRadians(angle)) -
                            deltaY * sin(Math.toRadians(angle)) +
                            newCenterX).toInt()
                val newY = (deltaX * sin(Math.toRadians(angle)) + deltaY * cos(Math.toRadians(angle)) + newCenterY).toInt()

                if (newX in 0..<newWidth && newY >= 0 && newY < newHeight) {
                    rotatedBitmap.setPixel(newX, newY, source.getPixel(x, y))
                }
            }
        }

        return rotatedBitmap
    }*/

    override fun compile(source: Image) {
        /*val prevBitmap = source.getBitmap()
        var bInput = source.getBitmap()
        val bOutput: Bitmap
        val degrees = 45f

        val matrix = Matrix()
        matrix.postRotate(degrees)
        bOutput = Bitmap.createBitmap(bInput, 0, 0, bInput.getWidth(), bInput.getHeight(), matrix, true)
        Log.i("Rotate", "done")
        source.setBitMap(bOutput)*/

        source.setBitMap(rotateImage(source.getBitmap()))
    }

    override fun settings(settings: Map<String, *>) {
        if ("angle" in settings.keys){
            this.angle = settings["angle"] as Double
        }
        else{
            this.angle = 90.0
        }
    }
}