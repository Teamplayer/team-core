package io.teamplayer.teamcore.entity.fake;

import com.comphenix.packetwrapper.AbstractPacket;
import io.teamplayer.teamcore.wrapper.WrapperPlayServerSpawnEntity;
import io.teamplayer.teamcore.wrapper.WrapperPlayServerSpawnEntityLiving;
import org.bukkit.Location;

/**
 * A client-side entity
 */
public class FakeLivingEntity extends FakeEntity {

    private int typeId;

    public FakeLivingEntity(int typeId, Location location) {
        super(null, location);
        this.typeId = typeId;
    }

    public int getTypeId() {
        return typeId;
    }

    @Override
    protected AbstractPacket buildSpawnPacket() {
        final WrapperPlayServerSpawnEntityLiving packet =
                WrapperPlayServerSpawnEntityLiving.fromSpawnEntity((WrapperPlayServerSpawnEntity) super.buildSpawnPacket());

        packet.setType(typeId);

        return packet;
    }
}
