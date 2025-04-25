package com.avoqado.pos.core.presentation.components

import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier

@Composable
fun <T> InfiniteScrollList(
    modifier: Modifier = Modifier,
    loading: Boolean = false,
    listState: LazyListState = rememberLazyListState(),
    items: List<T>,
    itemKey: (T) -> Any,
    itemContent: @Composable (T) -> Unit,
    loadingItem: @Composable () -> Unit,
    loadMore: () -> Unit,
    hasMorePages: Boolean = true, // Add this parameter
) {
    val reachedBottom: Boolean by remember { derivedStateOf { listState.reachedBottom() } }

    // Only load more if scrolled to bottom AND more pages are available
    LaunchedEffect(reachedBottom) {
        if (reachedBottom && hasMorePages && !loading) loadMore()
    }

    LazyColumn(modifier = modifier, state = listState) {
        items(
            items = items,
            key = { item: T -> itemKey(item) },
        ) { item ->
            itemContent(item)
        }

        if (loading) {
            item {
                loadingItem()
            }
        }
    }
}

internal fun LazyListState.reachedBottom(buffer: Int = 1): Boolean {
    val lastVisibleItem = this.layoutInfo.visibleItemsInfo.lastOrNull()
    return lastVisibleItem?.index != 0 && lastVisibleItem?.index == this.layoutInfo.totalItemsCount - buffer
}
