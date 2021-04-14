package io.teamplayer.teamcore.entity.fake;

import com.comphenix.protocol.wrappers.WrappedDataWatcher;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_16_R3.inventory.CraftItemStack;
import org.bukkit.entity.EntityType;
import org.bukkit.inventory.ItemStack;

public class FakeItemEntity extends FakeEntity {

    private ItemStack item;

    public FakeItemEntity(Location location, ItemStack item) {
        super(EntityType.DROPPED_ITEM, location);
        this.item = item;
    }

    public void setItem(ItemStack item) {
        this.item = item;
        updateMetadata();
    }

    public ItemStack getItem() {
        return item;
    }

    @Override
    protected WrappedDataWatcher buildMetadata() {
        final WrappedDataWatcher metadata = super.buildMetadata();

        metadata.setObject(7, WrappedDataWatcher.Registry.getItemStackSerializer(false),
                CraftItemStack.asCraftCopy(item));

        return metadata;
    }
}
