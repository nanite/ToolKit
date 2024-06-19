package com.sunekaer.toolkit.network;

import com.sunekaer.toolkit.Toolkit;
import dev.architectury.networking.NetworkChannel;
import net.minecraft.resources.ResourceLocation;

public class Handler {
    public static final NetworkChannel CHANNEL = NetworkChannel.create(ResourceLocation.fromNamespaceAndPath(Toolkit.MOD_ID, "networking_channel"));

    public static void init() {
        CHANNEL.register(SetCopy.class, SetCopy::encode, SetCopy::new, SetCopy::apply);
    }
}
