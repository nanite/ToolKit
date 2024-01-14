package com.sunekaer.toolkit.commands.inventory;

import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import com.sunekaer.toolkit.ToolkitPlatform;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public enum InventoryCollector {
    HAND("hand", (player) -> {
        ItemStack stack = player.getMainHandItem();

        return stack.isEmpty() ? List.of() : List.of(stack);
    }),
    OFFHAND("offhand", (player) -> {
        ItemStack stack = player.getOffhandItem();

        return stack.isEmpty() ? List.of() : List.of(stack);
    }),
    HOTBAR("hotbar", (player) -> {
        List<ItemStack> items = new ArrayList<>();
        for (int i = 0; i < 9; i++) {
            ItemStack stack = player.getInventory().getItem(i);
            if (!stack.isEmpty()) {
                items.add(stack);
            }
        }
        return items;
    }),
    INVENTORY("inventory", (player) -> {
        List<ItemStack> items = new ArrayList<>();
        Inventory inventory = player.getInventory();
        for (int i = 9; i < inventory.getContainerSize(); i++) {
            ItemStack stack = player.getInventory().getItem(i);
            if (!stack.isEmpty()) {
                items.add(stack);
            }
        }
        return items;
    }),
    INVENTORY_AND_HOTBAR("inventory_and_hotbar", (player) -> {
        List<ItemStack> items = new ArrayList<>();
        items.addAll(HOTBAR.itemCollector.apply(player));
        items.addAll(INVENTORY.itemCollector.apply(player));
        return items;
    }),
    ARMOR("armor", (player) -> {
        List<ItemStack> items = new ArrayList<>();
        for (ItemStack slot : player.getArmorSlots()) {
            if (!slot.isEmpty()) {
                items.add(slot);
            }
        }
        return items;
    }),
    TARGET_INVENTORY("target_inventory", (player) -> {
        List<ItemStack> items = new ArrayList<>();

        HitResult pick = player.pick(20, 0, true);
        if (!(pick instanceof BlockHitResult blockHit)) {
            return items;
        }

        var level = player.level;
        return ToolkitPlatform.getInventoryFromBlockEntity(level, blockHit.getBlockPos(), blockHit.getDirection());
    });

    String name;
    ItemCollector itemCollector;

    InventoryCollector(String name, ItemCollector itemCollector) {
        this.name = name;
        this.itemCollector = itemCollector;
    }

    public String getName() {
        return name;
    }

    public static InventoryCollector fromString(String name) {
        for (InventoryCollector type : InventoryCollector.values()) {
            if (type.name.equalsIgnoreCase(name)) {
                return type;
            }
        }
        return null;
    }

    public static CompletableFuture<Suggestions> suggestions(CommandContext<CommandSourceStack> context, SuggestionsBuilder suggestionsBuilder) {
        Arrays.stream(InventoryCollector.values()).toList().forEach(type -> suggestionsBuilder.suggest(type.getName()));
        return CompletableFuture.completedFuture(suggestionsBuilder.build());
    }

    @FunctionalInterface
    interface ItemCollector {
        List<ItemStack> apply(Player player);
    }
}
