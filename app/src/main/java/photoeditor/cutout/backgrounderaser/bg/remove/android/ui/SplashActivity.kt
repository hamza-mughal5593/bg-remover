package photoeditor.cutout.backgrounderaser.bg.remove.android.ui

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import photoeditor.cutout.backgrounderaser.bg.remove.android.MainActivity
import photoeditor.cutout.backgrounderaser.bg.remove.android.R
import java.util.*
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit

class SplashActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        Executors.newSingleThreadScheduledExecutor().schedule({
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            this@SplashActivity.finish()
        }, 3, TimeUnit.SECONDS)




    }
}