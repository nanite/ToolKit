package com.sunekaer.toolkit;

import dev.architectury.injectables.annotations.ExpectPlatform;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;

import java.nio.file.Path;

public class ToolkitPlatform {
    @ExpectPlatform
    public static Path getConfigDirectory() {
        throw new AssertionError();
    }

    @ExpectPlatform
    public static TagKey<Block> getOresTag() {
        throw new AssertionError();
    }

    @ExpectPlatform
    public static Path getGamePath() {
        throw new AssertionError();
    }
}
