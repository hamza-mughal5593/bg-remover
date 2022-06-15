package photoeditor.cutout.backgrounderaser.bg.remove.android.ui

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import photoeditor.cutout.backgrounderaser.bg.remove.android.MainActivity
import photoeditor.cutout.backgrounderaser.bg.remove.android.databinding.ActivityResultBinding


class ResultActivity : AppCompatActivity() {
    lateinit var binding: ActivityResultBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
         binding = ActivityResultBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.again.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
            this@ResultActivity.finish()
        }

    }
}