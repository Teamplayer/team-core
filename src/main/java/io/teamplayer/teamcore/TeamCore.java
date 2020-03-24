package io.teamplayer.teamcore;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Optional;

public class TeamCore extends JavaPlugin {

    private static TeamCore instance;

    @Override
    public void onEnable() {
        instance = this;
    }

    private Optional<Player> isPlayer(int id) {
        return Bukkit.getOnlinePlayers().stream()
                .map(p -> (Player) p)
                .filter(p -> p.getEntityId() == id)
                .findAny();
    }

    @Deprecated
    public static TeamCore getInstance() {
        return instance;
    }
}
