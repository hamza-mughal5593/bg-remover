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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        changeStatusBarColor()
        handleMainSelection()
        handleOnClickSelectImage()


    }


    fun abc() {

    }


//    private fun doBackgroundRemoval(frame: Mat): Mat? {
//        // init
//        val hsvImg = Mat()
//        val hsvPlanes: List<Mat> = ArrayList()
//        val thresholdImg = Mat()
//
//        // threshold the image with the histogram average value
//        hsvImg.create(frame.size(), CvType.CV_8U)
//        Imgproc.cvtColor(frame, hsvImg, Imgproc.COLOR_BGR2HSV)
//        Core.split(hsvImg, hsvPlanes)
//        val threshValue = getHistAverage(hsvImg, hsvPlanes[0])
////        if (this.inverse.isSelected()) threshold(
////            hsvPlanes[0],
////            thresholdImg,
////            threshValue,
////            179.0,
////            Imgproc.THRESH_BINARY_INV
////        ) else
////
//        threshold(
//            hsvPlanes[0], thresholdImg, threshValue, 179.0, Imgproc.THRESH_BINARY
//        )
//        Imgproc.blur(thresholdImg, thresholdImg, Size(5.0, 5.0))
//
//        // dilate to fill gaps, erode to smooth edges
//        Imgproc.dilate(thresholdImg, thresholdImg, Mat(), Point(-1.0, 1.0), 6)
//        Imgproc.erode(thresholdImg, thresholdImg, Mat(), Point(-1.0, 1.0), 6)
//        threshold(thresholdImg, thresholdImg, threshValue, 179.0, Imgproc.THRESH_BINARY)
//
//        // create the new image
//        val foreground = Mat(frame.size(), CvType.CV_8UC3, Scalar(255.0, 255.0, 255.0))
//        frame.copyTo(foreground, thresholdImg)
//
//
//        //Imgproc.cvtColor(seedsImage, tmp, Imgproc.COLOR_RGB2BGRA);
//        // Imgproc.cvtColor(seedsImage, tmp, Imgproc.COLOR_GRAY2RGBA, 4)
//        val bmp = Bitmap.createBitmap(foreground.cols(), foreground.rows(), Bitmap.Config.ARGB_8888)
//        Utils.matToBitmap(foreground, bmp)
//
//        binding.img.setImageBitmap(bmp)
//
//        return foreground
//    }
//
//
//    private fun doCanny(frame: Mat): Mat? {
//        val threshold = 0.0
//        // init
//        val grayImage = Mat()
//        val detectedEdges = Mat()
//
//        // convert to grayscale
//        Imgproc.cvtColor(frame, grayImage, Imgproc.COLOR_BGR2GRAY)
//
//        // reduce noise with a 3x3 kernel
//        Imgproc.blur(grayImage, detectedEdges, Size(3.0, 3.0))
//
//        // canny detector, with ratio of lower:upper threshold of 3:1
//        Imgproc.Canny(detectedEdges, detectedEdges, threshold, threshold * 3, 3, false)
//
//        // using Canny's output as a mask, display the result
//        val dest = Mat()
//        Core.add(dest, Scalar.all(0.0), dest)
//        frame.copyTo(dest, detectedEdges)
//        return dest
//    }
//
//
//    private fun getHistAverage(hsvImg: Mat, hueValues: Mat): Double {
//        // init
//        var average = 0.0
//        val hist_hue = Mat()
//        val histSize = MatOfInt(180)
//        val hue: MutableList<Mat> = ArrayList()
//        hue.add(hueValues)
//
//        // compute the histogram
//        Imgproc.calcHist(
//            hue,
//            MatOfInt(0),
//            Mat(),
//            hist_hue,
//            histSize,
//            MatOfFloat(0.toFloat(), 179.toFloat())
//        )
//
//        // get the average for each bin
//        for (h in 0..179) {
//            average += hist_hue[h, 0][0] * h
//        }
//        return average / hsvImg.size().height / hsvImg.size().width.also { average = it }
//    }
//

    private fun changeStatusBarColor() {
        window.statusBarColor = ContextCompat.getColor(this, R.color.black)
    }

    private fun handleMainSelection() {
        binding.cvBgRemover.setOnClickListener {

            showImagePickerLayout()
            backPress = true
        }
        binding.cvEditor.setOnClickListener {

            // showImagePickerLayout()
            // backPress = true
            val intent = Intent(this@MainActivity, Editor::class.java)
            startActivity(intent)

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
                    Log.i("BBC", "onActivityResult: ${imagePath}")

                    val intent = Intent(this, RemoveBG::class.java)
                    intent.putExtra("path", imagePath)
                    startActivity(intent)
//
//                    try {
//                        val bitmap = MediaStore.Images.Media.getBitmap(
//                            this.getContentResolver(),
//                            Uri.parse(data.data!!.toString())
//                        )
//
//                        val mat = Mat()
//                        val bmp32: Bitmap = bitmap.copy(Bitmap.Config.ARGB_8888, true)
//                        Utils.bitmapToMat(bmp32, mat)
//
//
//                       // var man = Imgcodecs.imread(data.data!!.toString())
//                    //   val man = doCanny(mat)
//                        doBackgroundRemoval(mat)
//
//                    } catch (e: java.lang.Exception) {
//
//                        Log.i("BBC", "onActivityResult: Exception ${e.message}")
//
//                    }

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