package com.woocommerce.android.ui.media

import android.os.Parcelable
import androidx.lifecycle.SavedStateHandle
import com.woocommerce.android.R
import com.woocommerce.android.ui.media.MediaFileUploadHandler.UploadStatus
import com.woocommerce.android.ui.media.MediaFileUploadHandler.UploadStatus.Failed
import com.woocommerce.android.viewmodel.LiveDataDelegate
import com.woocommerce.android.viewmodel.ResourceProvider
import com.woocommerce.android.viewmodel.ScopedViewModel
import com.woocommerce.android.viewmodel.navArgs
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.parcelize.Parcelize
import javax.inject.Inject

@HiltViewModel
class MediaUploadErrorListViewModel @Inject constructor(
    private val resourceProvider: ResourceProvider,
    mediaFileUploadHandler: MediaFileUploadHandler,
    savedState: SavedStateHandle
) : ScopedViewModel(savedState) {
    private val navArgs: MediaUploadErrorListFragmentArgs by savedState.navArgs()

    val viewStateData = LiveDataDelegate(savedState, ViewState())
    private var viewState by viewStateData

    init {
        mediaFileUploadHandler.observeCurrentUploadErrors(navArgs.remoteId)
            .onEach { errors ->
                viewState = viewState.copy(
                    uploadErrorList = errors.map { ErrorUiModel(it.uploadStatus as Failed) },
                    toolBarTitle = resourceProvider.getString(R.string.product_images_error_detail_title, errors.size)
                )
            }
            .launchIn(this)
    }

    @Parcelize
    data class ViewState(
        val uploadErrorList: List<ErrorUiModel> = emptyList(),
        val toolBarTitle: String = ""
    ) : Parcelable

    @Parcelize
    data class ErrorUiModel(
        val fileName: String,
        val errorMessage: String,
        val filePath: String
    ) : Parcelable {
        constructor(state: UploadStatus.Failed) : this(
            fileName = state.media.fileName,
            errorMessage = state.mediaErrorMessage,
            filePath = state.media.filePath
        )
    }
}
