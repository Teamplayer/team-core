package io.teamplayer.teamcore.scoreboard;

import com.google.common.collect.*;
import gnu.trove.map.TObjectLongMap;
import gnu.trove.map.hash.TObjectLongHashMap;
import io.teamplayer.teamcore.util.TeamRunnable;
import org.apache.commons.lang.Validate;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scoreboard.Scoreboard;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Manages all scoreboards for all players in a server
 */
public class ScoreboardManager {

    private static final Scoreboard BLANK_BOARD = Bukkit.getScoreboardManager().getNewScoreboard();

    private final JavaPlugin plugin;
    private final Scoreboard scoreboard;

    private final Map<Player, PlayerScoreboard> playerScoreboards = new WeakHashMap<>();

    //Variables for which ScoreboardLines are present in which ScoreboardFrames
    private final Map<ScoreboardLine, Multimap<ScoreboardFrame, Byte>> presentByLine =
            new HashMap<>();
    private final ListMultimap<String, ScoreboardLine> linesByName = LinkedListMultimap.create();

    private final TObjectLongMap<ScoreboardLine> animationCycle = new TObjectLongHashMap<>();
    private final Map<ScoreboardLine, BukkitRunnable> animatedLineRunnables = new HashMap<>();

    //Variables for controlling which Player has what ScoreboardFrame
    private ScoreboardFrame globalFrame;
    private final SetMultimap<ScoreboardFrame, UUID> playersViewingFrame = HashMultimap.create();
    private final Map<Player, ScoreboardFrame> playerSpecificFrames = new WeakHashMap<>();

    private final Map<ScoreboardLine, String> contentCache = new HashMap<>();

    public ScoreboardManager(JavaPlugin plugin, Scoreboard scoreboard) {
        this.plugin = plugin;
        this.scoreboard = scoreboard;

        Bukkit.getPluginManager().registerEvents(new PlayerListener(), plugin);
    }

    public void setGlobalFrame(ScoreboardFrame globalFrame) {
        final ScoreboardFrame oldGlobal = this.globalFrame;

        ensureLoaded(globalFrame);
        this.globalFrame = globalFrame; /*the new global frame needs to be set as a variable
            before all setPlayerFrame is called on every player because if it's not setPlayerFrame
            will treat the new global frame as a player specific one */
        playerScoreboards.keySet().forEach(p -> setPlayerFrame(p, globalFrame));
        ensureNecessary(oldGlobal);
    }

    public ScoreboardFrame getGlobalFrame() {
        return globalFrame;
    }

    public void setPlayerFrame(Player player, ScoreboardFrame newFrame) {

        if (!getScoreboardFrame(player).equals(newFrame)) {
            clearPlayerFrame(player);
            ensureLoaded(newFrame);
        }

        playersViewingFrame.put(newFrame, player.getUniqueId());

        if (globalFrame != null && !newFrame.equals(globalFrame)) {
            playerSpecificFrames.put(player, newFrame);
        }

        loadScoreboard(player);
    }

    private void clearPlayerFrame(Player player) {
        final ScoreboardFrame oldFrame = getScoreboardFrame(player);

        if (oldFrame != null) {
            playersViewingFrame.remove(oldFrame, player.getUniqueId());
            ensureNecessary(oldFrame);
        }

        playerSpecificFrames.remove(player);

        player.setScoreboard(BLANK_BOARD);
    }

    private ScoreboardFrame getScoreboardFrame(Player player) {
        return playerSpecificFrames.getOrDefault(player,
                globalFrame);
    }

    /**
     * Set the specified player's frame to be the global ScoreboardFrame or nothing, if a global
     * frame isn't present
     */
    public void resetPlayerFrame(Player player) {
        if (globalFrame != null) {
            setPlayerFrame(player, globalFrame);
        } else {
            clearPlayerFrame(player);
        }
    }

    /**
     * Update the content of the ScoreboardLine for every place it is being displayed to a player
     * for ScoreboardLines with the specified name
     *
     * @param lineName name of the lines to update
     */
    public void updateLine(String lineName) {
        linesByName.get(lineName).forEach(this::updateLine);
    }

    /**
     * Update the content of the ScoreboardLine for every place it is being displayed to a player
     *
     * @param line line to update
     */
    public void updateLine(ScoreboardLine line) {
        if (line == null) return;

        String lineContent = null;

        if (!(line instanceof PlayerScoreboardLine)) {
            lineContent = line.getContent(animationCycle.get(line));
            contentCache.put(line, lineContent); /* Line content is only cached for non
            PlayerScoreboardLines, as the cache is only used when a player joins the server, so
            there's no use of caching values while they're online */
        }

        for (ScoreboardFrame frame : presentByLine.get(line).keySet()) {
            for (Player player : getViewers(frame)) {

                if (line instanceof PlayerScoreboardLine) {
                    lineContent = getContent(line, player);
                }

                for (Byte position : presentByLine.get(line).get(frame)) {
                    PlayerScoreboard playerScoreboard = getPlayerScoreboard(player);

                    if (position == -1) {
                        playerScoreboard.setTitle(lineContent);
                    } else {
                        playerScoreboard.setLine(position, lineContent);
                    }
                }
            }
        }
    }

    private void loadScoreboard(Player player) {
        final ScoreboardFrame frame = getScoreboardFrame(player);
        final PlayerScoreboard scoreboard = getPlayerScoreboard(player);

        if (player.getScoreboard() == BLANK_BOARD) {
            player.setScoreboard(this.scoreboard);
        }

        scoreboard.setTitle(getContent(frame.getTitle(), player));

        final ScoreboardLine[] lines = frame.getLines();
        for (byte i = 0; i < PlayerScoreboard.SCOREBOARD_SIZE; i++) {
            if (i >= lines.length) {
                //Set all the unused lines to null so the unused lines disappear for the player
                scoreboard.setLine(i, null);
                continue;
            }

            scoreboard.setLine(i, getContent(lines[i], player));
        }
    }

    private PlayerScoreboard getPlayerScoreboard(Player player) {
        return playerScoreboards.get(player);
    }

    /**
     * Loads a ScoreboardFrame if it is not already
     */
    private void ensureLoaded(ScoreboardFrame frame) {
        if (frame == null) return;

        if (!isUsed(frame)) {
            Validate.isTrue(frame.getTitle() != null, "ScoreboardFrames must have titles");

            loadFrame(frame);
            refreshFrame(frame);
        }
    }

    /**
     * Unloads a ScoreboardFrame only if it is no longer used
     */
    private void ensureNecessary(ScoreboardFrame frame) {
        if (frame == null) {
            return;
        }
        if (!isUsed(frame)) {
            unloadFrame(frame);
        }
    }

    /**
     * Check to see if a ScoreboardFrame is visible to any player
     */
    private boolean isUsed(ScoreboardFrame frame) {
        return frame != null && ((globalFrame != null && frame.equals(globalFrame))
                || playerSpecificFrames.values().contains(frame));
    }

    private void loadFrame(ScoreboardFrame frame) {
        final ScoreboardLine[] lines = frame.getLines();
        for (byte position = 0; position < lines.length; position++) {
            loadLine(lines[position], frame, position);
        }

        loadLine(frame.getTitle(), frame, (byte) -1);
    }

    private void unloadFrame(ScoreboardFrame frame) {
        final ScoreboardLine[] lines = frame.getLines();
        for (byte position = 0; position < lines.length; position++) {
            unloadLine(lines[position], frame, position);
        }

        unloadLine(frame.getTitle(), frame, (byte) -1);
    }

    /**
     * Load and unload a ScoreboardFrame and update all of its ScoreboardLines
     */
    private void refreshFrame(ScoreboardFrame frame) {
        unloadFrame(frame);
        loadFrame(frame);

        Arrays.stream(frame.getLines())
                .distinct()
                .forEach(this::updateLine);
    }

    private void loadLine(ScoreboardLine line, ScoreboardFrame frame, byte position) {
        if (line == null) return;

        presentByLine.putIfAbsent(line, HashMultimap.create());
        presentByLine.get(line).put(frame, position);
        linesByName.put(line.getName(), line);

        loadAnimatedLine(line);
    }

    private void unloadLine(ScoreboardLine line, ScoreboardFrame frame, byte position) {
        if (line == null) return;

        presentByLine.get(line).remove(frame, position);
        linesByName.remove(line.getName(), line);

        unloadAnimatedLine(line);
    }

    private void loadAnimatedLine(ScoreboardLine line) {
        if (line.isAnimated() && !animatedLineRunnables.containsKey(line)) {
            final BukkitRunnable runnable =
                    ((TeamRunnable) () -> {
                        updateLine(line);
                        animationCycle.increment(line);
                    }).bukkit();

            animatedLineRunnables.put(line, runnable);
            runnable.runTaskTimer(plugin, 0, line.getUpdateInterval());

            animationCycle.put(line, 0);
        }
    }

    private void unloadAnimatedLine(ScoreboardLine line) {
        if (line.isAnimated() && animatedLineRunnables.containsKey(line)) {
            animatedLineRunnables.get(line).cancel();
            animatedLineRunnables.remove(line);

            animationCycle.remove(line);
        }
    }

    /**
     * Get the content of a line from the cache if it's not a PlayerScoreboardLine and get new
     * content if it is
     */
    private String getContent(ScoreboardLine line, Player player) {
        String content;

        if (line instanceof PlayerScoreboardLine) {
            content = ((PlayerScoreboardLine) line).getContent(animationCycle.get(line), player);
        } else {
            content = contentCache.computeIfAbsent(line, l -> l.getContent(animationCycle.get(l)));
        }

        return content;
    }

    private Set<Player> getViewers(ScoreboardFrame frame) {
        return playersViewingFrame.get(frame).stream()
                .map(Bukkit::getPlayer)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());
    }

    /**
     * Internal Class responsible for giving player's their scoreboard when they join the
     * server that must be public to interact with the BukkitAPI
     */
    public class PlayerListener implements Listener {

        PlayerListener() { //This class only needs to be used internally inside of ScoreboardManager
        }

        @EventHandler
        public void playerJoin(PlayerJoinEvent event) {
            final Player player = event.getPlayer();

            if (!playerScoreboards.containsKey(player)) {
                playerScoreboards.put(player, new PlayerScoreboard(player));
                event.getPlayer().setScoreboard(scoreboard);
            }

            resetPlayerFrame(player);
        }

        @EventHandler
        public void playerLeave(PlayerQuitEvent event) {
            playerScoreboards.remove(event);
        }
    }
}
