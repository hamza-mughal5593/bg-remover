package com.example.selfiesegmentation.util

import android.graphics.Bitmap
import android.graphics.Color
import androidx.annotation.ColorInt
import androidx.core.graphics.ColorUtils
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.segmentation.Segmentation
import com.google.mlkit.vision.segmentation.Segmenter
import com.google.mlkit.vision.segmentation.selfie.SelfieSegmenterOptions
import photoeditor.cutout.backgrounderaser.bg.remove.android.util.mergeBitmaps
import java.nio.ByteBuffer

/**
 * Implements functionality using the ML Kit Selfie Segmentation API
 */
class SegmentHelper(private val listener: ProcessedListener) {
    private val segmenter: Segmenter
    private var maskBuffer = ByteBuffer.allocate(0)
    private var maskWidth = 0
    private var maskHeight = 0

    init {
        val options = SelfieSegmenterOptions.Builder().setDetectorMode(SelfieSegmenterOptions.SINGLE_IMAGE_MODE).build()
        segmenter = Segmentation.getClient(options)
    }

    /**
     * Processes a bitmap and informs the listener on success
     */
    fun processImage(image: Bitmap) {
        val input = InputImage.fromBitmap(image, 0)
        segmenter.process(input)
            .addOnSuccessListener { segmentationMask ->
                // Store all information so we can reuse it if e.g. a background images is chosen
                maskBuffer = segmentationMask.buffer
                maskWidth = segmentationMask.width
                maskHeight = segmentationMask.height
                listener.imageProcessed()
            }
            .addOnFailureListener { e ->
                println("Image processing failed: $e")
            }
    }

    /**
     * Highlights the detected background of the segmentation mask as a green overlay
     */
    fun generateMaskImage(image: Bitmap): Bitmap {
        val maskBitmap = Bitmap.createBitmap(image.width, image.height, image.config)

        for (y in 0 until maskHeight) {
            for (x in 0 until maskWidth) {
                val bgConfidence = ((1.0 - maskBuffer.float) * 255).toInt()
                maskBitmap.setPixel(x, y, Color.argb(bgConfidence, 0, 255, 0))
            }
        }
        maskBuffer.rewind()
        return mergeBitmaps(image, maskBitmap)
    }

    private fun maskColorsFromByteBuffer(byteBuffer: ByteBuffer): IntArray {
        @ColorInt val colors =
            IntArray(maskWidth * maskHeight)
        for (i in 0 until maskWidth * maskHeight) {
            val backgroundLikelihood = 1 - byteBuffer.float
            if (backgroundLikelihood > 0.9) {
                colors[i] = Color.argb(128, 255, 0, 255)
            } else if (backgroundLikelihood > 0.2) {
                // Linear interpolation to make sure when backgroundLikelihood is 0.2, the alpha is 0 and
                // when backgroundLikelihood is 0.9, the alpha is 128.
                // +0.5 to round the float value to the nearest int.
                val alpha = (182.9 * backgroundLikelihood - 36.6 + 0.5).toInt()
                colors[i] = Color.argb(alpha, 255, 0, 255)
            }
        }
        return colors
    }

    /**
     * Uses the detected segmentation mask to overlay the background image
     */
    fun generateMaskBgImage(image: Bitmap, bg: Bitmap): Bitmap {
        val bgBitmap = Bitmap.createBitmap(image.width, image.height, image.config)

        for (y in 0 until maskHeight) {
            for (x in 0 until maskWidth) {
                val bgConfidence = ((1.0 - maskBuffer.float) * 255).toInt()
                var bgPixel = bg.getPixel(x, y)
                bgPixel = ColorUtils.setAlphaComponent(bgPixel, bgConfidence)
                bgBitmap.setPixel(x, y, bgPixel)
            }
        }
        maskBuffer.rewind()
        return mergeBitmaps(image, bgBitmap)
    }
}

/**
 * Interface to notify a listener about the processing status
 */
interface ProcessedListener {
    /**
     * Notifies the listener once the image was processed
     */
    fun imageProcessed()
}