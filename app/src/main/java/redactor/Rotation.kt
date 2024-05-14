package redactor
import android.graphics.Bitmap
import android.graphics.Matrix
import android.util.Log
import image.Image

class Rotation : Redactor() {

    private var angle : Int = 90



    override suspend fun compile(source: Image) {
        val prevBitmap = source.getBitmap()
        var bInput = source.getBitmap()
        val bOutput: Bitmap
        val degrees = 45f

        val matrix = Matrix()
        matrix.postRotate(degrees)
        bOutput = Bitmap.createBitmap(bInput, 0, 0, bInput.getWidth(), bInput.getHeight(), matrix, true)
        Log.i("Rotate", "done")
        source.setBitMap(bOutput)
    }

    override fun settings(settings: Map<String, *>) {
        if ("angle" in settings.keys){
            this.angle = settings["radius"] as Int
        }
        else{
            this.angle = 90
        }
    }
}