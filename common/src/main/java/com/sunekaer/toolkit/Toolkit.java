package com.sunekaer.toolkit;

import com.mojang.brigadier.CommandDispatcher;
import com.sunekaer.toolkit.commands.TKCommand;
import com.sunekaer.toolkit.commands.level.ClearCommand;
import com.sunekaer.toolkit.jobs.ServerTickJobRunner;
import dev.nanite.library.core.config.Config;
import dev.nanite.library.core.config.ConfigManager;
import dev.nanite.library.core.config.ConfigValueGroup;
import dev.nanite.library.core.config.values.BooleanConfigValue;
import dev.nanite.library.core.config.values.StringConfigValue;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ServiceLoader;
import java.util.function.Supplier;

public class Toolkit {
    private static final Logger LOGGER = LoggerFactory.getLogger(Toolkit.class);
    public static final String MOD_ID = "toolkit";

    public static final XPlatShim PLATFORM = ServiceLoader.load(XPlatShim.class).findFirst().orElseThrow();

    public static final Config CONFIG = Config.serverConfig(MOD_ID);
    private static final ConfigValueGroup JOIN_MESSAGE_GROUP = CONFIG.group("join_message")
            .comments("When enabled, Toolkit will present a message to the user each time they join.");

    public static final BooleanConfigValue SHOW_ON_JOIN_MESSAGE = JOIN_MESSAGE_GROUP.booleanValue("enable", true);
    public static final StringConfigValue JOIN_MESSAGE = JOIN_MESSAGE_GROUP.stringValue("message", "Hello from ToolKit, this message can be change or disabled in config.")
            .comments("The message displayed to the user on joining.");

    public Toolkit() {
        ConfigManager.register(CONFIG);
    }

    public void registerCommands(CommandDispatcher<CommandSourceStack> dispatcher, CommandBuildContext commandBuildContext, Commands.CommandSelection commandSelection) {
        TKCommand.register(dispatcher, commandBuildContext, commandSelection);
    }

    public void onServerStopping() {
        ClearCommand.EXECUTOR.shutdownNow();
    }

    public void onServerPostTick(MinecraftServer server) {
        ServerTickJobRunner.get().onTick(server);
    }

    public void onPlayerJoin(ServerPlayer player) {
        if (player != null && Toolkit.SHOW_ON_JOIN_MESSAGE.get()) {
            if (!player.level().isClientSide()) {
                player.sendSystemMessage(Component.literal(Toolkit.JOIN_MESSAGE.get()));
            }
        }
    }
}
