package io.teamplayer.teamcore.entity.fake;

import com.comphenix.packetwrapper.*;
import com.comphenix.protocol.wrappers.WrappedDataWatcher;
import io.teamplayer.teamcore.immutable.ImmutableLocation;
import io.teamplayer.teamcore.util.ClientSideObject;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

import static com.comphenix.protocol.wrappers.WrappedDataWatcher.*;

/**
 * A client-side entity
 */
public class FakeEntity implements ClientSideObject {

    private final EntityType type;
    private String customName = "";
    private boolean customNameVisible;

    //Things bitmasked inside of the entity metadata at index 0
    private static final byte METADATA_INDEX = 0;
    private final FakeEntityBitMask mask = new FakeEntityBitMask();

    private boolean spawned = false;

    private final int entityId = ThreadLocalRandom.current().nextInt();

    private Location location;

    private boolean global = true;
    private Set<Player> viewers = new HashSet<>();

    public FakeEntity(EntityType type, Location location) {
        this.type = type;
        this.location = location;
    }

    public FakeEntity(EntityType type, Location location, boolean global) {
        this(type, location);
        this.global = global;
    }

    /**
     * Spawn the entity for all viewers
     */
    @Override
    public void spawn() {
        final Collection<Player> viewers = getViewers();
        spawned = true;
        spawn(viewers.toArray(new Player[viewers.size()]));
    }

    /**
     * Destroy the entity for all clients that can see it
     */
    @Override
    public void destroy() {
        final Collection<Player> viewers = getViewers();
        spawned = false;
        destroy(viewers.toArray(new Player[viewers.size()]));
    }

    /**
     * Teleport the fake entity to the specified location
     *
     * @param location location to teleport the entity to
     */
    public void teleport(Location location) {
        final WrapperPlayServerEntityTeleport packet = new WrapperPlayServerEntityTeleport();

        packet.setEntityID(entityId);

        packet.setX(location.getX());
        packet.setY(location.getY());
        packet.setZ(location.getZ());

        packet.setPitch(location.getPitch());
        packet.setYaw(location.getYaw());

        packet.setOnGround(true);

        sendPacket(packet);

        this.location = location;
    }

    /**
     * Moves the entity to the specified location either through teleporting or by moving it. The
     * entity will move if the movement is not greater than 8 units in any direction, otherwise it
     * will teleport
     *
     * @param location the new fake entity location
     */
    public void move(Location location) {
        final double xDiff = this.location.getX() - location.getX();
        final double yDiff = this.location.getY() - location.getY();
        final double zDiff = this.location.getZ() - location.getZ();


        if (Math.abs(xDiff) > 8 || Math.abs(yDiff) > 8 || Math.abs(zDiff) > 8) {
            //If the entity moves more than 8 blocks in any direction the entity should be teleported instead of moved
            teleport(location);
            return;
        }

        move(xDiff, yDiff, zDiff);
    }

    /**
     * Moves the fake entity by using the difference on each axis between the new and old location.
     * The movement cannot be any more than 8 units in any direction
     *
     * @param xDiff difference in the x value
     * @param yDiff difference in the y value
     * @param zDiff difference in the z value
     * @throws IllegalArgumentException when entity movement is more than 8 units in any direction
     */
    public void move(double xDiff, double yDiff, double zDiff) {
        if (Math.abs(xDiff) > 8 || Math.abs(yDiff) > 8 || Math.abs(zDiff) > 8) {
            //TODO Change to use validate
            throw new IllegalArgumentException("Entity movement cannot be any more than 8 units in any direction");
        }

        final WrapperPlayServerRelEntityMove packet = new WrapperPlayServerRelEntityMove();

        packet.setEntityID(entityId);

        packet.setDx((int) calculateDelta(xDiff));
        packet.setDy((int) calculateDelta(yDiff));
        packet.setDz((int) calculateDelta(zDiff));

        packet.setOnGround(false);

        sendPacket(packet);

        location.add(xDiff, yDiff, zDiff);
    }

    /**
     * Rotate the fake entity to the specified yaw and pitch
     *
     * @param yaw new yaw
     * @param pitch new pitch
     */
    public void rotate(float yaw, float pitch) {
        final WrapperPlayServerEntityLook packet = new WrapperPlayServerEntityLook();

        packet.setEntityID(getEntityId());

        packet.setYaw(yaw);
        packet.setPitch(pitch);

        sendPacket(packet);

        location.setYaw(yaw);
        location.setPitch(pitch);
    }

    public void updateLocation(Location newLocation) {
        location = newLocation.clone();
    }

    public int getEntityId() {
        return entityId;
    }

    /**
     * Set the custom name that appears above the entities head
     *
     * @param customName the custom name
     */
    public void setCustomName(String customName) {
        modifyMetadata((byte) 2, Registry.get(String.class), customName);
        this.customName = customName;
    }

    public String getCustomName() {
        return customName;
    }

    /**
     * Set whether or not the entities custom name which appears above it's head is visible
     *
     * @param customNameVisible whether or not the custom name is visible
     */
    public void setCustomNameVisible(boolean customNameVisible) {
        modifyMetadata((byte) 3, Registry.get(Boolean.class), customNameVisible);
        this.customNameVisible = customNameVisible;
    }

    public boolean isCustomNameVisible() {
        return customNameVisible;
    }

    public boolean isOnFire() {
        return mask.isOnFire();
    }

    public void setOnFire(boolean onFire) {
        mask.setOnFire(onFire);
        updateEntityMask();
    }

    public boolean isCrouched() {
        return mask.isCrouched();
    }

    public void setCrouched(boolean crouched) {
        mask.setCrouched(crouched);
        updateEntityMask();
    }

    public boolean isSprinting() {
        return mask.isSprinting();
    }

    public void setSprinting(boolean sprinting) {
        mask.setSprinting(sprinting);
        updateEntityMask();
    }

    public boolean isInvisible() {
        return mask.isInvisible();
    }

    public void setInvisible(boolean invisible) {
        mask.setInvisible(true);
        updateEntityMask();
    }

    public boolean isGlowing() {
        return mask.isGlowing();
    }

    public void setGlowing(boolean glowing) {
        mask.setGlowing(glowing);
        updateEntityMask();
    }

    public boolean isElytraFlying() {
        return mask.isElytraFlying();
    }

    public void setElytraFlying(boolean elytraFlying) {
        mask.setElytraFlying(elytraFlying);
        updateEntityMask();
    }

    public Location getLocation() {
        return ImmutableLocation.from(location);
    }

    /**
     * Return a view of the players that can see the fake entity.
     */
    @Override
    public Collection<Player> getViewers() {
        if (global) {
            return (Collection<Player>) Bukkit.getOnlinePlayers();
        } else {
            return Collections.unmodifiableCollection(viewers);
        }
    }

    /**
     * Allows the player to see the entity and spawns it for them.
     *
     * @param player player to make a viewer
     */
    @Override
    public void addViewer(Player player) {
        viewers.add(player);

        if (spawned) {
            spawn(player);
        }
    }

    /**
     * Doesn't allow the specified player to see the entity anymore and despawns it for them.
     *
     * @param player removes the player as a view
     */
    @Override
    public void removeViewer(Player player) {
        viewers.remove(player);

        if (spawned) {
            destroy(player);
        }
    }

    /**
     * Returns whether or not all clients can see the entity
     */
    public boolean isGlobal() {
        return global;
    }

    /**
     * Sets whether or not all players can see the entity. The visibility of the entity will be
     * updated for each client when this method is run
     *
     * @param global if all clients can see the entity
     */
    public void setGlobal(boolean global) {
        if (spawned) { //No packets need to be sent if the entity shouldn't be visible anyway
            if (this.global && !global) {
                //Global was true and is now false
                final List<Player> nonViewers = new LinkedList<>(Bukkit.getOnlinePlayers());
                nonViewers.removeAll(viewers);

                nonViewers.forEach(this::destroy);
            } else if (!this.global && global) {
                //Global was false and is now true
                final List<Player> nonViewers = new LinkedList<>(Bukkit.getOnlinePlayers());
                nonViewers.removeAll(viewers);

                nonViewers.forEach(this::spawn);
            }
        }

        this.global = global;
    }

    //Override this method to handle custom metadata for entities
    protected WrappedDataWatcher buildMetadata() {
        final WrappedDataWatcher meta = new WrappedDataWatcher();

        meta.setObject(METADATA_INDEX, Registry.get(Byte.class), mask.buildByte()); //Bitmasked data
        meta.setObject((byte) 2, Registry.get(String.class), customName);          //Custom name
        meta.setObject(new WrappedDataWatcherObject(3, Registry.get(Boolean.class)),
                customNameVisible); /*booleans need to be wrapped in a WrappedDataWatcherObject
         because there is a method that takes a boolean as it's last value which doesn't do
         that same thing */

        return meta;
    }

    void destroy(Player... players) {
        final WrapperPlayServerEntityDestroy packet = new WrapperPlayServerEntityDestroy();

        packet.setEntityIds(new int[]{entityId});

        Arrays.stream(players).forEach(packet::sendPacket);
    }

    void spawn(Player... players) {
        final WrapperPlayServerSpawnEntityLiving packet = new WrapperPlayServerSpawnEntityLiving();

        packet.setEntityID(entityId);
        packet.setType(type);

        packet.setX(location.getX());
        packet.setY(location.getY());
        packet.setZ(location.getZ());

        packet.setYaw(location.getYaw());
        packet.setPitch(location.getPitch());
        packet.setHeadPitch(location.getYaw());

        packet.setMetadata(buildMetadata());

        Arrays.stream(players).forEach(packet::sendPacket);
    }

    void modifyMetadata(byte index, Serializer serializer, Object value) {
        if (!spawned) {
            return;
        }

        final WrappedDataWatcher wrappedWatcher = new WrappedDataWatcher();

        wrappedWatcher.setObject(new WrappedDataWatcherObject(index, serializer), value);

        final WrapperPlayServerEntityMetadata packet = new WrapperPlayServerEntityMetadata();

        packet.setEntityID(entityId);
        packet.setMetadata(wrappedWatcher.getWatchableObjects());

        sendPacket(packet);
    }

    private void updateEntityMask() {
        modifyMetadata((byte) METADATA_INDEX, Registry.get(Byte.class), mask.buildByte());
    }

    private void sendPacket(AbstractPacket packet) {
        getViewers().forEach(packet::sendPacket);
    }

    private double calculateDelta(double diff) {
        return diff * 4096;
    }

}
