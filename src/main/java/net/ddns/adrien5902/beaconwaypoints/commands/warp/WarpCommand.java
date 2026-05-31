package net.ddns.adrien5902.beaconwaypoints.commands.warp;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;

import java.util.ArrayList;
import java.util.Locale;
import java.util.Set;

import net.ddns.adrien5902.beaconwaypoints.Waypoint;
import net.ddns.adrien5902.beaconwaypoints.WaypointsManager;
import net.ddns.adrien5902.beaconwaypoints.WaypointsManagerWithLevel;
import net.ddns.adrien5902.beaconwaypoints.WaypointConstructor;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.phys.Vec3;

public class WarpCommand {
    public static void register() {
        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> {

            // Rewritten registration with intermediate builders for clarity
            LiteralArgumentBuilder<CommandSourceStack> warpRoot = Commands.literal("warp")
                    .executes(context -> {
                        try {
                            CommandSourceStack src = context.getSource();
                            ArrayList<WaypointsManagerWithLevel> managers = WaypointsManagerWithLevel
                                    .readGlobal(src.getServer());
                            WarpCommandGui gui = new WarpCommandGui(src, managers);
                            gui.open();
                        } catch (Exception e) {
                            System.out.println(e);
                        }
                        return Command.SINGLE_SUCCESS;
                    });

            // Direct warp by name argument
            warpRoot.then(Commands.argument("waypoint", StringArgumentType.string())
                    .suggests(new WaypointSuggestionProvider())
                    .executes(context -> {
                        CommandSourceStack src = context.getSource();
                        String waypoint_name_raw = StringArgumentType.getString(context, "waypoint");
                        String waypoint_name = stripQuotes(waypoint_name_raw);
                        ArrayList<WaypointsManagerWithLevel> withLevels = WaypointsManagerWithLevel
                                .readGlobal(src.getServer());
                        Waypoint found_waypoint = null;
                        ServerLevel current_level = null;
                        WaypointsManager found_manager = null;
                        outer: for (WaypointsManagerWithLevel withLevel : withLevels) {
                            for (Waypoint waypoint : withLevel.manager.waypoints) {
                                current_level = withLevel.level;
                                if (equalsIgnoreCase(waypoint_name, waypoint.name)) {
                                    found_waypoint = waypoint;
                                    found_manager = withLevel.manager;
                                    break outer;
                                }
                            }
                        }
                        if (found_waypoint == null) {
                            src.sendFailure(Component.translatable("beaconwaypoints.error.waypoint_not_found"));
                            return 0;
                        }
                        teleportTo(src, current_level, found_waypoint, found_manager);
                        return Command.SINGLE_SUCCESS;
                    }));

            // Remove subcommand
            LiteralArgumentBuilder<CommandSourceStack> removeRoot = Commands.literal("remove")
                    .then(Commands.argument("waypoint", StringArgumentType.string())
                            .suggests(new WaypointSuggestionProvider())
                            .executes(context -> {
                                CommandSourceStack src = context.getSource();
                                String waypoint_name_raw = StringArgumentType.getString(context, "waypoint");
                                String waypoint_name = stripQuotes(waypoint_name_raw);
                                ArrayList<WaypointsManagerWithLevel> withLevels = WaypointsManagerWithLevel
                                        .readGlobal(src.getServer());
                                Waypoint found_waypoint = null;
                                WaypointsManager found_manager = null;
                                ServerLevel current_level = null;
                                outer: for (WaypointsManagerWithLevel withLevel : withLevels) {
                                    for (Waypoint waypoint : withLevel.manager.waypoints) {
                                        current_level = withLevel.level;
                                        if (equalsIgnoreCase(waypoint_name, waypoint.name)) {
                                            found_waypoint = waypoint;
                                            found_manager = withLevel.manager;
                                            break outer;
                                        }
                                    }
                                }
                                if (found_waypoint == null) {
                                    src.sendFailure(Component
                                            .translatable("beaconwaypoints.error.waypoint_removal_failed"));
                                    return 0;
                                }
                                final Vec3 beaconCenter = found_waypoint.pos.getCenter();
                                final String removedName = found_waypoint.name;
                                final BlockPos targetPos = found_waypoint.pos;
                                boolean removed = found_manager.waypoints.removeIf(w -> w.pos.equals(targetPos));
                                if (removed) {
                                    found_manager.setDirty();
                                    src.sendSuccess(
                                            () -> Component.translatable("beaconwaypoints.success.waypoint_removed",
                                                    removedName),
                                            false);
                                    if (current_level != null) {
                                        current_level.playSound(null, beaconCenter.x, beaconCenter.y, beaconCenter.z,
                                                SoundEvents.BEACON_DEACTIVATE, SoundSource.PLAYERS, 1, 1);
                                    }
                                    return Command.SINGLE_SUCCESS;
                                } else {
                                    src.sendFailure(
                                            Component.translatable("beaconwaypoints.error.waypoint_removal_failed"));
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

    public static void teleportTo(CommandSourceStack src, ServerLevel level, Waypoint waypoint,
            WaypointsManager manager) {
        // Validate beacon still exists; if not remove waypoint and abort.
        if (!WaypointConstructor.isValidWaypoint(level, waypoint.pos)) {
            if (manager != null) {
                manager.waypoints.removeIf(w -> w.pos.equals(waypoint.pos));
                manager.setDirty();
            }
            src.sendFailure(Component.translatable("beaconwaypoints.error.waypoint_teleport_invalid", waypoint.name));
            return;
        }

        Vec3 pos = waypoint.pos.getCenter();

        src.getPlayer().teleportTo(level, pos.x, pos.y + 0.5, pos.z, Set.of(), 0f, 0f, false);

        level.playSound(null, pos.x, pos.y, pos.z, SoundEvents.PLAYER_TELEPORT,
                SoundSource.PLAYERS, 1, 1);

        level.sendParticles(ParticleTypes.WITCH, pos.x, pos.y, pos.z, 50, 0, 0.5, 0,
                2);

        src.sendSuccess(
                () -> Component.translatable("beaconwaypoints.success.teleporting_to", waypoint.name), false);
    }
}
