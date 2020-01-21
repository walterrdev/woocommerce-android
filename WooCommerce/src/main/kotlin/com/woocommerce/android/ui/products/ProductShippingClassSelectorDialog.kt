package com.woocommerce.android.ui.products

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import com.woocommerce.android.RequestCodes
import com.woocommerce.android.analytics.AnalyticsTracker
import org.wordpress.android.fluxc.network.rest.wpcom.wc.product.CoreProductBackOrders
import org.wordpress.android.fluxc.network.rest.wpcom.wc.product.CoreProductStockStatus
import org.wordpress.android.fluxc.network.rest.wpcom.wc.product.CoreProductTaxStatus

/**
 * Dialog displays a list of product shipping classes
 *
 * This fragment should be instantiated using the [ProductShippingClassSelectorDialog.newInstance] method.
 * Calling classes can obtain the results of selection through the [onActivityResult]
 * via [ProductShippingClassSelectorDialog.getTargetFragment].
 *
 * The [resultCode] passed to this fragment is used to classify the product shipping class
 */
class ProductShippingClassSelectorDialog : DialogFragment() {
    companion object {
        const val TAG: String = "ProductShippingSelectorDialog"

        fun newInstance(
            listener: Fragment,
            resultCode: Int,
            dialogTitle: String,
            listItemMap: Map<String, String>,
            selectedListItem: String?
        ): ProductShippingClassSelectorDialog {
            val fragment = ProductShippingClassSelectorDialog()
            fragment.setTargetFragment(listener, RequestCodes.PRODUCT_SHIPPING_CLASS)
            fragment.retainInstance = true
            fragment.resultCode = resultCode
            fragment.dialogTitle = dialogTitle
            fragment.listItemMap = listItemMap
            fragment.selectedListItem = selectedListItem
            return fragment
        }
    }

    interface ProductShippingClassSelectorDialogListener {
        fun onProductShippingClassSelected(resultCode: Int, selectedItem: String?)
    }

    private var resultCode: Int = -1
    private var selectedListItem: String? = null

    private var dialogTitle: String? = null
    private var listItemMap: Map<String, String>? = null

    private var listener: ProductShippingClassSelectorDialogListener? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        listener = targetFragment as ProductShippingClassSelectorDialogListener
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val selectedIndex = getCurrentProductShippingListIndex()

        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle(dialogTitle)
                .setSingleChoiceItems(listItemMap?.values?.toTypedArray(), selectedIndex) { dialog, which ->
                    listener?.onProductShippingClassSelected(resultCode, listItemMap?.keys?.toTypedArray()?.get(which))
                    dialog.dismiss()
                }
        return builder.create()
    }

    private fun getCurrentProductShippingListIndex(): Int {
        return listItemMap?.values?.indexOfFirst { it == selectedListItem } ?: 0
    }

    override fun onResume() {
        super.onResume()
        AnalyticsTracker.trackViewShown(this)
    }
}
