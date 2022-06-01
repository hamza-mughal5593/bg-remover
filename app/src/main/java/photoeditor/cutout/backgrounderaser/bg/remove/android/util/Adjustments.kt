package photoeditor.cutout.backgrounderaser.bg.remove.android.util

import android.content.Context
import android.graphics.Bitmap
import android.graphics.PointF
import com.zomato.photofilters.imageprocessors.Filter
import com.zomato.photofilters.imageprocessors.subfilters.BrightnessSubFilter
import com.zomato.photofilters.imageprocessors.subfilters.ContrastSubFilter
import com.zomato.photofilters.imageprocessors.subfilters.SaturationSubFilter
import jp.co.cyberagent.android.gpuimage.GPUImage
import jp.co.cyberagent.android.gpuimage.filter.*
import photoeditor.cutout.backgrounderaser.bg.remove.android.R
import photoeditor.cutout.backgrounderaser.bg.remove.android.models.GenerealEditorModel


fun initializeAdjustmentsList(): ArrayList<GenerealEditorModel> {
    val adjustments: ArrayList<GenerealEditorModel> = ArrayList()
    adjustments.add(GenerealEditorModel("Brightness", R.drawable.bright))
    adjustments.add(GenerealEditorModel("Contras", R.drawable.contras))
    adjustments.add(GenerealEditorModel("Vibrance", R.drawable.vibrance))
    adjustments.add(GenerealEditorModel("Saturation", R.drawable.saturation))
    adjustments.add(GenerealEditorModel("Hue", R.drawable.hue))
    adjustments.add(GenerealEditorModel("Exposure", R.drawable.exposure))
    adjustments.add(GenerealEditorModel("Sharpness", R.drawable.sharpness))
    adjustments.add(GenerealEditorModel("Vignette", R.drawable.vignette))
    adjustments.add(GenerealEditorModel("Highlights", R.drawable.highlights))
    adjustments.add(GenerealEditorModel("Shadow", R.drawable.sahdow))
    adjustments.add(GenerealEditorModel("Temp", R.drawable.temp))
    adjustments.add(GenerealEditorModel("Blur", R.drawable.blur))

    return adjustments
}

suspend fun brightness(value: Int, bitmap: Bitmap): Bitmap {
    val myFilter = Filter()
    myFilter.addSubFilter(BrightnessSubFilter(value))
    val mutableBitmap: Bitmap = bitmap.copy(Bitmap.Config.ARGB_8888, true)
    return myFilter.processFilter(mutableBitmap)

}

fun saturation(value: Float, bitmap: Bitmap): Bitmap {
    val myFilter = Filter()
    if (value in 0.1f..0.9f) {
        myFilter.addSubFilter(SaturationSubFilter(0.1f))
    } else if  (value in 1f..1.9f) {
        myFilter.addSubFilter(SaturationSubFilter(0.2f))
    } else if (value in 2f..2.9f) {
        myFilter.addSubFilter(SaturationSubFilter(0.4f))
    } else if (value in 3f..3.9f) {
        myFilter.addSubFilter(SaturationSubFilter(0.6f))
    } else if (value in 4f..4.9f) {
        myFilter.addSubFilter(SaturationSubFilter(0.8f))
    } else if (value in 5f..5.9f) {
        myFilter.addSubFilter(SaturationSubFilter(1f))
    } else if (value in 6f..6.9f) {
        myFilter.addSubFilter(SaturationSubFilter(1.1f))
    } else if (value in 7f..7.9f) {
        myFilter.addSubFilter(SaturationSubFilter(1.3f))
    } else if (value in 8f..8.9f) {
        myFilter.addSubFilter(SaturationSubFilter(1.5f))
    } else if (value in 9f..9.9f) {
        myFilter.addSubFilter(SaturationSubFilter(1.7f))
    } else if (value == 10f) {
        myFilter.addSubFilter(SaturationSubFilter(2f))
    }
    val mutableBitmap: Bitmap = bitmap.copy(Bitmap.Config.ARGB_8888, true)
    return myFilter.processFilter(mutableBitmap)
}

suspend fun contras(value: Float, bitmap: Bitmap): Bitmap {
    val myFilter = Filter()
    if (value in 0.1f..0.9f) {
        myFilter.addSubFilter(ContrastSubFilter(0.1f))
    } else if (value in 1f..1.9f) {
        myFilter.addSubFilter(ContrastSubFilter(0.2f))
    } else if (value in 2f..2.9f) {
        myFilter.addSubFilter(ContrastSubFilter(0.4f))
    } else if (value in 3f..3.9f) {
        myFilter.addSubFilter(ContrastSubFilter(0.6f))
    } else if (value in 4f..4.9f) {
        myFilter.addSubFilter(ContrastSubFilter(0.8f))
    } else if (value in 5f..5.9f) {
        myFilter.addSubFilter(ContrastSubFilter(1f))
    } else if (value in 6f..6.9f) {
        myFilter.addSubFilter(ContrastSubFilter(1.1f))
    } else if (value in 7f..7.9f) {
        myFilter.addSubFilter(ContrastSubFilter(1.3f))
    } else if (value in 8f..8.9f) {
        myFilter.addSubFilter(ContrastSubFilter(1.5f))
    } else if (value in 9f..9.9f) {
        myFilter.addSubFilter(ContrastSubFilter(1.7f))
    } else if (value == 10f) {
        myFilter.addSubFilter(ContrastSubFilter(2f))
    }
    val mutableBitmap: Bitmap = bitmap.copy(Bitmap.Config.ARGB_8888, true)
    return myFilter.processFilter(mutableBitmap)

}

suspend fun exposure(context: Context, value: Float, bitmap: Bitmap): Bitmap {
    val gpuImage = GPUImage(context)
    if (value in 0.1f..0.9f) {
        gpuImage.setFilter(GPUImageFilter())
        gpuImage.setFilter(GPUImageExposureFilter(-1.9f))
    } else if (value in 1f..1.9f) {
        gpuImage.setFilter(GPUImageFilter())
        gpuImage.setFilter(GPUImageExposureFilter(-1.6f))
    } else if (value in 2f..2.9f) {
        gpuImage.setFilter(GPUImageFilter())
        gpuImage.setFilter(GPUImageExposureFilter(-1.4f))
    } else if (value in 3f..3.9f) {
        gpuImage.setFilter(GPUImageFilter())
        gpuImage.setFilter(GPUImageExposureFilter(-1.2f))
    } else if (value in 4f..4.9f) {
        gpuImage.setFilter(GPUImageFilter())
        gpuImage.setFilter(GPUImageExposureFilter(-1.0f))
    } else if (value in 5f..5.9f) {
        gpuImage.setFilter(GPUImageFilter())
        gpuImage.setFilter(GPUImageExposureFilter(0.0f))
    } else if (value in 6f..6.9f) {
        gpuImage.setFilter(GPUImageFilter())
        gpuImage.setFilter(GPUImageExposureFilter(1.0f))
    } else if (value in 7f..7.9f) {
        gpuImage.setFilter(GPUImageFilter())
        gpuImage.setFilter(GPUImageExposureFilter(1.2f))
    } else if (value in 8f..8.9f) {
        gpuImage.setFilter(GPUImageFilter())
        gpuImage.setFilter(GPUImageExposureFilter(1.4f))
    } else if (value in 9f..9.9f) {
        gpuImage.setFilter(GPUImageFilter())
        gpuImage.setFilter(GPUImageExposureFilter(1.7f))
    } else if (value == 10f) {
        gpuImage.setFilter(GPUImageFilter())
        gpuImage.setFilter(GPUImageExposureFilter(2.0f))
    }
    val mutableBitmap: Bitmap = bitmap.copy(Bitmap.Config.ARGB_8888, true)
    gpuImage.setImage(mutableBitmap)
    return gpuImage.bitmapWithFilterApplied

}

suspend fun hue(context: Context, value: Float, bitmap: Bitmap): Bitmap {
    val gpuImage = GPUImage(context)
    if (value in 0.1f..0.9f) {
        gpuImage.setFilter(GPUImageFilter())
//        gpuImage.setFilter(GPUImageHueFilter(500f))
    } else if (value in 1f..1.9f) {
        gpuImage.setFilter(GPUImageFilter())
        gpuImage.setFilter(GPUImageHueFilter(470f))
    } else if (value in 2f..2.9f) {
        gpuImage.setFilter(GPUImageFilter())
        gpuImage.setFilter(GPUImageHueFilter(440f))
    } else if (value in 3f..3.9f) {
        gpuImage.setFilter(GPUImageFilter())
        gpuImage.setFilter(GPUImageHueFilter(410f))
    } else if (value in 4f..4.9f) {
        gpuImage.setFilter(GPUImageFilter())
        gpuImage.setFilter(GPUImageHueFilter(380f))
    } else if (value in 5f..5.9f) {
        gpuImage.setFilter(GPUImageFilter())
        gpuImage.setFilter(GPUImageHueFilter(360.0f))
    } else if (value in 6f..6.9f) {
        gpuImage.setFilter(GPUImageFilter())
        gpuImage.setFilter(GPUImageHueFilter(300f))
    } else if (value in 7f..7.9f) {
        gpuImage.setFilter(GPUImageFilter())
        gpuImage.setFilter(GPUImageHueFilter(250f))
    } else if (value in 8f..8.9f) {
        gpuImage.setFilter(GPUImageFilter())
        gpuImage.setFilter(GPUImageHueFilter(200f))
    } else if (value in 9f..9.9f) {
        gpuImage.setFilter(GPUImageFilter())
        gpuImage.setFilter(GPUImageHueFilter(150f))
    } else if (value == 10f) {
        gpuImage.setFilter(GPUImageFilter())
        gpuImage.setFilter(GPUImageHueFilter(100f))
    }
    val mutableBitmap: Bitmap = bitmap.copy(Bitmap.Config.ARGB_8888, true)
    gpuImage.setImage(mutableBitmap)
    return gpuImage.bitmapWithFilterApplied
}

suspend fun sharpen(context: Context, value: Float, bitmap: Bitmap): Bitmap {
    val gpuImage = GPUImage(context)
    if (value in 0f..0.9f) {
        gpuImage.setFilter(GPUImageSharpenFilter(-1.0f))
    } else if (value in 1f..1.9f) {
        gpuImage.setFilter(GPUImageSharpenFilter(-0.8f))
    } else if (value in 2f..2.9f) {
        gpuImage.setFilter(GPUImageSharpenFilter(-0.6f))
    } else if (value in 3f..3.9f) {
        gpuImage.setFilter(GPUImageSharpenFilter(-0.4f))
    } else if (value in 4f..4.9f) {
        gpuImage.setFilter(GPUImageSharpenFilter(-0.2f))
    } else if (value in 5f..5.9f) {
        gpuImage.setFilter(GPUImageSharpenFilter(0.0f))
    } else if (value in 6f..6.9f) {
        gpuImage.setFilter(GPUImageSharpenFilter(0.2f))
    } else if (value in 7f..7.9f) {
        gpuImage.setFilter(GPUImageSharpenFilter(0.4f))
    } else if (value in 8f..8.9f) {
        gpuImage.setFilter(GPUImageSharpenFilter(0.6f))
    } else if (value in 9f..9.9f) {
        gpuImage.setFilter(GPUImageSharpenFilter(0.8f))
    } else if (value == 10f) {
        gpuImage.setFilter(GPUImageSharpenFilter(1.0f))
    }
    val mutableBitmap: Bitmap = bitmap.copy(Bitmap.Config.ARGB_8888, true)
    gpuImage.setImage(mutableBitmap)
    return gpuImage.bitmapWithFilterApplied
}

suspend fun vibrance(context: Context, value: Float, bitmap: Bitmap): Bitmap {
    val gpuImage = GPUImage(context)
    if (value in 0f..0.9f) {
        gpuImage.setFilter(GPUImageVibranceFilter(-1.0f))
    } else if (value in 1f..1.9f) {
        gpuImage.setFilter(GPUImageVibranceFilter(-0.8f))
    } else if (value in 2f..2.9f) {
        gpuImage.setFilter(GPUImageVibranceFilter(-0.6f))
    } else if (value in 3f..3.9f) {
        gpuImage.setFilter(GPUImageVibranceFilter(-0.4f))
    } else if (value in 4f..4.9f) {
        gpuImage.setFilter(GPUImageVibranceFilter(-0.2f))
    } else if (value in 5f..5.9f) {
        gpuImage.setFilter(GPUImageVibranceFilter(0.0f))
    } else if (value in 6f..6.9f) {
        gpuImage.setFilter(GPUImageVibranceFilter(0.2f))
    } else if (value in 7f..7.9f) {
        gpuImage.setFilter(GPUImageVibranceFilter(0.4f))
    } else if (value in 8f..8.9f) {
        gpuImage.setFilter(GPUImageVibranceFilter(0.6f))
    } else if (value in 9f..9.9f) {
        gpuImage.setFilter(GPUImageVibranceFilter(0.8f))
    } else if (value == 10f) {
        gpuImage.setFilter(GPUImageVibranceFilter(1.0f))
    }
    val mutableBitmap: Bitmap = bitmap.copy(Bitmap.Config.ARGB_8888, true)
    gpuImage.setImage(mutableBitmap)
    return gpuImage.bitmapWithFilterApplied
}

suspend fun vignette(context: Context, value: Float, bitmap: Bitmap): Bitmap {
    val gpuImage = GPUImage(context)
    if (value in 5f..5.9f) {
        gpuImage.setFilter(
            GPUImageVignetteFilter(
                PointF(0.5f, 0.5f),
                floatArrayOf(0.0f, 0.0f, 0.0f),
                0.3f,
                0.7f
            )
        )
    } else if (value in 4f..4.9f) {
        gpuImage.setFilter(
            GPUImageVignetteFilter(
                PointF(0.5f, 0.5f),
                floatArrayOf(0.0f, 0.0f, 0.0f),
                0.3f,
                0.7f
            )
        )
    } else if (value in 3f..3.9f) {
        gpuImage.setFilter(
            GPUImageVignetteFilter(
                PointF(0.5f, 0.5f),
                floatArrayOf(0.1f, 0.1f, 0.1f),
                0.3f,
                0.7f
            )
        )
    } else if (value in 2f..2.9f) {
        gpuImage.setFilter(
            GPUImageVignetteFilter(
                PointF(0.5f, 0.5f),
                floatArrayOf(0.2f, 0.2f, 0.2f),
                0.3f,
                0.7f
            )
        )
    } else if (value in 1f..1.9f) {
        gpuImage.setFilter(
            GPUImageVignetteFilter(
                PointF(0.5f, 0.5f),
                floatArrayOf(0.3f, 0.3f, 0.3f),
                0.3f,
                0.7f
            )
        )
    } else if (value in 0f..0.9f) {
        gpuImage.setFilter(
            GPUImageVignetteFilter(
                PointF(0.5f, 0.5f),
                floatArrayOf(0.0f, 0f, 0f),
                1.0f,
                1.0f
            )
        )
    }
    val mutableBitmap: Bitmap = bitmap.copy(Bitmap.Config.ARGB_8888, true)
    gpuImage.setImage(mutableBitmap)
    return gpuImage.bitmapWithFilterApplied
}

suspend fun shadow(context: Context, value: Float, bitmap: Bitmap): Bitmap {
    val gpuImage = GPUImage(context)
    if (value in 0f..0.9f) {
        gpuImage.setFilter(GPUImageHighlightShadowFilter(1.0f, 1.0f))
    } else if (value in 1f..1.9f) {
        gpuImage.setFilter(GPUImageHighlightShadowFilter(0.8f, 1.0f))
    } else if (value in 2f..2.9f) {
        gpuImage.setFilter(GPUImageHighlightShadowFilter(0.46f, 1.0f))
    } else if (value in 3f..3.9f) {
        gpuImage.setFilter(GPUImageHighlightShadowFilter(0.4f, 1.0f))
    } else if (value in 4f..4.9f) {
        gpuImage.setFilter(GPUImageHighlightShadowFilter(0.2f, 1.0f))
    } else if (value in 5f..5.9f) {
        gpuImage.setFilter(GPUImageHighlightShadowFilter(0.0f, 1.0f))
    } else if (value in 6f..6.9f) {
        gpuImage.setFilter(GPUImageHighlightShadowFilter(0.0f, 0.8f))
    } else if (value in 7f..7.9f) {
        gpuImage.setFilter(GPUImageHighlightShadowFilter(0.0f, 0.6f))
    } else if (value in 8f..8.9f) {
        gpuImage.setFilter(GPUImageHighlightShadowFilter(0.0f, 0.4f))
    } else if (value in 9f..9.9f) {
        gpuImage.setFilter(GPUImageHighlightShadowFilter(0.0f, 0.2f))
    } else if (value == 10f) {
        gpuImage.setFilter(GPUImageHighlightShadowFilter(0.0f, 0.0f))
    }
    val mutableBitmap: Bitmap = bitmap.copy(Bitmap.Config.ARGB_8888, true)
    gpuImage.setImage(mutableBitmap)
    return gpuImage.bitmapWithFilterApplied
}

suspend fun temp(context: Context, value: Float, bitmap: Bitmap): Bitmap {
    val gpuImage = GPUImage(context)
    if (value in 0f..0.9f) {
        gpuImage.setFilter(GPUImageHueFilter(385f))
    } else if (value in 1f..1.9f) {
        gpuImage.setFilter(GPUImageHueFilter(380f))
    } else if (value in 2f..2.9f) {
        gpuImage.setFilter(GPUImageHueFilter(375f))
    } else if (value in 3f..3.9f) {
        gpuImage.setFilter(GPUImageHueFilter(370f))
    } else if (value in 4f..4.9f) {
        gpuImage.setFilter(GPUImageHueFilter(365f))
    } else if (value in 5f..5.9f) {
        gpuImage.setFilter(GPUImageHueFilter(360.0f))
    } else if (value in 6f..6.9f) {
        gpuImage.setFilter(GPUImageHueFilter(358f))
    } else if (value in 7f..7.9f) {
        gpuImage.setFilter(GPUImageHueFilter(356f))
    } else if (value in 8f..8.9f) {
        gpuImage.setFilter(GPUImageHueFilter(352f))
    } else if (value in 9f..9.9f) {
        gpuImage.setFilter(GPUImageHueFilter(346f))
    } else if (value == 10f) {
        gpuImage.setFilter(GPUImageHueFilter(340f))
    }
    val mutableBitmap: Bitmap = bitmap.copy(Bitmap.Config.ARGB_8888, true)
    gpuImage.setImage(mutableBitmap)
    return gpuImage.bitmapWithFilterApplied
}

suspend fun highLights(value: Int, bitmap: Bitmap): Bitmap {
    val myFilter = Filter()
    myFilter.addSubFilter(BrightnessSubFilter(value))
    val mutableBitmap: Bitmap = bitmap.copy(Bitmap.Config.ARGB_8888, true)
    return myFilter.processFilter(mutableBitmap)
}

suspend fun blur(context: Context, value: Float, bitmap: Bitmap): Bitmap {


    val gpuImage = GPUImage(context)

    if (value in 0f..0.9f) {
        gpuImage.setFilter(GPUImageGaussianBlurFilter(0.0f))
    } else if (value in 1f..1.9f) {
        gpuImage.setFilter(GPUImageGaussianBlurFilter(0.1f))
    } else if (value in 2f..2.9f) {
        gpuImage.setFilter(GPUImageGaussianBlurFilter(0.2f))
    } else if (value in 3f..3.9f) {
        gpuImage.setFilter(GPUImageGaussianBlurFilter(0.3f))
    } else if (value in 4f..4.9f) {
        gpuImage.setFilter(GPUImageGaussianBlurFilter(0.4f))
    } else if (value in 5f..5.9f) {
        gpuImage.setFilter(GPUImageGaussianBlurFilter(0.5f))
    } else if (value in 6f..6.9f) {
        gpuImage.setFilter(GPUImageGaussianBlurFilter(0.6f))
    } else if (value in 7f..7.9f) {
        gpuImage.setFilter(GPUImageGaussianBlurFilter(0.7f))
    } else if (value in 8f..8.9f) {
        gpuImage.setFilter(GPUImageGaussianBlurFilter(0.8f))
    } else if (value in 9f..9.9f) {
        gpuImage.setFilter(GPUImageGaussianBlurFilter(0.9f))
    } else if (value == 10f) {
        gpuImage.setFilter(GPUImageGaussianBlurFilter(1.0f))
    }
    val mutableBitmap: Bitmap = bitmap.copy(Bitmap.Config.ARGB_8888, true)
    gpuImage.setImage(mutableBitmap)
    return gpuImage.bitmapWithFilterApplied
}
