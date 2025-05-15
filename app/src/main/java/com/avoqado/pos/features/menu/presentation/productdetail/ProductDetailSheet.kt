package com.avoqado.pos.features.menu.presentation.productdetail

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SheetState
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.avoqado.pos.features.menu.domain.models.AvoqadoProduct
import com.avoqado.pos.features.menu.domain.models.ModifierGroup
import com.avoqado.pos.features.menu.domain.models.ProductModifier

/**
 * Main entry point for the product detail bottom sheet
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductDetailSheet(
    viewModel: ProductDetailViewModel,
    sheetState: SheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val selectedModifiers by viewModel.selectedModifiers.collectAsStateWithLifecycle()
    val totalPrice by viewModel.totalPrice.collectAsStateWithLifecycle()
    val quantity by viewModel.quantity.collectAsStateWithLifecycle()
    
    ModalBottomSheet(
        onDismissRequest = viewModel::dismissBottomSheet,
        modifier = Modifier.fillMaxWidth(),
        containerColor = Color.White,
        sheetState = sheetState
    ) {
        // Use the implementation from ProductDetailContent.kt
        ProductDetailContent(
            isLoading = uiState.isLoading,
            product = uiState.product,
            modifierGroups = uiState.modifierGroups,
            selectedModifiers = selectedModifiers,
            totalPrice = totalPrice,
            quantity = quantity,
            error = uiState.error,
            onModifierToggle = viewModel::toggleModifier,
            onQuantityChange = viewModel::updateQuantity,
            onAddToOrder = viewModel::addToOrder,
            onDismiss = viewModel::dismissBottomSheet,
            isModifierSelected = viewModel::isModifierSelected,
            areRequiredSelectionsComplete = viewModel::areRequiredSelectionsComplete
        )
    }
}
