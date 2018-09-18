package org.soraworld.guild.listener;

import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.soraworld.guild.core.TeamGuild;
import org.soraworld.guild.event.JoinApplicationEvent;
import org.soraworld.guild.manager.TeamManager;

import javax.annotation.Nonnull;

public class EventListener implements Listener {

    private final TeamManager manager;

    public EventListener(@Nonnull TeamManager manager) {
        this.manager = manager;
    }

    @EventHandler
    public void onPlayerJoin(final PlayerJoinEvent event) {
        final TeamGuild guild = manager.fetchTeam(event.getPlayer());
        if (guild != null && guild.hasManager(event.getPlayer())) {
            Bukkit.getScheduler().runTaskLater(manager.getPlugin(), () -> guild.notifyApplication(event.getPlayer()), 20);
        }
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        manager.clearPlayer(event.getPlayer());
    }

    @EventHandler
    public void onJoinApplication(JoinApplicationEvent event) {
        TeamGuild guild = manager.getGuild(event.guild);
        if (guild != null) {
            guild.handleApplication(null, event.applicant);
        }
    }
}
