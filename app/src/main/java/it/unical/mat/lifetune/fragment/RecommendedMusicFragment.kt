package it.unical.mat.lifetune.fragment

import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.LinearLayoutManager
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import it.unical.mat.lifetune.R
import it.unical.mat.lifetune.controller.RecommendationMusicController
import it.unical.mat.lifetune.decoration.CategoryDividerItemDecoration
import it.unical.mat.lifetune.entity.Category
import it.unical.mat.lifetune.service.ApiServiceFactory
import it.unical.mat.lifetune.service.CategoryServiceInterface
import it.unical.mat.lifetune.util.AppUtils
import kotlinx.android.synthetic.main.fragment_recommended_music.*


/**
 * Created by beantoan on 11/17/17.
 */
class RecommendedMusicFragment : BaseMusicFragment(), RecommendationMusicController.AdapterCallbacks {
    lateinit var recommendationMusicController: RecommendationMusicController

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_recommended_music, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        onCreateViewTasks(view)
    }

    override fun onDestroy() {
        mCompositeDisposable.clear()

        super.onDestroy()
    }

    override fun onPlaylistClicked(category: Category?, position: Int) {

    }

    private fun onCreateViewTasks(view: View) {
        Log.d(TAG, "onCreateViewTasks")

        setupRecyclerViewCategories()

        setupMusicController()

        callCategoriesIndexService()
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
        recommendationMusicController = RecommendationMusicController(this)

        recycler_view_categories.clear()
        recycler_view_categories.setController(recommendationMusicController)
    }

    private fun updateMusicController(data: List<Category>) {
        recommendationMusicController.setData(data)
    }

    private fun callCategoriesIndexService() {
        if (AppUtils.isInternetConnected(activity!!.applicationContext)) {
            val categoryService = ApiServiceFactory.create(CategoryServiceInterface::class.java)

            mCompositeDisposable.add(categoryService.recommendation()
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe { categories -> showCategories(categories) }
            )
        }
    }

    private fun showCategories(categories: List<Category>) {
        updateMusicController(categories)

        this.playMusicFragment.showMusicPlayer()
    }

    companion object {
        private val TAG = RecommendedMusicFragment::class.java.canonicalName

        fun newInstance(playMusicFragment: PlayMusicFragment): RecommendedMusicFragment {
            val recommendedMusicFragment = RecommendedMusicFragment()

            recommendedMusicFragment.playMusicFragment = playMusicFragment

            return recommendedMusicFragment
        }
    }
}
