package com.woocommerce.android.ui.login.overrides

import android.content.Context
import android.os.Bundle
import android.view.ViewGroup
import android.widget.Button
import androidx.annotation.LayoutRes
import com.bumptech.glide.Registry.MissingComponentException
import com.woocommerce.android.R
import com.woocommerce.android.analytics.AnalyticsEvent.LOGIN_MAGIC_LINK_OPEN_EMAIL_CLIENT_CLICKED
import com.woocommerce.android.analytics.AnalyticsTrackerWrapper
import dagger.android.support.AndroidSupportInjection
import org.wordpress.android.login.LoginEmailPasswordFragment
import org.wordpress.android.login.LoginListener
import javax.inject.Inject

class WooLoginEmailPasswordFragment : LoginEmailPasswordFragment() {
    companion object {
        const val TAG = "woo_login_email_password_fragment_tag"

        private const val ARG_EMAIL_ADDRESS = "ARG_EMAIL_ADDRESS"
        private const val ARG_SOCIAL_LOGIN = "ARG_SOCIAL_LOGIN"
        private const val ARG_ALLOW_MAGIC_LINK = "ARG_ALLOW_MAGIC_LINK"
        private const val ARG_VERIFY_MAGIC_LINK_EMAIL = "ARG_VERIFY_MAGIC_LINK_EMAIL"

        fun newInstance(
            emailAddress: String?,
            verifyEmail: Boolean
        ): WooLoginEmailPasswordFragment {
            val fragment = WooLoginEmailPasswordFragment()
            val args = Bundle()
            args.putString(ARG_EMAIL_ADDRESS, emailAddress)
            args.putBoolean(ARG_SOCIAL_LOGIN, false)
            args.putBoolean(ARG_ALLOW_MAGIC_LINK, false)
            args.putBoolean(ARG_VERIFY_MAGIC_LINK_EMAIL, verifyEmail)
            fragment.arguments = args
            return fragment
        }
    }

    @Inject lateinit var analyticsTrackerWrapper: AnalyticsTrackerWrapper
    private var loginListener: LoginListener? = null

    override fun onAttach(context: Context) {
        AndroidSupportInjection.inject(this)
        super.onAttach(context)
        loginListener = if (context is LoginListener) {
            context
        } else {
            throw MissingComponentException("$context must implement LoginListener")
        }
    }

    override fun onDetach() {
        super.onDetach()
        loginListener = null
    }

    @LayoutRes
    override fun getContentLayout(): Int {
        return R.layout.fragment_login_email_password
    }

    override fun setupContent(rootView: ViewGroup?) {
        super.setupContent(rootView)

        rootView?.findViewById<Button>(R.id.button_login_open_email_client)?.setOnClickListener {
            analyticsTrackerWrapper.track(LOGIN_MAGIC_LINK_OPEN_EMAIL_CLIENT_CLICKED)
            loginListener?.openEmailClient(true)
        }
    }
}
