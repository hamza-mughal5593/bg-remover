package photoeditor.cutout.backgrounderaser.bg.remove.android

import photoeditor.cutout.backgrounderaser.bg.remove.android.R
import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.GravityCompat
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.navigation.NavigationView
import photoeditor.cutout.backgrounderaser.bg.remove.android.databinding.ActivityMainBinding
import photoeditor.cutout.backgrounderaser.bg.remove.android.databinding.BottomSheetImagePickerBinding
import photoeditor.cutout.backgrounderaser.bg.remove.android.ui.Editor
import photoeditor.cutout.backgrounderaser.bg.remove.android.ui.RemoveBG
import photoeditor.cutout.backgrounderaser.bg.remove.android.util.openPrivacyPolicy
import photoeditor.cutout.backgrounderaser.bg.remove.android.util.rate_dialog
import photoeditor.cutout.backgrounderaser.bg.remove.android.util.saveCaptureImage
import photoeditor.cutout.backgrounderaser.bg.remove.android.util.shareApp


class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    private lateinit var binding: ActivityMainBinding
    private var backPress: Boolean = false
    var bgRemover=true
    var actionBarDrawerToggle: ActionBarDrawerToggle? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        changeStatusBarColor()
        setUpDrawer()
        handleMainSelection()
        handleOnClickSelectImage()


    }


    private fun changeStatusBarColor() {
        window.statusBarColor = ContextCompat.getColor(this, R.color.black)
    }

    fun setUpDrawer() {
        actionBarDrawerToggle = ActionBarDrawerToggle(this, binding.drawerLayout, R.string.app_name, R.string.app_name)
        actionBarDrawerToggle!!.syncState();

        binding.toolbar.drawer.setOnClickListener {
            binding.drawerLayout.openDrawer(GravityCompat.START)
        }
        binding.navView.setNavigationItemSelectedListener(this)  // for drawer item clicks
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
        binding.camera.setOnClickListener {

            openCamera()

        }


    }

    fun openCamera ()
    {
        val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        startActivityForResult(cameraIntent, 2)
    }


    private fun openGallery() {

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

            if (requestCode == 1 && data != null)
            {
                if (data.data != null) {
                    val imagePath: String = data.data!!.toString()

                    val intent = Intent(this, Editor::class.java)

                    if(bgRemover)
                      {
                          intent.putExtra("remove_bg", true)
                      }else
                      {
                          intent.putExtra("remove_bg", false)
                      }
                    intent.putExtra("path", imagePath)
                    startActivity(intent)

                }
            }else if (requestCode == 2 && data !=null)
            {
                if(data.extras !=null)
                {
                    val image = data.extras!!.get("data")
                    if(bgRemover)
                    {
                        val intent = Intent(this, RemoveBG::class.java)
                        intent.putExtra("path", saveCaptureImage(this,(image) as Bitmap,"name"))
                        startActivity(intent)
                    }else
                    {
                        val intent = Intent(this, Editor::class.java)
                        intent.putExtra("path", saveCaptureImage(this,(image) as Bitmap,"name"))
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

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        binding.drawerLayout.close()

        if (item.itemId == R.id.nav_share) {
            shareApp(this)
        } else if (item.itemId == R.id.nav_privacy) {
            openPrivacyPolicy(this)
        }
        else if(item.itemId == R.id.nav_rate)
        {
            rate_dialog(this)
        }else if (item.itemId ==R.id.nav_remove_ads)
        {
//            if(getfromSharedPrefs())
//            {
//                Toast.makeText(this, "Ads Are Already Removed", Toast.LENGTH_SHORT).show()
//            }else
//            {
//                startActivity(Intent(this,Splash::class.java))
//                finish()
//            }
        }
        return true
    }

}