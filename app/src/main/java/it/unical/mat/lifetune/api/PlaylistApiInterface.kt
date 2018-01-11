package it.unical.mat.lifetune.api

import io.reactivex.Flowable
import it.unical.mat.lifetune.entity.Playlist
import it.unical.mat.lifetune.entity.Song
import retrofit2.http.GET
import retrofit2.http.Path

/**
 * Created by beantoan on 5/14/16.
 */
interface PlaylistApiInterface {
    @GET("playlists/favourite.json")
    fun favourite(): Flowable<List<Playlist>>

    @GET("playlists/{id}/songs.json")
    fun songs(@Path("id") songId: Int): Flowable<List<Song>>
}