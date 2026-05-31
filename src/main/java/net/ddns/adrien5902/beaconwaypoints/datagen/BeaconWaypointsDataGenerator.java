package net.ddns.adrien5902.beaconwaypoints.datagen;

import net.ddns.adrien5902.beaconwaypoints.datagen.translations.EnglishLangProvider;
import net.ddns.adrien5902.beaconwaypoints.datagen.translations.FrenchLangProvider;
import net.fabricmc.fabric.api.datagen.v1.DataGeneratorEntrypoint;
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;

public class BeaconWaypointsDataGenerator implements DataGeneratorEntrypoint {
    @Override
    public void onInitializeDataGenerator(FabricDataGenerator fabricDataGenerator) {
        FabricDataGenerator.Pack pack = fabricDataGenerator.createPack();
        pack.addProvider(EnglishLangProvider::new);
        pack.addProvider(FrenchLangProvider::new);
    }
}