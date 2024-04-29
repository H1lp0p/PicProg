package redactor
import image.Image

abstract class redactor {
    protected abstract var image : Image
    abstract fun compile() : Image
    abstract fun settings()
}