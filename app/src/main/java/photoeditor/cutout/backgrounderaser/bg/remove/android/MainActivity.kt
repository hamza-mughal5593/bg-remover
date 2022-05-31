package photoeditor.cutout.backgrounderaser.bg.remove.android

import android.content.Intent
import android.graphics.Bitmap
import android.media.Image
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.google.android.material.bottomsheet.BottomSheetDialog
import photoeditor.cutout.backgrounderaser.bg.remove.android.databinding.ActivityMainBinding
import photoeditor.cutout.backgrounderaser.bg.remove.android.databinding.BottomSheetImagePickerBinding
import photoeditor.cutout.backgrounderaser.bg.remove.android.ui.RemoveBG
import java.io.ByteArrayInputStream
import photoeditor.cutout.backgrounderaser.bg.remove.android.ui.Editor


class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private var backPress: Boolean = false
    var bgRemover=true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        changeStatusBarColor()
        handleMainSelection()
        handleOnClickSelectImage()


    }


    private fun changeStatusBarColor() {
        window.statusBarColor = ContextCompat.getColor(this, R.color.black)
    }

    private fun handleMainSelection() {
        binding.cvBgRemover.setOnClickListener {

            showImagePickerLayout()
            backPress = true
            bgRemover=true
        }
        binding.cvEditor.setOnClickListener {

            showImagePickerLayout()
            backPress = true
            bgRemover=false

        }
    }

    fun showImagePickerLayout() {
        binding.selectionLayout.visibility = View.GONE
        binding.selectImageLayout.visibility = View.VISIBLE
    }

    fun showMainSelectionLayout() {
        binding.selectionLayout.visibility = View.VISIBLE
        binding.selectImageLayout.visibility = View.GONE
    }

    fun handleOnClickSelectImage() {
        binding.selectImage.setOnClickListener {

            showBottomSheet()

        }
    }

    fun showBottomSheet() {
        val binding: BottomSheetImagePickerBinding =
            BottomSheetImagePickerBinding.inflate(LayoutInflater.from(this))
        val bottomSheetDialog = BottomSheetDialog(this)
        bottomSheetDialog.setContentView(binding.root)
        bottomSheetDialog.show()

        binding.gallery.setOnClickListener {
            openGallery()
        }

    }

    fun openGallery() {

        val intent = Intent(
            Intent.ACTION_PICK,
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI
        )
        intent.type = "image/*"
        intent.action = Intent.ACTION_GET_CONTENT
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), 1)

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        try {

            if (requestCode == 1 && data != null) {
                if (data.data != null) {
                    val imagePath: String = data.data!!.toString()

                      if(bgRemover)
                      {
                          val intent = Intent(this, RemoveBG::class.java)
                          intent.putExtra("path", imagePath)
                          startActivity(intent)
                      }else
                      {
                          val intent = Intent(this, Editor::class.java)
                          intent.putExtra("path", imagePath)
                          startActivity(intent)
                      }

                }
            }

        } catch (e: Exception) {
            Log.i("BBC", "onActivityResult: ${e}")
        }

    }


    override fun onBackPressed() {
        if (backPress) {
            showMainSelectionLayout()
            backPress = false

        } else {
            super.onBackPressed()

        }
    }


}