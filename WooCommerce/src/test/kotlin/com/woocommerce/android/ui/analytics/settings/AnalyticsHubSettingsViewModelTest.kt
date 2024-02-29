package com.woocommerce.android.ui.analytics.settings

import androidx.lifecycle.SavedStateHandle
import com.woocommerce.android.model.AnalyticCardConfiguration
import com.woocommerce.android.ui.analytics.hub.ObserveAnalyticsCardsConfiguration
import com.woocommerce.android.ui.analytics.hub.settings.AnalyticCardConfigurationUI
import com.woocommerce.android.ui.analytics.hub.settings.AnalyticsHubSettingsViewModel
import com.woocommerce.android.ui.analytics.hub.settings.AnalyticsHubSettingsViewState
import com.woocommerce.android.ui.analytics.hub.settings.AnalyticsHubSettingsViewState.CardsConfiguration
import com.woocommerce.android.ui.analytics.hub.settings.SaveAnalyticsCardsConfiguration
import com.woocommerce.android.viewmodel.BaseUnitTest
import com.woocommerce.android.viewmodel.MultiLiveEvent
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.advanceTimeBy
import org.assertj.core.api.Assertions.assertThat
import org.junit.Assert.assertNotEquals
import org.junit.Before
import org.junit.Test
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import kotlin.test.assertEquals

@ExperimentalCoroutinesApi
class AnalyticsHubSettingsViewModelTest : BaseUnitTest() {
    private val observeAnalyticsCardsConfiguration: ObserveAnalyticsCardsConfiguration = mock()
    private val saveAnalyticsCardsConfiguration: SaveAnalyticsCardsConfiguration = mock()
    private val savedState: SavedStateHandle = SavedStateHandle()

    private lateinit var sut: AnalyticsHubSettingsViewModel

    private val defaultConfiguration = listOf(
        AnalyticCardConfiguration(1, "Revenue", true),
        AnalyticCardConfiguration(2, "Orders", true),
        AnalyticCardConfiguration(3, "Stats", false)
    )

    @Before
    fun setup() {
        sut = AnalyticsHubSettingsViewModel(
            observeAnalyticsCardsConfiguration = observeAnalyticsCardsConfiguration,
            saveAnalyticsCardsConfiguration = saveAnalyticsCardsConfiguration,
            savedState = savedState
        )
    }

    @Test
    fun `when back is pressed without changes then exit the screen`() = testBlocking {
        whenever(observeAnalyticsCardsConfiguration.invoke()).thenReturn(flowOf(defaultConfiguration))

        var event: MultiLiveEvent.Event? = null
        sut.event.observeForever { latestEvent -> event = latestEvent }

        var viewState: AnalyticsHubSettingsViewState? = null
        sut.viewStateData.observeForever { _, new -> viewState = new }

        advanceTimeBy(501)

        sut.onBackPressed()

        // The exit event is triggered
        assertEquals(MultiLiveEvent.Event.Exit, event)
        // The discard dialog is not displayed
        assertThat(viewState).isInstanceOf(CardsConfiguration::class.java)
        assertThat((viewState as CardsConfiguration).showDiscardDialog).isEqualTo(false)
    }

    @Test
    fun `when back is pressed with changes then display the discard dialog`() = testBlocking {
        whenever(observeAnalyticsCardsConfiguration.invoke()).thenReturn(flowOf(defaultConfiguration))

        var event: MultiLiveEvent.Event? = null
        sut.event.observeForever { latestEvent -> event = latestEvent }

        var viewState: AnalyticsHubSettingsViewState? = null
        sut.viewStateData.observeForever { _, new -> viewState = new }

        advanceTimeBy(501)

        sut.onSelectionChange(3, true)
        sut.onBackPressed()

        // The exit event is NOT triggered
        assertNotEquals(MultiLiveEvent.Event.Exit, event)
        // The discard dialog is displayed
        assertThat(viewState).isInstanceOf(CardsConfiguration::class.java)
        assertThat((viewState as CardsConfiguration).showDiscardDialog).isEqualTo(true)
    }

    @Test
    fun `when the screen is displayed save button is disabled`() = testBlocking {
        whenever(observeAnalyticsCardsConfiguration.invoke()).thenReturn(flowOf(defaultConfiguration))

        var viewState: AnalyticsHubSettingsViewState? = null
        sut.viewStateData.observeForever { _, new -> viewState = new }

        advanceTimeBy(501)

        // The save button is disabled when the configuration doesn't have any change
        assertThat(viewState).isInstanceOf(CardsConfiguration::class.java)
        assertThat((viewState as CardsConfiguration).isSaveButtonEnabled).isEqualTo(false)
    }

    @Test
    fun `when the screen is displayed and some change are made, the save button is enabled`() = testBlocking {
        whenever(observeAnalyticsCardsConfiguration.invoke()).thenReturn(flowOf(defaultConfiguration))

        var viewState: AnalyticsHubSettingsViewState? = null
        sut.viewStateData.observeForever { _, new -> viewState = new }

        advanceTimeBy(501)

        sut.onSelectionChange(3, true)

        // The save button is disabled when the configuration doesn't have any change
        assertThat(viewState).isInstanceOf(CardsConfiguration::class.java)
        assertThat((viewState as CardsConfiguration).isSaveButtonEnabled).isEqualTo(true)
    }

    @Test
    fun `when the configuration is changed and the save button is pressed, then the updated configuration is saved`() =
        testBlocking {
            whenever(observeAnalyticsCardsConfiguration.invoke()).thenReturn(flowOf(defaultConfiguration))

            val expectedConfiguration = defaultConfiguration.map { it.copy(isVisible = false) }

            advanceTimeBy(501)

            sut.onSelectionChange(1, false)
            sut.onSelectionChange(2, false)
            sut.onSelectionChange(3, false)

            sut.onSaveChanges()

            verify(saveAnalyticsCardsConfiguration).invoke(expectedConfiguration)
        }

    @Test
    fun `when the received configuration only have one selected card, then the selected card is disabled`() = testBlocking {
        val configuration = listOf(
            AnalyticCardConfiguration(1, "Revenue", true),
            AnalyticCardConfiguration(2, "Orders", false),
            AnalyticCardConfiguration(3, "Stats", false)
        )
        val expected = listOf(
            AnalyticCardConfigurationUI(1, "Revenue", true, isEnabled = false),
            AnalyticCardConfigurationUI(2, "Orders", false, isEnabled = true),
            AnalyticCardConfigurationUI(3, "Stats", false, isEnabled = true)
        )
        whenever(observeAnalyticsCardsConfiguration.invoke()).thenReturn(flowOf(configuration))

        var viewState: AnalyticsHubSettingsViewState? = null
        sut.viewStateData.observeForever { _, new -> viewState = new }

        advanceTimeBy(501)

        // The save button is disabled when the configuration doesn't have any change
        assertThat(viewState).isInstanceOf(CardsConfiguration::class.java)
        assertThat((viewState as CardsConfiguration).cardsConfiguration).isEqualTo(expected)
    }

    @Test
    fun `when configuration only have 1 selected card and other card is selected, then all cards are enabled`() = testBlocking {
        val configuration = listOf(
            AnalyticCardConfiguration(1, "Revenue", true),
            AnalyticCardConfiguration(2, "Orders", false),
            AnalyticCardConfiguration(3, "Stats", false)
        )
        val expected = listOf(
            AnalyticCardConfigurationUI(1, "Revenue", true, isEnabled = true),
            AnalyticCardConfigurationUI(2, "Orders", true, isEnabled = true),
            AnalyticCardConfigurationUI(3, "Stats", false, isEnabled = true)
        )
        whenever(observeAnalyticsCardsConfiguration.invoke()).thenReturn(flowOf(configuration))

        var viewState: AnalyticsHubSettingsViewState? = null
        sut.viewStateData.observeForever { _, new -> viewState = new }

        advanceTimeBy(501)

        sut.onSelectionChange(2, true)

        // The save button is disabled when the configuration doesn't have any change
        assertThat(viewState).isInstanceOf(CardsConfiguration::class.java)
        assertThat((viewState as CardsConfiguration).cardsConfiguration).isEqualTo(expected)
    }

    @Test
    fun `when configuration have 2 selected card and one of those cards is deselected, then the selected card is disabled`() = testBlocking {
        val configuration = listOf(
            AnalyticCardConfiguration(1, "Revenue", true),
            AnalyticCardConfiguration(2, "Orders", true),
            AnalyticCardConfiguration(3, "Stats", false)
        )
        val expected = listOf(
            AnalyticCardConfigurationUI(1, "Revenue", true, isEnabled = false),
            AnalyticCardConfigurationUI(2, "Orders", false, isEnabled = true),
            AnalyticCardConfigurationUI(3, "Stats", false, isEnabled = true)
        )
        whenever(observeAnalyticsCardsConfiguration.invoke()).thenReturn(flowOf(configuration))

        var viewState: AnalyticsHubSettingsViewState? = null
        sut.viewStateData.observeForever { _, new -> viewState = new }

        advanceTimeBy(501)

        sut.onSelectionChange(2, false)

        // The save button is disabled when the configuration doesn't have any change
        assertThat(viewState).isInstanceOf(CardsConfiguration::class.java)
        assertThat((viewState as CardsConfiguration).cardsConfiguration).isEqualTo(expected)
    }
}