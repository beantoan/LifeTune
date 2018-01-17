package it.unical.mat.lifetune.fragment

import android.Manifest
import android.content.IntentSender
import android.util.Log
import com.crashlytics.android.Crashlytics
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.LocationSettingsRequest
import com.google.android.gms.location.LocationSettingsStatusCodes
import it.unical.mat.lifetune.R
import it.unical.mat.lifetune.entity.ActivityResultEvent
import it.unical.mat.lifetune.util.AppDialog
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import pub.devrel.easypermissions.EasyPermissions

/**
 * Created by beantoan on 1/12/18.
 */
abstract class BaseLocationFragment : BaseFragment() {

    override fun onStart() {
        super.onStart()

        EventBus.getDefault().register(this)

        checkLocationSetting()
    }

    override fun onStop() {
        super.onStop()

        EventBus.getDefault().unregister(this)
    }

    @Subscribe
    fun onActivityResultEvent(event: ActivityResultEvent) {
        Log.d(TAG, "onActivityResultEvent: requestCode=${event.requestCode}, resultCode=${event.resultCode}")

        when (event.requestCode) {
            CHECK_LOCATION_SETTINGS_REQUEST_CODE -> {
                onCheckLocationSettingResult(event)
            }

        }
    }

    abstract fun onCheckLocationSettingResult(event: ActivityResultEvent)

    abstract fun onLocationSettingEnabled()

    abstract fun onLocationPermissionDenied()

    private fun checkLocationSetting() {
        Log.d(TAG, "checkLocationSetting")

        if (EasyPermissions.hasPermissions(context, Manifest.permission.ACCESS_FINE_LOCATION)) {

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

                    onLocationSettingEnabled()

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
            onLocationPermissionDenied()
        }
    }

    companion object {
        val TAG = BaseLocationFragment::class.java.simpleName
        val CHECK_LOCATION_SETTINGS_REQUEST_CODE = 101
    }
}