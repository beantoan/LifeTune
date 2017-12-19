package it.unical.mat.lifetune.service

import io.reactivex.Flowable
import it.unical.mat.lifetune.entity.PlaylistXml
import retrofit2.http.GET
import retrofit2.http.Query

/**
 * Created by beantoan on 5/14/16.
 */
interface PlaylistXmlServiceInterface {
    @GET("flash/xml")
    fun songs(@Query("key2") key: String): Flowable<PlaylistXml>
}