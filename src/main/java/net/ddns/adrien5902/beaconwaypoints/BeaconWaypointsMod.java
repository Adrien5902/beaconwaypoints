package net.ddns.adrien5902.beaconwaypoints;

import net.ddns.adrien5902.beaconwaypoints.commands.warp.WarpCommand;
import net.fabricmc.api.ModInitializer;
import net.minecraft.util.Identifier;

public class BeaconWaypointsMod implements ModInitializer {

	@Override
	public void onInitialize() {
		WarpCommand.register();
		WaypointConstructor.register();
	}

	public static final String MOD_NAMESPACE = "beaconwaypoints";

	public static Identifier id(String path) {
		return Identifier.of(MOD_NAMESPACE, path);
	}
}