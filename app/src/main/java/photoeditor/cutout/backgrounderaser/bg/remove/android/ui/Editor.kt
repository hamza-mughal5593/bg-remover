package photoeditor.cutout.backgrounderaser.bg.remove.android.ui

import android.animation.Animator
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.PorterDuff
import android.net.Uri
import android.os.Bundle
import android.view.KeyEvent
import android.view.MotionEvent
import android.view.View
import android.view.View.OnTouchListener
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.ImageView
import android.widget.TextView.OnEditorActionListener
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import io.paperdb.Paper
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import photoeditor.cutout.backgrounderaser.bg.remove.android.MainActivity
import photoeditor.cutout.backgrounderaser.bg.remove.android.R
import photoeditor.cutout.backgrounderaser.bg.remove.android.adapters.BgAdapter
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
Paper.init(this)
        System.loadLibrary("NativeImageProcessor");
        changeStatusBarColor()
        startEditor()


//        binding.toolbar.back.setOnClickListener {
//            backPressDialog(this)
//
//        }

//        val iv_image = StickerViewImage(this@Editor)
//        iv_image.setOnTouchListener(this);


        binding.retakePhoto.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            intent.putExtra("retake", "true")
            startActivity(intent)
            finish()
        }


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

        bgClickHandlers()

    }


    @SuppressLint("ClickableViewAccessibility")
    private fun bgClickHandlers() {


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
        binding.image3.setOnClickListener {
            binding.bgMain.setBackgroundResource(R.drawable.bg3)
            binding.optionsLayout.visibility = View.GONE
            binding.saveMain.visibility = View.VISIBLE
        }
        binding.image4.setOnClickListener {
            binding.bgMain.setBackgroundResource(R.drawable.bg4)
            binding.optionsLayout.visibility = View.GONE
            binding.saveMain.visibility = View.VISIBLE
        }
        binding.image5.setOnClickListener {
            binding.bgMain.setBackgroundResource(R.drawable.bgend)
            binding.optionsLayout.visibility = View.GONE
            binding.saveMain.visibility = View.VISIBLE
        }



        binding.image1.setOnTouchListener(OnTouchListener { v, event ->
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    val view: ImageView = v as ImageView
                    //overlay is black with transparency of 0x77 (119)
                    view.setBackgroundColor(resources.getColor(R.color.orange))
                }
                MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                    val view: ImageView = v as ImageView
                    //clear the overlay
                    view.setBackgroundColor(Color.TRANSPARENT)
                }
            }
            false
        })
        binding.image2.setOnTouchListener(OnTouchListener { v, event ->
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    val view: ImageView = v as ImageView
                    //overlay is black with transparency of 0x77 (119)
                    view.setBackgroundColor(resources.getColor(R.color.orange))
                }
                MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                    val view: ImageView = v as ImageView
                    //clear the overlay
                    view.setBackgroundColor(Color.TRANSPARENT)
                }
            }
            false
        })
        binding.image3.setOnTouchListener(OnTouchListener { v, event ->
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    val view: ImageView = v as ImageView
                    //overlay is black with transparency of 0x77 (119)
                    view.setBackgroundColor(resources.getColor(R.color.orange))
                }
                MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                    val view: ImageView = v as ImageView
                    //clear the overlay
                    view.setBackgroundColor(Color.TRANSPARENT)
                }
            }
            false
        })
        binding.image4.setOnTouchListener(OnTouchListener { v, event ->
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    val view: ImageView = v as ImageView
                    //overlay is black with transparency of 0x77 (119)
                    view.setBackgroundColor(resources.getColor(R.color.orange))
                }
                MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                    val view: ImageView = v as ImageView
                    //clear the overlay
                    view.setBackgroundColor(Color.TRANSPARENT)
                }
            }
            false
        })
        binding.image5.setOnTouchListener(OnTouchListener { v, event ->
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    val view: ImageView = v as ImageView
                    //overlay is black with transparency of 0x77 (119)
                    view.setBackgroundColor(resources.getColor(R.color.orange))
                }
                MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                    val view: ImageView = v as ImageView
                    //clear the overlay
                    view.setBackgroundColor(Color.TRANSPARENT)
                }
            }
            false
        })


        var list: ArrayList<String>? = null
        list = ArrayList<String>()
        list = Paper.book().read("emaillist", list)


        binding.email.setOnEditorActionListener(
            OnEditorActionListener { v, actionId, event -> // Identifier of the action. This will be either the identifier you supplied,
                // or EditorInfo.IME_NULL if being called due to the enter key being pressed.
                if (actionId == EditorInfo.IME_ACTION_SEARCH || actionId == EditorInfo.IME_ACTION_DONE || (event.action === KeyEvent.ACTION_DOWN
                            && event.keyCode === KeyEvent.KEYCODE_ENTER)
                ) {


                    list?.add(binding.email.text.toString())
                    Paper.book().write("emaillist", list!!)
                    val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
                    imm?.hideSoftInputFromWindow(v.windowToken, 0)

                    val bitmap = viewToBitmap()
                    savingAnimation()
                    saveImage(this, bitmap, binding.email.text.toString())
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
    }

    override fun onBackPressed() {
        backPressDialog(this)
    }

    override fun onTouch(p0: View?, p1: MotionEvent?): Boolean {
        TODO("Not yet implemented")
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 555) {
            val intent = Intent(this, ResultActivity::class.java)
            startActivity(intent)
        }
    }
}


