package it.unical.mat.lifetune.fragment

import android.support.annotation.UiThread
import android.support.v4.app.Fragment
import android.util.Log
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import it.unical.mat.lifetune.R
import it.unical.mat.lifetune.entity.Playlist
import it.unical.mat.lifetune.entity.PlaylistXml
import it.unical.mat.lifetune.service.ApiServiceFactory
import it.unical.mat.lifetune.util.AppDialog
import it.unical.mat.lifetune.util.AppUtils

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
    private fun playSongs(playlistXml: PlaylistXml?) {
        Log.d(TAG, "playSongs")

        this.playMusicFragment?.playSongs(playlistXml)

        hideLoading()
    }

    protected fun callPlaylistSongsService(playlist: Playlist) {
        Log.d(TAG, "callPlaylistSongsService")

        if (AppUtils.isInternetConnected(activity!!.applicationContext)) {
            showLoading()

            getCompositeDisposable().add(
                    ApiServiceFactory.createPlaylistXmlService().songs(playlist.key)
                            .subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe(
                                    { playlistXml -> onPlaylistSongsServiceSuccess(playlist, playlistXml) },
                                    { error -> onPlaylistSongsServiceFailure(error) }
                            )
            )
        } else {
            AppDialog.error(R.string.no_internet_error_title, R.string.no_internet_error_message, activity!!)
        }
    }

    @UiThread
    private fun onPlaylistSongsServiceSuccess(playlist: Playlist, playlistXml: PlaylistXml) {
        Log.d(TAG, "onPlaylistSongsServiceSuccess")
        playSongs(playlistXml)
    }

    @UiThread
    private fun onPlaylistSongsServiceFailure(error: Throwable) {
        Log.e(TAG, "onPlaylistSongsServiceFailure", error)

        playSongs(null)

        AppDialog.error(R.string.api_service_error_title, R.string.api_service_error_message, activity!!)
    }


    companion object {
        private val TAG = BaseMusicFragment::class.java.canonicalName
    }
}