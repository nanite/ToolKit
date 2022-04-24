package com.sunekaer.mods.toolkit.event;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.Blocks;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import java.util.*;

public class ServerEvents {
//    public static final Deque<BlockPos> REMOVAL_QUEUE = new ArrayDeque<>();

    @SubscribeEvent
    public void onTick(TickEvent.WorldTickEvent event) {
////        System.out.println(REMOVAL_QUEUE.size());
//
//        if (!event.side.isServer()) {
//            return;
//        }
//
//        if (REMOVAL_QUEUE.isEmpty()) {
//            return;
//        }
//
//        int i = 0;
//        while (i < 1000) {
//            BlockPos pos = REMOVAL_QUEUE.poll();
//            if (pos == null || i > REMOVAL_QUEUE.size()) {
//                break;
//            }
//
//            event.world.setBlock(pos, Blocks.AIR.defaultBlockState(), 3);
//            i ++;
//        }
//
//        System.out.println("Remaining " + REMOVAL_QUEUE.size());
    }
}
