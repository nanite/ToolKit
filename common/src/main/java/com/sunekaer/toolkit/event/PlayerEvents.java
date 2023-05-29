package com.sunekaer.toolkit.event;

import com.sunekaer.toolkit.Toolkit;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;

public class PlayerEvents {

    public static void playerJoined(ServerPlayer player) {
        if (player != null && Toolkit.SHOW_ON_JOIN_MESSAGE.get()) {
            if (!player.getLevel().isClientSide) {
                player.displayClientMessage(Component.literal(Toolkit.JOIN_MESSAGE.get()), false);
            }
        }
    }
}
