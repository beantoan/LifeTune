package it.unical.mat.lifetune.fragment

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.net.Uri
import android.os.Bundle
import android.support.design.widget.BottomSheetBehavior
import android.support.v4.content.ContextCompat
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.LinearLayoutManager
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.crashlytics.android.Crashlytics
import com.google.android.gms.awareness.Awareness
import com.google.android.gms.location.places.PlaceLikelihood
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import it.unical.mat.lifetune.R
import it.unical.mat.lifetune.activity.MainActivity
import it.unical.mat.lifetune.adapter.NearbyPlacesAdapter
import it.unical.mat.lifetune.decoration.RecyclerViewDividerItemDecoration
import it.unical.mat.lifetune.entity.ActivityResultEvent
import it.unical.mat.lifetune.entity.Place
import it.unical.mat.lifetune.util.AppDialog
import it.unical.mat.lifetune.util.AppUtils
import kotlinx.android.synthetic.main.fragment_nearby_places.*
import pub.devrel.easypermissions.EasyPermissions


/**
 * Created by beantoan on 11/17/17.
 */

/**
 * A dummy fragment representing a section of the app, but that simply displays dummy text.
 */
class NearbyPlacesFragment : BaseLocationFragment() {

    private var mainActivity: MainActivity? = null

    private var adapter: NearbyPlacesAdapter? = null

    private var isLoadingPlaces = false

    private var googleMap: GoogleMap? = null

    private var currentAddress: Address? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        super.onCreateView(inflater, container, savedInstanceState)

        val rootView = inflater.inflate(R.layout.fragment_nearby_places, container, false)

        return rootView
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        onViewCreatedTasks()
    }

    override fun onPause() {
        updateAdapterData(ArrayList())

        super.onPause()
    }

    override fun onCheckLocationSettingResult(event: ActivityResultEvent) {
        Log.d(TAG, "onCheckLocationSettingResult: event=${event.resultCode}")
    }

    override fun onLocationSettingEnabled() {
        Log.d(TAG, "onLocationSettingEnabled")

        callSnapshotPlacesApi()

        callSnapshotLocationApi()
    }

    override fun onLocationPermissionDenied() {
        Log.d(TAG, "onLocationPermissionDenied")
    }

    fun navigationToPlaceByGoogleMap(place: Place) {
        val gmmIntentUri = Uri.parse("google.navigation:q=${place.latLng.latitude},${place.latLng.longitude}")

        val mapIntent = Intent(Intent.ACTION_VIEW, gmmIntentUri)
        mapIntent.`package` = "com.google.android.apps.maps"

        startActivity(mapIntent)
    }

    private fun onViewCreatedTasks() {
        Log.d(MyActivitiesFragment.TAG, "onViewCreatedTasks")

        mainActivity = activity as MainActivity

        mainActivity!!.setupToggleDrawer(toolbar)

        setupRecyclerViewNearbyPlaces()

        setupBottomSheet()

        initializeGoogleMap()
    }

    private fun setupRecyclerViewNearbyPlaces() {
        Log.d(TAG, "setupRecyclerViewNearbyPlaces")

        val dividerDrawable = ContextCompat.getDrawable(activity!!.applicationContext, R.drawable.place_divider)
        val dividerItemDecoration = RecyclerViewDividerItemDecoration(activity!!.applicationContext, DividerItemDecoration.VERTICAL, dividerDrawable!!)

        recycler_view_nearby_places.layoutManager = LinearLayoutManager(activity!!.applicationContext)
        recycler_view_nearby_places.addItemDecoration(dividerItemDecoration)

        adapter = NearbyPlacesAdapter(this, ArrayList())

        recycler_view_nearby_places.adapter = adapter
    }

    private fun setupBottomSheet() {
        val bottomSheetBehavior = BottomSheetBehavior.from(bottom_sheet_google_map)

        show_map.setOnClickListener {
            if (bottomSheetBehavior.state == BottomSheetBehavior.STATE_EXPANDED) {
                bottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
                show_map.text = getString(R.string.button_show_map_title)
            } else {
                bottomSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED
                show_map.text = getString(R.string.button_hide_map_title)
            }
        }
    }

    @SuppressLint("MissingPermission")
    private fun callSnapshotLocationApi() {
        Log.d(TAG, "callSnapshotLocationApi")

        if (EasyPermissions.hasPermissions(context, Manifest.permission.ACCESS_FINE_LOCATION)) {

            if (AppUtils.isInternetConnected(context!!)) {
                Awareness.getSnapshotClient(activity).location
                        .addOnSuccessListener(activity!!, { locationResponse ->
                            Log.d(TAG, "Awareness.getSnapshotClient#location#addOnSuccessListener")

                            getCurrentAddress(locationResponse.location)
                        })
                        .addOnFailureListener(activity!!, { e ->
                            Crashlytics.log(Log.ERROR, TAG, "Awareness.getSnapshotClient#location#addOnFailureListener:" + e)
                            Crashlytics.logException(e)
                        })
            }
        }
    }

    private fun getCurrentAddress(location: Location) {
        Log.d(TAG, "getCurrentAddress")

        val geocoder = Geocoder(activity)

        try {
            val addresses = geocoder.getFromLocation(location.latitude, location.longitude, 1)

            currentAddress = addresses?.first()

            Log.d(TAG, "currentAddress = $currentAddress")

            current_location.text = currentAddress?.getAddressLine(0) ?: getString(R.string.get_current_location_error_message)

            zoomToCurrentAddressOnMap()

        } catch (e: Exception) {
            Crashlytics.log(Log.ERROR, TAG, "getCurrentAddress:" + e)
            Crashlytics.logException(e)
        }
    }

    @SuppressLint("MissingPermission")
    private fun initializeGoogleMap() {
        Log.d(TAG, "initializeGoogleMap")

        if (googleMap == null) {
            val map = childFragmentManager.findFragmentById(R.id.google_map) as SupportMapFragment

            map.getMapAsync { _googleMap ->
                googleMap = _googleMap

                if (EasyPermissions.hasPermissions(context, Manifest.permission.ACCESS_FINE_LOCATION)) {
                    googleMap?.isMyLocationEnabled = true
                }

                zoomToCurrentAddressOnMap()
            }
        } else {
            zoomToCurrentAddressOnMap()
        }
    }

    private fun zoomToCurrentAddressOnMap() {
        if (googleMap != null && currentAddress != null) {
            val latLng = LatLng(currentAddress!!.latitude, currentAddress!!.longitude)
            googleMap?.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, GOOGLE_MAP_ZOOM_LEVEL))
        }
    }

    @SuppressLint("MissingPermission")
    private fun callSnapshotPlacesApi() {
        Log.d(TAG, "callSnapshotPlacesApi")

        if (isLoadingPlaces) {
            return
        }

        isLoadingPlaces = true

        if (EasyPermissions.hasPermissions(context, Manifest.permission.ACCESS_FINE_LOCATION)) {

            if (AppUtils.isInternetConnected(context!!)) {

                showProgressBar(progress_bar)

                Awareness.getSnapshotClient(activity).places
                        .addOnCompleteListener(activity!!, {
                            isLoadingPlaces = false

                            hideProgressBar(progress_bar)
                        })
                        .addOnSuccessListener(activity!!, { placesResponse ->
                            Log.d(TAG, "Awareness.getSnapshotClient#places#addOnSuccessListener")

                            val places = parseNearbyPlaces(placesResponse.placeLikelihoods)

                            updateAdapterData(places)

                            addNearbyPlacesToMap(places)
                        })
                        .addOnFailureListener(activity!!, { e ->
                            Crashlytics.log(Log.ERROR, TAG, "Awareness.getSnapshotClient#places#addOnFailureListener:" + e)
                            Crashlytics.logException(e)

                            isLoadingPlaces = false

                            hideProgressBar(progress_bar)

                            AppDialog.error(R.string.get_nearby_places_error_title, R.string.get_nearby_places_error_message, activity!!)
                        })
            } else {
                AppDialog.error(R.string.no_internet_error_title, R.string.no_internet_error_message, activity!!)
            }
        }
    }

    private fun parseNearbyPlaces(placeLikelihoods: List<PlaceLikelihood>?): List<Place> {
        Log.d(TAG, "showNearbyPlaces")

        val places = ArrayList<Place>()

        placeLikelihoods?.forEach { placeLikelihood ->
            Log.d(TAG, placeLikelihood.toString())

            val place = Place(placeLikelihood.place.id, placeLikelihood.place.name.toString(),
                    placeLikelihood.place.address.toString(), placeLikelihood.place.phoneNumber.toString(),
                    placeLikelihood.place.latLng, placeLikelihood.place.rating)

            places.add(place)
        }

        return places
    }

    private fun updateAdapterData(places: List<Place>) {
        Log.d(TAG, "updateAdapterData: places.size=${places.size}")

        adapter!!.addAll(places)
    }

    private fun addNearbyPlacesToMap(places: List<Place>) {
        Log.d(TAG, "addNearbyPlacesToMap: places.size=${places.size}")

        if (googleMap != null) {

            googleMap?.clear()

            places.forEach { place ->

                val markerOptions = MarkerOptions()
                markerOptions.title(place.name)
                markerOptions.position(place.latLng)
                markerOptions.snippet(place.address)

                googleMap?.addMarker(markerOptions)
            }
        }
    }

    companion object {
        val TAG = NearbyPlacesFragment::class.java.simpleName

        val GOOGLE_MAP_ZOOM_LEVEL = 15f
    }
}
