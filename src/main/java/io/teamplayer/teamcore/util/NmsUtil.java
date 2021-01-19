package io.teamplayer.teamcore.util;

import net.minecraft.server.v1_16_R3.World;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_16_R3.CraftWorld;

/**
 * A utility class for doing common NMS methods
 */
public final class NmsUtil {

    private NmsUtil() {
        throw new UnsupportedOperationException("This is a utility class");
    }

    public static World getNmsWorld(org.bukkit.World world) {
        return ((CraftWorld) world).getHandle();
    }

    public static World getNmsWorld(Location location) {
        return getNmsWorld(location.getWorld());
    }

}
