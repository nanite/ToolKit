package com.sunekaer.toolkit.fabric;

import net.fabricmc.fabric.api.tag.convention.v1.ConventionalBlockTags;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;

import java.nio.file.Path;

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
}
