package net.ddns.adrien5902.beaconwaypoints;

import net.minecraft.world.PersistentState;
import net.minecraft.world.PersistentStateType;

import java.util.ArrayList;
import java.util.List;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.server.world.ServerWorld;

public class WaypointsManager extends PersistentState {
    private static final String WAYPOINTS = "waypoints";
    public static final Codec<WaypointsManager> CODEC = RecordCodecBuilder.create(
            instance -> instance.group(
                    Waypoint.CODEC
                            .listOf()
                            .optionalFieldOf("waypoints", List.of())
                            .forGetter(waypointsManager -> waypointsManager.waypoints))
                    .apply(instance, WaypointsManager::new));

    private static final PersistentStateType<WaypointsManager> type = new PersistentStateType<WaypointsManager>(
            WAYPOINTS,
            WaypointsManager::new,
            CODEC,
            null);

    public ArrayList<Waypoint> waypoints = new ArrayList<Waypoint>();

    WaypointsManager() {
        this.markDirty();
        this.waypoints = new ArrayList<>();
    }

    WaypointsManager(List<Waypoint> waypoints) {
        this.waypoints = new ArrayList<Waypoint>(waypoints);
    }

    public static WaypointsManager fromWorld(ServerWorld world) {
        return world.getPersistentStateManager().getOrCreate(type);
    }
}