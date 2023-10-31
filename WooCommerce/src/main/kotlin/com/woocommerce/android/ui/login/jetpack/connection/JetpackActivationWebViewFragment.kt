package com.woocommerce.android.ui.login.jetpack.connection

import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.fragment.app.viewModels
import com.woocommerce.android.ui.base.BaseFragment
import com.woocommerce.android.ui.common.wpcomwebview.WPComWebViewAuthenticator
import com.woocommerce.android.ui.main.AppBarStatus
import dagger.hilt.android.AndroidEntryPoint
import org.wordpress.android.fluxc.network.UserAgent
import javax.inject.Inject

@AndroidEntryPoint
class JetpackActivationWebViewFragment : BaseFragment() {
    override val activityAppBarStatus: AppBarStatus
        get() = AppBarStatus.Hidden

    private val viewModel: JetpackActivationWebViewViewModel by viewModels()

    @Inject
    lateinit var wpComAuthenticator: WPComWebViewAuthenticator
    @Inject
    lateinit var userAgent: UserAgent

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?) =
        ComposeView(requireContext()).apply {
            setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)

            setContent {
                JetpackActivationWebViewScreen(
                    viewModel = viewModel,
                    wpComAuthenticator = wpComAuthenticator,
                    userAgent = userAgent,
                    onUrlLoaded = viewModel::onUrlLoaded,
                    onDismiss = viewModel::onDismiss
                )
            }
        }
}
