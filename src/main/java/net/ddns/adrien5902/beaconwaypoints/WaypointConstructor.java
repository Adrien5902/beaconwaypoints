package net.ddns.adrien5902.beaconwaypoints;

import net.ddns.adrien5902.beaconwaypoints.mixin.BeaconUpdateBaseInvoker;
import net.fabricmc.fabric.api.event.player.UseBlockCallback;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.EnderpearlItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BeaconBlockEntity;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.phys.AABB;

import java.util.List;
import java.util.Optional;

public class WaypointConstructor {
    public static void register() {
        UseBlockCallback.EVENT.register((player, level, hand, hitResult) -> {
            if (!(player instanceof ServerPlayer)) {
                // Only handle this if the player is a server player
                return InteractionResult.PASS;
            }

            ItemStack holding = player.getMainHandItem();

            if (!(holding.getItem() instanceof EnderpearlItem))
                return InteractionResult.PASS;

            ServerLevel serverLevel = (ServerLevel) level;
            BlockPos pos = hitResult.getBlockPos();

            if (!isValidWaypoint(serverLevel, pos)) {
                return InteractionResult.PASS;
            }

            Component name = holding.getHoverName();

            // TODO: check for item renamed or not and so change waypoint's name if unamed
            Waypoint /* waypoint = null; */
            // if (name.getStyle().isItalic()) {
            waypoint = new Waypoint(name.getString(), pos, Optional.empty());
            // } else {
            // waypoint = Waypoint.unamed(pos);
            // }

            if (WaypointsManager.fromLevel(serverLevel).waypoints.stream()
                    .anyMatch((w) -> w.pos.equals(pos))) {
                player.sendSystemMessage(Component.translatable("beaconwaypoints.error.waypoint_already_set_up_here"));
                return InteractionResult.CONSUME;
            }

            WaypointsManager manager = WaypointsManager.fromLevel(serverLevel);
            manager.waypoints.add(waypoint);
            manager.setDirty();

            holding.consume(1, player);

            double x = pos.getX() + .5, y = pos.getY() + .5, z = pos.getZ() + .5;

            {
                List<ItemEntity> itemEntities = level.getEntitiesOfClass(ItemEntity.class,
                        new AABB(pos).inflate(1),
                        itemEntity -> true);
                if (!itemEntities.isEmpty()) {
                    waypoint.setGuiItemStack(itemEntities.getFirst().getItem());
                }
            }

            // Cosmetic
            serverLevel.playSound(null, x, y, z, SoundEvents.END_PORTAL_SPAWN,
                    SoundSource.PLAYERS, 1, 1);

            serverLevel.sendParticles(ParticleTypes.WITCH, x, y, z, 150, 1.0,
                    1.0, 1.0, 0.2);

            return InteractionResult.CONSUME;
        });
    }

    public static boolean isValidWaypoint(Level level, BlockPos pos) {
        // Get the BlockEntity at the specified position
        BlockEntity blockEntity = level.getBlockEntity(pos);

        if (blockEntity instanceof BeaconBlockEntity) {
            int beaconLevel = BeaconUpdateBaseInvoker.invokeUpdateBase(level, pos.getX(), pos.getY(), pos.getZ());

            return beaconLevel > 0;
        } else {
            // The block entity at the given position is not a BeaconBlockEntity
            return false;
        }
    }
}