package com.sunekaer.mods.toolkit.event;

import com.sunekaer.mods.toolkit.ToolKit;
import com.sunekaer.mods.toolkit.config.CommonConfig;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class PlayerEvents {
    @SubscribeEvent
    public void onPlayerJoin(PlayerEvent.PlayerLoggedInEvent event) {
        if (event.getEntity() instanceof Player && CommonConfig.messageOnJoin.get()) {
            if (!event.getEntity().getLevel().isClientSide) {
                ToolKit.sendChatMessage((Player) event.getEntity(), CommonConfig.joinMessage.get());
            }
        }
    }
}
