package photoeditor.cutout.backgrounderaser.bg.remove.android.util

import android.content.Context
import android.content.ContextWrapper
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Matrix
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.lang.Exception
import kotlin.math.roundToInt

fun mergeBitmaps(bmp1: Bitmap, bmp2: Bitmap): Bitmap {
    val merged = Bitmap.createBitmap(bmp1.width, bmp1.height, bmp1.config)
    val canvas = Canvas(merged)
    canvas.drawBitmap(bmp1, Matrix(), null)
    canvas.drawBitmap(bmp2, Matrix(), null)
    return merged
}

/**
 * Resizes a bitmap to the given height and width
 */
fun resizeBitmap(bmp: Bitmap, width: Int, height: Int): Bitmap {
    return Bitmap.createScaledBitmap(bmp, width, height, false)
}

/**
 * Resizes a bitmap to the given width and calculates height with respect to the aspect ratio
 *
 * Source: https://stackoverflow.com/a/28921075
 */
fun resizeBitmapWithAspect(bmp: Bitmap, width: Int): Bitmap {
    val aspectRatio: Float = bmp.width / bmp.height.toFloat()
    val height = (width / aspectRatio).roundToInt()
    return resizeBitmap(bmp, width, height)
}


public fun saveToInternalStorage(context: Context, bitmapImage: Bitmap, name: String): String? {
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
