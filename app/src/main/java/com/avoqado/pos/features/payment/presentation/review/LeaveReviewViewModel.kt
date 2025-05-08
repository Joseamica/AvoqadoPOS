package com.avoqado.pos.features.payment.presentation.review

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.avoqado.pos.AvoqadoApp
import com.avoqado.pos.core.presentation.navigation.NavigationArg
import com.avoqado.pos.core.presentation.navigation.NavigationDispatcher
import com.avoqado.pos.features.payment.presentation.navigation.PaymentDests
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

enum class ReviewRating {
    POOR, GOOD, EXCELLENT
}

class LeaveReviewViewModel(
    private val navigationDispatcher: NavigationDispatcher,
    val subtotal: String, // Made public so it can be accessed in the screen
    private val waiterName: String,
    private val splitType: String,
    val venueName: String // Made public so it can be accessed in the screen
) : ViewModel() {

    private val _rating = MutableStateFlow<ReviewRating?>(null)
    val rating: StateFlow<ReviewRating?> = _rating.asStateFlow()

    fun setRating(rating: ReviewRating) {
        _rating.value = rating
        
        // Log the rating (simplified without recordMetric)
        viewModelScope.launch {
            try {
                // Just log the rating in the console for now
                println("User rated their experience as: ${rating.name}")
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
        
        // Proceed to tip screen
        proceedToTipScreen()
    }

    fun skipReview() {
        proceedToTipScreen()
    }
    
    fun navigateBack() {
        // Go back to the previous screen (TableDetailScreen)
        navigationDispatcher.navigateBack()
    }

    private fun proceedToTipScreen() {
        navigationDispatcher.navigateWithArgs(
            PaymentDests.InputTip,
            NavigationArg.StringArg(PaymentDests.InputTip.ARG_SUBTOTAL, subtotal),
            NavigationArg.StringArg(PaymentDests.InputTip.ARG_WAITER, waiterName),
            NavigationArg.StringArg(PaymentDests.InputTip.ARG_SPLIT_TYPE, splitType)
        )
    }
}
