package com.woocommerce.android.ui.products.components

import android.os.Bundle
import android.view.View
import androidx.core.text.HtmlCompat
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.woocommerce.android.R
import com.woocommerce.android.databinding.FragmentComponentDetailsBinding
import com.woocommerce.android.di.GlideApp
import com.woocommerce.android.extensions.takeIfNotEqualTo
import com.woocommerce.android.ui.base.BaseFragment
import com.woocommerce.android.widgets.AlignedDividerDecoration
import com.woocommerce.android.widgets.SkeletonView
import dagger.hilt.android.AndroidEntryPoint
import org.wordpress.android.util.PhotonUtils

@AndroidEntryPoint
class ComponentDetailsFragment : BaseFragment(R.layout.fragment_component_details) {
    val viewModel: ComponentDetailsViewModel by viewModels()
    private var _binding: FragmentComponentDetailsBinding? = null
    private val binding get() = _binding!!

    private val skeletonView = SkeletonView()

    override fun getFragmentTitle() = resources.getString(R.string.product_component_settings)

    private val componentsOptionsListAdapter: ComponentOptionsListAdapter by lazy { ComponentOptionsListAdapter() }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentComponentDetailsBinding.bind(view)

        binding.componentOptionsRecycler.run {
            layoutManager = LinearLayoutManager(requireActivity())
            adapter = componentsOptionsListAdapter
            isMotionEventSplittingEnabled = false
            if (itemDecorationCount == 0) {
                addItemDecoration(
                    AlignedDividerDecoration(
                        context,
                        DividerItemDecoration.VERTICAL,
                        R.id.componentOptionTitle
                    )
                )
            }
        }

        viewModel.componentDetails.observe(viewLifecycleOwner) { details ->
            binding.componentTitle.text = details.title
            binding.componentDescription.text = HtmlCompat.fromHtml(
                details.description,
                HtmlCompat.FROM_HTML_MODE_COMPACT
            )
            showComponentImage(details.imageUrl)
        }

        viewModel.componentOptions.observe(viewLifecycleOwner) { componentOptions ->
            binding.componentOptionsType.text = componentOptions.type
            componentOptions.default?.let {
                binding.componentOptionsDefault.text = it.title
            } ?: run {
                binding.componentOptionsDefaultLabel.isVisible = false
                binding.componentOptionsDefault.isVisible = false
            }
            componentsOptionsListAdapter.submitList(componentOptions.options)
        }

        viewModel.componentDetailsViewStateData.observe(viewLifecycleOwner) { old, new ->
            new.isSkeletonShown?.takeIfNotEqualTo(old?.isSkeletonShown) { showSkeleton(it) }
        }
    }

    private fun showComponentImage(imageUrl: String?) {

        when {
            imageUrl.isNullOrEmpty() -> {
                binding.componentImage.isVisible = false
            }
            else -> {
                val imageSize = resources.getDimensionPixelSize(R.dimen.image_major_120)
                val photonUrl = PhotonUtils.getPhotonImageUrl(imageUrl, imageSize, imageSize)
                GlideApp.with(requireContext()).load(photonUrl)
                    .transform(CenterCrop()).placeholder(R.drawable.ic_product)
                    .into(binding.componentImage)
            }
        }
    }

    private fun showSkeleton(show: Boolean) {
        when (show) {
            true -> {
                skeletonView.show(binding.componentOptionsSection, R.layout.skeleton_component_options, delayed = true)
            }
            false -> skeletonView.hide()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}
