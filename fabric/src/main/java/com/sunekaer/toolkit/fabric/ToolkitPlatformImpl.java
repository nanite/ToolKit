package com.sunekaer.toolkit.fabric;

import net.fabricmc.fabric.api.tag.convention.v1.ConventionalBlockTags;
import net.fabricmc.fabric.api.transfer.v1.item.ItemStorage;
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.fabricmc.fabric.api.transfer.v1.storage.StorageView;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.jetbrains.annotations.Nullable;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class ToolkitPlatformImpl {
    public static Path getConfigDirectory() {
        return FabricLoader.getInstance().getConfigDir();
    }

    public static TagKey<Block> getOresTag() {
        return ConventionalBlockTags.ORES;
    }

    public static Path getGamePath() {
        return FabricLoader.getInstance().getGameDir();
    }

    public static List<ItemStack> getInventoryFromBlockEntity(Level level, BlockPos pos, @Nullable Direction direction) {
        List<ItemStack> items = new ArrayList<>();

        Storage<ItemVariant> storage = ItemStorage.SIDED.find(level, pos, direction);

        if (storage == null) {
            return items;
        }

        for (StorageView<ItemVariant> view : storage) {
            ItemStack stack = view.getResource().toStack();
            if (stack.isEmpty()) {
                continue;
            }

            items.add(stack);
        }

        return items;
    }
}
