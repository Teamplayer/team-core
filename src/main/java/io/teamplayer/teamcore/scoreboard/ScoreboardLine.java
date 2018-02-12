package io.teamplayer.teamcore.scoreboard;

/**
 * Provides the content generation for a line in the Scoreboard
 */
public class ScoreboardLine {

    private static final ScoreboardLine blankLine = new ScoreboardLine("");

    private final String name;
    private final long updateInterval;

    private String staticContent;

    /**
     * Create a named ScoreboardLine with no content
     *
     * @param name the identifying name used to update the ScoreboardLine
     */
    public ScoreboardLine(String name) {
        this(name, "");
    }

    /**
     * Create a named ScoreboardLine with specified content
     *
     * @param name    the identifying name used to update the ScoreboardLine
     * @param content the content of the ScoreboardLine
     */
    public ScoreboardLine(String name, String content) {
        this(name, content, 0);
    }

    /**
     * Create a named ScoreboardLine with no content that updates every specified amount of ticks
     *
     * @param name           name the identifying name used to update the ScoreboardLine
     * @param updateInterval the amount of ticks between each automated update
     */
    public ScoreboardLine(String name, long updateInterval) {
        this(name, "", updateInterval);
    }

    private ScoreboardLine(String name, String content, long updateInterval) {
        //This is private because there isn't a reason you would need to have content
        // specified and have the line automatically update
        this.name = name;
        this.staticContent = content;
        this.updateInterval = updateInterval;
    }

    /**
     * Returns a scoreboard line which is just a blank line
     */
    public static ScoreboardLine blank() {
        return blankLine;
    }

    /**
     * Set the static content that this line will return
     * Note: This will not update the scoreboard line on a scoreboard
     *
     * @param content new static content
     */
    public void setContent(String content) {
        staticContent = content;
    }

    /**
     * Get the content for the scoreboard line. This method is meant to be overriden to allow
     * ScoreboardLines to have dynamic content.
     *
     * @param animationCycle the current position in the animation cycle
     * @return the scoreboard line content
     */
    protected String getContent(long animationCycle) {
        return getStaticContent();
    }

    long getUpdateInterval() {
        return updateInterval;
    }

    boolean isAnimated() {
        return updateInterval != 0;
    }

    String getName() {
        return name;
    }

    private String getStaticContent() {
        if (staticContent != null) {
            return staticContent;
        } else {
            return "";
        }
    }

    @Override
    public String toString() {
        return "ScoreboardLine{" +
                "name='" + name + '\'' +
                ", updateInterval=" + updateInterval +
                ", staticContent='" + staticContent + '\'' +
                '}';
    }
}
