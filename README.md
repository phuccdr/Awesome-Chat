# Awesome Chat 2026

A modern, multi-module Android chat application built with Kotlin and the latest Jetpack components. This project serves as a robust foundation for building scalable, maintainable, and high-performance chat experiences.

## 🚀 Features

- **Authentication**: Secure login and registration flows.
- **Real-time Messaging**: Instant message delivery and receipt status using Firebase.
- **Media Sharing**: Support for sending images (Gallery integration) and stickers.
- **Conversation Management**: Easily manage and search through your chat history.
- **Friend System**: Manage friend requests, search for users, and maintain a friend list.
- **User Profile**: Customizable user profiles with edit functionality.
- **Adaptive UI**: Built with a focus on responsiveness and modern design principles.

## 🛠 Tech Stack

- **Language**: [Kotlin](https://kotlinlang.org/)
- **Dependency Injection**: [Hilt](https://developer.android.com/training/dependency-injection/hilt-android)
- **Networking**: [Retrofit](https://square.github.io/retrofit/) & OkHttp
- **Database/Backend**: [Firebase](https://firebase.google.com/) (Authentication, Firestore)
- **Image Loading**: [Glide](https://github.com/bumptech/glide)
- **Architecture Components**:
  - ViewModel, LiveData, Flow, Coroutines
  - Navigation Component (Single Activity Architecture)
  - Data Binding
- **Logging**: [Timber](https://github.com/JakeWharton/timber)
- **Static Analysis**: [SonarQube](https://www.sonarqube.org/)
- **CI/CD**: Jenkins, Fastlane

## 🏗 Architecture

The project follows a **Multi-Module MVVM (Model-View-ViewModel)** architecture to ensure separation of concerns and build scalability.

### Module Structure

- **`:app`**: The main entry point, handling application configuration and top-level navigation.
- **`:features:*`**: Feature-specific modules (Chat, Auth, Conversation, Friends, Profile) containing UI and business logic.
- **`:libraries:core`**: Base classes, utility functions, common models, and global DI modules (Network, Firebase).
- **`:libraries:permission`**: Dedicated library for simplified Android permission handling.
- **`:features:initdata`**: Tools for seeding initial data and migrations.

## 🏁 Getting Started

### Prerequisites

- Android Studio Koala or newer.
- JDK 17.
- A Firebase project (add `google-services.json` to the `:app` module).

### Installation

1.  **Clone the repository**:
    ```bash
    git clone <repository-url>
    ```
2.  **Configuration**:
    - Update `rootProject.name` in `settings.gradle.kts` if needed.
    - Replace the `google-services.json` file in the `app/` directory with your own.
    - Configure your keystore in `build.gradle.kts` for release builds.
3.  **Build**:
    Open the project in Android Studio and sync with Gradle files.

## 📈 Development & Quality

- **SonarQube**: Run `./gradlew sonar` to perform static code analysis.
- **CI/CD**: The project includes `Jenkinsfile` and `fastlane` configurations for automated builds and distribution to Firebase App Distribution.

---

**Author**: VietBH
