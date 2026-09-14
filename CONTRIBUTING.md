# Contributing to WaterTeleport

Thank you for your interest in contributing to **WaterTeleport**! This document outlines our development guidelines, code standards, and the pull request process.

---

## 🛠️ Development Setup

### Prerequisites
- **Java Development Kit (JDK) 25** (Adoptium Temurin or OpenJDK 25).
- **Git** installed on your system.

### Cloning & Building
1. Fork the repository on GitHub.
2. Clone your fork locally:
   ```bash
   git clone https://github.com/YOUR_USERNAME/WaterTeleport.git
   cd WaterTeleport
   ```
3. Verify the project builds cleanly:
   ```bash
   ./gradlew clean build
   ```

---

## 📋 Code & Architectural Standards

To ensure the mod remains robust, maintainable, and production-ready, all contributions must adhere to the following rules:

### 1. Java & API Standards
- Target **Java 25** and **Minecraft 26.1.2** with Fabric Loom.
- Use **Mojang Official Mappings** (`officialMojangMappings`).
- Do not introduce unnecessary third-party dependencies.

### 2. Client/Server Separation
- Common logic, entity definitions, and networking belong in `com.waterteleport` or its subpackages (excluding `client`).
- Client-only code (rendering, models, client events) MUST reside in `com.waterteleport.client` and only be registered through `WaterTeleportClient`.
- Never import or reference client classes in common/server classes (e.g., `Minecraft`, `EntityRenderers`, `Screen`).

### 3. Null Safety & Type Safety
- All classes must use JSpecify annotations (`org.jspecify.annotations.NullMarked` and `org.jspecify.annotations.Nullable`).
- Annotate every class with `@NullMarked`.
- Explicitly mark nullable fields, parameters, and return values with `@Nullable`.
- **Do NOT suppress warnings**: The build enforces `-Xlint:deprecation` and `-Xlint:unchecked`. Code must compile with **zero warnings**.

### 4. Game Balance & Anti-Exploit
- Always validate server-side actions; never trust client inputs unconditionally.
- Teleportation destination checks must use `TeleportSafetyHelper` or equivalent clearance algorithms.
- Item transactions (consumption, refunds, inventory additions) must handle edge cases like full player inventories and item duplication risks.

---

## 🔄 Pull Request Process

1. **Create a Feature Branch**:
   ```bash
   git checkout -b feature/your-feature-name
   # or
   git checkout -b fix/your-bugfix-name
   ```
2. **Commit Your Changes**:
   - Write clear, imperative commit messages (e.g., `Fix item voiding when player inventory is full`).
   - Keep commits focused and atomic.
3. **Verify Locally Before Pushing**:
   - Run a clean build:
     ```bash
     ./gradlew clean build
     ```
   - Ensure the build succeeds with **0 errors** and **0 warnings**.
   - Test in-game using `./gradlew runClient` or `./gradlew runServer`.
4. **Submit a Pull Request**:
   - Open a PR against the `main` branch.
   - Describe what changed, why the change is needed, and how it was tested.

---

## 🐛 Reporting Bugs

When reporting an issue, please include:
- Minecraft version, Fabric Loader version, and WaterTeleport version.
- A concise description of the bug and reproduction steps.
- Relevant crash reports or server/client logs (`logs/latest.log`).
- Any active conflicting mods.
