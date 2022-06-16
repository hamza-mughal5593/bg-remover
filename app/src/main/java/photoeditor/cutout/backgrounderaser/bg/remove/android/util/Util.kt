package photoeditor.cutout.backgrounderaser.bg.remove.android.util

import android.app.Activity
import android.app.ActivityManager
import android.content.*
import android.content.pm.ResolveInfo
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.graphics.Matrix
import android.graphics.drawable.ColorDrawable
import android.media.MediaScannerConnection
import android.net.Uri
import android.os.ParcelFileDescriptor
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.widget.TextView
import androidx.annotation.Nullable
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import androidx.exifinterface.media.ExifInterface
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import photoeditor.cutout.backgrounderaser.bg.remove.android.MainActivity
import photoeditor.cutout.backgrounderaser.bg.remove.android.R
import photoeditor.cutout.backgrounderaser.bg.remove.android.ui.Editor
import photoeditor.cutout.backgrounderaser.bg.remove.android.ui.ResultActivity
import java.io.File
import java.io.FileDescriptor
import java.io.FileOutputStream
import java.io.IOException
import kotlin.math.roundToInt


fun checkRAM(context: Context): Int {
    val mi = ActivityManager.MemoryInfo()
    val activityManager =
        context.getSystemService(AppCompatActivity.ACTIVITY_SERVICE) as ActivityManager
    activityManager.getMemoryInfo(mi)
    return (mi.totalMem / (1000f * 1000f * 1000f)).roundToInt()
}

fun resizeImage(available: Int, bitmapWidth: Int, bitmapHeight: Int): Bitmap {
    var divisor = 0

    val maxValue = if (available == 1) {
        1000
    } else {
         (180 * available)
    }

    return if (bitmapHeight > maxValue || bitmapWidth > maxValue) {
        divisor = if (bitmapHeight > bitmapWidth) {
            bitmapHeight / maxValue
        } else {
            bitmapWidth / maxValue
        }
        Bitmap.createScaledBitmap(
            Editor.bitmap!!,
            bitmapWidth / divisor,
            bitmapHeight / divisor,
            true
        )
    } else {
        Editor.bitmap!!
    }
}

@Nullable
@Throws(IOException::class)
fun getBitmapFromContentUri(contentResolver: ContentResolver?, imageUri: Uri?): Bitmap? {
    var decodedBitmap: Bitmap? = null
    try {
        decodedBitmap = MediaStore.Images.Media.getBitmap(contentResolver, imageUri)
    } catch (e: java.lang.Exception) {
        Log.e("TAG", "getBitmapFromContentUri: " + e.localizedMessage)
    }
    if (decodedBitmap == null) {
        return null
    }
    val orientation: Int = getExifOrientationTag(contentResolver!!, imageUri!!)
    var rotationDegrees = 0
    var flipX = false
    var flipY = false
    when (orientation) {
        ExifInterface.ORIENTATION_FLIP_HORIZONTAL -> flipX = true
        ExifInterface.ORIENTATION_ROTATE_90 -> rotationDegrees = 90
        ExifInterface.ORIENTATION_TRANSPOSE -> {
            rotationDegrees = 90
            flipX = true
        }
        ExifInterface.ORIENTATION_ROTATE_180 -> rotationDegrees = 180
        ExifInterface.ORIENTATION_FLIP_VERTICAL -> flipY = true
        ExifInterface.ORIENTATION_ROTATE_270 -> rotationDegrees = -90
        ExifInterface.ORIENTATION_TRANSVERSE -> {
            rotationDegrees = -90
            flipX = true
        }
        ExifInterface.ORIENTATION_UNDEFINED, ExifInterface.ORIENTATION_NORMAL -> {
        }
        else -> {
        }
    }
    return rotateBitmap(decodedBitmap, rotationDegrees, flipX, flipY)
}
private fun getExifOrientationTag(resolver: ContentResolver, imageUri: Uri): Int {
    // We only support parsing EXIF orientation tag from local file on the device.
    // See also:
    // https://android-developers.googleblog.com/2016/12/introducing-the-exifinterface-support-library.html
    if (ContentResolver.SCHEME_CONTENT != imageUri.scheme
        && ContentResolver.SCHEME_FILE != imageUri.scheme
    ) {
        return 0
    }
    var exif: ExifInterface
    try {
        resolver.openInputStream(imageUri).use { inputStream ->
            if (inputStream == null) {
                return 0
            }
            exif = ExifInterface(inputStream)
        }
    } catch (e: IOException) {
        Log.e("TAG", "failed to open file to read rotation meta data: $imageUri", e)
        return 0
    }
    return exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL)
}
private fun rotateBitmap(
    bitmap: Bitmap, rotationDegrees: Int, flipX: Boolean, flipY: Boolean
): Bitmap? {
    val matrix = Matrix()

    // Rotate the image back to straight.
    matrix.postRotate(rotationDegrees.toFloat())

    // Mirror the image along the X or Y axis.
    matrix.postScale(if (flipX) -1.0f else 1.0f, if (flipY) -1.0f else 1.0f)
    val rotatedBitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true)

    // Recycle the old bitmap if it has changed.
    if (rotatedBitmap != bitmap) {
        bitmap.recycle()
    }
    return rotatedBitmap
}
fun getBitmapFromUris(context: Context, selectedPhotoUrii: Uri): Bitmap? {
    //val selectedPhotoUri= getUriForFile(context, context.applicationContext.packageName + ".provider", File(selectedPhotoUrii.path))


    return MediaStore.Images.Media.getBitmap(
        context.contentResolver,
        selectedPhotoUrii
    )

}

fun getBitmapFromUri(context: Context, uri: Uri): Bitmap? {

    try
    {
        val sharedFileUri=FileProvider.getUriForFile(context, "${context.packageName}.provider", File(uri.path))
        val parcelFileDescriptor: ParcelFileDescriptor? = context.contentResolver.openFileDescriptor(sharedFileUri, "r")
        val fileDescriptor: FileDescriptor = parcelFileDescriptor!!.fileDescriptor
        val image = BitmapFactory.decodeFileDescriptor(fileDescriptor)
        parcelFileDescriptor.close()
        return image
    }catch (e:Exception)
    {
        val parcelFileDescriptor: ParcelFileDescriptor? = context.contentResolver.openFileDescriptor(uri, "r")
        val fileDescriptor: FileDescriptor = parcelFileDescriptor!!.fileDescriptor
        val image = BitmapFactory.decodeFileDescriptor(fileDescriptor)
        parcelFileDescriptor.close()
        return image
    }


}


fun saveCaptureImage(context: Context, bitmapImage: Bitmap, name: String): String? {
    val cw = ContextWrapper(context)
    val directory = cw.getDir("imageDir", Context.MODE_PRIVATE)
    // Create imageDir
    val mypath = File(directory, name)
    var fos: FileOutputStream? = null
    try {
        fos = FileOutputStream(mypath)
        // Use the compress method on the BitMap object to write image to the OutputStream
        bitmapImage.compress(Bitmap.CompressFormat.PNG, 100, fos)
    } catch (e: Exception) {
        e.printStackTrace()
    } finally {
        try {
            fos!!.close()
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }
    Log.i("BBC", "saveCaptureImage: ${mypath.absolutePath}")
    return mypath.absolutePath
}


fun saveImage (context: Activity, bitmap: Bitmap, toString: String)
{
    val path = getOutputDirectory(context).absolutePath
    CoroutineScope(IO).launch{
        val mediaFile = File(path,"image ${System.currentTimeMillis()}.jpeg")
        val fileOutputStream = FileOutputStream(mediaFile)
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, fileOutputStream)
        fileOutputStream.flush()
        fileOutputStream.close()

            saveform(context,mediaFile,toString)


        withContext(Main){



            var  mImageUri = FileProvider.getUriForFile(context, "${context.packageName}.provider", mediaFile)
            val email = toString
            Log.e("email", "saveImage: $toString")
//            val subject = "${resources.getString(R.string.app_name)} Feedback"
            val subject = "Photo événement pwc 2022"
            val body = "Bonjour, \n" +
                    "Trouvez ci-joint votre photo de l'événement PWC 2022 .\n" +
                    "Merci."

            val selectorIntent = Intent(Intent.ACTION_SENDTO)
            val urlString =
                "mailto:" + Uri.encode(email) + "?subject=" + Uri.encode(subject) + "&body=" + Uri.encode(
                    body
                )
            selectorIntent.data = Uri.parse(urlString)

            val emailIntent = Intent(Intent.ACTION_SEND)
            emailIntent.putExtra(Intent.EXTRA_EMAIL, arrayOf(email))
            emailIntent.putExtra(Intent.EXTRA_SUBJECT, subject)
            emailIntent.putExtra(Intent.EXTRA_TEXT, body)
            emailIntent.putExtra(Intent.EXTRA_STREAM, mImageUri)
//            emailIntent.type = "image/png"
            emailIntent.selector = selectorIntent

            context.startActivityForResult(Intent.createChooser(emailIntent, "Send email"),555)







            scanFile(context,mediaFile.absolutePath)
        }
    }
}

fun getOutputDirectory(activity: Activity): File {
    val mediaDir = activity.externalMediaDirs.firstOrNull()?.let {
        File(it, activity.resources.getString(R.string.app_name)).apply { mkdirs() } }
    val file = File(mediaDir,"BG Remover")
    file.mkdirs()
    return file
}
fun scanFile(ctx: Context, str: String) {
    MediaScannerConnection.scanFile(
        ctx, arrayOf(str), null
    ) { path, uri -> Log.i("TAG", "Finished scanning $path") }
//    Toast.makeText(ctx,"Saved Image to gallery", Toast.LENGTH_SHORT).show()
}



fun saveform(context: Activity, mediaFile: File, toString: String) {






//
//   var sad =  SendMail()
//
//
//    try {
//        val sender = GMailSender("hamza.mughal5593@yahoo.com", "1qaz2wsx!@#EDC")
//        sender.sendMail(
//            "This is Subject",
//            "This is Body",
//            "hamza.mughal5593@yahoo.com",
//            "hamza.mughal5593@gmail.com"
//        )
//    } catch (e: java.lang.Exception) {
//        Log.e("SendMail", e.message, e)
//    }


//    val dialogBuilder: AlertDialog.Builder = AlertDialog.Builder(context)
//// ...Irrelevant code for customizing the buttons and title
//// ...Irrelevant code for customizing the buttons and title
//    val inflater = context.layoutInflater
//    val dialogView: View = inflater.inflate(R.layout.save_form, null)
//    dialogBuilder.setView(dialogView)
//
//    val retry = dialogView.findViewById<View>(R.id.retry) as TextView
//    val send = dialogView.findViewById<View>(R.id.send) as TextView
//    val email = dialogView.findViewById<View>(R.id.email) as EditText
//
//    val alertDialog: AlertDialog = dialogBuilder.create()
//    alertDialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
//
//    alertDialog.setCancelable(false)
//    alertDialog.show()
//    retry.setOnClickListener {
//
//        alertDialog.dismiss()
//        context.finish()
//    }
//
//    send.setOnClickListener {
//
//        if (!email.text.toString().isEmpty()){
//            var  mImageUri = FileProvider.getUriForFile(context, "${context.packageName}.provider", mediaFile)
//
//            val i = Intent(Intent.ACTION_SEND)
//            i.putExtra(Intent.EXTRA_EMAIL, arrayOf(email.text.toString()))
//            i.putExtra(Intent.EXTRA_SUBJECT, "Picture")
//            //Log.d("URI@!@#!#!@##!", Uri.fromFile(pic).toString() + "   " + pic.exists());
//            //Log.d("URI@!@#!#!@##!", Uri.fromFile(pic).toString() + "   " + pic.exists());
//            i.putExtra(Intent.EXTRA_STREAM, mImageUri)
//            i.type = "image/png"
//            context.startActivity(Intent.createChooser(i, "Share you on the jobing"))
//
//            alertDialog.dismiss()
//            context.finish()
//        }else{
//            Toast.makeText(context, "please input email", Toast.LENGTH_SHORT).show()
//        }
//
//    }
}


fun backPressDialog(context: Activity) {

    val dialogBuilder: AlertDialog.Builder = AlertDialog.Builder(context)
// ...Irrelevant code for customizing the buttons and title
// ...Irrelevant code for customizing the buttons and title
    val inflater = context.layoutInflater
    val dialogView: View = inflater.inflate(R.layout.dialog_sure, null)
    dialogBuilder.setView(dialogView)

    val no = dialogView.findViewById<View>(R.id.no) as TextView
    val yes = dialogView.findViewById<View>(R.id.yes) as TextView

    val alertDialog: AlertDialog = dialogBuilder.create()
    alertDialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
    alertDialog.show()
    alertDialog.setCancelable(false)

    no.setOnClickListener {

        alertDialog.dismiss()
    }

    yes.setOnClickListener {

        alertDialog.dismiss()
        context.finish()
    }
}


