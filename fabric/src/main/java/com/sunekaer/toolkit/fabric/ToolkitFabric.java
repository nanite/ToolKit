package com.sunekaer.toolkit.fabric;

import com.sunekaer.toolkit.Toolkit;
import com.sunekaer.toolkit.commands.level.KillEntitiesCommand;
import com.sunekaer.toolkit.network.SetCopy;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.command.v2.ArgumentTypeRegistry;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.entity.event.v1.ServerPlayerEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.CommonLifecycleEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.commands.synchronization.SingletonArgumentInfo;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerPlayer;

public class ToolkitFabric implements ModInitializer {
    Toolkit toolkit;

    @Override
    public void onInitialize() {
        toolkit = new Toolkit();
        toolkit.onSetup();

        ServerPlayerEvents.JOIN.register((player) -> toolkit.onPlayerJoin(player));
        ServerLifecycleEvents.SERVER_STOPPING.register((server) -> toolkit.onServerStopping());
        ServerTickEvents.END_SERVER_TICK.register((server) -> toolkit.onServerPostTick(server));
        CommandRegistrationCallback.EVENT.register(((commandDispatcher, commandBuildContext, commandSelection) ->
                toolkit.registerCommands(commandDispatcher, commandBuildContext, commandSelection)));

        ArgumentTypeRegistry.registerArgumentType(
                Identifier.fromNamespaceAndPath(Toolkit.MOD_ID, "kill_type"),
                KillEntitiesCommand.KillTypeArgument.class,
                SingletonArgumentInfo.contextFree(KillEntitiesCommand.KillTypeArgument::killType)
        );

        PayloadTypeRegistry.playS2C().register(SetCopy.TYPE, SetCopy.CODEC);
        ClientPlayNetworking.registerGlobalReceiver(SetCopy.TYPE, (payload, context) -> {
            SetCopy.handle(payload);
        });
    }
}
