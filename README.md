# WaterTeleport

[![Minecraft Version](https://img.shields.io/badge/Minecraft-1.20.1%20--%2026.2-brightgreen.svg)](https://www.minecraft.net/)
[![Fabric Loader](https://img.shields.io/badge/Fabric%20Loader-0.15.0%2B-blue.svg)](https://fabricmc.net/)
[![Java](https://img.shields.io/badge/Java-17%20%7C%2021%20%7C%2025-orange.svg)](https://adoptium.net/)
[![License: MIT](https://img.shields.io/badge/License-MIT-yellow.svg)](LICENSE)
[![Build Status](https://github.com/mdmusabirulsk/WaterTeleport/actions/workflows/build.yml/badge.svg)](https://github.com/mdmusabirulsk/WaterTeleport/actions/workflows/build.yml)
[![Modrinth](https://img.shields.io/badge/Modrinth-Download%20Placeholder-00AF5C.svg)](https://modrinth.com/mod/waterteleport)

**WaterTeleport** is a lightweight, server-authoritative Fabric mod for **Minecraft 1.20.x, 1.21.x, and 26.x** that introduces an intuitive movement mechanic: throw Water Buckets as aerodynamic projectiles to teleport to your landing spot with particle effects, configurable cooldowns, and landing safety protection.

---

## 📑 Table of Contents

- [Features](#-features)
- [Supported Versions & Requirements](#-supported-versions--requirements)
- [Download & Distribution](#-download--distribution)
- [Installation](#-installation)
- [Usage](#-usage)
- [Landing Safety & Anti-Exploit Behavior](#-landing-safety--anti-exploit-behavior)
- [Configuration](#-configuration)
- [Compatibility & Architecture](#-compatibility--architecture)
- [Building from Source](#-building-from-source)
- [Adding a Future Minecraft Version](#-adding-a-future-minecraft-version)
- [Contributing](#-contributing)
- [Security](#-security)
- [License](#-license)

---

## ✨ Features

- **Throwable Water Bucket Projectiles**: Launch water buckets with custom flight physics, bubble trails, and splash particles.
- **Server-Authoritative Teleportation**: Instant, validated teleportation on projectile impact with portal particle bursts and ender teleport sound effects.
- **Multi-Layer Landing Safety**:
  - **Suffocation Prevention**: Verifies two vertical blocks of non-colliding clearance (air or water) at the destination.
  - **Obstruction Fallback Search**: Automatically searches surrounding blocks in a configurable radius if the projectile hits a steep wall or ceiling.
  - **Hazard Rejection**: Automatically aborts teleportation if landing inside lava, fire, campfire, magma blocks, sweet berry bushes, or below the void (`minY`).
  - **Fair Refund Mechanism**: If a landing is determined unsafe, the teleportation is cancelled, the player is notified, and the water bucket is refunded to inventory (or dropped at player feet if inventory is full) with zero item duplication risk.
- **Full Vanilla Bucket Preservation**:
  - By default, **Sneak (Shift) + Right-Click** throws the teleportation projectile.
  - Standard **Right-Click** retains 100% vanilla bucket functionality (placing water, scooping water/fish, filling cauldrons, extinguishing fires).
  - Configurable to trigger on normal right-click if preferred.
- **Customizable Cooldowns**: Built-in cooldown system prevents teleport spamming.
- **Vehicle & Spectator Protection**: Automatically dismounts riding entities cleanly prior to teleport to prevent client-server rubberbanding; spectators cannot throw teleport projectiles.
- **Zero Extraneous Dependencies**: Built entirely against Fabric API and Mojang Official Mappings without bloated third-party libraries.

---

## 📋 Supported Versions & Requirements

WaterTeleport uses a multi-version modular build system where each supported version is compiled, remapped, and packaged into dedicated release artifacts:

| Minecraft Version | Fabric Loader | Fabric API | Java Toolchain | Build Status | Release Artifact |
| :--- | :--- | :--- | :--- | :--- | :--- |
| **1.20 – 1.20.1** | `>=0.15.0` | `0.92.2+1.20.1` | Java 17 | **PASS** | `waterteleport-1.0.0+mc1.20.1.jar` |
| **1.20.2 – 1.20.4** | `>=0.15.0` | `0.97.2+1.20.4` | Java 17 | **PASS** | `waterteleport-1.0.0+mc1.20.4.jar` |
| **1.21 – 1.21.1** | `>=0.15.0` | `0.116.17+1.21.1` | Java 21 | **PASS** | `waterteleport-1.0.0+mc1.21.1.jar` |
| **1.21.2 – 1.21.4** | `>=0.15.0` | `0.119.4+1.21.4` | Java 21 | **PASS** | `waterteleport-1.0.0+mc1.21.4.jar` |
| **1.21.5 – 1.21.11** | `>=0.15.0` | `0.141.6+1.21.11` | Java 21 | **PASS** | `waterteleport-1.0.0+mc1.21.11.jar` |
| **26.1 – 26.1.2** | `>=0.19.5` | `0.155.3+26.1.2` | Java 25 | **PASS** | `waterteleport-1.0.0+mc26.1.2.jar` |
| **26.2** | `>=0.19.5` | `0.160.0+26.2` | Java 25 | **PASS** | `waterteleport-1.0.0+mc26.2.jar` |

---

## 📦 Download & Distribution

Official releases and pre-compiled binaries are distributed via:

- **GitHub Releases**: [https://github.com/mdmusabirulsk/WaterTeleport/releases](https://github.com/mdmusabirulsk/WaterTeleport/releases)
- **Modrinth**: [https://modrinth.com/mod/waterteleport](https://modrinth.com/mod/waterteleport) *(Placeholder)*

### 📥 Releases & JAR Download Mapping

Please download the specific JAR corresponding to your Minecraft version:

| Minecraft Version | Release JAR to Download | Java Requirement |
| :--- | :--- | :--- |
| **Minecraft 1.20.1** | `waterteleport-1.0.0+mc1.20.1.jar` | Java 17+ |
| **Minecraft 1.20.2** | `waterteleport-1.0.0+mc1.20.4.jar` | Java 17+ |
| **Minecraft 1.20.4** | `waterteleport-1.0.0+mc1.20.4.jar` | Java 17+ |
| **Minecraft 1.21** | `waterteleport-1.0.0+mc1.21.1.jar` | Java 21+ |
| **Minecraft 1.21.1** | `waterteleport-1.0.0+mc1.21.1.jar` | Java 21+ |
| **Minecraft 1.21.4** | `waterteleport-1.0.0+mc1.21.4.jar` | Java 21+ |
| **Minecraft 1.21.5** | `waterteleport-1.0.0+mc1.21.11.jar` | Java 21+ |
| **Minecraft 1.21.6** | `waterteleport-1.0.0+mc1.21.11.jar` | Java 21+ |
| **Minecraft 1.21.7** | `waterteleport-1.0.0+mc1.21.11.jar` | Java 21+ |
| **Minecraft 1.21.8** | `waterteleport-1.0.0+mc1.21.11.jar` | Java 21+ |
| **Minecraft 1.21.9** | `waterteleport-1.0.0+mc1.21.11.jar` | Java 21+ |
| **Minecraft 1.21.10** | `waterteleport-1.0.0+mc1.21.11.jar` | Java 21+ |
| **Minecraft 1.21.11** | `waterteleport-1.0.0+mc1.21.11.jar` | Java 21+ |
| **Minecraft 26.1.2** | `waterteleport-1.0.0+mc26.1.2.jar` | Java 25+ |
| **Minecraft 26.2** | `waterteleport-1.0.0+mc26.2.jar` | Java 25+ |

---

## 🚀 Installation

### Client Installation (Singleplayer / Multiplayer)
1. Install [Fabric Loader](https://fabricmc.net/use/installer/) for your Minecraft version.
2. Download the matching **Fabric API** from Modrinth and place it into your `.minecraft/mods/` directory.
3. Download the matching WaterTeleport JAR for your Minecraft version from the table above.
4. Place the JAR into your `.minecraft/mods/` directory.
5. Launch the game using the Fabric profile.

### Dedicated Server Installation
1. Install Fabric Loader on your Minecraft server.
2. Place `fabric-api-*.jar` and the matching `waterteleport-*.jar` into the server's `mods/` directory.
3. Restart the server. Configuration will automatically generate under `config/waterteleport.json`.

> **Note**: For multiplayer games, WaterTeleport must be installed on both the server (to handle projectile logic, physics, and teleport validation) and the client (for projectile entity rendering and client-side interpolation).

---

## 🎮 Usage

1. Hold a **Water Bucket** in your main hand or offhand.
2. **Sneak (Default: Shift) + Right-Click**: Throw the water bucket projectile toward your target.
3. **On Impact**: You will instantly teleport to the impact position with a portal particle effect and an ender sound.
4. **Regular Right-Click**: Places water or interacts with blocks normally without throwing.

---

## 🛡️ Landing Safety & Anti-Exploit Behavior

| Condition | Mod Behavior |
| :--- | :--- |
| **Solid Wall Impact** | Searches within `searchSafeLandingRadius` blocks for adjacent flat standing ground with 2 blocks of clearance. |
| **Unsafe Destination (Lava, Fire, Void)** | Teleportation is cancelled. Extinguish sound plays and player receives an actionbar warning (`"Unsafe landing position!"`). |
| **Inventory Full on Refund** | When a teleport fails and the bucket is refunded, if the player's inventory is completely full, the bucket safely drops at the player's feet to prevent item voiding. |
| **Item Duplication Prevention** | When refunding upon unsafe impact, the mod safely consumes the empty bucket provided upon throw before returning the filled water bucket. |
| **Mounted / Riding Entities** | The player is cleanly dismounted (`stopRiding()`) before coordinates are updated to prevent vehicle desynchronization. |
| **Creative Mode** | Buckets are not consumed and empty buckets are not granted. |

---

## ⚙️ Configuration

The configuration file is automatically created at `config/waterteleport.json` when the mod initializes:

```json
{
  "cooldownTicks": 40,
  "sneakToThrow": true,
  "consumeBucketInSurvival": true,
  "returnEmptyBucket": true,
  "projectileVelocity": 1.5,
  "projectileInaccuracy": 0.0,
  "searchSafeLandingRadius": 2,
  "playEffects": true,
  "preventSuffocation": true,
  "preventHazardousBlocks": true
}
```

### Configuration Options Reference

| Option | Type | Default | Description |
| :--- | :--- | :--- | :--- |
| `cooldownTicks` | `int` | `40` | Cooldown in ticks applied after throwing (20 ticks = 1s). Set `0` for none. |
| `sneakToThrow` | `boolean` | `true` | When `true`, only Shift + Right-Click throws. If `false`, any Right-Click throws. |
| `consumeBucketInSurvival` | `boolean` | `true` | Consumes the water bucket on throw in Survival mode. |
| `returnEmptyBucket` | `boolean` | `true` | Grants an empty bucket immediately upon throwing. |
| `projectileVelocity` | `float` | `1.5` | Throw velocity multiplier. |
| `projectileInaccuracy` | `float` | `0.0` | Random trajectory inaccuracy (higher means less precise). |
| `searchSafeLandingRadius` | `int` | `2` | Radius (in blocks) to search for safe ground if the projectile hits an obstacle. |
| `playEffects` | `boolean` | `true` | Enables sound effects and particle animations. |
| `preventSuffocation` | `boolean` | `true` | Aborts teleport if no 2-block non-colliding air/water clearance exists. |
| `preventHazardousBlocks` | `boolean` | `true` | Aborts teleport if landing in lava, fire, campfires, or magma blocks. |

---

## 🧩 Compatibility & Architecture

- **Shared Configuration & Logic**:
  - `WaterTeleportConfig` is unified across all versions in `src/common/main/java`.
  - Assets (`icon.png`, `en_us.json`) and parameterized `fabric.mod.json` are unified in `src/main/resources`.
- **Targeted Version Compatibility Layers**:
  - `src/v1_20`: Supports Minecraft 1.20.x and 1.21.1 (`InteractionResultHolder`, `getMinBuildHeight`, `EntityRendererRegistry`).
  - `src/v1_21_4`: Supports Minecraft 1.21.2 – 1.21.4 (`InteractionResult`, `ItemStack` cooldowns, `ResourceKey` entity builder).
  - `src/v26`: Supports Minecraft 1.21.5 – 1.21.11, 26.1.x, and 26.2 (`Identifier`, unobfuscated projectile packages, `sendSystemMessage`).
- **Clean Sided Separation**:
  - `WaterTeleportMod` initializes common logic, events, and entity registrations.
  - `WaterTeleportClient` exclusively registers client entity rendering via Fabric API or vanilla `EntityRenderers.register`. Client-only classes are never loaded on dedicated servers.
- **Strict Code Quality**:
  - JSpecify (`@NullMarked`, `@Nullable`) null-safety architecture across all classes.
  - 0 compilation errors and 0 compiler warnings under `-Xlint:deprecation` and `-Xlint:unchecked`.

---

## 🛠️ Building from Source

### Prerequisites
- **Java 25 JDK** installed ([Adoptium](https://adoptium.net/) or Homebrew `openjdk@25`).
- Git.

The project uses Gradle Java toolchains with `--release` flags to compile each version to its exact Java requirement (Java 17 for 1.20.x, Java 21 for 1.21.x, Java 25 for 26.x).

### Build All Versions

Run the root build command to compile all 7 version subprojects and aggregate all JARs:

```bash
# On Linux / macOS:
./gradlew clean build

# Or execute the buildAll task:
./gradlew buildAll

# On Windows:
gradlew.bat clean build
```

### Build a Single Version

You can build any specific Minecraft version subproject independently:

```bash
# Minecraft 1.20.1 (1.20 - 1.20.1):
./gradlew :waterteleport-1.20.1:build

# Minecraft 1.20.4 (1.20.2 - 1.20.4):
./gradlew :waterteleport-1.20.4:build

# Minecraft 1.21.1 (1.21 - 1.21.1):
./gradlew :waterteleport-1.21.1:build

# Minecraft 1.21.4 (1.21.2 - 1.21.4):
./gradlew :waterteleport-1.21.4:build

# Minecraft 1.21.11 (1.21.5 - 1.21.11):
./gradlew :waterteleport-1.21.11:build

# Minecraft 26.1.2 (26.1 - 26.1.2):
./gradlew :waterteleport-26.1.2:build

# Minecraft 26.2:
./gradlew :waterteleport-26.2:build
```

### Output JAR Locations

Upon a successful build, all output JARs are automatically collected into the root directory:
`build/libs/`

- `build/libs/waterteleport-1.0.0+mc1.20.1.jar`
- `build/libs/waterteleport-1.0.0+mc1.20.4.jar`
- `build/libs/waterteleport-1.0.0+mc1.21.1.jar`
- `build/libs/waterteleport-1.0.0+mc1.21.4.jar`
- `build/libs/waterteleport-1.0.0+mc1.21.11.jar`
- `build/libs/waterteleport-1.0.0+mc26.1.2.jar`
- `build/libs/waterteleport-1.0.0+mc26.2.jar`

Accompanying `-sources.jar` files are also generated for each module.

---

## ➕ Adding a Future Minecraft Version

To add a new Minecraft version to this project:

1. Create a new module folder under `versions/<mc-version>/` containing:
   - `build.gradle` (declares `fabric-loom` or `net.fabricmc.fabric-loom` plugin and repositories)
   - `gradle.properties` specifying:
     ```properties
     minecraft_version=<mc-version>
     fabric_api_version=<api-version>
     minecraft_dependency=>=<min-version> <=<max-version>
     java_version=<17|21|25>
     java_dependency=>=<17|21|25>
     era_dir=<v1_20|v1_21_4|v26>
     ```
2. Register the module in `settings.gradle`:
   ```groovy
   include 'waterteleport-<mc-version>'
   project(':waterteleport-<mc-version>').projectDir = file("versions/<mc-version>")
   ```
3. Test compilation with `./gradlew :waterteleport-<mc-version>:build`. If the new Minecraft version introduced API changes, create a targeted `src/v<new_era>/` source directory and assign `era_dir=v<new_era>`.

---

## 🤝 Contributing

Contributions are welcome! Please read [CONTRIBUTING.md](CONTRIBUTING.md) for details on code style, branch guidelines, and the pull request process.

---

## 🔒 Security

For vulnerability reporting and security policies, please consult [SECURITY.md](SECURITY.md).

---

## 📄 License

This project is licensed under the **MIT License** — see the [LICENSE](LICENSE) file for details.
