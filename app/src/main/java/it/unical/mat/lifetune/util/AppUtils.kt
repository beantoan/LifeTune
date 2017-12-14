package it.unical.mat.lifetune.util

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.net.Uri
import android.os.Build
import android.provider.Settings
import android.util.Log
import android.view.inputmethod.InputMethodManager
import android.widget.EditText

/**
 * Created by beantoan on 1/27/16.
 */
object AppUtils {
    private val TAG = "AppUtils"

    fun hideKeyboard(activity: Activity) {
        try {
            val imm = activity.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager

            val currentFocus = activity.currentFocus

            if (currentFocus != null) {
                imm.hideSoftInputFromWindow(currentFocus.windowToken, 0)
            }
        } catch (e: Exception) {
            Log.e(TAG, "hideKeyboard", e)
        }

    }

    fun showKeyboard(activity: Activity, editText: EditText) {
        try {
            val imm = activity.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.showSoftInput(editText, InputMethodManager.SHOW_IMPLICIT)
        } catch (e: Exception) {
            Log.e(TAG, "showKeyboard", e)
        }

    }

    fun isInternetConnected(context: Context): Boolean {
        try {
            val cm = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

            val activeNetwork = cm.activeNetworkInfo

            return activeNetwork != null && activeNetwork.isConnected
        } catch (e: NullPointerException) {
            Log.e(TAG, "isInternetConnected", e)
        }

        return false
    }

    fun openAppSettings(activity: Activity) {
        try {
            activity.runOnUiThread {
                var intent: Intent? = null

                val packageName = activity.packageName

                when {
                    Build.VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD -> {
                        intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                        intent.data = Uri.parse("package:" + packageName)
                    }
                    Build.VERSION.SDK_INT == Build.VERSION_CODES.FROYO -> {
                        intent = Intent(Intent.ACTION_VIEW)
                        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                        intent.setClassName("com.android.settings", "com.android.settings.InstalledAppDetails")
                        intent.putExtra("pkg", packageName)
                    }
                    else -> {
                        intent = Intent(Intent.ACTION_VIEW)
                        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                        intent.setClassName("com.android.settings", "com.android.settings.InstalledAppDetails")
                        intent.putExtra("com.android.settings.ApplicationPkgName", packageName)
                    }
                }

                activity.startActivity(intent)
            }
        } catch (e: Exception) {
            Log.e(TAG, "openAppSettings", e)
        }

    }
}
