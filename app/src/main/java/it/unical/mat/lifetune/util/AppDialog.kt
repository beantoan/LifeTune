package it.unical.mat.lifetune.util

import android.app.Activity
import android.app.DatePickerDialog
import android.app.ProgressDialog
import android.content.Context
import android.content.DialogInterface
import android.util.Log
import android.widget.EditText
import cn.pedant.SweetAlert.SweetAlertDialog
import it.unical.mat.lifetune.R
import java.text.DateFormat
import java.util.*


/**
 * Created by beantoan on 8/27/16.
 */
object AppDialog {
    private val TAG = "AppDialog"

    private var alertDialog: SweetAlertDialog? = null
    private var isShowingProgressDialog: Boolean = false
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

    fun showAlert(titleId: Int, messageId: Int, dialogType: Int, activity: Activity,
                  onDismissListener: DialogInterface.OnDismissListener?) {
        AppDialog.hideAlert(activity)

        if (!activity.isDestroyed) {
            activity.runOnUiThread {
                try {

                    AppDialog.alertDialog = SweetAlertDialog(activity, dialogType)
                            .setTitleText(activity.getString(titleId))
                            .setContentText(activity.getString(messageId))

                    AppDialog.alertDialog!!.setOnDismissListener(onDismissListener)

                    AppDialog.alertDialog!!.show()
                } catch (e: Exception) {
                    Log.e(TAG, "showAlert", e)
                }
            }
        }
    }

    fun error(titleId: Int, messageId: Int, activity: Activity,
              onDismissListener: DialogInterface.OnDismissListener?) {
        AppDialog.showAlert(titleId, messageId, SweetAlertDialog.ERROR_TYPE, activity, onDismissListener)
    }

    fun error(titleId: Int, messageId: Int, activity: Activity) {
        error(titleId, messageId, activity, null)
    }

    fun warning(titleId: Int, messageId: Int, activity: Activity,
                onDismissListener: DialogInterface.OnDismissListener?) {
        AppDialog.showAlert(titleId, messageId, SweetAlertDialog.WARNING_TYPE, activity, onDismissListener)
    }

    fun warning(titleId: Int, messageId: Int, activity: Activity) {
        warning(titleId, messageId, activity, null)
    }

    fun success(titleId: Int, messageId: Int, activity: Activity,
                onDismissListener: DialogInterface.OnDismissListener?) {
        AppDialog.showAlert(titleId, messageId, SweetAlertDialog.SUCCESS_TYPE, activity, onDismissListener)
    }

    fun success(titleId: Int, messageId: Int, activity: Activity) {
        success(titleId, messageId, activity, null)
    }

    private fun initProgressDialog(context: Context) {
        if (AppDialog.progressDialog == null) {
            AppDialog.progressDialog = ProgressDialog(context)
            AppDialog.progressDialog!!.setCancelable(true)
            AppDialog.progressDialog!!.setCanceledOnTouchOutside(true)
            AppDialog.progressDialog!!.setMessage(context.getString(R.string.progress_dialog_waiting_message))
        }
    }

    fun hideProgress(context: Context) {
        if (!isShowingProgressDialog) {
            return
        }
        
        if (AppDialog.progressDialog != null) {
            try {
                AppDialog.progressDialog!!.dismiss()

                isShowingProgressDialog = false

            } catch (e: Exception) {
                Log.e(TAG, "hideProgress", e)
            }
        }
    }

    fun showProgress(messageId: Int?, context: Context) {
        if (isShowingProgressDialog) {
            return
        }
        
        AppDialog.initProgressDialog(context)

        AppDialog.hideProgress(context)

        if (AppDialog.progressDialog != null) {
            if (messageId != null) {
                AppDialog.progressDialog!!.setMessage(context.getString(messageId))
            }

            try {
                AppDialog.progressDialog!!.show()

                isShowingProgressDialog = true

            } catch (e: Exception) {
                Log.e(TAG, "showProgress", e)
            }
        }
    }

    fun showDatePickerDialog(activity: Activity, mDate: EditText,
                             _year: Int, _month: Int, _day: Int,
                             isCancelable: Boolean = false, onDateSetListener: DateSetListener? = null) {
        val datePickerDialog = DatePickerDialog(activity, DatePickerDialog.OnDateSetListener { view, year, monthOfYear, dayOfMonth ->
            val cal = Calendar.getInstance()
            val dateFormat = DateFormat.getDateInstance(DateFormat.LONG)

            cal.set(year, monthOfYear, dayOfMonth)

            val date = dateFormat.format(cal.timeInMillis)

            mDate.setText(date)

            onDateSetListener?.onSetListener(year, monthOfYear, dayOfMonth)
        }, _year, _month, _day)

        datePickerDialog.setOnCancelListener {
            if (isCancelable) {
                mDate.text = null
            }
        }

        datePickerDialog.show()
    }

    interface DateSetListener {
        fun onSetListener(_year: Int, _month: Int, _day: Int)
    }
}
