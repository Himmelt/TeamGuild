package org.soraworld.guild.expansion;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.entity.Player;
import org.soraworld.guild.core.TeamGuild;
import org.soraworld.guild.manager.TeamManager;

public class GuildExpansion extends PlaceholderExpansion {

    private final TeamManager manager;

    public GuildExpansion(TeamManager manager) {
        this.manager = manager;
    }

    public String getIdentifier() {
        return manager.getPlugin().getId();
    }

    public String getAuthor() {
        return "Himmelt";
    }

    public String getVersion() {
        return manager.getPlugin().getVersion();
    }

    public String onPlaceholderRequest(Player player, String params) {
        TeamGuild guild = manager.fetchTeam(player);
        if (guild != null) return guild.getVariable(params);
        else return manager.getNoTeamDisplay();
    }
}
