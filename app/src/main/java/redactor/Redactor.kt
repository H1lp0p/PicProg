package redactor
import image.Image

abstract class Redactor {
    abstract suspend fun compile(source: Image)
    abstract fun settings(settings : Map<String, *>)
}