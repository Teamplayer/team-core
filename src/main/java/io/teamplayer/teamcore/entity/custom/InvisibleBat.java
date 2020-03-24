package io.teamplayer.teamcore.entity.custom;

import io.teamplayer.teamcore.TeamCore;
import io.teamplayer.teamcore.util.NmsUtil;
import io.teamplayer.teamcore.util.TeamRunnable;
import net.minecraft.server.v1_15_R1.DamageSource;
import net.minecraft.server.v1_15_R1.EntityBat;
import net.minecraft.server.v1_15_R1.SoundEffect;

/**
 * An invisible, stationary entity used mostly for putting things on top of it's head
 */
public class InvisibleBat extends EntityBat {

    public InvisibleBat(org.bukkit.World world) {
        super(NmsUtil.getNmsWorld(world));

       ((TeamRunnable) () -> setInvisible(true)).runTaskLater(TeamCore.getInstance(), 1);
    }

    //Cancels entity damage
    @Override
    public boolean damageEntity(DamageSource damageSource, float v) {
        return false;
    }

    //Remove the logic for moving
    @Override
    protected void M() {
    }

    //Prevent entity from being nudged
    @Override
    public void g(float f, float f1) {
        super.g(f, f1);
    }

    //Override entity sounds
    @Override
    protected SoundEffect G() {
        return null;
    }

    @Override
    protected SoundEffect bW() {
        return null;
    }

    @Override
    protected SoundEffect bX() {
        return null;
    }

    public static class EntiyInfo implements CustomEntityInfo {

        @Override
        public byte getProtocolId() {
            return 65;
        }

        @Override
        public String getProtocolName() {
            return "bat";
        }
    }
}
