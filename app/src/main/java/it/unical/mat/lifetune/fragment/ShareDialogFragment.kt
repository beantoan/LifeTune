package it.unical.mat.lifetune.fragment

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import android.support.v4.app.DialogFragment
import it.unical.mat.lifetune.R


/**
 * Created by beantoan on 1/18/18.
 */
class ShareDialogFragment : DialogFragment() {

    var shareDialogCallbacks: ShareDialogCallbacks? = null

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val title = arguments!!.getInt("title")

        return AlertDialog.Builder(activity)
                .setIcon(R.drawable.ic_share_black_24dp)
                .setTitle(title)
                .setItems(R.array.share_dialog_options_array, { dialog, which ->
                    dialog.cancel()

                    shareDialogCallbacks?.onOptionItemClicked(which)
                })
                .setNegativeButton(R.string.share_dialog_cancel_button_title, { dialog, id ->
                    dialog.cancel()
                })

                .create()
    }

    companion object {

        val TAG: String = ShareDialogFragment::class.java.simpleName

        val FACEBOOK_OPTION = 0
        val OTHERS_OPTION = 1

        fun newInstance(title: Int): ShareDialogFragment {
            val frag = ShareDialogFragment()
            val args = Bundle()
            args.putInt("title", title)
            frag.arguments = args
            return frag
        }
    }

    interface ShareDialogCallbacks {
        fun onOptionItemClicked(option: Int)
        fun onNegativeButtonClicked()
    }
}