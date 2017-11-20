package it.unical.mat.lifetune.adapter

import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentPagerAdapter
import it.unical.mat.lifetune.fragment.ActivitiesSectionFragment
import it.unical.mat.lifetune.fragment.MusicSectionFragment
import it.unical.mat.lifetune.fragment.SchedulesSectionFragment

/**
 * Created by beantoan on 11/17/17.
 */
class AppSectionsPagerAdapter(fm: FragmentManager) : FragmentPagerAdapter(fm) {

    override fun getItem(i: Int): Fragment = when (i) {
        0 -> MusicSectionFragment()
        1 -> ActivitiesSectionFragment()
        else -> SchedulesSectionFragment()
    }

    override fun getCount(): Int = TAB_COUNT

    override fun getPageTitle(position: Int): CharSequence = when (position) {
        0 -> "Music"
        1 -> "Activities"
        else -> "Schedules"
    }

    companion object {
        private val TAG = AppSectionsPagerAdapter::class.java.canonicalName
        private val TAB_COUNT = 3
    }
}