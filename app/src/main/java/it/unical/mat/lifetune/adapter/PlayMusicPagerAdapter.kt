package it.unical.mat.lifetune.adapter

import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentPagerAdapter
import it.unical.mat.lifetune.fragment.FavoriteMusicFragment
import it.unical.mat.lifetune.fragment.PlayMusicFragment
import it.unical.mat.lifetune.fragment.RecommendedMusicFragment

/**
 * Created by beantoan on 11/17/17.
 */
class PlayMusicPagerAdapter(playMusicFragment: PlayMusicFragment, fragmentManager: FragmentManager) :
        FragmentPagerAdapter(fragmentManager) {

    private val recommendationMusicFragment = RecommendedMusicFragment.newInstance(playMusicFragment)
    private val favoriteMusicFragment = FavoriteMusicFragment.newInstance(playMusicFragment)

    override fun getItem(i: Int): Fragment = when (i) {
        0 -> recommendationMusicFragment
        else -> favoriteMusicFragment
    }

    override fun getCount(): Int = TAB_COUNT

    override fun getPageTitle(position: Int): CharSequence = when (position) {
        0 -> "Recommendation"
        else -> "Favourite"
    }

    companion object {
        val TAG = PlayMusicPagerAdapter::class.java.canonicalName
        private val TAB_COUNT = 2
    }
}