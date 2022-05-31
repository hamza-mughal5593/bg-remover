package photoeditor.cutout.backgrounderaser.bg.remove.android.ui

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
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
import jp.co.cyberagent.android.gpuimage.GPUImage
import jp.co.cyberagent.android.gpuimage.filter.GPUImageFilter
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import photoeditor.cutout.backgrounderaser.bg.remove.android.R
import photoeditor.cutout.backgrounderaser.bg.remove.android.adapters.*
import photoeditor.cutout.backgrounderaser.bg.remove.android.databinding.ActivityEditorBinding
import photoeditor.cutout.backgrounderaser.bg.remove.android.models.GenerealEditorModel
import photoeditor.cutout.backgrounderaser.bg.remove.android.util.*
import android.util.Log
import android.view.MotionEvent
import android.view.View.OnTouchListener
import android.provider.MediaStore





class Editor : AppCompatActivity(), EditorAdapter.clickHandler, FiltersAdapter.filterHandler,
    BgAdapter.clikHandleBg, StickersAdapter.clickHandler, StickersSubAdapter.clickHandler,
    AdjustmentsAdapter.clickHandler {

    private lateinit var binding: ActivityEditorBinding
    private val editorOptions: ArrayList<GenerealEditorModel> = ArrayList()

    var count = 100
    var firsTime = true
    var job: Job? = null

    var brightness: Int = 0
    var contras: Float = -600.0f
    var vibrance: Float = -600.0f
    var saturation: Float = -600.0f
    var hue: Float = -600.0f
    var exposure: Float = -600.0f
    var sharpen: Float = -600.0f
    var vigentee: Float = -600.0f
    var highLights: Int = 0
    var temp: Float = -600.0f
    var blur: Float = -600.0f
    var shadow: Float = -600.0f


    var copiedBitmap: Bitmap? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEditorBinding.inflate(layoutInflater)
        setContentView(binding.root)

        System.loadLibrary("NativeImageProcessor");
        changeStatusBarColor()
        startEditor()
    }

    fun startEditor ()
    {
        CoroutineScope(IO).launch {
            val path = intent.getStringExtra("path")
            bitmap = MediaStore.Images.Media.getBitmap(contentResolver,Uri.parse(path))
            //bitmap = scaleDown(bitmap!!, 1000f, true)
            adjustedImage = bitmap
            copiedBitmap=bitmap

            withContext(Main)
            {
                binding.loading.visibility=View.GONE
                setBitmap()
                initializeEditorOptionsList()
                initializeEditorOptionsRecyclerView()
                handleAdjustments()
                switchOriginalVsEditedView()
                firsTime=false
            }
        }
    }

    fun setBitmap ()
    {
            binding.image.setImageBitmap(bitmap)
    }

    @SuppressLint("ClickableViewAccessibility")
    fun switchOriginalVsEditedView ()
    {
        binding.switchView.setOnTouchListener(OnTouchListener { v, event ->
            when (event.action) {
                MotionEvent.ACTION_DOWN ->                 // PRESSED
                   binding.image.setImageBitmap(bitmap)
                MotionEvent.ACTION_UP ->
                    // RELEASED
                    binding.image.setImageBitmap(adjustedImage)
            }
            true
        })
    }


    override fun onResume() {

        if(!firsTime)
        {
            setBitmap()
        }
        super.onResume()

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

    private fun initializeFiltersRecyclerView(filters: ArrayList<GenerealEditorModel>) {
        binding.recyclerFilters.layoutManager =
            LinearLayoutManager(this@Editor, LinearLayoutManager.HORIZONTAL, false)
        binding.recyclerFilters.adapter = FiltersAdapter(this@Editor, filters, this)

        filterBackAndNextClick()

    }

    private fun filterBackAndNextClick() {
        binding.filterBack.setOnClickListener {

            binding.filtersMainLayout.visibility = View.GONE
            binding.recyclerEditingOptions.visibility = View.VISIBLE
            binding.filterNext.visibility = View.GONE

            binding.image.setImageBitmap(bitmap)

        }

        binding.filterNext.setOnClickListener {

            binding.filtersMainLayout.visibility = View.GONE
            binding.recyclerEditingOptions.visibility = View.VISIBLE

            bitmap = filteredImage
            binding.image.setImageBitmap(bitmap)

        }
    }

    override fun onSelectFilter(position: Int) {
        if (bitmap != null) {
            binding.progressBar.visibility = View.VISIBLE

            CoroutineScope(IO).launch {
                filteredImage = applyFilter(position, bitmap!!)

                withContext(Main)
                {
                    binding.progressBar.visibility = View.GONE
                    binding.filterNext.visibility = View.VISIBLE
                    binding.image.setImageBitmap(filteredImage)
                }
            }

        } else {
            Toast.makeText(this, "Error Occurred", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onBgClick() {
        binding.recyclerEditingOptions.visibility = View.GONE
        binding.bgsMainLayout.visibility = View.VISIBLE
        bgClickHandlers()
    }

    private fun bgClickHandlers() {
        binding.colorBg.setOnClickListener {

            val colors = initializeColorsList()
            initializeBgRecyclerView(colors)

            binding.bgsMainLayout.visibility = View.GONE
            binding.colorsMainsLayout.visibility = View.VISIBLE

            binding.bgType.text = "Colors"


        }

        binding.gradientBg.setOnClickListener {

            val gradients = initializeGradientList()
            initializeBgRecyclerView(gradients)

            binding.bgsMainLayout.visibility = View.GONE
            binding.colorsMainsLayout.visibility = View.VISIBLE

            binding.bgType.text = "Gradient"

        }

        binding.imagesBg.setOnClickListener {

            val images = initializeImagesList()
            initializeBgRecyclerView(images)

            binding.bgsMainLayout.visibility = View.GONE
            binding.colorsMainsLayout.visibility = View.VISIBLE

            binding.bgType.text = "Images"

        }

        binding.bgClose.setOnClickListener {

            binding.bgsMainLayout.visibility = View.GONE
            binding.recyclerEditingOptions.visibility = View.VISIBLE
        }

        binding.colorBack.setOnClickListener {

            binding.bgsMainLayout.visibility = View.VISIBLE
            binding.colorsMainsLayout.visibility = View.GONE
        }
    }

    private fun initializeBgRecyclerView(filters: ArrayList<Int>) {
        binding.recyclerColors.layoutManager =
            LinearLayoutManager(this@Editor, LinearLayoutManager.HORIZONTAL, false)
        binding.recyclerColors.adapter = BgAdapter(this@Editor, filters, this)
    }

    override fun onClickBg(bg: Int, position: Int) {
        if (position == 10) {
            Toast.makeText(this, "Open Gallery", Toast.LENGTH_SHORT).show()
        } else {
            binding.bgMain.setBackgroundResource(bg)

        }
    }

    override fun onCropClick() {
        bitmap?.let {

            val intent = Intent(this, CropActivity::class.java)
            startActivity(intent)
        }
    }

    override fun onStickerClick() {

        binding.recyclerEditingOptions.visibility = View.GONE
        binding.stickersMainsLayout.visibility = View.VISIBLE

        val stickerTypes = initializeStickersTypes()
        initializeStickersTypesRecyclerView(stickerTypes)
        val stickers = initializeStickersListOne()
        initializeStickersRecyclerView(stickers)

        handleStickerBackClick()

    }

    private fun initializeStickersTypesRecyclerView(stickerTypes: ArrayList<Int>) {
        binding.recyclerStickersMain.layoutManager =
            LinearLayoutManager(this@Editor, LinearLayoutManager.HORIZONTAL, false)
        binding.recyclerStickersMain.adapter = StickersAdapter(stickerTypes, this)
    }

    private fun initializeStickersRecyclerView(sticker: ArrayList<Int>) {
        binding.recyclerStickersSub.layoutManager = GridLayoutManager(this@Editor, 4)
        binding.recyclerStickersSub.adapter = StickersSubAdapter(sticker, this)
    }

    override fun onClickStickerType(position: Int) {
        if (position == 0) {
            val stickers = initializeStickersListOne()
            initializeStickersRecyclerView(stickers)
        }
        if (position == 1) {
            val stickers = initializeStickersListTwo()
            initializeStickersRecyclerView(stickers)
        }
        if (position == 2) {
            val stickers = initializeStickersListThree()
            initializeStickersRecyclerView(stickers)
        }
        if (position == 3) {
            val stickers = initializeStickersListFour()
            initializeStickersRecyclerView(stickers)
        }
        if (position == 4) {
            val stickers = initializeStickersListFive()
            initializeStickersRecyclerView(stickers)
        }
    }

    override fun onClickSticker(sticker: Int) {
        var disabled = false
        val ca = ClipArt(this, sticker)
        binding.bgMain.addView(ca)
        ca.id = ++count

        ca.setOnClickListener {
            disabled = !disabled
            if (disabled) {
                ca.enableAll()
            } else {
                ca.disableAll()
                ca.imageView
            }
        }
    }

    private fun handleStickerBackClick() {
        binding.stickersBack.setOnClickListener {

            binding.stickersMainsLayout.visibility = View.GONE
            binding.recyclerEditingOptions.visibility = View.VISIBLE
        }
    }

    override fun onAdjustmentsClick() {

        binding.recyclerEditingOptions.visibility = View.GONE
        binding.adjustmentLayout.visibility = View.GONE
        binding.adjustmentsMainsLayout.visibility = View.VISIBLE

        val adjustments = initializeAdjustmentsList()
        initializeAdjustmentsRecyclerView(adjustments)
        adjustmentsBackAndNextClick()
    }

    private fun initializeAdjustmentsRecyclerView(adjustments: ArrayList<GenerealEditorModel>) {
        binding.recyclerAdjustments.layoutManager =
            LinearLayoutManager(this@Editor, LinearLayoutManager.HORIZONTAL, false)
        binding.recyclerAdjustments.adapter = AdjustmentsAdapter(this@Editor, adjustments, this)
    }

    private fun adjustmentsBackAndNextClick() {
        binding.adjustmentsBack.setOnClickListener {

            binding.adjustmentsMainsLayout.visibility = View.GONE
            binding.recyclerEditingOptions.visibility = View.VISIBLE

            binding.image.setImageBitmap(bitmap)

        }

        binding.adjustmentsNext.setOnClickListener {


            binding.adjustmentsMainsLayout.visibility = View.GONE
            binding.recyclerEditingOptions.visibility = View.VISIBLE

            //bitmap = adjustedImage
            binding.image.setImageBitmap(copiedBitmap)

        }
    }

    override fun onAdjustmentClick(position: Int) {

        testing(position)
        binding.adjustmentLayout.visibility = View.VISIBLE

        if (position == 0) {
            binding.brightness.visibility = View.VISIBLE
            binding.contrass.visibility = View.GONE
            binding.vibrance.visibility = View.GONE
            binding.saturation.visibility = View.GONE
            binding.hue.visibility = View.GONE
            binding.exposure.visibility = View.GONE
            binding.sharpen.visibility = View.GONE
            binding.vigentee.visibility = View.GONE
            binding.highLights.visibility = View.GONE
            binding.shadow.visibility = View.GONE
            binding.temp.visibility = View.GONE
            binding.blur.visibility = View.GONE


            binding.adjustment.text = "Brighntness"
            binding.adjustmentValue.text = "${(binding.brightness.progress) - 100}"

        }
        if (position == 1) {
            binding.brightness.visibility = View.GONE
            binding.contrass.visibility = View.VISIBLE
            binding.vibrance.visibility = View.GONE
            binding.saturation.visibility = View.GONE
            binding.hue.visibility = View.GONE
            binding.exposure.visibility = View.GONE
            binding.sharpen.visibility = View.GONE
            binding.vigentee.visibility = View.GONE
            binding.highLights.visibility = View.GONE
            binding.shadow.visibility = View.GONE
            binding.temp.visibility = View.GONE
            binding.blur.visibility = View.GONE

            binding.adjustment.text = "Contras"
            binding.adjustmentValue.text = "${(binding.contrass.progress) - 50}"


        }
        if (position == 2) {
            binding.brightness.visibility = View.GONE
            binding.contrass.visibility = View.GONE
            binding.vibrance.visibility = View.VISIBLE
            binding.saturation.visibility = View.GONE
            binding.hue.visibility = View.GONE
            binding.exposure.visibility = View.GONE
            binding.sharpen.visibility = View.GONE
            binding.vigentee.visibility = View.GONE
            binding.highLights.visibility = View.GONE
            binding.shadow.visibility = View.GONE
            binding.temp.visibility = View.GONE
            binding.blur.visibility = View.GONE

            binding.adjustment.text = "Vibrance"
            binding.adjustmentValue.text = "${binding.vibrance.progress}"

        }
        if (position == 3) {
            binding.brightness.visibility = View.GONE
            binding.contrass.visibility = View.GONE
            binding.vibrance.visibility = View.GONE
            binding.saturation.visibility = View.VISIBLE
            binding.hue.visibility = View.GONE
            binding.exposure.visibility = View.GONE
            binding.sharpen.visibility = View.GONE
            binding.vigentee.visibility = View.GONE
            binding.highLights.visibility = View.GONE
            binding.shadow.visibility = View.GONE
            binding.temp.visibility = View.GONE
            binding.blur.visibility = View.GONE

            binding.adjustment.text = "Saturation"
            binding.adjustmentValue.text = "${binding.saturation.progress}"


        }
        if (position == 4) {
            binding.brightness.visibility = View.GONE
            binding.contrass.visibility = View.GONE
            binding.vibrance.visibility = View.GONE
            binding.saturation.visibility = View.GONE
            binding.hue.visibility = View.VISIBLE
            binding.exposure.visibility = View.GONE
            binding.sharpen.visibility = View.GONE
            binding.vigentee.visibility = View.GONE
            binding.highLights.visibility = View.GONE
            binding.shadow.visibility = View.GONE
            binding.temp.visibility = View.GONE
            binding.blur.visibility = View.GONE

            binding.adjustment.text = "Hue"
            binding.adjustmentValue.text = "${binding.hue.progress}"

        }
        if (position == 5) {
            binding.brightness.visibility = View.GONE
            binding.contrass.visibility = View.GONE
            binding.vibrance.visibility = View.GONE
            binding.saturation.visibility = View.GONE
            binding.hue.visibility = View.GONE
            binding.exposure.visibility = View.VISIBLE
            binding.sharpen.visibility = View.GONE
            binding.vigentee.visibility = View.GONE
            binding.highLights.visibility = View.GONE
            binding.shadow.visibility = View.GONE
            binding.temp.visibility = View.GONE
            binding.blur.visibility = View.GONE

            binding.adjustment.text = "Exposure"
            binding.adjustmentValue.text = "${binding.exposure.progress}"

        }
        if (position == 6) {
            binding.brightness.visibility = View.GONE
            binding.contrass.visibility = View.GONE
            binding.vibrance.visibility = View.GONE
            binding.saturation.visibility = View.GONE
            binding.hue.visibility = View.GONE
            binding.exposure.visibility = View.GONE
            binding.sharpen.visibility = View.VISIBLE
            binding.vigentee.visibility = View.GONE
            binding.highLights.visibility = View.GONE
            binding.shadow.visibility = View.GONE
            binding.temp.visibility = View.GONE
            binding.blur.visibility = View.GONE

            binding.adjustment.text = "Sharpen"
            binding.adjustmentValue.text = "${binding.sharpen.progress}"

        }
        if (position == 7) {
            binding.brightness.visibility = View.GONE
            binding.contrass.visibility = View.GONE
            binding.vibrance.visibility = View.GONE
            binding.saturation.visibility = View.GONE
            binding.hue.visibility = View.GONE
            binding.exposure.visibility = View.GONE
            binding.sharpen.visibility = View.GONE
            binding.vigentee.visibility = View.VISIBLE
            binding.highLights.visibility = View.GONE
            binding.shadow.visibility = View.GONE
            binding.temp.visibility = View.GONE
            binding.blur.visibility = View.GONE

            binding.adjustment.text = "Vigentee"
            binding.adjustmentValue.text = "${binding.vigentee.progress}"

        }
        if (position == 8) {
            binding.brightness.visibility = View.GONE
            binding.contrass.visibility = View.GONE
            binding.vibrance.visibility = View.GONE
            binding.saturation.visibility = View.GONE
            binding.hue.visibility = View.GONE
            binding.exposure.visibility = View.GONE
            binding.sharpen.visibility = View.GONE
            binding.vigentee.visibility = View.GONE
            binding.highLights.visibility = View.VISIBLE
            binding.shadow.visibility = View.GONE
            binding.temp.visibility = View.GONE
            binding.blur.visibility = View.GONE

            binding.adjustment.text = "Highlights"
            binding.adjustmentValue.text = "${binding.highLights.progress}"

        }
        if (position == 9) {
            binding.brightness.visibility = View.GONE
            binding.contrass.visibility = View.GONE
            binding.vibrance.visibility = View.GONE
            binding.saturation.visibility = View.GONE
            binding.hue.visibility = View.GONE
            binding.exposure.visibility = View.GONE
            binding.sharpen.visibility = View.GONE
            binding.vigentee.visibility = View.GONE
            binding.highLights.visibility = View.GONE
            binding.shadow.visibility = View.VISIBLE
            binding.temp.visibility = View.GONE
            binding.blur.visibility = View.GONE

            binding.adjustment.text = "Shadow"
            binding.adjustmentValue.text = "${binding.shadow.progress}"

        }
        if (position == 10) {
            binding.brightness.visibility = View.GONE
            binding.contrass.visibility = View.GONE
            binding.vibrance.visibility = View.GONE
            binding.saturation.visibility = View.GONE
            binding.hue.visibility = View.GONE
            binding.exposure.visibility = View.GONE
            binding.sharpen.visibility = View.GONE
            binding.vigentee.visibility = View.GONE
            binding.highLights.visibility = View.GONE
            binding.shadow.visibility = View.GONE
            binding.blur.visibility = View.GONE
            binding.temp.visibility = View.VISIBLE

            binding.adjustment.text = "Temprature"
            binding.adjustmentValue.text = "${binding.temp.progress}"

        }
        if (position == 11) {
            binding.brightness.visibility = View.GONE
            binding.contrass.visibility = View.GONE
            binding.vibrance.visibility = View.GONE
            binding.saturation.visibility = View.GONE
            binding.hue.visibility = View.GONE
            binding.exposure.visibility = View.GONE
            binding.sharpen.visibility = View.GONE
            binding.vigentee.visibility = View.GONE
            binding.highLights.visibility = View.GONE
            binding.shadow.visibility = View.GONE
            binding.temp.visibility = View.GONE
            binding.blur.visibility = View.VISIBLE

            binding.adjustment.text = "Blur"
            binding.adjustmentValue.text = "${binding.blur.progress}"

        }

    }

    private fun handleAdjustments() {

        binding.brightness.setOnSeekBarChangeListener(object : OnSeekBarChangeListener {
            var progressChangedValue = 0
            override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
                progressChangedValue = progress
                binding.progressBar.visibility = View.VISIBLE
                binding.adjustmentValue.text = "${seekBar.progress - 100}"

            }

            override fun onStartTrackingTouch(seekBar: SeekBar) {
                // TODO Auto-generated method stub
            }

            override fun onStopTrackingTouch(seekBar: SeekBar) {

                brightness = (seekBar.progress - 100)

                Log.i("BBC", "Brightness ${brightness}")

//                CoroutineScope(IO).launch {


                    testing(0)
//                    val aadjustedImage = brightness(brightness, adjustedImage!!)
//                    withContext(Main)
//                    {
//
//                        binding.progressBar.visibility = View.GONE
//                        binding.image.setImageBitmap(aadjustedImage)
//                    }
//                }
            }
        })

        binding.contrass.setOnSeekBarChangeListener(object : OnSeekBarChangeListener {
            var progressChangedValue = 0
            override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
                progressChangedValue = progress
                binding.progressBar.visibility = View.VISIBLE
                binding.adjustmentValue.text = "${seekBar.progress - 50}"

            }

            override fun onStartTrackingTouch(seekBar: SeekBar) {
                // TODO Auto-generated method stub
            }

            override fun onStopTrackingTouch(seekBar: SeekBar) {
                contras = (seekBar.progress / 10).toFloat()

                testing(1)

//                CoroutineScope(IO).launch {
//
//                    val aadjustedImage = contras(contras, adjustedImage!!)
//
//                    withContext(Main)
//                    {
//
//                        binding.progressBar.visibility = View.GONE
//                        binding.image.setImageBitmap(aadjustedImage!!)
//                    }
//                }


            }
        })

        binding.vibrance.setOnSeekBarChangeListener(object : OnSeekBarChangeListener {
            var progressChangedValue = 0
            override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
                progressChangedValue = progress
                binding.progressBar.visibility = View.VISIBLE
                binding.adjustmentValue.text = "${seekBar.progress - 50}"

            }

            override fun onStartTrackingTouch(seekBar: SeekBar) {
                // TODO Auto-generated method stub
            }

            override fun onStopTrackingTouch(seekBar: SeekBar) {

                vibrance = (seekBar.progress / 10).toFloat()
//
                testing(2)

            }
        })

        binding.saturation.setOnSeekBarChangeListener(object : OnSeekBarChangeListener {
            var progressChangedValue = 0
            override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
                progressChangedValue = progress
                binding.progressBar.visibility = View.VISIBLE
                binding.adjustmentValue.text = "${seekBar.progress - 50}"
            }

            override fun onStartTrackingTouch(seekBar: SeekBar) {
                // TODO Auto-generated method stub
            }

            override fun onStopTrackingTouch(seekBar: SeekBar) {

                saturation = (seekBar.progress / 10).toFloat()
//                CoroutineScope(IO).launch {
//
//                    val aadjustedImage =
//                        saturation((seekBar.progress / 10).toFloat(), adjustedImage!!)
//                    withContext(Main)
//                    {
//
//                        binding.progressBar.visibility = View.GONE
//                        binding.image.setImageBitmap(aadjustedImage!!)
//                    }
//                }
                testing(0)

            }
        })

        binding.hue.setOnSeekBarChangeListener(object : OnSeekBarChangeListener {
            var progressChangedValue = 0
            override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
                progressChangedValue = progress
                binding.progressBar.visibility = View.VISIBLE
                binding.adjustmentValue.text = "${seekBar.progress - 50}"
            }

            override fun onStartTrackingTouch(seekBar: SeekBar) {
                // TODO Auto-generated method stub
            }

            override fun onStopTrackingTouch(seekBar: SeekBar) {

                hue = (seekBar.progress / 10).toFloat()
//                CoroutineScope(IO).launch {
//
//                    val aadjustedImage =
//                        hue(this@Editor, (seekBar.progress / 10).toFloat(), adjustedImage!!)
//                    withContext(Main)
//                    {
//
//                        binding.progressBar.visibility = View.GONE
//                        binding.image.setImageBitmap(aadjustedImage!!)
//                    }
//                }
                testing(0)
            }
        })

        binding.exposure.setOnSeekBarChangeListener(object : OnSeekBarChangeListener {
            var progressChangedValue = 0
            override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
                progressChangedValue = progress
                binding.progressBar.visibility = View.VISIBLE
                binding.adjustmentValue.text = "${seekBar.progress - 50}"
            }

            override fun onStartTrackingTouch(seekBar: SeekBar) {
                // TODO Auto-generated method stub
            }

            override fun onStopTrackingTouch(seekBar: SeekBar) {

                exposure = (seekBar.progress / 10).toFloat()
//                CoroutineScope(IO).launch {
//
//                    adjustedImage =
//                        exposure(this@Editor, (seekBar.progress / 10).toFloat(), bitmap!!)
//                    withContext(Main)
//                    {
//
//                        binding.progressBar.visibility = View.GONE
//                        binding.image.setImageBitmap(adjustedImage)
//                    }
//                }
                testing(0)
            }
        })

        binding.sharpen.setOnSeekBarChangeListener(object : OnSeekBarChangeListener {
            var progressChangedValue = 0
            override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
                progressChangedValue = progress
                binding.progressBar.visibility = View.VISIBLE
                binding.adjustmentValue.text = "${seekBar.progress}"
            }

            override fun onStartTrackingTouch(seekBar: SeekBar) {
                // TODO Auto-generated method stub
            }

            override fun onStopTrackingTouch(seekBar: SeekBar) {

                sharpen = (seekBar.progress / 10).toFloat()
//                CoroutineScope(IO).launch {
//
//                    adjustedImage =
//                        sharpen(this@Editor, (seekBar.progress / 10).toFloat(), bitmap!!)
//                    withContext(Main)
//                    {
//
//                        binding.progressBar.visibility = View.GONE
//                        binding.image.setImageBitmap(adjustedImage)
//                    }
//                }
                testing(0)
            }
        })

        binding.vigentee.setOnSeekBarChangeListener(object : OnSeekBarChangeListener {
            var progressChangedValue = 0
            override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
                progressChangedValue = progress
                binding.progressBar.visibility = View.VISIBLE
                binding.adjustmentValue.text = "${seekBar.progress}"
            }

            override fun onStartTrackingTouch(seekBar: SeekBar) {
                // TODO Auto-generated method stub
            }

            override fun onStopTrackingTouch(seekBar: SeekBar) {

                vigentee = (seekBar.progress / 10).toFloat()

//                CoroutineScope(IO).launch {
//
//                    adjustedImage =
//                        vignette(this@Editor, ((seekBar.progress) / 10).toFloat(), bitmap!!)
//                    withContext(Main)
//                    {
//
//                        binding.progressBar.visibility = View.GONE
//                        binding.image.setImageBitmap(adjustedImage)
//                    }
//                }
                testing(0)
            }
        })

        binding.highLights.setOnSeekBarChangeListener(object : OnSeekBarChangeListener {
            var progressChangedValue = 0
            override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
                progressChangedValue = progress
                binding.progressBar.visibility = View.VISIBLE
                binding.adjustmentValue.text = "${seekBar.progress - 50}"

            }

            override fun onStartTrackingTouch(seekBar: SeekBar) {
                // TODO Auto-generated method stub
            }

            override fun onStopTrackingTouch(seekBar: SeekBar) {

                highLights = (seekBar.progress / 10)

//                CoroutineScope(IO).launch {
//
//                    adjustedImage = highLights((seekBar.progress / 10), bitmap!!)
//                    withContext(Main)
//                    {
//
//                        binding.progressBar.visibility = View.GONE
//                        binding.image.setImageBitmap(adjustedImage)
//                    }
//                }
                testing(0)
            }
        })

        binding.shadow.setOnSeekBarChangeListener(object : OnSeekBarChangeListener {
            var progressChangedValue = 0
            override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
                progressChangedValue = progress
                binding.progressBar.visibility = View.VISIBLE
                binding.adjustmentValue.text = "${seekBar.progress - 50}"
            }

            override fun onStartTrackingTouch(seekBar: SeekBar) {
                // TODO Auto-generated method stub
            }

            override fun onStopTrackingTouch(seekBar: SeekBar) {

                shadow = (seekBar.progress / 10).toFloat()
//                CoroutineScope(IO).launch {
//
//                    adjustedImage = shadow(this@Editor, (seekBar.progress / 10).toFloat(), bitmap!!)
//                    withContext(Main)
//                    {
//
//                        binding.progressBar.visibility = View.GONE
//                        binding.image.setImageBitmap(adjustedImage)
//                    }
//                }
                testing(0)
            }
        })

        binding.temp.setOnSeekBarChangeListener(object : OnSeekBarChangeListener {
            var progressChangedValue = 0
            override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
                progressChangedValue = progress
                binding.progressBar.visibility = View.VISIBLE
                binding.adjustmentValue.text = "${seekBar.progress - 50}"
            }

            override fun onStartTrackingTouch(seekBar: SeekBar) {
                // TODO Auto-generated method stub
            }

            override fun onStopTrackingTouch(seekBar: SeekBar) {

                temp = (seekBar.progress / 10).toFloat()

//                CoroutineScope(IO).launch {
//
//                    adjustedImage = temp(this@Editor, (seekBar.progress / 10).toFloat(), bitmap!!)
//                    withContext(Main)
//                    {
//
//                        binding.progressBar.visibility = View.GONE
//                        binding.image.setImageBitmap(adjustedImage)
//                    }
//                }
                testing(0)
            }
        })

        binding.blur.setOnSeekBarChangeListener(object : OnSeekBarChangeListener {
            var progressChangedValue = 0
            override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
                progressChangedValue = progress
                binding.progressBar.visibility = View.VISIBLE
                binding.adjustmentValue.text = "${seekBar.progress}"
            }

            override fun onStartTrackingTouch(seekBar: SeekBar) {
                // TODO Auto-generated method stub
            }

            override fun onStopTrackingTouch(seekBar: SeekBar) {

                blur = (seekBar.progress / 10).toFloat()

//                CoroutineScope(IO).launch {
//
//                    adjustedImage = blur(this@Editor, (seekBar.progress / 10).toFloat(), bitmap!!)
//                    withContext(Main)
//                    {
//
//                        binding.progressBar.visibility = View.GONE
//                        binding.image.setImageBitmap(adjustedImage)
//                    }
//                }
                testing(0)
            }
        })


    }

    fun testing(position: Int) {

        adjustedImage = bitmap
//        copiedBitmap = bitmap




        job?.cancel()
        job = CoroutineScope(IO).launch {


//            if ( position == 0) {
//                copiedBitmap = brightness(brightness, copiedBitmap!!)
//            }
//            if ( position == 1) {
//                copiedBitmap = contras(contras, copiedBitmap!!)
//
//            }
//            if ( position == 2) {
//                copiedBitmap = vibrance(this@Editor,vibrance, copiedBitmap!!)
//
//            }
//            if (position == 3) {
//                copiedBitmap = saturation(saturation, copiedBitmap!!)
//            }
//            if (position == 4) {
//                copiedBitmap = hue(this@Editor,hue, copiedBitmap!!)
//
//            }
//            if ( position == 5) {
//                copiedBitmap = exposure(this@Editor,exposure, copiedBitmap!!)
//
//            }
//            if (position == 6) {
//                copiedBitmap = sharpen(this@Editor,sharpen, copiedBitmap!!)
//
//
//            }
//            if (position == 7) {
//                copiedBitmap = vignette(this@Editor,vigentee, copiedBitmap!!)
//
//
//            }
//            if (position == 8) {
//                copiedBitmap = highLights(highLights, copiedBitmap!!)
//
//            }
//            if ( position == 9) {
//                copiedBitmap = shadow(this@Editor,shadow, copiedBitmap!!)
//
//
//            }
//            if ( position == 10) {
//                copiedBitmap = temp(this@Editor,temp, copiedBitmap!!)
//
//
//            }
//            if (position == 11) {
//                copiedBitmap = blur(this@Editor,blur, copiedBitmap!!)
//
//            }


            /////////// Break Point




//            if (brightness != 0 && position != 0) {
//                brightFilter(brightness)
//            }
//            if (contras != -600.0f && position != 1) {
//                contrasFilter(contras)
//
//            }
//            if (vibrance != -600.0f && position != 2) {
//                vibranceFilter(vibrance)
//
//            }
//            if (saturation != -600.0f && position != 3) {
//                saturationFilter(saturation)
//
//            }
//            if (hue != -600.0f && position != 4) {
//                hueFilter(hue)
//
//            }
//
//            if (exposure != -600.0f && position != 5) {
//                exposureFilter(exposure)
//
//            }
//            if (sharpen != -600.0f && position != 6) {
//                sharpenFilter(sharpen)
//
//            }
//            if (vigentee != -600.0f && position != 7) {
//                vigneteeFilter(vigentee)
//
//            }
//            if (highLights != 0 && position != 8) {
//                highLightsFilter(highLights)
//
//            }
//            if (shadow != -600.0f && position != 9) {
//                shadowFilter(shadow)
//
//            }
//            if (temp != -600.0f && position != 10) {
//                tempFilter(temp)
//
//            }
//            if (blur != -600.0f && position != 11) {
//                blurFilter(blur)
//
//            }




            if (brightness != 0 ) {
                brightFilter(brightness)
            }
            if (contras != -600.0f) {
                contrasFilter(contras)

            }
            if (vibrance != -600.0f ) {
                vibranceFilter(vibrance)

            }
            if (saturation != -600.0f ) {
                saturationFilter(saturation)

            }
            if (hue != -600.0f ) {
                hueFilter(hue)

            }

            if (exposure != -600.0f ) {
                exposureFilter(exposure)

            }
            if (sharpen != -600.0f ) {
                sharpenFilter(sharpen)

            }
            if (vigentee != -600.0f ) {
                vigneteeFilter(vigentee)

            }
            if (highLights != 0 ) {
                highLightsFilter(highLights)

            }
            if (shadow != -600.0f ) {
                shadowFilter(shadow)

            }
            if (temp != -600.0f ) {
                tempFilter(temp)

            }
            if (blur != -600.0f ) {
                blurFilter(blur)

            }


            copiedBitmap = adjustedImage

            withContext(Main)
            {
                binding.progressBar.visibility=View.GONE
                binding.image.setImageBitmap(copiedBitmap)

            }
        }
        job!!.start()


    }

    suspend fun brightFilter(value: Int) {
        adjustedImage = brightness(value, adjustedImage!!)
        copiedBitmap = adjustedImage
    }

    suspend fun  contrasFilter(value: Float) {
        adjustedImage = contras(value, adjustedImage!!)
        copiedBitmap = adjustedImage
    }

    suspend fun vibranceFilter(value: Float) {
        adjustedImage = vibrance(this@Editor, value, adjustedImage!!)
        copiedBitmap = adjustedImage
    }

    suspend fun saturationFilter(value: Float) {
        adjustedImage = saturation(value, adjustedImage!!)
        copiedBitmap = adjustedImage
    }

    suspend fun hueFilter(value: Float) {
        adjustedImage = hue(this@Editor, value, adjustedImage!!)
        copiedBitmap = adjustedImage
    }

    suspend fun exposureFilter(value: Float) {
        adjustedImage = exposure(this@Editor, value, adjustedImage!!)
        copiedBitmap = adjustedImage
    }

    suspend fun sharpenFilter(value: Float) {
        adjustedImage = sharpen(this@Editor, value, adjustedImage!!)
        copiedBitmap = adjustedImage
    }

    suspend fun vigneteeFilter(value: Float) {
        adjustedImage = vignette(this@Editor, value, adjustedImage!!)
        copiedBitmap = adjustedImage
    }

    suspend fun highLightsFilter(value: Int) {
        adjustedImage = highLights(value, adjustedImage!!)
        copiedBitmap = adjustedImage
    }

    suspend fun shadowFilter(value: Float) {
        adjustedImage = shadow(this@Editor, value, adjustedImage!!)
        copiedBitmap = adjustedImage
    }

    suspend fun tempFilter(value: Float) {
        adjustedImage = temp(this@Editor, value, adjustedImage!!)
        copiedBitmap = adjustedImage
    }

    suspend fun blurFilter(value: Float) {
        adjustedImage = blur(this@Editor, value, adjustedImage!!)
        copiedBitmap = adjustedImage
    }

    companion object {
        var bitmap: Bitmap? = null
        var filteredImage: Bitmap? = null
        var adjustedImage: Bitmap? = null


    }

    open fun scaleDown(
        realImage: Bitmap, maxImageSize: Float,
        filter: Boolean
    ): Bitmap? {
        var ratio: Float = Math.min(
            maxImageSize  / realImage.getWidth(),
            maxImageSize  / realImage.getHeight()
        )
        var width: kotlin.Int = java.lang.Math.round(ratio as kotlin.Float * realImage.getWidth())
        var height: kotlin.Int = java.lang.Math.round(ratio as kotlin.Float * realImage.getHeight())
        var newBitmap: Bitmap? = Bitmap.createScaledBitmap(
            realImage, width,
            height, filter
        )
        return newBitmap
    }

    override fun onBackPressed() {
        this.finish()
        super.onBackPressed()
    }
}


