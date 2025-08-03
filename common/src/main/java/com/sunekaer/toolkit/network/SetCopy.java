package com.sunekaer.toolkit.network;

import com.sunekaer.toolkit.Toolkit;
import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

public record SetCopy(String toCopy) implements CustomPacketPayload {
    public static final Type<SetCopy> TYPE = new Type<>(ResourceLocation.fromNamespaceAndPath(Toolkit.MOD_ID, "set_copy"));

    public static final StreamCodec<ByteBuf, SetCopy> CODEC = StreamCodec.composite(
            ByteBufCodecs.STRING_UTF8,
            SetCopy::toCopy,
            SetCopy::new
    );

    public static void handle(SetCopy message) {
        Minecraft.getInstance().keyboardHandler.setClipboard(message.toCopy);
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
