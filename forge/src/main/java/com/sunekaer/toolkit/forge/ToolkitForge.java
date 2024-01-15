package com.sunekaer.toolkit.forge;

import com.sunekaer.toolkit.commands.level.KillEntitiesCommand;
import dev.architectury.platform.forge.EventBuses;
import com.sunekaer.toolkit.Toolkit;
import net.minecraft.commands.synchronization.ArgumentTypeInfo;
import net.minecraft.commands.synchronization.ArgumentTypeInfos;
import net.minecraft.commands.synchronization.SingletonArgumentInfo;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

@Mod(Toolkit.MOD_ID)
public class ToolkitForge {
    private static final DeferredRegister<ArgumentTypeInfo<?, ?>> ARGUMENT_REGISTRY = DeferredRegister.create(ForgeRegistries.COMMAND_ARGUMENT_TYPES, Toolkit.MOD_ID);
    private static final RegistryObject<SingletonArgumentInfo<KillEntitiesCommand.KillTypeArgument>> KILL_TYPE_ARG = ARGUMENT_REGISTRY.register("kill_type",
            () -> ArgumentTypeInfos.registerByClass(KillEntitiesCommand.KillTypeArgument.class, SingletonArgumentInfo.contextFree(KillEntitiesCommand.KillTypeArgument::killType)));
    public ToolkitForge() {
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
        EventBuses.registerModEventBus(Toolkit.MOD_ID, modEventBus);
        Toolkit.init();

        ARGUMENT_REGISTRY.register(modEventBus);
    }
}
