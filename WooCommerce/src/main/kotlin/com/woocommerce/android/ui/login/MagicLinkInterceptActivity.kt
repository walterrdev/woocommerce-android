@file:Suppress("DEPRECATION")

package com.woocommerce.android.ui.login

import android.app.ProgressDialog
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ScrollView
import android.widget.TextView
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import com.google.android.material.snackbar.BaseTransientBottomBar
import com.google.android.material.snackbar.Snackbar
import com.woocommerce.android.R
import com.woocommerce.android.analytics.AnalyticsEvent
import com.woocommerce.android.analytics.AnalyticsTracker
import com.woocommerce.android.ui.main.MainActivity
import dagger.hilt.android.AndroidEntryPoint
import org.wordpress.android.login.LoginAnalyticsListener
import org.wordpress.android.login.LoginMode
import javax.inject.Inject

@AndroidEntryPoint
class MagicLinkInterceptActivity : AppCompatActivity() {
    companion object {
        private const val TOKEN_PARAMETER = "token"
    }

    @Inject
    lateinit var loginAnalyticsListener: LoginAnalyticsListener

    private var progressDialog: ProgressDialog? = null

    private val viewModel: MagicLinkInterceptViewModel by viewModels()

    private var retryButton: Button? = null
    private var retryContainer: ScrollView? = null

    private val authToken by lazy {
        val uri = intent.data
        uri?.getQueryParameter(TOKEN_PARAMETER)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        loginAnalyticsListener.trackLoginMagicLinkOpened()

        setContentView(R.layout.login_magic_link_sent_screen)
        retryButton = findViewById(R.id.login_open_email_client)
        retryContainer = findViewById(R.id.login_magic_link_container)
        retryButton?.text = getString(R.string.retry)
        showRetryScreen(false)
        retryButton?.setOnClickListener {
            AnalyticsTracker.track(AnalyticsEvent.LOGIN_MAGIC_LINK_INTERCEPT_RETRY_TAPPED)
            viewModel.fetchAccountInfo()
        }

        findViewById<TextView>(R.id.login_enter_password).visibility = View.GONE

        initializeViewModel()
    }

    private fun initializeViewModel() {
        setupObservers()
        authToken?.let { viewModel.updateMagicLinkAuthToken(it) }
    }

    private fun setupObservers() {
        viewModel.isLoading.observe(this) {
            showProgressDialog(it)
        }

        viewModel.isAuthTokenUpdated.observe(this) { authTokenUpdated ->
            if (authTokenUpdated) {
                showSitePickerScreen()
            } else showLoginScreen()
        }

        viewModel.showSnackbarMessage.observe(this) { messageId ->
            Snackbar.make(
                findViewById(android.R.id.content),
                getString(messageId),
                BaseTransientBottomBar.LENGTH_LONG
            ).show()
        }

        viewModel.showRetryOption.observe(this) {
            showRetryScreen(it)
        }
    }

    @Suppress("DEPRECATION")
    private fun showProgressDialog(show: Boolean) {
        if (show) {
            hideProgressDialog()
            progressDialog = ProgressDialog.show(
                this, "", getString(R.string.login_magic_link_token_updating), true
            )
            progressDialog?.setCancelable(false)
        } else {
            hideProgressDialog()
        }
    }

    private fun hideProgressDialog() {
        progressDialog?.apply {
            if (isShowing) {
                cancel()
                progressDialog = null
            }
        }
    }

    private fun showSitePickerScreen() {
        val intent = Intent(this, MainActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }

    private fun showLoginScreen() {
        val intent = Intent(this, LoginActivity::class.java)
        LoginMode.WOO_LOGIN_MODE.putInto(intent)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }

    private fun showRetryScreen(show: Boolean) {
        retryButton?.isVisible = show
        retryContainer?.isVisible = show
    }
}
