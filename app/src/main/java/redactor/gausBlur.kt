package redactor
import image.Image
import redactor.redactor

class gausBlur : redactor() {

    private var radius : Int = 7
    override var image: Image
        get() = TODO("Not yet implemented")
        set(value) {}

    override fun compile(): Image {
        TODO("Not yet implemented")
    }

    override fun settings() {
        this.radius = 10
        TODO("Not yet implemented")
    }
}