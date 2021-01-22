package io.teamplayer.teamcore.scoreboard;

import com.comphenix.packetwrapper.WrapperPlayServerScoreboardObjective;
import com.comphenix.packetwrapper.WrapperPlayServerScoreboardTeam;
import com.comphenix.protocol.wrappers.WrappedChatComponent;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.ListMultimap;
import org.bukkit.ChatColor;
import org.bukkit.craftbukkit.libs.org.apache.commons.lang3.Validate;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.*;

/**
 * Controls an individual player's scoreboard
 */
class PlayerScoreboard {

    final static byte SCOREBOARD_SIZE = 15;
    private final static byte TEAM_PRESUF_SIZE = 16;
    private final static byte MAX_LINE_SIZE = TEAM_PRESUF_SIZE * 2;
    private final static String OBJECTIVE_NAME = "TeamCoreBoard";

    private static final ListMultimap<Scoreboard, Team> teams = ArrayListMultimap.create();

    private static final String[] lineNames = new String[SCOREBOARD_SIZE];

    private static int teamsCreated = 0;

    static {
        final char rangeStart = 0xE900;
        for (int i = 0; i < SCOREBOARD_SIZE; i++) {
            lineNames[SCOREBOARD_SIZE - 1 - i] = "§" + (char) ((int) rangeStart + i);
        }


    }

    private final Player player;
    private final Objective objective;
    private final Scoreboard scoreboard;
    private final String[] lines = new String[SCOREBOARD_SIZE];

    PlayerScoreboard(Player player, Scoreboard scoreboard) {
        this.player = player;
        this.scoreboard = scoreboard;

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
     * Set the line in the player's scoreboard. Setting content to null removes the line.
     *
     * @param line    line number
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
                lines[line] = null;
            }
            return;
        }

        final WrapperPlayServerScoreboardTeam packet = new WrapperPlayServerScoreboardTeam();

        packet.setName(teams.get(scoreboard).get(line).getName());

        //This stuff doesn't matter because no player or entity is on this team
        packet.setMode(WrapperPlayServerScoreboardTeam.Mode.TEAM_UPDATED);
        packet.setNameTagVisibility("always");
        packet.setCollisionRule("always");
        packet.setColor(ChatColor.WHITE); //-1 means no color

        //Setting the team prefix and suffix forms the content
        packet.setPrefix(WrappedChatComponent.fromText(content.substring(0, Math.min(content.length(),
                TEAM_PRESUF_SIZE))));
        if (content.length() > TEAM_PRESUF_SIZE) {
            packet.setSuffix(WrappedChatComponent.fromText((content.charAt(TEAM_PRESUF_SIZE - 1) == '§' ?
                '§' : "") + content.substring(TEAM_PRESUF_SIZE)));
        } else {
            packet.setSuffix(WrappedChatComponent.fromText(""));
        }

        packet.sendPacket(player);

        if (lines[line] == null) {
            //We're adding the line after updating the content so that the line doesn't appear
            // then have the content appear, instead it will have the proper content when it appears
            setVisible(line, true);
        }

        lines[line] = content;
    }

    void setTitle(String content) {
        final WrapperPlayServerScoreboardObjective packet = new WrapperPlayServerScoreboardObjective();

        packet.setName(objective.getName());
        packet.setDisplayName(WrappedChatComponent.fromText(content));
        packet.setMode(WrapperPlayServerScoreboardObjective.Mode.UPDATE_VALUE);
        packet.setHealthDisplay(WrapperPlayServerScoreboardObjective.HealthDisplay.HEARTS);

        packet.sendPacket(player);
    }

    private void setVisible(byte line, boolean visible) {
        final Score score = objective.getScore(lineNames[line]);

        if (visible) {
            score.setScore((int) Double.NaN);
        } else {
            scoreboard.resetScores(lineNames[line]);
        }
    }
}
