package it.unical.mat.lifetune.fragment

import android.support.v4.app.Fragment
import io.reactivex.disposables.CompositeDisposable
import it.unical.mat.lifetune.entity.Playlist
import it.unical.mat.lifetune.entity.Song

/**
 * Created by beantoan on 12/14/17.
 */
abstract class BaseMusicFragment : Fragment() {

    private var mCompositeDisposable: CompositeDisposable? = null

    protected lateinit var playMusicFragment: PlayMusicFragment

    protected var currentPlaylist: Playlist? = null

    override fun onDestroy() {
        mCompositeDisposable!!.clear()

        super.onDestroy()
    }

    protected fun getCompositeDisposable(): CompositeDisposable {
        if (mCompositeDisposable == null) {
            mCompositeDisposable = CompositeDisposable()
        }

        return mCompositeDisposable!!
    }

    protected abstract fun displayLoading(isShown: Boolean)

    protected fun showLoading() {
        displayLoading(true)
    }

    protected fun hideLoading() {
        displayLoading(false)
    }

    protected fun playSongs(songs: List<Song>) {
        this.playMusicFragment.playSongs(songs)
        hideLoading()
    }

    protected fun determineDisplayMusicPlayer() {
        if (currentPlaylist == null) {
            this.playMusicFragment.hideMusicPlayer()
        } else {
            this.playMusicFragment.showMusicPlayer()
        }
    }

    companion object {
        private val TAG = BaseMusicFragment::class.java.canonicalName
    }
}