package com.sunekaer.mods.toolkit.network;

import net.minecraft.client.Minecraft;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.network.NetworkRegistry;
import net.minecraftforge.fml.network.simple.SimpleChannel;

public class Handler {
    public static SimpleChannel CHANNEL = NetworkRegistry.newSimpleChannel(new ResourceLocation("toolkit:main"), () -> "1", "1"::equals, "1"::equals);
    public static void init() {
        CHANNEL.registerMessage(1, Copy.class, (Copy, packetBuffer) -> packetBuffer.writeString(Copy.toCopy), packetBuffer -> new Copy(packetBuffer.readString()), (Copy, contextSupplier) -> contextSupplier.get().enqueueWork(() -> Minecraft.getInstance().keyboardListener.setClipboardString(Copy.toCopy)));
    }
}
