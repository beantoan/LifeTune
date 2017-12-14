package it.unical.mat.lifetune.fragment

import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.LinearLayoutManager
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import it.unical.mat.lifetune.R
import it.unical.mat.lifetune.controller.MusicController
import it.unical.mat.lifetune.decoration.CategoryDividerItemDecoration
import it.unical.mat.lifetune.entity.Category
import kotlinx.android.synthetic.main.fragment_favorite_music.*


/**
 * Created by beantoan on 11/17/17.
 */
class FavoriteMusicFragment : BaseMusicFragment(), MusicController.AdapterCallbacks {

    lateinit var musicController: MusicController

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_favorite_music, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        onCreateViewTasks(view)
    }

    override fun onPlaylistClicked(category: Category?, position: Int) {

    }

    private fun onCreateViewTasks(view: View) {
        Log.d(TAG, "onCreateViewTasks")

        setupRecyclerViewCategories()

        setupMusicController()
    }

    private fun setupRecyclerViewCategories() {
        Log.d(TAG, "setupRecyclerViewCategories")

        val dividerDrawable = ContextCompat.getDrawable(activity!!.applicationContext, R.drawable.category_divider)
        val dividerItemDecoration = CategoryDividerItemDecoration(activity!!.applicationContext, DividerItemDecoration.VERTICAL, dividerDrawable!!)

        recycler_view_categories.layoutManager = LinearLayoutManager(activity!!.applicationContext)
        recycler_view_categories.addItemDecoration(dividerItemDecoration)
    }

    private fun setupMusicController() {
        Log.d(TAG, "setupMusicController")
        musicController = MusicController(this)

        recycler_view_categories.clear()
        recycler_view_categories.setController(musicController)
    }

    private fun updateMusicController(data: List<Category>) {
        musicController.setData(data)
    }

    companion object {
        private val TAG = FavoriteMusicFragment::class.java.canonicalName

        fun newInstance(playMusicFragment: PlayMusicFragment): FavoriteMusicFragment {
            val favoriteMusicFragment = FavoriteMusicFragment()

            favoriteMusicFragment.playMusicFragment = playMusicFragment

            return favoriteMusicFragment
        }
    }
}
