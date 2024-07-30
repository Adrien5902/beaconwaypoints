package net.ddns.adrien5902.beaconwaypoints.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

import net.minecraft.world.World;

@Mixin(net.minecraft.block.entity.BeaconBlockEntity.class)
public interface BeaconUpdateLevelInvoker {
    @Invoker("updateLevel")
    static int invokeUpdateLevel(World world, int x, int y, int z) {
        throw new UnsupportedOperationException("This method should not be called directly.");
    }
}
