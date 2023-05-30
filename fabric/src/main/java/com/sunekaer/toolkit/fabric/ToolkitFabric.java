package com.sunekaer.toolkit.fabric;

import com.sunekaer.toolkit.Toolkit;
import com.sunekaer.toolkit.commands.CommandKill;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.ArgumentTypeRegistry;
import net.minecraft.commands.synchronization.SingletonArgumentInfo;
import net.minecraft.resources.ResourceLocation;

public class ToolkitFabric implements ModInitializer {
    @Override
    public void onInitialize() {
        Toolkit.init();
        ArgumentTypeRegistry.registerArgumentType(
                new ResourceLocation(Toolkit.MODID, "kill_type"),
                CommandKill.KillTypeArgument.class,
                SingletonArgumentInfo.contextFree(CommandKill.KillTypeArgument::killType)
        );
    }
}
