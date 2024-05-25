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

/*TODO: may be we should attach redactor to Image
   and use all "business logic" in Image class
   (this will help to separate view on different coroutines)*/

class Image(private var srcBitmap: Bitmap, private val name: String, private var imgView: ImageView?) {

    private var newBitmap : Bitmap
    public var width : Int = 0
    public var height : Int = 0

    init {
        newBitmap = srcBitmap.copy(Bitmap.Config.ARGB_8888, true)
        if (imgView != null){
            imgView!!.setImageDrawable(null)
            updateView()
        }
    }

    fun save(){
        try{
            val dir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS)
            val file = File(dir, "${this.name}.png")
            val stream = FileOutputStream(file)
            this.newBitmap.compress(Bitmap.CompressFormat.PNG, 100, stream)
            /*Log.i("save", "I try to save!")*/
            stream.flush()
            stream.close()
        }
        catch (e : IOException){
            /*Log.i("save", "I can't save!")*/
            e.printStackTrace()
        }
    }

    fun setView(imageView: ImageView){
        this.imgView = imageView
        updateView()
    }

    fun revert(){
        newBitmap = srcBitmap.copy(Bitmap.Config.ARGB_8888, true)
        updateView()
    }

    private fun updateView(){
        if (imgView != null){
            imgView!!.setImageBitmap(this.newBitmap)
            this.width = imgView!!.measuredWidth
            this.height = imgView!!.measuredHeight
        }
    }

    fun setBitMap(newBitmap: Bitmap){
        this.newBitmap = newBitmap
        updateView()
    }

    fun getBitmap(): Bitmap{
        return this.srcBitmap.copy(Bitmap.Config.ARGB_8888, true)
    }
}