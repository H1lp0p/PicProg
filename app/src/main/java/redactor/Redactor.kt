package redactor
import android.content.Context
import android.text.Layout
import android.widget.LinearLayout
import androidx.constraintlayout.widget.ConstraintLayout
import image.Image

abstract class Redactor {
    abstract suspend fun compile(source: Image)
    abstract fun settings(layout: LinearLayout, context: Context, image: Image)
}