package it.unical.mat.lifetune.entity

import android.databinding.BaseObservable
import android.databinding.Bindable
import com.google.gson.annotations.SerializedName

/**
 * Created by beantoan on 11/20/17.
 */
data class Song(
        @SerializedName("id") var id: Int,
        @SerializedName("title") @get:Bindable var title: String,
        @SerializedName("mp3_url") @get:Bindable var mp3_url: String
) : BaseObservable()