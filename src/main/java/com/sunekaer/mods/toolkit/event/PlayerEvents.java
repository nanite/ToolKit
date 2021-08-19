package com.sunekaer.mods.toolkit.event;

import com.sunekaer.mods.toolkit.ToolKit;
import com.sunekaer.mods.toolkit.config.CommonConfig;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.text.StringTextComponent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class PlayerEvents {

    @SubscribeEvent
    public void onPlayerJoin(PlayerEvent.PlayerLoggedInEvent event) {
        if (event.getEntity() instanceof PlayerEntity && CommonConfig.messageOnJoin.get()) {
                if (!event.getPlayer().getEntityWorld().isRemote) {
                    ToolKit.sendChatMessage((PlayerEntity) event.getEntity(), new StringTextComponent(CommonConfig.joinMessage.get()));
                }
            }
        }

    }
