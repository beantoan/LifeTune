package it.unical.mat.lifetune.activity

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.design.widget.NavigationView
import android.support.v4.view.GravityCompat
import android.support.v4.widget.DrawerLayout
import android.support.v7.app.ActionBarDrawerToggle
import android.support.v7.app.AppCompatActivity
import android.view.MenuItem
import com.beantoan.smsbackup.util.ActivityUtils
import com.firebase.ui.auth.IdpResponse
import it.unical.mat.lifetune.R
import it.unical.mat.lifetune.fragment.MyActivitiesFragment
import it.unical.mat.lifetune.fragment.PlayMusicFragment
import it.unical.mat.lifetune.fragment.SchedulesFragment
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity :
        AppCompatActivity(),
        NavigationView.OnNavigationItemSelectedListener {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        onCreateTasks()
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.nav_play_music -> {
                ActivityUtils.replaceFragmentToPlaceholder(supportFragmentManager,
                        PlayMusicFragment(), R.id.content_main_placeholder, PlayMusicFragment.TAG)
            }
            R.id.nav_my_activities -> {
                ActivityUtils.replaceFragmentToPlaceholder(supportFragmentManager,
                        MyActivitiesFragment(), R.id.content_main_placeholder, MyActivitiesFragment.TAG)
            }
            R.id.nav_schedules -> {
                ActivityUtils.replaceFragmentToPlaceholder(supportFragmentManager,
                        SchedulesFragment(), R.id.content_main_placeholder, SchedulesFragment.TAG)
            }
        }

        drawer_layout.closeDrawer(GravityCompat.START)

        return true
    }

    override fun onBackPressed() {
        if (drawer_layout.isDrawerOpen(GravityCompat.START)) {
            drawer_layout.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }
    
    private fun onCreateTasks() {
        setContentView(R.layout.activity_main)

        setupNavigationDrawer()

        ActivityUtils.replaceFragmentToPlaceholder(supportFragmentManager,
                PlayMusicFragment(), R.id.content_main_placeholder, PlayMusicFragment.TAG)
    }

    private fun setupNavigationDrawer() {
        val toggle = ActionBarDrawerToggle(
                this, drawer_layout, R.string.navigation_drawer_open, R.string.navigation_drawer_close)
        drawer_layout.addDrawerListener(toggle)
        toggle.syncState()

        nav_view.setNavigationItemSelectedListener(this)
    }

    fun getDrawerLayout(): DrawerLayout? {
        return drawer_layout
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
