package it.unical.mat.lifetune.api

import io.reactivex.Single
import it.unical.mat.lifetune.entity.CommonApiResponse
import it.unical.mat.lifetune.entity.Playlist
import it.unical.mat.lifetune.entity.Song
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

/**
 * Created by beantoan on 5/14/16.
 */
interface PlaylistApiInterface {
    @GET("playlists/favourite.json")
    fun favourite(@Query("user_id") user_id: String): Single<List<Playlist>>

    @GET("playlists/{id}/songs.json")
    fun songs(@Path("id") songId: Int): Single<List<Song>>

    @GET("playlists/{playlist_id}/like.json")
    fun like(@Path("playlist_id") playlistId: Int, @Query("user_id") user_id: String): Single<CommonApiResponse>

    @GET("playlists/{playlist_id}/unlike.json")
    fun unlike(@Path("playlist_id") playlistId: Int, @Query("user_id") user_id: String): Single<CommonApiResponse>
}