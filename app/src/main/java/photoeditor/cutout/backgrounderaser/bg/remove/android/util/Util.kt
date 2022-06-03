package photoeditor.cutout.backgrounderaser.bg.remove.android.util

import android.app.Activity
import android.app.ActivityManager
import android.app.Dialog
import android.content.*
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import androidx.core.content.FileProvider.getUriForFile
import photoeditor.cutout.backgrounderaser.bg.remove.android.ui.Editor
import kotlin.math.roundToInt
import android.graphics.BitmapFactory
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.media.MediaScannerConnection
import android.os.Environment

import android.os.ParcelFileDescriptor
import android.util.Log
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.NonNull
import androidx.appcompat.app.AlertDialog
import com.airbnb.lottie.BuildConfig
import com.willy.ratingbar.ScaleRatingBar
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import photoeditor.cutout.backgrounderaser.bg.remove.android.MainActivity
import photoeditor.cutout.backgrounderaser.bg.remove.android.R
import java.io.*
import java.io.File.separator
import java.util.*


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
        1000 + (250 * available)
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

fun getBitmapFromUris(context: Context, selectedPhotoUrii: Uri): Bitmap? {
    //val selectedPhotoUri= getUriForFile(context, context.applicationContext.packageName + ".provider", File(selectedPhotoUrii.path))


    return MediaStore.Images.Media.getBitmap(
        context.contentResolver,
        selectedPhotoUrii
    )

}

fun getBitmapFromUri(context: Context, uri: Uri): Bitmap? {
    val parcelFileDescriptor: ParcelFileDescriptor? =
        context.contentResolver.openFileDescriptor(uri, "r")
    val fileDescriptor: FileDescriptor = parcelFileDescriptor!!.fileDescriptor
    val image = BitmapFactory.decodeFileDescriptor(fileDescriptor)
    parcelFileDescriptor.close()
    return image
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
    return directory.canonicalPath
}


fun saveImage (context: Activity,bitmap:Bitmap)
{
    val path = getOutputDirectory(context).absolutePath
    CoroutineScope(IO).launch{
        val mediaFile = File(path,"image ${System.currentTimeMillis()}.png")
        val fileOutputStream = FileOutputStream(mediaFile)
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, fileOutputStream)
        fileOutputStream.flush()
        fileOutputStream.close()
        withContext(Main){
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

fun shareApp(context: Context) {
    try {
        val shareIntent = Intent(Intent.ACTION_SEND)
        shareIntent.type = "text/plain"
        shareIntent.putExtra(Intent.EXTRA_SUBJECT, "Document Scanner")
        var shareMessage = "\nLet me recommend you this application\n\n"
        shareMessage =
            """
                ${shareMessage}https://play.google.com/store/apps/details?id=${BuildConfig.APPLICATION_ID}
                
                
                """.trimIndent()
        shareIntent.putExtra(Intent.EXTRA_TEXT, shareMessage)
        context.startActivity(Intent.createChooser(shareIntent, "choose one"))
    } catch (e: Exception) {
        //e.toString();
    }
}

fun openPrivacyPolicy(context: Context) {
    val url = "https://appverseltd.blogspot.com/2022/02/document-scanner-privacy-policy.html"
    val i = Intent(Intent.ACTION_VIEW)
    i.data = Uri.parse(url)
    context.startActivity(i)
}

fun rate_dialog(context: Context) {

     val sharedPrefFile = "kotlinsharedpreference"
     lateinit var sharedPreferences: SharedPreferences
    val dialog = Dialog(context)
    dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
    dialog.setContentView(R.layout.rate_us_dialog)
    val simpleRatingBar: ScaleRatingBar = dialog.findViewById(R.id.simpleRatingBar)
    val rating_btn = dialog.findViewById(R.id.rating_btn) as TextView
    simpleRatingBar.setOnRatingChangeListener { ratingBar, rating, fromUser -> //
        if (rating.toDouble() == 5.0) {
            rating_btn.text = "RATE US"
        } else {
            rating_btn.text = "FEEDBACK"
        }
    }
    rating_btn.setOnClickListener {
        dialog.dismiss()
        if (rating_btn.text.toString() == "RATE US") {
            val uri =
                Uri.parse("market://details?id=" + context.applicationContext.packageName)
            val rateAppIntent = Intent(Intent.ACTION_VIEW, uri)
            if (context.packageManager.queryIntentActivities(rateAppIntent, 0).size > 0) {
                context.startActivity(rateAppIntent)
            }
        } else {
            val intent = Intent(Intent.ACTION_SENDTO)
            intent.putExtra(Intent.EXTRA_SUBJECT, "BG Remover Feedback")
            intent.setData(Uri.parse("mailto:")); // or just "mailto:" for blank
            intent.putExtra(Intent.EXTRA_EMAIL, arrayOf<String>("default@recipient.com"))

            if (intent.resolveActivity(context.packageManager) != null) {
                context.startActivity(intent)
            }
        }

        val sharedPreferences: SharedPreferences = context.getSharedPreferences(sharedPrefFile,Context.MODE_PRIVATE)
        val editor: SharedPreferences.Editor =  sharedPreferences.edit()
        editor.putBoolean("show_rate_us",false)
        editor.apply()
        editor.commit()

    }
    val no = dialog.findViewById(R.id.no) as TextView
    no.setOnClickListener {
        dialog.dismiss()
    }

    dialog.show()
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


