package com.abhiumale.kartikmartapp.ui.presentation.order_tracking


import androidx.lifecycle.ViewModel
import com.abhiumale.kartikmartapp.data.repository.CheckoutRepository
import com.google.android.gms.maps.model.LatLng
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

@HiltViewModel
class OrderTrackingViewModel @Inject constructor(
    private val repository: CheckoutRepository
) : ViewModel() {

    fun getCourierLiveLocation(orderId: String): Flow<LatLng> {
        return repository.getLiveLocation(orderId)
    }
}