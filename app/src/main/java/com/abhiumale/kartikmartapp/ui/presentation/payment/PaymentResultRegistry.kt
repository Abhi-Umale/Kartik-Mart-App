package com.abhiumale.kartikmartapp.ui.presentation.payment

import kotlinx.coroutines.flow.MutableSharedFlow

object PaymentResultRegistry {
    val paymentResults = MutableSharedFlow<Boolean>()
}
