package io.teamplayer.teamcore.util;

import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;

import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

public final class BlockFindingUtil {

    private static final BlockFace[] directNeighborFaces = new BlockFace[6];

    static {
        //The first 6 block faces are the 6 cardinal directions
        System.arraycopy(BlockFace.values(), 0, directNeighborFaces, 0, 6);
    }

    private BlockFindingUtil() {
        throw new UnsupportedOperationException("This is a utility class");
    }

    /**
     * Get blocks directly connected to this block
     * @param block origin block
     * @return neighboring blocks
     */
    public Set<Block> getNeighborBlocks(Block block) {
        return Arrays.stream(directNeighborFaces)
                .map(block::getRelative)
                .collect(Collectors.toSet());
    }
}
