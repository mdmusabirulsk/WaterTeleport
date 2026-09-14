package com.waterteleport.util;

import com.waterteleport.config.WaterTeleportConfig;
import net.minecraft.core.BlockPos;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.Objects;
import java.util.Optional;

/**
 * Utility helper for analyzing destination safety, suffocation risks,
 * and environmental hazards during water bucket teleportation on Minecraft 1.21.x.
 */
@NullMarked
public final class TeleportSafetyHelper {

    private static final int MAX_SAFE_SEARCH_RADIUS = 16;

    private TeleportSafetyHelper() {
        // Utility class
    }

    public static boolean isHazardous(Level level, BlockPos pos) {
        if (pos.getY() < level.getMinY()) {
            return true; // The Void
        }

        if (!level.isLoaded(pos)) {
            return true; // Unloaded chunk treated as unsafe
        }

        BlockState state = level.getBlockState(pos);

        // Lava fluid or lava block
        if (state.getFluidState().is(FluidTags.LAVA) || state.is(Blocks.LAVA)) {
            return true;
        }

        // Fire (using standard fire tag and blocks)
        if (state.is(BlockTags.FIRE) || state.is(Blocks.FIRE) || state.is(Blocks.SOUL_FIRE)) {
            return true;
        }

        // Magma
        if (state.is(Blocks.MAGMA_BLOCK)) {
            return true;
        }

        // Campfires (using standard campfire tag and blocks)
        if (state.is(BlockTags.CAMPFIRES) || state.is(Blocks.CAMPFIRE) || state.is(Blocks.SOUL_CAMPFIRE)) {
            return true;
        }

        // Damaging vegetation and dangerous environmental blocks
        return state.is(Blocks.CACTUS)
                || state.is(Blocks.SWEET_BERRY_BUSH)
                || state.is(Blocks.WITHER_ROSE)
                || state.is(Blocks.POWDER_SNOW);
    }

    public static boolean isSuffocating(Level level, BlockPos pos) {
        if (!level.isLoaded(pos)) {
            return true;
        }

        BlockState state = level.getBlockState(pos);

        // Fluids (water) and air do not suffocate the player
        if (state.isAir() || state.getFluidState().is(FluidTags.WATER)) {
            return false;
        }

        // Check if the block has physical collision or suffocates the player
        return state.isSuffocating(level, pos) || !state.getCollisionShape(level, pos).isEmpty();
    }

    public static boolean isSafeLandingSpot(Level level, BlockPos footPos) {
        WaterTeleportConfig config = WaterTeleportConfig.get();

        if (footPos.getY() < level.getMinY() || footPos.getY() >= level.getMaxY() - 1) {
            return false;
        }

        if (!level.isLoaded(footPos)) {
            return false;
        }

        BlockPos headPos = footPos.above();
        BlockPos groundPos = footPos.below();

        // Check hazards
        if (config.preventHazardousBlocks) {
            if (isHazardous(level, footPos) || isHazardous(level, headPos) || isHazardous(level, groundPos)) {
                return false;
            }
        }

        // Check suffocation / obstruction for feet and head
        if (config.preventSuffocation) {
            if (isSuffocating(level, footPos) || isSuffocating(level, headPos)) {
                return false;
            }
        }

        return true;
    }

    public static Optional<Vec3> findSafeDestination(Level level, Vec3 hitLocation, int searchRadius) {
        BlockPos basePos = BlockPos.containing(hitLocation);
        int radius = Math.max(0, Math.min(MAX_SAFE_SEARCH_RADIUS, searchRadius));

        @Nullable Vec3 safeTarget = null;

        // 1. Direct hit check
        if (isSafeLandingSpot(level, basePos)) {
            safeTarget = new Vec3(basePos.getX() + 0.5, basePos.getY(), basePos.getZ() + 0.5);
        } else if (isSafeLandingSpot(level, basePos.above())) {
            // 2. Check directly above (projectile hit the floor)
            BlockPos abovePos = basePos.above();
            safeTarget = new Vec3(abovePos.getX() + 0.5, abovePos.getY(), abovePos.getZ() + 0.5);
        } else {
            // 3. Search in expanding radius around the hit point
            @Nullable BlockPos bestCandidate = null;
            double bestDistSq = Double.MAX_VALUE;

            for (int dy = -1; dy <= radius; dy++) {
                for (int dx = -radius; dx <= radius; dx++) {
                    for (int dz = -radius; dz <= radius; dz++) {
                        BlockPos candidate = basePos.offset(dx, dy, dz);
                        if (isSafeLandingSpot(level, candidate)) {
                            double distSq = hitLocation.distanceToSqr(candidate.getX() + 0.5, candidate.getY(), candidate.getZ() + 0.5);
                            if (distSq < bestDistSq) {
                                bestDistSq = distSq;
                                bestCandidate = candidate;
                            }
                        }
                    }
                }
            }

            if (bestCandidate != null) {
                safeTarget = new Vec3(bestCandidate.getX() + 0.5, bestCandidate.getY(), bestCandidate.getZ() + 0.5);
            }
        }

        if (safeTarget != null) {
            Vec3 target = safeTarget;
            return Objects.requireNonNull(Optional.of(target));
        }

        return Objects.requireNonNull(Optional.empty());
    }
}
