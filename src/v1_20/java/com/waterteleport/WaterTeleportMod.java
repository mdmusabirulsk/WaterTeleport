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
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import org.jspecify.annotations.NullMarked;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Objects;

/**
 * Main entrypoint for WaterTeleport mod on Minecraft 1.20.1.
 */
@NullMarked
public class WaterTeleportMod implements ModInitializer {
    public static final String MOD_ID = "waterteleport";
    public static final Logger LOGGER = Objects.requireNonNull(LoggerFactory.getLogger(MOD_ID));

    public static final ResourceLocation THROWN_WATER_BUCKET_ID = Objects.requireNonNull(ResourceLocation.tryParse(MOD_ID + ":thrown_water_bucket"));
    public static final ResourceKey<EntityType<?>> THROWN_WATER_BUCKET_KEY = ResourceKey.create(Registries.ENTITY_TYPE, THROWN_WATER_BUCKET_ID);

    public static final EntityType<ThrownWaterBucketEntity> THROWN_WATER_BUCKET = Objects.requireNonNull(Registry.<EntityType<?>, EntityType<ThrownWaterBucketEntity>>register(
            BuiltInRegistries.ENTITY_TYPE,
            THROWN_WATER_BUCKET_KEY,
            EntityType.Builder.<ThrownWaterBucketEntity>of(ThrownWaterBucketEntity::new, MobCategory.MISC)
                    .sized(0.25F, 0.25F)
                    .clientTrackingRange(4)
                    .updateInterval(10)
                    .build("waterteleport:thrown_water_bucket")
    ));

    @Override
    public void onInitialize() {
        LOGGER.info("Initializing WaterTeleport mod for Minecraft 1.20.1...");

        WaterTeleportConfig.load();

        UseItemCallback.EVENT.register((player, world, hand) -> {
            ItemStack stack = player.getItemInHand(hand);

            if (player.isSpectator()) {
                return InteractionResultHolder.pass(stack);
            }

            if (stack.is(Items.WATER_BUCKET)) {
                WaterTeleportConfig config = WaterTeleportConfig.get();

                if (config.sneakToThrow && !player.isShiftKeyDown()) {
                    return InteractionResultHolder.pass(stack);
                }

                if (player.getCooldowns().isOnCooldown(stack.getItem())) {
                    return InteractionResultHolder.fail(stack);
                }

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

                    if (config.cooldownTicks > 0) {
                        player.getCooldowns().addCooldown(stack.getItem(), config.cooldownTicks);
                    }

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

                    player.awardStat(Stats.ITEM_USED.get(Items.WATER_BUCKET));
                }

                player.swing(hand, true);
                return InteractionResultHolder.success(stack);
            }

            return InteractionResultHolder.pass(stack);
        });

        LOGGER.info("WaterTeleport mod initialized successfully for Minecraft 1.20.1.");
    }
}
