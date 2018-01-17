package it.unical.mat.lifetune.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import it.unical.mat.lifetune.R
import it.unical.mat.lifetune.entity.Place
import it.unical.mat.lifetune.fragment.NearbyPlacesFragment
import it.unical.mat.lifetune.view.NearbyPlaceViewHolder


/**
 * Created by beantoan on 1/14/18.
 */
class NearbyPlacesAdapter(val nearbyPlacesFragment: NearbyPlacesFragment, places: ArrayList<Place>) :
        ManipulatedAdapter<Place, NearbyPlaceViewHolder>(places) {

    override fun onBindViewHolder(holder: NearbyPlaceViewHolder?, position: Int) {
        val place = this.getItem(position)

        holder!!.bindPlace(place)
    }

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): NearbyPlaceViewHolder {
        val view = LayoutInflater.from(parent!!.context)
                .inflate(R.layout.view_holder_nearby_place, parent, false)
        return NearbyPlaceViewHolder(nearbyPlacesFragment, view)
    }


}