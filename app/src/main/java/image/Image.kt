package image

import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.widget.ImageView
import android.widget.Toast
import com.example.picprog.R
import java.io.File
import java.io.FileOutputStream
import java.io.IOException

/*TODO: may be we should attach redactor to Image
   and use all "business logic" in Image class
   (this will help to separate view on different coroutines)*/

class Image(private var srcBitmap: Bitmap, private val name: String, private var imgView: ImageView?) {

    private var newBitmap : Bitmap
    private val fileName = "${this.name}.png"
    public var width : Int = 0
    public var height : Int = 0

    init {
        newBitmap = srcBitmap.copy(Bitmap.Config.ARGB_8888, true)
        if (imgView != null){
            imgView!!.setImageDrawable(null)
            updateView()
        }
    }

    fun save(context: Context){
        try{
            val dir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS)
            val file = File(dir, fileName)
            val stream = FileOutputStream(file)
            this.newBitmap.compress(Bitmap.CompressFormat.PNG, 100, stream)
            /*Log.i("save", "I try to save!")*/
            stream.flush()
            stream.close()
            Toast.makeText(context, context.getString(R.string.system_image_saved, fileName), Toast.LENGTH_SHORT).show()
        }
        catch (e : IOException){
            Toast.makeText(context, context.getText(R.string.system_error), Toast.LENGTH_SHORT).show()
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