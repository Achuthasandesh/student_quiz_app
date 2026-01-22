# Student Quiz App

A timed quiz application built with Jetpack Compose that fetches trivia questions from a public API. The app features a 10-second countdown timer per question, instant feedback, and state preservation across configuration changes. 

Apk is in the project level folder by name: [StudentQuizApp.apk](https://github.com/Achuthasandesh/student_quiz_app/blob/main/StudentQuizApp.apk)
## Screenshots

<p align="center">
  <img width="250" alt="Home screen" src="https://github.com/user-attachments/assets/f575a335-fbad-4259-9774-afd9c9651fa7" />
  <img width="250" alt="Quiz screen" src="https://github.com/user-attachments/assets/f38b80fa-b88f-4375-9a03-dc4cc9819a9d" />
  <img width="250" alt="Result screen" src="https://github.com/user-attachments/assets/61991db7-c62f-4629-baf0-28cae58ff939" />
</p>

<p align="center">
  <img width="600" alt="Quiz screen landscape" src="https://github.com/user-attachments/assets/30b79d0c-0fc9-4772-b405-f3fef02e9eeb" />
</p>

## Features

- Fetches 10 random trivia questions from [The Trivia API](https://the-trivia-api.com/)
- 10-second countdown timer per question
- Shuffled multiple-choice options
- Instant feedback on answer selection (correct/incorrect)
- Auto-advance after timeout
- Final score summary with question review
- State preservation on rotation and background/foreground transitions
- Timer continues correctly even when app is in background


## Project Structure

```
app/src/main/java/com/android/student_quiz_app/
├── QuizApplication.kt          # Application class with Koin initialization
├── MainActivity.kt             # Single Activity with Compose UI
├── data/
│   ├── model/
│   │   └── Question.kt         # Data models (API + Domain)
│   ├── remote/
│   │   └── TriviaApiService.kt # Retrofit API interface
│   └── repository/
│       └── QuizRepository.kt   # Data repository
├── di/
│   └── AppModules.kt           # Koin dependency modules
└── ui/
    ├── screens/
    │   ├── StartQuizScreen.kt  # Welcome screen
    │   ├── QuizScreen.kt       # Quiz gameplay + Loading + Error
    │   └── ResultScreen.kt     # Score summary
    ├── state/
    │   └── QuizState.kt        # Sealed class for UI states
    ├── viewmodel/
    │   └── QuizViewModel.kt    # Business logic + state management
    └── theme/
        └── ...                 # Material 3 theming
```

## Tech Stack

### Core
| Technology | Version | Purpose |
|------------|---------|---------|
| Kotlin | 2.0.21 | Primary language |
| Jetpack Compose | BOM 2024.09.00 | Declarative UI |
| Material 3 | Latest | UI components and theming |

### Architecture Components
| Library | Purpose |
|---------|---------|
| ViewModel | UI state holder, survives configuration changes |
| SavedStateHandle | State preservation across process death |
| StateFlow | Reactive state management |
| Coroutines | Asynchronous programming |

### Dependency Injection
| Library | Version | Purpose |
|---------|---------|---------|
| Koin Android | 3.5.6 | DI container |
| Koin Compose | 3.5.6 | Compose integration (`koinViewModel()`) |

### Networking
| Library | Version | Purpose |
|---------|---------|---------|
| Retrofit | 2.9.0 | REST API client |
| OkHttp | 4.12.0 | HTTP client with logging |
| Gson | 2.10.1 | JSON serialization |

### Other
| Library | Purpose |
|---------|---------|
| Kotlin Parcelize | Parcelable generation for state saving |
