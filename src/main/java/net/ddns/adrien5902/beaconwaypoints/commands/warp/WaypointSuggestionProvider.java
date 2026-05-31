package net.ddns.adrien5902.beaconwaypoints.commands.warp;

import java.util.concurrent.CompletableFuture;

import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;

import net.ddns.adrien5902.beaconwaypoints.Waypoint;
import net.ddns.adrien5902.beaconwaypoints.WaypointsManagerWithLevel;
import net.minecraft.commands.CommandSourceStack;

import java.util.ArrayList;

public class WaypointSuggestionProvider implements SuggestionProvider<CommandSourceStack> {

    @Override
    public CompletableFuture<Suggestions> getSuggestions(CommandContext<CommandSourceStack> context,
            SuggestionsBuilder builder) throws CommandSyntaxException {

        ArrayList<WaypointsManagerWithLevel> withWorlds = WaypointsManagerWithLevel
                .readGlobal(context.getSource().getServer());

        for (WaypointsManagerWithLevel withWorld : withWorlds) {
            for (Waypoint waypoint : withWorld.manager.waypoints) {
                if (waypoint.name.matches("[A-Za-z0-9]+")) {
                    builder.suggest(waypoint.name, waypoint.getTooltip(withWorld.level));
                } else {
                    builder.suggest('"' + waypoint.name + '"', waypoint.getTooltip(withWorld.level));
                }
            }
        }

        return builder.buildFuture();
    }
}
