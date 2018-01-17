package it.unical.mat.lifetune.view

import android.databinding.DataBindingUtil
import android.support.v7.widget.RecyclerView
import android.view.View
import butterknife.ButterKnife
import butterknife.OnClick
import it.unical.mat.lifetune.R
import it.unical.mat.lifetune.databinding.ViewHolderNearbyPlaceBinding
import it.unical.mat.lifetune.entity.Place
import it.unical.mat.lifetune.fragment.NearbyPlacesFragment

/**
 * Created by beantoan on 1/14/18.
 */
class NearbyPlaceViewHolder(val nearbyPlacesFragment: NearbyPlacesFragment, var view: View)
    : RecyclerView.ViewHolder(view) {

    init {
        ButterKnife.bind(this, view)
    }

    private var place: Place? = null

    fun bindPlace(_place: Place) {
        place = _place

        val binding = DataBindingUtil.bind(view) as ViewHolderNearbyPlaceBinding
        binding.place = _place
        binding.executePendingBindings()
    }

    @OnClick(R.id.get_direction)
    fun onGetDirectionClicked() {
        nearbyPlacesFragment.navigationToPlaceByGoogleMap(place!!)
    }
}