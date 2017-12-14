package it.unical.mat.lifetune.fragment

import android.support.v4.app.Fragment
import io.reactivex.disposables.CompositeDisposable

/**
 * Created by beantoan on 12/14/17.
 */
abstract class BaseMusicFragment : Fragment() {

    internal val mCompositeDisposable = CompositeDisposable()

    internal lateinit var playMusicFragment: PlayMusicFragment

    companion object {
        private val TAG = BaseMusicFragment::class.java.canonicalName
    }
}