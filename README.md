# FieldOps

FieldOps is an enterprise-oriented Android field-service application designed to support field technicians and service operations across work order management, field execution, evidence collection, offline operations, synchronization, and operational monitoring.

The application is built with Kotlin and Jetpack Compose and uses an offline-first approach to keep core field operations available when network connectivity is limited or temporarily unavailable.

## Features

### Work Order Management

* Work order listing
* Search and filtering
* Work order detail
* Work order status workflow
* Technician assignment
* Customer and site information
* Activity history
* Technician notes
* Photo evidence
* GPS/location evidence
* Offline action queue

### Customers and Assets

* Customer list and detail
* Site information
* Asset detail
* Asset service history
* Preventive maintenance information

### Offline-First Operations

FieldOps is designed for field environments where reliable connectivity cannot always be assumed.

* Room-backed local persistence
* Offline work order operations
* Offline action queue
* Background synchronization with WorkManager
* Remote and local data synchronization
* Safe operation when the backend is temporarily unavailable

### Notifications

* Persistent notification storage
* Read/unread notification state
* Work order-related notifications
* Notification management from the application shell

### Role-Aware Operations

FieldOps supports role-aware operational workflows.

Supported roles include:

* Administrator
* Manager
* Dispatcher
* Technician

Technicians receive work orders assigned to their account, while management-level users can access work orders within their organization.

## Architecture

FieldOps follows an MVVM architecture with a separation between UI, presentation logic, data access, local persistence, and remote services.

```text
FieldOps Android Application
│
├── Jetpack Compose UI
│   ├── Dashboard
│   ├── Work Orders
│   ├── Customers
│   ├── Assets
│   ├── Activity History
│   ├── Notifications
│   ├── Profile & Settings
│   └── Management
│
├── ViewModels
│
├── Repository Layer
│   ├── Local Data
│   └── Remote API
│
├── Room Database
│   ├── Work Orders
│   ├── Notes
│   ├── Photos
│   ├── GPS Evidence
│   ├── Notifications
│   └── Offline Action Queue
│
├── Retrofit
│   └── REST API
│
└── WorkManager
    └── Background Synchronization
```

## Technology Stack

### Android

* Kotlin
* Jetpack Compose
* Material 3
* MVVM
* Kotlin Coroutines
* Kotlin Flow
* Room
* Retrofit
* WorkManager
* Hilt
* JUnit
* Android Instrumentation Tests

### Backend

* Node.js
* REST API
* JWT authentication
* SQLite
* Node.js built-in SQLite support

## Backend

The FieldOps repository includes the backend service in:

```text
backend/
```

### Requirements

* Node.js 22.5+
* npm

The backend has been verified with Node.js 24.20.0.

### Start the Backend

From the project root:

```powershell
cd backend
npm start
```

The backend starts on:

```text
http://localhost:8080
```

For local development, configure the required environment variables before starting the server:

```powershell
$env:JWT_SECRET="replace-with-a-long-random-secret"
$env:ADMIN_PASSWORD="replace-with-a-secure-admin-password"
npm start
```

Do not commit real passwords, JWT secrets, API keys, or other sensitive credentials.

### Android Emulator

The debug Android configuration uses:

```text
http://10.0.2.2:8080/api/
```

This allows the Android Emulator to access the backend running on the development machine.

### Physical Android Device

When using a physical Android device, configure the debug API base URL to use the development computer's local network IP address.

The device and development computer must be connected to the same network.

## Android Configuration

Application ID:

```text
com.example.fieldops
```

Minimum Android version:

```text
Android 7.0 / API 24
```

The current debug configuration supports local HTTP communication with the development backend.

For production deployment, the remote API should use HTTPS and production-grade network security configuration.

## Development Credentials

The backend provides a development administrator account for local development.

Development credentials should only be used for local development and demonstration.

Replace development credentials before any real deployment.

## Testing

The project contains unit tests and Android instrumentation/UI tests.

Recommended verification commands:

```text
./gradlew test
./gradlew assembleDebug
./gradlew connectedDebugAndroidTest
```

The current release has been verified with:

* Debug build successfully assembled
* Unit test suite successfully executed
* Android instrumentation test suite successfully executed
* 6 Android instrumentation tests passed
* Backend successfully started on port 8080

## Release

Current release:

**v1.0.0**

The `v1.0.0` tag represents the current FieldOps portfolio baseline.

## Project Structure

```text
FieldOps/
│
├── app/
│   └── src/
│       ├── main/
│       ├── test/
│       └── androidTest/
│
├── backend/
│   ├── server.js
│   ├── package.json
│   └── ...
│
├── gradle/
├── build.gradle.kts
├── settings.gradle.kts
├── gradlew
├── gradlew.bat
└── README.md
```

## Design and Engineering Goals

FieldOps was developed with an emphasis on:

* Enterprise-style field-service workflows
* Offline-first reliability
* Role-aware access and operations
* Technician-focused execution workflows
* Persistent operational data
* Background synchronization
* Evidence-based work order execution
* Maintainable Android architecture
* Consistent enterprise user experience
* Separation between Android client and REST backend

## Security Considerations

This repository is intended as a development and portfolio project.

Before production deployment:

* Replace all development credentials
* Use HTTPS for remote API communication
* Generate a strong random JWT secret
* Store secrets using secure environment/configuration management
* Review authentication and authorization policies
* Configure production database and backup strategy
* Remove development-only configuration
* Review Android network security configuration
* Apply appropriate production logging and monitoring

## License

This project is currently provided as a portfolio project.
