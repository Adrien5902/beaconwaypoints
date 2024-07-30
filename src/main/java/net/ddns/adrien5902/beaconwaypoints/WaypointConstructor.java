package net.ddns.adrien5902.beaconwaypoints;

import net.minecraft.text.Text;
import net.ddns.adrien5902.beaconwaypoints.mixin.BeaconUpdateLevelInvoker;
import net.fabricmc.fabric.api.event.player.UseBlockCallback;
import net.minecraft.block.entity.BeaconBlockEntity;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.ItemEntity;
import net.minecraft.item.EnderPearlItem;
import net.minecraft.item.ItemStack;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.ActionResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.world.World;
import java.util.List;

public class WaypointConstructor {
    public static void register() {
        UseBlockCallback.EVENT.register((player, world, hand, hitResult) -> {
            if (!(player instanceof ServerPlayerEntity)) {
                // Only handle this if the player is a server player
                return ActionResult.PASS;
            }

            ItemStack holding = player.getMainHandStack();

            if (!(holding.getItem() instanceof EnderPearlItem))
                return ActionResult.PASS;

            ServerWorld serverWorld = (ServerWorld) world;
            BlockPos pos = hitResult.getBlockPos();

            if (!isValidWaypoint(serverWorld, pos)) {
                return ActionResult.PASS;
            }

            Text name = holding.getName();

            // TODO: check for item renamed or not and so change waypoint's name if unamed
            Waypoint /* waypoint = null; */
            // if (name.getStyle().isItalic()) {
            waypoint = new Waypoint(name.getString(), pos);
            // } else {
            // waypoint = Waypoint.unamed(pos);
            // }

            if (WaypointsManager.fromWorld(serverWorld).waypoints.stream()
                    .anyMatch((w) -> w.pos.equals(pos))) {
                player.sendMessage(Text.literal("This is already a waypoint"));
                return ActionResult.CONSUME;
            }

            WaypointsManager manager = WaypointsManager.fromWorld(serverWorld);
            manager.waypoints.add(waypoint);
            manager.markDirty();

            holding.decrementUnlessCreative(1, player);

            double x = pos.getX() + .5, y = pos.getY() + .5, z = pos.getZ() + .5;

            {
                List<ItemEntity> itemEntities = world.getEntitiesByClass(ItemEntity.class, new Box(pos).expand(1),
                        itemEntity -> true);
                if (!itemEntities.isEmpty()) {
                    waypoint.setGuiItemStack(itemEntities.getFirst().getStack());
                }
            }

            // Cosmetic
            serverWorld.playSound(null, x, y, z, SoundEvents.BLOCK_END_PORTAL_SPAWN,
                    SoundCategory.PLAYERS, 1, 1);

            serverWorld.spawnParticles(ParticleTypes.DRAGON_BREATH, x, y, z, 150, 1, 1, 1,
                    0.2);

            return ActionResult.CONSUME;
        });
    }

    public static boolean isValidWaypoint(World world, BlockPos pos) {
        // Get the BlockEntity at the specified position
        BlockEntity blockEntity = world.getBlockEntity(pos);

        if (blockEntity instanceof BeaconBlockEntity) {
            int level = BeaconUpdateLevelInvoker.invokeUpdateLevel(world, pos.getX(), pos.getY(), pos.getZ());

            return level > 0;
        } else {
            // The block entity at the given position is not a BeaconBlockEntity
            return false;
        }
    }
}