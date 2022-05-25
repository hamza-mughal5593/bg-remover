package photoeditor.cutout.backgrounderaser.bg.remove.android.ui

import android.graphics.Bitmap
import com.zomato.photofilters.SampleFilters
import com.zomato.photofilters.imageprocessors.Filter
import com.zomato.photofilters.imageprocessors.subfilters.BrightnessSubFilter
import photoeditor.cutout.backgrounderaser.bg.remove.android.R
import photoeditor.cutout.backgrounderaser.bg.remove.android.models.GenerealEditorModel


fun initializeFiltersList() :ArrayList<GenerealEditorModel>{

     val filters: ArrayList<GenerealEditorModel> = ArrayList()

    filters.add(GenerealEditorModel("Moon", R.drawable.ic_effect))
    filters.add(GenerealEditorModel("Heights", R.drawable.ic_sticker))
    filters.add(GenerealEditorModel("Lal", R.drawable.ic_text))
    filters.add(GenerealEditorModel("Plaza", R.drawable.ic_adjustment))
    filters.add(GenerealEditorModel("Thokar", R.drawable.ic_crop))
    filters.add(GenerealEditorModel("Niaz", R.drawable.ic_background))

    return filters
}


 fun applyFilter (position: Int,bitmap:Bitmap): Bitmap?
{
    val bitmaps= bitmap.copy(Bitmap.Config.ARGB_8888, true)
    var outputImage:Bitmap?=null

    when(position)
    {
        0->
        {
            val fooFilter = SampleFilters.getNightWhisperFilter()
             outputImage = fooFilter.processFilter(bitmaps)

        }
        1->
        {
            val fooFilter = SampleFilters.getBlueMessFilter()
             outputImage = fooFilter.processFilter(bitmaps)
        }
        2->
        {
            val fooFilter = SampleFilters.getAweStruckVibeFilter()
             outputImage = fooFilter.processFilter(bitmaps)
        }
        3->
        {
            val fooFilter = SampleFilters.getLimeStutterFilter()
             outputImage = fooFilter.processFilter(bitmaps)
        }
        4->
        {
            val fooFilter = SampleFilters.getStarLitFilter()
             outputImage = fooFilter.processFilter(bitmaps)
        }
        5->
        {
            val myFilter = Filter()
            myFilter.addSubFilter(BrightnessSubFilter(30))
            outputImage = myFilter.processFilter(bitmaps)
        }

    }

    return outputImage

}
