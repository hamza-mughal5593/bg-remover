package photoeditor.cutout.backgrounderaser.bg.remove.android.ui

import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.isseiaoki.simplecropview.CropImageView
import com.isseiaoki.simplecropview.callback.CropCallback
import photoeditor.cutout.backgrounderaser.bg.remove.android.R
import photoeditor.cutout.backgrounderaser.bg.remove.android.adapters.CropAdapter
import photoeditor.cutout.backgrounderaser.bg.remove.android.databinding.ActivityCropBinding
import photoeditor.cutout.backgrounderaser.bg.remove.android.models.CropModel
import photoeditor.cutout.backgrounderaser.bg.remove.android.util.saveToInternalStorage
import java.io.File


class CropActivity : AppCompatActivity(),CropAdapter.clickHandler {

    private lateinit var binding: ActivityCropBinding
    private val cropList:ArrayList<CropModel> = ArrayList()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCropBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initializeCropList()
        initializeCropRecyclerView()

        binding.crop.setOnClickListener {

           val uri = saveToInternalStorage(this,Editor.bitmap!!,"Crop")
            binding.cropImageView.crop(Uri.fromFile( File("${uri!!}/Crop").absoluteFile))
                .execute(object : CropCallback {
                    override fun onSuccess(cropped: Bitmap) {
                        Editor.bitmap=cropped
                        finish()
                    }

                    override fun onError(e: Throwable) {

                        Log.i("BBC", "onError: ${e.message}")
                    }
                })
        }


    }


    override fun onResume() {
        super.onResume()
        binding.cropImageView.imageBitmap=Editor.bitmap

    }

    private fun initializeCropList ()
    {
        cropList.add(CropModel(CropImageView.CropMode.FREE,R.drawable.model))
        cropList.add(CropModel(CropImageView.CropMode.FIT_IMAGE,R.drawable.model))
        cropList.add(CropModel(CropImageView.CropMode.SQUARE,R.drawable.model))
        cropList.add(CropModel(CropImageView.CropMode.CIRCLE,R.drawable.model))
        cropList.add(CropModel(CropImageView.CropMode.CIRCLE_SQUARE,R.drawable.model))
        cropList.add(CropModel(CropImageView.CropMode.RATIO_16_9,R.drawable.model))
        cropList.add(CropModel(CropImageView.CropMode.RATIO_9_16,R.drawable.model))
        cropList.add(CropModel(CropImageView.CropMode.RATIO_3_4,R.drawable.model))
        cropList.add(CropModel(CropImageView.CropMode.RATIO_4_3,R.drawable.model))
    }

    private fun initializeCropRecyclerView ()
    {
        binding.recyclerCrop.layoutManager = LinearLayoutManager(this@CropActivity, LinearLayoutManager.HORIZONTAL, false)
        binding.recyclerCrop.adapter = CropAdapter(cropList,this)
    }

    override fun onCropClick(mode: CropImageView.CropMode) {

        binding.cropImageView.setCropMode(mode)
    }
}