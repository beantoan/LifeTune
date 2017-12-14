package it.unical.mat.lifetune.service

import io.reactivex.Single
import it.unical.mat.lifetune.entity.Playlist
import retrofit2.http.GET

/**
 * Created by beantoan on 5/14/16.
 */
interface PlaylistServiceInterface {
    @GET("playlists/favourite.json")
    fun favourite(): Single<List<Playlist>>
}
