package photoeditor.cutout.backgrounderaser.bg.remove.android.util

import android.app.ActivityManager
import android.content.Context
import android.content.ContextWrapper
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import androidx.core.content.FileProvider.getUriForFile
import photoeditor.cutout.backgrounderaser.bg.remove.android.ui.Editor
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import kotlin.math.roundToInt
import android.graphics.BitmapFactory

import android.os.ParcelFileDescriptor
import java.io.FileDescriptor


fun checkRAM (context: Context) : Int
{
    val mi = ActivityManager.MemoryInfo()
    val activityManager = context.getSystemService(AppCompatActivity.ACTIVITY_SERVICE) as ActivityManager
    activityManager.getMemoryInfo(mi)
    return (mi.totalMem/(1000f*1000f*1000f)).roundToInt()
}

fun resizeImage (available : Int,bitmapWidth:Int,bitmapHeight: Int) : Bitmap
{
    var divisor=0

    val maxValue = if (available == 1){
        1000
    }else{
        1000 + (250*available)
    }

    return if(bitmapHeight>maxValue || bitmapWidth >maxValue) {
        divisor = if(bitmapHeight>bitmapWidth) {
            bitmapHeight/maxValue
        }else {
            bitmapWidth/maxValue
        }
        Bitmap.createScaledBitmap(Editor.bitmap!!, bitmapWidth/divisor, bitmapHeight/divisor, true)
    }else {
        Editor.bitmap!!
    }
}

fun getBitmapFromUris (context: Context,selectedPhotoUrii: Uri) :Bitmap?
{
   //val selectedPhotoUri= getUriForFile(context, context.applicationContext.packageName + ".provider", File(selectedPhotoUrii.path))


      return MediaStore.Images.Media.getBitmap(
            context.contentResolver,
            selectedPhotoUrii)

}

 fun getBitmapFromUri(context: Context,uri: Uri): Bitmap? {
    val parcelFileDescriptor: ParcelFileDescriptor? =
        context.contentResolver.openFileDescriptor(uri, "r")
    val fileDescriptor: FileDescriptor = parcelFileDescriptor!!.fileDescriptor
    val image = BitmapFactory.decodeFileDescriptor(fileDescriptor)
    parcelFileDescriptor.close()
    return image
}


 fun saveCaptureImage(context: Context, bitmapImage: Bitmap, name: String): String? {
    val cw = ContextWrapper(context)
    val directory = cw.getDir("imageDir", Context.MODE_PRIVATE)
    // Create imageDir
    val mypath = File(directory, name)
    var fos: FileOutputStream? = null
    try {
        fos = FileOutputStream(mypath)
        // Use the compress method on the BitMap object to write image to the OutputStream
        bitmapImage.compress(Bitmap.CompressFormat.PNG, 100, fos)
    } catch (e: Exception) {
        e.printStackTrace()
    } finally {
        try {
            fos!!.close()
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }
    return directory.canonicalPath
}
