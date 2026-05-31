package net.ddns.adrien5902.beaconwaypoints.commands.warp;

import eu.pb4.sgui.api.elements.GuiElementBuilder;
import eu.pb4.sgui.api.elements.SimpleGuiElement;
import eu.pb4.sgui.api.gui.SimpleGui;
import java.util.Arrays;
import java.util.ArrayList;
import java.util.List;
import com.mojang.datafixers.util.Pair;
import net.ddns.adrien5902.beaconwaypoints.Waypoint;
import net.ddns.adrien5902.beaconwaypoints.WaypointsManagerWithLevel;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.component.ItemLore;

public class WarpCommandGui {
    private CommandSourceStack src;
    private SimpleGui gui;
    private int page = 0;
    private List<Pair<Waypoint, ServerLevel>> waypoints;
    private int max_page;

    private static final int PAGE_SIZE = 18;
    private static final int LEFT_BOTTOM = PAGE_SIZE;
    private static final int MIDDLE_BOTTOM = PAGE_SIZE + 4;
    private static final int RIGHT_BOTTOM = PAGE_SIZE + 8;

    public WarpCommandGui(CommandSourceStack src, ArrayList<WaypointsManagerWithLevel> withLevels) {
        this.src = src;
        this.gui = new SimpleGui(MenuType.GENERIC_9x3, src.getPlayer(), false);
        gui.setTitle(Component.literal("Beacon Waypoints"));
        gui.setAutoUpdate(true);
        this.waypoints = withLevels.stream().flatMap(
                (withLevel) -> withLevel.manager.waypoints.stream()
                        .map((waypoint) -> new Pair<Waypoint, ServerLevel>(waypoint, withLevel.level)))
                .toList();

        this.max_page = Math.floorDiv(waypoints.size() == 0 ? 0 : waypoints.size() - 1, PAGE_SIZE);
    }

    public void open() {
        gui.open();
        this.refresh();
    }

    private void refresh() {
        for (int i = 0; i < PAGE_SIZE; i++) {
            gui.clearSlot(i);
        }

        List<SimpleGuiElement> uiElements = getPageUiElements(page);
        for (int i = 0; i < uiElements.size(); i++) {
            gui.setSlot(i, uiElements.get(i));
        }

        if (page > 0) {
            gui.setSlot(LEFT_BOTTOM,
                    new GuiElementBuilder().setItem(Items.ARROW)
                            .setName(Component.translatable("beaconwaypoints.gui.go_to_page", page))
                            .setCount(page)
                            .setCallback((index, type, action, gui) -> {
                                if (page > 0) {
                                    page--;
                                    refresh();
                                }
                            })
                            .build());
        } else {
            gui.clearSlot(LEFT_BOTTOM);
        }

        if (page < max_page) {
            gui.setSlot(RIGHT_BOTTOM,
                    new GuiElementBuilder().setItem(Items.ARROW)
                            .setName(Component.translatable("beaconwaypoints.gui.go_to_page", page + 2))
                            .setCount(page + 2)
                            .setCallback((index, type, action, gui) -> {
                                if (page < max_page) {
                                    page++;
                                }
                                refresh();
                            })
                            .build());
        } else {
            gui.clearSlot(RIGHT_BOTTOM);
        }

        gui.setSlot(MIDDLE_BOTTOM,
                new GuiElementBuilder().setItem(Items.PAPER)
                        .setName(Component.translatable("beaconwaypoints.gui.on_page", page + 1))
                        .setCount(page + 1)
                        .build());
    }

    private List<SimpleGuiElement> getPageUiElements(int page) {
        return getPage(page).stream().map((pair) -> {
            Waypoint waypoint = pair.getFirst();
            ServerLevel level = pair.getSecond();
            ItemStack stack = waypoint.getGuiItemStack();

            stack.set(DataComponents.CUSTOM_NAME,
                    Component.literal(waypoint.name).withStyle(style -> style.withItalic(false)));

            ItemLore lore = new ItemLore(Arrays.asList(waypoint.getTooltip(level)));
            stack.set(DataComponents.LORE, lore);

            return new GuiElementBuilder(stack).setCallback((index, type, action, gui) -> {
                // Need manager for validity removal; re-fetch manager via world
                WaypointsManagerWithLevel.fromLevel(level).manager.setDirty(); // ensure state is loaded
                WarpCommand.teleportTo(src, level, waypoint,
                        WaypointsManagerWithLevel.fromLevel(level).manager);
                gui.close();
            })
                    .build();

        }).toList();
    }

    private List<Pair<Waypoint, ServerLevel>> getPage(int page) {
        int max_index = this.waypoints.size() < (page + 1) * PAGE_SIZE ? this.waypoints.size()
                : (page + 1) * PAGE_SIZE;
        return this.waypoints.subList(page * PAGE_SIZE, max_index);
    }
}
