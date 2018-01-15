package it.unical.mat.lifetune.fragment

import android.support.annotation.UiThread
import android.support.v4.app.Fragment
import android.util.Log
import com.crashlytics.android.Crashlytics
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import it.unical.mat.lifetune.R
import it.unical.mat.lifetune.api.ApiServiceFactory
import it.unical.mat.lifetune.controller.BaseMusicController
import it.unical.mat.lifetune.entity.*
import it.unical.mat.lifetune.util.AppDialog
import it.unical.mat.lifetune.util.AppUtils

/**
 * Created by beantoan on 12/14/17.
 */
abstract class BaseMusicFragment : Fragment(), BaseMusicController.AdapterCallbacks {

    private var mCompositeDisposable: CompositeDisposable? = null

    protected var playMusicFragment: PlayMusicFragment? = null

    protected val recommendationParameter: RecommendationParameter = RecommendationParameter()

    abstract protected fun startLoadingData()

    abstract protected fun clearControllerData()

    override fun onResume() {
        super.onResume()

        startLoadingData()
    }

    override fun onDestroy() {
        Log.d(TAG, "onDestroy")

        try {
            mCompositeDisposable?.clear()
        } catch (err: Exception) {
            Log.e(TAG, "errorOnDestroy", err)

            Crashlytics.log(Log.ERROR, TAG, "errorOnDestroy" + err)
            Crashlytics.logException(err)
        }

        super.onDestroy()
    }

    override final fun onPlaylistClicked(playlist: Playlist) {
        Log.d(TAG, "onPlaylistClicked#${playlist.id}-${playlist.title}")

        callSongsApi(playlist)
    }

    override final fun onSongClicked(song: Song) {
        Log.d(TAG, "onPlaylistClicked#${song.id}-${song.title}")

    }

    open protected fun onRecommendationApiSuccess(categories: List<Category>) {
        Log.d(TAG, "onRecommendationApiSuccess: categories.size=" + categories.size)

        onCommonApiSuccess()
    }

    open protected fun onRecommendationApiFailure(error: Throwable) {
        Crashlytics.log(Log.ERROR, TAG, "onRecommendationApiFailure:" + error)
        Crashlytics.logException(error)

        onCommonApiFailure()
    }

    open protected fun onFavouriteApiSuccess(playlists: List<Playlist>) {
        Log.d(TAG, "onFavouriteApiSuccess: playlists.size=" + playlists.size)

        onCommonApiSuccess()
    }

    open protected fun onFavouriteApiFailure(error: Throwable) {
        Crashlytics.log(Log.ERROR, TAG, "onFavouriteApiFailure:" + error)
        Crashlytics.logException(error)

        onCommonApiFailure()
    }

    open protected fun beforeCallFavouriteApi() {
        if (playMusicFragment!!.isCurrentFavouriteMusicFragment()) {
            showLoading()
        }
    }

    open protected fun beforeCallRecommendationApi() {
        if (playMusicFragment!!.isCurrentRecommendationMusicFragment()) {
            showLoading()
        }
    }

    open protected fun beforeCallSongsApi() {
        showLoading()
    }

    private fun onCommonApiSuccess() {
        Log.d(TAG, "onCommonApiSuccess")

        hideLoading()
    }

    private fun onCommonApiFailure() {
        Log.d(TAG, "onCommonApiFailure")

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
        Log.d(TAG, "displayLoading: isShown=$isShown")

        when {
            isShown -> AppDialog.showProgress(R.string.progress_dialog_waiting_message, context!!)
            else -> AppDialog.hideProgress(context!!)
        }
    }

    @UiThread
    protected fun showLoading() {
        Log.d(TAG, "showLoading")

        displayLoading(true)
    }

    @UiThread
    protected fun hideLoading() {
        Log.d(TAG, "hideLoading")

        displayLoading(false)
    }

    @UiThread
    private fun playSongs(playlist: Playlist?) {
        Log.d(TAG, "playSongs")

        this.playMusicFragment?.playSongs(playlist)

        hideLoading()
    }

    public fun callSongsApi(playlist: Playlist) {
        Log.d(TAG, "callSongsApi#${playlist.id}-${playlist.title}")

        if (AppUtils.isInternetConnected(context!!)) {
            beforeCallSongsApi()

            getCompositeDisposable().add(
                    ApiServiceFactory.createPlaylistXmlApi().songs(playlist.key)
                            .subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe(
                                    { playlistXml -> onSongsApiSuccess(playlist, playlistXml) },
                                    { error -> onSongsApiFailure(error) }
                            )
            )
        } else {
            AppDialog.error(R.string.no_internet_error_title, R.string.no_internet_error_message, activity!!)
        }
    }

    fun callRecommendationApi() {
        Log.d(TAG, "callRecommendationApi")

        if (AppUtils.isInternetConnected(context!!)) {

            beforeCallRecommendationApi()

            getCompositeDisposable().add(
                    ApiServiceFactory.createCategoryApi().recommendation()
                            .subscribeOn(Schedulers.io()) // "work" on io thread
                            .observeOn(AndroidSchedulers.mainThread()) // "listen" on UIThread
                            .subscribe(
                                    { categories -> onRecommendationApiSuccess(categories) },
                                    { error -> onRecommendationApiFailure(error) }
                            )
            )
        } else {
            AppDialog.error(R.string.no_internet_error_title, R.string.no_internet_error_message, activity!!)
        }
    }

    fun callFavouriteApi() {
        Log.d(TAG, "callFavouriteApi")

        if (AppUtils.isInternetConnected(activity!!)) {

            beforeCallFavouriteApi()

            getCompositeDisposable().add(
                    ApiServiceFactory.createPlaylistApi().favourite()
                            .subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe(
                                    { playlists -> onFavouriteApiSuccess(playlists) },
                                    { error -> onFavouriteApiFailure(error) }
                            )
            )
        } else {
            AppDialog.error(R.string.no_internet_error_title, R.string.no_internet_error_message, activity!!)
        }
    }

    @UiThread
    private fun onSongsApiSuccess(playlist: Playlist, trackList: TrackList) {
        Log.d(TAG, "onSongsApiSuccess")

        playlist.tracks = trackList.tracks

        playSongs(playlist)
    }

    @UiThread
    private fun onSongsApiFailure(error: Throwable) {
        Crashlytics.log(Log.ERROR, TAG, "onSongsApiFailure:" + error)
        Crashlytics.logException(error)
        
        playSongs(null)

        AppDialog.error(R.string.api_service_error_title, R.string.api_service_error_message, activity!!)
    }


    companion object {
        private val TAG = BaseMusicFragment::class.java.simpleName
    }
}