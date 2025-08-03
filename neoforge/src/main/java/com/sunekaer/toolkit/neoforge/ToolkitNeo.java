package com.sunekaer.toolkit.neoforge;

import com.sunekaer.toolkit.commands.level.KillEntitiesCommand;
import com.sunekaer.toolkit.Toolkit;
import com.sunekaer.toolkit.network.SetCopy;
import net.minecraft.commands.synchronization.ArgumentTypeInfo;
import net.minecraft.commands.synchronization.ArgumentTypeInfos;
import net.minecraft.commands.synchronization.SingletonArgumentInfo;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.RegisterCommandsEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.event.server.ServerStoppingEvent;
import net.neoforged.neoforge.event.tick.ServerTickEvent;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

@Mod(Toolkit.MOD_ID)
public class ToolkitNeo {
    private final Toolkit toolkit;

    private static final DeferredRegister<ArgumentTypeInfo<?, ?>> ARGUMENT_REGISTRY = DeferredRegister.create(BuiltInRegistries.COMMAND_ARGUMENT_TYPE, Toolkit.MOD_ID);
    private static final DeferredHolder<ArgumentTypeInfo<?, ?>, SingletonArgumentInfo<KillEntitiesCommand.KillTypeArgument>> KILL_TYPE_ARG = ARGUMENT_REGISTRY.register("kill_type",            () -> ArgumentTypeInfos.registerByClass(KillEntitiesCommand.KillTypeArgument.class, SingletonArgumentInfo.contextFree(KillEntitiesCommand.KillTypeArgument::killType)));

    public ToolkitNeo(IEventBus modEventBus) {
        toolkit = new Toolkit();

        modEventBus.addListener(this::onSetup);
        modEventBus.addListener(this::registerNetwork);
        NeoForge.EVENT_BUS.addListener(this::registerCommands);
        NeoForge.EVENT_BUS.addListener(this::onServerStopping);
        NeoForge.EVENT_BUS.addListener(this::serverPostTick);
        NeoForge.EVENT_BUS.addListener(this::onPlayerJoin);

        ARGUMENT_REGISTRY.register(modEventBus);
    }

    public void onSetup(FMLCommonSetupEvent event) {
        toolkit.onSetup();
    }

    public void registerCommands(RegisterCommandsEvent event) {
        toolkit.registerCommands(event.getDispatcher(), event.getBuildContext(), event.getCommandSelection());
    }

    public void onServerStopping(ServerStoppingEvent event) {
        toolkit.onServerStopping();
    }

    public void serverPostTick(ServerTickEvent.Post event) {
        toolkit.onServerPostTick(event.getServer());
    }

    public void onPlayerJoin(PlayerEvent.PlayerLoggedInEvent event) {
        if (event.getEntity() instanceof ServerPlayer) {
            toolkit.onPlayerJoin((ServerPlayer) event.getEntity());
        }
    }

    public void registerNetwork(RegisterPayloadHandlersEvent event) {
        PayloadRegistrar registrar = event.registrar("1");
        registrar.playToClient(SetCopy.TYPE, SetCopy.CODEC, (payload, context) -> {
            context.enqueueWork(() -> SetCopy.handle(payload));
        });
    }
}
