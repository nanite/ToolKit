package com.sunekaer.mods.toolkit.network;

import com.sunekaer.mods.toolkit.ToolKit;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.network.NetworkRegistry;
import net.minecraftforge.fml.network.simple.SimpleChannel;

public class Handler {
    public static SimpleChannel MAIN;
    private static final String MAIN_VERSION = "1";

    public static void init(){
        MAIN = NetworkRegistry.ChannelBuilder
            .named(new ResourceLocation(ToolKit.MODID, "main"))
            .clientAcceptedVersions(MAIN_VERSION::equals)
            .serverAcceptedVersions(MAIN_VERSION::equals)
            .networkProtocolVersion(() -> MAIN_VERSION)
            .simpleChannel();

        MAIN.registerMessage(1, SetCopy.class, SetCopy::write, SetCopy::new, SetCopy::handle);
    }
}
