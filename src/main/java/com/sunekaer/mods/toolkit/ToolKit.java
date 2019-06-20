package com.sunekaer.mods.toolkit;

import com.sunekaer.mods.toolkit.command.TKCommand;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.server.FMLServerStartingEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod("toolkit")
public class ToolKit {
    public static final Logger LOGGER = LogManager.getLogger();

    public ToolKit() {
        MinecraftForge.EVENT_BUS.register(this);
    }

    @SubscribeEvent
    public void onServerStarting(FMLServerStartingEvent event) {
        LOGGER.info("Hello Minecraft");
        new TKCommand(event.getCommandDispatcher());
    }
}
