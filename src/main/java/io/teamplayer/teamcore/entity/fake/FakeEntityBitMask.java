package io.teamplayer.teamcore.entity.fake;

import io.teamplayer.teamcore.util.BitMaskBuilder;

final class FakeEntityBitMask extends BitMaskBuilder {

    private final static byte ON_FIRE = 0;
    private final static byte CROUCHED = 1;
    private final static byte SPRINTING = 3;
    private final static byte INVISBLE = 5;
    private final static byte GLOWING = 6;
    private final static byte ELYTRA_FLYING = 7;

    boolean isOnFire() {
        return getBit(ON_FIRE);
    }

    FakeEntityBitMask setOnFire(boolean onFire) {
        return (FakeEntityBitMask) setBit(ON_FIRE, onFire);
    }

    boolean isCrouched() {
        return getBit(CROUCHED);
    }

    FakeEntityBitMask setCrouched(boolean crouched) {
        return (FakeEntityBitMask) setBit(CROUCHED, crouched);
    }

    boolean isSprinting() {
        return getBit(SPRINTING);
    }

    FakeEntityBitMask setSprinting(boolean sprinting) {
        return (FakeEntityBitMask) setBit(SPRINTING, sprinting);
    }

    boolean isInvisible() {
        return getBit(INVISBLE);
    }

    FakeEntityBitMask setInvisible(boolean invisible) {
        return (FakeEntityBitMask) setBit(INVISBLE, invisible);
    }

    boolean isGlowing() {
        return getBit(GLOWING);
    }

    FakeEntityBitMask setGlowing(boolean glowing) {
        return (FakeEntityBitMask) setBit(GLOWING, glowing);
    }

    boolean isElytraFlying() {
        return getBit(ELYTRA_FLYING);
    }

    FakeEntityBitMask setElytraFlying(boolean elytraFlying) {
        return (FakeEntityBitMask) setBit(ELYTRA_FLYING, elytraFlying);
    }
}
