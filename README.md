# Zedge Photos Details 

## Features
- **Dynamic Photo Feed**: A vertically scrolling list screen that displays images and descriptions retrieved from a remote test API.
- **Paging Feed**:
- **Modularized**:
- **Material Design 3 UI**: A vibrant user interface featuring a full edge-to-edge display. 
- **Centralized Dependency Injection**: A scalable architecture using Hilt to manage API services and repository components.
- **Optimized Networking Layer**: A robust integration of Retrofit and Moshi to handle JSON data serialization and API requests with pagination support.

## High-Level Technical Stack
- **Language**: Kotlin
- **UI Framework**: Jetpack Compose (Material Design 3)
- **Dependency Injection**: Hilt
- **Networking**: Retrofit & Moshi
- **Image Loading**: Glide
- **Asynchrony**: Kotlin Coroutines & Flow
- **Persistence**: Room Db


struggles:
- landing on specific module hierarchy
- constructing everything around common Loadable.kt pattern
- designing reusable e2e scaffold, collapsing toolbar
- designing reusable pull refresh
- DB with suspension
- adding favorite made me change a lot, DB to preserve user favorited stuff, added suspend function to flows
to show instantly new state of favorited item
- Error states acting in a coherent UX way from initial load, refresh, load-more
- pagination with DB was interesting