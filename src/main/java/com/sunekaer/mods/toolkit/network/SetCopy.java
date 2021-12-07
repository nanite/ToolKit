package com.sunekaer.mods.toolkit.network;


import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class SetCopy {

    public final String toCopy;

    public SetCopy(String toCopy) {
        this.toCopy = toCopy;
    }

    public SetCopy(FriendlyByteBuf buf)
    {
        toCopy = buf.readUtf();

    }

    public void write(FriendlyByteBuf buf)
    {
        buf.writeUtf(toCopy);

    }

    public void handle(Supplier<NetworkEvent.Context> context)
    {
        context.get().enqueueWork(() ->
                Minecraft.getInstance().keyboardHandler.setClipboard(toCopy)
        );

        context.get().setPacketHandled(true);
    }
}
