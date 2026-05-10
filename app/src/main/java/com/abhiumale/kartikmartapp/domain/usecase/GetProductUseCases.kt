package com.abhiumale.kartikmartapp.domain.usecase

import com.abhiumale.kartikmartapp.data.repository.ProductRepository
import com.abhiumale.kartikmartapp.domain.model.Product
import javax.inject.Inject

class GetProductsUseCase @Inject constructor(
    private val repository: ProductRepository
) {
    suspend operator fun invoke(): List<Product> {
        return repository.getProducts()
    }

    suspend fun byCategory(category: String): List<Product> {
        return repository.getByCategory(category)
    }

    suspend fun byBrand(brand: String): List<Product> {
        return repository.getByBrand(brand)
    }
}