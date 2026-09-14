# WaterTeleport

[![Minecraft Version](https://img.shields.io/badge/Minecraft-26.1.2-brightgreen.svg)](https://www.minecraft.net/)
[![Fabric Loader](https://img.shields.io/badge/Fabric%20Loader-0.19.5%2B-blue.svg)](https://fabricmc.net/)
[![Java](https://img.shields.io/badge/Java-25-orange.svg)](https://adoptium.net/)
[![License: MIT](https://img.shields.io/badge/License-MIT-yellow.svg)](LICENSE)
[![Modrinth](https://img.shields.io/badge/Modrinth-Download%20Placeholder-00AF5C.svg)](https://modrinth.com/mod/waterteleport)

**WaterTeleport** is a lightweight, server-authoritative Fabric mod for **Minecraft 26.1.2** that introduces an intuitive movement mechanic: throw Water Buckets as aerodynamic projectiles to teleport to your landing spot with particle effects, configurable cooldowns, and landing safety protection.

---

## 📑 Table of Contents

- [Features](#-features)
- [Requirements](#-requirements)
- [Download & Distribution](#-download--distribution)
- [Installation](#-installation)
- [Usage](#-usage)
- [Landing Safety & Anti-Exploit Behavior](#-landing-safety--anti-exploit-behavior)
- [Configuration](#-configuration)
- [Compatibility & Architecture](#-compatibility--architecture)
- [Building from Source](#-building-from-source)
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

## 📋 Requirements

| Component | Minimum Version | Notes |
| :--- | :--- | :--- |
| **Minecraft** | `26.1.2` | Uses Mojang Official Mappings |
| **Fabric Loader** | `0.19.5+` | Required on client and server |
| **Fabric API** | `0.155.3+26.1.2` | Standard Fabric runtime library |
| **Java Runtime (JRE)** | `Java 25+` | Modern JVM requirement for 26.1.2 |

---

## 📦 Download & Distribution

Official releases and pre-compiled binaries are distributed via:

- **Modrinth**: [https://modrinth.com/mod/waterteleport](https://modrinth.com/mod/waterteleport) *(Placeholder)*
- **GitHub Releases**: [https://github.com/mdmusabirulsk/WaterTeleport/releases](https://github.com/mdmusabirulsk/WaterTeleport/releases)

---

## 🚀 Installation

### Client Installation (Singleplayer / Multiplayer)
1. Install [Fabric Loader](https://fabricmc.net/use/installer/) for **Minecraft 26.1.2**.
2. Download [Fabric API](https://modrinth.com/mod/fabric-api) for 26.1.2 and place it into your `.minecraft/mods/` directory.
3. Download `waterteleport-1.0.0.jar` from [Releases](https://github.com/mdmusabirulsk/WaterTeleport/releases) and place it into your `.minecraft/mods/` directory.
4. Launch the game using the Fabric profile.

### Dedicated Server Installation
1. Install Fabric Loader on your Minecraft 26.1.2 server.
2. Place `fabric-api-*.jar` and `waterteleport-1.0.0.jar` into the server's `mods/` directory.
3. Restart the server. Configuration will generate under `config/waterteleport.json`.

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

- **Clean Sided Separation**:
  - `WaterTeleportMod` initializes common logic, events, and entity registrations.
  - `WaterTeleportClient` exclusively registers client entity rendering via `EntityRenderers.register`. Client-only classes are never referenced on the dedicated server.
- **Modern Minecraft 26.1.2 API Standards**:
  - Official Mojang Mappings.
  - Modern `InteractionResult` handling.
  - JSpecify (`@NullMarked`, `@Nullable`) null-safety architecture across all classes.
  - Validated with compiler flags `-Xlint:deprecation` and `-Xlint:unchecked`.

---

## 🛠️ Building from Source

### Prerequisites
- **Java 25 JDK** installed ([Adoptium](https://adoptium.net/) or Homebrew `openjdk@25`).
- Git.

### Build Steps

```bash
# Clone the repository
git clone https://github.com/mdmusabirulsk/WaterTeleport.git
cd WaterTeleport

# Build using Gradle Wrapper
# On Linux / macOS:
./gradlew clean build

# On Windows:
gradlew.bat clean build
```

Compiled output `.jar` files will be placed in `build/libs/`:
- `build/libs/waterteleport-1.0.0.jar` (Production mod artifact)
- `build/libs/waterteleport-1.0.0-sources.jar` (Decompiled source jar)

---

## 🤝 Contributing

Contributions are welcome! Please read [CONTRIBUTING.md](CONTRIBUTING.md) for details on code style, branch guidelines, and the pull request process.

---

## 🔒 Security

For vulnerability reporting and security policies, please consult [SECURITY.md](SECURITY.md).

---

## 📄 License

This project is licensed under the **MIT License** — see the [LICENSE](LICENSE) file for details.
