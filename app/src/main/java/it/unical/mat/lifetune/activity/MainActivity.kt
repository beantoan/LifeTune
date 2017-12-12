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

    private var playMusicFragment: PlayMusicFragment? = null

    private var myActivitiesFragment: MyActivitiesFragment? = null

    private var schedulesFragment: SchedulesFragment? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        onCreateTasks()
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.nav_play_music -> {
                showPlayMusicFragment()
            }
            R.id.nav_my_activities -> {
                showMyActivitiesFragment()
            }
            R.id.nav_schedules -> {
                showSchedulesFragment()
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

        nav_view.setCheckedItem(R.id.nav_play_music)
        nav_view.menu.performIdentifierAction(R.id.nav_play_music, 0)
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

    private fun showPlayMusicFragment() {
        if (playMusicFragment == null) {
            playMusicFragment = PlayMusicFragment()
        }

        ActivityUtils.addOrAttachFragment(supportFragmentManager,
                playMusicFragment!!, R.id.content_main_placeholder, PlayMusicFragment.TAG)
    }

    private fun showMyActivitiesFragment() {
        if (myActivitiesFragment == null) {
            myActivitiesFragment = MyActivitiesFragment()
        }

        ActivityUtils.addOrAttachFragment(supportFragmentManager,
                myActivitiesFragment!!, R.id.content_main_placeholder, MyActivitiesFragment.TAG)
    }

    private fun showSchedulesFragment() {
        if (schedulesFragment == null) {
            schedulesFragment = SchedulesFragment()
        }

        ActivityUtils.addOrAttachFragment(supportFragmentManager,
                schedulesFragment!!, R.id.content_main_placeholder, SchedulesFragment.TAG)

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
