package it.unical.mat.lifetune.presenter

import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import it.unical.mat.lifetune.api.ApiServiceFactory
import it.unical.mat.lifetune.entity.CommonApiResponse
import it.unical.mat.lifetune.entity.Playlist

/**
 * Created by beantoan on 1/16/18.
 */

class PlaylistPresenter {

    private var likePlaylistCallbacks: LikePlaylistCallbacks? = null
    private var unlikePlaylistCallbacks: UnlikePlaylistCallbacks? = null
    private var favouriteCallbacks: FavouriteCallbacks? = null

    constructor(callbacks: LikePlaylistCallbacks) {
        likePlaylistCallbacks = callbacks
    }

    constructor(callbacks: UnlikePlaylistCallbacks) {
        unlikePlaylistCallbacks = callbacks
    }

    constructor(callbacks: FavouriteCallbacks) {
        favouriteCallbacks = callbacks
    }

    fun callLikePlaylistApi(playlist: Playlist, userId: String) {
        ApiServiceFactory.createPlaylistApi().like(playlist.id, userId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        { commonApiResponse -> likePlaylistCallbacks?.onLikePlaylistSuccess(commonApiResponse, playlist) },
                        { error -> likePlaylistCallbacks?.onLikePlaylistError(error, playlist) }
                )
    }

    fun callUnlikePlaylistApi(playlist: Playlist, userId: String) {
        ApiServiceFactory.createPlaylistApi().unlike(playlist.id, userId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        { commonApiResponse -> unlikePlaylistCallbacks?.onUnlikePlaylistSuccess(commonApiResponse, playlist) },
                        { error -> unlikePlaylistCallbacks?.onUnlikePlaylistError(error, playlist) }
                )
    }

    fun callFavouriteApi(userId: String) {
        ApiServiceFactory.createPlaylistApi().favourite(userId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        { playlists -> favouriteCallbacks?.onFavouriteApiSuccess(playlists) },
                        { error -> favouriteCallbacks?.onFavouriteApiError(error) }
                )
    }

    interface LikePlaylistCallbacks {
        fun onLikePlaylistSuccess(commonApiResponse: CommonApiResponse, playlist: Playlist)
        fun onLikePlaylistError(error: Throwable, playlist: Playlist)
    }

    interface UnlikePlaylistCallbacks {
        fun onUnlikePlaylistSuccess(commonApiResponse: CommonApiResponse, playlist: Playlist)
        fun onUnlikePlaylistError(error: Throwable, playlist: Playlist)
    }

    interface FavouriteCallbacks {
        fun onFavouriteApiSuccess(playlists: List<Playlist>)
        fun onFavouriteApiError(error: Throwable)
    }
}


