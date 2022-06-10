package photoeditor.cutout.backgrounderaser.bg.remove.android.ui

import android.content.Context
import android.graphics.Bitmap
import com.zomato.photofilters.SampleFilters
import com.zomato.photofilters.imageprocessors.Filter
import com.zomato.photofilters.imageprocessors.subfilters.BrightnessSubFilter
import com.zomato.photofilters.imageprocessors.subfilters.ToneCurveSubFilter
import jp.co.cyberagent.android.gpuimage.GPUImage
import jp.co.cyberagent.android.gpuimage.filter.*
import photoeditor.cutout.backgrounderaser.bg.remove.android.R
import photoeditor.cutout.backgrounderaser.bg.remove.android.models.GenerealEditorModel


fun initializeFiltersList(): ArrayList<GenerealEditorModel> {

    val filters: ArrayList<GenerealEditorModel> = ArrayList()

    filters.add(GenerealEditorModel("Night", R.drawable.night))
    filters.add(GenerealEditorModel("Blue", R.drawable.blue))
    filters.add(GenerealEditorModel("Struck", R.drawable.struck))
    filters.add(GenerealEditorModel("Lime", R.drawable.lime))
    filters.add(GenerealEditorModel("Star", R.drawable.star))
    filters.add(GenerealEditorModel("Bright", R.drawable.bright_b))
    filters.add(GenerealEditorModel("Vibrancy", R.drawable.vibrancy))
    filters.add(GenerealEditorModel("Shine", R.drawable.shine))
    filters.add(GenerealEditorModel("Purple", R.drawable.purple))
    filters.add(GenerealEditorModel("Dark", R.drawable.dark))
    filters.add(GenerealEditorModel("Grey", R.drawable.grey))
    filters.add(GenerealEditorModel("Old", R.drawable.old))
    filters.add(GenerealEditorModel("Honk", R.drawable.honk))
    filters.add(GenerealEditorModel("Charm", R.drawable.charm))
    filters.add(GenerealEditorModel("Fresh", R.drawable.fresh))

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
