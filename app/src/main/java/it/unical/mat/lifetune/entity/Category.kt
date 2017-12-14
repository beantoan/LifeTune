package it.unical.mat.lifetune.entity

import android.databinding.BaseObservable
import android.databinding.Bindable
import com.google.gson.annotations.SerializedName
import it.unical.mat.lifetune.BR

/**
 * Created by beantoan on 12/14/17.
 */

data class Category(
        @get:Bindable var id: Int,
        @SerializedName("title") internal var title: String,
        @SerializedName("recommendation", alternate = ["favourite"]) @get:Bindable var playlists: List<Playlist>
) : BaseObservable() {

    @Bindable
    fun getTitle(): String {
        return title
    }

    fun setTitle(title: String) {
        this.title = title
        notifyPropertyChanged(BR.title)
    }
}
