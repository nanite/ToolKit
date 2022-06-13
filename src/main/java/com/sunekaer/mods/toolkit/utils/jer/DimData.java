package com.sunekaer.mods.toolkit.utils.jer;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.Block;
import org.apache.commons.lang3.mutable.MutableLong;

import java.util.HashMap;

public class DimData {
    public final Scanner scanner;
    public final ServerLevel dimension;
    public HashMap<Block, MutableLong>[] distribution;

    public DimData(Scanner s, ServerLevel w) {
        scanner = s;
        dimension = w;
    }
}
