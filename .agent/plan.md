# Project Plan

Set up a Compose app named 'Zedge-Photos-Details' with Hilt and a networking layer using Retrofit/Moshi. The app should connect to a public test API (like Unsplash or Picsum) that supports pagination. Focus on the API setup, DI, and a basic list display using Glide, but do not implement full pagination logic or the details screen yet. Ensure Material 3 and edge-to-edge support.

## Project Brief

# Zedge-Photos-Details Project Brief

## Features
- **Dynamic Photo Feed**: A vertically scrolling list screen that displays high-quality images and descriptions retrieved from a remote test API.
- **Material Design 3 UI**: A vibrant and energetic user interface featuring a full edge-to-edge display, strictly adhering to Material 3 guidelines.
- **Centralized Dependency Injection**: A scalable architecture using Hilt to manage API services and repository components.
- **Optimized Networking Layer**: A robust integration of Retrofit and Moshi to handle JSON data serialization and API requests with pagination support.

## High-Level Technical Stack
- **Language**: Kotlin
- **UI Framework**: Jetpack Compose (Material Design 3)
- **Dependency Injection**: Hilt
- **Networking**: Retrofit & Moshi
- **Image Loading**: Glide
- **Asynchrony**: Kotlin Coroutines & Flow
