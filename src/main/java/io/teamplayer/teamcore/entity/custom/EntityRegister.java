package io.teamplayer.teamcore.entity.custom;

import net.minecraft.server.v1_15_R1.Entity;
import net.minecraft.server.v1_15_R1.EntityTypes;
import net.minecraft.server.v1_15_R1.MinecraftKey;
import net.minecraft.server.v1_15_R1.World;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_15_R1.CraftWorld;
import org.bukkit.event.entity.CreatureSpawnEvent;

import java.util.HashSet;
import java.util.Set;

/**
 * A utility class for registering and spawning custom entities
 */
public final class EntityRegister {

    private static final Set<Class<? extends Entity>> registered = new HashSet<>();

    private EntityRegister() {
        throw new UnsupportedOperationException("This is a utility class");
    }

    public static void registerEntity(Class<? extends Entity> clazz, CustomEntityInfo info) {
        if (registered.contains(clazz)) return;

        EntityTypes.b.a(info.getProtocolId(), new MinecraftKey(info.getProtocolName()), clazz);

        registered.add(clazz);
    }

    public static Entity spawnCustomEntity(Location location, Entity entity) {
        final World world = ((CraftWorld) location.getWorld()).getHandle();

        entity.setLocation(location.getX(), location.getY(), location.getZ(), location.getYaw(),
                location.getPitch());

        world.addEntity(entity, CreatureSpawnEvent.SpawnReason.CUSTOM);

        return entity;
    }
}
