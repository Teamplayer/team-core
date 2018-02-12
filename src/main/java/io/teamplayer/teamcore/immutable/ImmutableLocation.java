package io.teamplayer.teamcore.immutable;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.util.Vector;

/**
 * A bukkit location that cannot be modified
 */
public class ImmutableLocation extends Location {

    private final static String exceptionMessage = "You cannot change the values of an ImmutableLocation";

    private ImmutableLocation(Location loc) {
        super(loc.getWorld(), loc.getX(), loc.getY(), loc.getZ(), loc.getYaw(), loc.getPitch());
    }

    /**
     * Get a ImmutableLocation based off of a location
     * @param location the location that will contain the information for the ImmutableLocation
     * @return a copy of the location, but immutable
     */
    public static ImmutableLocation from(Location location) {
        return new ImmutableLocation(location);
    }

    /**
     * Get a location object with the same information as the immutable location
     *
     * @return an identical, but mutable location object
     */
    public Location mutable() {
        return new Location(getWorld(), getX(), getY(), getZ(), getYaw(), getPitch());
    }

    @Deprecated
    @Override
    public Location setDirection(Vector vector) {
        throw new UnsupportedOperationException(exceptionMessage);
    }

    @Deprecated
    @Override
    public void setPitch(float pitch) {
        throw new UnsupportedOperationException(exceptionMessage);
    }

    @Deprecated
    @Override
    public void setWorld(World world) {
        throw new UnsupportedOperationException(exceptionMessage);
    }

    @Deprecated
    @Override
    public void setX(double x) {
        throw new UnsupportedOperationException(exceptionMessage);
    }

    @Deprecated
    @Override
    public void setY(double y) {
        throw new UnsupportedOperationException(exceptionMessage);
    }

    @Deprecated
    @Override
    public void setYaw(float yaw) {
        throw new UnsupportedOperationException(exceptionMessage);
    }

    @Deprecated
    @Override
    public void setZ(double z) {
        throw new UnsupportedOperationException(exceptionMessage);
    }

    @Deprecated
    @Override
    public Location add(Vector vec) {
        throw new UnsupportedOperationException(exceptionMessage);
    }

    @Deprecated
    @Override
    public Location add(Location vec) {
        throw new UnsupportedOperationException(exceptionMessage);
    }

    @Deprecated
    @Override
    public Location add(double x, double y, double z) {
        throw new UnsupportedOperationException(exceptionMessage);
    }

    @Deprecated
    @Override
    public Location subtract(Vector vec) {
        throw new UnsupportedOperationException(exceptionMessage);
    }

    @Deprecated
    @Override
    public Location subtract(Location vec) {
        throw new UnsupportedOperationException(exceptionMessage);
    }

    @Deprecated
    @Override
    public Location subtract(double x, double y, double z) {
        throw new UnsupportedOperationException(exceptionMessage);
    }

    @Deprecated
    @Override
    public Location multiply(double m) {
        throw new UnsupportedOperationException(exceptionMessage);
    }

    @Deprecated
    @Override
    public Location zero() {
        throw new UnsupportedOperationException(exceptionMessage);
    }
}
