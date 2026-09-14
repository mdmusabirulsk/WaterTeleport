package com.waterteleport.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.fabricmc.loader.api.FabricLoader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Objects;

import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

/**
 * Configuration options and persistence manager for WaterTeleport.
 * Uses Gson for JSON serialization and validates values upon load.
 */
@NullMarked
public class WaterTeleportConfig {
    private static final Logger LOGGER = Objects.requireNonNull(LoggerFactory.getLogger("WaterTeleport/Config"));
    private static final Gson GSON = Objects.requireNonNull(new GsonBuilder().setPrettyPrinting().create());
    private static final Path CONFIG_PATH = Objects.requireNonNull(FabricLoader.getInstance().getConfigDir().resolve("waterteleport.json"));

    private static volatile WaterTeleportConfig instance = new WaterTeleportConfig();

    /**
     * Cooldown in ticks applied to the water bucket after throwing.
     * 20 ticks = 1 second. Default is 40 ticks (2 seconds).
     */
    public int cooldownTicks = 40;

    /**
     * If true, holding Sneak (Shift) and Right-Clicking throws the Water Bucket projectile,
     * while normal Right-Clicking maintains 100% vanilla bucket functionality (placing water, etc.).
     * If false, any right-click throws the projectile.
     */
    public boolean sneakToThrow = true;

    /**
     * Whether throwing a water bucket consumes the item in Survival mode.
     * Creative mode is always non-destructive.
     */
    public boolean consumeBucketInSurvival = true;

    /**
     * Whether an empty bucket is returned to the player when thrown in Survival mode.
     */
    public boolean returnEmptyBucket = true;

    /**
     * Velocity multiplier for the thrown water bucket projectile.
     */
    public float projectileVelocity = 1.5f;

    /**
     * Inaccuracy factor for the projectile (0.0f = pinpoint accuracy).
     */
    public float projectileInaccuracy = 0.0f;

    /**
     * Radius (in blocks) to search for a safe landing spot if the exact landing position is inside a block or unsafe.
     * Clamped between 0 and 16.
     */
    public int searchSafeLandingRadius = 2;

    /**
     * Whether to play water splash and teleport sound effects and particles.
     */
    public boolean playEffects = true;

    /**
     * Whether to prevent teleporting the player into suffocating solid blocks.
     */
    public boolean preventSuffocation = true;

    /**
     * Whether to prevent teleporting into dangerous locations (lava, fire, magma blocks, void, etc.).
     */
    public boolean preventHazardousBlocks = true;

    public static WaterTeleportConfig get() {
        return instance;
    }

    /**
     * Validates and sanitizes configuration parameters to prevent invalid states or server crashes.
     */
    public void validate() {
        if (cooldownTicks < 0) {
            cooldownTicks = 0;
        }
        if (!Float.isFinite(projectileVelocity) || projectileVelocity <= 0.0f) {
            projectileVelocity = 1.5f;
        } else {
            projectileVelocity = Math.min(projectileVelocity, 10.0f);
        }
        if (!Float.isFinite(projectileInaccuracy) || projectileInaccuracy < 0.0f) {
            projectileInaccuracy = 0.0f;
        } else {
            projectileInaccuracy = Math.min(projectileInaccuracy, 10.0f);
        }
        searchSafeLandingRadius = Math.max(0, Math.min(16, searchSafeLandingRadius));
    }

    /**
     * Loads configuration from disk, creating default if absent.
     */
    public static void load() {
        if (!Files.exists(CONFIG_PATH)) {
            LOGGER.info("No config file found for WaterTeleport, creating default at {}", CONFIG_PATH);
            instance = new WaterTeleportConfig();
            save();
            return;
        }

        try (BufferedReader reader = Files.newBufferedReader(CONFIG_PATH, StandardCharsets.UTF_8)) {
            @Nullable WaterTeleportConfig loaded = GSON.fromJson(reader, (java.lang.reflect.Type) WaterTeleportConfig.class);
            if (loaded != null) {
                loaded.validate();
                instance = loaded;
                LOGGER.info("WaterTeleport configuration loaded successfully.");
            } else {
                LOGGER.warn("Config file was empty; creating default configuration.");
                instance = new WaterTeleportConfig();
                save();
            }
        } catch (Exception e) {
            LOGGER.error("Failed to load WaterTeleport config, reverting to defaults", e);
            instance = new WaterTeleportConfig();
            save();
        }
    }

    /**
     * Saves current configuration to disk using UTF-8 formatting.
     */
    public static void save() {
        try {
            @Nullable Path parent = CONFIG_PATH.getParent();
            if (parent != null && !Files.exists(parent)) {
                Files.createDirectories(parent);
            }
            try (BufferedWriter writer = Files.newBufferedWriter(CONFIG_PATH, StandardCharsets.UTF_8)) {
                GSON.toJson(instance, writer);
            }
        } catch (IOException e) {
            LOGGER.error("Failed to save WaterTeleport configuration to {}", CONFIG_PATH, e);
        }
    }
}
