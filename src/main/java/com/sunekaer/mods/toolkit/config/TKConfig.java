package com.sunekaer.mods.toolkit.config;

import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import org.apache.commons.lang3.tuple.Pair;

@Mod.EventBusSubscriber
public class TKConfig {
    private static CommonConfig common;
    private static ForgeConfigSpec commonSpec;

    public static void initCommon()
    {
        final Pair<CommonConfig, ForgeConfigSpec> specPair = new ForgeConfigSpec.Builder().configure(CommonConfig::new);
        commonSpec = specPair.getRight();
        common = specPair.getLeft();
    }

    public static void register(final ModLoadingContext context)
    {
        initCommon();
        context.registerConfig(ModConfig.Type.COMMON, commonSpec);
    }
}
