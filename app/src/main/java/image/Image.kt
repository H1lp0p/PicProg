package image

import android.graphics.Bitmap
import android.widget.ImageView


class Image(var bitMap: Bitmap, var imgView: ImageView?) {

    init {
        if (imgView != null){
            updateView()
        }
    }

    fun setView(imageView: ImageView){
        this.imgView = imageView
    }

    private fun updateView(){
        if (imgView != null){
            imgView!!.setImageBitmap(this.bitMap)
        }
    }

}