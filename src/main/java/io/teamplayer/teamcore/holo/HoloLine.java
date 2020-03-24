package io.teamplayer.teamcore.holo;

import io.teamplayer.teamcore.entity.fake.FakeArmorStand;
import io.teamplayer.teamcore.immutable.ImmutableLocation;
import org.bukkit.Location;

/**
 * A single line for a hologram
 */
class HoloLine {

    private FakeArmorStand armorStand;
    private Location location;

    HoloLine(String content, Location location) {
        this.location = location;

        armorStand = new FakeArmorStand(transformLocation(location.clone()));

        armorStand.setCustomNameVisible(!content.equals(""));
        armorStand.setCustomName(content);
        armorStand.setInvisible(true);
        armorStand.setSmall(true);

        armorStand.spawn();
    }

    void setName(String content) {
        armorStand.setCustomName(content);
        armorStand.setCustomNameVisible(!content.equals(""));
    }

    String getName() {
        return armorStand.getCustomName();
    }

    FakeArmorStand getArmorStand() {
        return armorStand;
    }

    Location getLocation() {
        return ImmutableLocation.from(location);
    }

    private Location transformLocation(Location location) {
        return location.add(0, -1.37, 0);
    }
}
