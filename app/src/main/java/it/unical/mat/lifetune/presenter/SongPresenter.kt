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

    var songsCallbacks: SongsCallbacks? = null
    var searchCallbacks: SearchCallbacks? = null
    var playlistCallbacks: PlaylistCallbacks? = null

    constructor(callbacks: SongsCallbacks) {
        songsCallbacks = callbacks
    }

    constructor(callbacks: SearchCallbacks) {
        searchCallbacks = callbacks
    }

    constructor(callbacks: PlaylistCallbacks) {
        playlistCallbacks = callbacks
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
                        { trackList -> songsCallbacks?.onSongsApiSuccess(playlist, trackList) },
                        { error -> songsCallbacks?.onSongsApiError(error) }
                )
    }

    fun callSearchApi(term: String) {
        Log.d(TAG, "callSearchApi: term=$term")

        ApiServiceFactory.createSongApi().search(term)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        { songs -> searchCallbacks?.onSearchSuccess(songs) },
                        { error -> searchCallbacks?.onSearchError(error) }
                )
    }

    fun callPlaylistApi(song: Song, userId: String) {
        Log.d(TAG, "callPlaylistApi: ${song.shortLog()}")

        ApiServiceFactory.createSongApi().playlist(song.id, userId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        { playlist -> playlistCallbacks?.onPlaylistSuccess(song, playlist) },
                        { error -> playlistCallbacks?.onPlaylistError(error, song) }
                )
    }

    interface SongsCallbacks {
        fun onSongsApiSuccess(playlist: Playlist, trackList: TrackList)
        fun onSongsApiError(error: Throwable)
    }

    interface SearchCallbacks {
        fun onSearchSuccess(songs: List<Song>)
        fun onSearchError(error: Throwable)
    }

    interface PlaylistCallbacks {
        fun onPlaylistSuccess(song: Song, playlist: Playlist)
        fun onPlaylistError(error: Throwable, song: Song)
    }
}