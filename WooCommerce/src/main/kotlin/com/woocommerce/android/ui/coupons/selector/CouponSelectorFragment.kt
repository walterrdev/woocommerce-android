package com.woocommerce.android.ui.coupons.selector

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.woocommerce.android.R
import com.woocommerce.android.extensions.navigateBackWithResult
import com.woocommerce.android.ui.base.BaseFragment
import com.woocommerce.android.ui.compose.theme.WooThemeWithBackground
import com.woocommerce.android.viewmodel.MultiLiveEvent
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class CouponSelectorFragment : BaseFragment() {
    private val args: CouponSelectorFragmentArgs by navArgs()
    private val viewModel by viewModels<CouponSelectorViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return ComposeView(requireContext()).apply {
            setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)
            setContent {
                WooThemeWithBackground {
                    CouponSelectorScreen(viewModel)
                }
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        viewModel.event.observe(viewLifecycleOwner){
            when(it) {
                is MultiLiveEvent.Event.Exit -> {
                    findNavController().navigateUp()
                }
                is NavigateBackToOrderCreationEvent -> {
                    val action = CouponSelectorFragmentDirections.actionCouponSelectorFragmentToOrderCreationFragment(
                        mode = args.orderCreationMode,
                        sku = null,
                        barcodeFormat = null,
                    )
                    findNavController().navigate(action)
                }
            }
        }
    }
}
