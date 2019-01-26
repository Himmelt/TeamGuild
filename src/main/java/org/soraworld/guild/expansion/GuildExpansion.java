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
        else {
            switch (params) {
                case "leader":
                    return "null";
                case "display":
                    return manager.getNoTeamDisplay();
                case "level":
                case "fame":
                case "balance":
                case "mem_amount":
                case "mem_max":
                case "man_amount":
                case "man_max":
                    return "0";
                default:
                    return "no variable";
            }
        }
    }
}
