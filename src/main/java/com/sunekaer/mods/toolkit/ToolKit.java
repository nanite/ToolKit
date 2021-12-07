package com.sunekaer.mods.toolkit;

import com.sunekaer.mods.toolkit.config.TKConfig;
import com.sunekaer.mods.toolkit.event.PlayerEvents;
import com.sunekaer.mods.toolkit.network.Handler;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.server.ServerStartingEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import static org.apache.logging.log4j.Level.INFO;

@Mod(ToolKit.MODID)
public class ToolKit {
    public static final String MODID = "toolkit";
    public static final Logger LOGGER = LogManager.getLogger("ToolKit");


    public ToolKit() {
        LOGGER.log(INFO, "Loading mod");
        MinecraftForge.EVENT_BUS.register(this);
        Handler.init();
        TKConfig.register(ModLoadingContext.get());
    }

    public static void sendChatMessage(Player entity, String message) {
        entity.sendMessage(new TextComponent(message), entity.getUUID());
    }

    public static void sendChatMessage(Player entity, TextComponent message) {
        entity.sendMessage((message), entity.getUUID());
    }

    @SubscribeEvent
    public void onServerStarting(ServerStartingEvent event) {
        LOGGER.log(INFO, "Loading server stuff");
        MinecraftForge.EVENT_BUS.addListener(new PlayerEvents()::onPlayerJoin);
    }
}