package it.unical.mat.lifetune.fragment

import android.support.v4.app.Fragment
import android.view.View
import android.widget.ProgressBar

/**
 * Created by beantoan on 1/16/18.
 */
abstract class BaseFragment : Fragment() {

    protected fun displayProgressBar(progressBar: ProgressBar, isShown: Boolean) {
        progressBar.visibility = if (isShown) View.VISIBLE else View.GONE
    }

    protected fun showProgressBar(progressBar: ProgressBar) = displayProgressBar(progressBar, true)

    protected fun hideProgressBar(progressBar: ProgressBar) = displayProgressBar(progressBar, false)
}