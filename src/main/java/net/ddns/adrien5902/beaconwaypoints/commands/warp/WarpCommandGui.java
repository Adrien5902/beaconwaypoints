package net.ddns.adrien5902.beaconwaypoints.commands.warp;

import eu.pb4.sgui.api.ClickType;
import eu.pb4.sgui.api.elements.GuiElement;
import eu.pb4.sgui.api.elements.GuiElementBuilder;
import eu.pb4.sgui.api.gui.SimpleGui;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.LoreComponent;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.Pair;

import java.util.Arrays;
import java.util.ArrayList;
import java.util.List;

import net.ddns.adrien5902.beaconwaypoints.Waypoint;
import net.ddns.adrien5902.beaconwaypoints.WaypointsManager;

public class WarpCommandGui {
    private ServerCommandSource src;
    private SimpleGui gui;
    private int page = 0;
    private List<Pair<Waypoint, ServerWorld>> waypoints;
    private int max_page;

    private static final int PAGE_SIZE = 18;
    private static final int LEFT_BOTTOM = PAGE_SIZE;
    private static final int MIDDLE_BOTTOM = PAGE_SIZE + 4;
    private static final int RIGHT_BOTTOM = PAGE_SIZE + 8;

    public WarpCommandGui(ServerCommandSource src, ArrayList<WaypointsManager> waypoints_managers) {
        this.src = src;
        this.gui = new SimpleGui(ScreenHandlerType.GENERIC_9X3, src.getPlayer(), false);
        gui.setTitle(Text.literal("Beacon Waypoints"));
        gui.setAutoUpdate(true);
        this.waypoints = waypoints_managers.stream().flatMap(
                (manager) -> manager.waypoints.stream()
                        .map((waypoint) -> new Pair<Waypoint, ServerWorld>(waypoint, manager.world)))
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

        List<GuiElement> uiElements = getPageUiElements(page);
        for (int i = 0; i < uiElements.size(); i++) {
            gui.setSlot(i, uiElements.get(i));
        }

        if (page > 0) {
            gui.setSlot(LEFT_BOTTOM,
                    new GuiElementBuilder().setItem(Items.ARROW).setName(Text.literal("Go to page " + (page)))
                            .setCount(page)
                            .setCallback((int index, ClickType type, SlotActionType action) -> {
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
                    new GuiElementBuilder().setItem(Items.ARROW).setName(Text.literal("Go to page " + (page + 2)))
                            .setCount(page + 2)
                            .setCallback((int index, ClickType type, SlotActionType action) -> {
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
                new GuiElementBuilder().setItem(Items.PAPER).setName(Text.literal("On page " + (page + 1)))
                        .setCount(page + 1)
                        .build());
    }

    private List<GuiElement> getPageUiElements(int page) {
        return getPage(page).stream().map((pair) -> {
            ItemStack stack = pair.getLeft().getGuiItemStack();

            stack.set(DataComponentTypes.CUSTOM_NAME, Text.literal(pair.getLeft().name));

            LoreComponent lore = new LoreComponent(Arrays.asList(pair.getLeft().getTooltip(pair.getRight())));
            stack.set(DataComponentTypes.LORE, lore);

            return new GuiElement(stack, (int index, ClickType type, SlotActionType action) -> {
                WarpCommand.teleportTo(src, pair.getRight(), pair.getLeft());
                gui.close();
            });
        }).toList();
    }

    private List<Pair<Waypoint, ServerWorld>> getPage(int page) {
        int max_index = this.waypoints.size() < (page + 1) * PAGE_SIZE ? this.waypoints.size()
                : (page + 1) * PAGE_SIZE;
        return this.waypoints.subList(page * PAGE_SIZE, max_index);
    }
}
