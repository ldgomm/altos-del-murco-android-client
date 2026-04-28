# Altos del Murco — Android Client

Native Android client for **Altos del Murco**, a restaurant and adventure reservation experience built with Kotlin, Jetpack Compose, Firebase, Hilt, Room, and a Clean Architecture + MVVM structure.

The app lets clients explore the restaurant menu, place immediate or scheduled food orders, build adventure experiences, add food to adventure reservations, manage bookings, view loyalty rewards, and maintain their profile from one polished mobile experience.

> This repository contains the **Android customer app**. It is designed to work together with the Altos del Murco iOS client and the administrative/back-office system.

---

## Table of Contents

- [Overview](#overview)
- [Core Features](#core-features)
- [Tech Stack](#tech-stack)
- [Architecture](#architecture)
- [Project Structure](#project-structure)
- [Main App Flows](#main-app-flows)
- [Firebase Collections](#firebase-collections)
- [Local Persistence](#local-persistence)
- [Setup](#setup)
- [Running the App](#running-the-app)
- [Testing](#testing)
- [Security Notes](#security-notes)
- [Roadmap](#roadmap)
- [Contributing](#contributing)
- [License](#license)

---

## Overview

**Altos del Murco** combines a restaurant ordering flow with outdoor experience reservations. The Android client is focused on the end user:

- Discover featured posts and promotions.
- Browse restaurant dishes by category.
- Add menu items to a local cart.
- Apply loyalty rewards automatically.
- Submit immediate restaurant orders.
- Schedule future food-only reservations.
- Build adventure reservations with activities, packages, food, dates, and time slots.
- Track restaurant orders and adventure bookings.
- View profile stats, loyalty level, active benefits, and account actions.

The app uses Firebase as the cloud backend and stores selected local state, such as cart drafts and preferences, on device.

---

## Core Features

### Authentication and Session

- Google Sign-In with Firebase Authentication.
- Firebase session verification and automatic sign-out when a user is disabled, deleted, or has an invalid/expired token.
- Mandatory profile completion before entering the main app shell.
- Profile fields for client identity, national ID, phone, birthday, address, and emergency contact.
- Account deletion with recent Google reauthentication.

### Home

- Premium home dashboard for the customer experience.
- Featured posts feed from Firestore.
- Featured dish cards.
- Experience package cards.
- Reward entry points.
- Deep links from Home into restaurant, experiences, bookings, and profile flows.

### Restaurant

- Real-time Firestore menu observation.
- Ordered menu categories:
  1. Entradas
  2. Sopas
  3. Platos Fuertes
  4. Extras
  5. Postres
  6. Bebidas
  7. Bebidas Alcohólicas
- Menu item detail screen with reward preview and final displayed price.
- Local cart draft persistence using Room.
- Immediate orders and scheduled food-only reservations.
- Trusted server-side-style validation in repository layer before writing orders.
- Stock consumption for current orders.
- Future reservations without immediate stock decrement.
- Order success and order history screens.
- Loyalty rewards reserved when applied to an order.

### Experiences / Adventure

- Firestore-driven adventure activity catalog.
- Supported activity model:
  - Off-road 4x4
  - Paintball
  - Go karts
  - Shooting range
  - Camping
  - Extreme slide
- Featured adventure packages.
- Combo builder with selected activities, date, available slots, pricing, package discounts, and loyalty rewards.
- Food picker sheet that allows adding restaurant items to adventure reservations.
- Pricing policy that keeps valid combo discounts while pricing extra activities separately.
- Adventure booking creation with blocks, subtotals, discounts, food totals, loyalty discounts, and final total.
- Availability slot generation based on selected day, duration, activities, food, and catalog configuration.

### Bookings

- Customer booking list for adventure reservations.
- Reservation status support:
  - Pending
  - Confirmed
  - Completed
  - Canceled
- Cancellation policy layer.
- Firestore observation by national ID.

### Profile and Loyalty

- Premium profile dashboard.
- Profile image upload and cache.
- Firebase Storage integration for profile images.
- Theme preferences.
- Loyalty level calculation from completed restaurant orders and adventure bookings.
- Active reward templates and wallet snapshot.
- Restaurant and adventure reward engines.
- Support for reward rules such as:
  - Most expensive menu item percentage discount.
  - Specific menu item percentage discount.
  - Activity percentage discount.
  - Free menu item.
  - Buy X get Y free.

---

## Tech Stack

| Layer | Technology |
|---|---|
| Language | Kotlin |
| UI | Jetpack Compose, Material 3 |
| Architecture | Clean Architecture, MVVM |
| Dependency Injection | Hilt |
| Navigation | Navigation Compose |
| Async | Kotlin Coroutines, Flow, StateFlow, SharedFlow |
| Authentication | Firebase Authentication, Google Sign-In / Credential Manager |
| Database | Cloud Firestore |
| Storage | Firebase Storage |
| Local Persistence | Room, DataStore Preferences |
| App Entry | `AltosApplication`, `MainActivity` |
| Testing | JUnit, Android Instrumented Tests |

---

## Architecture

The project follows a feature-first Clean Architecture style:

```text
presentation/
  view/
  viewmodel/

domain/
  models
  repositories/contracts
  use cases
  business rules

data/
  dto
  repositories
  mappers
  local/remote persistence
```

Each feature owns its UI, state, business contracts, and repository implementation where appropriate. Shared infrastructure lives under `util` and dependency injection modules live under `di`.

### Main architectural principles

- UI observes immutable state from ViewModels.
- ViewModels coordinate use cases and repositories.
- Domain models stay independent from Firebase DTOs.
- Firestore DTOs map into domain models before reaching UI.
- Repository interfaces define contracts used by use cases.
- Hilt provides concrete implementations.
- Firebase listeners are exposed as Kotlin Flows.
- Long-running operations use coroutines.
- Cart and preferences are persisted locally.

---

## Project Structure

```text
app/src/main/java/com/premierdarkcoffee/tourism/altosdelmurco/
├── AltosApplication.kt
├── MainActivity.kt
├── root/
│   └── feature/
│       ├── altos/
│       │   ├── authentication/
│       │   │   ├── data/
│       │   │   ├── domain/
│       │   │   └── presentation/
│       │   ├── home/
│       │   │   ├── data/
│       │   │   ├── domain/
│       │   │   └── presentation/
│       │   ├── restaurant/
│       │   │   ├── data/
│       │   │   │   ├── local/
│       │   │   │   └── remote/
│       │   │   ├── domain/
│       │   │   └── presentation/
│       │   ├── adventure/
│       │   │   ├── data/
│       │   │   ├── domain/
│       │   │   └── presentation/
│       │   ├── booking/
│       │   │   ├── data/
│       │   │   ├── domain/
│       │   │   └── presentation/
│       │   └── profile/
│       │       ├── data/
│       │       ├── domain/
│       │       └── presentation/
│       └── di/
└── util/
    ├── constant/
    ├── database/
    ├── extension/
    ├── navigation/
    ├── theme/
    └── ui/
```

---

## Main App Flows

### App Startup

```text
MainActivity
  → AltosApp
    → AuthGateRoute
      → AuthenticationScreen
      → CompleteProfileScreen
      → AltosMainShell
```

### Main Shell Tabs

```text
Inicio
Restaurante
Experiencias
Reservas
Perfil
```

The shell uses Navigation Compose and preserves top-level tab state when navigating between destinations.

### Restaurant Order Flow

```text
RestaurantScreen
  → MenuListScreen
  → MenuItemDetailScreen
  → CartScreen
  → CheckoutScreen
  → OrderSuccessScreen
```

The cart is backed by Room, allowing the order draft to survive process recreation and navigation changes.

### Adventure Booking Flow

```text
AdventureScreen
  → Catalog / package selection
  → Combo builder
  → Date and slot selection
  → Optional restaurant food picker
  → Booking confirmation
```

Adventure pricing is calculated from catalog configuration, selected activities, package discounts, loyalty rewards, and optional food reservation items.

### Profile Flow

```text
ProfileScreen
  → Dashboard
  → Edit profile
  → Loyalty / benefits
  → Preferences
  → Support
  → Account actions
```

Profile image upload uses Firebase Storage and local cache files.

---

## Firebase Collections

The Android client expects the following Firestore collections:

| Collection | Purpose |
|---|---|
| `clients` | Client profile documents keyed by Firebase user ID. |
| `restaurant_menu_items` | Restaurant menu catalog. |
| `restaurant_orders` | Restaurant orders and scheduled food reservations. |
| `adventure_activities` | Firestore-driven adventure activity catalog. |
| `adventure_featured_packages` | Featured adventure packages and combo definitions. |
| `adventure_bookings` | Adventure reservations. |
| `client_loyalty_wallets` | Loyalty wallet snapshots by national ID. |
| `loyalty_reward_templates` | Automatic and manual reward template definitions. |
| `posts` | General post collection constant. |
| `featured_posts` | Home featured media feed used by the Android/iOS client experience. |

> Keep collection names synchronized with the iOS client and admin/back-office app to avoid persistence differences between platforms.

---

## Local Persistence

### Room

Room is used for the restaurant cart draft:

- `CartDraftEntity`
- `CartItemEntity`
- `CartDao`
- `AltosDatabase`

Database name:

```text
altos_database
```

Current database version:

```text
3
```

### DataStore

DataStore Preferences stores app-level preferences such as theme mode:

```text
app_preferences
```

---

## Setup

### Prerequisites

- Android Studio
- JDK compatible with your Android Gradle Plugin version
- Firebase project
- Google Sign-In configured in Firebase Authentication
- Firestore enabled
- Firebase Storage enabled
- Android package registered in Firebase:

```text
com.premierdarkcoffee.tourism.altosdelmurco
```

### 1. Clone the repository

```bash
git clone https://github.com/ldgomm/altos-del-murco-android-client.git
cd altos-del-murco-android-client
```

### 2. Add Firebase configuration

Download `google-services.json` from Firebase Console and place it here:

```text
app/google-services.json
```

Do not commit production Firebase secrets or private environment files.

### 3. Configure Google Sign-In

In Firebase Console:

1. Enable **Authentication → Sign-in method → Google**.
2. Register the Android app package.
3. Add the SHA-1 and SHA-256 fingerprints for your debug and release keystores.
4. Download a fresh `google-services.json` after changes.

Useful commands:

```bash
./gradlew signingReport
```

### 4. Sync Gradle

Open the project in Android Studio and sync Gradle.

### 5. Run the app

```bash
./gradlew installDebug
```

Or run directly from Android Studio.

---

## Running the App

For local development:

```bash
./gradlew assembleDebug
```

For release builds:

```bash
./gradlew assembleRelease
```

For static checks, use the Gradle tasks available in your project:

```bash
./gradlew tasks
```

---

## Testing

Run local unit tests:

```bash
./gradlew test
```

Run Android instrumented tests:

```bash
./gradlew connectedAndroidTest
```

The current test structure includes:

```text
app/src/test/
app/src/androidTest/
```

---

## Security Notes

This client should be paired with strict Firebase Security Rules. At minimum:

- Only authenticated users should read/write their own `clients/{uid}` profile.
- Users should not be allowed to impersonate another `clientId` or `nationalId`.
- Menu and catalog collections should usually be read-only for clients.
- Order and booking writes should validate trusted fields server-side or through rules.
- Loyalty rewards should not be blindly trusted from client payloads.
- Admin-only collections and fields should be writable only from the admin app or trusted backend.
- Firebase Storage profile images should be scoped by authenticated user ID.
- Release builds should use a properly configured signing key and Firebase SHA fingerprints.
- App Check is recommended for Firestore and Storage abuse reduction.

Client-side checks improve UX, but they are not a replacement for Firestore and Storage rules.

---

## Recommended Firestore Indexes

Depending on production query volume, create indexes for queries involving:

- `restaurant_orders.nationalId`
- `restaurant_orders.scheduledDayKey`
- `restaurant_orders.status`
- `adventure_bookings.nationalId`
- `adventure_bookings.startDayKey`
- `adventure_bookings.status`
- `featured_posts.isVisible + expiresAt`
- `loyalty_reward_templates.isActive`
- `client_loyalty_wallets.nationalId`

Firestore will show direct index creation links in logs when a required composite index is missing.

---

## Roadmap

Recommended next improvements:

- Add screenshot previews to this README.
- Add CI with Gradle build, unit tests, and lint.
- Add Firebase App Check.
- Add stricter Firestore Security Rules documentation.
- Add release signing documentation.
- Add production/provisioning environment separation.
- Add UI tests for authentication, cart, checkout, and booking creation.
- Add analytics for home card taps, menu conversion, reservation conversion, and reward usage.
- Add Crashlytics and structured logging.
- Add offline-first handling for menu and adventure catalog.
- Add deep links for specific dishes, packages, posts, and bookings.
- Add accessibility audit for all major screens.

---

## Suggested Screenshots Section

Add screenshots under a `/docs/screenshots` folder and update this section:

```text
docs/screenshots/home.png
docs/screenshots/restaurant.png
docs/screenshots/experiences.png
docs/screenshots/bookings.png
docs/screenshots/profile.png
```

Example:

```md
## Screenshots

| Home | Restaurant | Experiences |
|---|---|---|
| ![Home](docs/screenshots/home.png) | ![Restaurant](docs/screenshots/restaurant.png) | ![Experiences](docs/screenshots/experiences.png) |
```

---

## Development Notes

- Keep Android and iOS models synchronized.
- Keep Firestore field names compatible between the client apps and admin app.
- Avoid writing admin-only fields from the client.
- Keep reward computation consistent across restaurant and adventure flows.
- Treat `nationalId` as sensitive user data.
- Avoid logging full user identifiers in production.
- Validate all prices and discounts before persistence.
- Prefer domain models in UI and DTOs only at data boundaries.

---

## Contributing

1. Create a feature branch.
2. Keep changes scoped by feature.
3. Run tests before opening a pull request.
4. Avoid committing generated exports, local configs, secrets, or build artifacts.
5. Document Firestore schema changes when modifying DTOs.

```bash
git checkout -b feature/my-change
./gradlew test
git add .
git commit -m "Describe change"
git push origin feature/my-change
```

---

## License

Private project for **Altos del Murco**.

All rights reserved unless a different license is added to this repository.
