package net.ddns.adrien5902.beaconwaypoints.commands.warp;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.StringArgumentType;

import java.util.ArrayList;

import net.ddns.adrien5902.beaconwaypoints.Waypoint;
import net.ddns.adrien5902.beaconwaypoints.WaypointsManager;
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

            dispatcher.register(
                    CommandManager.literal("warp").executes((context) -> {
                        // Open Menu

                        try {
                            ServerCommandSource src = context.getSource();

                            ArrayList<WaypointsManager> managers = WaypointsManager
                                    .readGlobal(src.getServer());

                            WarpCommandGui gui = new WarpCommandGui(src, managers);
                            gui.open();
                        } catch (Exception e) {
                            System.out.println(e);
                        }

                        return Command.SINGLE_SUCCESS;
                    })
                            .then(CommandManager.argument("waypoint", StringArgumentType.string())
                                    .suggests(new WaypointSuggestionProvider()).executes(context -> {
                                        // Teleport directly

                                        ServerCommandSource src = context.getSource();

                                        String waypoint_name = StringArgumentType.getString(context, "waypoint");

                                        ArrayList<WaypointsManager> managers = WaypointsManager
                                                .readGlobal(src.getServer());

                                        Waypoint found_waypoint = null;
                                        ServerWorld current_world = null;
                                        outer: for (WaypointsManager manager : managers) {
                                            for (Waypoint waypoint : manager.waypoints) {
                                                current_world = manager.world;
                                                if (waypoint_name.equals(waypoint.name)) {
                                                    found_waypoint = waypoint;
                                                    break outer;
                                                }
                                            }
                                        }

                                        if (found_waypoint == null) {
                                            src.sendError(Text.literal("Waypoint introuvable"));
                                            return 0;
                                        }

                                        teleportTo(src, current_world, found_waypoint);

                                        return Command.SINGLE_SUCCESS;
                                    })));
        });
    }

    public static void teleportTo(ServerCommandSource src, ServerWorld world, Waypoint waypoint) {
        Vec3d pos = waypoint.pos.toCenterPos();

        src.getPlayer().teleport(world, pos.x, pos.y + 0.5, pos.z, 0,
                0);

        src.sendFeedback(
                () -> Text.literal("Teleporting to " + waypoint.name + "..."), false);

        world.playSound(null, pos.x, pos.y, pos.z, SoundEvents.ENTITY_PLAYER_TELEPORT,
                SoundCategory.PLAYERS, 1, 1);

        world.spawnParticles(ParticleTypes.WITCH, pos.x, pos.y, pos.z, 50, 0, 0.5, 0,
                2);
    }
}
