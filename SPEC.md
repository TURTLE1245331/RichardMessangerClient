# RichardMessanger - Specification Document

## 1. Project Overview

**Project Name:** RichardMessanger
**Type:** Kotlin Compose Multiplatform Application (Windows Desktop + Android)
**Core Functionality:** Real-time chat client with server-based messaging and persistent login

## 2. Technology Stack & Choices

- **Framework:** Kotlin Compose Multiplatform (KMP)
- **UI Framework:** Jetpack Compose with Material 3
- **Networking:** Ktor Client 3.0.1
- **Storage:** multiplatform-settings 1.2.0
- **Serialization:** kotlinx-serialization-json 1.7.3
- **Coroutines:** kotlinx-coroutines-core 1.9.0

### Architecture Pattern
- **Clean Architecture** with 3 layers:
  - **Data Layer:** API client, Settings storage
  - **Domain Layer:** ViewModels, business logic
  - **UI Layer:** Compose screens

## 3. Feature List

### Setup Screen
- First-launch detection
- Server IP input field
- Persistent server IP storage

### Login System
- Username/password input
- Secure credential storage (encrypted via settings)
- Persistent login (auto-login on app restart)

### Chat Screen
- Real-time message polling (every 1 second)
- GET /get?last_id=X endpoint with auth headers
- POST /send endpoint for sending messages
- Message display with sender info

### Multi-User Support
- Different users can log in
- Credentials stored per user session

## 4. UI/UX Design Direction

### Visual Style
- Modern Windows 11 design
- Material 3 components
- Dark theme color scheme
- Rounded corners (16dp radius)

### Color Scheme
- Primary: Deep Purple / Blue accent
- Surface: Dark gray (#1C1C1C)
- Background: Near black
- On-surface: Light gray text
- Error: Red accent

### Layout Approach
- Single-screen navigation (no navigation library needed)
- Conditional screen display based on app state
- Server Setup → Login → Chat flow

### API Endpoints
- GET `{server}/get?last_id={id}` - Headers: X-User, X-Pass
- POST `{server}/send` - Body: `{"text": "..."}`

## 5. Project Structure

```
src/commonMain/kotlin/org/turtledev/richard/
├── data/
│   ├── api/          # Ktor client, API service
│   └── storage/      # Settings storage
├── domain/
│   └── viewmodel/   # ViewModels
└── ui/
    ├── screens/     # Compose screens
    ├── components/ # Reusable components
    └── theme/       # Material 3 theme
```