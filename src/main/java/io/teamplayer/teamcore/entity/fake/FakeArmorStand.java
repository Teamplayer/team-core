package io.teamplayer.teamcore.entity.fake;

import com.comphenix.protocol.wrappers.WrappedDataWatcher;
import org.bukkit.Location;

/**
 * A client-side armor stand
 */
public class FakeArmorStand extends FakeLivingEntity {

    //Attributes masked inside of the metadata at index 11
    private static final byte METADATA_INDEX = 14;
    private final FakeArmorStandBitMask mask = new FakeArmorStandBitMask();

    public FakeArmorStand(Location location) {
        super(1, location);
    }

    public boolean isSmall() {
        return mask.isSmall();
    }

    public void setSmall(boolean small) {
        mask.setSmall(small);
        updateArmorStandMask();
    }

    public boolean isHasArms() {
        return mask.hasArms();
    }

    public void setHasArms(boolean hasArms) {
        mask.setHasArms(hasArms);
        updateArmorStandMask();
    }

    public boolean hasNoBase() {
        return mask.hasNoBase();
    }

    public void setNoBase(boolean noBase) {
        mask.setNoBase(noBase);
        updateArmorStandMask();
    }

    public boolean isSetMarker() {
        return mask.isSetMarker();
    }

    public void setSetMarker(boolean setMarker) {
        mask.setSetMarker(setMarker);
        updateArmorStandMask();
    }

    @Override
    protected WrappedDataWatcher buildMetadata() {
        final WrappedDataWatcher metaData = super.buildMetadata();

        metaData.setObject(METADATA_INDEX, WrappedDataWatcher.Registry.get(Byte.class), mask.buildByte());

        return metaData;
    }


    private void updateArmorStandMask() {
        updateMetadata();
    }
}
