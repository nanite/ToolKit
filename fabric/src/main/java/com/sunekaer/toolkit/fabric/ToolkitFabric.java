package com.sunekaer.toolkit.fabric;

import com.sunekaer.toolkit.Toolkit;
import com.sunekaer.toolkit.commands.level.KillEntitiesCommand;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.ArgumentTypeRegistry;
import net.minecraft.commands.synchronization.SingletonArgumentInfo;
import net.minecraft.resources.ResourceLocation;

public class ToolkitFabric implements ModInitializer {
    @Override
    public void onInitialize() {
        Toolkit.init();
        ArgumentTypeRegistry.registerArgumentType(
                ResourceLocation.fromNamespaceAndPath(Toolkit.MOD_ID, "kill_type"),
                KillEntitiesCommand.KillTypeArgument.class,
                SingletonArgumentInfo.contextFree(KillEntitiesCommand.KillTypeArgument::killType)
        );
    }
}
