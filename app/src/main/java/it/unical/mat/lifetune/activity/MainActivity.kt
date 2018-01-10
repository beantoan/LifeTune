package it.unical.mat.lifetune.activity

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.support.design.widget.NavigationView
import android.support.v4.view.GravityCompat
import android.support.v4.widget.DrawerLayout
import android.support.v7.app.ActionBarDrawerToggle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.MenuItem
import com.beantoan.smsbackup.util.ActivityUtils
import com.firebase.ui.auth.IdpResponse
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.fitness.Fitness
import com.google.android.gms.fitness.FitnessOptions
import com.google.android.gms.fitness.data.DataType
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.crash.FirebaseCrash
import it.unical.mat.lifetune.R
import it.unical.mat.lifetune.entity.ActivityResultEvent
import it.unical.mat.lifetune.fragment.MyActivitiesFragment
import it.unical.mat.lifetune.fragment.PlayMusicFragment
import it.unical.mat.lifetune.fragment.SchedulesFragment
import it.unical.mat.lifetune.util.AppDialog
import it.unical.mat.lifetune.util.AppUtils
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.nav_header_main.view.*
import org.greenrobot.eventbus.EventBus
import pub.devrel.easypermissions.AfterPermissionGranted
import pub.devrel.easypermissions.EasyPermissions


class MainActivity :
        AppCompatActivity(),
        EasyPermissions.PermissionCallbacks,
        NavigationView.OnNavigationItemSelectedListener {

    private var playMusicFragment: PlayMusicFragment? = null

    private var myActivitiesFragment: MyActivitiesFragment? = null

    private var schedulesFragment: SchedulesFragment? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        Log.d(TAG, "onCreate")

        super.onCreate(savedInstanceState)

        onCreateTasks()
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        Log.d(TAG, "onNavigationItemSelected")

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
        Log.d(TAG, "onBackPressed")

        if (drawer_layout.isDrawerOpen(GravityCompat.START)) {
            drawer_layout.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        Log.d(TAG, "onActivityResult")

        super.onActivityResult(requestCode, resultCode, data)

        EventBus.getDefault().post(ActivityResultEvent(requestCode, resultCode, data))

        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == FITNESS_OAUTH_REQUEST_CODE) {
                subscribeFitness()
            }
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>,
                                            grantResults: IntArray) {
        Log.d(TAG, "onRequestPermissionsResult")
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults,
                this@MainActivity)
    }

    override fun onPermissionsDenied(requestCode: Int, perms: MutableList<String>?) {
        Log.d(TAG, "onPermissionsDenied")

        AppDialog.warning(R.string.request_access_fine_location_error_title,
                R.string.request_access_fine_location_error_message,
                this@MainActivity, DialogInterface.OnDismissListener {
            AppUtils.openAppSettings(this)
        })
    }

    override fun onPermissionsGranted(requestCode: Int, perms: MutableList<String>?) {
        Log.d(TAG, "onPermissionsGranted")
    }

    @AfterPermissionGranted(ACCESS_FINE_LOCATION_REQUEST_CODE)
    private fun checkFineLocationPermission() {
        Log.d(TAG, "onPermissionsGranted")

        if (!EasyPermissions.hasPermissions(this, Manifest.permission.ACCESS_FINE_LOCATION)) {
            EasyPermissions.requestPermissions(this, getString(R.string.request_access_fine_location),
                    ACCESS_FINE_LOCATION_REQUEST_CODE, Manifest.permission.ACCESS_FINE_LOCATION)
        }
    }

    private fun onCreateTasks() {
        Log.d(TAG, "onCreateTasks")

        setContentView(R.layout.activity_main)

        showUserInfo()

        setupNavigationDrawer()

        checkFineLocationPermission()

        checkFitnessPermissions()
    }

    private fun setupNavigationDrawer() {
        Log.d(TAG, "setupNavigationDrawer")

        val toggle = ActionBarDrawerToggle(
                this, drawer_layout, R.string.navigation_drawer_open, R.string.navigation_drawer_close)
        drawer_layout.addDrawerListener(toggle)
        toggle.syncState()

        nav_view.setNavigationItemSelectedListener(this)

//        nav_view.setCheckedItem(R.id.nav_play_music)
//        nav_view.menu.performIdentifierAction(R.id.nav_play_music, 0)

        nav_view.setCheckedItem(R.id.nav_my_activities)
        nav_view.menu.performIdentifierAction(R.id.nav_my_activities, 0)
    }

    private fun showUserInfo() {
        Log.d(TAG, "showUserInfo")

        val header = nav_view.getHeaderView(0)

        val authUser = FirebaseAuth.getInstance().currentUser!!

        header.user_fullname.text = authUser.displayName
        header.user_email.text = authUser.email ?: authUser.phoneNumber
    }

    fun getDrawerLayout(): DrawerLayout? {
        return drawer_layout
    }

    private fun showPlayMusicFragment() {
        Log.d(TAG, "showPlayMusicFragment")

        if (playMusicFragment == null) {
            playMusicFragment = PlayMusicFragment()
        }

        ActivityUtils.addOrAttachFragment(supportFragmentManager,
                playMusicFragment!!, R.id.content_main_placeholder, PlayMusicFragment.TAG)
    }

    private fun showMyActivitiesFragment() {
        Log.d(TAG, "showMyActivitiesFragment")

        if (myActivitiesFragment == null) {
            myActivitiesFragment = MyActivitiesFragment()
        }

        ActivityUtils.addOrAttachFragment(supportFragmentManager,
                myActivitiesFragment!!, R.id.content_main_placeholder, MyActivitiesFragment.TAG)
    }

    private fun showSchedulesFragment() {
        Log.d(TAG, "showSchedulesFragment")

        if (schedulesFragment == null) {
            schedulesFragment = SchedulesFragment()
        }

        ActivityUtils.addOrAttachFragment(supportFragmentManager,
                schedulesFragment!!, R.id.content_main_placeholder, SchedulesFragment.TAG)

    }

    private fun checkFitnessPermissions() {
        Log.d(TAG, "checkFitnessPermissions")

        val fitnessOptions = FitnessOptions.builder()
                .addDataType(DataType.TYPE_STEP_COUNT_CUMULATIVE)
                .addDataType(DataType.TYPE_STEP_COUNT_DELTA)
                .addDataType(DataType.TYPE_STEP_COUNT_CADENCE)
                .addDataType(DataType.TYPE_ACTIVITY_SEGMENT)
                .addDataType(DataType.AGGREGATE_ACTIVITY_SUMMARY)
                .addDataType(DataType.AGGREGATE_STEP_COUNT_DELTA)
                .addDataType(DataType.AGGREGATE_CALORIES_EXPENDED)
                .addDataType(DataType.AGGREGATE_DISTANCE_DELTA)
                .addDataType(DataType.TYPE_DISTANCE_DELTA)
                .addDataType(DataType.TYPE_CALORIES_EXPENDED)
                .build()

        if (!GoogleSignIn.hasPermissions(GoogleSignIn.getLastSignedInAccount(this), fitnessOptions)) {
            GoogleSignIn.requestPermissions(
                    this,
                    FITNESS_OAUTH_REQUEST_CODE,
                    GoogleSignIn.getLastSignedInAccount(this),
                    fitnessOptions);
        } else {
            subscribeFitness()
        }
    }

    private fun subscribeFitness() {
        Log.d(TAG, "subscribeFitness")

        val account = GoogleSignIn.getLastSignedInAccount(this)

        Fitness.getRecordingClient(this, account)
                .subscribe(DataType.TYPE_STEP_COUNT_CUMULATIVE)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        Log.d(TAG, "Fitness.getRecordingClient#addOnCompleteListener: Successfully subscribed!")
                    } else {
                        FirebaseCrash.logcat(Log.ERROR, TAG, "Fitness.getRecordingClient#addOnCompleteListener:" + task.exception!!)
                        FirebaseCrash.report(task.exception)
                    }
                }

        Fitness.getRecordingClient(this, account)
                .subscribe(DataType.TYPE_STEP_COUNT_DELTA)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        Log.d(TAG, "Fitness.getRecordingClient#addOnCompleteListener: Successfully subscribed!")
                    } else {
                        FirebaseCrash.logcat(Log.ERROR, TAG, "Fitness.getRecordingClient#addOnCompleteListener:" + task.exception!!)
                        FirebaseCrash.report(task.exception)
                    }
                }

        Fitness.getRecordingClient(this, account)
                .subscribe(DataType.TYPE_DISTANCE_DELTA)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        Log.d(TAG, "Fitness.getRecordingClient#addOnCompleteListener: Successfully subscribed!")
                    } else {
                        FirebaseCrash.logcat(Log.ERROR, TAG, "Fitness.getRecordingClient#addOnCompleteListener:" + task.exception!!)
                        FirebaseCrash.report(task.exception)
                    }
                }

        Fitness.getRecordingClient(this, account)
                .subscribe(DataType.TYPE_CALORIES_EXPENDED)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        Log.d(TAG, "Fitness.getRecordingClient#addOnCompleteListener: Successfully subscribed!")
                    } else {
                        FirebaseCrash.logcat(Log.ERROR, TAG, "Fitness.getRecordingClient#addOnCompleteListener:" + task.exception!!)
                        FirebaseCrash.report(task.exception)
                    }
                }
    }

    companion object {

        private val TAG = MainActivity::class.java.simpleName

        const val ACCESS_FINE_LOCATION_REQUEST_CODE = 100
        const val FITNESS_OAUTH_REQUEST_CODE = 102

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
