package it.unical.mat.lifetune.fragment

import android.Manifest
import android.content.pm.PackageManager
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.os.Bundle
import android.support.design.widget.BottomSheetBehavior
import android.support.v4.content.ContextCompat
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.LinearLayoutManager
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.gms.awareness.Awareness
import com.google.android.gms.location.places.PlaceLikelihood
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.firebase.crash.FirebaseCrash
import it.unical.mat.lifetune.R
import it.unical.mat.lifetune.activity.MainActivity
import it.unical.mat.lifetune.controller.NearbyPlacesController
import it.unical.mat.lifetune.decoration.RecyclerViewDividerItemDecoration
import it.unical.mat.lifetune.entity.ActivityResultEvent
import it.unical.mat.lifetune.entity.Place
import it.unical.mat.lifetune.util.AppDialog
import it.unical.mat.lifetune.util.AppUtils
import kotlinx.android.synthetic.main.fragment_nearby_places.*


/**
 * Created by beantoan on 11/17/17.
 */

/**
 * A dummy fragment representing a section of the app, but that simply displays dummy text.
 */
class NearbyPlacesFragmentBase : BaseLocationFragment(),
        NearbyPlacesController.AdapterCallbacks {

    private var mainActivity: MainActivity? = null

    private lateinit var controller: NearbyPlacesController

    private var isLoadingPlaces = false

    private val nearbyPlaces = ArrayList<Place>()

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

    override fun onPlaceClicked(place: Place?) {

    }

    private fun onViewCreatedTasks() {
        Log.d(MyActivitiesFragment.TAG, "onViewCreatedTasks")

        mainActivity = activity as MainActivity

        mainActivity!!.setupToggleDrawer(toolbar)

        setupRecyclerViewNearbyPlaces()

        setupNearbyPlacesController()

        setupBottomSheet()
    }

    private fun setupRecyclerViewNearbyPlaces() {
        Log.d(TAG, "setupRecyclerViewNearbyPlaces")

        val dividerDrawable = ContextCompat.getDrawable(activity!!.applicationContext, R.drawable.category_divider)
        val dividerItemDecoration = RecyclerViewDividerItemDecoration(activity!!.applicationContext, DividerItemDecoration.VERTICAL, dividerDrawable!!)

        recycler_view_nearby_places.layoutManager = LinearLayoutManager(activity!!.applicationContext)
        recycler_view_nearby_places.addItemDecoration(dividerItemDecoration)
    }

    private fun setupNearbyPlacesController() {
        Log.d(TAG, "setupNearbyPlacesController")

        controller = NearbyPlacesController(this)

        recycler_view_nearby_places.clear()
        recycler_view_nearby_places.setController(controller)

        controller.setData(nearbyPlaces)
    }

    private fun setupBottomSheet() {
        val bottomSheetBehavior = BottomSheetBehavior.from(bottom_sheet)

        show_map.setOnClickListener {
            if (bottomSheetBehavior.state == BottomSheetBehavior.STATE_EXPANDED) {
                bottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
                show_map.text = getString(R.string.button_show_map_title)
            } else {
                bottomSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED
                show_map.text = getString(R.string.button_hide_map_title)
            }
        }

        bottomSheetBehavior.setBottomSheetCallback(object : BottomSheetBehavior.BottomSheetCallback() {
            override fun onSlide(bottomSheet: View, slideOffset: Float) {

            }

            override fun onStateChanged(bottomSheet: View, newState: Int) {
                when (newState) {
                    BottomSheetBehavior.STATE_DRAGGING -> bottomSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED
                    BottomSheetBehavior.STATE_EXPANDED -> showGoogleMap()
                }
            }
        })

    }

    private fun callSnapshotLocationApi() {
        Log.d(TAG, "callSnapshotLocationApi")

        if (ContextCompat.checkSelfPermission(activity!!, Manifest.permission.ACCESS_FINE_LOCATION) ==
                PackageManager.PERMISSION_GRANTED) {

            if (AppUtils.isInternetConnected(context!!)) {
                Awareness.getSnapshotClient(activity).location
                        .addOnSuccessListener(activity!!, { locationResponse ->
                            Log.d(TAG, "Awareness.getSnapshotClient#location#addOnSuccessListener")

                            getCurrentAddress(locationResponse.location)
                        })
                        .addOnFailureListener(activity!!, { e ->
                            FirebaseCrash.logcat(Log.ERROR, TAG, "Awareness.getSnapshotClient#location#addOnFailureListener:" + e)
                            FirebaseCrash.report(e)
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
            FirebaseCrash.logcat(Log.ERROR, TAG, "getCurrentAddress:" + e)
            FirebaseCrash.report(e)
        }
    }

    private fun showGoogleMap() {
        if (googleMap == null) {
            val map = childFragmentManager.findFragmentById(R.id.google_map) as SupportMapFragment

            map.getMapAsync { _googleMap ->
                googleMap = _googleMap

                if (ContextCompat.checkSelfPermission(activity!!, Manifest.permission.ACCESS_FINE_LOCATION) ==
                        PackageManager.PERMISSION_GRANTED) {
                    googleMap?.isMyLocationEnabled = true
                }

                zoomToCurrentAddressOnMap()

                addNearbyPlacesToMap()
            }
        } else {
            zoomToCurrentAddressOnMap()

            addNearbyPlacesToMap()
        }
    }

    private fun zoomToCurrentAddressOnMap() {
        if (googleMap != null && currentAddress != null) {
            val latLng = LatLng(currentAddress!!.latitude, currentAddress!!.longitude)
            googleMap?.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, GOOGLE_MAP_ZOOM_LEVEL))
        }
    }

    private fun callSnapshotPlacesApi() {
        Log.d(TAG, "callSnapshotPlacesApi")

        if (isLoadingPlaces) {
            return
        }

        isLoadingPlaces = true

        if (ContextCompat.checkSelfPermission(activity!!, Manifest.permission.ACCESS_FINE_LOCATION) ==
                PackageManager.PERMISSION_GRANTED) {

            if (AppUtils.isInternetConnected(context!!)) {

                AppDialog.showProgress(R.string.progress_dialog_waiting_message, context!!)

                Awareness.getSnapshotClient(activity).places
                        .addOnCompleteListener(activity!!, {
                            isLoadingPlaces = false

                            AppDialog.hideProgress(context!!)
                        })
                        .addOnSuccessListener(activity!!, { placesResponse ->
                            Log.d(TAG, "Awareness.getSnapshotClient#places#addOnSuccessListener")

                            showNearbyPlaces(placesResponse.placeLikelihoods)

                            addNearbyPlacesToMap()
                        })
                        .addOnFailureListener(activity!!, { e ->
                            FirebaseCrash.logcat(Log.ERROR, TAG, "Awareness.getSnapshotClient#places#addOnFailureListener:" + e)
                            FirebaseCrash.report(e)

                            isLoadingPlaces = false

                            AppDialog.hideProgress(context!!)

                            AppDialog.error(R.string.get_nearby_places_error_title, R.string.get_nearby_places_error_message, activity!!)
                        })
            } else {
                AppDialog.error(R.string.no_internet_error_title, R.string.no_internet_error_message, activity!!)
            }
        }
    }

    private fun showNearbyPlaces(placeLikelihoods: List<PlaceLikelihood>?) {
        Log.d(TAG, "showNearbyPlaces")

        nearbyPlaces.clear()

        placeLikelihoods?.forEach { placeLikelihood ->
            Log.d(TAG, placeLikelihood.toString())

            val place = placeLikelihood.place

            nearbyPlaces.add(Place(place.id, place.name.toString(), place.address.toString(),
                    place.phoneNumber.toString(), place.latLng, place.rating))
        }

        controller.setData(nearbyPlaces)
    }

    private fun addNearbyPlacesToMap() {
        Log.d(TAG, "addNearbyPlacesToMap")

        if (googleMap != null) {

            googleMap?.clear()

            nearbyPlaces.forEach { place ->

                val markerOptions = MarkerOptions()
                markerOptions.title(place.name)
                markerOptions.position(place.latLng)
                markerOptions.snippet(place.address)

                googleMap?.addMarker(markerOptions)
            }
        }
    }

    companion object {
        val TAG = NearbyPlacesFragmentBase::class.java.simpleName
        val GOOGLE_MAP_ZOOM_LEVEL = 15f
    }
}
