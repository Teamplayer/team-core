package io.teamplayer.teamcore.entity.custom;

import io.teamplayer.teamcore.util.NmsUtil;
import net.minecraft.server.v1_11_R1.EntityFallingBlock;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_11_R1.util.CraftMagicNumbers;

/**
 * A falling block entity that will not despawn
 */
public class BlockEntity extends EntityFallingBlock {

    public BlockEntity(org.bukkit.World world, Material material) {
        this(world, material, (byte) 0);
        ticksLived = 1; //Without setting ticksLived to 1 the entity will just disappear
    }

    public BlockEntity(org.bukkit.World world, Material material, byte data) {
        super(NmsUtil.getNmsWorld(world), 0, 0, 0,
                CraftMagicNumbers.getBlock(material).fromLegacyData(data));
    }

    //This method is the entity tick
    @Override
    public void A_() {
        super.A_();
       ticksLived = 1; //We need the entity forever young, or it will be despawned
    }
}
