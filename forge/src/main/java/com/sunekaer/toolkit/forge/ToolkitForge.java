package com.sunekaer.toolkit.forge;

import com.sunekaer.toolkit.commands.CommandKill;
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

@Mod(Toolkit.MODID)
public class ToolkitForge {
    private static final DeferredRegister<ArgumentTypeInfo<?, ?>> ARGUMENT_REGISTRY = DeferredRegister.create(ForgeRegistries.COMMAND_ARGUMENT_TYPES, Toolkit.MODID);
    private static final RegistryObject<SingletonArgumentInfo<CommandKill.KillTypeArgument>> KILL_TYPE_ARG = ARGUMENT_REGISTRY.register("kill_type",
            () -> ArgumentTypeInfos.registerByClass(CommandKill.KillTypeArgument.class, SingletonArgumentInfo.contextFree(CommandKill.KillTypeArgument::killType)));
    public ToolkitForge() {
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
        EventBuses.registerModEventBus(Toolkit.MODID, modEventBus);
        Toolkit.init();

        ARGUMENT_REGISTRY.register(modEventBus);
    }
}
