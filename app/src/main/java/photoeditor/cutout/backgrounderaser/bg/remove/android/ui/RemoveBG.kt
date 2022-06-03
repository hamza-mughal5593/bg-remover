package photoeditor.cutout.backgrounderaser.bg.remove.android.ui

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.*
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.annotation.Nullable
import androidx.appcompat.app.AppCompatActivity
import androidx.core.graphics.ColorUtils
import androidx.core.graphics.scale
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.segmentation.Segmentation
import com.google.mlkit.vision.segmentation.selfie.SelfieSegmenterOptions
import photoeditor.cutout.backgrounderaser.bg.remove.android.databinding.ActivityRemoveBgBinding
import photoeditor.cutout.backgrounderaser.bg.remove.android.util.mergeBitmaps
import java.io.IOException
import java.nio.ByteBuffer
import java.nio.ByteOrder


class RemoveBG : AppCompatActivity()
{

    private lateinit var binding: ActivityRemoveBgBinding
    lateinit var mask: ByteBuffer
    var maskWidth: Int = 0
    var maskHeight: Int = 0
    var bitmap: Bitmap? = null


    @SuppressLint("ClickableViewAccessibility")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityRemoveBgBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val path = intent.getStringExtra("path")
        binding.image.setImageURI(Uri.parse(path))


        Log.i("BBC", "onCreate: ${binding.layout.measuredWidth}")
        Log.i("BBC", "onCreate: ${binding.layout.measuredHeight}")

//        CutOut.activity()
//            .src(Uri.parse(path))
//            .bordered()
//            .noCrop()
//            .intro()
//            .start(this);

        binding.image.post(Runnable {

            // val bitmap=  getBitmapFromView(binding.layout)
            binding.image.setDrawingCacheEnabled(true)
            binding.image.buildDrawingCache()
            bitmap = Bitmap.createBitmap(binding.image.getDrawingCache())
         //   abc(bitmap!!)
        })

    }

}