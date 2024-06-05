package com.woocommerce.android.ui.woopos.home.products

import com.woocommerce.android.model.Product
import kotlinx.coroutines.flow.Flow

interface ProductsDataSource {
    val products: Flow<List<Product>>
    suspend fun loadProducts()
    suspend fun loadMore()
}