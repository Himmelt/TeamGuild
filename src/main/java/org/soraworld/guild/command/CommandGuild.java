package org.soraworld.guild.command;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.soraworld.guild.config.Config;
import org.soraworld.guild.core.TeamGuild;
import org.soraworld.violet.command.CommandViolet;
import org.soraworld.violet.command.IICommand;

import java.util.ArrayList;
import java.util.List;

public class CommandGuild extends CommandViolet {

    public CommandGuild(String name, String perm, final Config config, Plugin plugin) {
        super(name, perm, config, plugin);
        addSub(new CommandTeam("team", null, config));
        addSub(new IICommand("create", null, config, true) {
            @Override
            public boolean execute(Player player, ArrayList<String> args) {
                TeamGuild team = config.getTeam(player.getName());
                if (team == null) {
                    config.createGuild(player.getName());
                } else {

                }
                return true;
            }
        });
        addSub(new IICommand("join", null, config, true) {
            @Override
            public boolean execute(Player player, ArrayList<String> args) {
                if (args.isEmpty()) {
                    System.out.println("参数为空");
                } else {
                    TeamGuild team = config.getTeam(player.getName());
                    if (team != null) {
                        // 已加入队伍
                    } else {
                        TeamGuild guild = config.getGuild(args.get(0));
                        if (guild != null) {
                            // 处理申请
                        } else {
                            // 队伍不存在
                        }
                    }
                }
                return true;
            }

            @Override
            public List<String> getTabCompletions(ArrayList<String> args) {
                return new ArrayList<>(config.getGuilds());
            }
        });
        addSub(new IICommand("leave", null, config, true) {
            @Override
            public boolean execute(CommandSender sender, ArrayList<String> args) {
                return super.execute(sender, args);
            }
        });
        addSub(new IICommand("kick", null, config, true) {
            @Override
            public boolean execute(CommandSender sender, ArrayList<String> args) {
                return super.execute(sender, args);
            }
        });
        addSub(new IICommand("list", null, config, true) {
            @Override
            public boolean execute(Player player, ArrayList<String> args) {
                TeamGuild team = TeamGuild.getTeam(player.getName());
                if (team != null) {
                    team.showMembers(player);
                } else {
                    config.send(player, "notInTeam");
                }
                return true;
            }
        });
        addSub(new IICommand("upgrade", null, config, true) {
            @Override
            public boolean execute(Player player, ArrayList<String> args) {
                TeamGuild guild = config.getGuild(player.getName());
                if (guild != null) {
                    if (!guild.isGuild()) {

                    } else {
                        config.send(player, "guildCantUpgrade");
                    }
                } else {
                    config.send(player, "ownNoGuild");
                }
                return true;
            }
        });
    }

}
