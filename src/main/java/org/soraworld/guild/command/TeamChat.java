package org.soraworld.guild.command;

import org.bukkit.entity.Player;
import org.soraworld.guild.core.TeamGuild;
import org.soraworld.guild.manager.TeamManager;
import org.soraworld.violet.command.Args;
import org.soraworld.violet.command.SpigotCommand;
import org.soraworld.violet.manager.SpigotManager;

public class TeamChat extends SpigotCommand {
    public TeamChat(String name, String perm, boolean onlyPlayer, SpigotManager manager, String... aliases) {
        super(name, perm, onlyPlayer, manager, aliases);
    }

    public void execute(Player player, Args args) {
        TeamGuild team = ((TeamManager) manager).fetchTeam(player);
        if (team != null) {
            team.teamChat(player, args.getContent());
        } else manager.sendKey(player, "player.notInAny");
    }

    public String getUsage() {
        return "/teamchat|tchat|tmsg|tm <message>";
    }
}
