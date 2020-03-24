package io.teamplayer.teamcore.util;

import org.bukkit.entity.Player;

import java.util.Collection;

/**
 * Any object/entity that exists only for the client. Contains methods for controlling who can
 * view the object
 */
public interface ClientSideObject {

    /**
     * Display the object for all viewers
     */
    public void spawn();

    /**
     * Destroy the object for all viewers
     */
    public void destroy();

    /**
     * Allow a player to see the object
     * @param player the new viewer
     */
    public void addViewer(Player player);

    /**
     * Disallow player to see the object
     * @param player the viewer to be removed
     */
    public void removeViewer(Player player);

    /**
     * Get a collection containing all of the players that can see the object
     */
    public Collection<Player> getViewers();

    /**
     * Get whether or not all players can see the object by default
     */
    public boolean isGlobal();

    /**
     * Set whether or not all players can see the object by default
     */
    public void setGlobal(boolean global);

}
