package com.sunekaer.mods.toolkit.commands;

import com.mojang.brigadier.builder.ArgumentBuilder;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.vehicle.AbstractMinecart;

import java.util.concurrent.atomic.AtomicInteger;

public class CommandKillAll {
    public static ArgumentBuilder<CommandSourceStack, ?> register() {
        return Commands.literal("killAll")
                .requires(cs -> cs.hasPermission(2))
                        .executes(ctx -> remove(
                                ctx.getSource()
                        )
                );
    }

    private static int remove(CommandSourceStack source) {
        ServerLevel level = source.getLevel();
        AtomicInteger i = new AtomicInteger();

        source.sendSuccess(Component.translatable("commands.toolkit.killall.start"), true);

        System.out.println(i.get());

        for (Entity entity : level.getEntities().getAll()) {
            if (!(entity instanceof Player) && entity != null && !(entity instanceof AbstractMinecart)) {
                i.addAndGet(1);
                entity.remove(Entity.RemovalReason.KILLED);
            }
        }

        System.out.println(i.get());

        if (i.get() > 0) {
            source.sendSuccess(Component.translatable("commands.toolkit.killall.done", i.get()), true);
        } else {
            source.sendSuccess(Component.translatable("commands.toolkit.killall.no"), true);
        }
        return 1;
    }
}

