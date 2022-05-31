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
//    , View.OnTouchListener
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
            abc(bitmap!!)
        })

        binding.btn.setOnClickListener {

         abc2(bitmap!!,binding.value.text.toString().toFloat())

        }

    }


//    private fun doBackgroundRemoval(frame: Mat): Mat? {
//        // init
//        val hsvImg = Mat()
//        val hsvPlanes: List<Mat> = ArrayList()
//        val thresholdImg = Mat()
//        var thresh_type = Imgproc.THRESH_BINARY_INV
//        thresh_type = Imgproc.THRESH_BINARY
//
//        // threshold the image with the average hue value
//        hsvImg.create(frame.size(), CvType.CV_8U)
//        Imgproc.cvtColor(frame, hsvImg, Imgproc.COLOR_BGR2HSV)
//        Core.split(hsvImg, hsvPlanes)
//
//        // get the average hue value of the image
//        val threshValue: Double = getHistAverage(hsvImg, hsvPlanes[0])
//        threshold(hsvPlanes[0], thresholdImg, threshValue, 78.0, thresh_type)
//        Imgproc.blur(thresholdImg, thresholdImg, Size(1.toDouble(), 1.toDouble()))
//
//        val kernel1 =
//            Imgproc.getStructuringElement(Imgproc.MORPH_ELLIPSE, Size(11.toDouble(), 11.toDouble()))
//        val kernel2 = Mat.ones(3, 3, CvType.CV_8U)
//        // dilate to fill gaps, erode to smooth edges
//        Imgproc.dilate(thresholdImg, thresholdImg, kernel1, Point(-1.toDouble(), -1.toDouble()), 1)
//        Imgproc.erode(thresholdImg, thresholdImg, kernel2, Point(-1.toDouble(), -1.toDouble()), 7)
//        threshold(thresholdImg, thresholdImg, threshValue, 255.0, Imgproc.THRESH_BINARY_INV)
//
//        // create the new image
//        val foreground = Mat(
//            frame.size(), CvType.CV_8UC3, Scalar(
//                255.toDouble(),
//                255.toDouble(),
//                255.toDouble()
//            )
//        )
//        frame.copyTo(foreground, thresholdImg)
//        val img_bitmap =
//            Bitmap.createBitmap(foreground.cols(), foreground.rows(), Bitmap.Config.ARGB_8888)
//        Utils.matToBitmap(foreground, img_bitmap)
//        binding.image.setImageBitmap(img_bitmap)
//
//        return foreground
//    }


    fun getBitmapFromView(view: View): Bitmap? {
        //Define a bitmap with the same size as the view
        val returnedBitmap =
            Bitmap.createBitmap(view.getWidth(), view.getHeight(), Bitmap.Config.ARGB_8888)
        //Bind a canvas to it
        val canvas = Canvas(returnedBitmap)
        //Get the view's background
        val bgDrawable: Drawable = view.getBackground()
        if (bgDrawable != null) //has background drawable, then draw it on the canvas
            bgDrawable.draw(canvas) else  //does not have background drawable, then draw white background on the canvas
            canvas.drawColor(Color.WHITE)
        // draw the view on the canvas
        view.draw(canvas)
        //return the bitmap
        return returnedBitmap
    }




    fun abc(orignalImage: Bitmap) {
        val options = SelfieSegmenterOptions.Builder()
            .setDetectorMode(SelfieSegmenterOptions.SINGLE_IMAGE_MODE).enableRawSizeMask().build()
        val segmenter = Segmentation.getClient(options)
        var image: InputImage? = null
        try {
            image = InputImage.fromBitmap(orignalImage, 0)
        } catch (e: IOException) {
            e.printStackTrace()
        }


        segmenter.process(image!!)
            .addOnSuccessListener { segmentationMask ->
                // Store all information so we can reuse it if e.g. a background images is chosen
                mask = segmentationMask.buffer
                maskWidth = segmentationMask.width
                maskHeight = segmentationMask.height
                val maskedBitmap = convertByteBufferToBitmap(mask, maskWidth, maskHeight)
                abc(orignalImage, maskedBitmap!!.scale(orignalImage.width, orignalImage.height, true))
            }
            .addOnFailureListener { e ->
                println("Image processing failed: $e")
            }
    }


    private fun convertByteBufferToBitmap(
        byteBuffer: ByteBuffer,
        imgSizeX: Int,
        imgSizeY: Int
    ): Bitmap? {
        byteBuffer.rewind()
        byteBuffer.order(ByteOrder.nativeOrder())
        val bitmap = Bitmap.createBitmap(imgSizeX, imgSizeY, Bitmap.Config.ARGB_8888)
        val pixels = IntArray(imgSizeX * imgSizeY)

        for (i in 0 until imgSizeX * imgSizeY) {
            val a = byteBuffer.float
                pixels[i] = Color.argb((255 * a).toInt(), 0, 0, 0)
        }
        bitmap.setPixels(pixels, 0, imgSizeX, 0, 0, imgSizeX, imgSizeY)
        return bitmap
    }


    private fun convertByteBufferToBitmap2(
        byteBuffer: ByteBuffer,
        imgSizeX: Int,
        imgSizeY: Int,
        bitrate: Float
    ): Bitmap? {
        byteBuffer.rewind()
        byteBuffer.order(ByteOrder.nativeOrder())
        val bitmap = Bitmap.createBitmap(imgSizeX, imgSizeY, Bitmap.Config.ARGB_8888)
        val pixels = IntArray(imgSizeX * imgSizeY)

        for (i in 0 until imgSizeX * imgSizeY) {
                if(String.format("%.1f", byteBuffer.float).toFloat() >= bitrate)
                {
                    pixels[i] = Color.argb((255).toInt(), 0, 0, 0)

                }
        }
        bitmap.setPixels(pixels, 0, imgSizeX, 0, 0, imgSizeX, imgSizeY)
        return bitmap
    }



    fun abc2(orignalImage: Bitmap,value:Float) {
        val options = SelfieSegmenterOptions.Builder()
            .setDetectorMode(SelfieSegmenterOptions.SINGLE_IMAGE_MODE).enableRawSizeMask().build()
        val segmenter = Segmentation.getClient(options)
        var image: InputImage? = null
        try {
            image = InputImage.fromBitmap(orignalImage, 0)
        } catch (e: IOException) {
            e.printStackTrace()
        }


        segmenter.process(image!!)
            .addOnSuccessListener { segmentationMask ->
                // Store all information so we can reuse it if e.g. a background images is chosen
                mask = segmentationMask.buffer
                maskWidth = segmentationMask.width
                maskHeight = segmentationMask.height
                val maskedBitmap = convertByteBufferToBitmap2(mask, maskWidth, maskHeight,value)
                abc(orignalImage, maskedBitmap!!.scale(orignalImage.width, orignalImage.height, true))
            }
            .addOnFailureListener { e ->
                println("Image processing failed: $e")
            }
    }


    private fun cropBitmapWithMask(original: Bitmap, mask: Bitmap): Bitmap? {
        val w = mask.width
        val h = mask.height

        val cropped = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(cropped)
        val paint = Paint(Paint.ANTI_ALIAS_FLAG)
        paint.xfermode = PorterDuffXfermode(PorterDuff.Mode.DST_IN)
        canvas.drawBitmap(original, 0.0f, 0.0f, null)
        canvas.drawBitmap(mask, 0.0f, 0.0f, paint)
        paint.xfermode = null
        canvas.drawBitmap(cropped, 0.0f, 0.0f, Paint())
        return cropped
    }





    private fun cropBitmap1(bitmap: Bitmap, width: Int, height: Int): Bitmap? {
        val bmOverlay = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        val paint = Paint()
        paint.xfermode = PorterDuffXfermode(PorterDuff.Mode.CLEAR)
        val canvas = Canvas(bmOverlay)
        canvas.drawBitmap(bitmap, 0.toFloat(), 0.toFloat(), null)
        canvas.drawRect(30F, 30F, 100F, 100F, paint)
        return bmOverlay
    }


    private fun generateMaskBgImage(image: Bitmap, bg: Bitmap): Bitmap {
        val bgBitmap = Bitmap.createBitmap(image.width, image.height, image.config)

        for (y in 0 until maskHeight) {
            for (x in 0 until maskWidth) {
                val bgConfidence = ((1.0 - mask.float) * 255).toInt()
                var bgPixel = bg.getPixel(x, y)
                bgPixel = ColorUtils.setAlphaComponent(bgPixel, bgConfidence)
                bgBitmap.setPixel(x, y, bgPixel)
            }
        }
        mask.rewind()
        // return bgBitmap
        return mergeBitmaps(image, bgBitmap)
    }


    fun generateMaskImage(image: Bitmap): Bitmap {
        val maskBitmap = Bitmap.createBitmap(image.width, image.height, image.config)

        for (y in 0 until maskHeight) {
            for (x in 0 until maskWidth) {
                val bgConfidence = ((1.0 - mask.float) * 260).toInt()
                maskBitmap.setPixel(x, y, Color.argb(bgConfidence, 0, 255, 0))
            }
        }
        mask.rewind()
        return mergeBitmaps(image, maskBitmap)
    }


    private fun abc(orignal: Bitmap, mask: Bitmap): Bitmap {
        val result = Bitmap.createBitmap(mask.width, mask.height, Bitmap.Config.ARGB_8888)
        val mCanvas = Canvas(result)
        val paint = Paint()
        paint.isAntiAlias = true;
        paint.isFilterBitmap = true;
        paint.isDither = true;
        paint.xfermode = PorterDuffXfermode(PorterDuff.Mode.DST_IN)
        mCanvas.drawBitmap(orignal, 0.toFloat(), 0.toFloat(), null)
        mCanvas.drawBitmap(mask, 0.toFloat(), 0.toFloat(), paint)
        paint.xfermode = null
        binding.image.setImageBitmap(result)
        return result
        //  binding.image.setScaleType(ScaleType.CENTER)
        //binding.image.setBackgroundResource(R.drawable.background_frame)


    }


//    suspend fun grabcutAlgo(bit: Bitmap) {
//
//
//        val b = bit.copy(Bitmap.Config.ARGB_8888, true)
//        val tl = org.opencv.core.Point()
//        val br = org.opencv.core.Point()
//        //GrabCut part
//        val img = Mat()
//        Utils.bitmapToMat(b, img)
//        Imgproc.cvtColor(img, img, Imgproc.COLOR_RGBA2RGB)
//        val r = img.rows()
//        val c = img.cols()
//        val p1 = org.opencv.core.Point((c / 100).toDouble(), (r / 100).toDouble())
//        val p2 = org.opencv.core.Point((c - c / 100).toDouble(), (r - r / 100).toDouble())
////        val rect = Imgproc.rectangle(img, p1, p2, Scalar(255.0, 255.0, 255.0))
//        val rect = org.opencv.core.Rect(p1, p2)
//        //Rect rect = new Rect(tl, br);
//        var background = Mat(
//            img.size(), CvType.CV_8UC3,
//            Scalar(255.0, 255.0, 255.0)
//        )
//        val firstMask = Mat()
//        val bgModel = Mat()
//        val fgModel = Mat()
//        val mask: Mat
//        val source = Mat(1, 1, CvType.CV_8U, Scalar(Imgproc.GC_PR_FGD.toDouble()))
//        val dst = Mat()
//        Imgproc.grabCut(img, firstMask, rect, bgModel, fgModel, 5, Imgproc.GC_INIT_WITH_RECT)
//        Core.compare(firstMask, source, firstMask, Core.CMP_EQ)
//        val foreground = Mat(img.size(), CvType.CV_8UC3, Scalar(255.0, 255.0, 255.0))
//        img.copyTo(foreground, firstMask)
//        val color = Scalar(255.0, 0.0, 0.0, 255.0)
//        Imgproc.rectangle(img, tl, br, color)
//        val tmp = Mat()
//        Imgproc.resize(background, tmp, img.size())
//        background = tmp
//        mask = Mat(
//            foreground.size(), CvType.CV_8UC1,
//            Scalar(255.0, 255.0, 255.0)
//        )
//        Imgproc.cvtColor(foreground, mask, Imgproc.COLOR_BGR2GRAY)
//        threshold(mask, mask, 254.0, 255.0, Imgproc.THRESH_BINARY_INV)
//        println()
//        val vals = Mat(1, 1, CvType.CV_8UC3, Scalar(0.0))
//        background.copyTo(dst)
//        background.setTo(vals, mask)
//        Core.add(background, foreground, dst, mask)
//        val grabCutImage = Bitmap.createBitmap(dst.cols(), dst.rows(), Bitmap.Config.ARGB_8888)
//        val processedImage = Bitmap.createBitmap(dst.cols(), dst.rows(), Bitmap.Config.RGB_565)
//        Utils.matToBitmap(dst, grabCutImage)
////        val sampleImage : Mat = Mat()
////        dst.copyTo(sampleImage)
//        withContext(Main) {
//            binding.image.setImageBitmap(grabCutImage)
//        }
//        firstMask.release()
//        source.release()
//        bgModel.release()
//        fgModel.release()
//    }

//    fun fg ()
//    {
//        val TEXT_GRAPH = "./mask_rcnn_inception_v2_coco_2018_01_28.pbtxt"
//        val MODEL_WEIGHTS = "./frozen_inference_graph.pb"
//        val CLASSES_FILE = "./mscoco_labels"
//
//
//
//        val tmp = Mat(bitmap.getWidth(), bitmap.getHeight(), CV_8UC1)
//        Utils.bitmapToMat(bitmap, tmp)
//        var image = Imgcodecs.imread(img_path)
//        image = tmp
//        val size = image.size()
//        val cols = image.cols()
//        val rows = image.rows()
//        val h = size.height
//        val w = size.width
//        val hh = size.height.toInt()
//        val ww = size.width.toInt()
//        if (!image.empty()) {
//            val blob = Dnn.blobFromImage(image, 1.0, Size(w, h), Scalar(0), true, false)
//            // Load the network
//            val net: Net = Dnn.readNetFromTensorflow(MODEL_WEIGHTS, TEXT_GRAPH)
//            net.setPreferableBackend(Dnn.DNN_BACKEND_OPENCV)
//            net.setPreferableTarget(Dnn.DNN_TARGET_CPU)
//            net.setInput(blob)
//            val outputlayers = ArrayList<String>()
//            val outputMats = ArrayList<Mat>()
//            outputlayers.add("detection_out_final")
//            outputlayers.add("detection_masks")
//            net.forward(outputMats, outputlayers)
//            var numClasses = outputMats[0]
//            val numMasks = outputMats[1]
//            numClasses = numClasses.reshape(1, numClasses.total().toInt() / 7)
//            for (i in 0 until numClasses.rows()) {
//                val confidence = numClasses[i, 2][0]
//                //System.out.println(confidence);
//                // Mat objectMask=outputMats.get(i);
//                if (confidence > 0.5) {
//                    val classId = numClasses[i, 1][0].toInt()
//                    val label: String = classes.get(classId).toString() + ": " + confidence
//                    println(label)
//                    var left = (numClasses[i, 3][0] * cols).toInt()
//                    var top = (numClasses[i, 4][0] * rows).toInt()
//                    var right = (numClasses[i, 5][0] * cols).toInt()
//                    var bottom = (numClasses[i, 6][0] * rows).toInt()
//                    println("$left $top $right $bottom")
//                    left = max(0, min(left, cols - 1))
//                    top = max(0, min(top, rows - 1))
//                    right = max(0, min(right, cols - 1))
//                    bottom = max(0, min(bottom, rows - 1))
//                    val box = Rect(left, top, right - left + 1, bottom - top + 1)
//                    //Mat objectMask(numMasks.rows(), numMasks.size[3],CV_32F, numMasks.ptr<float>(i,classId));
//                    // Mat obj();
//                    val objectMask = Mat(numMasks.rows(), numMasks.cols(), CV_32F)
//                    rectangle(
//                        image,
//                        Point(box.x, box.y),
//                        Point(box.x + box.width, box.y + box.height),
//                        Scalar(255, 178, 50),
//                        3
//                    )
//                    /* String lab = format("%.2f", confidence);
//               if (!classes.isEmpty()){
//                  //CV_Assert(classId < (int)classes.size());
//                  if(classId<(int)classes.size()) {
//                      lab = classes.get(classId) + ":" + lab;
//                   }
//                }*/
//                    val color = Scalar(rng.nextInt(256), rng.nextInt(256), rng.nextInt(256))
//                    val maskThreshold = 0.3
//                    // Resize the mask, threshold, color and apply it on the image
//                    resize(
//                        objectMask,
//                        objectMask,
//                        Size(box.width.toDouble(), box.height.toDouble())
//                    )
//                    threshold(
//                        objectMask,
//                        objectMask,
//                        255 * maskThreshold,
//                        255.0,
//                        Imgproc.THRESH_BINARY
//                    )
//                    // Mat mask = (objectMask > maskThreshold);
//                    val ili = Mat()
//                    multiply(image, Scalar(0.7), ili)
//                    val coloredRoi = Mat()
//                    add(ili, Scalar(0.3).mul(color), coloredRoi)
//                    coloredRoi.convertTo(coloredRoi, CV_8UC3)
//                    val contours: List<MatOfPoint>? = null
//                    val hierarchy: Mat? = null
//                    objectMask.convertTo(objectMask, CV_8U)
//                    findContours(objectMask, contours, hierarchy, RETR_CCOMP, CHAIN_APPROX_SIMPLE)
//                    drawContours(coloredRoi, contours, -1, color, 5, LINE_8, hierarchy, 100)
//                    coloredRoi.copyTo(image, objectMask)
//                }
//                val detectedFrame = Mat()
//                image.convertTo(detectedFrame, CV_8U)
//                Imgcodecs.imwrite("outputFile.jpg", detectedFrame)
//            }
//        }
//    }

}