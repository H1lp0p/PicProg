package redactor
import image.Image

abstract class Redactor {
    protected abstract var image : Image
    abstract fun compile() : Image
    abstract fun settings()
}