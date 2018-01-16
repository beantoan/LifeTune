package it.unical.mat.lifetune.api

import io.reactivex.Single
import it.unical.mat.lifetune.entity.TrackList
import retrofit2.http.GET
import retrofit2.http.Query

/**
 * Created by beantoan on 5/14/16.
 */
interface TrackListApiInterface {
    @GET("flash/xml")
    fun songs(@Query("key2") key: String): Single<TrackList>
}