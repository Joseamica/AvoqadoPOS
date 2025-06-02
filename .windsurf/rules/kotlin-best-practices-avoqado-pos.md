---
trigger: model_decision
description: Deciding best practices
---

# Kotlin Best Practices for Avoqado POS

## Architecture & State Management
- Use unidirectional data flow with StateFlow in ViewModels and collect with collectAsStateWithLifecycle() in Composables
- Maintain clear separation between UI state (ViewModel) and UI rendering (Composables)
- Follow repository pattern with domain models distinct from data layer models
- Use suspend functions for async operations in repositories and UseCase classes

## Coroutines & Flow
- Implement proper error handling with coroutine exception handlers
- Use viewModelScope for ViewModel-bound operations
- Apply structured concurrency principles with supervisorScope for independent operations
- Consider Flow.shareIn() or stateIn() for expensive operations that need multiple observers

## WebSocket Management
- Create a resilient WebSocket manager with automatic reconnection strategy
- Buffer events during disconnection periods to maintain consistency
- Use state machines to model connection status for predictable behavior
- Implement proper teardown with lifecycle awareness to prevent leaks

## Compose UI
- Create stateless composables with state hoisting pattern
- Use rememberSaveable for state that survives configuration changes
- Optimize recomposition with remember(), stable keys, and immutable data structures
- Apply modifiers consistently (.then() pattern) for readable, composable UI code

## Error Handling
- Expand AvoqadoError hierarchy for more precise error handling
- Use sealed classes for representing different error states
- Implement consistent SnackbarDelegate event prioritization logic
- Consider Result<T> wrapper for potentially failing operations

## Testing
- Write unit tests for ViewModels with TestCoroutineDispatcher
- Mock repositories and data sources with interfaces
- Use test tags for Compose UI testing
- Create test fixtures for API responses

## Performance
- Use ViewModelScope for background processing
- Apply proper pagination patterns for large data sets
- Implement efficient list rendering with LazyColumn key strategies
- Optimize recomposition with remember and derived state

# Your rule content

- You can @ files here
- You can use markdown but dont have to