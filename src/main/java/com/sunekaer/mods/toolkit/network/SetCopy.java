package com.sunekaer.mods.toolkit.network;

import net.minecraft.client.Minecraft;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.function.Supplier;

public class SetCopy {

    public final String toCopy;

    public SetCopy(String toCopy) {
        this.toCopy = toCopy;
    }

    public SetCopy(PacketBuffer buf)
    {
        toCopy = buf.readString();

    }

    public void write(PacketBuffer buf)
    {
        buf.writeString(toCopy);

    }

    public void handle(Supplier<NetworkEvent.Context> context)
    {
        context.get().enqueueWork(() ->
                Minecraft.getInstance().keyboardListener.setClipboardString(toCopy)
        );

        context.get().setPacketHandled(true);
    }
}
