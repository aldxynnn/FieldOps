# FieldOps

FieldOps is an Android field-service application built with Kotlin, Jetpack Compose, Material 3, Room, Retrofit, WorkManager, and offline-first local persistence.

## Current release scope

- Production-oriented Android application shell and enterprise UI
- Room-backed local Work Order, notes, photos, GPS evidence, activity history, notifications, and sync queue
- Offline-first status updates with WorkManager scheduling
- Search and filtering for Work Orders
- Customer / Site / Asset UI prepared for API data
- Retrofit API contract prepared for a future backend
- No demo/seed data is shipped in the production source

## Backend status

This repository intentionally does **not** contain a fake backend or a fake API URL. The backend service has not been provided yet. Until a real API is configured, the application operates as an offline local client and the remote sync worker safely performs no remote operation.

### Configure the real API

When the backend is available, set `FIELDOPS_API_BASE_URL` in `app/build.gradle.kts` to the HTTPS API base URL, then implement the authenticated API endpoints in `FieldOpsApi`. Do not commit passwords, API keys, JWT secrets, or service-account files.

The API contract should cover authentication, users, customers, sites, assets, work orders, status updates, notes, evidence uploads, GPS evidence, preventive maintenance, notifications, and sync operations.

## Build

Open the project in Android Studio and allow Gradle to resolve the configured dependencies.

Recommended verification commands:

- `./gradlew testDebugUnitTest`
- `./gradlew assembleDebug`

## Package

Application ID: `com.example.fieldops`
Minimum Android version: 24

## Backend

FieldOps sekarang menyertakan backend REST API pada folder `backend/`.

Requirement backend: Node.js 22.5+.

Jalankan dari PowerShell:

`cd backend`

`$env:JWT_SECRET="ganti-dengan-secret-random-yang-panjang"`

`$env:ADMIN_PASSWORD="ganti-password-admin"`

`npm start`

Backend berjalan pada `http://localhost:8080`.
Untuk Android Emulator, aplikasi debug menggunakan `http://10.0.2.2:8080/api/` secara default.
Untuk perangkat fisik, gunakan property Gradle `fieldopsApiBaseUrl` yang menunjuk ke IP komputer pada jaringan yang sama.

Default development login:

- Email: `admin@fieldops.local`
- Password: `ChangeMe123!`

Ganti kredensial tersebut sebelum penggunaan nyata.
