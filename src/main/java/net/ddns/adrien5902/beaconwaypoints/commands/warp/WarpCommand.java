package net.ddns.adrien5902.beaconwaypoints.commands.warp;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;

import java.util.ArrayList;
import java.util.Locale;
import java.util.Set;

import net.ddns.adrien5902.beaconwaypoints.Waypoint;
import net.ddns.adrien5902.beaconwaypoints.WaypointsManager;
import net.ddns.adrien5902.beaconwaypoints.WaypointsManagerWithWorld;
import net.ddns.adrien5902.beaconwaypoints.WaypointConstructor;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.math.Vec3d;

public class WarpCommand {
    public static void register() {
        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> {

            // Rewritten registration with intermediate builders for clarity
            LiteralArgumentBuilder<ServerCommandSource> warpRoot = CommandManager.literal("warp")
                    .executes(context -> {
                        try {
                            ServerCommandSource src = context.getSource();
                            ArrayList<WaypointsManagerWithWorld> managers = WaypointsManagerWithWorld
                                    .readGlobal(src.getServer());
                            WarpCommandGui gui = new WarpCommandGui(src, managers);
                            gui.open();
                        } catch (Exception e) {
                            System.out.println(e);
                        }
                        return Command.SINGLE_SUCCESS;
                    });

            // Direct warp by name argument
            warpRoot.then(CommandManager.argument("waypoint", StringArgumentType.string())
                    .suggests(new WaypointSuggestionProvider())
                    .executes(context -> {
                        ServerCommandSource src = context.getSource();
                        String waypoint_name_raw = StringArgumentType.getString(context, "waypoint");
                        String waypoint_name = stripQuotes(waypoint_name_raw);
                        ArrayList<WaypointsManagerWithWorld> withWorlds = WaypointsManagerWithWorld
                                .readGlobal(src.getServer());
                        Waypoint found_waypoint = null;
                        ServerWorld current_world = null;
                        WaypointsManager found_manager = null;
                        outer: for (WaypointsManagerWithWorld withWorld : withWorlds) {
                            for (Waypoint waypoint : withWorld.manager.waypoints) {
                                current_world = withWorld.world;
                                if (equalsIgnoreCase(waypoint_name, waypoint.name)) {
                                    found_waypoint = waypoint;
                                    found_manager = withWorld.manager;
                                    break outer;
                                }
                            }
                        }
                        if (found_waypoint == null) {
                            src.sendError(Text.translatable("beaconwaypoints.error.waypoint_not_found"));
                            return 0;
                        }
                        teleportTo(src, current_world, found_waypoint, found_manager);
                        return Command.SINGLE_SUCCESS;
                    }));

            // Remove subcommand
            LiteralArgumentBuilder<ServerCommandSource> removeRoot = CommandManager.literal("remove")
                    .then(CommandManager.argument("waypoint", StringArgumentType.string())
                            .suggests(new WaypointSuggestionProvider())
                            .executes(context -> {
                                ServerCommandSource src = context.getSource();
                                String waypoint_name_raw = StringArgumentType.getString(context, "waypoint");
                                String waypoint_name = stripQuotes(waypoint_name_raw);
                                ArrayList<WaypointsManagerWithWorld> withWorlds = WaypointsManagerWithWorld
                                        .readGlobal(src.getServer());
                                Waypoint found_waypoint = null;
                                WaypointsManager found_manager = null;
                                ServerWorld current_world = null;
                                outer: for (WaypointsManagerWithWorld withWorld : withWorlds) {
                                    for (Waypoint waypoint : withWorld.manager.waypoints) {
                                        current_world = withWorld.world;
                                        if (equalsIgnoreCase(waypoint_name, waypoint.name)) {
                                            found_waypoint = waypoint;
                                            found_manager = withWorld.manager;
                                            break outer;
                                        }
                                    }
                                }
                                if (found_waypoint == null) {
                                    src.sendError(Text
                                            .translatable("beaconwaypoints.error.waypoint_removal_failed"));
                                    return 0;
                                }
                                final Vec3d beaconCenter = found_waypoint.pos.toCenterPos();
                                final String removedName = found_waypoint.name;
                                final net.minecraft.util.math.BlockPos targetPos = found_waypoint.pos;
                                boolean removed = found_manager.waypoints.removeIf(w -> w.pos.equals(targetPos));
                                if (removed) {
                                    found_manager.markDirty();
                                    src.sendFeedback(
                                            () -> Text.translatable("beaconwaypoints.success.waypoint_removed",
                                                    removedName),
                                            false);
                                    if (current_world != null) {
                                        current_world.playSound(null, beaconCenter.x, beaconCenter.y, beaconCenter.z,
                                                SoundEvents.BLOCK_BEACON_DEACTIVATE, SoundCategory.PLAYERS, 1, 1);
                                    }
                                    return Command.SINGLE_SUCCESS;
                                } else {
                                    src.sendError(Text.translatable("beaconwaypoints.error.waypoint_removal_failed"));
                                    return 0;
                                }
                            }));

            warpRoot.then(removeRoot);

            dispatcher.register(warpRoot);
        });
    }

    private static boolean equalsIgnoreCase(String a, String b) {
        return a.toLowerCase(Locale.ROOT).equals(b.toLowerCase(Locale.ROOT));
    }

    private static String stripQuotes(String s) {
        if (s.length() >= 2 && ((s.startsWith("\"") && s.endsWith("\"")) || (s.startsWith("'") && s.endsWith("'")))) {
            return s.substring(1, s.length() - 1);
        }
        return s;
    }

    public static void teleportTo(ServerCommandSource src, ServerWorld world, Waypoint waypoint,
            WaypointsManager manager) {
        // Validate beacon still exists; if not remove waypoint and abort.
        if (!WaypointConstructor.isValidWaypoint(world, waypoint.pos)) {
            if (manager != null) {
                manager.waypoints.removeIf(w -> w.pos.equals(waypoint.pos));
                manager.markDirty();
            }
            src.sendError(Text.translatable("beaconwaypoints.error.waypoint_teleport_invalid", waypoint.name));
            return;
        }

        Vec3d pos = waypoint.pos.toCenterPos();

        src.getPlayer().teleport(world, pos.x, pos.y + 0.5, pos.z, Set.of(), 0, 0, false);

        world.playSound(null, pos.x, pos.y, pos.z, SoundEvents.ENTITY_PLAYER_TELEPORT,
                SoundCategory.PLAYERS, 1, 1);

        world.spawnParticles(ParticleTypes.WITCH, pos.x, pos.y, pos.z, 50, 0, 0.5, 0,
                2);

        src.sendFeedback(
                () -> Text.translatable("beaconwaypoints.success.teleporting_to", waypoint.name), false);
    }
}
