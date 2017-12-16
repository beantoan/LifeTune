package it.unical.mat.lifetune.fragment

import android.support.v4.app.Fragment
import io.reactivex.disposables.CompositeDisposable

/**
 * Created by beantoan on 12/14/17.
 */
abstract class BaseMusicFragment : Fragment() {

    private var mCompositeDisposable: CompositeDisposable? = null

    internal lateinit var playMusicFragment: PlayMusicFragment

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

    companion object {
        private val TAG = BaseMusicFragment::class.java.canonicalName
    }
}