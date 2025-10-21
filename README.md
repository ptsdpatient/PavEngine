# Pav Engine

![Pav Engine Banner](images/banner.png)

**⚙️ A Modular Game Framework for Modern Creators**

A lightweight, modular, and extensible game engine built on **LibGDX** and **gdx-gltf**, designed for seamless 3D workflows, real-time performance, and cross-platform publishing.

---

## 🚀 Features

- **ECS Architecture** – Clean data separation using Entity-Component-System for scalable gameplay logic.
- **Modern Rendering** – Physically Based Rendering (PBR) with HDR environments and gdx-gltf integration.
- **Built-in Tools** – PavMapEditor, PavUI, and PavPhysics for fast iteration and prototyping.
- **Hybrid Layering** – Combine low-level LibGDX control with high-level Pav APIs.
- **Cross-Platform** – Deploy effortlessly to HTML5, Windows, and Linux.
- **Optimized Performance** – Efficient memory, batching, and profiling for stable frame rates.
- **Game Jam Ready** – Lightweight setup and instant deploy support for quick projects.

---

## 🏁 Getting Started

### 1. Clone the Repository

```bash
git clone https://github.com/yourname/pav-engine.git
cd pav-engine
```

### 2. Setup Gradle

    Ensure you have Java 17+ and Gradle 8+ installed. Pav Engine uses Gradle for dependency management and builds.

    gradle wrapper


    Run once to generate wrapper scripts (gradlew, gradlew.bat).

### 🧩 Project Structure
    pav-engine/
    ├── core/              # Main engine code (ECS, rendering, utilities)
    ├── editor/            # PavMapEditor and PavUI tools
    ├── physics/           # PavPhysics integration
    ├── assets/            # Demo assets, shaders, and materials
    ├── samples/           # Example games and tech demos
    └── build.gradle       # Gradle configuration


### 🧰 Editor Tools

| Tool         | Purpose                                     |
| ------------ | ------------------------------------------- |
| PavMapEditor | Create and edit scenes visually             |
| PavUI        | Build responsive UI layouts                 |
| PavPhysics   | Manage physics bodies and collisions easily |


### 📦 Dependencies
| Library  | Purpose                  |
| -------- | ------------------------ |
| LibGDX   | Base framework           |
| gdx-gltf | PBR and glTF support     |
| Kryo     | Serialization            |
| Box2D    | Physics (2D/3D wrappers) |


### 🧠 Concepts

Entity-Component-System (ECS) – Game objects composed of components managed by systems.

PBR Rendering – Realistic lighting and materials for Blender-exported assets.

Cross-Platform Abstraction – Unified APIs for rendering, input, and assets across desktop and web.


### 🧑‍💻 Contributing

Fork this repository

Create a new feature branch:

    git checkout -b feature/my-feature


Commit your changes and push:

    git push origin feature/my-feature


Open a Pull Request 🎉

### 🧱 Compilation & Build

Run on Desktop
    
    ./gradlew desktop:run

Run on Web (HTML5)
    
    ./gradlew html:superDev


Then open in your browser:

    http://localhost:8080/

Package Release
    
    ./gradlew build


Compiled output will appear in /build/libs.

## Wix 3.14 link
    https://github.com/wixtoolset/wix3/releases



💬 Contact

    Author: Tanishq Dhote

    Website: orbittechnagpur.in

    Email: contact@orbittechnagpur.in

    Twitter: @OrbitTechnagpur

## Platforms

- `core`: Main module with the application logic shared by all platforms.
- `lwjgl3`: Primary desktop platform using LWJGL3; was called 'desktop' in older docs.
- `android`: Android mobile platform. Needs Android SDK.
- `html`: Web platform using GWT and WebGL. Supports only Java projects.

## Gradle

This project uses [Gradle](https://gradle.org/) to manage dependencies.
The Gradle wrapper was included, so you can run Gradle tasks using `gradlew.bat` or `./gradlew` commands.
Useful Gradle tasks and flags:

- `--continue`: when using this flag, errors will not stop the tasks from running.
- `--daemon`: thanks to this flag, Gradle daemon will be used to run chosen tasks.
- `--offline`: when using this flag, cached dependency archives will be used.
- `--refresh-dependencies`: this flag forces validation of all dependencies. Useful for snapshot versions.
- `android:lint`: performs Android project validation.
- `build`: builds sources and archives of every project.
- `cleanEclipse`: removes Eclipse project data.
- `cleanIdea`: removes IntelliJ project data.
- `clean`: removes `build` folders, which store compiled classes and built archives.
- `eclipse`: generates Eclipse project data.
- `html:dist`: compiles GWT sources. The compiled application can be found at `html/build/dist`: you can use any HTTP server to deploy it.
- `html:superDev`: compiles GWT sources and runs the application in SuperDev mode. It will be available at [localhost:8080/html](http://localhost:8080/html). Use only during development.
- `idea`: generates IntelliJ project data.
- `lwjgl3:jar`: builds application's runnable jar, which can be found at `lwjgl3/build/libs`.
- `lwjgl3:run`: starts the application.
- `test`: runs unit tests (if any).

Note that most tasks that are not specific to a single project can be run with `name:` prefix, where the `name` should be replaced with the ID of a specific project.
For example, `core:clean` removes `build` folder only from the `core` project.
