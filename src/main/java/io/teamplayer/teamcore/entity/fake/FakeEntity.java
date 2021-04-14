package io.teamplayer.teamcore.entity.fake;

import com.comphenix.packetwrapper.*;
import com.comphenix.protocol.wrappers.EnumWrappers;
import com.comphenix.protocol.wrappers.WrappedChatComponent;
import com.comphenix.protocol.wrappers.WrappedDataWatcher;
import io.teamplayer.teamcore.immutable.ImmutableLocation;
import io.teamplayer.teamcore.util.ClientSideObject;
import io.teamplayer.teamcore.wrapper.WrapperPlayServerEntityEquipment;
import io.teamplayer.teamcore.wrapper.WrapperPlayServerSpawnEntity;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

public class FakeEntity implements ClientSideObject {

    private final EntityType type;
    private String customName = "";
    private boolean customNameVisible;
    private boolean noGravity = false;
    private final ItemStack[] equippedItems = new ItemStack[EnumWrappers.ItemSlot.values().length];
    private final boolean[] equippedItemsPending = new boolean[EnumWrappers.ItemSlot.values().length]; //tracks if packet has been sente
    private FakeEntity[] passengers;

    private static final byte METADATA_INDEX = 0;
    private final FakeEntityBitMask mask = new FakeEntityBitMask();

    private boolean spawned = false;

    protected final int entityId = ThreadLocalRandom.current().nextInt();

    protected Location location;

    private boolean global = true;
    private final Set<Player> viewers = new HashSet<>();

    public FakeEntity(EntityType type, Location location) {
        this.type = type;
        this.location = location;
    }

    /**
     * Spawn the entity for all viewers
     */
    @Override
    public void spawn() {
        spawned = true;
        getViewers().forEach(this::spawn);
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
        final double xDiff = location.getX() - this.location.getX();
        final double yDiff = location.getY() - this.location.getY();
        final double zDiff = location.getZ() - this.location.getZ();


        if (Math.abs(xDiff) > 8 || Math.abs(yDiff) > 8 || Math.abs(zDiff) > 8) {
            //If the entity moves more than 8 blocks in any direction the entity should be teleported instead of moved
            teleport(location);
            return;
        }

        move((short) xDiff, (short) yDiff, (short) zDiff);
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
    public void move(short xDiff, short yDiff, short zDiff) {
        if (Math.abs(xDiff) > 8 || Math.abs(yDiff) > 8 || Math.abs(zDiff) > 8) {
            throw new IllegalArgumentException("Entity movement cannot be any more than 8 units in any direction");
        }

        final io.teamplayer.teamcore.wrapper.WrapperPlayServerRelEntityMove packet = new io.teamplayer.teamcore.wrapper.WrapperPlayServerRelEntityMove();

        packet.setEntityID(entityId);

        packet.setDx(xDiff);
        packet.setDy(yDiff);
        packet.setDz(zDiff);

        packet.setOnGround(false);

        sendPacket(packet);

        location.add(xDiff, yDiff, zDiff);
    }

    /**
     * Rotate the fake entity to the specified yaw and pitch
     *
     * @param yaw   new yaw
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

    /**
     *
     * @return a WrappedDataWatcher containing all of the metadata for this entity
     */
    protected WrappedDataWatcher buildMetadata() {
        final WrappedDataWatcher meta = new WrappedDataWatcher();

        meta.setObject(METADATA_INDEX, WrappedDataWatcher.Registry.get(Byte.class), mask.buildByte()); //Bitmasked data
        meta.setObject((byte) 2, WrappedDataWatcher.Registry.getChatComponentSerializer(true),
                Optional.of(WrappedChatComponent.fromText(customName).getHandle()));          //Custom name
        meta.setObject(new WrappedDataWatcher.WrappedDataWatcherObject(3, WrappedDataWatcher.Registry.get(Boolean.class)),
                customNameVisible); /*booleans need to be wrapped in a WrappedDataWatcherObject
         because there is a method that takes a boolean as it's last value which doesn't do
         that same thing */
        meta.setObject(new WrappedDataWatcher.WrappedDataWatcherObject(5, WrappedDataWatcher.Registry.get(Boolean.class)),
                noGravity);

        return meta;
    }

    /**
     * Destroy the entity client side for the specified player
     */
    private void destroy(Player... players) {
        final WrapperPlayServerEntityDestroy packet = new WrapperPlayServerEntityDestroy();

        packet.setEntityIds(new int[]{entityId});

        Arrays.stream(players).forEach(packet::sendPacket);
    }

    /**
     *
     */
    private void spawn(Player player) {
        sendPacket(buildSpawnPacket());

        updateMetadata();
        updateEquipment(true);
    }

    protected AbstractPacket buildSpawnPacket() {
        final WrapperPlayServerSpawnEntity packet = new WrapperPlayServerSpawnEntity();

        packet.setEntityID(entityId);
        packet.setType(type);

        packet.setX(location.getX());
        packet.setY(location.getY());
        packet.setZ(location.getZ());

        packet.setYaw(location.getYaw());
        packet.setPitch(location.getPitch());

        return packet;
    }

    /**
     * Updates all of the metadata for the entity and sends a new packet
     */
    protected final void updateMetadata() {
        getViewers().forEach(this::sendMetadataPacket);
    }

    protected final void sendMetadataPacket(Player player) {
        if (!spawned) return;

        final WrapperPlayServerEntityMetadata packet = new WrapperPlayServerEntityMetadata();

        packet.setEntityID(entityId);
        packet.setMetadata(buildMetadata().getWatchableObjects());

        packet.sendPacket(player);
    }

    protected final void updateEquipment(boolean force) {
        final EnumWrappers.ItemSlot[] slots = EnumWrappers.ItemSlot.values();
        final WrapperPlayServerEntityEquipment packet = new WrapperPlayServerEntityEquipment();
        
        boolean changed = false;

        for (int i = 0; i < slots.length; i++) {
            if ((force || equippedItemsPending[i]) && equippedItems[i] != null) {

                packet.setEntityID(entityId);
                packet.setSlot(equippedItems[i], slots[i]);

                equippedItemsPending[i] = false;

                changed = true;
            }
        }

        if (changed) sendPacket(packet);
    }

    /**
     * Send packet to all viewers of FakeEntity
     */
    private void sendPacket(AbstractPacket packet) {
        getViewers().forEach(packet::sendPacket);
    }

    /**
     * Manually change the location of the FakeEntity
     * This method does not actually move or teleport the FakeEntity,
     * only changes it's internal location reference
     *
     * @param location new value
     */
    public void updateLocation(Location location) {
        this.location = location.clone();
    }

    public int getEntityId() {
        return entityId;
    }

    /**
     * Set the custom name that appears above the entities head.
     * Ensure customNameVisible is true to see custom name
     *
     * @param customName the custom name
     */
    public void setCustomName(String customName) {
        this.customName = customName;
        updateMetadata();
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
        this.customNameVisible = customNameVisible;
        updateMetadata();
    }

    public void setPassengers(FakeEntity... passengers) {
        WrapperPlayServerMount packet = new WrapperPlayServerMount();

        packet.setEntityID(entityId);
        packet.setPassengerIds(Arrays.stream(passengers)
                .mapToInt(FakeEntity::getEntityId)
                .toArray());

        sendPacket(packet);

        this.passengers = passengers;
    }

    public Optional<FakeEntity[]> getPassengers() {
        return Optional.ofNullable(passengers);
    }

    public boolean isCustomNameVisible() {
        return customNameVisible;
    }

    public boolean isOnFire() {
        return mask.isOnFire();
    }

    public void setOnFire(boolean onFire) {
        mask.setOnFire(onFire);
        updateMetadata();
    }

    public boolean isCrouched() {
        return mask.isCrouched();
    }

    public void setCrouched(boolean crouched) {
        mask.setCrouched(crouched);
        updateMetadata();
    }

    public boolean isSprinting() {
        return mask.isSprinting();
    }

    public void setSprinting(boolean sprinting) {
        mask.setSprinting(sprinting);
        updateMetadata();
    }

    public boolean isInvisible() {
        return mask.isInvisible();
    }

    public void setInvisible(boolean invisible) {
        mask.setInvisible(invisible);
        updateMetadata();
    }

    public boolean isGlowing() {
        return mask.isGlowing();
    }

    public void setGlowing(boolean glowing) {
        mask.setGlowing(glowing);
        updateMetadata();
    }

    public boolean isElytraFlying() {
        return mask.isElytraFlying();
    }

    public void setElytraFlying(boolean elytraFlying) {
        mask.setElytraFlying(elytraFlying);
        updateMetadata();
    }

    public boolean isNoGravity() {
        return noGravity;
    }

    public void setNoGravity(boolean noGravity) {
        this.noGravity = noGravity;
        updateMetadata();
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

    /**
     * Set what itemstack to have in specified slot for entity
     *
     * @param item new itemstack (null to clear slot)
     * @param slot slot to put item in
     */
    public void setEquipment(ItemStack item, EnumWrappers.ItemSlot slot) {
        equippedItems[slot.ordinal()] = item;
        equippedItemsPending[slot.ordinal()] = true;

        updateEquipment(false);
    }

    /**
     * Get ItemStack that entity has in specified equipment slot
     *
     * @param slot slot to get equipment of
     * @return optional containing equipped item
     */
    public Optional<ItemStack> getEquipment(EnumWrappers.ItemSlot slot) {
        return Optional.ofNullable(equippedItems[slot.ordinal()]);
    }
}
