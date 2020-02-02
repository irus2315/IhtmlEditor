package kr.twothumb.ieditor

import android.content.Context
import kr.twothumb.ieditor.di.appModule
import kr.twothumb.lib.logger.DevLog
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

object IEditor {
    lateinit var applicationContext:Context
    fun init(context: Context){
        applicationContext = context
        DevLog.getInstance().init("Editor")
        startKoin {
            androidContext(applicationContext)
            modules(listOf(appModule))
        }
    }

}