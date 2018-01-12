package it.unical.mat.lifetune.entity

import android.databinding.BaseObservable
import android.databinding.Bindable
import com.google.android.gms.maps.model.LatLng

/**
 * Created by beantoan on 1/6/18.
 */
data class Place(
        val id: String,
        @get:Bindable val name: String,
        @get:Bindable val address: String,
        @get:Bindable val phoneNumber: String,
        @get:Bindable val latLng: LatLng,
        @get:Bindable val rating: Float
) : BaseObservable()