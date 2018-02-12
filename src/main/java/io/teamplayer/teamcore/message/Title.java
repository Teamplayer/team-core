package io.teamplayer.teamcore.message;

import org.bukkit.entity.Player;

/**
 * Data for sending a minecraft title
 */
public class Title {

    private String title = "";
    private String subtitle = "";

    private int fadeIn = 0;
    private int fadeOut = 0;
    private int duration = 20 * 2;

    public Title() {
    }

    public Title(String title, String subtitle) {
        this();
        this.title = title;
        this.subtitle = subtitle;
    }

    public Title(String title, String subtitle, int fadeIn, int fadeOut, int duration) {
        this(title, subtitle);
        this.fadeIn = fadeIn;
        this.fadeOut = fadeOut;
        this.duration = duration;
    }

    public void send(Player player) {
        player.sendTitle(getTitle(),
                getSubtitle(),
                getFadeIn(),
                getDuration(),
                getFadeOut());
    }

    public void send(Player... players) {
        for (Player player: players) {
            send(player);
        }
    }

    public String getTitle() {
        return title;
    }

    public Title setTitle(String title) {
        this.title = title;
        return this;
    }

    public String getSubtitle() {
        return subtitle;
    }

    public Title setSubtitle(String subtitle) {
        this.subtitle = subtitle;
        return this;
    }

    public int getFadeIn() {
        return fadeIn;
    }

    public Title setFadeIn(int fadeIn) {
        this.fadeIn = fadeIn;
        return this;
    }

    public int getFadeOut() {
        return fadeOut;
    }

    public Title setFadeOut(int fadeOut) {
        this.fadeOut = fadeOut;
        return this;
    }

    public int getDuration() {
        return duration;
    }

    public Title setDuration(int duration) {
        this.duration = duration;
        return this;
    }
}
