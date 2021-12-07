package com.sunekaer.mods.toolkit.commands;


import com.mojang.brigadier.builder.ArgumentBuilder;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.monster.Monster;

import java.util.concurrent.atomic.AtomicInteger;

public class CommandKillAllMonsters {
    public static ArgumentBuilder<CommandSourceStack, ?> register() {
        return Commands.literal("killAllMonsters")
                .requires(cs -> cs.hasPermission(2))
                        .executes(ctx -> remove(
                                ctx.getSource()
                        )
                );
    }

    private static int remove(CommandSourceStack source) {
        ServerLevel level = source.getLevel();
        AtomicInteger i = new AtomicInteger();

        source.sendSuccess(new TranslatableComponent("commands.toolkit.killall.monsters.start"), true);

        for (Entity entity : level.getEntities().getAll()) {
            if (entity instanceof Monster) {
                i.addAndGet(1);
                entity.remove(Entity.RemovalReason.KILLED);
            }
        }

        if (i.get() > 0) {
            source.sendSuccess(new TranslatableComponent("commands.toolkit.killall.monsters.done", i.get()), true);
        } else {
            source.sendSuccess(new TranslatableComponent("commands.toolkit.killall.monsters.no"), true);
        }
        return 1;
    }
}

