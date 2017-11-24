package it.unical.mat.lifetune

import android.app.Application
import com.facebook.stetho.Stetho

/**
 * Created by beantoan on 11/24/17.
 */
class LifeTuneApplication : Application() {
    override fun onCreate() {
        super.onCreate()

        Stetho.initializeWithDefaults(this)
    }
}