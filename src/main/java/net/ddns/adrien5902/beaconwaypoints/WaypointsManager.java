package net.ddns.adrien5902.beaconwaypoints;

import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.saveddata.SavedData;
import net.minecraft.world.level.saveddata.SavedDataType;

import java.util.ArrayList;
import java.util.List;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

public class WaypointsManager extends SavedData {
    private static final String WAYPOINTS = "waypoints";
    public static final Codec<WaypointsManager> CODEC = RecordCodecBuilder.create(
            instance -> instance.group(
                    Waypoint.CODEC
                            .listOf()
                            .optionalFieldOf("waypoints", List.of())
                            .forGetter(waypointsManager -> waypointsManager.waypoints))
                    .apply(instance, WaypointsManager::new));

    private static final SavedDataType<WaypointsManager> type = new SavedDataType<WaypointsManager>(
            Identifier.fromNamespaceAndPath(BeaconWaypointsMod.MOD_NAMESPACE, WAYPOINTS),
            WaypointsManager::new,
            CODEC,
            null);

    public ArrayList<Waypoint> waypoints = new ArrayList<Waypoint>();

    WaypointsManager() {
        this.setDirty();
        this.waypoints = new ArrayList<>();
    }

    WaypointsManager(List<Waypoint> waypoints) {
        this.waypoints = new ArrayList<Waypoint>(waypoints);
    }

    public static WaypointsManager fromLevel(ServerLevel world) {
        return world.getDataStorage().computeIfAbsent(type);
    }
}