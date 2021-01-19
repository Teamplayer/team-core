package io.teamplayer.teamcore.entity.fake;

import com.comphenix.protocol.wrappers.WrappedDataWatcher;
import org.bukkit.Location;

/**
 * A client-side slime
 */
public class FakeSlime extends FakeEntity {

    private static final byte SIZE_INDEX = 12;
    private int size = 1;


    public FakeSlime(Location location) {
        super(75, location);
    }

    public void setSize(int size) {
        this.size = size;
        updateMetaData();
    }

    public int getSize() {
        return size;
    }

    @Override
    protected WrappedDataWatcher buildMetadata() {
        final WrappedDataWatcher watcher = super.buildMetadata();

        watcher.setObject(SIZE_INDEX, WrappedDataWatcher.Registry.get(Integer.class), 1);

        return watcher;
    }
}
