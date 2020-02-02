package kr.twothumb.sample

import android.app.Application
import kr.twothumb.ieditor.IEditor

class SampleApplication: Application() {
    init {
        instance = this
    }

    override fun onCreate() {
        super.onCreate()

        IEditor.init(this@SampleApplication)

    }

    companion object {
        lateinit var instance: SampleApplication
    }
}