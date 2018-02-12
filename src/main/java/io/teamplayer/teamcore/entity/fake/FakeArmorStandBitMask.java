package io.teamplayer.teamcore.entity.fake;

import io.teamplayer.teamcore.util.BitMaskBuilder;

final class FakeArmorStandBitMask extends BitMaskBuilder {

    private final static byte SMALL = 0;
    private final static byte HAS_ARMS = 1;
    private final static byte HAS_NO_BASE = 2;
    private final static byte SET_MARKER = 3;

    boolean isSmall() {
        return getBit(SMALL);
    }

    FakeArmorStandBitMask setSmall(boolean small) {
        return (FakeArmorStandBitMask) setBit(SMALL, small);
    }

    boolean hasArms() {
        return getBit(HAS_ARMS);
    }

    FakeArmorStandBitMask setHasArms(boolean hasArms) {
        return (FakeArmorStandBitMask) setBit(HAS_ARMS, hasArms);
    }

    boolean hasNoBase() {
        return getBit(HAS_NO_BASE);
    }

    FakeArmorStandBitMask setNoBase(boolean noBase) {
        return (FakeArmorStandBitMask) setBit(HAS_NO_BASE, noBase);
    }

    boolean isSetMarker() {
        return getBit(SET_MARKER);
    }

    FakeArmorStandBitMask setSetMarker(boolean setMarker) {
        return (FakeArmorStandBitMask) setBit(SET_MARKER, setMarker);
    }
}
