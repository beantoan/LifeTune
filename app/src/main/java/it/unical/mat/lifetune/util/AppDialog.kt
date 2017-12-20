package it.unical.mat.lifetune.util

import android.app.Activity
import android.app.ProgressDialog
import android.content.Context
import android.util.Log
import cn.pedant.SweetAlert.SweetAlertDialog
import it.unical.mat.lifetune.R

/**
 * Created by beantoan on 8/27/16.
 */
object AppDialog {
    private val TAG = "AppDialog"

    private var alertDialog: SweetAlertDialog? = null
    private var progressDialog: ProgressDialog? = null

    fun hideAlert(activity: Activity) {
        if (AppDialog.alertDialog != null) {
            activity.runOnUiThread {
                try {
                    AppDialog.alertDialog!!.dismiss()
                } catch (e: Exception) {
                    Log.e(TAG, "hideAlert", e)
                }
            }
        }
    }

    fun showAlert(titleId: Int, messageId: Int, dialogType: Int, activity: Activity) {
        AppDialog.hideAlert(activity)

        activity.runOnUiThread {
            try {
                AppDialog.alertDialog = SweetAlertDialog(activity, dialogType)
                        .setTitleText(activity.getString(titleId))
                        .setContentText(activity.getString(messageId))

                AppDialog.alertDialog!!.show()
            } catch (e: Exception) {
                Log.e(TAG, "showAlert", e)
            }
        }
    }

    fun error(titleId: Int, messageId: Int, activity: Activity) {
        AppDialog.showAlert(titleId, messageId, SweetAlertDialog.ERROR_TYPE, activity)
    }

    fun warning(titleId: Int, messageId: Int, activity: Activity) {
        AppDialog.showAlert(titleId, messageId, SweetAlertDialog.WARNING_TYPE, activity)
    }

    fun success(titleId: Int, messageId: Int, activity: Activity) {
        AppDialog.showAlert(titleId, messageId, SweetAlertDialog.SUCCESS_TYPE, activity)
    }

    private fun initProgressDialog(context: Context) {
        AppDialog.progressDialog = ProgressDialog(context)
        AppDialog.progressDialog!!.setCancelable(false)
        AppDialog.progressDialog!!.setCanceledOnTouchOutside(false)
        AppDialog.progressDialog!!.setMessage(context.getString(R.string.progress_dialog_waiting_message))
    }

    fun hideProgress(context: Context) {
        if (AppDialog.progressDialog != null) {
            try {
                AppDialog.progressDialog!!.dismiss()

            } catch (e: Exception) {
                Log.e(TAG, "hideProgress", e)
            }
        }
    }

    fun showProgress(messageId: Int?, context: Context) {
        AppDialog.initProgressDialog(context)

        AppDialog.hideProgress(context)

        if (AppDialog.progressDialog != null) {
            if (messageId != null) {
                AppDialog.progressDialog!!.setMessage(context.getString(messageId))
            }

            try {
                AppDialog.progressDialog!!.show()

            } catch (e: Exception) {
                Log.e(TAG, "showProgress", e)
            }
        }
    }
}
