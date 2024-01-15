package com.sunekaer.toolkit.jobs;

import net.minecraft.server.MinecraftServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayDeque;
import java.util.Queue;

public enum ServerTickJobRunner {
    INSTANCE;

    private static final Logger LOGGER = LoggerFactory.getLogger(ServerTickJobRunner.class);

    public static ServerTickJobRunner get() {
        return INSTANCE;
    }

    private final Queue<Runnable> queue = new ArrayDeque<>();
    private int tickCount = 0;

    /**
     * Add a job to the queue
     */
    public void add(Runnable job) {
        queue.add(job);
    }

    public void onTick(MinecraftServer server) {
        if (queue.isEmpty()) {
            // This is overkill as the int would just underflow, but it's here for clarity
            if (tickCount > 0) {
                LOGGER.debug("Finished running jobs");
                tickCount = 0;
            }
            return;
        }

        tickCount++;
        // Wait 5 ticks before running each job
        if (tickCount % 5 != 0) {
            return;
        }

        Runnable job = queue.poll();
        if (job == null) {
            return;
        }

        server.execute(() -> {
            try {
                job.run();
            } catch (Exception e) {
                LOGGER.error("Failed to run job", e);
            }
        });
    }
}
