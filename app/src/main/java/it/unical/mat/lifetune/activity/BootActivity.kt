package it.unical.mat.lifetune.activity

import android.app.Activity
import android.content.Intent
import android.support.annotation.MainThread
import android.support.annotation.StringRes
import android.support.design.widget.Snackbar
import android.support.v7.app.AppCompatActivity
import android.util.Log
import com.firebase.ui.auth.AuthUI
import com.firebase.ui.auth.AuthUI.IdpConfig
import com.firebase.ui.auth.ErrorCodes
import com.firebase.ui.auth.IdpResponse
import com.google.firebase.auth.FirebaseAuth
import it.unical.mat.lifetune.R
import java.util.*

class BootActivity : AppCompatActivity() {

    private val selectedProviders: List<IdpConfig>
        @MainThread
        get() {
            val selectedProviders = ArrayList<IdpConfig>()

            selectedProviders.add(IdpConfig.Builder(AuthUI.GOOGLE_PROVIDER).build())

            selectedProviders.add(IdpConfig.Builder(AuthUI.FACEBOOK_PROVIDER).build())

            selectedProviders.add(IdpConfig.Builder(AuthUI.TWITTER_PROVIDER).build())

            selectedProviders.add(IdpConfig.Builder(AuthUI.EMAIL_PROVIDER).build())

            selectedProviders.add(IdpConfig.Builder(AuthUI.PHONE_VERIFICATION_PROVIDER).build())

            return selectedProviders
        }

    public override fun onStart() {
        Log.d(TAG, "onStart")

        super.onStart()

        boot()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent) {
        Log.d(TAG, "onActivityResult")

        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == RC_SIGN_IN) {
            handleSignInResponse(resultCode, data)
            return
        }

        showSnackbar(R.string.unknown_response)
    }

    private fun boot() {
        Log.d(TAG, "boot")

        val auth = FirebaseAuth.getInstance()

        if (auth.currentUser != null) {
            showMainActivity(null)
        } else {
            showFireBaseAuthUI()
        }
    }

    @MainThread
    private fun handleSignInResponse(resultCode: Int, data: Intent) {
        Log.d(TAG, "handleSignInResponse")

        val response = IdpResponse.fromResultIntent(data)

        // Successfully signed in
        if (resultCode == Activity.RESULT_OK) {
            showMainActivity(response)
        } else {
            // Sign in failed

            showFireBaseAuthUI()

            if (response == null) {
                // User pressed back button
                showSnackbar(R.string.sign_in_cancelled)
                return
            }

            if (response.errorCode == ErrorCodes.NO_NETWORK) {
                showSnackbar(R.string.no_internet_connection)
                return
            }

            if (response.errorCode == ErrorCodes.UNKNOWN_ERROR) {
                showSnackbar(R.string.unknown_error)
                return
            }

            showSnackbar(R.string.unknown_sign_in_response)
        }
    }

    /**
     * Show FireBaseUI for authenticating
     */
    private fun showFireBaseAuthUI() {
        Log.d(TAG, "showFireBaseAuthUI")

        startActivityForResult(
                AuthUI.getInstance().createSignInIntentBuilder()
                        .setLogo(R.drawable.logo)
                        .setAvailableProviders(selectedProviders)
                        .setTosUrl(GOOGLE_TOS_URL)
                        .setPrivacyPolicyUrl(GOOGLE_PRIVACY_POLICY_URL)
                        .build(),
                RC_SIGN_IN)
    }

    /**
     * When logged in successfully
     * @param response
     */
    private fun showMainActivity(response: IdpResponse?) {
        Log.d(TAG, "showMainActivity")

        startActivity(MainActivity.createIntent(this, response))
    }

    @MainThread
    private fun showSnackbar(@StringRes errorMessageRes: Int) {
        Snackbar.make(window.decorView!!, errorMessageRes, Snackbar.LENGTH_LONG).show()
    }

    companion object {
        private val TAG = BootActivity::class.java.canonicalName

        private val GOOGLE_TOS_URL = "https://www.google.com/policies/terms/"
        private val GOOGLE_PRIVACY_POLICY_URL = "https://www.google.com/policies/privacy/"

        private val RC_SIGN_IN = 100
    }
}
