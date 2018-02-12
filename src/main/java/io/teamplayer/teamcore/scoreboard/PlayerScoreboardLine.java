package io.teamplayer.teamcore.scoreboard;

import org.bukkit.entity.Player;

/**
 * Provides the content generation for a line in the Scoreboard that needs to generate different
 * content for each player
 */
public abstract class PlayerScoreboardLine extends ScoreboardLine {

    /**
     * Create a named ScoreboardLine with no content
     *
     * @param name the identifying name used to update the ScoreboardLine
     */
    public PlayerScoreboardLine(String name) {
        super(name);
    }

    /**
     * Create a named ScoreboardLine with no content that updates every specified amount of ticks
     *
     * @param name           name the identifying name used to update the ScoreboardLine
     * @param updateInterval the amount of ticks between each automated update
     */
    public PlayerScoreboardLine(String name, long updateInterval) {
        super(name, updateInterval);
    }

    @Deprecated
    @Override
    protected final String getContent(long animationCycle) {
        //This method should be used in this class as it's not the one that should be overriden
        // for dynamic content generation
        throw new UnsupportedOperationException("PlayerScoreboardLine needs a player passed into " +
                "its getContent");
    }

    /**
     * Get the content of the line by passing in the amount of times this line's content has been
     * updated and the player that this content is currently being generated for
     *
     * @param updateAmount the amount of times this ScoreboardLine has been updated
     * @param player       the player this content is being generated for
     */
    protected abstract String getContent(long updateAmount, Player player);
}
