# Changelog

All notable changes to the **WaterTeleport** project will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.1.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

---

## [1.0.0] - 2026-09-15

### Added
- **Initial Stable Release**: Complete release of WaterTeleport with full multi-version Fabric support.
- **Throwable Water Bucket Projectile (`ThrownWaterBucketEntity`)**:
  - Custom projectile entity launched by throwing a water bucket.
  - Realistic in-flight physics with water bubble trails and water splash particle effects.
  - Sided entity renderer (`ThrownItemRenderer`) registered via `EntityRenderers.register`.
- **Server-Authoritative Teleportation**:
  - Teleports player to the projectile's validated impact location upon impact.
  - Triggers portal particle bursts and ender teleport sound effects on success.
  - Vehicle dismount safety: cleanly calls `stopRiding()` before teleporting mounted players.
- **Intelligent Landing Safety (`TeleportSafetyHelper`)**:
  - 2-block non-colliding clearance validation (air or water) to prevent suffocating in walls.
  - Obstruction fallback search algorithm: scans surrounding blocks up to configurable radius for safe ground when hitting walls or ceilings.
  - Comprehensive hazard detection: rejects landing in lava, fire, campfires, magma blocks, sweet berry bushes, or below void level (`minY`).
- **Fair Refund & Anti-Exploit Protection**:
  - If a landing is unsafe, plays extinguish sound and notifies player via action bar message.
  - Safe refund logic: consumes 1 empty bucket (given on throw) before refunding the filled water bucket to eliminate item duplication exploits.
  - Full inventory safety: drops the refunded water bucket at player's feet if inventory is full, preventing item voiding.
  - Creative mode awareness: players in Creative mode do not consume items or receive empty buckets.
  - Spectator check: players in Spectator mode cannot throw teleport projectiles.
- **Vanilla Bucket Preservation**:
  - By default, throwing requires sneaking (Shift + Right-Click).
  - Regular Right-Click retains 100% vanilla bucket functionality (placing water, picking up fish, filling cauldrons, extinguishing fires).
- **JSON Configuration System (`WaterTeleportConfig`)**:
  - Persistent, human-readable configuration file at `config/waterteleport.json`.
  - Configurable cooldown, sneak-to-throw toggle, projectile velocity, inaccuracy, search radius, and hazard checks.
  - Auto-repair and value validation clamping for out-of-range settings.
  - Thread-safe volatile singleton with NIO UTF-8 I/O.
- **Multi-Version Minecraft Support (1.20.1 through 26.2)**:
  - Multi-subproject modular architecture with shared codebase across supported versions.
  - Dedicated production artifacts:
    - `waterteleport-1.0.0+mc1.20.1.jar` (supports Minecraft 1.20 and 1.20.1, Java 17)
    - `waterteleport-1.0.0+mc1.20.4.jar` (supports Minecraft 1.20.2 through 1.20.4, Java 17)
    - `waterteleport-1.0.0+mc1.21.1.jar` (supports Minecraft 1.21 and 1.21.1, Java 21)
    - `waterteleport-1.0.0+mc1.21.4.jar` (supports Minecraft 1.21.2 through 1.21.4, Java 21)
    - `waterteleport-1.0.0+mc1.21.11.jar` (supports Minecraft 1.21.5 through 1.21.11, Java 21)
    - `waterteleport-1.0.0+mc26.1.2.jar` (supports Minecraft 26.1 through 26.1.2, Java 25)
    - `waterteleport-1.0.0+mc26.2.jar` (supports Minecraft 26.2, Java 25)
  - Built against Mojang Official Mappings and Fabric Loader 0.15.0+ / 0.19.5+.
  - Standard JSpecify 1.0.0 null-safety annotations (`@NullMarked`, `@Nullable`) with zero compilation warnings.
- **Build & Release Verification**:
  - Full automated suite verification across all 7 version subprojects.
  - Bytecode target validation: Java 17 (major 61), Java 21 (major 65), Java 25 (major 69).
  - 0 compiler warnings under `-Xlint:deprecation` and `-Xlint:unchecked`.
  - Zero leaked development/IDE files, caches, or credentials.
