# 🚀 MediBudget Native Android Application

Welcome to the native Android implementation of **MediBudget**, a production-grade, state-of-the-art healthcare cost intelligence application built using **Android Studio, Kotlin, Jetpack Compose, MVVM Architecture, Room SQLite Database (offline caching), and the Supabase Kotlin SDK**.

---

## 🎨 Technology Stack
* **UI Framework**: 100% Jetpack Compose (Declarative UI)
* **Design Guidelines**: Material 3 Design paired with Custom HSL Glassmorphic containers, emerald-teal gradients, and infinite breathing pulse animations.
* **Language**: Kotlin 1.9.22 (Modern, safe, and asynchronous)
* **Local Caching (Offline First)**: Room Database with coroutine pre-population assets seeds.
* **Authentication & Backend Sync**: Supabase GoTrue Auth & Postgrest client SDK.
* **Location Queries**: OpenStreetMap Nominatim reverse-geocoder (via Retrofit converter calls).
* **AI Diagnostics**: Local smart NLP classification engine paired with Gemini AI.
* **OCR Medicine Scanner**: CameraX API wrapping Google ML Kit Text Recognition.

---

## 📂 Codebase File Index

```
c:\MediBudget Android App
├── build.gradle.kts                      # Project-level gradle configurations
├── settings.gradle.kts                   # Repositories & Jitpack module registry
├── gradle.properties                     # JVM & Gradle build caching optimizations
└── app
    ├── build.gradle.kts                  # App-module packages, KSP, and compiler options
    └── src
        └── main
            ├── AndroidManifest.xml       # Permissions (Camera, Fine Location, Internet)
            ├── assets                    # Pre-populated SQLite DB JSON files
            │   ├── medicines.json
            │   ├── hospitals.json
            │   ├── government_schemes.json
            │   └── insurance_providers.json
            └── java/com/medibudget/app
                ├── MainActivity.kt        # App launcher entry activity
                ├── data
                │   ├── local
                │   │   ├── dao/DAOs.kt   # Database DAOs (Medicines, Hospitals, Schemes)
                │   │   ├── database/AppDatabase.kt # Seeding Room database callback
                │   │   └── entity/Entities.kt # Room schema structure models
                │   ├── remote
                │   │   ├── LocationApi.kt # Nominatim Retrofit requests
                │   │   └── NetworkClient.kt # Supabase & Retrofit initializers
                │   └── repository
                │       ├── AuthRepository.kt # User credential transactions
                │       └── HealthRepository.kt # Calculations, reverse geocoding, AI chatbot
                ├── ui
                │   ├── components
                │   │   └── PremiumComponents.kt # Reusable UI widgets
                │   ├── navigation
                │   │   ├── ScreenRoute.kt # Sealed destinations index
                │   │   └── NavGraph.kt    # Compose Navigation router
                │   ├── screens            # Modern Jetpack Compose UI Screens
                │   │   ├── SplashAndOnboarding.kt
                │   │   ├── AuthScreens.kt
                │   │   ├── DashboardAndSettings.kt
                │   │   ├── SymptomChatScreen.kt
                │   │   ├── CostEstimatorScreen.kt
                │   │   ├── OCRScannerScreen.kt
                │   │   ├── MapFinderScreen.kt
                │   │   ├── SchemeCheckerScreen.kt
                │   │   ├── SOSModeScreen.kt
                │   │   └── AdminAndReportsScreen.kt
                │   └── theme              # Design system styling tokens
                │       ├── Color.kt
                │       ├── Type.kt
                │       └── Theme.kt
                └── utils
                    └── SessionManager.kt  # User states & biometrics cache controller
```

---

## 🛠️ Step-by-Step Developer Setup

Follow these instructions to preview, run, or compile the application:

### Prerequisites
* **Android Studio**: Install the latest stable version of Android Studio (Hedgehog or higher recommended).
* **Java Development Kit**: JDK 17 is required (automatically bundled with Android Studio).

### Setup & Gradle Sync
1. Launch Android Studio.
2. Select **File -> Open** and choose the directory: `c:\MediBudget Android App`.
3. Allow Android Studio to import the Gradle wrapper project structures.
4. Open the Gradle terminal and click **Sync Project with Gradle Files** in the top bar.

### Running the App
* **Android Emulator**: In the top toolbar, select your virtual device in the dropdown menu and press the **Run 'app'** button (green play icon).
* **Physical Device**: Connect a physical Android smartphone via USB with *Developer Options* and *USB Debugging* enabled. Choose your connected device name and press the **Run** button.

---

## ⚙️ Modifying API Endpoint Keys

All remote credential keys are loaded in [NetworkClient.kt](file:///c:/MediBudget%20Android%20App/app/src/main/java/com/medibudget/app/data/remote/NetworkClient.kt). You can modify them directly inside the file:

* **Supabase Project URL**: Update `SUPABASE_URL` to match your updated Supabase Edge instance.
* **Supabase Publishable Client Key**: Update `SUPABASE_KEY` to match your project secret JWT signing key.

---

## 🔐 Advanced Device Security
The application includes extra layers of mobile client security:
* **Android BiometricPrompt**: If enabled in settings, the app uses biometric fingerprint verification to bypass password prompts on return visits.
* **Root Detection (`RootBeer`)**: Evaluates system paths to prevent operational hazards on compromised or jailbroken devices.
