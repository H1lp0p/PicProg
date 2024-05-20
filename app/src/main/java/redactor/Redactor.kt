package redactor
import android.text.Layout
import androidx.constraintlayout.widget.ConstraintLayout
import image.Image

abstract class Redactor {
    abstract suspend fun compile(source: Image)
    abstract fun settings(layout: ConstraintLayout)
}