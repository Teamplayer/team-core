/**
 * PacketWrapper - ProtocolLib wrappers for Minecraft packets
 * Copyright (C) dmulloy2 <http://dmulloy2.net>
 * Copyright (C) Kristian S. Strangeland
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package io.teamplayer.teamcore.wrapper;

import com.comphenix.packetwrapper.AbstractPacket;
import com.comphenix.protocol.wrappers.Pair;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.inventory.ItemStack;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.events.PacketEvent;
import com.comphenix.protocol.wrappers.EnumWrappers.ItemSlot;

import java.util.List;
import java.util.Optional;

public class WrapperPlayServerEntityEquipment extends AbstractPacket {
    public static final PacketType TYPE =
            PacketType.Play.Server.ENTITY_EQUIPMENT;

    public WrapperPlayServerEntityEquipment() {
        super(new PacketContainer(TYPE), TYPE);
        handle.getModifier().writeDefaults();
    }

    public WrapperPlayServerEntityEquipment(PacketContainer packet) {
        super(packet, TYPE);
    }

    /**
     * Retrieve Entity ID.
     * <p>
     * Notes: entity's ID
     *
     * @return The current Entity ID
     */
    public int getEntityID() {
        return handle.getIntegers().read(0);
    }

    /**
     * Set Entity ID.
     *
     * @param value - new value.
     */
    public void setEntityID(int value) {
        handle.getIntegers().write(0, value);
    }

    /**
     * Retrieve the entity of the painting that will be spawned.
     *
     * @param world - the current world of the entity.
     * @return The spawned entity.
     */
    public Entity getEntity(World world) {
        return handle.getEntityModifier(world).read(0);
    }

    /**
     * Retrieve the entity of the painting that will be spawned.
     *
     * @param event - the packet event.
     * @return The spawned entity.
     */
    public Entity getEntity(PacketEvent event) {
        return getEntity(event.getPlayer().getWorld());
    }

    /**
     * Set specified slot with an item
     *
     * @param item new item
     * @param slot slot to set item in
     */
    public void setSlot(ItemStack item, ItemSlot slot) {
        clearSlot(slot);
        if (item == null) return;

        List<Pair<ItemSlot, ItemStack>> pairs = handle.getSlotStackPairLists().read(0);
        pairs.add(new Pair<>(slot, item));
        handle.getSlotStackPairLists().write(0, pairs);
    }

    /** Clear item at slot **/
    private void clearSlot(ItemSlot slot) {
        List<Pair<ItemSlot, ItemStack>> pairs = handle.getSlotStackPairLists().read(0);
        pairs.removeIf(p -> p.getFirst().equals(slot));
        handle.getSlotStackPairLists().write(0, pairs);
    }

    /**
     * Get the item in the specified slot
     *
     * @param slot slot to look in
     * @return optional containing item at slot
     */
    public Optional<ItemStack> getItem(ItemSlot slot) {
        return handle.getSlotStackPairLists().read(0).stream()
                .filter(pair -> pair.getFirst().equals(slot))
                .map(Pair::getSecond)
                .findFirst();
    }
}