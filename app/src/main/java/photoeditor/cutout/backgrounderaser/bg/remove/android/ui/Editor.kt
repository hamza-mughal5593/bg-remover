package photoeditor.cutout.backgrounderaser.bg.remove.android.ui

import android.animation.Animator
import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.net.Uri
import android.os.Bundle
import android.view.KeyEvent
import android.view.MotionEvent
import android.view.View
import android.view.View.OnTouchListener
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.TextView.OnEditorActionListener
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import photoeditor.cutout.backgrounderaser.bg.remove.android.R
import photoeditor.cutout.backgrounderaser.bg.remove.android.adapters.*
import photoeditor.cutout.backgrounderaser.bg.remove.android.databinding.ActivityEditorBinding
import photoeditor.cutout.backgrounderaser.bg.remove.android.util.*


class Editor : AppCompatActivity(),
    BgAdapter.clikHandleBg, OnTouchListener {

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


//        binding.toolbar.back.setOnClickListener {
//            backPressDialog(this)
//
//        }

//        val iv_image = StickerViewImage(this@Editor)
//        iv_image.setOnTouchListener(this);

    }

    fun savingAnimation() {
        binding.saving.visibility = View.VISIBLE
        binding.saving.playAnimation()
        binding.saving.addAnimatorListener(object : Animator.AnimatorListener {
            override fun onAnimationStart(animation: Animator) {
            }

            override fun onAnimationEnd(animation: Animator) {
                Toast.makeText(this@Editor, "Saved Successfully", Toast.LENGTH_SHORT).show()
                binding.saving.visibility = View.GONE

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

        bgClickHandlers()

    }


//
//    override fun onBgClick() {
//        binding.recyclerEditingOptions.visibility = View.GONE
//        binding.bgsMainLayout.visibility = View.VISIBLE
//        bgClickHandlers()
//    }

    private fun bgClickHandlers() {


        val images = initializeImagesList()

        binding.saveMain.visibility = View.GONE
        binding.optionsLayout.visibility = View.VISIBLE

        binding.image1.setOnClickListener {
            binding.bgMain.setBackgroundResource(R.drawable.bg1)
            binding.optionsLayout.visibility = View.GONE
            binding.saveMain.visibility = View.VISIBLE
        }
        binding.image2.setOnClickListener {
            binding.bgMain.setBackgroundResource(R.drawable.bg2)
            binding.optionsLayout.visibility = View.GONE
            binding.saveMain.visibility = View.VISIBLE
        }




        binding.email.setOnEditorActionListener(
            OnEditorActionListener { v, actionId, event -> // Identifier of the action. This will be either the identifier you supplied,
                // or EditorInfo.IME_NULL if being called due to the enter key being pressed.
                if (actionId == EditorInfo.IME_ACTION_SEARCH || actionId == EditorInfo.IME_ACTION_DONE || (event.action === KeyEvent.ACTION_DOWN
                            && event.keyCode === KeyEvent.KEYCODE_ENTER)
                ) {

                    val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
                    imm?.hideSoftInputFromWindow(v.windowToken, 0)

                    val bitmap = viewToBitmap()
                    savingAnimation()
                    saveImage(this, bitmap, binding.email.toString())
                    return@OnEditorActionListener true
                }
                // Return true if you have consumed the action, else false.
                false
            })

//        binding.colorNext.setOnClickListener {
//
//
//
//
//            val bitmap = viewToBitmap()
//            savingAnimation()
//            saveImage(this, bitmap)
//        }

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


