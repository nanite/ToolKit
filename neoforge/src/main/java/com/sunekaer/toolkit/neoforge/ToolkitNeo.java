package com.sunekaer.toolkit.neoforge;

import com.sunekaer.toolkit.commands.level.KillEntitiesCommand;
import com.sunekaer.toolkit.Toolkit;
import net.minecraft.commands.synchronization.ArgumentTypeInfo;
import net.minecraft.commands.synchronization.ArgumentTypeInfos;
import net.minecraft.commands.synchronization.SingletonArgumentInfo;
import net.minecraft.core.registries.BuiltInRegistries;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.javafmlmod.FMLJavaModLoadingContext;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;


@Mod(Toolkit.MOD_ID)
public class ToolkitNeo {
    private static final DeferredRegister<ArgumentTypeInfo<?, ?>> ARGUMENT_REGISTRY = DeferredRegister.create(BuiltInRegistries.COMMAND_ARGUMENT_TYPE, Toolkit.MOD_ID);
    private static final DeferredHolder<ArgumentTypeInfo<?, ?>, SingletonArgumentInfo<KillEntitiesCommand.KillTypeArgument>> KILL_TYPE_ARG = ARGUMENT_REGISTRY.register("kill_type",
            () -> ArgumentTypeInfos.registerByClass(KillEntitiesCommand.KillTypeArgument.class, SingletonArgumentInfo.contextFree(KillEntitiesCommand.KillTypeArgument::killType)));
    public ToolkitNeo() {
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
        Toolkit.init();

        ARGUMENT_REGISTRY.register(modEventBus);
    }
}
