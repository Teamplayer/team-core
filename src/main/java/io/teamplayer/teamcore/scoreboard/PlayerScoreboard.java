package io.teamplayer.teamcore.scoreboard;

import com.comphenix.packetwrapper.WrapperPlayServerScoreboardObjective;
import com.comphenix.packetwrapper.WrapperPlayServerScoreboardTeam;
import com.comphenix.protocol.wrappers.WrappedChatComponent;
import org.bukkit.ChatColor;
import org.bukkit.craftbukkit.libs.org.apache.commons.lang3.Validate;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.*;

/**
 * Controls an individual player's scoreboard
 * All client-side
 */
class PlayerScoreboard {

    final static byte SCOREBOARD_SIZE = 15;
    private final static byte SIDEBAR_POSITION = 1;

    private final static String OBJECTIVE_NAME = "tc:obj";
    private final static String[] lineNames = new String[SCOREBOARD_SIZE];

    private static int teamsCreated = 0;

    static {
        final char rangeStart = 0xE900;
        for (int i = 0; i < SCOREBOARD_SIZE; i++) {
            lineNames[SCOREBOARD_SIZE - 1 - i] = "§" + (char) ((int) rangeStart + i);
        }


    }

    private final Player player;
    private final String[] activeLines = new String[SCOREBOARD_SIZE];

    PlayerScoreboard(Player player) {
        this.player = player;

        if (!teams.containsKey(scoreboard)) {
            for (byte i = 0; i < SCOREBOARD_SIZE; i++) {
                final Team team;
                final String teamName = teamsCreated + "tc";

                if (scoreboard.getTeam(teamName) != null) {
                    team = scoreboard.getTeam(teamName);
                } else {
                    team = scoreboard.registerNewTeam(teamsCreated + "tc");
                }

                teamsCreated++;
                teams.put(scoreboard, team);
                team.addEntry(lineNames[i]);
            }
        }

        if (scoreboard.getObjective(OBJECTIVE_NAME) == null) {
            objective = scoreboard.registerNewObjective(OBJECTIVE_NAME, "dummy");
            objective.setDisplaySlot(DisplaySlot.SIDEBAR);
        } else {
            objective = scoreboard.getObjective(OBJECTIVE_NAME);
        }

        player.setScoreboard(scoreboard);
    }

    /**
     * Set scoreboard title
     *
     * @param content new scoreboard title
     */
    void setTitle(String content) {
        sendTitlePacket(content, false);
    }

    /**
     * Set the line in the player's scoreboard. Setting content to null removes the line.
     *
     * @param line    line NUMBER
     * @param content new line content. Can be null
     * @throws IndexOutOfBoundsException when line is larger than the max scoreboard size
     */
    void setLine(byte line, String content) {
        Validate.validIndex(lines, line, "Scoreboards can not have more than" + SCOREBOARD_SIZE +
                " lines");
        if (content != null) {
            Validate.isTrue(content.length() <= MAX_LINE_SIZE, String.format("Lines " +
                    "cannot have content that is longer than %d characters", MAX_LINE_SIZE));
        }

        if (content == null) { //Remove this scoreboard line
            if (lines[line] != null) {
                setVisible(line, false);
                activeLines[line] = null;
            }
            return;
        }

        if (content.equals(activeLines[line])) return;

        sendTeamPacket(line, content, false);

        /* We're adding the line after updating the content so that the line doesn't appear
             then have the content appear. Instead it will have the proper content when it appears */
        if (activeLines[line] == null) {
            setVisible(line, true);
        }

        activeLines[line] = content;
    }

    private void setVisible(byte line, boolean visible) {
        final Score score = objective.getScore(lineNames[line]);

        if (visible) {
            score.setScore((int) Double.NaN);
        } else {
            scoreboard.resetScores(lineNames[line]);
        }
    }

    /**
     * Sends the packet to set the new title of the scoreboard or create the objective for the scoreboard
     *
     * @param title  new scoreboard title
     * @param create whether or not we're creative a new objective. false = updating existing obj
     */
    private void sendTitlePacket(String title, boolean create) {
        final WrapperPlayServerScoreboardObjective packet = new WrapperPlayServerScoreboardObjective();

        packet.setName(OBJECTIVE_NAME);
        packet.setDisplayName(WrappedChatComponent.fromText(title));

        packet.setMode(create ? WrapperPlayServerScoreboardObjective.Mode.ADD_OBJECTIVE :
                WrapperPlayServerScoreboardObjective.Mode.UPDATE_VALUE);

        packet.sendPacket(player);
    }

    /**
     * Send the ServerScoreboardTeam packet with the for the specified line with 'prefix' as the team prefix
     *
     * @param line   the scoreboard line to be updated
     * @param prefix the new team prefix
     * @param create whether we're creating a new team. false = updating existing team
     */
    private void sendTeamPacket(byte line, String prefix, boolean create) {
        final WrapperPlayServerScoreboardTeam teamCreate = new WrapperPlayServerScoreboardTeam();

        teamCreate.setName(getTeamName(line));
        teamCreate.setDisplayName(WrappedChatComponent.fromText(getTeamName(line)));
        teamCreate.setPrefix(WrappedChatComponent.fromText(prefix));
        teamCreate.setColor(ChatColor.WHITE); //This sets default chat color for prefix. I prefer white as a default

        teamCreate.setMode(create ? WrapperPlayServerScoreboardTeam.Mode.TEAM_CREATED :
                WrapperPlayServerScoreboardTeam.Mode.TEAM_UPDATED);
        if (create) teamCreate.setPlayers(Collections.singletonList(lineNames[line]));

        teamCreate.sendPacket(player);
    }

    /**
     * Sends the packet that sets the current objective to be displayed on the sidebar(scoreboard)
     */
    private void sendDisplayObjectivePacket() {
        final WrapperPlayServerScoreboardDisplayObjective displayObjective =
                new WrapperPlayServerScoreboardDisplayObjective();

        displayObjective.setScoreName(OBJECTIVE_NAME);
        displayObjective.setPosition(SIDEBAR_POSITION);

        displayObjective.sendPacket(player);
    }

    private String getTeamName(int index) {
        return "tc:" + index;
    }
}
