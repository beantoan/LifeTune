package it.unical.mat.lifetune.presenter

import android.util.Log
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import it.unical.mat.lifetune.api.ApiServiceFactory
import it.unical.mat.lifetune.entity.Playlist
import it.unical.mat.lifetune.entity.Song
import it.unical.mat.lifetune.entity.TrackList

/**
 * Created by beantoan on 1/16/18.
 */
class SongPresenter {

    var songsCallback: SongsCallback? = null
    var searchCallback: SearchCallback? = null

    constructor(callbacks: SongsCallback) {
        songsCallback = callbacks
    }

    constructor(callbacks: SearchCallback) {
        searchCallback = callbacks
    }

    companion object {
        val TAG = SongPresenter::class.java.simpleName
    }

    fun callSongsApi(playlist: Playlist) {
        Log.d(TAG, "callSongsApi: ${playlist.shortLog()}")

        ApiServiceFactory.createPlaylistXmlApi().songs(playlist.key)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        { trackList -> songsCallback?.onSongsApiSuccess(playlist, trackList) },
                        { error -> songsCallback?.onSongsApiError(error) }
                )
    }

    fun callSearchApi(term: String) {
        Log.d(TAG, "callSearchApi: term=$term")

        ApiServiceFactory.createSongApi().search(term)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        { songs -> searchCallback?.onSearchSuccess(songs) },
                        { error -> searchCallback?.onSearchError(error) }
                )
    }

    interface SongsCallback {
        fun onSongsApiSuccess(playlist: Playlist, trackList: TrackList)
        fun onSongsApiError(error: Throwable)
    }

    interface SearchCallback {
        fun onSearchSuccess(songs: List<Song>)
        fun onSearchError(error: Throwable)
    }
}