package net.ddns.adrien5902.beaconwaypoints;

import net.minecraft.world.PersistentState;
import net.minecraft.world.PersistentStateManager;

import java.util.ArrayList;

import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.registry.RegistryWrapper.WrapperLookup;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.world.ServerWorld;

public class WaypointsManager extends PersistentState {
    public ArrayList<Waypoint> waypoints = new ArrayList<Waypoint>();
    public ServerWorld world;

    private static final String WAYPOINTS = "waypoints";

    WaypointsManager(ServerWorld world) {
        this.world = world;
    }

    WaypointsManager(ServerWorld world, ArrayList<Waypoint> waypoints) {
        this.world = world;
        this.waypoints = waypoints;
    }

    public static ArrayList<WaypointsManager> readGlobal(MinecraftServer server) {
        ArrayList<WaypointsManager> list = new ArrayList<WaypointsManager>();

        for (ServerWorld world : server.getWorlds()) {
            WaypointsManager manager = WaypointsManager.fromWorld(world);
            list.add(manager);
        }

        return list;
    }

    @Override
    public NbtCompound writeNbt(NbtCompound nbt, WrapperLookup registryLookup) {
        NbtList nbtList = new NbtList();

        for (Waypoint waypoint : this.waypoints) {
            NbtCompound waypointNbt = new NbtCompound();
            waypoint.writeNbt(waypointNbt, registryLookup);

            nbtList.add(waypointNbt);
        }

        nbt.put(WAYPOINTS, nbtList);

        return nbt;
    }

    public static WaypointsManager fromWorld(ServerWorld world) {
        PersistentStateManager persistentStateManager = world.getPersistentStateManager();
        WaypointsManager manager = persistentStateManager.getOrCreate(getPersistentStateType(world), WAYPOINTS);

        for (int i = manager.waypoints.size() - 1; i >= 0; --i) {
            if (!WaypointConstructor.isValidWaypoint(world, manager.waypoints.get(i).pos)) {
                manager.waypoints.remove(i);
            }
        }

        manager.markDirty();

        return manager;
    }

    public static Type<WaypointsManager> getPersistentStateType(ServerWorld world) {
        return new Type<WaypointsManager>(() -> new WaypointsManager(world),
                (NbtCompound nbt, RegistryWrapper.WrapperLookup wrapper) -> fromNbt(world, nbt, wrapper), null);
    }

    public static WaypointsManager fromNbt(ServerWorld world, NbtCompound nbt,
            RegistryWrapper.WrapperLookup registries) {
        NbtList list = nbt.getList(WAYPOINTS, NbtElement.COMPOUND_TYPE);

        ArrayList<Waypoint> waypoints = new ArrayList<Waypoint>();
        for (NbtElement nbt_element : list) {
            NbtCompound waypoint_nbt = (NbtCompound) nbt_element;

            Waypoint waypoint = Waypoint.fromNbt(waypoint_nbt, registries);
            waypoints.add(waypoint);
        }

        return new WaypointsManager(world, waypoints);
    }

}