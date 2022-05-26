package photoeditor.cutout.backgrounderaser.bg.remove.android.ui

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.view.View
import android.widget.SeekBar
import android.widget.SeekBar.OnSeekBarChangeListener
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.zomato.photofilters.SampleFilters
import com.zomato.photofilters.imageprocessors.Filter
import com.zomato.photofilters.imageprocessors.subfilters.BrightnessSubFilter
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import photoeditor.cutout.backgrounderaser.bg.remove.android.R
import photoeditor.cutout.backgrounderaser.bg.remove.android.adapters.*
import photoeditor.cutout.backgrounderaser.bg.remove.android.databinding.ActivityEditorBinding
import photoeditor.cutout.backgrounderaser.bg.remove.android.models.GenerealEditorModel
import photoeditor.cutout.backgrounderaser.bg.remove.android.util.*


class Editor : AppCompatActivity(), EditorAdapter.clickHandler, FiltersAdapter.filterHandler,BgAdapter.clikHandleBg,StickersAdapter.clickHandler,StickersSubAdapter.clickHandler,AdjustmentsAdapter.clickHandler {

    private lateinit var binding: ActivityEditorBinding
    private val editorOptions: ArrayList<GenerealEditorModel> = ArrayList()

    var count=100

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEditorBinding.inflate(layoutInflater)
        setContentView(binding.root)

        System.loadLibrary("NativeImageProcessor");
        bitmap = BitmapFactory.decodeResource(resources, R.drawable.f)

        changeStatusBarColor()
        initializeEditorOptionsList()
        initializeEditorOptionsRecyclerView()

    }

    override fun onResume() {
        super.onResume()
        binding.image.setImageBitmap(bitmap)

    }

    private fun changeStatusBarColor() {
        window.statusBarColor = ContextCompat.getColor(this, R.color.black)
    }

    private fun initializeEditorOptionsList() {
        editorOptions.add(GenerealEditorModel("Filter", R.drawable.ic_effect))
        editorOptions.add(GenerealEditorModel("Stickers", R.drawable.ic_sticker))
        editorOptions.add(GenerealEditorModel("Text", R.drawable.ic_text))
        editorOptions.add(GenerealEditorModel("Adjustments", R.drawable.ic_adjustment))
        editorOptions.add(GenerealEditorModel("Crop", R.drawable.ic_crop))
        editorOptions.add(GenerealEditorModel("Backgrounds", R.drawable.ic_background))
    }

    private fun initializeEditorOptionsRecyclerView() {
        binding.recyclerEditingOptions.layoutManager =
            LinearLayoutManager(this@Editor, LinearLayoutManager.HORIZONTAL, false)
        binding.recyclerEditingOptions.adapter = EditorAdapter(this@Editor, editorOptions, this)
    }

    override fun onFilterClick() {
        val filters = initializeFiltersList()
        initializeFiltersRecyclerView(filters)

        binding.recyclerEditingOptions.visibility = View.GONE
        binding.filtersMainLayout.visibility = View.VISIBLE
    }


    private fun initializeFiltersRecyclerView(filters:ArrayList<GenerealEditorModel>)
    {
        binding.recyclerFilters.layoutManager = LinearLayoutManager(this@Editor, LinearLayoutManager.HORIZONTAL, false)
        binding.recyclerFilters.adapter = FiltersAdapter(this@Editor, filters, this)

        filterBackAndNextClick()

    }

    private fun filterBackAndNextClick ()
    {
        binding.filterBack.setOnClickListener {

            binding.filtersMainLayout.visibility=View.GONE
            binding.recyclerEditingOptions.visibility=View.VISIBLE
            binding.filterNext.visibility=View.GONE

            binding.image.setImageBitmap(bitmap)

        }

        binding.filterNext.setOnClickListener {

            binding.filtersMainLayout.visibility=View.GONE
            binding.recyclerEditingOptions.visibility=View.VISIBLE

            bitmap= filteredImage
            binding.image.setImageBitmap(bitmap)

        }
    }
    override fun onSelectFilter(position:Int)
    {
        if(bitmap !=null)
        {
            binding.progressBar.visibility=View.VISIBLE

            CoroutineScope(IO).launch {
                filteredImage = applyFilter(position,bitmap!!)

                withContext(Main)
                {
                    binding.progressBar.visibility=View.GONE
                    binding.filterNext.visibility=View.VISIBLE
                    binding.image.setImageBitmap(filteredImage)
                }
            }

        }else
        {
            Toast.makeText(this, "Error Occurred", Toast.LENGTH_SHORT).show()
        }
    }



    override fun onBgClick()
    {
        binding.recyclerEditingOptions.visibility = View.GONE
        binding.bgsMainLayout.visibility=View.VISIBLE
        bgClickHandlers()
    }


    private fun bgClickHandlers ()
    {
        binding.colorBg.setOnClickListener {

            val colors =initializeColorsList()
            initializeBgRecyclerView(colors)

            binding.bgsMainLayout.visibility=View.GONE
            binding.colorsMainsLayout.visibility=View.VISIBLE

            binding.bgType.text="Colors"


        }

        binding.gradientBg.setOnClickListener {

            val gradients =initializeGradientList()
            initializeBgRecyclerView(gradients)

            binding.bgsMainLayout.visibility=View.GONE
            binding.colorsMainsLayout.visibility=View.VISIBLE

            binding.bgType.text="Gradient"

        }

        binding.imagesBg.setOnClickListener {

            val images = initializeImagesList()
            initializeBgRecyclerView(images)

            binding.bgsMainLayout.visibility=View.GONE
            binding.colorsMainsLayout.visibility=View.VISIBLE

            binding.bgType.text="Images"

        }

        binding.bgClose.setOnClickListener {

            binding.bgsMainLayout.visibility=View.GONE
            binding.recyclerEditingOptions.visibility=View.VISIBLE
        }

        binding.colorBack.setOnClickListener {

            binding.bgsMainLayout.visibility=View.VISIBLE
            binding.colorsMainsLayout.visibility=View.GONE
        }
    }

    private fun initializeBgRecyclerView (filters:ArrayList<Int>)
    {
        binding.recyclerColors.layoutManager = LinearLayoutManager(this@Editor, LinearLayoutManager.HORIZONTAL, false)
        binding.recyclerColors.adapter = BgAdapter(this@Editor, filters,this)
    }

    override fun onClickBg(bg: Int,position: Int)
    {
        if(position == 10 )
        {
            Toast.makeText(this, "Open Gallery", Toast.LENGTH_SHORT).show()
        }else
        {
            binding.bgMain.setBackgroundResource(bg)

        }
    }

    override fun onCropClick()
    {
        bitmap?.let {

            val intent = Intent (this,CropActivity::class.java)
            startActivity(intent)
        }
    }

    override fun onStickerClick() {

        binding.recyclerEditingOptions.visibility = View.GONE
        binding.stickersMainsLayout.visibility=View.VISIBLE

        val stickerTypes= initializeStickersTypes()
        initializeStickersTypesRecyclerView(stickerTypes)
        val stickers= initializeStickersListOne()
        initializeStickersRecyclerView(stickers)

        handleStickerBackClick()

    }

    private fun initializeStickersTypesRecyclerView (stickerTypes:ArrayList<Int>)
    {
        binding.recyclerStickersMain.layoutManager = LinearLayoutManager(this@Editor, LinearLayoutManager.HORIZONTAL, false)
        binding.recyclerStickersMain.adapter = StickersAdapter(stickerTypes,this)
    }
    private fun initializeStickersRecyclerView (sticker:ArrayList<Int>)
    {
        binding.recyclerStickersSub.layoutManager = GridLayoutManager(this@Editor, 4)
        binding.recyclerStickersSub.adapter = StickersSubAdapter(sticker,this)
    }


    override fun onClickStickerType(position: Int)
    {
        if(position == 0)
        {
            val stickers= initializeStickersListOne()
            initializeStickersRecyclerView(stickers)
        }
        if(position == 1)
        {
            val stickers= initializeStickersListTwo()
            initializeStickersRecyclerView(stickers)
        }
        if(position == 2)
        {
            val stickers= initializeStickersListThree()
            initializeStickersRecyclerView(stickers)
        }
        if(position == 3)
        {
            val stickers= initializeStickersListFour()
            initializeStickersRecyclerView(stickers)
        }
        if(position == 4)
        {
            val stickers= initializeStickersListFive()
            initializeStickersRecyclerView(stickers)
        }
    }

    override fun onClickSticker(sticker: Int)
    {
        var disabled=false
        val ca = ClipArt(this, sticker)
        binding.bgMain.addView(ca)
        ca.id=++count

        ca.setOnClickListener {
            disabled=!disabled
            if(disabled)
            {
                ca.enableAll()
            }else
            {
                ca.disableAll()
                ca.imageView
            }
        }
    }

   private fun handleStickerBackClick ()
   {
       binding.stickersBack.setOnClickListener{

           binding.stickersMainsLayout.visibility=View.GONE
           binding.recyclerEditingOptions.visibility=View.VISIBLE
       }
   }



    override fun onAdjustmentsClick() {

        binding.recyclerEditingOptions.visibility=View.GONE
        binding.adjustmentsMainsLayout.visibility=View.VISIBLE

        val adjustments= initializeAdjustmentsList()
        initializeAdjustmentsRecyclerView(adjustments)
        adjustmentsBackAndNextClick()
    }

    private fun initializeAdjustmentsRecyclerView (adjustments:ArrayList<GenerealEditorModel>)
    {
        binding.recyclerAdjustments.layoutManager = LinearLayoutManager(this@Editor, LinearLayoutManager.HORIZONTAL, false)
        binding.recyclerAdjustments.adapter = AdjustmentsAdapter(this@Editor,adjustments,this)
    }

    private fun adjustmentsBackAndNextClick ()
    {
        binding.adjustmentsBack.setOnClickListener {

            binding.adjustmentsMainsLayout.visibility=View.GONE
            binding.recyclerEditingOptions.visibility=View.VISIBLE

            binding.image.setImageBitmap(bitmap)

        }

        binding.adjustmentsNext.setOnClickListener {


            binding.adjustmentsMainsLayout.visibility=View.GONE
            binding.recyclerEditingOptions.visibility=View.VISIBLE

            bitmap= adjustedImage
            binding.image.setImageBitmap(bitmap)

        }
    }

    override fun onAdjustmentClick(position: Int)
    {
        binding.adjustmentLayout.visibility=View.VISIBLE

        if(position == 0)
        {
            binding.brightness.visibility=View.VISIBLE
            binding.contrass.visibility=View.GONE
            binding.vibrance.visibility=View.GONE
            binding.saturation.visibility=View.GONE
            binding.hue.visibility=View.GONE
            binding.exposure.visibility=View.GONE
            binding.sharpen.visibility=View.GONE
            binding.vigentee.visibility=View.GONE
            binding.highLights.visibility=View.GONE
            binding.shadow.visibility=View.GONE
            binding.temp.visibility=View.GONE


            binding.adjustment.text="Brighntness"
            binding.adjustmentValue.text="${binding.brightness.progress}"

        }
        if(position == 1)
        {
            binding.brightness.visibility=View.GONE
            binding.contrass.visibility=View.VISIBLE
            binding.vibrance.visibility=View.GONE
            binding.saturation.visibility=View.GONE
            binding.hue.visibility=View.GONE
            binding.exposure.visibility=View.GONE
            binding.sharpen.visibility=View.GONE
            binding.vigentee.visibility=View.GONE
            binding.highLights.visibility=View.GONE
            binding.shadow.visibility=View.GONE
            binding.temp.visibility=View.GONE

            binding.adjustment.text="Contras"
            binding.adjustmentValue.text="${binding.contrass.progress}"


        }
        if(position == 2)
        {
            binding.brightness.visibility=View.GONE
            binding.contrass.visibility=View.GONE
            binding.vibrance.visibility=View.VISIBLE
            binding.saturation.visibility=View.GONE
            binding.hue.visibility=View.GONE
            binding.exposure.visibility=View.GONE
            binding.sharpen.visibility=View.GONE
            binding.vigentee.visibility=View.GONE
            binding.highLights.visibility=View.GONE
            binding.shadow.visibility=View.GONE
            binding.temp.visibility=View.GONE

            binding.adjustment.text="Vibrance"
            binding.adjustmentValue.text="${binding.vibrance.progress}"

        }
        if(position == 3)
        {
            binding.brightness.visibility=View.GONE
            binding.contrass.visibility=View.GONE
            binding.vibrance.visibility=View.GONE
            binding.saturation.visibility=View.VISIBLE
            binding.hue.visibility=View.GONE
            binding.exposure.visibility=View.GONE
            binding.sharpen.visibility=View.GONE
            binding.vigentee.visibility=View.GONE
            binding.highLights.visibility=View.GONE
            binding.shadow.visibility=View.GONE
            binding.temp.visibility=View.GONE

            binding.adjustment.text="Saturation"
            binding.adjustmentValue.text="${binding.saturation.progress}"


        }
        if(position == 4)
        {
            binding.brightness.visibility=View.GONE
            binding.contrass.visibility=View.GONE
            binding.vibrance.visibility=View.GONE
            binding.saturation.visibility=View.GONE
            binding.hue.visibility=View.VISIBLE
            binding.exposure.visibility=View.GONE
            binding.sharpen.visibility=View.GONE
            binding.vigentee.visibility=View.GONE
            binding.highLights.visibility=View.GONE
            binding.shadow.visibility=View.GONE
            binding.temp.visibility=View.GONE

            binding.adjustment.text="Hue"
            binding.adjustmentValue.text="${binding.hue.progress}"

        }
        if(position == 5)
        {
            binding.brightness.visibility=View.GONE
            binding.contrass.visibility=View.GONE
            binding.vibrance.visibility=View.GONE
            binding.saturation.visibility=View.GONE
            binding.hue.visibility=View.GONE
            binding.exposure.visibility=View.VISIBLE
            binding.sharpen.visibility=View.GONE
            binding.vigentee.visibility=View.GONE
            binding.highLights.visibility=View.GONE
            binding.shadow.visibility=View.GONE
            binding.temp.visibility=View.GONE

            binding.adjustment.text="Exposure"
            binding.adjustmentValue.text="${binding.exposure.progress}"

        }
        if(position == 6)
        {
            binding.brightness.visibility=View.GONE
            binding.contrass.visibility=View.GONE
            binding.vibrance.visibility=View.GONE
            binding.saturation.visibility=View.GONE
            binding.hue.visibility=View.GONE
            binding.exposure.visibility=View.GONE
            binding.sharpen.visibility=View.VISIBLE
            binding.vigentee.visibility=View.GONE
            binding.highLights.visibility=View.GONE
            binding.shadow.visibility=View.GONE
            binding.temp.visibility=View.GONE

            binding.adjustment.text="Sharpen"
            binding.adjustmentValue.text="${binding.sharpen.progress}"

        }
        if(position == 7)
        {
            binding.brightness.visibility=View.GONE
            binding.contrass.visibility=View.GONE
            binding.vibrance.visibility=View.GONE
            binding.saturation.visibility=View.GONE
            binding.hue.visibility=View.GONE
            binding.exposure.visibility=View.GONE
            binding.sharpen.visibility=View.GONE
            binding.vigentee.visibility=View.VISIBLE
            binding.highLights.visibility=View.GONE
            binding.shadow.visibility=View.GONE
            binding.temp.visibility=View.GONE

            binding.adjustment.text="Vigentee"
            binding.adjustmentValue.text="${binding.vigentee.progress}"

        }
        if(position == 8)
        {
            binding.brightness.visibility=View.GONE
            binding.contrass.visibility=View.GONE
            binding.vibrance.visibility=View.GONE
            binding.saturation.visibility=View.GONE
            binding.hue.visibility=View.GONE
            binding.exposure.visibility=View.GONE
            binding.sharpen.visibility=View.GONE
            binding.vigentee.visibility=View.GONE
            binding.highLights.visibility=View.VISIBLE
            binding.shadow.visibility=View.GONE
            binding.temp.visibility=View.GONE

            binding.adjustment.text="Highlights"
            binding.adjustmentValue.text="${binding.highLights.progress}"

        }
        if(position == 9)
        {
            binding.brightness.visibility=View.GONE
            binding.contrass.visibility=View.GONE
            binding.vibrance.visibility=View.GONE
            binding.saturation.visibility=View.GONE
            binding.hue.visibility=View.GONE
            binding.exposure.visibility=View.GONE
            binding.sharpen.visibility=View.GONE
            binding.vigentee.visibility=View.GONE
            binding.highLights.visibility=View.GONE
            binding.shadow.visibility=View.VISIBLE
            binding.temp.visibility=View.GONE

            binding.adjustment.text="Shadow"
            binding.adjustmentValue.text="${binding.shadow.progress}"

        }
        if(position == 10)
        {
            binding.brightness.visibility=View.GONE
            binding.contrass.visibility=View.GONE
            binding.vibrance.visibility=View.GONE
            binding.saturation.visibility=View.GONE
            binding.hue.visibility=View.GONE
            binding.exposure.visibility=View.GONE
            binding.sharpen.visibility=View.GONE
            binding.vigentee.visibility=View.GONE
            binding.highLights.visibility=View.GONE
            binding.shadow.visibility=View.GONE
            binding.temp.visibility=View.VISIBLE

            binding.adjustment.text="Temprature"
            binding.adjustmentValue.text="${binding.temp.progress}"

        }

        handleAdjustments()
    }

    private fun handleAdjustments ()
    {

        binding.brightness.setOnSeekBarChangeListener(object : OnSeekBarChangeListener {
            var progressChangedValue = 0
            override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
                progressChangedValue = progress
                binding.progressBar.visibility=View.VISIBLE

                binding.adjustmentValue.text="${seekBar.progress}"
            }

            override fun onStartTrackingTouch(seekBar: SeekBar) {
                // TODO Auto-generated method stub
            }

            override fun onStopTrackingTouch(seekBar: SeekBar) {

                CoroutineScope(IO).launch {

                    adjustedImage= brightness(seekBar.progress, bitmap!!)
                    withContext(Main)
                    {

                        binding.progressBar.visibility=View.GONE
                        binding.image.setImageBitmap(adjustedImage)
                    }
                }
            }
        })


        binding.contrass.setOnSeekBarChangeListener(object : OnSeekBarChangeListener {
            var progressChangedValue = 0
            override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
                progressChangedValue = progress
                binding.progressBar.visibility=View.VISIBLE
                binding.adjustmentValue.text="${seekBar.progress}"

            }

            override fun onStartTrackingTouch(seekBar: SeekBar) {
                // TODO Auto-generated method stub
            }

            override fun onStopTrackingTouch(seekBar: SeekBar) {

                CoroutineScope(IO).launch {

                    val progress = seekBar.progress/10
                    adjustedImage= contras(progress, bitmap!!)
                    withContext(Main)
                    {

                        binding.progressBar.visibility=View.GONE
                        binding.image.setImageBitmap(adjustedImage)
                    }
                }
            }
        })

        binding.vibrance.setOnSeekBarChangeListener(object : OnSeekBarChangeListener {
            var progressChangedValue = 0
            override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
                progressChangedValue = progress
                binding.progressBar.visibility=View.VISIBLE
                binding.adjustmentValue.text="${seekBar.progress/100}"
            }

            override fun onStartTrackingTouch(seekBar: SeekBar) {
                // TODO Auto-generated method stub
            }

            override fun onStopTrackingTouch(seekBar: SeekBar) {

                CoroutineScope(IO).launch {

                    adjustedImage= vibrance(this@Editor,seekBar.progress/100, bitmap!!)
                    withContext(Main)
                    {

                        binding.progressBar.visibility=View.GONE
                        binding.image.setImageBitmap(adjustedImage)
                    }
                }
            }
        })

        binding.saturation.setOnSeekBarChangeListener(object : OnSeekBarChangeListener {
            var progressChangedValue = 0
            override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
                progressChangedValue = progress
                binding.progressBar.visibility=View.VISIBLE
                binding.adjustmentValue.text="${seekBar.progress}"
            }

            override fun onStartTrackingTouch(seekBar: SeekBar) {
                // TODO Auto-generated method stub
            }

            override fun onStopTrackingTouch(seekBar: SeekBar) {

                CoroutineScope(IO).launch {

                    adjustedImage= saturation(seekBar.progress, bitmap!!)
                    withContext(Main)
                    {

                        binding.progressBar.visibility=View.GONE
                        binding.image.setImageBitmap(adjustedImage)
                    }
                }
            }
        })

        binding.hue.setOnSeekBarChangeListener(object : OnSeekBarChangeListener {
            var progressChangedValue = 0
            override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
                progressChangedValue = progress
                binding.progressBar.visibility=View.VISIBLE
            }

            override fun onStartTrackingTouch(seekBar: SeekBar) {
                // TODO Auto-generated method stub
            }

            override fun onStopTrackingTouch(seekBar: SeekBar) {

                CoroutineScope(IO).launch {

                    adjustedImage= hue(this@Editor,seekBar.progress/10, bitmap!!)
                    withContext(Main)
                    {

                        binding.progressBar.visibility=View.GONE
                        binding.image.setImageBitmap(adjustedImage)
                    }
                }
            }
        })

        binding.exposure.setOnSeekBarChangeListener(object : OnSeekBarChangeListener {
            var progressChangedValue = 0
            override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
                progressChangedValue = progress
                binding.progressBar.visibility=View.VISIBLE
            }

            override fun onStartTrackingTouch(seekBar: SeekBar) {
                // TODO Auto-generated method stub
            }

            override fun onStopTrackingTouch(seekBar: SeekBar) {

                CoroutineScope(IO).launch {

                    adjustedImage= exposure(this@Editor,seekBar.progress/10, bitmap!!)
                    withContext(Main)
                    {

                        binding.progressBar.visibility=View.GONE
                        binding.image.setImageBitmap(adjustedImage)
                    }
                }
            }
        })

        binding.sharpen.setOnSeekBarChangeListener(object : OnSeekBarChangeListener {
            var progressChangedValue = 0
            override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
                progressChangedValue = progress
                binding.progressBar.visibility=View.VISIBLE
            }

            override fun onStartTrackingTouch(seekBar: SeekBar) {
                // TODO Auto-generated method stub
            }

            override fun onStopTrackingTouch(seekBar: SeekBar) {

                CoroutineScope(IO).launch {

                    adjustedImage= sharpen(this@Editor,seekBar.progress/10, bitmap!!)
                    withContext(Main)
                    {

                        binding.progressBar.visibility=View.GONE
                        binding.image.setImageBitmap(adjustedImage)
                    }
                }
            }
        })

        binding.vigentee.setOnSeekBarChangeListener(object : OnSeekBarChangeListener {
            var progressChangedValue = 0
            override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
                progressChangedValue = progress
                binding.progressBar.visibility=View.VISIBLE
            }

            override fun onStartTrackingTouch(seekBar: SeekBar) {
                // TODO Auto-generated method stub
            }

            override fun onStopTrackingTouch(seekBar: SeekBar) {

                CoroutineScope(IO).launch {

                    adjustedImage= vignette(this@Editor,((seekBar.progress)/10)/2, bitmap!!)
                    withContext(Main)
                    {

                        binding.progressBar.visibility=View.GONE
                        binding.image.setImageBitmap(adjustedImage)
                    }
                }
            }
        })

        binding.highLights.setOnSeekBarChangeListener(object : OnSeekBarChangeListener {
            var progressChangedValue = 0
            override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
                progressChangedValue = progress
                binding.progressBar.visibility=View.VISIBLE
            }

            override fun onStartTrackingTouch(seekBar: SeekBar) {
                // TODO Auto-generated method stub
            }

            override fun onStopTrackingTouch(seekBar: SeekBar) {

                CoroutineScope(IO).launch {

                    adjustedImage= highLights((seekBar.progress)/2, bitmap!!)
                    withContext(Main)
                    {

                        binding.progressBar.visibility=View.GONE
                        binding.image.setImageBitmap(adjustedImage)
                    }
                }
            }
        })

        binding.shadow.setOnSeekBarChangeListener(object : OnSeekBarChangeListener {
            var progressChangedValue = 0
            override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
                progressChangedValue = progress
                binding.progressBar.visibility=View.VISIBLE
            }

            override fun onStartTrackingTouch(seekBar: SeekBar) {
                // TODO Auto-generated method stub
            }

            override fun onStopTrackingTouch(seekBar: SeekBar) {

                CoroutineScope(IO).launch {

                    adjustedImage= sharpen(this@Editor,seekBar.progress/10, bitmap!!)
                    withContext(Main)
                    {

                        binding.progressBar.visibility=View.GONE
                        binding.image.setImageBitmap(adjustedImage)
                    }
                }
            }
        })

        binding.temp.setOnSeekBarChangeListener(object : OnSeekBarChangeListener {
            var progressChangedValue = 0
            override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
                progressChangedValue = progress
                binding.progressBar.visibility=View.VISIBLE
            }

            override fun onStartTrackingTouch(seekBar: SeekBar) {
                // TODO Auto-generated method stub
            }

            override fun onStopTrackingTouch(seekBar: SeekBar) {

                CoroutineScope(IO).launch {

                    adjustedImage= temp(this@Editor,seekBar.progress/10, bitmap!!)
                    withContext(Main)
                    {

                        binding.progressBar.visibility=View.GONE
                        binding.image.setImageBitmap(adjustedImage)
                    }
                }
            }
        })


    }


    companion object
    {
        var bitmap: Bitmap?=null
        var filteredImage: Bitmap?=null
        var adjustedImage: Bitmap?=null

    }



}


