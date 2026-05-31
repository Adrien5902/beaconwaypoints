package net.ddns.adrien5902.beaconwaypoints.datagen.translations;

import java.util.concurrent.CompletableFuture;

import net.fabricmc.fabric.api.datagen.v1.FabricPackOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricLanguageProvider;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.HolderLookup.Provider;

public class EnglishLangProvider extends FabricLanguageProvider {
        public EnglishLangProvider(FabricPackOutput dataOutput,
                        CompletableFuture<HolderLookup.Provider> registryLookup) {
                super(dataOutput, "en_us", registryLookup);
        }

        @Override
        public void generateTranslations(Provider registryLookup, TranslationBuilder translationBuilder) {
                translationBuilder.add("beaconwaypoints.error.waypoint_already_set_up_here",
                                "This beacon is already a waypoint");
                translationBuilder.add("beaconwaypoints.error.waypoint_not_found",
                                "Can't find this waypoint");
                translationBuilder.add("beaconwaypoints.error.waypoint_removal_failed",
                                "Failed to remove waypoint");
                translationBuilder.add("beaconwaypoints.error.waypoint_teleport_invalid",
                                "Beacon missing. Removed waypoint \"%1$s\".");

                translationBuilder.add("beaconwaypoints.success.teleporting_to",
                                "Teleporting to \"%1$s\"...");
                translationBuilder.add("beaconwaypoints.success.waypoint_removed",
                                "Removed waypoint \"%1$s\"");

                translationBuilder.add("beaconwaypoints.gui.on_page",
                                "On page: %1$s");
                translationBuilder.add("beaconwaypoints.gui.go_to_page",
                                "Go to page: %1$s");
        }
}