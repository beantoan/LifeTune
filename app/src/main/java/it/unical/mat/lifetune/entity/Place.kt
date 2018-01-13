package it.unical.mat.lifetune.entity

import android.databinding.BaseObservable
import android.databinding.Bindable
import com.google.android.gms.maps.model.LatLng
import org.apache.commons.lang3.StringUtils

/**
 * Created by beantoan on 1/6/18.
 */
data class Place(
        val id: String,
        @get:Bindable val name: String,
        @get:Bindable var address: String,
        @get:Bindable var phoneNumber: String,
        @get:Bindable var latLng: LatLng,
        @get:Bindable var rating: Float,
        @get:Bindable var photos: ArrayList<String> = ArrayList()
) : BaseObservable() {

    @Bindable
    fun getInfo(): String {
        val info = StringBuilder()

        if (StringUtils.isNotBlank(phoneNumber)) {
            info.append("Phone: ")
            info.append(phoneNumber)

            info.append("\n")
        }

        info.append(address)

        return info.toString()
    }

    fun addPhoto(photoPath: String) {
        photos.add(photoPath)
    }

    override fun equals(other: Any?): Boolean {
        if (other is Place) {
            return id == other.id
        }

        return false
    }
}