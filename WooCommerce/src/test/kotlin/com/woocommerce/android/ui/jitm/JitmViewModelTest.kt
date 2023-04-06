package com.woocommerce.android.ui.jitm

import androidx.lifecycle.SavedStateHandle
import com.woocommerce.android.AppUrls
import com.woocommerce.android.BuildConfig
import com.woocommerce.android.model.UiString
import com.woocommerce.android.tools.SelectedSite
import com.woocommerce.android.ui.jitm.JitmViewModel.Companion.JITM_MESSAGE_PATH_KEY
import com.woocommerce.android.ui.mystore.MyStoreUtmProvider
import com.woocommerce.android.ui.mystore.MyStoreViewModel
import com.woocommerce.android.ui.payments.banner.BannerState
import com.woocommerce.android.viewmodel.BaseUnitTest
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.assertj.core.api.Assertions.assertThat
import org.junit.Test
import org.mockito.ArgumentMatchers.anyInt
import org.mockito.ArgumentMatchers.anyString
import org.mockito.kotlin.any
import org.mockito.kotlin.argumentCaptor
import org.mockito.kotlin.eq
import org.mockito.kotlin.mock
import org.mockito.kotlin.times
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import org.wordpress.android.fluxc.model.SiteModel
import org.wordpress.android.fluxc.network.BaseRequest
import org.wordpress.android.fluxc.network.rest.wpcom.wc.WooError
import org.wordpress.android.fluxc.network.rest.wpcom.wc.WooErrorType
import org.wordpress.android.fluxc.network.rest.wpcom.wc.WooResult
import org.wordpress.android.fluxc.network.rest.wpcom.wc.jitm.JITMApiResponse
import org.wordpress.android.fluxc.network.rest.wpcom.wc.jitm.JITMContent
import org.wordpress.android.fluxc.network.rest.wpcom.wc.jitm.JITMCta
import org.wordpress.android.fluxc.store.JitmStore

@ExperimentalCoroutinesApi
class JitmViewModelTest : BaseUnitTest() {
    private val savedState: SavedStateHandle = mock {
        on { get<String>(JITM_MESSAGE_PATH_KEY) }.thenReturn("woomobile:my_store:admin_notices")
    }
    private val jitmStore: JitmStore = mock()
    private val jitmTracker: JitmTracker = mock()
    private val utmProvider: MyStoreUtmProvider = mock()
    private val queryParamsEncoder: QueryParamsEncoder = mock {
        on { getEncodedQueryParams() }.thenReturn(
            "build_type=developer&platform=android&version=${BuildConfig.VERSION_NAME}"
        )
    }
    private val selectedSite: SelectedSite = mock()

    private lateinit var sut: JitmViewModel

    @Test
    fun `given jitm success response, when viewmodel init, then proper banner state event is triggered`() {
        testBlocking {
            whenever(selectedSite.get()).thenReturn(SiteModel())
            whenever(
                jitmStore.fetchJitmMessage(any(), any(), any())
            ).thenReturn(
                WooResult(
                    model = arrayOf(provideJitmApiResponse())
                )
            )

            whenViewModelIsCreated()

            assertThat(sut.jitmState.value).isInstanceOf(BannerState::class.java)
        }
    }

    @Test
    fun `given jitm error response, when viewmodel init, then banner state event is not triggered`() {
        testBlocking {
            whenever(selectedSite.get()).thenReturn(SiteModel())
            whenever(
                jitmStore.fetchJitmMessage(any(), any(), any())
            ).thenReturn(
                WooResult(
                    error = WOO_GENERIC_ERROR
                )
            )

            whenViewModelIsCreated()

            assertThat(sut.jitmState.value).isNull()
        }
    }

    @Test
    fun `given jitm empty response, when viewmodel init, then banner state hide event is triggered`() {
        testBlocking {
            whenever(selectedSite.get()).thenReturn(SiteModel())
            whenever(
                jitmStore.fetchJitmMessage(any(), any(), any())
            ).thenReturn(
                WooResult(
                    model = emptyArray()
                )
            )

            whenViewModelIsCreated()

            assertThat(sut.jitmState.value).isInstanceOf(BannerState.HideBannerState::class.java)
        }
    }

    @Test
    fun `given jitm success response, when viewmodel init, then proper jitm message is used in UI`() {
        testBlocking {
            whenever(selectedSite.get()).thenReturn(SiteModel())
            val testJitmMessage = "Test jitm message"
            whenever(
                jitmStore.fetchJitmMessage(any(), any(), any())
            ).thenReturn(
                WooResult(
                    model = arrayOf(
                        provideJitmApiResponse(
                            content = provideJitmContent(message = testJitmMessage)
                        )
                    )
                )
            )

            whenViewModelIsCreated()

            assertThat(
                ((sut.jitmState.value as BannerState) as BannerState.DisplayBannerState).title
            ).isEqualTo(
                UiString.UiStringText(text = testJitmMessage, containsHtml = false)
            )
        }
    }

    @Test
    fun `given jitm success response, when viewmodel init, then proper jitm description is used in UI`() {
        testBlocking {
            whenever(selectedSite.get()).thenReturn(SiteModel())
            val testJitmDescription = "Test jitm description"
            whenever(
                jitmStore.fetchJitmMessage(any(), any(), any())
            ).thenReturn(
                WooResult(
                    model = arrayOf(
                        provideJitmApiResponse(
                            content = provideJitmContent(description = testJitmDescription)
                        )
                    )
                )
            )

            whenViewModelIsCreated()

            assertThat(
                ((sut.jitmState.value as BannerState) as BannerState.DisplayBannerState).description
            ).isEqualTo(
                UiString.UiStringText(text = testJitmDescription, containsHtml = false)
            )
        }
    }

    @Test
    fun `given jitm success response, when viewmodel init, then proper jitm cta label is used in UI`() {
        testBlocking {
            whenever(selectedSite.get()).thenReturn(SiteModel())
            val testJitmCtaLabel = "Test jitm Cta label"
            whenever(
                jitmStore.fetchJitmMessage(any(), any(), any())
            ).thenReturn(
                WooResult(
                    model = arrayOf(
                        provideJitmApiResponse(
                            jitmCta = provideJitmCta(message = testJitmCtaLabel)
                        )
                    )
                )
            )

            whenViewModelIsCreated()

            assertThat(
                ((sut.jitmState.value as BannerState) as BannerState.DisplayBannerState).primaryActionLabel
            ).isEqualTo(
                UiString.UiStringText(text = testJitmCtaLabel, containsHtml = false)
            )
        }
    }

    @Test
    fun `given jitm displayed, when jitm cta clicked, then jitm click event emitted`() {
        testBlocking {
            whenever(selectedSite.get()).thenReturn(SiteModel())
            whenever(selectedSite.getIfExists()).thenReturn(SiteModel())
            whenever(
                jitmStore.fetchJitmMessage(any(), any(), any())
            ).thenReturn(
                WooResult(
                    model = arrayOf(provideJitmApiResponse())
                )
            )
            whenever(
                utmProvider.getUrlWithUtmParams(
                    anyString(),
                    anyString(),
                    anyString(),
                    any(),
                    anyString(),
                )
            ).thenReturn("")

            whenViewModelIsCreated()
            ((sut.jitmState.value as BannerState) as BannerState.DisplayBannerState).onPrimaryActionClicked.invoke()

            assertThat(sut.event.value).isInstanceOf(JitmViewModel.CtaClick::class.java)
        }
    }

    @Test
    fun `given jitm displayed, when jitm cta clicked, then proper url is passedto OpenJITM event`() {
        testBlocking {
            whenever(selectedSite.get()).thenReturn(SiteModel())
            whenever(selectedSite.getIfExists()).thenReturn(SiteModel())
            whenever(
                jitmStore.fetchJitmMessage(any(), any(), any())
            ).thenReturn(
                WooResult(
                    model = arrayOf(
                        provideJitmApiResponse(
                            jitmCta = provideJitmCta(
                                link = "${AppUrls.WOOCOMMERCE_PURCHASE_CARD_READER_IN_COUNTRY}US"
                            )
                        )
                    )
                )
            )
            whenever(
                utmProvider.getUrlWithUtmParams(
                    anyString(),
                    anyString(),
                    anyString(),
                    any(),
                    anyString(),
                )
            ).thenReturn(
                "${AppUrls.WOOCOMMERCE_PURCHASE_CARD_READER_IN_COUNTRY}US"
            )

            whenViewModelIsCreated()
            ((sut.jitmState.value as BannerState) as BannerState.DisplayBannerState).onPrimaryActionClicked.invoke()

            assertThat(sut.event.value as JitmViewModel.CtaClick).isEqualTo(
                JitmViewModel.CtaClick("${AppUrls.WOOCOMMERCE_PURCHASE_CARD_READER_IN_COUNTRY}US")
            )
        }
    }

    @Test
    fun `when fetch jitms, then fetch JITMS twice`() {
        testBlocking {
            whenever(selectedSite.get()).thenReturn(SiteModel())
            whenViewModelIsCreated()

            sut.fetchJitms()

            // called twice, on view model init and on pull to refresh
            verify(jitmStore, times(2)).fetchJitmMessage(any(), any(), any())
        }
    }

    @Test
    fun `given store setup in US, when viewmodel init, then request for jitm with valid message path`() {
        testBlocking {
            whenever(selectedSite.get()).thenReturn(SiteModel())
            val expectedMessagePath = "woomobile:my_store:admin_notices"
            val captor = argumentCaptor<String>()

            whenViewModelIsCreated()
            verify(jitmStore).fetchJitmMessage(any(), captor.capture(), any())

            assertThat(captor.firstValue).isEqualTo(expectedMessagePath)
        }
    }

    @Test
    fun `given jitm displayed, when jitm dismiss tapped, then banner state is updated to not display`() {
        testBlocking {
            whenever(selectedSite.get()).thenReturn(SiteModel())
            whenever(
                jitmStore.fetchJitmMessage(any(), any(), any())
            ).thenReturn(
                WooResult(
                    model = arrayOf(provideJitmApiResponse())
                )
            )

            whenViewModelIsCreated()
            ((sut.jitmState.value as BannerState) as BannerState.DisplayBannerState).onDismissClicked.invoke()

            assertThat(sut.jitmState.value).isInstanceOf(BannerState.HideBannerState::class.java)
        }
    }

    @Test
    fun `given jitm success response, when viewmodel init, then jitm fetch success is tracked`() {
        testBlocking {
            whenever(selectedSite.get()).thenReturn(SiteModel())
            whenever(
                jitmStore.fetchJitmMessage(any(), any(), any())
            ).thenReturn(
                WooResult(
                    model = arrayOf(provideJitmApiResponse())
                )
            )

            whenViewModelIsCreated()

            verify(jitmTracker).trackJitmFetchSuccess(
                any(), any(), any()
            )
        }
    }

    @Test
    fun `given jitm success, when viewmodel init, then jitm fetch success is tracked with correct properties`() {
        testBlocking {
            whenever(selectedSite.get()).thenReturn(SiteModel())
            whenever(
                jitmStore.fetchJitmMessage(any(), any(), any())
            ).thenReturn(
                WooResult(
                    model = arrayOf(provideJitmApiResponse(id = "12345"))
                )
            )

            whenViewModelIsCreated()

            verify(jitmTracker).trackJitmFetchSuccess(
                MyStoreViewModel.UTM_SOURCE, "12345", 1
            )
        }
    }

    @Test
    fun `given jitm success, when viewmodel init, then jitm fetch success is tracked with highest score jitm id`() {
        testBlocking {
            whenever(selectedSite.get()).thenReturn(SiteModel())
            whenever(
                jitmStore.fetchJitmMessage(any(), any(), any())
            ).thenReturn(
                WooResult(
                    model = arrayOf(
                        provideJitmApiResponse(id = "12345"),
                        provideJitmApiResponse(id = "123456"),
                        provideJitmApiResponse(id = "123")
                    )
                )
            )

            whenViewModelIsCreated()

            verify(jitmTracker).trackJitmFetchSuccess(
                MyStoreViewModel.UTM_SOURCE, "12345", 3
            )
        }
    }

    @Test
    fun `given jitm success response, when viewmodel init, then jitm displayed is tracked`() {
        testBlocking {
            whenever(selectedSite.get()).thenReturn(SiteModel())
            whenever(
                jitmStore.fetchJitmMessage(any(), any(), any())
            ).thenReturn(
                WooResult(
                    model = arrayOf(provideJitmApiResponse())
                )
            )

            whenViewModelIsCreated()

            verify(jitmTracker).trackJitmDisplayed(
                any(), any(), any()
            )
        }
    }

    @Test
    fun `given jitm success, when viewmodel init, then jitm displayed is tracked with correct properties`() {
        testBlocking {
            whenever(selectedSite.get()).thenReturn(SiteModel())
            whenever(
                jitmStore.fetchJitmMessage(any(), any(), any())
            ).thenReturn(
                WooResult(
                    model = arrayOf(
                        provideJitmApiResponse(
                            id = "12345", featureClass = "woomobile_ipp"
                        )
                    )
                )
            )

            whenViewModelIsCreated()

            verify(jitmTracker).trackJitmDisplayed(
                MyStoreViewModel.UTM_SOURCE, "12345", "woomobile_ipp"
            )
        }
    }

    @Test
    fun `given jitm success with empty jitms, when viewmodel init, then jitm fetch success is tracked`() {
        testBlocking {
            whenever(selectedSite.get()).thenReturn(SiteModel())
            whenever(
                jitmStore.fetchJitmMessage(any(), any(), any())
            ).thenReturn(
                WooResult(
                    model = emptyArray()
                )
            )

            whenViewModelIsCreated()

            verify(jitmTracker).trackJitmFetchSuccess(
                anyString(), eq(null), anyInt()
            )
        }
    }

    @Test
    fun `given jitm success with empty jitms, when viewmodel init, then event is tracked with correct properties`() {
        testBlocking {
            whenever(selectedSite.get()).thenReturn(SiteModel())
            whenever(
                jitmStore.fetchJitmMessage(any(), any(), any())
            ).thenReturn(
                WooResult(
                    model = emptyArray()
                )
            )

            whenViewModelIsCreated()

            verify(jitmTracker).trackJitmFetchSuccess(
                MyStoreViewModel.UTM_SOURCE, null, 0
            )
        }
    }

    @Test
    fun `given jitm failure response, when viewmodel init, then jitm fetch failure is tracked`() {
        testBlocking {
            whenever(selectedSite.get()).thenReturn(SiteModel())
            whenever(
                jitmStore.fetchJitmMessage(any(), any(), any())
            ).thenReturn(
                WooResult(
                    WooError(
                        type = WooErrorType.GENERIC_ERROR,
                        original = BaseRequest.GenericErrorType.NETWORK_ERROR,
                        message = ""
                    )
                )
            )

            whenViewModelIsCreated()

            verify(jitmTracker).trackJitmFetchFailure(anyString(), any(), anyString())
        }
    }

    @Test
    fun `given jitm failure, when viewmodel init, then jitm fetch failure is tracked with correct properties`() {
        testBlocking {
            whenever(selectedSite.get()).thenReturn(SiteModel())
            whenever(
                jitmStore.fetchJitmMessage(any(), any(), any())
            ).thenReturn(
                WooResult(
                    WooError(
                        type = WooErrorType.GENERIC_ERROR,
                        original = BaseRequest.GenericErrorType.NETWORK_ERROR,
                        message = "Generic error"
                    )
                )
            )

            whenViewModelIsCreated()

            verify(jitmTracker).trackJitmFetchFailure(
                MyStoreViewModel.UTM_SOURCE, WooErrorType.GENERIC_ERROR, "Generic error"
            )
        }
    }

    @Test
    fun `given jitm displayed, when cta tapped, then cta tapped event is tracked`() {
        testBlocking {
            whenever(selectedSite.get()).thenReturn(SiteModel())
            whenever(selectedSite.getIfExists()).thenReturn(SiteModel())
            whenever(
                jitmStore.fetchJitmMessage(any(), any(), any())
            ).thenReturn(
                WooResult(
                    model = arrayOf(provideJitmApiResponse())
                )
            )
            whenever(
                utmProvider.getUrlWithUtmParams(
                    anyString(),
                    anyString(),
                    anyString(),
                    any(),
                    anyString(),
                )
            ).thenReturn("")

            whenViewModelIsCreated()
            ((sut.jitmState.value as BannerState) as BannerState.DisplayBannerState).onPrimaryActionClicked.invoke()

            verify(jitmTracker).trackJitmCtaTapped(
                any(), any(), any()
            )
        }
    }

    @Test
    fun `given jitm displayed, when cta tapped, then cta tapped event is tracked with correct properties`() {
        testBlocking {
            whenever(selectedSite.get()).thenReturn(SiteModel())
            whenever(selectedSite.getIfExists()).thenReturn(SiteModel())
            whenever(
                jitmStore.fetchJitmMessage(any(), any(), any())
            ).thenReturn(
                WooResult(
                    model = arrayOf(
                        provideJitmApiResponse(
                            id = "12345", featureClass = "woomobile_ipp"
                        )
                    )
                )
            )
            whenever(
                utmProvider.getUrlWithUtmParams(
                    anyString(),
                    anyString(),
                    anyString(),
                    any(),
                    anyString(),
                )
            ).thenReturn("")

            whenViewModelIsCreated()
            ((sut.jitmState.value as BannerState) as BannerState.DisplayBannerState).onPrimaryActionClicked.invoke()

            verify(jitmTracker).trackJitmCtaTapped(
                MyStoreViewModel.UTM_SOURCE, "12345", "woomobile_ipp"
            )
        }
    }

    @Test
    fun `given jitm displayed, when dismiss tapped, then dismiss tapped event is tracked`() {
        testBlocking {
            whenever(selectedSite.get()).thenReturn(SiteModel())
            whenever(
                jitmStore.fetchJitmMessage(any(), any(), any())
            ).thenReturn(
                WooResult(
                    model = arrayOf(provideJitmApiResponse())
                )
            )

            whenViewModelIsCreated()
            ((sut.jitmState.value as BannerState) as BannerState.DisplayBannerState).onDismissClicked.invoke()

            verify(jitmTracker).trackJitmDismissTapped(
                any(), any(), any()
            )
        }
    }

    @Test
    fun `given jitm displayed, when dismiss tapped, then dismiss tapped event is tracked with correct properties`() {
        testBlocking {
            whenever(selectedSite.get()).thenReturn(SiteModel())
            whenever(
                jitmStore.fetchJitmMessage(any(), any(), any())
            ).thenReturn(
                WooResult(
                    model = arrayOf(
                        provideJitmApiResponse(
                            id = "12345", featureClass = "woomobile_ipp"
                        )
                    )
                )
            )

            whenViewModelIsCreated()
            ((sut.jitmState.value as BannerState) as BannerState.DisplayBannerState).onDismissClicked.invoke()

            verify(jitmTracker).trackJitmDismissTapped(
                MyStoreViewModel.UTM_SOURCE, "12345", "woomobile_ipp"
            )
        }
    }

    @Test
    fun `given jitm dismissed, when dismiss success, then dismiss success event is tracked`() {
        testBlocking {
            whenever(selectedSite.get()).thenReturn(SiteModel())
            whenever(
                jitmStore.fetchJitmMessage(any(), any(), any())
            ).thenReturn(
                WooResult(
                    model = arrayOf(provideJitmApiResponse())
                )
            )
            whenever(jitmStore.dismissJitmMessage(any(), any(), any())).thenReturn(
                WooResult(true)
            )

            whenViewModelIsCreated()
            ((sut.jitmState.value as BannerState) as BannerState.DisplayBannerState).onDismissClicked.invoke()

            verify(jitmTracker).trackJitmDismissSuccess(
                any(), any(), any()
            )
        }
    }

    @Test
    fun `given jitm dismissed, when dismiss success, then dismiss success event is tracked with correct properties`() {
        testBlocking {
            whenever(selectedSite.get()).thenReturn(SiteModel())
            whenever(
                jitmStore.fetchJitmMessage(any(), any(), any())
            ).thenReturn(
                WooResult(
                    model = arrayOf(
                        provideJitmApiResponse(
                            id = "12345", featureClass = "woomobile_ipp"
                        )
                    )
                )
            )
            whenever(jitmStore.dismissJitmMessage(any(), any(), any())).thenReturn(
                WooResult(true)
            )

            whenViewModelIsCreated()
            ((sut.jitmState.value as BannerState) as BannerState.DisplayBannerState).onDismissClicked.invoke()

            verify(jitmTracker).trackJitmDismissSuccess(
                MyStoreViewModel.UTM_SOURCE, "12345", "woomobile_ipp"
            )
        }
    }

    @Test
    fun `given jitm dismissed, when dismiss failure, then dismiss failure event is tracked`() {
        testBlocking {
            whenever(selectedSite.get()).thenReturn(SiteModel())
            whenever(
                jitmStore.fetchJitmMessage(any(), any(), any())
            ).thenReturn(
                WooResult(
                    model = arrayOf(provideJitmApiResponse())
                )
            )
            whenever(jitmStore.dismissJitmMessage(any(), any(), any())).thenReturn(
                WooResult(false)
            )

            whenViewModelIsCreated()
            ((sut.jitmState.value as BannerState) as BannerState.DisplayBannerState).onDismissClicked.invoke()

            verify(jitmTracker).trackJitmDismissFailure(
                anyString(), anyString(), anyString(), eq(null), eq(null)
            )
        }
    }

    @Test
    fun `given jitm dismissed, when dismiss error, then dismiss failure event is tracked`() {
        testBlocking {
            whenever(selectedSite.get()).thenReturn(SiteModel())
            whenever(
                jitmStore.fetchJitmMessage(any(), any(), any())
            ).thenReturn(
                WooResult(
                    model = arrayOf(provideJitmApiResponse())
                )
            )
            whenever(jitmStore.dismissJitmMessage(any(), any(), any())).thenReturn(
                WooResult(
                    WooError(
                        type = WooErrorType.GENERIC_ERROR, original = BaseRequest.GenericErrorType.NETWORK_ERROR
                    )
                )
            )

            whenViewModelIsCreated()
            ((sut.jitmState.value as BannerState) as BannerState.DisplayBannerState).onDismissClicked.invoke()

            verify(jitmTracker).trackJitmDismissFailure(
                anyString(), anyString(), anyString(), any(), eq(null)
            )
        }
    }

    @Test
    fun `given jitm dismissed, when dismiss error, then dismiss failure event is tracked with correct properties`() {
        testBlocking {
            whenever(selectedSite.get()).thenReturn(SiteModel())
            whenever(
                jitmStore.fetchJitmMessage(any(), any(), any())
            ).thenReturn(
                WooResult(
                    model = arrayOf(
                        provideJitmApiResponse(
                            id = "12345", featureClass = "woomobile_ipp"
                        )
                    )
                )
            )
            whenever(jitmStore.dismissJitmMessage(any(), any(), any())).thenReturn(
                WooResult(
                    WooError(
                        type = WooErrorType.GENERIC_ERROR,
                        original = BaseRequest.GenericErrorType.NETWORK_ERROR,
                        message = "Generic error"
                    )
                )
            )

            whenViewModelIsCreated()
            ((sut.jitmState.value as BannerState) as BannerState.DisplayBannerState).onDismissClicked.invoke()

            verify(jitmTracker).trackJitmDismissFailure(
                MyStoreViewModel.UTM_SOURCE, "12345", "woomobile_ipp", WooErrorType.GENERIC_ERROR, "Generic error"
            )
        }
    }

    @Test
    fun `given jitm dismissed, when dismiss failure, then dismiss failure event is tracked with correct properties`() {
        testBlocking {
            whenever(selectedSite.get()).thenReturn(SiteModel())
            whenever(
                jitmStore.fetchJitmMessage(any(), any(), any())
            ).thenReturn(
                WooResult(
                    model = arrayOf(
                        provideJitmApiResponse(
                            id = "12345", featureClass = "woomobile_ipp"
                        )
                    )
                )
            )
            whenever(jitmStore.dismissJitmMessage(any(), any(), any())).thenReturn(
                WooResult(false)
            )

            whenViewModelIsCreated()
            ((sut.jitmState.value as BannerState) as BannerState.DisplayBannerState).onDismissClicked.invoke()

            verify(jitmTracker).trackJitmDismissFailure(
                MyStoreViewModel.UTM_SOURCE, "12345", "woomobile_ipp", null, null
            )
        }
    }

    private fun whenViewModelIsCreated() {
        sut = JitmViewModel(
            savedState,
            jitmStore,
            jitmTracker,
            utmProvider,
            queryParamsEncoder,
            selectedSite,
        )
    }

    private fun provideJitmApiResponse(
        content: JITMContent = provideJitmContent(),
        jitmCta: JITMCta = provideJitmCta(),
        timeToLive: Int = 0,
        id: String = "",
        featureClass: String = "",
        expires: Long = 0L,
        maxDismissal: Int = 2,
        isDismissible: Boolean = false,
        url: String = "",
        jitmStatsUrl: String = ""
    ) = JITMApiResponse(
        content = content,
        cta = jitmCta,
        timeToLive = timeToLive,
        id = id,
        featureClass = featureClass,
        expires = expires,
        maxDismissal = maxDismissal,
        isDismissible = isDismissible,
        url = url,
        jitmStatsUrl = jitmStatsUrl
    )

    private fun provideJitmContent(
        message: String = "",
        description: String = "",
        icon: String = "",
        title: String = ""
    ) = JITMContent(
        message = message, description = description, icon = icon, title = title
    )

    private fun provideJitmCta(
        message: String = "",
        link: String = ""
    ) = JITMCta(
        message = message, link = link
    )

    private companion object {
        val WOO_GENERIC_ERROR = WooError(WooErrorType.GENERIC_ERROR, BaseRequest.GenericErrorType.UNKNOWN)
    }
}