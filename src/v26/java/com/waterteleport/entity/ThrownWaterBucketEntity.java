package com.waterteleport.entity;

import com.waterteleport.WaterTeleportMod;
import com.waterteleport.config.WaterTeleportConfig;
import com.waterteleport.util.TeleportSafetyHelper;
import net.minecraft.ChatFormatting;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.throwableitemprojectile.ThrowableItemProjectile;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.Optional;

/**
 * Custom projectile entity representing a thrown water bucket.
 * Handles client particle trails during flight, impact detection,
 * destination safety validation, player teleportation, and exploit-safe item refunds.
 */
@NullMarked
public class ThrownWaterBucketEntity extends ThrowableItemProjectile {

    public ThrownWaterBucketEntity(EntityType<? extends ThrownWaterBucketEntity> entityType, Level level) {
        super(entityType, level);
    }

    public ThrownWaterBucketEntity(Level level, LivingEntity owner, ItemStack stack) {
        super(WaterTeleportMod.THROWN_WATER_BUCKET, owner, level, stack);
    }

    @Override
    protected Item getDefaultItem() {
        return Items.WATER_BUCKET;
    }

    @Override
    public void tick() {
        super.tick();

        // Spawn visual water particle trail while traveling through the air (client only)
        if (this.level().isClientSide() && WaterTeleportConfig.get().playEffects) {
            Vec3 delta = this.getDeltaMovement();
            for (int i = 0; i < 2; i++) {
                this.level().addParticle(
                        ParticleTypes.BUBBLE,
                        this.getX() - delta.x * 0.25D,
                        this.getY() - delta.y * 0.25D,
                        this.getZ() - delta.z * 0.25D,
                        delta.x * 0.05D,
                        delta.y * 0.05D,
                        delta.z * 0.05D
                );
                this.level().addParticle(
                        ParticleTypes.SPLASH,
                        this.getX() - delta.x * 0.25D,
                        this.getY() - delta.y * 0.25D,
                        this.getZ() - delta.z * 0.25D,
                        0.0D,
                        0.02D,
                        0.0D
                );
            }
        }
    }

    @Override
    protected void onHit(HitResult hitResult) {
        super.onHit(hitResult);

        if (!this.level().isClientSide() && this.level() instanceof ServerLevel serverLevel) {
            @Nullable Entity owner = this.getOwner();
            WaterTeleportConfig config = WaterTeleportConfig.get();

            if (owner instanceof ServerPlayer serverPlayer
                    && serverPlayer.isAlive()
                    && !serverPlayer.hasDisconnected()
                    && serverPlayer.level() == this.level()) {

                Vec3 hitPos = hitResult.getLocation();
                Optional<Vec3> safeDestination = TeleportSafetyHelper.findSafeDestination(
                        this.level(),
                        hitPos,
                        config.searchSafeLandingRadius
                );

                if (safeDestination.isPresent()) {
                    Vec3 target = safeDestination.get();

                    // Pre-teleport sound & particles at original position
                    if (config.playEffects) {
                        serverLevel.sendParticles(
                                ParticleTypes.PORTAL,
                                serverPlayer.getX(),
                                serverPlayer.getY() + 0.5D,
                                serverPlayer.getZ(),
                                20, 0.4D, 0.8D, 0.4D, 0.1D
                        );
                    }

                    // Dismount player if riding a vehicle to prevent vehicle desync/rubberbanding
                    if (serverPlayer.isPassenger()) {
                        serverPlayer.stopRiding();
                    }

                    // Reset fall distance and teleport
                    serverPlayer.resetFallDistance();
                    serverPlayer.teleportTo(target.x, target.y, target.z);

                    // Post-teleport sound & particles at landing position
                    if (config.playEffects) {
                        serverLevel.playSound(
                                null,
                                target.x, target.y, target.z,
                                SoundEvents.ENDERMAN_TELEPORT,
                                SoundSource.PLAYERS,
                                1.0F, 1.0F
                        );
                        serverLevel.playSound(
                                null,
                                target.x, target.y, target.z,
                                SoundEvents.PLAYER_SPLASH_HIGH_SPEED,
                                SoundSource.PLAYERS,
                                0.8F, 1.2F
                        );

                        // Water and portal particle explosion
                        serverLevel.sendParticles(
                                ParticleTypes.SPLASH,
                                target.x, target.y + 0.3D, target.z,
                                60, 0.5D, 0.5D, 0.5D, 0.15D
                        );
                        serverLevel.sendParticles(
                                ParticleTypes.PORTAL,
                                target.x, target.y + 0.7D, target.z,
                                40, 0.6D, 0.9D, 0.6D, 0.2D
                        );
                        serverLevel.sendParticles(
                                ParticleTypes.BUBBLE,
                                target.x, target.y + 0.5D, target.z,
                                30, 0.4D, 0.5D, 0.4D, 0.05D
                        );
                    }
                } else {
                    // Safe landing point could not be found
                    if (config.playEffects) {
                        serverLevel.playSound(
                                null,
                                this.getX(), this.getY(), this.getZ(),
                                SoundEvents.FIRE_EXTINGUISH,
                                SoundSource.PLAYERS,
                                1.0F, 1.2F
                        );
                    }

                    serverPlayer.sendSystemMessage(
                            Component.translatable("text.waterteleport.unsafe_destination").withStyle(ChatFormatting.RED),
                            true
                    );

                    // Refund water bucket safely without duplicating items or voiding on full inventory
                    if (config.consumeBucketInSurvival && !serverPlayer.isCreative()) {
                        if (config.returnEmptyBucket) {
                            // Reclaim the empty bucket that was granted upon throw
                            if (consumeEmptyBucket(serverPlayer)) {
                                giveOrDropItem(serverPlayer, new ItemStack(Items.WATER_BUCKET));
                            }
                        } else {
                            giveOrDropItem(serverPlayer, new ItemStack(Items.WATER_BUCKET));
                        }
                    }
                }
            }

            // Discard the entity after collision resolution
            this.discard();
        }
    }

    /**
     * Consumes one empty bucket from the player's inventory to safely revert an unused throw.
     * Prevents bucket duplication exploits.
     */
    private static boolean consumeEmptyBucket(ServerPlayer player) {
        for (int i = 0; i < player.getInventory().getContainerSize(); i++) {
            ItemStack stack = player.getInventory().getItem(i);
            if (!stack.isEmpty() && stack.is(Items.BUCKET)) {
                stack.shrink(1);
                return true;
            }
        }
        return false;
    }

    /**
     * Gives an item stack to the player, or safely drops it at their feet if inventory is full.
     * Prevents item deletion/voiding bugs.
     */
    private static void giveOrDropItem(ServerPlayer player, ItemStack stack) {
        if (!player.getInventory().add(stack)) {
            player.drop(stack, false);
        }
    }
}
