package it.unical.mat.lifetune.fragment

import android.os.Bundle
import android.support.annotation.UiThread
import android.support.v4.app.Fragment
import android.util.Log
import android.view.View
import io.reactivex.disposables.CompositeDisposable
import it.unical.mat.lifetune.LifeTuneApplication
import it.unical.mat.lifetune.entity.PlaylistXml
import it.unical.mat.lifetune.entity.Song

/**
 * Created by beantoan on 12/14/17.
 */
abstract class BaseMusicFragment : Fragment() {

    private var mCompositeDisposable: CompositeDisposable? = null

    protected var playMusicFragment: PlayMusicFragment? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        Log.d(TAG, "onCreate")
        super.onCreate(savedInstanceState)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        Log.d(TAG, "onViewCreated")

        super.onViewCreated(view, savedInstanceState)
    }

    override fun onStop() {
        Log.d(TAG, "onStop")

        super.onStop()
    }

    override fun onDestroy() {
        Log.d(TAG, "onDestroy")

        try {
            mCompositeDisposable?.clear()
        } catch (err: Exception) {
            Log.e(TAG, "errorOnDestroy", err)
        }

        super.onDestroy()
    }

    protected fun getCompositeDisposable(): CompositeDisposable {
        if (mCompositeDisposable == null) {
            mCompositeDisposable = CompositeDisposable()
        }

        return mCompositeDisposable!!
    }

    @UiThread
    protected abstract fun displayLoading(isShown: Boolean)

    @UiThread
    protected fun showLoading() {
        displayLoading(true)
    }

    @UiThread
    protected fun hideLoading() {
        displayLoading(false)
    }

    @UiThread
    protected fun playSongs(playlistXml: PlaylistXml) {
        Log.d(TAG, "playSongs")

        this.playMusicFragment?.playSongs(playlistXml)

        hideLoading()
    }

    @UiThread
    protected fun playSongs(songs: List<Song>) {
        Log.d(TAG, "playSongs")

        this.playMusicFragment?.playSongs(songs)

        hideLoading()
    }

    @UiThread
    protected fun determineDisplayMusicPlayer() {
        Log.d(TAG, "determineDisplayMusicPlayer")

        if (LifeTuneApplication.musicPlayer == null || LifeTuneApplication.musicPlayer.tracks.isEmpty()) {
            this.playMusicFragment?.hideMusicPlayer()
        } else {
            this.playMusicFragment?.showMusicPlayer()
        }
    }

    companion object {
        private val TAG = BaseMusicFragment::class.java.canonicalName
    }
}