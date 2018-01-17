package it.unical.mat.lifetune.api

import io.reactivex.Single
import it.unical.mat.lifetune.entity.Playlist
import it.unical.mat.lifetune.entity.Song
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

/**
 * Created by beantoan on 5/14/16.
 */
interface SongApiInterface {
    @GET("/songs/search.json")
    fun search(@Query("q") term: String): Single<List<Song>>

    @GET("songs/{song_id}/playlist.json")
    fun playlist(@Path("song_id") songId: Int, @Query("user_id") user_id: String): Single<Playlist>
}