package org.soraworld.guild.listener;

import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.soraworld.guild.config.Config;
import org.soraworld.guild.core.TeamGuild;
import org.soraworld.guild.core.TeamManager;
import org.soraworld.guild.event.JoinApplicationEvent;

import javax.annotation.Nonnull;

public class EventListener implements Listener {

    private final Config config;
    private final TeamManager manager;

    public EventListener(@Nonnull Config config) {
        this.config = config;
        this.manager = config.getTeamManager();
    }

    @EventHandler
    public void onPlayerJoin(final PlayerJoinEvent event) {
        String player = event.getPlayer().getName();
        final TeamGuild guild = manager.fetchTeam(player);
        if (guild != null && guild.hasManager(player)) {
            Bukkit.getScheduler().runTaskLaterAsynchronously(config.plugin, new Runnable() {
                @Override
                public void run() {
                    guild.notifyApplication(event.getPlayer(), config);
                }
            }, 20);
        }
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        manager.clearPlayer(event.getPlayer().getName());
    }

    @EventHandler
    public void onJoinApplication(JoinApplicationEvent event) {
        TeamGuild guild = manager.getGuild(event.guild);
        if (guild != null) {
            guild.handleApplication(null, event.applicant, config);
        }
    }

}
