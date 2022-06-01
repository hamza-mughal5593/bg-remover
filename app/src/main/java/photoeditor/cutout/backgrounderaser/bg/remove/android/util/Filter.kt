package photoeditor.cutout.backgrounderaser.bg.remove.android.ui

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Point
import android.util.Log
import com.zomato.photofilters.SampleFilters
import com.zomato.photofilters.imageprocessors.Filter
import com.zomato.photofilters.imageprocessors.subfilters.BrightnessSubFilter
import com.zomato.photofilters.imageprocessors.subfilters.ToneCurveSubFilter
import com.zomato.photofilters.imageprocessors.subfilters.VignetteSubFilter
import jp.co.cyberagent.android.gpuimage.GPUImage
import jp.co.cyberagent.android.gpuimage.filter.*
import photoeditor.cutout.backgrounderaser.bg.remove.android.R
import photoeditor.cutout.backgrounderaser.bg.remove.android.models.GenerealEditorModel


fun initializeFiltersList(): ArrayList<GenerealEditorModel> {

    val filters: ArrayList<GenerealEditorModel> = ArrayList()

    filters.add(GenerealEditorModel("0", R.drawable.ic_effect))
    filters.add(GenerealEditorModel("1", R.drawable.ic_sticker))
    filters.add(GenerealEditorModel("2", R.drawable.ic_text))
    filters.add(GenerealEditorModel("3", R.drawable.ic_adjustment))
    filters.add(GenerealEditorModel("4", R.drawable.ic_crop))
    filters.add(GenerealEditorModel("5", R.drawable.ic_background))
    filters.add(GenerealEditorModel("6", R.drawable.ic_background))
    filters.add(GenerealEditorModel("7", R.drawable.ic_background))
    filters.add(GenerealEditorModel("8", R.drawable.ic_background))
    filters.add(GenerealEditorModel("9", R.drawable.ic_background))
    filters.add(GenerealEditorModel("10", R.drawable.ic_background))
    filters.add(GenerealEditorModel("11", R.drawable.ic_background))
    filters.add(GenerealEditorModel("12", R.drawable.ic_background))
    filters.add(GenerealEditorModel("13", R.drawable.ic_background))
    filters.add(GenerealEditorModel("14", R.drawable.ic_background))

    return filters
}


suspend fun applyFilter(position: Int, bitmap: Bitmap, context: Context): Bitmap? {
    val bitmaps = bitmap.copy(Bitmap.Config.ARGB_8888, true)
    var outputImage: Bitmap? = null

    when (position) {
        0 -> {
            val fooFilter = SampleFilters.getNightWhisperFilter()
            outputImage = fooFilter.processFilter(bitmaps)

        }
        1 -> {
            val fooFilter = SampleFilters.getBlueMessFilter()
            outputImage = fooFilter.processFilter(bitmaps)
        }
        2 -> {
            val fooFilter = SampleFilters.getAweStruckVibeFilter()
            outputImage = fooFilter.processFilter(bitmaps)
        }
        3 -> {
            val fooFilter = SampleFilters.getLimeStutterFilter()
            outputImage = fooFilter.processFilter(bitmaps)
        }
        4 -> {
            val fooFilter = SampleFilters.getStarLitFilter()
            outputImage = fooFilter.processFilter(bitmaps)
        }
        5 -> {
            val myFilter = Filter()
            myFilter.addSubFilter(BrightnessSubFilter(30))
            outputImage = myFilter.processFilter(bitmaps)
        }
        6 -> {
            val gpuImage = GPUImage(context)
            gpuImage.setFilter(GPUImageVibranceFilter(-1.5f))
            val mutableBitmap: Bitmap = bitmaps.copy(Bitmap.Config.ARGB_8888, true)
            gpuImage.setImage(mutableBitmap)
            outputImage= gpuImage.bitmapWithFilterApplied
        }
        7 -> {

            val gpuImage = GPUImage(context)
            gpuImage.setFilter(GPUImageVibranceFilter(1.0f))
            gpuImage.setFilter(GPUImageContrastFilter(2.0f))
            val mutableBitmap: Bitmap = bitmaps.copy(Bitmap.Config.ARGB_8888, true)
            gpuImage.setImage(mutableBitmap)
            outputImage= gpuImage.bitmapWithFilterApplied
        }
        8 ->
        {
            val gpuImage = GPUImage(context)
            gpuImage.setFilter(GPUImageHueFilter(300f))
            val mutableBitmap: Bitmap = bitmaps.copy(Bitmap.Config.ARGB_8888, true)
            gpuImage.setImage(mutableBitmap)
            outputImage= gpuImage.bitmapWithFilterApplied

        }
        9 -> {

            val myFilter = Filter()
            val rgbKnots: Array<com.zomato.photofilters.geometry.Point?> = arrayOfNulls(3)

            rgbKnots[0] = com.zomato.photofilters.geometry.Point(0f, 0f)
            rgbKnots[1] = com.zomato.photofilters.geometry.Point(225f, 139f)
            rgbKnots[2] = com.zomato.photofilters.geometry.Point(255f, 255f)

            myFilter.addSubFilter(ToneCurveSubFilter(rgbKnots, null, null, null))
            outputImage = myFilter.processFilter(bitmaps)

        }
        10 -> {


            val gpuImage = GPUImage(context)
            gpuImage.setFilter(GPUImageGrayscaleFilter())
            val mutableBitmap: Bitmap = bitmaps.copy(Bitmap.Config.ARGB_8888, true)
            gpuImage.setImage(mutableBitmap)
            outputImage = gpuImage.bitmapWithFilterApplied
        }
        11 -> {


            val gpuImage = GPUImage(context)
            gpuImage.setFilter(GPUImageSepiaToneFilter())
            val mutableBitmap: Bitmap = bitmaps.copy(Bitmap.Config.ARGB_8888, true)
            gpuImage.setImage(mutableBitmap)
            outputImage = gpuImage.bitmapWithFilterApplied
        }
        12 -> {

            val gpuImage = GPUImage(context)
            gpuImage.setFilter(GPUImageHueFilter(250f))
            val mutableBitmap: Bitmap = bitmaps.copy(Bitmap.Config.ARGB_8888, true)
            gpuImage.setImage(mutableBitmap)
            outputImage= gpuImage.bitmapWithFilterApplied
        }
        13 -> {

            val gpuImage = GPUImage(context)
            gpuImage.setFilter(GPUImageHueFilter(200f))
            val mutableBitmap: Bitmap = bitmaps.copy(Bitmap.Config.ARGB_8888, true)
            gpuImage.setImage(mutableBitmap)
            outputImage= gpuImage.bitmapWithFilterApplied
        }
        14 -> {

            val gpuImage = GPUImage(context)
            gpuImage.setFilter(GPUImageHueFilter(170f))
            val mutableBitmap: Bitmap = bitmaps.copy(Bitmap.Config.ARGB_8888, true)
            gpuImage.setImage(mutableBitmap)
            outputImage= gpuImage.bitmapWithFilterApplied
        }


    }

    return outputImage

}
