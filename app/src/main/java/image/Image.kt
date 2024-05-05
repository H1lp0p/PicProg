package image

import android.graphics.Bitmap
import android.widget.ImageView


class Image(private var bitMap: Bitmap,private var imgView: ImageView?) {

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

    fun setBitMap(newBitmap: Bitmap){
        this.bitMap = newBitmap
        updateView()
    }

    fun getBitmap(): Bitmap{
        return this.bitMap
    }

}