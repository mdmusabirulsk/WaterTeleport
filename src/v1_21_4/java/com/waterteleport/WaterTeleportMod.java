package com.waterteleport;

import com.waterteleport.config.WaterTeleportConfig;
import com.waterteleport.entity.ThrownWaterBucketEntity;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.player.UseItemCallback;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import org.jspecify.annotations.NullMarked;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Objects;

/**
 * Main entrypoint for WaterTeleport mod on Minecraft 1.21.x.
 * Initializes configuration, registers the projectile entity type,
 * and intercepts item interactions to handle water bucket throwing.
 */
@NullMarked
public class WaterTeleportMod implements ModInitializer {
    public static final String MOD_ID = "waterteleport";
    public static final Logger LOGGER = Objects.requireNonNull(LoggerFactory.getLogger(MOD_ID));

    public static final ResourceLocation THROWN_WATER_BUCKET_ID = ResourceLocation.fromNamespaceAndPath(MOD_ID, "thrown_water_bucket");
    public static final ResourceKey<EntityType<?>> THROWN_WATER_BUCKET_KEY = ResourceKey.create(Registries.ENTITY_TYPE, THROWN_WATER_BUCKET_ID);

    public static final EntityType<ThrownWaterBucketEntity> THROWN_WATER_BUCKET = Objects.requireNonNull(Registry.<EntityType<?>, EntityType<ThrownWaterBucketEntity>>register(
            BuiltInRegistries.ENTITY_TYPE,
            THROWN_WATER_BUCKET_KEY,
            EntityType.Builder.<ThrownWaterBucketEntity>of(ThrownWaterBucketEntity::new, MobCategory.MISC)
                    .sized(0.25F, 0.25F)
                    .clientTrackingRange(4)
                    .updateInterval(10)
                    .build(THROWN_WATER_BUCKET_KEY)
    ));

    @Override
    public void onInitialize() {
        LOGGER.info("Initializing WaterTeleport mod for Minecraft 1.21.x...");

        // Load configuration
        WaterTeleportConfig.load();

        // Register item use interception
        UseItemCallback.EVENT.register((player, world, hand) -> {
            // Spectator check - spectators cannot use items
            if (player.isSpectator()) {
                return InteractionResult.PASS;
            }

            ItemStack stack = player.getItemInHand(hand);

            if (stack.is(Items.WATER_BUCKET)) {
                WaterTeleportConfig config = WaterTeleportConfig.get();

                // If sneakToThrow is enabled, normal right-click preserves vanilla behavior (placing water, etc.)
                if (config.sneakToThrow && !player.isShiftKeyDown()) {
                    return InteractionResult.PASS;
                }

                // Check cooldown
                if (player.getCooldowns().isOnCooldown(stack)) {
                    return InteractionResult.FAIL;
                }

                // Perform throw on logical server
                if (!world.isClientSide()) {
                    ThrownWaterBucketEntity projectile = new ThrownWaterBucketEntity(world, player, stack);
                    projectile.shootFromRotation(
                            player,
                            player.getXRot(),
                            player.getYRot(),
                            0.0F,
                            config.projectileVelocity,
                            config.projectileInaccuracy
                    );
                    world.addFreshEntity(projectile);

                    // Throw sound
                    if (config.playEffects) {
                        world.playSound(
                                null,
                                player.getX(), player.getY(), player.getZ(),
                                SoundEvents.SNOWBALL_THROW,
                                SoundSource.PLAYERS,
                                0.5F,
                                0.4F / (world.getRandom().nextFloat() * 0.4F + 0.8F)
                        );
                    }

                    // Apply cooldown
                    if (config.cooldownTicks > 0) {
                        player.getCooldowns().addCooldown(stack, config.cooldownTicks);
                    }

                    // Consume water bucket in survival mode if configured
                    if (!player.isCreative() && config.consumeBucketInSurvival) {
                        stack.shrink(1);
                        if (config.returnEmptyBucket) {
                            ItemStack emptyBucket = new ItemStack(Items.BUCKET);
                            if (stack.isEmpty()) {
                                player.setItemInHand(hand, emptyBucket);
                            } else if (!player.getInventory().add(emptyBucket)) {
                                player.drop(emptyBucket, false);
                            }
                        }
                    }

                    // Award item used statistic
                    player.awardStat(Stats.ITEM_USED.get(Items.WATER_BUCKET));
                }

                player.swing(hand, true);
                return InteractionResult.SUCCESS;
            }

            return InteractionResult.PASS;
        });

        LOGGER.info("WaterTeleport mod initialized successfully for Minecraft 1.21.x.");
    }
}
