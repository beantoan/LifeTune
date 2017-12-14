package it.unical.mat.lifetune.service

import io.reactivex.Single
import retrofit2.http.GET

/**
 * Created by beantoan on 5/14/16.
 */
interface PlaylistServiceInterface {
    @GET("playlists.json")
    fun index(): Single<PlaylistResponse>
}
