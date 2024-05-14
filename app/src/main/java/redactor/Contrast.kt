package redactor
import image.Image

class Contrast : Redactor() {

    private var contrastValue : Double = 0.5




    override suspend fun compile(source: Image) {
        TODO("Not yet implemented")
    }

    override fun settings(settings : Map<String, *>) {
        this.contrastValue = 0.5
        TODO("Not yet implemented")
    }
}