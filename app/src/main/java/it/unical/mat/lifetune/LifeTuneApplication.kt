package it.unical.mat.lifetune

import android.app.Application
import com.google.android.exoplayer2.DefaultLoadControl
import com.google.android.exoplayer2.DefaultRenderersFactory
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector
import it.unical.mat.lifetune.util.CustomExoPlayer

/**
 * Created by beantoan on 11/24/17.
 */
class LifeTuneApplication : Application() {

    override fun onCreate() {
        super.onCreate()

        musicPlayer = CustomExoPlayer(
                DefaultRenderersFactory(this),
                DefaultTrackSelector(),
                DefaultLoadControl()
        )
    }

    override fun onTerminate() {
        musicPlayer.release()

        super.onTerminate()
    }

    companion object {
        lateinit var musicPlayer: CustomExoPlayer
    }
}