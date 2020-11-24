package io.teamplayer.teamcore.scoreboard;

import org.apache.commons.lang.ArrayUtils;

import java.util.Arrays;

/**
 * A representation of what should be seen in the scoreboard sidebar consisting of many
 * ScoreboardLines.
 *
 * A title is the only requirement for a the Scoreboard frame to be viewable
 */
public class ScoreboardFrame {

    private ScoreboardLine title;
    private final ScoreboardLine[] lines = new ScoreboardLine[15];

    private byte cursor = (byte) (lines.length - 1);

    /**
     * Sets the title of the ScoreboardFrame
     *
     * @param title new title
     * @return this object
     */
    public ScoreboardFrame setTitle(ScoreboardLine title) {
        this.title = title;
        return this;
    }

    /**
     * Inserts a scoreboard line at the next available position in the scoreboard
     *
     * @param line scoreboard line to insert
     * @return this object
     */
    public ScoreboardFrame insert(ScoreboardLine line) {
        if (cursor == 0) {
            throw new IndexOutOfBoundsException("ScoreboardFrames can only have 15 lines");
        }

        lines[cursor] = line;

        cursor--;

        return this;
    }

    /**
     * Create a ScoreboardLine with specified content
     *
     * @param line content of line
     * @return this object
     */
    public ScoreboardFrame insert(String line) {
        return insert(new ScoreboardLine("unnamed",line));
    }

    /** Get the amount of lines in the ScoreboardFrame */
    public byte getSize() {
        return (byte) ((lines.length - 1) - cursor);
    }

    public ScoreboardLine[] getLines() {
        return minimizeArray();
    }

    private ScoreboardLine[] minimizeArray() {
        return (ScoreboardLine[]) ArrayUtils.subarray(lines, lines.length - getSize(), lines.length);
    }

    /**
     * Get current title of the frame
     */
    public ScoreboardLine getTitle() {
        return title;
    }

    @Override
    public String toString() {
        return "ScoreboardFrame{" +
                "title=" + title +
                ", lines=" + Arrays.toString(lines) +
                ", cursor=" + cursor +
                '}';
    }
}
