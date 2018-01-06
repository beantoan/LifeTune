package it.unical.mat.lifetune.fragment

import android.Manifest
import android.app.Activity
import android.content.DialogInterface
import android.content.IntentSender
import android.content.pm.PackageManager
import android.location.Geocoder
import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.LinearLayoutManager
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.gms.awareness.Awareness
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.LocationSettingsRequest
import com.google.android.gms.location.LocationSettingsStatusCodes
import com.google.firebase.crash.FirebaseCrash
import it.unical.mat.lifetune.R
import it.unical.mat.lifetune.activity.MainActivity
import it.unical.mat.lifetune.controller.RecommendationMusicController
import it.unical.mat.lifetune.decoration.RecyclerViewDividerItemDecoration
import it.unical.mat.lifetune.entity.Category
import it.unical.mat.lifetune.util.AppDialog
import kotlinx.android.synthetic.main.fragment_recommended_music.*


/**
 * Created by beantoan on 11/17/17.
 */
class RecommendedMusicFragment : BaseMusicFragment() {

    private lateinit var controller: RecommendationMusicController

    var recommendationCategories: List<Category> = ArrayList()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_recommended_music, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        onCreateViewTasks(view)
    }

    override fun onRecommendationServiceSuccess(categories: List<Category>) {
        super.onRecommendationServiceSuccess(categories)

        recommendationCategories = categories

        controller.setData(recommendationCategories)
    }

    override fun onRecommendationServiceFailure(error: Throwable) {
        super.onRecommendationServiceFailure(error)

        recommendationCategories = ArrayList()

        controller.setData(recommendationCategories)
    }

    private fun onCreateViewTasks(view: View) {
        Log.d(TAG, "onCreateViewTasks")

        setupRecyclerViewCategories()

        setupMusicController()

        checkLocationSetting()
    }

    private fun checkLocationSetting() {
        Log.d(TAG, "checkLocationSetting")

        if (ContextCompat.checkSelfPermission(activity!!, Manifest.permission.ACCESS_FINE_LOCATION) ==
                PackageManager.PERMISSION_GRANTED) {
            val mLocationRequest = LocationRequest()
            mLocationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY

            val builder = LocationSettingsRequest.Builder()
            builder.addLocationRequest(mLocationRequest)
            val locationSettingsRequest = builder.build()

            val settingsClient = LocationServices.getSettingsClient(activity!!)
            val result = settingsClient.checkLocationSettings(locationSettingsRequest)

            result.addOnCompleteListener { locationSettingsResponse ->
                Log.d(TAG, "checkLocationSetting#addOnCompleteListener")

                try {
                    locationSettingsResponse.getResult(ApiException::class.java)

                    callSnapshotApi()

                } catch (exception: ApiException) {
                    FirebaseCrash.logcat(Log.ERROR, TAG, "checkLocationSetting#addOnCompleteListener#ApiException:" + exception)
                    FirebaseCrash.report(exception)

                    when (exception.statusCode) {
                        LocationSettingsStatusCodes.RESOLUTION_REQUIRED -> {
                            try {
                                val resolvable = exception as ResolvableApiException

                                resolvable.startResolutionForResult(activity, MainActivity.CHECK_LOCATION_SETTINGS_REQUEST_CODE)

                            } catch (e: IntentSender.SendIntentException) {
                                FirebaseCrash.logcat(Log.ERROR, TAG, "checkLocationSetting#addOnCompleteListener#SendIntentException:" + e)
                                FirebaseCrash.report(e)

                                AppDialog.warning(R.string.error_turn_on_location_title, R.string.error_turn_on_location_message, activity!!)
                            } catch (e: ClassCastException) {
                                FirebaseCrash.logcat(Log.ERROR, TAG, "checkLocationSetting#addOnCompleteListener#ClassCastException:" + e)
                                FirebaseCrash.report(e)

                                AppDialog.warning(R.string.error_turn_on_location_title, R.string.error_turn_on_location_message, activity!!)
                            }
                        }
                        else -> {
                            AppDialog.warning(R.string.error_turn_on_location_title, R.string.error_turn_on_location_message, activity!!)
                        }
                    }
                }
            }
        } else {
            callRecommendationService()
        }

    }

    private fun callSnapshotApi() {
        Log.d(TAG, "callSnapshotApi")

        if (ContextCompat.checkSelfPermission(activity!!, Manifest.permission.ACCESS_FINE_LOCATION) ==
                PackageManager.PERMISSION_GRANTED) {
            Awareness.getSnapshotClient(activity).location
                    .addOnSuccessListener({ locationResponse ->
                        Log.d(TAG, "Awareness.getSnapshotClient#location#addOnSuccessListener")

                        val location = locationResponse.location
                        val geocoder = Geocoder(activity)
                        val addresses = geocoder.getFromLocation(location.latitude, location.longitude, 1)
                        val countryCode = addresses.first().countryCode

                        Log.d(TAG, "countryCode = $countryCode")

                        recommendationParameter.countryCode = countryCode

                        callSnapshotActivity()
                    })
                    .addOnFailureListener({ e ->
                        FirebaseCrash.logcat(Log.ERROR, TAG, "Awareness.getSnapshotClient#location#addOnFailureListener:" + e)
                        FirebaseCrash.report(e)

                        recommendationParameter.countryCode = null

                        callSnapshotActivity()
                    })
        }
    }

    private fun callSnapshotActivity() {
        Awareness.getSnapshotClient(activity).detectedActivity
                .addOnSuccessListener { detectedActivityResponse ->
                    Log.d(TAG, "Awareness.getSnapshotClient#detectedActivity#addOnSuccessListener")

                    val activityRecognitionResult = detectedActivityResponse.activityRecognitionResult
                    val mostProbableActivity = activityRecognitionResult.mostProbableActivity

                    Log.d(TAG, "probableActivity = ${mostProbableActivity.type}, confidence = ${mostProbableActivity.confidence}")

                    recommendationParameter.activityType = mostProbableActivity.type

                    callSnapshotWeather()
                }
                .addOnFailureListener { e ->
                    FirebaseCrash.logcat(Log.ERROR, TAG, "Awareness.getSnapshotClient#detectedActivity#addOnFailureListener:" + e)
                    FirebaseCrash.report(e)

                    recommendationParameter.activityType = null

                    callSnapshotWeather()
                }
    }

    private fun callSnapshotWeather() {
        if (ContextCompat.checkSelfPermission(activity!!, Manifest.permission.ACCESS_FINE_LOCATION) ==
                PackageManager.PERMISSION_GRANTED) {

            Awareness.getSnapshotClient(activity).weather
                    .addOnSuccessListener { weatherResponse ->
                        Log.d(TAG, "Awareness.getSnapshotClient#weather#addOnSuccessListener")

                        val temp = weatherResponse.weather.getFeelsLikeTemperature(2)

                        Log.d(TAG, "temp = $temp")

                        recommendationParameter.temp = temp

                        callRecommendationService()
                    }
                    .addOnFailureListener { e ->
                        FirebaseCrash.logcat(Log.ERROR, TAG, "Awareness.getSnapshotClient#weather#addOnFailureListener:" + e)
                        FirebaseCrash.report(e)

                        recommendationParameter.temp = null

                        callRecommendationService()
                    }
        }
    }

    private fun setupRecyclerViewCategories() {
        Log.d(TAG, "setupRecyclerViewCategories")

        val dividerDrawable = ContextCompat.getDrawable(activity!!.applicationContext, R.drawable.category_divider)
        val dividerItemDecoration = RecyclerViewDividerItemDecoration(activity!!.applicationContext, DividerItemDecoration.VERTICAL, dividerDrawable!!)

        recycler_view_categories.layoutManager = LinearLayoutManager(activity!!.applicationContext)
        recycler_view_categories.addItemDecoration(dividerItemDecoration)
    }

    private fun setupMusicController() {
        Log.d(TAG, "setupMusicController")

        controller = RecommendationMusicController(this)

        recycler_view_categories.clear()
        recycler_view_categories.setController(controller)

        controller.setData(recommendationCategories)
    }

    private fun showCategories() {
        Log.d(TAG, "showCategories: " + recommendationCategories.size + " items")

        controller.setData(recommendationCategories)

        hideLoading()
    }

    fun onCheckLocationSettingResult(resultCode: Int) {
        Log.d(TAG, "onCheckLocationSettingResult: resultCode = $resultCode")

        when (resultCode) {
            Activity.RESULT_OK -> {
                callSnapshotApi()
            }
            else -> {
                AppDialog.warning(R.string.error_turn_on_location_title, R.string.error_turn_on_location_message, activity!!,
                        DialogInterface.OnDismissListener {
                            callRecommendationService()
                        })
            }
        }
    }

    companion object {
        private val TAG = RecommendedMusicFragment::class.java.canonicalName

        fun newInstance(playMusicFragment: PlayMusicFragment): RecommendedMusicFragment {
            val fragment = RecommendedMusicFragment()

            fragment.playMusicFragment = playMusicFragment

            return fragment
        }
    }
}
