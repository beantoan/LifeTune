package it.unical.mat.lifetune.entity

/**
 * Created by beantoan on 1/9/18.
 * https://developers.google.com/android/reference/com/google/android/gms/location/DetectedActivity.html#IN_VEHICLE
 */
class FitnessActivity {
    companion object {
        val IN_VEHICLE: String = "in_vehicle"
        val ON_BICYCLE: String = "on_bicycle"
        val ON_FOOT: String = "on_foot"
        val RUNNING: String = "running"
        val STILL: String = "still"
        val TILTING: String = "tilting"
        val UNKNOWN: String = "unknown"
        val WALKING: String = "walking"
    }
}