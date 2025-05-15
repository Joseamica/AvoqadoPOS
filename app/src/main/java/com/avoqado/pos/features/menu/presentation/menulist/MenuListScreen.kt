package com.avoqado.pos.features.menu.presentation.menulist

import timber.log.Timber
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
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
import com.avoqado.pos.core.presentation.components.SimpleToolbar
import com.avoqado.pos.core.presentation.model.FlowStep
import com.avoqado.pos.core.presentation.model.IconAction
import com.avoqado.pos.core.presentation.model.IconType
import com.avoqado.pos.core.presentation.theme.AppFont
import com.avoqado.pos.features.menu.domain.models.AvoqadoMenu
import com.avoqado.pos.features.menu.domain.models.Language
import com.avoqado.pos.features.menu.domain.models.MenuCategory


@Composable
fun MenuListScreen(viewModel: MenuListViewModel) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    
    // Debug logs to check what's in UI state
    LaunchedEffect(uiState) {
        Timber.d("UI State: loading=${uiState.isLoading}, menus.size=${uiState.menus.size}, error=${uiState.error}")
        if (uiState.menus.isNotEmpty()) {
            Timber.d("First menu: ${uiState.menus.first().name}")
        }
    }

    MenuListContent(
        isLoading = uiState.isLoading,
        menus = uiState.menus,
        error = uiState.error,
        onMenuClick = { viewModel.navigateToMenuDetail(it) },
        onBackClick = { viewModel.navigateBack() },
        onRefresh = { viewModel.fetchMenus() }
    )
}

@Composable
fun MenuListContent(
    isLoading: Boolean = false,
    menus: List<AvoqadoMenu> = emptyList(),
    error: String? = null,
    onMenuClick: (String) -> Unit = {},
    onBackClick: () -> Unit = {},
    onRefresh: () -> Unit = {}
) {
    Scaffold(
        topBar = {
            ToolbarWithIcon(
                title = "Menús",
                iconAction = IconAction(
                    iconType = IconType.BACK,
                    flowStep = FlowStep.NAVIGATE_BACK,
                    context = LocalContext.current
                ),
                onAction = onBackClick,
                onActionSecond = onRefresh,
                showSecondIcon = true,
                secondIconRes = R.drawable.baseline_refresh_24
            )
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
                } else if (error != null && menus.isEmpty()) {
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
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "Toca para reintentar",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.clickable { onRefresh() }
                        )
                    }
                } else if (menus.isNotEmpty()) {
                    // Display menus if available
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = 16.dp)
                    ) {
                        // Debug info at the top
                        item {
                            Text(
                                text = "Mostrando ${menus.size} menús",
                                style = MaterialTheme.typography.bodySmall,
                                color = Color.Gray,
                                modifier = Modifier.padding(vertical = 4.dp)
                            )
                            Divider()
                            Spacer(modifier = Modifier.height(8.dp))
                        }
                        
                        // List of menus
                        items(menus) { menu ->
                            MenuCard(
                                menu = menu,
                                onClick = { onMenuClick(menu.id) }
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                        }
                        item {
                            Spacer(modifier = Modifier.height(16.dp))
                        }
                    }
                } else {
                    // Fallback message when no menus and no error
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Spacer(modifier = Modifier.height(32.dp))
                        Text(
                            text = "No se encontraron menús disponibles",
                            style = MaterialTheme.typography.bodyLarge,
                            textAlign = TextAlign.Center
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "Toca para reintentar",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.clickable { onRefresh() }
                        )
                    }
                }
            }
        }
    )
}

@Composable
fun MenuCard(
    menu: AvoqadoMenu,
    onClick: () -> Unit = {}
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 2.dp
        ),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            // Menu image if available
            if (!menu.image.isNullOrEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(150.dp)
                        .clip(RoundedCornerShape(topStart = 12.dp, topEnd = 12.dp))
                        .background(color = Color(0xFFEEEEEE))
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
            
            Column(modifier = Modifier.padding(16.dp)) {
                // Menu name
                Text(
                    text = menu.name,
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontFamily = AppFont.EffraFamily,
                        color = Color.Black,
                        fontWeight = FontWeight.W600
                    )
                )
                
                // Menu description if available
                if (!menu.description.isNullOrEmpty()) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = menu.description,
                        style = MaterialTheme.typography.bodyMedium.copy(
                            color = Color.Gray
                        ),
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                }
                
                // Menu time info if available
                if (menu.startTime != null && menu.endTime != null) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            painter = painterResource(R.drawable.baseline_access_time_24),
                            contentDescription = null,
                            tint = Color.Gray,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "${menu.startTime} - ${menu.endTime}",
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = Color.Gray,
                                fontSize = 12.sp
                            )
                        )
                    }
                }
                
                Spacer(modifier = Modifier.height(8.dp))
                Divider(color = Color(0xFFEEEEEE))
                Spacer(modifier = Modifier.height(8.dp))
                
                // Display categories count
                Text(
                    text = "${menu.categories.size} categorías",
                    style = MaterialTheme.typography.bodySmall.copy(
                        color = Color.Gray,
                        fontSize = 12.sp
                    )
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun MenuCardPreview() {
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
                orderByNumber = 1
            ),
            MenuCategory(
                id = "cat-2",
                name = "Platos Fuertes",
                description = "Platos principales",
                image = null,
                venueId = "venue-1",
                isActive = true,
                orderByNumber = 2
            )
        )
    )
    
    Surface {
        MenuCard(menu = sampleMenu)
    }
}
