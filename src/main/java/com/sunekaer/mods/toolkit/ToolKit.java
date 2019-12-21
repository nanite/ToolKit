package com.sunekaer.mods.toolkit;

import com.sunekaer.mods.toolkit.commands.CommandGM;
import com.sunekaer.mods.toolkit.commands.TKCommand;
import com.sunekaer.mods.toolkit.config.TKConfig;
import com.sunekaer.mods.toolkit.event.PlayerEvents;
import com.sunekaer.mods.toolkit.network.Handler;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.server.FMLServerStartingEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import static org.apache.logging.log4j.Level.INFO;

@Mod(ToolKit.MODID)
public class ToolKit{
    public static final String MODID = "toolkit";
    public static final Logger LOGGER = LogManager.getLogger("ToolKit");


    public ToolKit() {
        LOGGER.log(INFO, "Loading mod");
        MinecraftForge.EVENT_BUS.register(this);
        Handler.init();
        TKConfig.register(ModLoadingContext.get());
    }

    @SubscribeEvent
    public void onServerStarting(FMLServerStartingEvent event) {
        LOGGER.log(INFO, "Loading server stuff");
        new TKCommand(event.getCommandDispatcher());
        CommandGM.register(event.getCommandDispatcher());
        MinecraftForge.EVENT_BUS.addListener(new PlayerEvents()::onPlayerJoin);
    }

    public static void sendChatMessage(PlayerEntity entity, String message){
        entity.sendMessage(new StringTextComponent(message));
    }

    public static void sendChatMessage(PlayerEntity entity, ITextComponent message){
        entity.sendMessage((message));
    }
}