# Avoqado POS

## Short Description

Avoqado POS is a native Android Point of Sale (POS) application designed for restaurants, cafes, and other venues. It provides staff with the tools to manage tables, take customer orders, process payments efficiently, and handle daily shifts. The application is built to streamline venue operations and enhance the customer experience.

## Tech Stack

*   **Programming Language**: [Kotlin](https://kotlinlang.org/)
*   **UI Framework**: [Jetpack Compose](https://developer.android.com/jetpack/compose)
*   **Architecture**: MVVM (Model-View-ViewModel)
*   **Networking**: [Retrofit](https://square.github.io/retrofit/), [OkHttp](https://square.github.io/okhttp/)
*   **Database**: [Room](https://developer.android.com/training/data-storage/room)
*   **Real-time Communication**: [Socket.IO](https://socket.io/)
*   **Dependency Injection**: Hilt (inferred from common practice, not explicitly seen)
*   **Asynchronous Programming**: Kotlin Coroutines
*   **Image Loading**: Coil (inferred from common practice, not explicitly seen)
*   **Analytics & Crash Reporting**: [Firebase Analytics](https://firebase.google.com/products/analytics), [Firebase Crashlytics](https://firebase.google.com/products/crashlytics)
*   **Barcode Scanning**: [Google ML Kit](https://developers.google.com/ml-kit/vision/barcode-scanning)
*   **QR Code Generation**: [compose-qr-code](https://github.com/lightsparkdev/compose-qr-code)

## Key Features

*   **User Authentication**: Secure login for staff members.
*   **Menu Management**: View and manage menu items and categories.
*   **Order Management**: Create, modify, and track customer orders.
*   **Cart Functionality**: Add items to a cart for a seamless checkout process.
*   **Payment Processing**: Handle payments, including QR code-based payments.
*   **Shift Management**: Start, end, and manage work shifts for staff.
*   **Customer Reviews**: Collect and view customer feedback.

## Project Structure

The project follows a modular, feature-based architecture:

```
.AvoqadoPOS/
├── app/                # Main application module
│   ├── src/main/
│   │   ├── java/com/avoqado/pos/
│   │   │   ├── core/       # Shared business logic, data models, and utilities
│   │   │   ├── di/         # Dependency injection setup
│   │   │   ├── features/   # Feature-specific modules (e.g., auth, menu, payment)
│   │   │   ├── ui/         # Shared UI components and themes
│   │   │   └── utils/      # Utility functions and helpers
│   │   └── res/          # Android resources (layouts, drawables, etc.)
│   └── build.gradle.kts # App-level Gradle build script
├── build.gradle.kts    # Project-level Gradle build script
├── gradle.properties   # Project-wide Gradle settings
└── settings.gradle.kts # Gradle settings for included modules
```

## Getting Started

### Prerequisites

*   [Android Studio](https://developer.android.com/studio) (latest stable version recommended)
*   JDK 1.8 or higher
*   Android SDK

### Installation & Setup

1.  **Clone the repository**:

    ```bash
    git clone <your-repository-url>
    cd AvoqadoPOS
    ```

2.  **Open in Android Studio**:

    *   Open Android Studio.
    *   Select "Open an existing Android Studio project".
    *   Navigate to the cloned `AvoqadoPOS` directory and open it.

3.  **Install Dependencies**:

    Gradle will automatically sync and download the required dependencies when you open the project. If it doesn't, you can trigger a manual sync by going to `File > Sync Project with Gradle Files`.

4.  **Environment Variables**:

    If the project requires API keys or other sensitive information, create a `local.properties` file in the root of the project and add the necessary variables. You may find a `local.properties.example` file with the required keys.

## Usage

To run the application, you can either use an Android emulator or a physical device:

1.  **Select a run configuration** from the dropdown menu in Android Studio's toolbar.
2.  **Choose a device** (either a running emulator or a connected physical device).
3.  **Click the 'Run' button** (green play icon) or use the shortcut `Shift + F10`.

Alternatively, you can build and install the app using Gradle from the command line:

```bash
./gradlew installDebug
```

## Running Tests

To run the automated tests for the project, execute the following command in the root directory:

```bash
./gradlew test
```

This will run all unit tests in the project. To run instrumented tests, you'll need a connected device or emulator and can use the `connectedAndroidTest` task.

## License

This project is currently unlicensed.
