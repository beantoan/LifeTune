package it.unical.mat.lifetune.util

import android.app.Activity
import android.app.DatePickerDialog
import android.content.DialogInterface
import android.util.Log
import android.widget.EditText
import cn.pedant.SweetAlert.SweetAlertDialog
import java.text.DateFormat
import java.util.*


/**
 * Created by beantoan on 8/27/16.
 */
object AppDialog {
    private val TAG = "AppDialog"

    private var alertDialog: SweetAlertDialog? = null

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
