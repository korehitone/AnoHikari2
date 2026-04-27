# AnoHikari2

A modern Android application for Islamic resources, featuring Quranic content, prayer times (adhan), and qibla direction finding. Built with Jetpack Compose and following clean architecture principles.

## About

AnoHikari2 is a **remake project** designed to provide comprehensive Islamic digital tools for Muslims worldwide. The app combines offline content access with real-time data integration for prayer schedules and location-based qibla direction.

**App Name**: Kore Qoran  
**Package**: com.syntxr.anohikari3  
**Current Version**: 1.4 (Version Code: 6)

---

## Features

### 📖 Quranic Content
- Access to complete Quranic texts and Islamic resources
- Offline-first approach with local database caching
- Multi-language support (Indonesian & English)

### 🕌 Prayer Times & Adhan
- Real-time prayer times (Adhan) information
- Location-based prayer schedule calculations
- Background service for notifications

### 🧭 Qibla Direction
- Compass-based qibla direction finder
- Location services integration
- Multiple data source support for accuracy

### 🎵 Audio Playback
- Built-in media player for Quranic recitations
- Background audio playback with foreground service
- Media controls and notification integration
- Support for network and local audio sources

### 🌙 Dark Mode & Localization
- Material Design 3 with night mode support
- Indonesian (in) and English (en) interface
- RTL support enabled

### 💾 Offline Access
- Room database for local content storage
- Schema versioning for safe database migrations
- Persistent user preferences

---

## Tech Stack

### Architecture & Framework
| Component | Version | Purpose |
|-----------|---------|---------|
| **Kotlin** | 2.3.21 | Primary language |
| **Jetpack Compose** | 2026.04.01 | Modern declarative UI |
| **Material3** | Latest | UI components & design system |
| **Hilt** | 2.59.2 | Dependency injection |
| **Room** | 2.8.4 | Local database with ORM |
| **Compose Destinations** | 2.3.0 | Navigation & screen routing |

### Networking & Services
| Library | Version | Use Case |
|---------|---------|----------|
| **Retrofit** | 3.0.0 | REST API calls |
| **OkHttp** | 5.3.2 | HTTP client with interceptors |
| **Gson** | Retrofit bundled | JSON serialization |
| **Play Services Location** | 21.3.0 | GPS & location services |

### Data & Storage
| Library | Version | Purpose |
|---------|---------|---------|
| **Kotpref** | 2.13.2 | Shared preferences wrapper with type safety |
| **Room KTX** | 2.8.4 | Coroutines support for database operations |

### Media & Audio
| Library | Version | Feature |
|---------|---------|---------|
| **Snow Player** | 1.2.13 | Audio playback engine |
| **Accompanist Permissions** | 0.37.3 | Runtime permissions handling |
| **Media3 (AndroidX)** | Latest | Media session management |

### Additional Tools
| Library | Version | Purpose |
|---------|---------|---------|
| **AgentWeb** | 4.1.9 | WebView management |
| **Constraint Layout Compose** | 1.1.1 | Advanced layout controls |
| **Appcompat** | 1.7.1 | Backward compatibility |

---

## Project Structure


### Directory Details

#### `/data` - Data Layer
Handles all data operations (local & remote)
- **datasource/** - API clients & local database access
- **repository/** - Repository implementations
- **model/** - API response models & entities

#### `/domain` - Domain Layer
Pure business logic, framework-independent
- **usecase/** - Application use cases
- **model/** - Domain entities
- **repository/** - Repository interfaces

#### `/presentation` - Presentation Layer
All UI & UX related code
- **screen/** - Compose screens
- **viewmodel/** - State management
- **component/** - Reusable UI components
- **navigation/** - Navigation logic

#### `/service` - Background Services
- **MyPlayerService** - Media playback foreground service

#### `/utils` - Utilities
- **extension/** - Extension functions
- **constant/** - App constants
- **helper/** - Helper functions
