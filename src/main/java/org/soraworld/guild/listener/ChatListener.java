package org.soraworld.guild.listener;

import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.soraworld.guild.core.TeamGuild;
import org.soraworld.guild.manager.TeamManager;

public class ChatListener implements Listener {

    private final TeamManager manager;

    public ChatListener(final TeamManager manager) {
        this.manager = manager;
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onAsyncPlayerChat(AsyncPlayerChatEvent event) {
        if (!event.isCancelled()) {
            if (event.getFormat().contains("%1$s")) {
                String player = event.getPlayer().getName();
                TeamGuild guild = manager.fetchTeam(player);
                if (guild != null) {
                    StringBuilder build = new StringBuilder(event.getFormat());
                    build.insert(build.indexOf("%1$s") + 4, '[' + guild.getDisplay().replace('&', ChatColor.COLOR_CHAR) + ']');
                    event.setFormat(build.toString());
                }
            }
        }
    }
}
