package photoeditor.cutout.backgrounderaser.bg.remove.android.ui

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.isseiaoki.simplecropview.CropImageView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import photoeditor.cutout.backgrounderaser.bg.remove.android.R
import photoeditor.cutout.backgrounderaser.bg.remove.android.adapters.CropAdapter
import photoeditor.cutout.backgrounderaser.bg.remove.android.databinding.ActivityCropBinding
import photoeditor.cutout.backgrounderaser.bg.remove.android.models.CropModel


class CropActivity : AppCompatActivity(),CropAdapter.clickHandler {

    private lateinit var binding: ActivityCropBinding
    private val cropList:ArrayList<CropModel> = ArrayList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCropBinding.inflate(layoutInflater)
        setContentView(binding.root)
        changeStatusBarColor()
        initializeCropList()
        initializeCropRecyclerView()

        binding.cropImageView.imageBitmap=Editor.bitmap

        binding.crop.setOnClickListener {

            binding.progressBar.visibility= View.VISIBLE
//            val uri = saveToInternalStorage(this,Editor.bitmap!!,"Crop")
            CoroutineScope(IO).launch {
               val croppedBitmap=  binding.cropImageView.croppedBitmap
                Editor.bitmap=croppedBitmap
                withContext(Main)
                {
                    binding.progressBar.visibility= View.GONE
                    finish()
                }
            }

        }

        binding.cropBack.setOnClickListener {

            finish()
        }

        binding.toolbar.back.setOnClickListener {

            finish()
        }


    }


    override fun onResume() {
        super.onResume()
        binding.cropImageView.imageBitmap=Editor.bitmap

    }

    private fun initializeCropList ()
    {
        cropList.add(CropModel(CropImageView.CropMode.FREE,R.drawable.c_free))
        cropList.add(CropModel(CropImageView.CropMode.FIT_IMAGE,R.drawable.c_free))
        cropList.add(CropModel(CropImageView.CropMode.SQUARE,R.drawable.c_square))
       //  cropList.add(CropModel(CropImageView.CropMode.CIRCLE,R.drawable.model))
        cropList.add(CropModel(CropImageView.CropMode.RATIO_16_9,R.drawable.c_16_9))
        cropList.add(CropModel(CropImageView.CropMode.RATIO_9_16,R.drawable.c_9_16))
        cropList.add(CropModel(CropImageView.CropMode.RATIO_3_4,R.drawable.c_3_4))
        cropList.add(CropModel(CropImageView.CropMode.RATIO_4_3,R.drawable.c_4_3))
    }

    private fun initializeCropRecyclerView ()
    {
        binding.recyclerCrop.layoutManager = LinearLayoutManager(this@CropActivity, LinearLayoutManager.HORIZONTAL, false)
        binding.recyclerCrop.adapter = CropAdapter(this,cropList,this)
    }

    override fun onCropClick(mode: CropImageView.CropMode) {

        binding.cropImageView.setCropMode(mode)
    }

    private fun changeStatusBarColor() {
        window.statusBarColor = ContextCompat.getColor(this, R.color.black)
    }
}