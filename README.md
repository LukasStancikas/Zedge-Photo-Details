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
- additional features could be: 
  - add favorite toggle in the list item 
  - drop-down or dialog to select what to share, URL or raw photo data
  - full screen display of image on click inside details with finger controls
- Bug: when DB fetches, lets say 48 images, and network call finishes with first page (4 items), we scroll all the way to the bottom, and only then trigger page 2, when indeed its already page (48 / 4)
  - calculate how many pages to advances from the initial fetch (divide by exposed 4), or keep incrementing number if upsert returns false (nothing was added or updated)