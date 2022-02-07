package com.woocommerce.android.ui.prefs.cardreader.hub

import androidx.lifecycle.SavedStateHandle
import com.woocommerce.android.AppUrls
import com.woocommerce.android.R
import com.woocommerce.android.model.UiString
import com.woocommerce.android.ui.prefs.cardreader.InPersonPaymentsCanadaFeatureFlag
import com.woocommerce.android.viewmodel.BaseUnitTest
import org.assertj.core.api.Assertions.assertThat
import org.junit.Before
import org.junit.Test
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever

class CardReaderHubViewModelTest : BaseUnitTest() {
    private lateinit var viewModel: CardReaderHubViewModel
    private val inPersonPaymentsCanadaFeatureFlag: InPersonPaymentsCanadaFeatureFlag = mock()

    @Before
    fun setUp() {
        initViewModel()
    }

    @Test
    fun `when screen shown, then manage card reader row present`() {
        assertThat((viewModel.viewStateData.value as CardReaderHubViewModel.CardReaderHubViewState.Content).rows)
            .anyMatch {
                it.label == UiString.UiStringRes(R.string.card_reader_manage_card_reader)
            }
    }

    @Test
    fun `when screen shown, then manage card reader row icon is present`() {
        assertThat((viewModel.viewStateData.value as CardReaderHubViewModel.CardReaderHubViewState.Content).rows)
            .anyMatch {
                it.icon == R.drawable.ic_manage_card_reader
            }
    }

    @Test
    fun `when screen shown, then purchase card reader row present`() {
        assertThat((viewModel.viewStateData.value as CardReaderHubViewModel.CardReaderHubViewState.Content).rows)
            .anyMatch {
                it.label == UiString.UiStringRes(R.string.card_reader_purchase_card_reader)
            }
    }

    @Test
    fun `when screen shown, then purchase card reader row icon is present`() {
        assertThat((viewModel.viewStateData.value as CardReaderHubViewModel.CardReaderHubViewState.Content).rows)
            .anyMatch {
                it.icon == R.drawable.ic_shopping_cart
            }
    }

    @Test
    fun `when screen shown, then bbpos manual card reader row present`() {
        assertThat((viewModel.viewStateData.value as CardReaderHubViewModel.CardReaderHubViewState.Content).rows)
            .anyMatch {
                it.label == UiString.UiStringRes(R.string.card_reader_bbpos_manual_card_reader)
            }
    }

    @Test
    fun `when screen shown, then manual card reader row icon is present`() {
        assertThat((viewModel.viewStateData.value as CardReaderHubViewModel.CardReaderHubViewState.Content).rows)
            .anyMatch {
                it.icon == R.drawable.ic_card_reader_manual
            }
    }

    @Test
    fun `when screen shown, then m2 manual card reader row present`() {
        assertThat((viewModel.viewStateData.value as CardReaderHubViewModel.CardReaderHubViewState.Content).rows)
            .anyMatch {
                it.label == UiString.UiStringRes(R.string.card_reader_m2_manual_card_reader) &&
                    it.icon == R.drawable.ic_card_reader_manual
            }
    }

    @Test
    fun `when screen shown, then bbpos chipper manual card reader row present`() {
        assertThat((viewModel.viewStateData.value as CardReaderHubViewModel.CardReaderHubViewState.Content).rows)
            .anyMatch {
                it.label == UiString.UiStringRes(R.string.card_reader_bbpos_manual_card_reader) &&
                    it.icon == R.drawable.ic_card_reader_manual
            }
    }

    @Test
    fun `given ipp canada enabled, when screen shown, then wisepad manual card reader row present`() {
        whenever(inPersonPaymentsCanadaFeatureFlag.isEnabled()).thenReturn(true)
        initViewModel()

        assertThat((viewModel.viewStateData.value as CardReaderHubViewModel.CardReaderHubViewState.Content).rows)
            .anyMatch {
                it.label == UiString.UiStringRes(R.string.card_reader_wisepad_3_manual_card_reader) &&
                    it.icon == R.drawable.ic_card_reader_manual
            }
    }

    @Test
    fun `given ipp canada disabled, when screen shown, then wisepad manual card reader row not present`() {
        whenever(inPersonPaymentsCanadaFeatureFlag.isEnabled()).thenReturn(false)
        initViewModel()

        assertThat((viewModel.viewStateData.value as CardReaderHubViewModel.CardReaderHubViewState.Content).rows)
            .noneMatch {
                it.label == UiString.UiStringRes(R.string.card_reader_wisepad_3_manual_card_reader) &&
                    it.icon == R.drawable.ic_card_reader_manual
            }
    }

    @Test
    fun `when screen shown, then bbpos manual card reader row present on third position`() {
        val rows = (viewModel.viewStateData.value as CardReaderHubViewModel.CardReaderHubViewState.Content).rows
        assertThat(rows[2].label).isEqualTo(UiString.UiStringRes(R.string.card_reader_bbpos_manual_card_reader))
    }

    @Test
    fun `when screen shown, then m2 manual card reader row present at fourth last`() {
        val rows = (viewModel.viewStateData.value as CardReaderHubViewModel.CardReaderHubViewState.Content).rows
        assertThat(rows[3].label).isEqualTo(UiString.UiStringRes(R.string.card_reader_m2_manual_card_reader))
    }

    @Test
    fun `given ipp canada enabled, when screen shown, then wisepade manual card reader row present at fourth last`() {
        whenever(inPersonPaymentsCanadaFeatureFlag.isEnabled()).thenReturn(true)
        initViewModel()

        val rows = (viewModel.viewStateData.value as CardReaderHubViewModel.CardReaderHubViewState.Content).rows
        assertThat(rows[4].label).isEqualTo(UiString.UiStringRes(R.string.card_reader_wisepad_3_manual_card_reader))
    }

    @Test
    fun `when user clicks on manage card reader, then app navigates to card reader detail screen`() {
        (viewModel.viewStateData.value as CardReaderHubViewModel.CardReaderHubViewState.Content).rows
            .find {
                it.label == UiString.UiStringRes(R.string.card_reader_manage_card_reader)
            }!!.onItemClicked.invoke()

        assertThat(viewModel.event.value)
            .isEqualTo(CardReaderHubViewModel.CardReaderHubEvents.NavigateToCardReaderDetail)
    }

    @Test
    fun `when user clicks on purchase card reader, then app opens external webview`() {
        (viewModel.viewStateData.value as CardReaderHubViewModel.CardReaderHubViewState.Content).rows
            .find {
                it.label == UiString.UiStringRes(R.string.card_reader_purchase_card_reader)
            }!!.onItemClicked.invoke()

        assertThat(viewModel.event.value)
            .isEqualTo(CardReaderHubViewModel.CardReaderHubEvents.NavigateToPurchaseCardReaderFlow)
    }

    @Test
    fun `when user clicks on purchase card reader, then app opens external webview with in-person-payments link`() {
        (viewModel.viewStateData.value as CardReaderHubViewModel.CardReaderHubViewState.Content).rows
            .find {
                it.label == UiString.UiStringRes(R.string.card_reader_purchase_card_reader)
            }!!.onItemClicked.invoke()

        assertThat(
            (viewModel.event.value as CardReaderHubViewModel.CardReaderHubEvents.NavigateToPurchaseCardReaderFlow).url
        ).isEqualTo(AppUrls.WOOCOMMERCE_PURCHASE_CARD_READER)
    }

    @Test
    fun `when user clicks on bbpos manual card reader, then app opens external webview with bbpos link`() {
        (viewModel.viewStateData.value as CardReaderHubViewModel.CardReaderHubViewState.Content).rows
            .find {
                it.label == UiString.UiStringRes(R.string.card_reader_bbpos_manual_card_reader)
            }!!.onItemClicked.invoke()

        assertThat(viewModel.event.value)
            .isEqualTo(
                CardReaderHubViewModel.CardReaderHubEvents.NavigateToManualCardReaderFlow(
                    AppUrls.BBPOS_MANUAL_CARD_READER
                )
            )
    }

    @Test
    fun `when user clicks on m2 manual card reader, then app opens external webview with m2 link`() {
        (viewModel.viewStateData.value as CardReaderHubViewModel.CardReaderHubViewState.Content).rows
            .find {
                it.label == UiString.UiStringRes(R.string.card_reader_m2_manual_card_reader)
            }!!.onItemClicked.invoke()

        assertThat(viewModel.event.value)
            .isEqualTo(
                CardReaderHubViewModel.CardReaderHubEvents.NavigateToManualCardReaderFlow(
                    AppUrls.M2_MANUAL_CARD_READER
                )
            )
    }

    @Test
    fun `given ipp canada enabled, when user clicks on wp3 manual card reader, then app opens webview with wp3 link`() {
        whenever(inPersonPaymentsCanadaFeatureFlag.isEnabled()).thenReturn(true)
        initViewModel()

        (viewModel.viewStateData.value as CardReaderHubViewModel.CardReaderHubViewState.Content).rows
            .find {
                it.label == UiString.UiStringRes(R.string.card_reader_wisepad_3_manual_card_reader)
            }!!.onItemClicked.invoke()

        assertThat(viewModel.event.value)
            .isEqualTo(
                CardReaderHubViewModel.CardReaderHubEvents.NavigateToManualCardReaderFlow(
                    AppUrls.WISEPAD_3_MANUAL_CARD_READER
                )
            )
    }

    private fun initViewModel() {
        viewModel = CardReaderHubViewModel(SavedStateHandle(), inPersonPaymentsCanadaFeatureFlag)
    }
}
