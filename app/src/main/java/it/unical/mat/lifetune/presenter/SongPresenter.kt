package it.unical.mat.lifetune.presenter

import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import it.unical.mat.lifetune.api.ApiServiceFactory
import it.unical.mat.lifetune.entity.Playlist
import it.unical.mat.lifetune.entity.TrackList

/**
 * Created by beantoan on 1/16/18.
 */
data class SongPresenter(val callbacks: Callbacks) {

    fun callSongsApi(playlist: Playlist) {
        ApiServiceFactory.createPlaylistXmlApi().songs(playlist.key)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        { trackList -> callbacks.onSongsApiSuccess(playlist, trackList) },
                        { error -> callbacks.onSongsApiError(error) }
                )
    }

    interface Callbacks {
        fun onSongsApiSuccess(playlist: Playlist, trackList: TrackList)
        fun onSongsApiError(error: Throwable)
    }
}