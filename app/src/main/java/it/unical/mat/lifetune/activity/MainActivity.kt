package it.unical.mat.lifetune.activity

import android.app.ActionBar
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v4.app.FragmentTransaction
import android.support.v4.view.ViewPager
import android.support.v7.app.AppCompatActivity
import android.util.Log
import com.firebase.ui.auth.IdpResponse
import it.unical.mat.lifetune.R
import it.unical.mat.lifetune.adapter.AppSectionsPagerAdapter

class MainActivity : AppCompatActivity(), android.support.v7.app.ActionBar.TabListener {
    lateinit var mAppSectionsPagerAdapter: AppSectionsPagerAdapter

    lateinit var mViewPager: ViewPager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        // TODO: need to check again
//        setSupportActionBar(toolbar)

        boot()
    }

    private fun boot() {
        // Create the adapter that will return a fragment for each of the three primary sections
        // of the app.
        mAppSectionsPagerAdapter = AppSectionsPagerAdapter(supportFragmentManager)

        // Set up the action bar.
        val actionBar = supportActionBar

        // Specify that the Home/Up button should not be enabled, since there is no hierarchical
        // parent.
        actionBar!!.setHomeButtonEnabled(false)

        // Specify that we will be displaying tabs in the action bar.
        actionBar.navigationMode = ActionBar.NAVIGATION_MODE_TABS

        // Set up the ViewPager, attaching the adapter and setting up a listener for when the
        // user swipes between sections.
        mViewPager = findViewById(R.id.pager)
        mViewPager.adapter = mAppSectionsPagerAdapter
        mViewPager.addOnPageChangeListener(object : ViewPager.SimpleOnPageChangeListener() {
            override fun onPageSelected(position: Int) {
                // When swiping between different app sections, select the corresponding tab.
                // We can also use ActionBar.Tab#select() to do this if we have a reference to the
                // Tab.
                actionBar.setSelectedNavigationItem(position)
            }
        })

        // For each of the sections in the app, add a tab to the action bar.
        for (i in 0 until mAppSectionsPagerAdapter.count) {
            // Create a tab with text corresponding to the page title defined by the adapter.
            // Also specify this Activity object, which implements the TabListener interface, as the
            // listener for when this tab is selected.
            actionBar.addTab(
                    actionBar.newTab()
                            .setText(mAppSectionsPagerAdapter.getPageTitle(i))
                            .setTabListener(this))
        }
    }

    override fun onTabReselected(tab: android.support.v7.app.ActionBar.Tab?, ft: FragmentTransaction?) {
    }

    override fun onTabUnselected(tab: android.support.v7.app.ActionBar.Tab?, ft: FragmentTransaction?) {

    }

    override fun onTabSelected(tab: android.support.v7.app.ActionBar.Tab?, ft: FragmentTransaction?) {
        Log.d(TAG, tab!!.position.toString())
        mViewPager.currentItem = tab!!.position
    }

    companion object {

        private val TAG = MainActivity::class.java.canonicalName

        private val EXTRA_IDP_RESPONSE = "extra_idp_response"

        fun createIntent(context: Context, idpResponse: IdpResponse?): Intent {

            val startIntent = Intent()

            if (idpResponse != null) {
                startIntent.putExtra(EXTRA_IDP_RESPONSE, idpResponse)
            }

            return startIntent.setClass(context, MainActivity::class.java)
        }
    }
}
