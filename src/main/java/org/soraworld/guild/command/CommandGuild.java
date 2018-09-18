package org.soraworld.guild.command;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.soraworld.guild.core.TeamGuild;
import org.soraworld.guild.manager.TeamManager;
import org.soraworld.violet.command.Paths;
import org.soraworld.violet.command.SpigotCommand;
import org.soraworld.violet.command.Sub;

public final class CommandGuild {
    @Sub
    public static void rank(SpigotCommand self, CommandSender sender, Paths args) {
        TeamManager manager = (TeamManager) self.manager;
        if (args.empty()) {
            manager.showRank(sender, 1);
        } else {
            try {
                manager.showRank(sender, Integer.valueOf(args.get(0)));
            } catch (Throwable ignored) {
                manager.sendKey(sender, "invalidInt");
            }
        }
    }

    @Sub
    public static void info(SpigotCommand self, CommandSender sender, Paths args) {
        TeamManager manager = (TeamManager) self.manager;
        if (args.empty()) {
            if (sender instanceof Player) {
                TeamGuild guild = manager.fetchTeam((Player) sender);
                if (guild != null) guild.showGuildInfo(sender);
                else manager.sendKey(sender, "notInAnyTeam");
            } else manager.sendKey(sender, "invalidArgs");
        } else {
            TeamGuild guild = manager.fetchTeam(args.get(0));
            if (guild != null) guild.showGuildInfo(sender);
            else manager.sendKey(sender, "guildNotExist");
        }
    }

    @Sub
    public static void disband(SpigotCommand self, CommandSender sender, Paths args) {
        TeamManager manager = (TeamManager) self.manager;
        if (args.empty()) {
            if (sender instanceof Player) {
                TeamGuild guild = manager.getGuild(sender.getName());
                if (guild != null) {
                    manager.disband(guild, sender.getName());
                    manager.sendKey(sender, "disbandGuild", guild.getDisplay());
                } else {
                    manager.sendKey(sender, "ownNoGuild");
                }
            } else manager.sendKey(sender, "invalidArgs");
        } else {
            TeamGuild guild = manager.getGuild(args.get(0));
            if (guild != null) {
                if (sender.hasPermission("guild" + ".admin") || guild.isLeader(sender.getName())) {
                    manager.disband(guild, args.get(0));
                    manager.sendKey(sender, "disbandGuild", guild.getDisplay());
                } else {
                    manager.sendKey(sender, "noOpPermOrNotLeader");
                }
            } else manager.sendKey(sender, "guildNotExist");
        }
    }

    @Sub(paths = {"frame", "give"}, perm = "admin")
    public static void frame_give(SpigotCommand self, CommandSender sender, Paths args) {
        TeamManager manager = (TeamManager) self.manager;
        if (args.notEmpty()) {
            if (args.size() == 2) {
                TeamGuild guild = manager.getGuild(args.first());
                if (guild != null) {
                    try {
                        int frame = Integer.valueOf(args.get(1));
                        manager.updateGuild(guild, g -> g.addFrame(frame));
                        manager.sendKey(sender, "addFrame");
                    } catch (Throwable ignored) {
                        manager.sendKey(sender, "invalidInt");
                    }
                } else manager.sendKey(sender, "guildNotExist");
            } else manager.sendKey(sender, "invalidArgs");
        } else manager.sendKey(sender, "emptyArgs");
    }

    @Sub(paths = {"frame", "take"}, perm = "admin")
    public static void frame_take(SpigotCommand self, CommandSender sender, Paths args) {
        TeamManager manager = (TeamManager) self.manager;
        if (args.notEmpty()) {
            if (args.size() == 2) {
                TeamGuild guild = manager.getGuild(args.first());
                if (guild != null) {
                    try {
                        int frame = Integer.valueOf(args.get(1));
                        manager.updateGuild(guild, g -> g.addFrame(-1 * frame));
                        manager.sendKey(sender, "takeFrame");
                    } catch (Throwable ignored) {
                        manager.sendKey(sender, "invalidInt");
                    }
                } else manager.sendKey(sender, "guildNotExist");
            } else manager.sendKey(sender, "invalidArgs");
        } else manager.sendKey(sender, "emptyArgs");
    }

    @Sub(paths = {"rankjoin"}, onlyPlayer = true)
    public static void showRankJoin(SpigotCommand self, CommandSender sender, Paths args) {
        if (args.notEmpty()) {
            TeamManager manager = (TeamManager) self.manager;
            Player player = (Player) sender;
            TeamGuild guild = manager.getGuild(player.getName());
            if (guild != null) {
                guild.setShowRankJoin(Boolean.valueOf(args.first()));
                manager.sendKey(player, guild.isShowRankJoin() ? "showRankJoin" : "notShowRankJoin");
            } else manager.sendKey(player, "guildNotExist");
        } else self.manager.sendKey(sender, "emptyArgs");
    }

    @Sub(onlyPlayer = true)
    public static void create(SpigotCommand self, CommandSender sender, Paths args) {
        TeamManager manager = (TeamManager) self.manager;
        Player player = (Player) sender;
        if (args.notEmpty()) {
            String text = args.first();
            try {
                if (text.getBytes("GB2312").length <= manager.maxDisplay) {
                    manager.createGuild(player, text);
                } else manager.sendKey(player, "textTooLong", manager.maxDisplay);
            } catch (Throwable e) {
                if (manager.isDebug()) e.printStackTrace();
                manager.sendKey(player, "EncodingException");
            }
        } else manager.createGuild(player, "Team_" + player.getName());
    }

    @Sub(paths = {"attorn", "to"}, onlyPlayer = true)
    public static void attorn_to(SpigotCommand self, CommandSender sender, Paths args) {
        TeamManager manager = (TeamManager) self.manager;
        Player player = (Player) sender;
        if (args.notEmpty()) {
            TeamGuild guild = manager.getGuild(player.getName());
            if (guild != null) {
                Player target = Bukkit.getPlayer(args.first());
                if (target != null) {
                    // TODO sendAttorn
                    manager.sendKey(player, "sendAttorn");
                    manager.sendKey(target, "receiveAttorn");
                } else manager.sendKey(player, "playerIsOffline", args.first());
            } else manager.sendKey(player, "noCreateTeam");
        } else manager.sendKey(player, "emptyArgs");
    }

    @Sub(paths = {"attorn", "accept"}, onlyPlayer = true)
    public static void attorn_accept(SpigotCommand self, CommandSender sender, Paths args) {
        // TODO accept
        TeamManager manager = (TeamManager) self.manager;
        Player player = (Player) sender;
        manager.sendKey(player, "attornAccept");
    }

    @Sub(paths = {"attorn", "reject"}, onlyPlayer = true)
    public static void attorn_reject(SpigotCommand self, CommandSender sender, Paths args) {
        // TODO reject
        TeamManager manager = (TeamManager) self.manager;
        Player player = (Player) sender;
        manager.sendKey(player, "attornReject");
    }

    @Sub(onlyPlayer = true)
    public static void join(SpigotCommand self, CommandSender sender, Paths args) {
        TeamManager manager = (TeamManager) self.manager;
        Player player = (Player) sender;
        if (args.empty()) manager.sendKey(player, "emptyArgs");
        else manager.joinGuild(player, args.first());
    }

    @Sub(onlyPlayer = true)
    public static void recruit(SpigotCommand self, CommandSender sender, Paths args) {
        // TODO recruit
        TeamManager manager = (TeamManager) self.manager;
        Player player = (Player) sender;
        manager.sendKey(player, "recruit");
    }

    @Sub(onlyPlayer = true)
    public static void leave(SpigotCommand self, CommandSender sender, Paths args) {
        TeamManager manager = (TeamManager) self.manager;
        Player player = (Player) sender;
        String username = player.getName();
        TeamGuild guild = manager.fetchTeam(player);
        if (guild != null) {
            if (guild.isLeader(username)) {
                manager.sendKey(player, "leaderCantLeave");
            } else {
                manager.leaveGuild(player, guild);
                manager.sendKey(player, "leaveGuild", guild.getDisplay());
                guild.notifyLeave(username);
            }
        } else manager.sendKey(player, "notInAnyTeam");
    }

    @Sub(onlyPlayer = true)
    public static void kick(SpigotCommand self, CommandSender sender, Paths args) {
        TeamManager manager = (TeamManager) self.manager;
        Player player = (Player) sender;
        if (args.empty()) {
            manager.sendKey(player, "emptyArgs");
            return;
        }
        TeamGuild guild = manager.fetchTeam(player);
        if (guild == null) {
            manager.sendKey(player, "notInAnyTeam");
            return;
        }
        if (guild.hasManager(player.getName())) {
            String member = args.get(0);
            if (guild.hasMember(member)) {
                manager.leaveGuild(member, guild);
                manager.sendKey(player, "kickSuccess", member);
                Player mmp = Bukkit.getPlayer(member);
                if (mmp != null) {
                    manager.sendKey(mmp, "beKicked", guild.getDisplay());
                }
            } else manager.sendKey(player, "noSuchMember", member);
        } else manager.sendKey(player, "notManager");
    }

    @Sub(onlyPlayer = true)
    public static void accept(SpigotCommand self, CommandSender sender, Paths args) {
        TeamManager manager = (TeamManager) self.manager;
        Player player = (Player) sender;
        if (args.empty()) {
            manager.sendKey(player, "emptyArgs");
            return;
        }
        TeamGuild guild = manager.fetchTeam(player);
        if (guild == null) {
            manager.sendKey(player, "notInAnyTeam");
            return;
        }
        if (guild.hasManager(player.getName())) {
            String applicant = args.get(0);
            TeamGuild team = manager.fetchTeam(applicant);
            if (team != null) {
                manager.sendKey(player, "alreadyJoinedTeam", applicant, team.getDisplay());
                guild.closeApplication(applicant);
                manager.saveGuild();
                return;
            }
            if (guild.hasApplication(applicant)) {
                if (guild.addMember(applicant)) {
                    manager.sendKey(player, "acceptMember", applicant);
                    Player app = Bukkit.getPlayer(applicant);
                    if (app != null) {
                        manager.sendKey(app, "joinAccepted", player.getName(), guild.getDisplay());
                    }
                } else {
                    manager.sendKey(player, "acceptFailed");
                    Player app = Bukkit.getPlayer(applicant);
                    if (app != null) {
                        manager.sendKey(app, "joinAcceptFailed", player.getName(), guild.getDisplay());
                    }
                }
                guild.closeApplication(applicant);
                manager.saveGuild();
            } else manager.sendKey(player, "noJoinApplication", applicant);
        } else manager.sendKey(player, "notManager");
    }

    @Sub(onlyPlayer = true)
    public static void reject(SpigotCommand self, CommandSender sender, Paths args) {
        TeamManager manager = (TeamManager) self.manager;
        Player player = (Player) sender;
        if (args.empty()) {
            manager.sendKey(player, "emptyArgs");
            return;
        }
        TeamGuild guild = manager.fetchTeam(player);
        if (guild == null) {
            manager.sendKey(player, "notInAnyTeam");
            return;
        }
        if (guild.hasManager(player)) {
            String applicant = args.get(0);
            if (guild.hasApplication(applicant)) {
                manager.sendKey(player, "rejectApplication", applicant);
                guild.closeApplication(applicant);
                manager.saveGuild();
                Player app = Bukkit.getPlayer(applicant);
                if (app != null) manager.sendKey(app, "applicationRejected");
            } else manager.sendKey(player, "noJoinApplication", applicant);
        } else manager.sendKey(player, "notManager");
    }

    @Sub(onlyPlayer = true)
    public static void display(SpigotCommand self, CommandSender sender, Paths args) {
        TeamManager manager = (TeamManager) self.manager;
        Player player = (Player) sender;
        TeamGuild guild = manager.getGuild(player.getName());
        if (guild == null) {
            manager.sendKey(player, "ownNoGuild");
            return;
        }
        if (args.empty()) {
            manager.sendKey(player, "getDisplay", guild.getDisplay());
        } else {
            String text = args.get(0);
            try {
                if (text.getBytes("GB2312").length <= manager.maxDisplay) {
                    guild.setDisplay(text);
                    manager.sendKey(player, "setDisplay", text);
                    manager.saveGuild();
                } else {
                    manager.sendKey(player, "textTooLong", manager.maxDisplay);
                }
            } catch (Throwable e) {
                if (manager.isDebug()) e.printStackTrace();
                manager.sendKey(player, "EncodingException");
            }
        }
    }

    @Sub(onlyPlayer = true)
    public static void describe(SpigotCommand self, CommandSender sender, Paths args) {
        TeamManager manager = (TeamManager) self.manager;
        Player player = (Player) sender;
        TeamGuild guild = manager.getGuild(player.getName());
        if (guild == null) {
            manager.sendKey(player, "ownNoGuild");
            return;
        }
        if (args.empty()) {
            manager.sendKey(player, "getDescription", guild.getDescription());
        } else {
            String text = args.get(0);
            try {
                if (text.getBytes("GB2312").length <= manager.maxDescription) {
                    guild.setDescription(text);
                    manager.sendKey(player, "setDescription", text);
                    manager.saveGuild();
                } else {
                    manager.sendKey(player, "textTooLong", manager.maxDescription);
                }
            } catch (Throwable e) {
                if (manager.isDebug()) e.printStackTrace();
                manager.sendKey(player, "EncodingException");
            }
        }
    }

    @Sub(onlyPlayer = true)
    public static void setman(SpigotCommand self, CommandSender sender, Paths args) {
        TeamManager manager = (TeamManager) self.manager;
        Player player = (Player) sender;
        TeamGuild guild = manager.getGuild(player.getName());
        if (guild == null) {
            manager.sendKey(player, "ownNoGuild");
            return;
        }
        if (args.notEmpty()) {
            String name = args.get(0);
            if (guild.hasMember(name)) {
                if (guild.getManSize() < guild.getTeamLevel().mans) {
                    guild.setManager(name);
                    manager.saveGuild();
                    manager.sendKey(player, "setManager", name);
                } else manager.sendKey(player, "maxManagers");
            } else manager.sendKey(player, "noSuchMember", name);
        } else manager.sendKey(player, "emptyArgs");
    }

    @Sub(onlyPlayer = true)
    public static void unsetman(SpigotCommand self, CommandSender sender, Paths args) {
        TeamManager manager = (TeamManager) self.manager;
        Player player = (Player) sender;
        TeamGuild guild = manager.getGuild(player.getName());
        if (guild == null) {
            manager.sendKey(player, "ownNoGuild");
            return;
        }
        if (args.notEmpty()) {
            String name = args.get(0);
            if (guild.hasManager(name)) {
                if (guild.isLeader(name)) {
                    manager.sendKey(player, "cantUnsetLeader");
                } else {
                    guild.unsetManager(name);
                    manager.saveGuild();
                    manager.sendKey(player, "unsetManager", name);
                }
            } else manager.sendKey(player, "isNotManager", name);
        } else manager.sendKey(player, "emptyArgs");
    }

    @Sub(onlyPlayer = true)
    public static void list(SpigotCommand self, CommandSender sender, Paths args) {
        TeamManager manager = (TeamManager) self.manager;
        Player player = (Player) sender;
        TeamGuild team = manager.fetchTeam(player);
        if (team != null) {
            team.showMemberList(player);
        } else manager.sendKey(player, "notInAnyTeam");
    }

    @Sub(onlyPlayer = true)
    public static void upgrade(SpigotCommand self, CommandSender sender, Paths args) {
        TeamManager manager = (TeamManager) self.manager;
        Player player = (Player) sender;
        manager.upgrade(player);
    }
}
