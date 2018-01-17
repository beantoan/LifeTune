package it.unical.mat.lifetune.adapter

import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentPagerAdapter
import it.unical.mat.lifetune.fragment.BaseMusicFragment
import it.unical.mat.lifetune.fragment.FavoriteMusicFragment
import it.unical.mat.lifetune.fragment.PlayMusicFragment
import it.unical.mat.lifetune.fragment.RecommendedMusicFragment

/**
 * Created by beantoan on 11/17/17.
 */
class PlayMusicPagerAdapter(playMusicFragment: PlayMusicFragment, fragmentManager: FragmentManager) :
        FragmentPagerAdapter(fragmentManager) {

    val recommendationMusicFragment = RecommendedMusicFragment.newInstance(playMusicFragment)
    val favoriteMusicFragment = FavoriteMusicFragment.newInstance(playMusicFragment)

    override fun getItem(i: Int): Fragment = when (i) {
        RECOMMENDATION_MUSIC_FRAGMENT -> recommendationMusicFragment
        else -> favoriteMusicFragment
    }

    override fun getCount(): Int = TAB_COUNT

    override fun getPageTitle(position: Int): CharSequence = when (position) {
        RECOMMENDATION_MUSIC_FRAGMENT -> "Recommendation"
        else -> "Favourite"
    }

    fun getMusicFragment(position: Int): BaseMusicFragment {
        return getItem(position) as BaseMusicFragment
    }

    companion object {
        val TAG = PlayMusicPagerAdapter::class.java.simpleName
        private val TAB_COUNT = 2

        val RECOMMENDATION_MUSIC_FRAGMENT = 0
        val FAVOURITE_MUSIC_FRAGMENT = 1
    }
}