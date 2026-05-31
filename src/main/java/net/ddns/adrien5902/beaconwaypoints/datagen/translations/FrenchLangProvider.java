package net.ddns.adrien5902.beaconwaypoints.datagen.translations;

import java.util.concurrent.CompletableFuture;

import net.fabricmc.fabric.api.datagen.v1.FabricPackOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricLanguageProvider;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.HolderLookup.Provider;

public class FrenchLangProvider extends FabricLanguageProvider {
        public FrenchLangProvider(FabricPackOutput dataOutput,
                        CompletableFuture<HolderLookup.Provider> registryLookup) {
                super(dataOutput, "fr_fr", registryLookup);
        }

        @Override
        public void generateTranslations(Provider registryLookup, TranslationBuilder translationBuilder) {
                translationBuilder.add("beaconwaypoints.error.waypoint_already_set_up_here",
                                "Cette balise est déjà un point de téléportation");
                translationBuilder.add("beaconwaypoints.error.waypoint_not_found",
                                "Impossible de trouver ce point de téléportation");
                translationBuilder.add("beaconwaypoints.error.waypoint_removal_failed",
                                "Impossible de retirer ce point de téléportation");
                translationBuilder.add("beaconwaypoints.error.waypoint_teleport_invalid",
                                "Balise introuvable. Le point de téléportation \"%1$s\" a été retiré.");

                translationBuilder.add("beaconwaypoints.success.teleporting_to",
                                "Téléportation vers \"%1$s\"...");
                translationBuilder.add("beaconwaypoints.success.waypoint_removed",
                                "Le point de téléportation \"%1$s\" a été retiré");

                translationBuilder.add("beaconwaypoints.gui.on_page",
                                "Page: %1$s");
                translationBuilder.add("beaconwaypoints.gui.go_to_page",
                                "Aller à la page: %1$s");
        }
}