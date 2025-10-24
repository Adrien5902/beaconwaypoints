package net.ddns.adrien5902.beaconwaypoints;

import java.util.ArrayList;

import net.minecraft.server.MinecraftServer;
import net.minecraft.server.world.ServerWorld;

public class WaypointsManagerWithWorld {
    public ServerWorld world;
    public WaypointsManager manager;

    public WaypointsManagerWithWorld(ServerWorld world, WaypointsManager manager) {
        this.world = world;
        this.manager = manager;
    }

    public static ArrayList<WaypointsManagerWithWorld> readGlobal(MinecraftServer server) {
        ArrayList<WaypointsManagerWithWorld> list = new ArrayList<>();

        for (ServerWorld world : server.getWorlds()) {
            WaypointsManager manager = WaypointsManager.fromWorld(world);
            list.add(new WaypointsManagerWithWorld(world, manager));
        }

        return list;
    }

    public void verifyWaypointsValidity() {
        for (int i = manager.waypoints.size() - 1; i >= 0; --i) {
            if (!WaypointConstructor.isValidWaypoint(world, manager.waypoints.get(i).pos)) {
                manager.waypoints.remove(i);
                manager.markDirty();
            }
        }
    }

    public static WaypointsManagerWithWorld fromWorld(ServerWorld world) {
        WaypointsManager manager = WaypointsManager.fromWorld(world);
        WaypointsManagerWithWorld withWorld = new WaypointsManagerWithWorld(world, manager);

        withWorld.verifyWaypointsValidity();

        return withWorld;
    }
}