package it.unical.mat.lifetune.util

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.net.ConnectivityManager
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.Settings
import android.util.Log
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import it.unical.mat.lifetune.BuildConfig
import java.io.File
import java.io.FileOutputStream
import java.util.*

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

    fun getPhotosDir(): String = Environment.getExternalStorageDirectory().absolutePath + "/${BuildConfig.APPLICATION_ID}"

    fun createPhotosDir(photosDirPath: String) {
        if (!File(photosDirPath).mkdirs()) {
            Log.e(TAG, "Directory not created")
        }
    }

    fun clearPhotosDir(photosDirPath: String) {
        val dir = File(photosDirPath)

        if (dir.exists()) {
            dir.delete()
        }
    }

    fun savePlacePhoto(photosDirPath: String, bitmap: Bitmap): File? {

        val random = Random()
        val filename = random.nextInt(999999).toString() + ".png"
        val imgFile = File(photosDirPath, filename)

        try {
            val out = FileOutputStream(imgFile)
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, out)
            out.flush()
            out.close()

            return imgFile
        } catch (e: Exception) {

        }

        return null
    }
}
