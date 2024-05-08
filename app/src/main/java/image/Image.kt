package image

import android.graphics.Bitmap
import android.net.Uri
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.widget.ImageView
import java.io.File
import java.io.FileOutputStream
import java.io.IOException

class Image(private var bitMap: Bitmap, private val name: String, private var imgView: ImageView?) {

    init {
        if (imgView != null){
            updateView()
        }
    }

    fun save(){
        try{
            val dir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS)
            val file = File(dir, "${this.name}.png")
            val stream = FileOutputStream(file)
            this.getBitmap().compress(Bitmap.CompressFormat.PNG, 100, stream)
            stream.flush()
            stream.close()
        }
        catch (e : IOException){
            e.printStackTrace()
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