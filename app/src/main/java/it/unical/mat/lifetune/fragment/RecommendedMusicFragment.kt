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
import com.crashlytics.android.Crashlytics
import com.google.android.gms.awareness.Awareness
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.LocationSettingsRequest
import com.google.android.gms.location.LocationSettingsStatusCodes
import it.unical.mat.lifetune.R
import it.unical.mat.lifetune.controller.RecommendationMusicController
import it.unical.mat.lifetune.decoration.RecyclerViewDividerItemDecoration
import it.unical.mat.lifetune.entity.ActivityResultEvent
import it.unical.mat.lifetune.entity.Category
import it.unical.mat.lifetune.util.AppDialog
import it.unical.mat.lifetune.util.AppUtils
import kotlinx.android.synthetic.main.fragment_recommended_music.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe


/**
 * Created by beantoan on 11/17/17.
 */
class RecommendedMusicFragment : BaseMusicFragment() {

    private var controller: RecommendationMusicController? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_recommended_music, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        onCreateViewTasks(view)
    }

    override fun onStop() {
        onStopTasks()

        super.onStop()
    }

    override fun onPause() {
        updateControllerData(ArrayList())

        super.onPause()
    }

    override fun beforeCallRecommendationApi() {
        super.beforeCallRecommendationApi()

        updateControllerData(ArrayList())
    }

    override fun onRecommendationApiSuccess(categories: List<Category>) {
        super.onRecommendationApiSuccess(categories)

        updateControllerData(categories)
    }

    override fun onRecommendationApiFailure(error: Throwable) {
        super.onRecommendationApiFailure(error)

        updateControllerData(ArrayList())
    }

    override fun startLoadingData() {
        checkLocationSetting()
    }

    @Subscribe
    fun onActivityResultEvent(event: ActivityResultEvent) {
        Log.d(TAG, "onActivityResultEvent: requestCode=${event.requestCode}, resultCode=${event.resultCode}")

        when (event.requestCode) {
            CHECK_LOCATION_SETTINGS_REQUEST_CODE -> {
                onCheckLocationSettingResult(event.resultCode)
            }

        }
    }

    private fun onCreateViewTasks(view: View) {
        Log.d(TAG, "onCreateViewTasks")

        EventBus.getDefault().register(this)

        setupRecyclerViewCategories()

        setupMusicController()

        checkLocationSetting()
    }

    private fun onStopTasks() {
        EventBus.getDefault().unregister(this)
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

                    callSnapshotLocationApi()

                } catch (exception: ApiException) {
                    Crashlytics.log(Log.ERROR, TAG, "checkLocationSetting#addOnCompleteListener#ApiException:" + exception)
                    Crashlytics.logException(exception)

                    when (exception.statusCode) {
                        LocationSettingsStatusCodes.RESOLUTION_REQUIRED -> {
                            try {
                                val resolvable = exception as ResolvableApiException

                                resolvable.startResolutionForResult(activity, CHECK_LOCATION_SETTINGS_REQUEST_CODE)

                            } catch (e: IntentSender.SendIntentException) {
                                Crashlytics.log(Log.ERROR, TAG, "checkLocationSetting#addOnCompleteListener#SendIntentException:" + e)
                                Crashlytics.logException(e)

                                AppDialog.warning(R.string.error_turn_on_location_title, R.string.error_turn_on_location_message, activity!!)
                            } catch (e: ClassCastException) {
                                Crashlytics.log(Log.ERROR, TAG, "checkLocationSetting#addOnCompleteListener#ClassCastException:" + e)
                                Crashlytics.logException(e)

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
            callRecommendationApi()
        }

    }

    private fun callSnapshotLocationApi() {
        Log.d(TAG, "callSnapshotLocationApi")

        if (ContextCompat.checkSelfPermission(activity!!, Manifest.permission.ACCESS_FINE_LOCATION) ==
                PackageManager.PERMISSION_GRANTED) {

            if (AppUtils.isInternetConnected(context!!)) {
                Awareness.getSnapshotClient(activity).location
                        .addOnSuccessListener(activity!!, { locationResponse ->
                            Log.d(TAG, "Awareness.getSnapshotClient#location#addOnSuccessListener")

                            val location = locationResponse.location
                            val geocoder = Geocoder(activity)

                            try {
                                val addresses = geocoder.getFromLocation(location.latitude, location.longitude, 1)
                                val countryCode = addresses.first().countryCode

                                Log.d(TAG, "countryCode = $countryCode")

                                recommendationParameter.countryCode = countryCode
                            } catch (e: Exception) {
                                Crashlytics.log(Log.ERROR, TAG, "Awareness.getSnapshotClient#location#addOnSuccessListener:" + e)
                                Crashlytics.logException(e)
                            }

                            callSnapshotActivity()
                        })
                        .addOnFailureListener(activity!!, { e ->
                            Crashlytics.log(Log.ERROR, TAG, "Awareness.getSnapshotClient#location#addOnFailureListener:" + e)
                            Crashlytics.logException(e)

                            recommendationParameter.countryCode = null

                            callSnapshotActivity()
                        })
            } else {
                AppDialog.error(R.string.no_internet_error_title, R.string.no_internet_error_message, activity!!)
            }
        }
    }

    private fun callSnapshotActivity() {

        if (AppUtils.isInternetConnected(context!!)) {
            Awareness.getSnapshotClient(activity).detectedActivity
                    .addOnSuccessListener(activity!!, { detectedActivityResponse ->
                        Log.d(TAG, "Awareness.getSnapshotClient#detectedActivity#addOnSuccessListener")

                        val activityRecognitionResult = detectedActivityResponse.activityRecognitionResult
                        val mostProbableActivity = activityRecognitionResult.mostProbableActivity

                        Log.d(TAG, "probableActivity = ${mostProbableActivity.type}, confidence = ${mostProbableActivity.confidence}")

                        recommendationParameter.activityType = mostProbableActivity.type

                        callSnapshotWeather()
                    })
                    .addOnFailureListener(activity!!, { e ->
                        Crashlytics.log(Log.ERROR, TAG, "Awareness.getSnapshotClient#detectedActivity#addOnFailureListener:" + e)
                        Crashlytics.logException(e)

                        recommendationParameter.activityType = null

                        callSnapshotWeather()
                    })
        } else {
            AppDialog.error(R.string.no_internet_error_title, R.string.no_internet_error_message, activity!!)
        }
    }

    private fun callSnapshotWeather() {
        if (ContextCompat.checkSelfPermission(activity!!, Manifest.permission.ACCESS_FINE_LOCATION) ==
                PackageManager.PERMISSION_GRANTED) {

            if (AppUtils.isInternetConnected(context!!)) {
                Awareness.getSnapshotClient(activity).weather
                        .addOnSuccessListener(activity!!, { weatherResponse ->
                            Log.d(TAG, "Awareness.getSnapshotClient#weather#addOnSuccessListener")

                            val temp = weatherResponse.weather.getFeelsLikeTemperature(2)

                            Log.d(TAG, "temp = $temp")

                            recommendationParameter.temp = temp

                            callRecommendationApi()
                        })
                        .addOnFailureListener(activity!!, { e ->
                            Crashlytics.log(Log.ERROR, TAG, "Awareness.getSnapshotClient#weather#addOnFailureListener:" + e)
                            Crashlytics.logException(e)

                            recommendationParameter.temp = null

                            callRecommendationApi()
                        })
            } else {
                AppDialog.error(R.string.no_internet_error_title, R.string.no_internet_error_message, activity!!)
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
    }

    private fun updateControllerData(categories: List<Category>) {
        controller?.cancelPendingModelBuild()
        controller?.setData(categories)
    }

    private fun onCheckLocationSettingResult(resultCode: Int) {
        Log.d(TAG, "onCheckLocationSettingResult: resultCode = $resultCode")

        when (resultCode) {
            Activity.RESULT_OK -> {
                callSnapshotLocationApi()
            }
            else -> {
                AppDialog.warning(R.string.error_turn_on_location_title, R.string.error_turn_on_location_message, activity!!,
                        DialogInterface.OnDismissListener {
                            callRecommendationApi()
                        })
            }
        }
    }

    companion object {
        private val TAG = RecommendedMusicFragment::class.java.simpleName
        const val CHECK_LOCATION_SETTINGS_REQUEST_CODE = 101

        fun newInstance(playMusicFragment: PlayMusicFragment): RecommendedMusicFragment {
            val fragment = RecommendedMusicFragment()

            fragment.playMusicFragment = playMusicFragment

            return fragment
        }
    }
}
