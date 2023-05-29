package com.sunekaer.toolkit.network;


import dev.architectury.networking.NetworkManager;
import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;

import java.util.function.Supplier;

public class SetCopy {
    public final String toCopy;

    public SetCopy(FriendlyByteBuf buf) {
        // Decode data into a message
        this.toCopy = buf.readUtf();
    }

    public SetCopy(String toCopy) {
        this.toCopy = toCopy;
    }

    public void encode(FriendlyByteBuf buf) {
        buf.writeUtf(toCopy);
    }

    public void apply(Supplier<NetworkManager.PacketContext> context) {
        context.get().queue(() ->
                Minecraft.getInstance().keyboardHandler.setClipboard(toCopy)
        );
    }
}
