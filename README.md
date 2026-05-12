# VizuaLearn AI

VizuaLearn AI is an intelligent Android application designed to help students and lifelong learners organize their study materials through AI-generated content. By leveraging the power of Google's Gemini AI, the app transforms static study sets into interactive Flashcards and Mind Maps.

## 🚀 Features

- **AI-Powered Generation:** Automatically generate study materials using the Gemini AI model.
- **Dynamic Study Sets:** Organize your learning into custom sets with progress tracking.
- **Interactive Flashcards:** Review and master topics with an intuitive flashcard interface.
- **Mind Map Visualization:** See your study topics organized visually to understand complex relationships.
- **Local Storage:** All your study sets and progress are saved locally using Room Database for offline access.

## 🛠️ Tech Stack

- **Language:** Kotlin
- **Architecture:** MVP (Model-View-Presenter) / Clean Architecture patterns
- **AI Integration:** Google Generative AI SDK (Gemini)
- **Database:** Room (SQLite)
- **Concurrency:** Kotlin Coroutines
- **UI Components:** Material Design 3, RecyclerView, ConstraintLayout

## ⚙️ Setup & Installation

To run this project, you will need to add your own API Key for the Gemini AI.

1. **Clone the repository:**

2. **Get a Gemini API Key:**
   Obtain your key from the [Google AI Studio](https://aistudio.google.com/).

3. **Configure Secrets:**
   In the root directory of the project, create a file named `secrets.properties` and add your key:
   
   
4. **Build the project:**
   Open the project in Android Studio and let Gradle sync.

## 📱 Screenshots

| Home / Study Sets | Flashcard View | Mind Map |
| :---: | :---: | :---: |
| *[Add Screenshot]* | *[Add Screenshot]* | *[Add Screenshot]* |

## 📄 License

This project is for educational purposes as part of a University curriculum.
