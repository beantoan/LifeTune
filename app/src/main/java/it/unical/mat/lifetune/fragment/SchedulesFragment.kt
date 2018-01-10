package it.unical.mat.lifetune.fragment

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import it.unical.mat.lifetune.R

/**
 * Created by beantoan on 11/17/17.
 */

/**
 * A dummy fragment representing a section of the app, but that simply displays dummy text.
 */
class SchedulesFragment : Fragment() {
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        super.onCreateView(inflater, container, savedInstanceState)

        val rootView = inflater.inflate(R.layout.fragment_schedules, container, false)

        return rootView
    }

    companion object {
        val TAG = SchedulesFragment::class.java.simpleName
    }
}