package org.soraworld.guild.command;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.soraworld.guild.config.Config;
import org.soraworld.guild.core.TeamGuild;
import org.soraworld.guild.core.TeamManager;
import org.soraworld.violet.command.CommandViolet;
import org.soraworld.violet.command.IICommand;
import org.soraworld.violet.constant.Violets;

import java.util.ArrayList;
import java.util.List;

public class CommandGuild extends CommandViolet {

    public CommandGuild(String name, String perm, final Config config, Plugin plugin) {
        super(name, perm, config, plugin);
        final TeamManager manager = config.getTeamManager();
        addSub(new IICommand("rank", config) {
            @Override
            public boolean execute(CommandSender sender, ArrayList<String> args) {
                System.out.println("rank");
                return super.execute(sender, args);
            }
        });
        addSub(new IICommand("create", null, config, true) {
            @Override
            public boolean execute(Player player, ArrayList<String> args) {
                if (args.isEmpty()) manager.createGuild(player, "Team_" + player.getName());
                else manager.createGuild(player, args.get(0));
                return true;
            }
        });
        addSub(new IICommand("join", null, config, true) {
            @Override
            public boolean execute(Player player, ArrayList<String> args) {
                if (args.isEmpty()) config.sendV(player, Violets.KEY_INVALID_ARG);
                else manager.joinGuild(player, args.get(0));
                return true;
            }

            @Override
            public List<String> getTabCompletions(ArrayList<String> args) {
                return config.getTeamManager().getGuilds();
            }
        });
        addSub(new IICommand("leave", null, config, true) {
            @Override
            public boolean execute(Player player, ArrayList<String> args) {
                return true;
            }
        });
        addSub(new IICommand("kick", null, config, true) {
            @Override
            public boolean execute(CommandSender sender, ArrayList<String> args) {
                return super.execute(sender, args);
            }
        });
        addSub(new IICommand("accept", null, config, true) {
            @Override
            public boolean execute(Player player, ArrayList<String> args) {
                if (args.isEmpty()) {
                    config.sendV(player, Violets.KEY_INVALID_ARG);
                    return true;
                }
                TeamGuild guild = manager.fetchTeam(player.getName());
                if (guild == null) {
                    config.send(player, "notInAnyTeam");
                    return true;
                }
                if (guild.hasManager(player.getName())) {
                    String applicant = args.get(0);
                    TeamGuild team = manager.fetchTeam(applicant);
                    if (team != null) {
                        config.send(player, "alreadyJoinedTeam", applicant, team.getDisplay());
                        guild.closeApplication(applicant);
                        manager.saveGuild();
                        return true;
                    }
                    if (guild.hasApplication(applicant)) {
                        if (guild.addMember(applicant)) {
                            config.send(player, "acceptMember", applicant);
                            Player app = Bukkit.getPlayer(applicant);
                            if (app != null) {
                                config.send(app, "joinAccepted", player.getName(), guild.getDisplay());
                            }
                        } else {
                            config.send(player, "acceptFailed");
                            Player app = Bukkit.getPlayer(applicant);
                            if (app != null) {
                                config.send(app, "joinAcceptFailed", player.getName(), guild.getDisplay());
                            }
                        }
                        guild.closeApplication(applicant);
                        manager.saveGuild();
                    } else {
                        config.send(player, "noJoinApplication", applicant);
                    }
                } else {
                    config.send(player, "notManager");
                }
                return true;
            }
        });
        addSub(new IICommand("reject", null, config, true) {
            @Override
            public boolean execute(Player player, ArrayList<String> args) {
                if (args.isEmpty()) {
                    config.sendV(player, Violets.KEY_INVALID_ARG);
                    return true;
                }
                TeamGuild guild = manager.fetchTeam(player.getName());
                if (guild == null) {
                    config.send(player, "notInAnyTeam");
                    return true;
                }
                if (guild.hasManager(player.getName())) {
                    String applicant = args.get(0);
                    if (guild.hasApplication(applicant)) {
                        config.send(player, "rejectApplication", applicant);
                        guild.closeApplication(applicant);
                        manager.saveGuild();
                        Player app = Bukkit.getPlayer(applicant);
                        if (app != null) {
                            config.send(app, "applicationRejected");
                        }
                    } else {
                        config.send(player, "noJoinApplication", applicant);
                    }
                } else {
                    config.send(player, "notManager");
                }
                return true;
            }
        });
        addSub(new IICommand("display", null, config, true) {
            @Override
            public boolean execute(Player player, ArrayList<String> args) {
                TeamGuild team = manager.fetchTeam(player.getName());
                if (args.isEmpty()) {
                    config.send(player, "getDisplay", team.getDisplay());
                } else {
                    team.setDisplay(args.get(0));
                    config.send(player, "setDisplay", args.get(0));
                    manager.saveGuild();
                }
                return true;
            }
        });
        addSub(new IICommand("list", null, config, true) {
            @Override
            public boolean execute(Player player, ArrayList<String> args) {
                TeamGuild team = manager.fetchTeam(player.getName());
                if (team != null) {
                    team.showMemberList(player, config);
                } else {
                    config.send(player, "notInAnyTeam");
                }
                return true;
            }
        });
        addSub(new IICommand("upgrade", null, config, true) {
            @Override
            public boolean execute(Player player, ArrayList<String> args) {
                TeamGuild guild = manager.getGuild(player.getName());
                if (guild != null) {
                    if (manager.upgrade(guild)) {
                        config.send(player, "guildUpgraded");
                        manager.saveGuild();
                    } else {
                        config.send(player, "guildIsTopLevel");
                    }
                } else {
                    config.send(player, "ownNoGuild");
                }
                return true;
            }
        });
    }

}
