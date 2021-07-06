package com.woocommerce.android.ui.prefs.cardreader.detail

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import com.woocommerce.android.R
import com.woocommerce.android.analytics.AnalyticsTracker
import com.woocommerce.android.databinding.DialogCardReaderTutorialBinding
import com.woocommerce.android.databinding.DialogCardReaderUpdateBinding
import com.woocommerce.android.extensions.navigateBackWithResult
import com.woocommerce.android.ui.prefs.cardreader.update.CardReaderUpdateViewModel.UpdateResult
import com.woocommerce.android.util.UiHelpers
import com.woocommerce.android.viewmodel.MultiLiveEvent.Event.ExitWithResult
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class CardReaderTutorialDialogFragment : DialogFragment(R.layout.dialog_card_reader_tutorial) {
    val viewModel: CardReaderDetailViewModel by viewModels()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        dialog!!.setCanceledOnTouchOutside(false)
        dialog!!.requestWindowFeature(Window.FEATURE_NO_TITLE)
        return super.onCreateView(inflater, container, savedInstanceState)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val binding = DialogCardReaderTutorialBinding.bind(view)

        initObservers(binding)
    }

    private fun initObservers(binding: DialogCardReaderUpdateBinding) {
        viewModel.event.observe(
            viewLifecycleOwner,
            { event ->
                when (event) {
                    is ExitWithResult<*> -> navigateBackWithResult(
                        KEY_READER_UPDATE_RESULT,
                        event.data as UpdateResult
                    )
                    else -> event.isHandled = false
                }
            }
        )

        viewModel.viewStateData.observe(
            viewLifecycleOwner,
            { state ->
                with(binding) {
                    UiHelpers.setTextOrHide(updateReaderTitleTv, state.title)
                    UiHelpers.setTextOrHide(updateReaderDescriptionTv, state.description)
                    UiHelpers.updateVisibility(updateReaderProgressGroup, state.showProgress)
                    UiHelpers.setTextOrHide(updateReaderPrimaryActionBtn, state.primaryButton?.text)
                    updateReaderPrimaryActionBtn.setOnClickListener { state.primaryButton?.onActionClicked?.invoke() }
                    UiHelpers.setTextOrHide(updateReaderSecondaryActionBtn, state.secondaryButton?.text)
                    updateReaderSecondaryActionBtn.setOnClickListener {
                        state.secondaryButton?.onActionClicked?.invoke()
                    }
                }
            }
        )
    }

    override fun onResume() {
        super.onResume()
        AnalyticsTracker.trackViewShown(this)
    }
}
