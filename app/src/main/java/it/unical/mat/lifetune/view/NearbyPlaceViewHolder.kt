package it.unical.mat.lifetune.view

import android.databinding.DataBindingUtil
import android.support.v7.widget.RecyclerView
import android.view.View
import it.unical.mat.lifetune.databinding.ViewHolderNearbyPlaceBinding
import it.unical.mat.lifetune.entity.Place

/**
 * Created by beantoan on 1/14/18.
 */
class NearbyPlaceViewHolder(var view: View) : RecyclerView.ViewHolder(view) {

    private var place: Place? = null

    fun bindPlace(_place: Place) {
        place = _place

        val binding = DataBindingUtil.bind(view) as ViewHolderNearbyPlaceBinding
        binding.place = _place
        binding.executePendingBindings()
    }
}