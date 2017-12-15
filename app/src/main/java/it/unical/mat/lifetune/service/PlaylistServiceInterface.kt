package it.unical.mat.lifetune.service

import io.reactivex.Single
import it.unical.mat.lifetune.entity.Playlist
import it.unical.mat.lifetune.entity.Song
import retrofit2.http.GET
import retrofit2.http.Path

/**
 * Created by beantoan on 5/14/16.
 */
interface PlaylistServiceInterface {
    @GET("playlists/favourite.json")
    fun favourite(): Single<List<Playlist>>

    @GET("playlists/{id}/songs.json")
    fun songs(@Path("id") songId: Int): Single<List<Song>>
}