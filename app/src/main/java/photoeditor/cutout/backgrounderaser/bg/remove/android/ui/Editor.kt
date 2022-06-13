package photoeditor.cutout.backgrounderaser.bg.remove.android.ui

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.view.View.OnTouchListener
import android.widget.SeekBar
import android.widget.SeekBar.OnSeekBarChangeListener
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import photoeditor.cutout.backgrounderaser.bg.remove.android.R
import photoeditor.cutout.backgrounderaser.bg.remove.android.adapters.*
import photoeditor.cutout.backgrounderaser.bg.remove.android.util.*

import android.graphics.Canvas
import android.animation.Animator
import photoeditor.cutout.backgrounderaser.bg.remove.android.databinding.ActivityEditorBinding


class Editor : AppCompatActivity(),
    BgAdapter.clikHandleBg,  OnTouchListener {

    private lateinit var binding: ActivityEditorBinding

    var firsTime = true




    var imagePath: String = ""
    var bgRemover: Boolean = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEditorBinding.inflate(layoutInflater)
        setContentView(binding.root)

        System.loadLibrary("NativeImageProcessor");
        changeStatusBarColor()
        startEditor()

        binding.toolbar.next.setOnClickListener {

               val bitmap = viewToBitmap()
               savingAnimation()
               saveImage(this, bitmap)

        }

        binding.toolbar.back.setOnClickListener {
            backPressDialog(this)

        }

//        val iv_image = StickerViewImage(this@Editor)
//        iv_image.setOnTouchListener(this);

    }
    fun savingAnimation ()
    {
        binding.saving.visibility=View.VISIBLE
        binding.saving.playAnimation()
        binding.saving.addAnimatorListener(object : Animator.AnimatorListener {
            override fun onAnimationStart(animation: Animator) {
            }

            override fun onAnimationEnd(animation: Animator) {
                Toast.makeText(this@Editor, "Saved Successfully", Toast.LENGTH_SHORT).show()
                binding.saving.visibility=View.GONE

            }

            override fun onAnimationCancel(animation: Animator) {
            }

            override fun onAnimationRepeat(animation: Animator) {
            }
        })
    }


    fun viewToBitmap(): Bitmap {
        val bitmap = Bitmap.createBitmap(
            binding.bgMain.measuredWidth,
            binding.bgMain.measuredHeight,
            Bitmap.Config.ARGB_8888
        )

        val canvas = Canvas(bitmap)
        binding.bgMain.draw(canvas)
        return bitmap
    }

    fun getImagePath() {
        imagePath = intent.getStringExtra("path").toString()
        bgRemover = intent.getBooleanExtra("remove_bg", true)
    }


    private fun startEditor() {
        CoroutineScope(IO).launch {

            getImagePath()
            bitmap = getBitmapFromUri(this@Editor, Uri.parse(imagePath))
            if (bitmap == null) {
                return@launch
            }
            val ram = checkRAM(this@Editor)
            bitmap = resizeImage(ram, bitmap!!.width, bitmap!!.height)
            if (bgRemover) {
                bitmap = segmentation(bitmap!!, binding.image)
            }
            adjustedImage = bitmap

            withContext(Main)
            {

                binding.loading.visibility = View.GONE
                setBitmap()
                initializeEditorOptionsList()
//                initializeEditorOptionsRecyclerView()
                switchOriginalVsEditedView()
                firsTime = false
            }
        }
    }

    private fun setBitmap() {
        binding.image.setImageBitmap(bitmap)
    }

    @SuppressLint("ClickableViewAccessibility")
    fun switchOriginalVsEditedView() {
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

        if (!firsTime) {
            setBitmap()
        }
        super.onResume()

    }

    private fun changeStatusBarColor() {
        window.statusBarColor = ContextCompat.getColor(this, R.color.black)
    }

    private fun initializeEditorOptionsList() {
//        editorOptions.add(GenerealEditorModel("Filter", R.drawable.ic_effect))
//        editorOptions.add(GenerealEditorModel("Stickers", R.drawable.ic_sticker))
        // editorOptions.add(GenerealEditorModel("Text", R.drawable.ic_text))
//        editorOptions.add(GenerealEditorModel("Adjustments", R.drawable.ic_adjustment))
//        editorOptions.add(GenerealEditorModel("Crop", R.drawable.ic_crop))
//        editorOptions.add(GenerealEditorModel("Backgrounds", R.drawable.ic_background))

        binding.recyclerEditingOptions.visibility = View.GONE
        binding.bgsMainLayout.visibility = View.VISIBLE
        bgClickHandlers()

    }


//
//    override fun onBgClick() {
//        binding.recyclerEditingOptions.visibility = View.GONE
//        binding.bgsMainLayout.visibility = View.VISIBLE
//        bgClickHandlers()
//    }

    private fun bgClickHandlers() {
        binding.colorBg.setOnClickListener {

            val colors = initializeColorsList()
            initializeBgRecyclerView(colors,"Colors")

            binding.bgsMainLayout.visibility = View.GONE
            binding.colorsMainsLayout.visibility = View.VISIBLE

            binding.bgType.text = "Colors"


        }

        binding.gradientBg.setOnClickListener {

            val gradients = initializeGradientList()
            initializeBgRecyclerView(gradients,"Gradients")

            binding.bgsMainLayout.visibility = View.GONE
            binding.colorsMainsLayout.visibility = View.VISIBLE

            binding.bgType.text = "Gradient"

        }

        binding.imagesBg.setOnClickListener {

            val images = initializeImagesList()
            initializeBgRecyclerView(images,"Images")

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
            binding.bgMain.setBackgroundColor(resources.getColor(R.color.transparent))

        }
        binding.colorNext.setOnClickListener {
            binding.bgsMainLayout.visibility = View.VISIBLE
            binding.colorsMainsLayout.visibility = View.GONE
        }

    }

    private fun initializeBgRecyclerView(filters: ArrayList<Int>,type:String) {
        binding.recyclerColors.layoutManager =
            LinearLayoutManager(this@Editor, LinearLayoutManager.HORIZONTAL, false)
        binding.recyclerColors.adapter = BgAdapter(this@Editor, filters,type, this)
    }

    override fun onClickBg(bg: Int, position: Int) {
        binding.bgMain.setBackgroundResource(bg)
    }

    companion object {
        var bitmap: Bitmap? = null
        var filteredImage: Bitmap? = null
        var adjustedImage: Bitmap? = null
        val addedStickers: ArrayList<ClipArt> = ArrayList()
    }

    override fun onBackPressed() {
        backPressDialog(this)
    }

    override fun onTouch(p0: View?, p1: MotionEvent?): Boolean {
        TODO("Not yet implemented")
    }


}


