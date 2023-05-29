package com.sunekaer.toolkit.forge;

import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.common.Tags;
import net.minecraftforge.fml.loading.FMLPaths;

import java.nio.file.Path;

public class ToolkitPlatformImpl {
    public static Path getConfigDirectory() {
        return FMLPaths.CONFIGDIR.get();
    }

    public static TagKey<Block> getOresTag() {
        return Tags.Blocks.ORES;
    }

    public static Path getGamePath() {
        return FMLPaths.GAMEDIR.get();
    }
}
