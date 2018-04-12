package org.soraworld.guild.command;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.soraworld.guild.config.Config;
import org.soraworld.guild.constant.Constant;
import org.soraworld.guild.core.TeamGuild;
import org.soraworld.guild.core.TeamManager;
import org.soraworld.violet.command.CommandViolet;
import org.soraworld.violet.command.IICommand;
import org.soraworld.violet.constant.Violets;
import org.soraworld.violet.util.ListUtil;

import java.util.ArrayList;
import java.util.List;

public class CommandGuild extends CommandViolet {

    public CommandGuild(String name, String perm, final Config config, Plugin plugin) {
        super(name, perm, config, plugin);
        final TeamManager manager = config.getTeamManager();
        addSub(new IICommand("rank", config) {
            @Override
            public boolean execute(CommandSender sender, ArrayList<String> args) {
                if (args.isEmpty()) {
                    manager.showRank(sender, 1);
                } else {
                    try {
                        manager.showRank(sender, Integer.valueOf(args.get(0)));
                    } catch (Throwable ignored) {
                        config.sendV(sender, Violets.KEY_INVALID_INT);
                    }
                }
                return true;
            }
        });
        addSub(new IICommand("info", config) {
            @Override
            public boolean execute(CommandSender sender, ArrayList<String> args) {
                if (args.isEmpty()) {
                    if (sender instanceof Player) {
                        TeamGuild guild = manager.fetchTeam(sender.getName());
                        if (guild != null) guild.showGuildInfo(sender, config, manager);
                        else config.send(sender, "notInAnyTeam");
                    } else {
                        config.sendV(sender, Violets.KEY_ONLY_PLAYER_OR_INVALID_ARG);
                    }
                } else {
                    TeamGuild guild = manager.fetchTeam(args.get(0));
                    if (guild != null) guild.showGuildInfo(sender, config, manager);
                    else config.send(sender, "guildNotExist");
                }
                return true;
            }

            @Override
            public List<String> getTabCompletions(ArrayList<String> args) {
                if (args.isEmpty()) return manager.getGuilds();
                else return ListUtil.getMatchList(args.get(0), manager.getGuilds());
            }
        });
        addSub(new IICommand("disband", config) {
            @Override
            public boolean execute(CommandSender sender, ArrayList<String> args) {
                if (args.isEmpty()) {
                    if (sender instanceof Player) {
                        TeamGuild guild = manager.getGuild(sender.getName());
                        if (guild != null) {
                            manager.disband(guild, sender.getName());
                            config.send(sender, "disbandGuild", guild.getDisplay());
                        } else {
                            config.send(sender, "ownNoGuild");
                        }
                    } else {
                        config.sendV(sender, Violets.KEY_ONLY_PLAYER_OR_INVALID_ARG);
                    }
                } else {
                    TeamGuild guild = manager.getGuild(args.get(0));
                    if (guild != null) {
                        if (sender.hasPermission(Constant.PERM_ADMIN) || guild.isLeader(sender.getName())) {
                            manager.disband(guild, args.get(0));
                            config.send(sender, "disbandGuild", guild.getDisplay());
                        } else {
                            config.send(sender, "noOpPermOrNotLeader");
                        }
                    } else {
                        config.send(sender, "guildNotExist");
                    }
                }
                return true;
            }

            @Override
            public List<String> getTabCompletions(ArrayList<String> args) {
                if (args.isEmpty()) return manager.getGuilds();
                else return ListUtil.getMatchList(args.get(0), manager.getGuilds());
            }
        });
        addSub(new IICommand("create", null, config, true) {
            @Override
            public boolean execute(Player player, ArrayList<String> args) {
                if (args.isEmpty()) manager.createGuild(player, "Team_" + player.getName());
                else {
                    String text = args.get(0);
                    try {
                        if (text.getBytes("GB2312").length <= config.maxDisplay()) {
                            manager.createGuild(player, text);
                        } else {
                            config.send(player, "textTooLong", config.maxDisplay());
                        }
                    } catch (Throwable e) {
                        if (config.debug()) e.printStackTrace();
                        config.send(player, "EncodingException");
                    }
                }
                return true;
            }
        });
        addSub(new IICommand("attorn", null, config, true) {
            {
                addSub(new IICommand("to", null, config, true) {
                    @Override
                    public boolean execute(Player player, ArrayList<String> args) {
                        if (args.isEmpty()) {
                            config.sendV(player, Violets.KEY_INVALID_ARG);
                        } else {
                            TeamGuild guild = manager.getGuild(player.getName());
                            if (guild != null) {
                                Player target = Bukkit.getPlayer(args.get(0));
                                if (target != null) {
                                    // TODO sendAttorn
                                    config.send(player, "sendAttorn");
                                    config.send(target, "receiveAttorn");
                                } else {
                                    config.send(player, "playerIsOffline", args.get(0));
                                }
                            } else {
                                config.send(player, "noCreateTeam");
                            }
                        }
                        return true;
                    }
                });
                addSub(new IICommand("accept", null, config, true) {
                    @Override
                    public boolean execute(Player player, ArrayList<String> args) {
                        // TODO accept
                        config.send(player, "attornAccept");
                        return true;
                    }
                });
                addSub(new IICommand("reject", null, config, true) {
                    @Override
                    public boolean execute(Player player, ArrayList<String> args) {
                        // TODO reject
                        config.send(player, "attornReject");
                        return true;
                    }
                });
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
                if (args.isEmpty()) return manager.getGuilds();
                else return ListUtil.getMatchList(args.get(0), manager.getGuilds());
            }
        });
        addSub(new IICommand("recruit", null, config, true) {
            @Override
            public boolean execute(Player player, ArrayList<String> args) {
                // TODO recruit
                config.send(player, "recruit");
                return true;
            }
        });
        addSub(new IICommand("leave", null, config, true) {
            @Override
            public boolean execute(Player player, ArrayList<String> args) {
                String username = player.getName();
                TeamGuild guild = manager.fetchTeam(username);
                if (guild != null) {
                    if (guild.isLeader(username)) {
                        config.send(player, "leaderCantLeave");
                    } else {
                        manager.leaveGuild(username, guild);
                        config.send(player, "leaveGuild", guild.getDisplay());
                        guild.notifyLeave(username, config);
                    }
                } else {
                    config.send(player, "notInAnyTeam");
                }
                return true;
            }
        });
        addSub(new IICommand("kick", null, config, true) {
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
                    String member = args.get(0);
                    if (guild.hasMember(member)) {
                        manager.leaveGuild(member, guild);
                        config.send(player, "kickSuccess", member);
                        Player mmp = Bukkit.getPlayer(member);
                        if (mmp != null) {
                            config.send(mmp, "beKicked", guild.getDisplay());
                        }
                    } else {
                        config.send(player, "noSuchMember", member);
                    }
                } else {
                    config.send(player, "notManager");
                }
                return true;
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
                TeamGuild guild = manager.getGuild(player.getName());
                if (guild == null) {
                    config.send(player, "ownNoGuild");
                    return true;
                }
                if (args.isEmpty()) {
                    config.send(player, "getDisplay", guild.getDisplay());
                } else {
                    String text = args.get(0);
                    try {
                        if (text.getBytes("GB2312").length <= config.maxDisplay()) {
                            guild.setDisplay(text);
                            config.send(player, "setDisplay", text);
                            manager.saveGuild();
                        } else {
                            config.send(player, "textTooLong", config.maxDisplay());
                        }
                    } catch (Throwable e) {
                        if (config.debug()) e.printStackTrace();
                        config.send(player, "EncodingException");
                    }
                }
                return true;
            }
        });
        addSub(new IICommand("describe", null, config, true) {
            @Override
            public boolean execute(Player player, ArrayList<String> args) {
                TeamGuild guild = manager.getGuild(player.getName());
                if (guild == null) {
                    config.send(player, "ownNoGuild");
                    return true;
                }
                if (args.isEmpty()) {
                    config.send(player, "getDescription", guild.getDescription());
                } else {
                    String text = args.get(0);
                    try {
                        if (text.getBytes("GB2312").length <= config.maxDescription()) {
                            guild.setDescription(text);
                            config.send(player, "setDescription", text);
                            manager.saveGuild();
                        } else {
                            config.send(player, "textTooLong", config.maxDescription());
                        }
                    } catch (Throwable e) {
                        if (config.debug()) e.printStackTrace();
                        config.send(player, "EncodingException");
                    }
                }
                return true;
            }
        });
        addSub(new IICommand("setma", null, config, true) {
            @Override
            public boolean execute(Player player, ArrayList<String> args) {
                TeamGuild guild = manager.getGuild(player.getName());
                if (guild == null) {
                    config.send(player, "ownNoGuild");
                    return true;
                }
                if (args.isEmpty()) {
                    config.sendV(player, Violets.KEY_INVALID_ARG);
                } else {
                    String name = args.get(0);
                    if (guild.hasMember(name)) {
                        if (guild.getManSize() < manager.getLevel(guild).mans) {
                            guild.setManager(name);
                            manager.saveGuild();
                            config.send(player, "setManager", name);
                        } else {
                            config.send(player, "maxManagers");
                        }
                    } else {
                        config.send(player, "noSuchMember", name);
                    }
                }
                return true;
            }
        });
        addSub(new IICommand("unsetma", null, config, true) {
            @Override
            public boolean execute(Player player, ArrayList<String> args) {
                TeamGuild guild = manager.getGuild(player.getName());
                if (guild == null) {
                    config.send(player, "ownNoGuild");
                    return true;
                }
                if (args.isEmpty()) {
                    config.sendV(player, Violets.KEY_INVALID_ARG);
                } else {
                    String name = args.get(0);
                    if (guild.hasManager(name)) {
                        if (guild.isLeader(name)) {
                            config.send(player, "cantUnsetLeader");
                        } else {
                            guild.unsetManager(name);
                            manager.saveGuild();
                            config.send(player, "unsetManager", name);
                        }
                    } else {
                        config.send(player, "isNotManager", name);
                    }
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
                manager.upgrade(player);
                return true;
            }
        });
    }

}
