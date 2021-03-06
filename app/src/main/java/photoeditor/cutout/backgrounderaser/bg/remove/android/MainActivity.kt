package photoeditor.cutout.backgrounderaser.bg.remove.android

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.text.TextUtils
import android.util.Log
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import io.paperdb.Paper
import photoeditor.cutout.backgrounderaser.bg.remove.android.databinding.ActivityMainBinding
import photoeditor.cutout.backgrounderaser.bg.remove.android.ui.Editor
import photoeditor.cutout.backgrounderaser.bg.remove.android.util.getBitmapFromContentUri
import photoeditor.cutout.backgrounderaser.bg.remove.android.util.getOutputDirectory
import photoeditor.cutout.backgrounderaser.bg.remove.android.util.saveCaptureImage
import java.io.BufferedWriter
import java.io.File
import java.io.FileWriter
import java.io.IOException


class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private var backPress: Boolean = false
    var bgRemover = true
    private val PERMISSION_REQUEST_CODE = 200

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Paper.init(this)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)


        val id = intent.getStringExtra("retake")
        if (id != null)
            if (id == "true") {
                if (checkPermission()) {
                    openCamera()

                } else {
                    requestPermission()
                }
            }


binding.download.setOnClickListener {
    try {

        var list: ArrayList<String>? = null
        list = ArrayList<String>()
        list = Paper.book().read("emaillist", list)

        val joined = TextUtils.join(", ", list!!)

        val path = getOutputDirectory(this).absolutePath
        val file = File(path,"email_list.csv")
//            val file: File = File(fileName.toString() + ".csv")
        // if file doesnt exists, then create it
        if (!file.exists()) {
            file.createNewFile()
        }
        val fw = FileWriter(file.absoluteFile)
        val bw = BufferedWriter(fw)
        bw.write(joined)

        bw.close()
        Toast.makeText(this, "CSV file saved", Toast.LENGTH_SHORT).show()

    } catch (e: IOException) {
        e.printStackTrace()
    }
}





        binding.opencam.setOnClickListener {


//
//            val email = "busybeeapps2k21@gmail.com"
//            val subject = "${resources.getString(R.string.app_name)} Feedback"
//            val body = ""
//
//            val selectorIntent = Intent(Intent.ACTION_SENDTO)
//            val urlString =
//                "mailto:" + Uri.encode(email) + "?subject=" + Uri.encode(subject) + "&body=" + Uri.encode(
//                    body
//                )
//            selectorIntent.data = Uri.parse(urlString)
//
//            val emailIntent = Intent(Intent.ACTION_SEND)
//            emailIntent.putExtra(Intent.EXTRA_EMAIL, arrayOf(email))
//            emailIntent.putExtra(Intent.EXTRA_SUBJECT, subject)
//            emailIntent.putExtra(Intent.EXTRA_TEXT, body)
//            emailIntent.selector = selectorIntent
//
//            startActivity(Intent.createChooser(emailIntent, "Send email"))


//            CoroutineScope(Dispatchers.IO).launch{
//                var sad =  SendMail()
//            }

            if (checkPermission()) {
                openCamera()

            } else {
                requestPermission()
            }
        }


    }


    private fun checkPermission(): Boolean {
        return ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.CAMERA
        ) == PackageManager.PERMISSION_GRANTED
    }

    private fun requestPermission() {
        ActivityCompat.requestPermissions(
            this, arrayOf(Manifest.permission.CAMERA),
            PERMISSION_REQUEST_CODE
        )
    }


    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == PERMISSION_REQUEST_CODE) {
            if (grantResults.size > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                openCamera()
            } else {
                Toast.makeText(this, "Permission not granted", Toast.LENGTH_SHORT).show()
            }
        }
    }

    fun openCamera() {
//        val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
//        cameraIntent.action = MediaStore.EXTRA_OUTPUT
//        startActivityForResult(cameraIntent, 2)


        val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        val outputDir: File = getCacheDir()
        var file: File? = null
        try {
            file = File.createTempFile("img", "jpg", outputDir)
        } catch (e: IOException) {
            e.printStackTrace()
        }
        if (file != null) {

            mImageUri = FileProvider.getUriForFile(this, "${this.packageName}.provider", file)
            //mImageUri = File.toString();    //If I use String instead of an Uri, it works better (ie, can accept camera photo)
            cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, mImageUri)
            startActivityForResult(cameraIntent, 2)
        }


    }

    var mImageUri: Uri? = null


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        try {

            if (requestCode == 1 && data != null) {
                if (data.data != null) {
                    val imagePath: String = data.data!!.toString()

                    val intent = Intent(this, Editor::class.java)

                    if (bgRemover) {
                        intent.putExtra("remove_bg", true)
                    } else {
                        intent.putExtra("remove_bg", false)
                    }
                    intent.putExtra("path", imagePath)
                    startActivity(intent)

                }
            } else if (requestCode == 2) {
//                if(data.extras !=null)
//                {
//                    val image = data.extras!!.get("data")
//    val image = getBitmapFromUri(this,mImageUri!!)
                val image = getBitmapFromContentUri(this.contentResolver, mImageUri)



                if (bgRemover) {
                    val intent = Intent(this, Editor::class.java)
                    intent.putExtra("path", saveCaptureImage(this, (image) as Bitmap, "name"))
                    startActivity(intent)
                } else {
                    val intent = Intent(this, Editor::class.java)
                    intent.putExtra("path", saveCaptureImage(this, (image) as Bitmap, "name"))
                    startActivity(intent)
                }
//                }

            }

        } catch (e: Exception) {
            Log.i("BBC", "onActivityResult: ${e}")
        }

    }


    override fun onBackPressed() {
        if (backPress) {
            backPress = false

        } else {
            super.onBackPressed()

        }
    }


}