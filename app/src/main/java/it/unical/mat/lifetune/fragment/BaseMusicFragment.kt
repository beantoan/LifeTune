package it.unical.mat.lifetune.fragment

import android.support.annotation.UiThread
import android.support.v4.app.Fragment
import android.util.Log
import io.reactivex.disposables.CompositeDisposable
import it.unical.mat.lifetune.R
import it.unical.mat.lifetune.entity.PlaylistXml
import it.unical.mat.lifetune.entity.Song
import it.unical.mat.lifetune.util.AppDialog

/**
 * Created by beantoan on 12/14/17.
 */
abstract class BaseMusicFragment : Fragment() {

    private var mCompositeDisposable: CompositeDisposable? = null

    protected var playMusicFragment: PlayMusicFragment? = null

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
    private fun displayLoading(isShown: Boolean) {
        when {
            isShown -> AppDialog.showProgress(R.string.progress_dialog_waiting_message, context!!)
            else -> AppDialog.hideProgress(context!!)
        }
    }

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

    companion object {
        private val TAG = BaseMusicFragment::class.java.canonicalName
    }
}