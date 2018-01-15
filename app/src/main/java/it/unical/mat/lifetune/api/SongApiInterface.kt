package it.unical.mat.lifetune.api

import io.reactivex.Flowable
import it.unical.mat.lifetune.entity.Song
import retrofit2.http.GET
import retrofit2.http.Query

/**
 * Created by beantoan on 5/14/16.
 */
interface SongApiInterface {
    @GET("/songs/search.json")
    fun search(@Query("q") term: String): Flowable<List<Song>>
}