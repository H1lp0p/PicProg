import android.graphics.Bitmap
import androidx.core.graphics.createBitmap
import org.opencv.android.Utils
import org.opencv.core.Mat
import org.opencv.core.MatOfRect
import org.opencv.core.Point
import org.opencv.core.Scalar
import org.opencv.core.Size
import org.opencv.imgproc.Imgproc
import org.opencv.objdetect.CascadeClassifier
import java.io.File


object FindFaces {
    private fun findAllFaces(image: Bitmap, file: File): MatOfRect {
        val toProcessImage = Mat()
        Utils.bitmapToMat(image, toProcessImage)

        val cascadeClassifier = CascadeClassifier(file.absolutePath)
        val faceDetections = MatOfRect()
        cascadeClassifier.detectMultiScale(
            toProcessImage,
            faceDetections,
            1.1,
            4,
            0,
            Size(30.0, 30.0),
            Size(2400.0, 2400.0)
        )

        return faceDetections
    }

    fun drawRectangles(image: Bitmap, file: File): Bitmap {
        val matrix = findAllFaces(image, file)
        val result = createBitmap(image.width, image.height, image.config)
        val newImage = Mat()

        Utils.bitmapToMat(image, newImage)

        for (rt in matrix.toArray()) {
            Imgproc.rectangle(
                newImage,
                Point(rt.x.toDouble(), rt.y.toDouble()),
                Point((rt.x + rt.width).toDouble(), (rt.y + rt.height).toDouble()),
                Scalar(0.0, 0.0, 255.0),
                3
            )
        }
        Utils.matToBitmap(newImage, result)


        return result
    }
}