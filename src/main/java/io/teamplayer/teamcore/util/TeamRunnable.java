package io.teamplayer.teamcore.util;

import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

/**
 * FunctionalInterface that allows you to easily schedule tasks in Bukkit
 */
@FunctionalInterface
public interface TeamRunnable extends Runnable {

    void run();

    /**
     * Create a new BukkitRunnable that wraps this Runnable
     */
    default BukkitRunnable bukkit() {
        return new BukkitRunnable() {
            @Override
            public void run() {
                TeamRunnable.this.run();
            }
        };
    }

    /**
     * Schedule task to be run in next server tick
     *
     * @param plugin plugin the task belongs to
     * @return BukkitTask representing current task
     */
    default BukkitTask runTask(JavaPlugin plugin) {
        return bukkit().runTask(plugin);
    }

    /**
     * Schedule task to be run async on next server tick
     * <b>Do not access or modify the world asynchronously</b>
     *
     * @param plugin plugin the task belongs to
     * @return BukkitTask representing current task
     */
    default BukkitTask runTaskAsync(JavaPlugin plugin) {
        return bukkit().runTaskAsynchronously(plugin);
    }

    /**
     * Schedule task to run after a specified amount of server ticks have passed
     *
     * @param plugin plugin the task belongs to
     * @param delay  amount of ticks to delay task by
     * @return BukkitTask representing current task
     */
    default BukkitTask runTaskLater(JavaPlugin plugin, long delay) {
        return bukkit().runTaskLater(plugin, delay);
    }

    /**
     * Schedule task to run async after a specified amount of server ticks have passed
     * <b>Do not access or modify the world asynchronously</b>
     *
     * @param plugin plugin the task belongs to
     * @param delay  amount of ticks to delay task by
     * @return BukkitTask representing current task
     */
    default BukkitTask runTaskLaterAsync(JavaPlugin plugin, long delay) {
        return bukkit().runTaskLaterAsynchronously(plugin, delay);
    }

    /**
     * Schedule task to run repeatedly with a specified tick delay between each run
     *
     * @param plugin plugin the task belongs to
     * @param period amount of tick delay between each run
     * @return BukkitTask representing current task
     */
    default BukkitTask runTaskTimer(JavaPlugin plugin, long period) {
        return bukkit().runTaskTimer(plugin, 0, period);
    }

    /**
     * Schedule task to run async repeatedly with a specified tick delay between each run
     * <b>Do not access or modify the world asynchronously</b>
     *
     * @param plugin plugin the task belongs to
     * @param period amount of tick delay between each run
     * @return BukkitTask representing current task
     */
    default BukkitTask runTaskTimerAsync(JavaPlugin plugin, long period) {
        return bukkit().runTaskTimerAsynchronously(plugin, 0, period);
    }
}
