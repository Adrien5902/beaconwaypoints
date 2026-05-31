package net.ddns.adrien5902.beaconwaypoints;

import java.util.ArrayList;

import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;

public class WaypointsManagerWithLevel {
    public ServerLevel level;
    public WaypointsManager manager;

    public WaypointsManagerWithLevel(ServerLevel level, WaypointsManager manager) {
        this.level = level;
        this.manager = manager;
    }

    public static ArrayList<WaypointsManagerWithLevel> readGlobal(MinecraftServer server) {
        ArrayList<WaypointsManagerWithLevel> list = new ArrayList<>();

        for (ServerLevel level : server.getAllLevels()) {
            WaypointsManager manager = WaypointsManager.fromLevel(level);
            list.add(new WaypointsManagerWithLevel(level, manager));
        }

        return list;
    }

    public void verifyWaypointsValidity() {
        for (int i = manager.waypoints.size() - 1; i >= 0; --i) {
            if (!WaypointConstructor.isValidWaypoint(level, manager.waypoints.get(i).pos)) {
                manager.waypoints.remove(i);
                manager.setDirty();
            }
        }
    }

    public static WaypointsManagerWithLevel fromLevel(ServerLevel level) {
        WaypointsManager manager = WaypointsManager.fromLevel(level);
        WaypointsManagerWithLevel withLevel = new WaypointsManagerWithLevel(level, manager);

        withLevel.verifyWaypointsValidity();

        return withLevel;
    }
}