package org.greenthread.whatsinmycloset

import android.app.Application
import org.greenthread.whatsinmycloset.di.initKoin
import org.koin.android.ext.koin.androidContext

class ClosetApplication: Application() {

    override fun onCreate() {
        super.onCreate()
        initKoin{
            androidContext(this@ClosetApplication)
        }
    }
}