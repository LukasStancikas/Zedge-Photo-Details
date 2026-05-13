# Zedge Photos Details 

## Features
- **Dynamic Photo Feed**: A vertically scrolling list screen that displays images and descriptions retrieved from a remote test API.
- **Data retrieval**: Repository implementation handles loading network responses in batches (with insertion to database, while database gives a stream of photos 
- **Modularized**:
  - `:core:common` - module used by all (Loadable for better results, UI util functions)
  - `:core:domain` - provide domain object and expected repository API
  - `:core:database`, `:core:network` - for actual repositories
  - `:core:data` - to connect network with db and give a specific implementation of repository
  - `:core:ui` - reusable composables
  - `:feature:list` - list screen
  - `:feature:details` - details screen 
- **Material Design 3 UI**: Full edge-to-edge display support. 

## High-Level Technical Stack
- **Language**: Kotlin
- **UI Framework**: Jetpack Compose (Material Design 3)
- **Dependency Injection**: Hilt
- **Networking**: Retrofit, Moshi
- **Image Loading**: Glide
- **Asynchrony**: Kotlin Coroutines, Flow
- **Persistence**: Room DB
- **Testing**: Turbine, Mockito

## Hurdles when doing the task
- Deciding on specific module hierarchy
- Utilizing the Loadable.kt pattern (might have gone for kotlin.Result + boolean)
- Designing reusable e2e scaffold, collapsing toolbar, exposing only limited canvas for content  
- Designing reusable pull refresh (PullRefreshBox didn't have `isEnabled`)
- DB with suspension was first approach, but it didn't work to update live changes from details page
- Combining network updates with database, pull refresh and paging
- Error states acting in a coherent UX way from initial load, refresh, load-more
- At first the UiState drove the current page, but it didn't sync with network and db truths together, so I made it be handled by repository, 
- Additional features could be: 
  - Add favorite toggle in the list item 
  - Drop-down or dialog to select what to share, URL or raw photo data
  - Full screen display of image on click inside details with finger controls
  - Update all dependency versions

## TODO
- handle relevant dispatchers for io/main for db + network
- don't clear items on refresh (or preserve favorite at least)
- dynamic paging fetching from DB, maybe batch size will be different depending on screen size
- load items not on last item seen but earlier (scroll position or earlier item seen, but trigger faster)
- unstable parameters → direct viewmodel lambdas
- Why Channel not SharedFlow for effect, how to avoid losing missed effects (store them until it is used)
