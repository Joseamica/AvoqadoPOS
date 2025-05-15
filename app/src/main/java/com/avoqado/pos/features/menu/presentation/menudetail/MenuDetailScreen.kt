package com.avoqado.pos.features.menu.presentation.menudetail

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.avoqado.pos.R
import com.avoqado.pos.core.presentation.components.ToolbarWithIcon
import com.avoqado.pos.core.presentation.model.FlowStep
import com.avoqado.pos.core.presentation.model.IconAction
import com.avoqado.pos.core.presentation.model.IconType
import com.avoqado.pos.features.cart.presentation.CartBadge
import com.avoqado.pos.features.cart.presentation.CartViewModel
import com.avoqado.pos.core.presentation.theme.AppFont
import com.avoqado.pos.features.menu.domain.models.AvoqadoMenu
import com.avoqado.pos.features.menu.domain.models.AvoqadoProduct
import com.avoqado.pos.features.menu.domain.models.Language
import com.avoqado.pos.features.menu.domain.models.MenuCategory
import java.text.NumberFormat
import java.util.Locale

@Composable
fun MenuDetailScreen(viewModel: MenuDetailViewModel) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val selectedCategory by viewModel.selectedCategory.collectAsStateWithLifecycle()
    val cartViewModel = CartViewModel()
    
    MenuDetailContent(
        isLoading = uiState.isLoading,
        menu = uiState.menu,
        categories = uiState.categories,
        selectedCategory = selectedCategory,
        error = uiState.error,
        cartViewModel = cartViewModel,
        onCategorySelected = viewModel::selectCategory,
        onBackClick = viewModel::navigateBack,
        onProductClick = viewModel::navigateToProductDetail,
        onCartClick = viewModel::navigateToCart
    )
}

@Composable
fun MenuDetailContent(
    isLoading: Boolean = false,
    menu: AvoqadoMenu? = null,
    categories: List<MenuCategory> = emptyList(),
    selectedCategory: MenuCategory? = null,
    error: String? = null,
    cartViewModel: CartViewModel = CartViewModel(),
    onCategorySelected: (MenuCategory) -> Unit = {},
    onBackClick: () -> Unit = {},
    onProductClick: (AvoqadoProduct) -> Unit = {},
    onCartClick: () -> Unit = {}
) {
    Scaffold(
        topBar = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp), // Standard toolbar height
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // Back button and title
                Row(
                    modifier = Modifier.weight(1f),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                    
                    Text(
                        text = menu?.name ?: "Detalles de menú",
                        style = MaterialTheme.typography.titleLarge,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
                
                // Cart badge positioned at the end of the row
                CartBadge(
                    viewModel = cartViewModel,
                    onCartClick = onCartClick,
                    modifier = Modifier.padding(end = 8.dp)
                )
            }
        },
        content = { paddingValues ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
            ) {
                if (isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.align(Alignment.Center)
                    )
                } else if (error != null && menu == null) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Spacer(modifier = Modifier.height(32.dp))
                        Text(
                            text = error,
                            style = MaterialTheme.typography.bodyLarge,
                            textAlign = TextAlign.Center
                        )
                    }
                } else if (menu != null) {
                    MenuDetail(
                        menu = menu,
                        categories = categories,
                        selectedCategory = selectedCategory,
                        onCategorySelected = onCategorySelected,
                        onProductClick = onProductClick
                    )
                }
            }
        }
    )
}

@Composable
fun MenuDetail(
    menu: AvoqadoMenu,
    categories: List<MenuCategory>,
    selectedCategory: MenuCategory?,
    onCategorySelected: (MenuCategory) -> Unit,
    onProductClick: (AvoqadoProduct) -> Unit = {}
) {
    Column(modifier = Modifier.fillMaxSize()) {
        // Menu image if available
        if (!menu.image.isNullOrEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp)
                    .background(Color(0xFFEEEEEE))
            ) {
                // Placeholder for image
                Icon(
                    painter = painterResource(R.drawable.baseline_restaurant_menu_24),
                    contentDescription = menu.name,
                    modifier = Modifier
                        .size(48.dp)
                        .align(Alignment.Center),
                    tint = Color.Gray
                )
            }
        }
        
        // Categories horizontal scrollable list
        if (categories.isNotEmpty()) {
            val scrollState = rememberScrollState()
            
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(scrollState)
                    .padding(vertical = 8.dp)
            ) {
                Spacer(modifier = Modifier.width(8.dp))
                
                categories.forEach { category ->
                    val isSelected = category.id == selectedCategory?.id
                    
                    Card(
                        modifier = Modifier
                            .padding(horizontal = 4.dp)
                            .clickable { onCategorySelected(category) },
                        colors = CardDefaults.cardColors(
                            containerColor = if (isSelected) Color.Black else Color(0xFFEEEEEE)
                        ),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Text(
                            text = category.name,
                            style = MaterialTheme.typography.labelLarge.copy(
                                color = if (isSelected) Color.White else Color.Gray
                            ),
                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                        )
                    }
                }
                
                Spacer(modifier = Modifier.width(8.dp))
            }
        }
        
        Divider(color = Color(0xFFEEEEEE), thickness = 1.dp)
        
        // Display products in the selected category
        if (selectedCategory != null) {
            if (selectedCategory.avoqadoProducts.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    Text(
                        text = "No hay productos disponibles en esta categoría",
                        style = MaterialTheme.typography.bodyMedium,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 16.dp, vertical = 8.dp)
                ) {
                    items(selectedCategory.avoqadoProducts) { product ->
                        ProductItem(
                            product = product,
                            onClick = { onProductClick(product) }
                        )
                        Divider(
                            color = Color.Gray,
                            modifier = Modifier.padding(vertical = 8.dp)
                        )
                    }
                    
                    item {
                        Spacer(modifier = Modifier.height(16.dp))
                    }
                }
            }
        } else {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Text(
                    text = "Selecciona una categoría para ver los productos",
                    style = MaterialTheme.typography.bodyMedium,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.align(Alignment.Center)
                )
            }
        }
    }
}

@Composable
fun ProductItem(product: AvoqadoProduct, onClick: () -> Unit = {}) {
    val priceFormat = NumberFormat.getCurrencyInstance(Locale("es", "MX"))
    val hasModifiers = product.modifierGroups.isNotEmpty()
    
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(vertical = 6.dp, horizontal = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Main content column with product details
        Column(
            modifier = Modifier
                .weight(1f)
                .padding(end = 8.dp)
        ) {
            // Product name and optional modifier indicator
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = product.name,
                    style = MaterialTheme.typography.bodyLarge.copy(
                        fontFamily = AppFont.EffraFamily,
                        fontWeight = FontWeight.Medium,
                        color = Color.Black
                    ),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f)
                )
                
                if (hasModifiers) {
                    Spacer(modifier = Modifier.width(4.dp))
                    Box(
                        modifier = Modifier
                            .size(16.dp)
                            .background(
                                color = Color(0xFF2E7D32),
                                shape = CircleShape
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            painter = painterResource(R.drawable.baseline_restaurant_menu_24),
                            contentDescription = "Opciones disponibles",
                            tint = Color.White,
                            modifier = Modifier.size(10.dp)
                        )
                    }
                }
            }
            
            // Product description (if available)
            if (!product.description.isNullOrEmpty()) {
                Text(
                    text = product.description,
                    style = MaterialTheme.typography.labelSmall.copy(
                        color = Color.Gray
                    ),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
        
        // Price display
        Text(
            text = priceFormat.format(product.price).replace("MXN", ""),
            style = MaterialTheme.typography.bodyMedium.copy(
                fontFamily = AppFont.EffraFamily,
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )
        )
    }
}

@Preview(showBackground = true)
@Composable
fun ProductItemPreview() {
    val sampleProduct = AvoqadoProduct(
        id = "1",
        name = "Hamburguesa Clásica",
        description = "Hamburguesa con queso, lechuga, tomate y cebolla",
        image = null,
        price = 129.0,
        venueId = "venue-1",
        isActive = true,
        orderByNumber = 1,
        categoryId = "cat-1"
    )
    
    Surface {
        ProductItem(product = sampleProduct)
    }
}

@Preview(showBackground = true)
@Composable
fun MenuDetailPreview() {
    val sampleMenu = AvoqadoMenu(
        id = "1",
        name = "Menú Principal",
        description = "Nuestro menú incluye una gran variedad de platillos",
        image = null,
        venueId = "venue-1",
        isActive = true,
        language = Language(
            id = "lang-1",
            name = "Español",
            code = "es",
            venueId = "venue-1"
        ),
        isFixed = true,
        startTime = "08:00",
        endTime = "22:00",
        orderByNumber = 1,
        categories = listOf(
            MenuCategory(
                id = "cat-1",
                name = "Entradas",
                description = "Deliciosas entradas",
                image = null,
                venueId = "venue-1",
                isActive = true,
                orderByNumber = 1,
                avoqadoProducts = listOf(
                    AvoqadoProduct(
                        id = "prod-1",
                        name = "Nachos con queso",
                        description = "Crujientes nachos con queso derretido y jalapeños",
                        image = null,
                        price = 85.0,
                        venueId = "venue-1",
                        isActive = true,
                        orderByNumber = 1,
                        categoryId = "cat-1"
                    ),
                    AvoqadoProduct(
                        id = "prod-2",
                        name = "Guacamole tradicional",
                        description = "Guacamole preparado al momento con chips de maíz",
                        image = null,
                        price = 95.0,
                        venueId = "venue-1",
                        isActive = true,
                        orderByNumber = 2,
                        categoryId = "cat-1"
                    )
                )
            ),
            MenuCategory(
                id = "cat-2",
                name = "Platos Fuertes",
                description = "Platos principales",
                image = null,
                venueId = "venue-1",
                isActive = true,
                orderByNumber = 2,
                avoqadoProducts = listOf(
                    AvoqadoProduct(
                        id = "prod-3",
                        name = "Hamburguesa Clásica",
                        description = "Hamburguesa con queso, lechuga, tomate y cebolla",
                        image = null,
                        price = 129.0,
                        venueId = "venue-1",
                        isActive = true,
                        orderByNumber = 1,
                        categoryId = "cat-2"
                    )
                )
            )
        )
    )
    
    Surface {
        MenuDetailContent(
            menu = sampleMenu,
            categories = sampleMenu.categories,
            selectedCategory = sampleMenu.categories.first()
        )
    }
}
