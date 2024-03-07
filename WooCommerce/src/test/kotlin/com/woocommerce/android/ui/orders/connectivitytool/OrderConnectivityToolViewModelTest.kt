package com.woocommerce.android.ui.orders.connectivitytool

import androidx.lifecycle.SavedStateHandle
import com.woocommerce.android.ui.orders.connectivitytool.OrderConnectivityToolViewModel.ConnectivityCheckStatus
import com.woocommerce.android.ui.orders.connectivitytool.OrderConnectivityToolViewModel.ConnectivityCheckStatus.InProgress
import com.woocommerce.android.ui.orders.connectivitytool.OrderConnectivityToolViewModel.ConnectivityCheckStatus.NotStarted
import com.woocommerce.android.ui.orders.connectivitytool.OrderConnectivityToolViewModel.ConnectivityCheckStatus.Success
import com.woocommerce.android.ui.orders.connectivitytool.OrderConnectivityToolViewModel.OpenSupportRequest
import com.woocommerce.android.ui.orders.connectivitytool.useCases.InternetConnectionCheckUseCase
import com.woocommerce.android.ui.orders.connectivitytool.useCases.StoreConnectionCheckUseCase
import com.woocommerce.android.ui.orders.connectivitytool.useCases.StoreOrdersCheckUseCase
import com.woocommerce.android.ui.orders.connectivitytool.useCases.WordPressConnectionCheckUseCase
import com.woocommerce.android.viewmodel.BaseUnitTest
import com.woocommerce.android.viewmodel.MultiLiveEvent
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import org.assertj.core.api.Assertions.assertThat
import org.junit.Before
import org.junit.Test
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever

@OptIn(ExperimentalCoroutinesApi::class)
class OrderConnectivityToolViewModelTest : BaseUnitTest() {
    private lateinit var sut: OrderConnectivityToolViewModel
    private lateinit var internetConnectionCheck: InternetConnectionCheckUseCase
    private lateinit var wordPressConnectionCheck: WordPressConnectionCheckUseCase
    private lateinit var storeConnectionCheck: StoreConnectionCheckUseCase
    private lateinit var storeOrdersCheck: StoreOrdersCheckUseCase

    @Before
    fun setUp() {
        internetConnectionCheck = mock()
        wordPressConnectionCheck = mock()
        storeConnectionCheck = mock()
        storeOrdersCheck = mock()
        whenever(internetConnectionCheck()).thenReturn(flowOf())
        whenever(wordPressConnectionCheck()).thenReturn(flowOf())
        whenever(storeConnectionCheck()).thenReturn(flowOf())
        whenever(storeOrdersCheck()).thenReturn(flowOf())
        sut = OrderConnectivityToolViewModel(
            internetConnectionCheck = internetConnectionCheck,
            wordPressConnectionCheck = wordPressConnectionCheck,
            storeConnectionCheck = storeConnectionCheck,
            storeOrdersCheck = storeOrdersCheck,
            savedState = SavedStateHandle()
        )
    }

    @Test
    fun `when internetConnectionTest use case starts, then update ViewState as expected`() = testBlocking {
        // Given
        val stateEvents = mutableListOf<ConnectivityCheckStatus>()
        whenever(internetConnectionCheck()).thenReturn(flowOf(Success))
        sut.viewState.observeForever {
            stateEvents.add(it.internetConnectionCheckStatus)
        }

        // When
        sut.startConnectionTests()

        // Then
        assertThat(stateEvents).isEqualTo(listOf(NotStarted, Success))
    }

    @Test
    fun `when wordPressConnectionTest use case starts, then update ViewState as expected`() = testBlocking {
        // Given
        val stateEvents = mutableListOf<ConnectivityCheckStatus>()
        whenever(wordPressConnectionCheck()).thenReturn(flowOf(Success))
        sut.viewState.observeForever {
            stateEvents.add(it.wordpressConnectionCheckStatus)
        }

        // When
        sut.startConnectionTests()

        // Then
        assertThat(stateEvents).isEqualTo(listOf(NotStarted, Success))
    }

    @Test
    fun `when storeConnectionTest use case starts, then update ViewState as expected`() = testBlocking {
        // Given
        val stateEvents = mutableListOf<ConnectivityCheckStatus>()
        whenever(storeConnectionCheck()).thenReturn(flowOf(Success))
        sut.viewState.observeForever {
            stateEvents.add(it.storeConnectionCheckStatus)
        }

        // When
        sut.startConnectionTests()

        // Then
        assertThat(stateEvents).isEqualTo(listOf(NotStarted, Success))
    }

    @Test
    fun `when storeOrdersTest use case starts, then update ViewState as expected`() = testBlocking {
        // Given
        val stateEvents = mutableListOf<ConnectivityCheckStatus>()
        whenever(storeOrdersCheck()).thenReturn(flowOf(Success))
        sut.viewState.observeForever {
            stateEvents.add(it.storeOrdersCheckStatus)
        }

        // When
        sut.startConnectionTests()

        // Then
        assertThat(stateEvents).isEqualTo(listOf(NotStarted, Success))
    }

    @Test
    fun `when all checks are finished, then isContactSupportButtonEnabled is true`() = testBlocking {
        // Given
        val stateEvents = mutableListOf<Boolean>()
        whenever(internetConnectionCheck()).thenReturn(flowOf(Success))
        whenever(wordPressConnectionCheck()).thenReturn(flowOf(Success))
        whenever(storeConnectionCheck()).thenReturn(flowOf(Success))
        whenever(storeOrdersCheck()).thenReturn(flowOf(Success))
        sut.viewState.observeForever {
            stateEvents.add(it.isCheckFinished)
        }

        // When
        sut.startConnectionTests()

        // Then
        assertThat(stateEvents).isEqualTo(listOf(false, true))
    }

    @Test
    fun `when checks are still running, then isContactSupportButtonEnabled is false`() = testBlocking {
        // Given
        val stateEvents = mutableListOf<Boolean>()
        whenever(internetConnectionCheck()).thenReturn(flowOf(InProgress))
        whenever(wordPressConnectionCheck()).thenReturn(flowOf(InProgress))
        whenever(storeConnectionCheck()).thenReturn(flowOf(InProgress))
        whenever(storeOrdersCheck()).thenReturn(flowOf(InProgress))
        sut.viewState.observeForever {
            stateEvents.add(it.isCheckFinished)
        }

        // When
        sut.startConnectionTests()

        // Then
        assertThat(stateEvents).isEqualTo(listOf(false, false))
    }

    @Test
    fun `when onContactSupportClicked is called, then trigger OpenSupportRequest event`() {
        // Given
        val events = mutableListOf<MultiLiveEvent.Event>()
        sut.event.observeForever { events.add(it) }

        // When
        sut.onContactSupportClicked()

        // Then
        assertThat(events).isEqualTo(listOf(OpenSupportRequest))
    }
}
