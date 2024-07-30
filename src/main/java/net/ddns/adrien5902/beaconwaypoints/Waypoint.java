package net.ddns.adrien5902.beaconwaypoints;

import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class Waypoint {
    public BlockPos pos;
    public String name;
    private ItemStack gui_item = null;

    Waypoint(String name, BlockPos pos) {
        this.pos = pos;
        this.name = name;
    }

    public static Waypoint unamed(BlockPos pos) {
        return new Waypoint("Unamed", pos);
    }

    public NbtCompound writeNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registries) {
        nbt.putString("name", name);
        NbtCompound pos = new NbtCompound();

        pos.putInt("x", this.pos.getX());
        pos.putInt("y", this.pos.getY());
        pos.putInt("z", this.pos.getZ());
        nbt.put("pos", pos);

        if (this.gui_item != null) {
            nbt.put("gui_item", this.gui_item.encode(registries));
        }

        return nbt;
    }

    public static Waypoint fromNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registries) {
        String name = nbt.getString("name");

        NbtCompound pos = nbt.getCompound("pos");

        int x = pos.getInt("x");
        int y = pos.getInt("y");
        int z = pos.getInt("z");

        BlockPos block_pos = new BlockPos(x, y, z);

        Waypoint waypoint = new Waypoint(name, block_pos);

        if (nbt.contains("gui_item", NbtCompound.COMPOUND_TYPE)) {
            NbtCompound itemNbt = nbt.getCompound("gui_item");
            waypoint.setGuiItemStack((ItemStack.fromNbt(registries, itemNbt).orElse(null)));
        }

        return waypoint;
    }

    public Text getTooltip(World world) {
        return Text
                .literal(String.format("x: %d, y: %d, z: %d in %s", this.pos.getX(), this.pos.getY(), this.pos.getZ(),
                        world.getRegistryKey().getValue()));
    }

    public ItemStack getGuiItemStack() {
        return this.gui_item != null ? this.gui_item : new ItemStack(Items.BEACON);
    }

    public void setGuiItemStack(ItemStack stack) {
        this.gui_item = stack;
    }
}
