package it.unical.mat.lifetune.entity

import android.databinding.BaseObservable
import android.databinding.Bindable
import com.google.gson.annotations.SerializedName

/**
 * Created by beantoan on 11/20/17.
 */
data class Playlist(
        @SerializedName("id") var id: Int,
        @SerializedName("title") @get:Bindable var title: String,
        @SerializedName("desc") @get:Bindable var desc: String?,
        @SerializedName("key") @get:Bindable var key: String,
        @SerializedName("img") @get:Bindable var img: String,
        @SerializedName("favourite", alternate = ["songs"]) @get:Bindable var songs: List<Song>?,
        @SerializedName("is_liked") @get:Bindable var isLiked: Boolean = false,
        @get:Bindable var isPlaying: Boolean = false,
        var tracks: List<Track> = ArrayList()

) : BaseObservable() {
    fun shortLog(): String = "Playlist(id=$id, title=$title)"
}