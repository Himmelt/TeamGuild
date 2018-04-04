package org.soraworld.guild.listener;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;
import org.soraworld.guild.config.Config;
import org.soraworld.guild.core.TeamManager;

import javax.annotation.Nonnull;

public class EventListener implements Listener {

    private final TeamManager manager;

    public EventListener(@Nonnull Config config) {
        this.manager = config.getTeamManager();
    }

    @EventHandler
    public void onPlayerLogout(PlayerQuitEvent event) {
        manager.clearPlayer(event.getPlayer().getName());
    }

}
