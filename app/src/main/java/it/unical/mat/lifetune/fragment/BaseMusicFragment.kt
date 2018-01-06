package it.unical.mat.lifetune.fragment

import android.support.annotation.UiThread
import android.support.v4.app.Fragment
import android.util.Log
import com.google.firebase.crash.FirebaseCrash
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import it.unical.mat.lifetune.R
import it.unical.mat.lifetune.controller.BaseMusicController
import it.unical.mat.lifetune.entity.*
import it.unical.mat.lifetune.service.ApiServiceFactory
import it.unical.mat.lifetune.util.AppDialog
import it.unical.mat.lifetune.util.AppUtils

/**
 * Created by beantoan on 12/14/17.
 */
abstract class BaseMusicFragment : Fragment(), BaseMusicController.AdapterCallbacks {

    private var mCompositeDisposable: CompositeDisposable? = null

    protected var playMusicFragment: PlayMusicFragment? = null

    protected val recommendationParameter: RecommendationParameter = RecommendationParameter()


    override fun onDestroy() {
        Log.d(TAG, "onDestroy")

        try {
            mCompositeDisposable?.clear()
        } catch (err: Exception) {
            Log.e(TAG, "errorOnDestroy", err)

            FirebaseCrash.logcat(Log.ERROR, TAG, "errorOnDestroy" + err)
            FirebaseCrash.report(err)
        }

        super.onDestroy()
    }

    override final fun onPlaylistClicked(playlist: Playlist) {
        Log.d(TAG, "onPlaylistClicked#${playlist.id}-${playlist.title}")

        callSongsService(playlist)
    }

    override final fun onSongClicked(song: Song) {
        Log.d(TAG, "onPlaylistClicked#${song.id}-${song.title}")

    }

    open protected fun onRecommendationServiceSuccess(categories: List<Category>) {
        Log.d(TAG, "onRecommendationServiceSuccess: categories.size=" + categories.size)

        onCommonServiceSuccess()
    }

    open protected fun onRecommendationServiceFailure(error: Throwable) {
        FirebaseCrash.logcat(Log.ERROR, TAG, "onRecommendationServiceFailure:" + error)
        FirebaseCrash.report(error)

        onCommonServiceFailure()
    }

    open protected fun onFavouriteServiceSuccess(playlists: List<Playlist>) {
        Log.d(TAG, "onFavouriteServiceSuccess: playlists.size=" + playlists.size)

        onCommonServiceSuccess()
    }

    open protected fun onFavouriteServiceFailure(error: Throwable) {
        FirebaseCrash.logcat(Log.ERROR, TAG, "onFavouriteServiceFailure:" + error)
        FirebaseCrash.report(error)

        onCommonServiceFailure()
    }

    private fun onCommonServiceSuccess() {
        hideLoading()
    }

    private fun onCommonServiceFailure() {
        hideLoading()
        AppDialog.error(R.string.api_service_error_title, R.string.api_service_error_message, activity!!)
    }

    private fun getCompositeDisposable(): CompositeDisposable {
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
    private fun playSongs(trackList: TrackList?) {
        Log.d(TAG, "playSongs")

        this.playMusicFragment?.playSongs(trackList)

        hideLoading()
    }

    private fun callSongsService(playlist: Playlist) {
        Log.d(TAG, "callSongsService#${playlist.id}-${playlist.title}")

        if (AppUtils.isInternetConnected(context!!)) {
            showLoading()

            getCompositeDisposable().add(
                    ApiServiceFactory.createPlaylistXmlService().songs(playlist.key)
                            .subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe(
                                    { playlistXml -> onSongsServiceSuccess(playlist, playlistXml) },
                                    { error -> onSongsServiceFailure(error) }
                            )
            )
        } else {
            AppDialog.error(R.string.no_internet_error_title, R.string.no_internet_error_message, activity!!)
        }
    }

    fun callRecommendationService() {
        Log.d(TAG, "callRecommendationService")

        if (AppUtils.isInternetConnected(context!!)) {

            if (this.playMusicFragment!!.isCurrentRecommendationMusicFragment()) {
                showLoading()
            }

            getCompositeDisposable().add(
                    ApiServiceFactory.createCategoryService().recommendation()
                            .subscribeOn(Schedulers.io()) // "work" on io thread
                            .observeOn(AndroidSchedulers.mainThread()) // "listen" on UIThread
                            .subscribe(
                                    { categories -> onRecommendationServiceSuccess(categories) },
                                    { error -> onRecommendationServiceFailure(error) }
                            )
            )
        } else {
            AppDialog.error(R.string.no_internet_error_title, R.string.no_internet_error_message, activity!!)
        }
    }

    fun callFavouritePlaylistsService() {
        Log.d(TAG, "callFavouritePlaylistsService")

        if (AppUtils.isInternetConnected(context!!)) {

            if (this.playMusicFragment!!.isCurrentFavouriteMusicFragment()) {
                showLoading()
            }

            getCompositeDisposable().add(
                    ApiServiceFactory.createPlaylistService().favourite()
                            .subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe(
                                    { playlists -> onFavouriteServiceSuccess(playlists) },
                                    { error -> onFavouriteServiceFailure(error) }
                            )
            )
        } else {
            AppDialog.error(R.string.no_internet_error_title, R.string.no_internet_error_message, activity!!)
        }
    }

    @UiThread
    private fun onSongsServiceSuccess(playlist: Playlist, trackList: TrackList) {
        Log.d(TAG, "onSongsServiceSuccess")

        trackList.tracks.forEach { it.playlist = playlist }

        playSongs(trackList)
    }

    @UiThread
    private fun onSongsServiceFailure(error: Throwable) {
        FirebaseCrash.logcat(Log.ERROR, TAG, "onSongsServiceFailure:" + error)
        FirebaseCrash.report(error)
        
        playSongs(null)

        AppDialog.error(R.string.api_service_error_title, R.string.api_service_error_message, activity!!)
    }


    companion object {
        private val TAG = BaseMusicFragment::class.java.canonicalName
    }
}