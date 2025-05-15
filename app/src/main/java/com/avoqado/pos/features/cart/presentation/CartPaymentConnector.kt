package com.avoqado.pos.features.cart.presentation

import android.content.Context
import android.content.Intent
import android.util.Log
import com.avoqado.pos.AvoqadoApp
import com.avoqado.pos.OperationFlowHolder
import com.avoqado.pos.core.domain.models.SplitType
import com.avoqado.pos.features.cart.data.repository.CartRepository
import com.avoqado.pos.views.DoPaymentActivity
import com.menta.android.core.model.Amount
import com.menta.android.core.model.OperationFlow
import com.menta.android.core.model.OperationType
import java.math.BigDecimal

/**
 * Helper class to connect the cart to the existing payment flow
 */
object CartPaymentConnector {
    
    private const val TAG = "CartPaymentConnector"
    
    /**
     * Start the payment process from cart content
     * 
     * @param context Activity context to launch the payment flow
     * @param waiterName Optional name of the waiter/server
     */
    fun startPaymentFromCart(context: Context, waiterName: String = "") {
        val cartRepository = CartRepository.getInstance()
        val cart = cartRepository.cart.value
        
        if (cart.isEmpty()) {
            Log.e(TAG, "Cannot start payment process with empty cart")
            return
        }
        
        // Calculate the total amount to pay
        val totalAmount = cart.calculateTotal()
        
        try {
            // Set up the operation flow for payment
            val operationFlow = OperationFlow()
            
            // Create proper Amount object
            val amountObj = Amount()
            // If total expects a string, convert the double to string format
            amountObj.total = String.format("%.2f", totalAmount)
            
            operationFlow.apply {
                // Assign the Amount object
                amount = amountObj
                // Don't set tip here as it might be handled differently
                transactionType = OperationType.PAYMENT
                // Set any other required payment parameters
            }
            
            // Store the operation flow in the global holder
            OperationFlowHolder.operationFlow = operationFlow
            
            // Create intent to start payment activity
            val intent = Intent(context, DoPaymentActivity::class.java)
            
            // Add necessary extras
            intent.putExtra("splitType", SplitType.FULLPAYMENT.name) // Use available enum value
            intent.putExtra("waiterName", waiterName)
            
            // Start the payment activity
            context.startActivity(intent)
            
            // Optionally clear the cart after successful payment initiation
            // cartRepository.clearCart()
        } catch (e: Exception) {
            Log.e(TAG, "Error starting payment process: ${e.message}", e)
        }
    }
}
