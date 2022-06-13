package photoeditor.cutout.backgrounderaser.bg.remove.android.util

import android.graphics.*
import android.util.Log
import android.widget.ImageView
import androidx.core.graphics.scale
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.segmentation.Segmentation
import com.google.mlkit.vision.segmentation.selfie.SelfieSegmenterOptions
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import photoeditor.cutout.backgrounderaser.bg.remove.android.databinding.ActivityEditorBinding
import photoeditor.cutout.backgrounderaser.bg.remove.android.ui.Editor
import java.io.IOException
import java.nio.ByteBuffer
import java.nio.ByteOrder

fun segmentation(originalImage: Bitmap, img: ImageView): Bitmap? {

    lateinit var mask: ByteBuffer
    var maskWidth: Int = 0
    var maskHeight: Int = 0
    var removeBGBitmap: Bitmap? = null

    val options =
        SelfieSegmenterOptions.Builder().setDetectorMode(SelfieSegmenterOptions.SINGLE_IMAGE_MODE)
            .enableRawSizeMask().build()
    val segmenter = Segmentation.getClient(options)
    var image: InputImage? = null
    try {
        image = InputImage.fromBitmap(originalImage, 0)
    } catch (e: IOException) {
        e.printStackTrace()

        Log.i("BBC", "segmentation: Error idr ")
    }

    segmenter.process(image!!)
        .addOnSuccessListener { segmentationMask ->
            // Store all information so we can reuse it if e.g. a background images is chosen
            mask = segmentationMask.buffer
            maskWidth = segmentationMask.width
            maskHeight = segmentationMask.height
            val maskedBitmap = convertByteBufferToBitmap(mask, maskWidth, maskHeight)
            removeBGBitmap = maskToBitmap(
                originalImage,
                maskedBitmap.scale(originalImage.width, originalImage.height, true)
            )
            img.setImageBitmap(removeBGBitmap)
            Editor.bitmap = removeBGBitmap
        }
        .addOnFailureListener { e ->
            println("Image processing failed: $e")
        }
    return removeBGBitmap
}

private fun convertByteBufferToBitmap(
    byteBuffer: ByteBuffer,
    imgSizeX: Int,
    imgSizeY: Int
): Bitmap {
    byteBuffer.rewind()
    byteBuffer.order(ByteOrder.nativeOrder())
    val bitmap = Bitmap.createBitmap(imgSizeX, imgSizeY, Bitmap.Config.ARGB_8888)
    val pixels = IntArray(imgSizeX * imgSizeY)

    for (i in 0 until imgSizeX * imgSizeY) {
        val a = byteBuffer.float
        if (a > 0.3)
            pixels[i] = Color.argb((255 * a).toInt(), 0, 0, 0)
    }
    bitmap.setPixels(pixels, 0, imgSizeX, 0, 0, imgSizeX, imgSizeY)
    return bitmap
}

private fun maskToBitmap(orignal: Bitmap, mask: Bitmap): Bitmap {
    val result = Bitmap.createBitmap(mask.width, mask.height, Bitmap.Config.ARGB_8888)
    val mCanvas = Canvas(result)
    val paint = Paint()
    paint.isAntiAlias = true;
    paint.isFilterBitmap = true;
    paint.isDither = true;
    paint.xfermode = PorterDuffXfermode(PorterDuff.Mode.DST_IN)
    mCanvas.drawBitmap(orignal, 0.toFloat(), 0.toFloat(), null)
    mCanvas.drawBitmap(mask, 0.toFloat(), 0.toFloat(), paint)
    paint.xfermode = null
    return result
}
