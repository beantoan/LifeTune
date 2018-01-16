package it.unical.mat.lifetune.fragment

import android.support.annotation.UiThread
import android.support.v4.app.Fragment
import android.util.Log
import com.crashlytics.android.Crashlytics
import com.google.firebase.auth.FirebaseAuth
import io.reactivex.disposables.CompositeDisposable
import it.unical.mat.lifetune.R
import it.unical.mat.lifetune.controller.BaseMusicController
import it.unical.mat.lifetune.entity.*
import it.unical.mat.lifetune.presenter.CategoryPresenter
import it.unical.mat.lifetune.presenter.PlaylistPresenter
import it.unical.mat.lifetune.presenter.SongPresenter
import it.unical.mat.lifetune.util.AppDialog
import it.unical.mat.lifetune.util.AppUtils

/**
 * Created by beantoan on 12/14/17.
 */
abstract class BaseMusicFragment :
        Fragment(), BaseMusicController.AdapterCallbacks {

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

    override fun onLikePlaylistClicked(playlist: Playlist) {
        Log.d(TAG, "onLikePlaylistClicked#${playlist.id}-${playlist.title}")

        callLikePlaylistApi(playlist)
    }

    override fun onUnlikePlaylistClicked(playlist: Playlist) {
        Log.d(TAG, "onUnlikePlaylistClicked#${playlist.id}-${playlist.title}")

        callUnlikePlaylistApi(playlist)
    }

    open protected fun onSongsApiSuccess(playlist: Playlist, trackList: TrackList) {
        Log.d(TAG, "onSongsApiSuccess")

        playlist.tracks = trackList.tracks

        playSongs(playlist)
    }

    open protected fun onSongsApiError(error: Throwable) {
        Crashlytics.log(Log.ERROR, TAG, "onSongsApiError:" + error)
        Crashlytics.logException(error)

        playSongs(null)

        AppDialog.error(R.string.api_service_error_title, R.string.api_service_error_message, activity!!)
    }

    open protected fun onRecommendationApiSuccess(categories: List<Category>) {
        Log.d(TAG, "onRecommendationApiSuccess: categories.size=" + categories.size)

        onCommonApiSuccess()
    }

    open protected fun onRecommendationApiError(error: Throwable) {
        Crashlytics.log(Log.ERROR, TAG, "onRecommendationApiError:" + error)
        Crashlytics.logException(error)

        onCommonApiError()
    }

    open protected fun onFavouriteApiSuccess(playlists: List<Playlist>) {
        Log.d(TAG, "onFavouriteApiSuccess: playlists.size=" + playlists.size)

        onCommonApiSuccess()
    }

    open protected fun onFavouriteApiError(error: Throwable) {
        Crashlytics.log(Log.ERROR, TAG, "onFavouriteApiError:" + error)
        Crashlytics.logException(error)

        onCommonApiError()
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

    private fun onCommonApiError() {
        Log.d(TAG, "onCommonApiError")

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

    fun callSongsApi(playlist: Playlist) {
        Log.d(TAG, "callSongsApi#${playlist.id}-${playlist.title}")

        if (AppUtils.isInternetConnected(context!!)) {
            beforeCallSongsApi()

            SongPresenter(ImplSongCallbacks(this)).callSongsApi(playlist)
        } else {
            AppDialog.error(R.string.no_internet_error_title, R.string.no_internet_error_message, activity!!)
        }
    }

    fun callRecommendationApi() {
        Log.d(TAG, "callRecommendationApi")

        if (AppUtils.isInternetConnected(context!!)) {

            beforeCallRecommendationApi()

            CategoryPresenter(ImplRecommendationCallbacks(this)).callRecommendationApi()
        } else {
            AppDialog.error(R.string.no_internet_error_title, R.string.no_internet_error_message, activity!!)
        }
    }

    fun callFavouriteApi() {
        Log.d(TAG, "callFavouriteApi")

        if (AppUtils.isInternetConnected(activity!!)) {

            beforeCallFavouriteApi()

            val userId = FirebaseAuth.getInstance().currentUser!!.uid

            PlaylistPresenter(ImplFavouriteCallbacks(this)).callFavouriteApi(userId)
        } else {
            AppDialog.error(R.string.no_internet_error_title, R.string.no_internet_error_message, activity!!)
        }
    }

    fun callLikePlaylistApi(playlist: Playlist) {
        val userId = FirebaseAuth.getInstance().currentUser!!.uid

        PlaylistPresenter(ImplLikePlaylistCallbacks(this)).callLikePlaylistApi(playlist, userId)
    }

    fun callUnlikePlaylistApi(playlist: Playlist) {
        val userId = FirebaseAuth.getInstance().currentUser!!.uid

        PlaylistPresenter(ImplUnlikePlaylistCallbacks(this)).callUnlikePlaylistApi(playlist, userId)
    }

    companion object {
        private val TAG = BaseMusicFragment::class.java.simpleName
    }

    private class ImplLikePlaylistCallbacks(val baseMusicFragment: BaseMusicFragment) : PlaylistPresenter.LikePlaylistCallbacks {
        companion object {
            val TAG = ImplLikePlaylistCallbacks::class.java.simpleName
        }

        override fun onLikePlaylistSuccess(commonApiResponse: CommonApiResponse, playlist: Playlist) {
            Log.d(TAG, "onLikePlaylistSuccess: commonApiResponse=$commonApiResponse, playlist.id=${playlist.id}")

        }

        override fun onLikePlaylistError(error: Throwable) {

        }
    }

    private class ImplUnlikePlaylistCallbacks(val baseMusicFragment: BaseMusicFragment) : PlaylistPresenter.UnlikePlaylistCallbacks {
        companion object {
            val TAG = ImplUnlikePlaylistCallbacks::class.java.simpleName
        }

        override fun onUnlikePlaylistSuccess(commonApiResponse: CommonApiResponse, playlist: Playlist) {
            Log.d(TAG, "onUnlikePlaylistSuccess: commonApiResponse=$commonApiResponse, playlist.id=${playlist.id}")

        }

        override fun onUnlikePlaylistError(error: Throwable) {

        }
    }

    private class ImplFavouriteCallbacks(val baseMusicFragment: BaseMusicFragment) : PlaylistPresenter.FavouriteCallbacks {
        companion object {
            val TAG = ImplFavouriteCallbacks::class.java.simpleName
        }

        override fun onFavouriteApiSuccess(playlists: List<Playlist>) {
            baseMusicFragment.onFavouriteApiSuccess(playlists)
        }

        override fun onFavouriteApiError(error: Throwable) {
            baseMusicFragment.onFavouriteApiError(error)
        }
    }

    private class ImplRecommendationCallbacks(val baseMusicFragment: BaseMusicFragment) : CategoryPresenter.RecommendationCallbacks {
        override fun onRecommendationApiSuccess(categories: List<Category>) {
            baseMusicFragment.onRecommendationApiSuccess(categories)
        }

        override fun onRecommendationApiError(error: Throwable) {
            baseMusicFragment.onRecommendationApiError(error)
        }
    }

    private class ImplSongCallbacks(val baseMusicFragment: BaseMusicFragment) : SongPresenter.Callbacks {
        override fun onSongsApiSuccess(playlist: Playlist, trackList: TrackList) {
            baseMusicFragment.onSongsApiSuccess(playlist, trackList)
        }

        override fun onSongsApiError(error: Throwable) {
            baseMusicFragment.onSongsApiError(error)
        }

    }
}