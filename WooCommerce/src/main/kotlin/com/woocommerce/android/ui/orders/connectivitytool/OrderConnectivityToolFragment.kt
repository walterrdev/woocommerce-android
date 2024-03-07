package com.woocommerce.android.ui.orders.connectivitytool

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.fragment.app.viewModels
import com.woocommerce.android.R
import com.woocommerce.android.support.help.HelpOrigin
import com.woocommerce.android.support.requests.SupportRequestFormActivity
import com.woocommerce.android.ui.base.BaseFragment
import com.woocommerce.android.ui.orders.connectivitytool.OrderConnectivityToolViewModel.OpenSupportRequest
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class OrderConnectivityToolFragment : BaseFragment() {
    val viewModel: OrderConnectivityToolViewModel by viewModels()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        return ComposeView(requireContext()).apply {
            setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)
            setContent {
                OrderConnectivityToolScreen(viewModel = viewModel)
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.event.observe(viewLifecycleOwner) {
            when (it) {
                is OpenSupportRequest -> openSupportRequestScreen()
            }
        }
        viewModel.startConnectionTests()
    }

    override fun getFragmentTitle() = getString(R.string.orderlist_connectivity_tool_title)

    private fun openSupportRequestScreen() {
        SupportRequestFormActivity.createIntent(
            context = requireContext(),
            origin = HelpOrigin.ORDERS_LIST,
            extraTags = ArrayList()
        ).let { activity?.startActivity(it) }
    }
}
