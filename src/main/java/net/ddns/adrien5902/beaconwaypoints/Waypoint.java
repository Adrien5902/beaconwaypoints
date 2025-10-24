package net.ddns.adrien5902.beaconwaypoints;

import java.util.Optional;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class Waypoint {
    public String name;
    public BlockPos pos;
    public Optional<ItemStack> gui_item;

    public static final Codec<Waypoint> CODEC = RecordCodecBuilder.create(
            instance -> instance
                    .group(
                            Codec.STRING.fieldOf("name").forGetter(wp -> wp.name),
                            BlockPos.CODEC.fieldOf("pos").forGetter(wp -> wp.pos),
                            ItemStack.CODEC.optionalFieldOf("gui_item").forGetter(wp -> wp.gui_item))
                    .apply(instance, Waypoint::new));

    public Waypoint(String name, BlockPos pos, Optional<ItemStack> gui_item) {
        this.pos = pos;
        this.name = name;
        this.gui_item = gui_item;
    }

    public Text getTooltip(World world) {
        return Text
                .literal(String.format("x: %d, y: %d, z: %d in %s", this.pos.getX(), this.pos.getY(), this.pos.getZ(),
                        world.getRegistryKey().getValue()))
                .styled(style -> style.withItalic(false));
    }

    public ItemStack getGuiItemStack() {
        return this.gui_item.orElse(new ItemStack(Items.BEACON));
    }

    public void setGuiItemStack(ItemStack stack) {
        this.gui_item = Optional.of(stack);
    }
}
