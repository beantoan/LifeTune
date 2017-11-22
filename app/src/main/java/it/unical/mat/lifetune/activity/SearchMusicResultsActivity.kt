package it.unical.mat.lifetune.activity

import android.app.Activity
import android.app.SearchManager
import android.content.Intent
import android.os.Bundle
import android.util.Log


/**
 * Created by beantoan on 11/21/17.
 */
class SearchMusicResultsActivity : Activity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        Log.d(TAG, "onCreate")

        handleIntent(intent)
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)

        Log.d(TAG, "onNewIntent")

        handleIntent(intent!!)
    }

    private fun handleIntent(intent: Intent) {
        Log.d(TAG, "handleIntent: ${intent.action}")

        if (Intent.ACTION_SEARCH == intent.action) {
            val query = intent.getStringExtra(SearchManager.QUERY)
            //use the query to search your data somehow
        }
    }

    companion object {
        private val TAG = SearchMusicResultsActivity::class.java.canonicalName!!
    }
}