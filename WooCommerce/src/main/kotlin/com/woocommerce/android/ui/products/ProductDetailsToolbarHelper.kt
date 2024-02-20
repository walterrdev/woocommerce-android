package com.woocommerce.android.ui.products

import android.app.Activity
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.view.Menu
import android.view.MenuItem
import androidx.annotation.IdRes
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.content.ContextCompat
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import com.woocommerce.android.R
import com.woocommerce.android.databinding.FragmentProductDetailBinding
import javax.inject.Inject

class ProductDetailsToolbarHelper @Inject constructor(
    private val activity: Activity,
) : DefaultLifecycleObserver {
    private var fragment: ProductDetailFragment? = null
    private var binding: FragmentProductDetailBinding? = null
    private var viewModel: ProductDetailViewModel? = null

    private var menu: Menu? = null

    fun onViewCreated(
        fragment: ProductDetailFragment,
        viewModel: ProductDetailViewModel,
        binding: FragmentProductDetailBinding,
    ) {
        this.fragment = fragment
        this.binding = binding
        this.viewModel = viewModel

        fragment.lifecycle.addObserver(this)

        setupToolbar()

        viewModel.menuButtonsState.observe(fragment.viewLifecycleOwner) {
            menu?.updateOptions(it)
        }
    }

    fun updateTitle(title: String) {
        binding?.productDetailToolbar?.title = title
    }

    override fun onDestroy(owner: LifecycleOwner) {
        fragment = null
        binding = null
        viewModel = null
        menu = null
    }

    fun setupToolbar() {
        val toolbar = binding?.productDetailToolbar ?: return

        toolbar.inflateMenu(R.menu.menu_product_detail_fragment)
        this.menu = toolbar.menu

        toolbar.navigationIcon = if (fragment?.findNavController()?.hasBackStackEntry(R.id.products) == true) {
            AppCompatResources.getDrawable(activity, R.drawable.ic_back_24dp)
        } else {
            AppCompatResources.getDrawable(activity, R.drawable.ic_gridicons_cross_24dp)
        }

        // change the font color of the trash menu item to red, and only show it if it should be enabled
        with(toolbar.menu.findItem(R.id.menu_trash_product)) {
            if (this == null) return@with
            val title = SpannableString(this.title)
            title.setSpan(
                ForegroundColorSpan(
                    ContextCompat.getColor(
                        activity,
                        R.color.woo_red_30
                    )
                ),
                0,
                title.length,
                0
            )
            this.title = title
        }

        viewModel?.menuButtonsState?.value?.let {
            toolbar.menu.updateOptions(it)
        }
    }

    private fun Menu.updateOptions(state: ProductDetailViewModel.MenuButtonsState) {
        findItem(R.id.menu_save)?.isVisible = state.saveOption
        findItem(R.id.menu_save_as_draft)?.isVisible = state.saveAsDraftOption
        findItem(R.id.menu_view_product)?.isVisible = state.viewProductOption
        findItem(R.id.menu_publish)?.apply {
            isVisible = state.publishOption
            if (state.saveOption) {
                setShowAsActionFlags(MenuItem.SHOW_AS_ACTION_NEVER)
            } else {
                setShowAsActionFlags(MenuItem.SHOW_AS_ACTION_IF_ROOM)
            }
        }
        findItem(R.id.menu_share)?.apply {
            isVisible = state.shareOption

            setShowAsActionFlags(
                if (state.showShareOptionAsActionWithText) {
                    MenuItem.SHOW_AS_ACTION_IF_ROOM
                } else {
                    MenuItem.SHOW_AS_ACTION_NEVER
                }
            )
        }
        findItem(R.id.menu_trash_product)?.isVisible = state.trashOption
    }

    private fun NavController.hasBackStackEntry(@IdRes destinationId: Int) = runCatching {
        getBackStackEntry(destinationId)
    }.isSuccess
}
