package it.unical.mat.lifetune.fragment

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.LinearLayoutManager
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.gms.awareness.Awareness
import it.unical.mat.lifetune.R
import it.unical.mat.lifetune.controller.RecommendationMusicController
import it.unical.mat.lifetune.decoration.RecyclerViewDividerItemDecoration
import it.unical.mat.lifetune.entity.Category
import kotlinx.android.synthetic.main.fragment_recommended_music.*


/**
 * Created by beantoan on 11/17/17.
 */
class RecommendedMusicFragment : BaseMusicFragment() {

    private lateinit var controller: RecommendationMusicController

    var recommendationCategories: List<Category> = ArrayList()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_recommended_music, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        onCreateViewTasks(view)
    }

    override fun onRecommendationServiceSuccess(categories: List<Category>) {
        super.onRecommendationServiceSuccess(categories)

        recommendationCategories = categories

        controller.setData(recommendationCategories)
    }

    override fun onRecommendationServiceFailure(error: Throwable) {
        super.onRecommendationServiceFailure(error)

        recommendationCategories = ArrayList()

        controller.setData(recommendationCategories)
    }

    private fun onCreateViewTasks(view: View) {
        Log.d(TAG, "onCreateViewTasks")

        setupRecyclerViewCategories()

        setupMusicController()

        callRecommendationService()
    }

    private fun callSnapshotApi() {
        if (ContextCompat.checkSelfPermission(activity!!, Manifest.permission.ACCESS_FINE_LOCATION) ==
                PackageManager.PERMISSION_GRANTED) {

            Awareness.getSnapshotClient(activity!!).location
                    .addOnSuccessListener({ locationResponse ->
                        Log.d(TAG, "Awareness.getSnapshotClient#addOnSuccessListener")

                        callRecommendationService()
                    })
                    .addOnFailureListener({ e ->
                        Log.e(TAG, "Awareness.getSnapshotClient#addOnFailureListener", e)
                    })
        }
    }

    private fun setupRecyclerViewCategories() {
        Log.d(TAG, "setupRecyclerViewCategories")

        val dividerDrawable = ContextCompat.getDrawable(activity!!.applicationContext, R.drawable.category_divider)
        val dividerItemDecoration = RecyclerViewDividerItemDecoration(activity!!.applicationContext, DividerItemDecoration.VERTICAL, dividerDrawable!!)

        recycler_view_categories.layoutManager = LinearLayoutManager(activity!!.applicationContext)
        recycler_view_categories.addItemDecoration(dividerItemDecoration)
    }

    private fun setupMusicController() {
        Log.d(TAG, "setupMusicController")

        controller = RecommendationMusicController(this)

        recycler_view_categories.clear()
        recycler_view_categories.setController(controller)

        controller.setData(recommendationCategories)
    }

    private fun showCategories() {
        Log.d(TAG, "showCategories: " + recommendationCategories.size + " items")

        controller.setData(recommendationCategories)

        hideLoading()
    }

    companion object {
        private val TAG = RecommendedMusicFragment::class.java.canonicalName

        fun newInstance(playMusicFragment: PlayMusicFragment): RecommendedMusicFragment {
            val fragment = RecommendedMusicFragment()

            fragment.playMusicFragment = playMusicFragment

            return fragment
        }
    }
}
