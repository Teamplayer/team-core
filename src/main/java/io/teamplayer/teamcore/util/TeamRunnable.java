package io.teamplayer.teamcore.util;

import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

/**
 * FunctionalInterface bukkit runnable
 */
@FunctionalInterface
public interface TeamRunnable extends Runnable {

    void run();

    default BukkitRunnable bukkit() {
        return new BukkitRunnable() {
            @Override
            public void run() {
                TeamRunnable.this.run();
            }
        };
    }

    default BukkitTask runTask(JavaPlugin plugin) {
        return bukkit().runTask(plugin);
    }

    default BukkitTask runTaskAsync(JavaPlugin plugin) {
        return bukkit().runTaskAsynchronously(plugin);
    }

    default BukkitTask runTaskLater(JavaPlugin plugin, long delay) {
        return bukkit().runTaskLater(plugin, delay);
    }

    default BukkitTask runTaskLaterAsync(JavaPlugin plugin, long delay) {
        return bukkit().runTaskLaterAsynchronously(plugin, delay);
    }

    default BukkitTask runTaskTimer(JavaPlugin plugin, long period) {
        return bukkit().runTaskTimer(plugin, 0, period);
    }

    default BukkitTask runTaskTimerAsync(JavaPlugin plugin, long period) {
        return bukkit().runTaskTimerAsynchronously(plugin, 0, period);
    }
}
