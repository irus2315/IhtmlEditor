package kr.twothumb.sample

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.databinding.DataBindingUtil
import kr.twothumb.lib.logger.DevLog
import kr.twothumb.sample.databinding.ActivitySourceBinding

class SourceActivity : AppCompatActivity() {
    private val bind by lazy {
        DataBindingUtil.setContentView<ActivitySourceBinding>(
            this@SourceActivity,
            R.layout.activity_source
        )
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        bind

        DevLog.e(intent.getStringExtra("source"))
        bind.source.text = intent.getStringExtra("source")

    }
}
