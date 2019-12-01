package com.sunekaer.mods.toolkit;

import com.sunekaer.mods.toolkit.commands.TKCommand;
import com.sunekaer.mods.toolkit.network.Handler;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.server.FMLServerStartingEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod(ToolKit.MODID)
public class ToolKit{
    public static final String MODID = "toolkit";
    public static final Logger LOGGER = LogManager.getLogger("ToolKit");

    public ToolKit() {
        MinecraftForge.EVENT_BUS.register(this);
        Handler.init();
    }

    @SubscribeEvent
    public void onServerStarting(FMLServerStartingEvent event) {
        new TKCommand(event.getCommandDispatcher());
    }
}
