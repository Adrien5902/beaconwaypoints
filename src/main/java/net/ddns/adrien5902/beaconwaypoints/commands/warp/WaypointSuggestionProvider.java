package net.ddns.adrien5902.beaconwaypoints.commands.warp;

import java.util.concurrent.CompletableFuture;

import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;

import net.ddns.adrien5902.beaconwaypoints.Waypoint;
import net.ddns.adrien5902.beaconwaypoints.WaypointsManager;
import net.minecraft.server.command.ServerCommandSource;

import java.util.ArrayList;

public class WaypointSuggestionProvider implements SuggestionProvider<ServerCommandSource> {

    @Override
    public CompletableFuture<Suggestions> getSuggestions(CommandContext<ServerCommandSource> context,
            SuggestionsBuilder builder) throws CommandSyntaxException {

        ArrayList<WaypointsManager> managers = WaypointsManager.readGlobal(context.getSource().getServer());

        for (WaypointsManager manager : managers) {
            for (Waypoint waypoint : manager.waypoints) {
                if (waypoint.name.matches("[A-Za-z0-9]+")) {
                    builder.suggest(waypoint.name, waypoint.getTooltip(manager.world));
                } else {
                    builder.suggest('"' + waypoint.name + '"', waypoint.getTooltip(manager.world));
                }
            }
        }

        return builder.buildFuture();
    }

}
