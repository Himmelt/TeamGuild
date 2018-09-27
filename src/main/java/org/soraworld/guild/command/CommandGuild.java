package org.soraworld.guild.command;

import com.bekvon.bukkit.residence.Residence;
import com.bekvon.bukkit.residence.protection.ClaimedResidence;
import com.bekvon.bukkit.residence.protection.FlagPermissions;
import com.bekvon.bukkit.residence.protection.ResidenceManager;
import com.bekvon.bukkit.residence.selection.SelectionManager;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.soraworld.guild.core.TeamGuild;
import org.soraworld.guild.economy.Economy;
import org.soraworld.guild.manager.TeamManager;
import org.soraworld.violet.command.Paths;
import org.soraworld.violet.command.SpigotCommand;
import org.soraworld.violet.command.Sub;

import java.util.UUID;

import static org.soraworld.guild.TeamGuild.residenceApi;

public final class CommandGuild {
    @Sub
    public static void top(SpigotCommand self, CommandSender sender, Paths args) {
        TeamManager manager = (TeamManager) self.manager;
        if (args.empty()) {
            manager.showRank(sender, 1);
        } else {
            try {
                manager.showRank(sender, Integer.valueOf(args.first()));
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
                if (guild != null) sender.sendMessage(guild.getHover());
                else manager.sendKey(sender, "notInAnyTeam");
            } else manager.sendKey(sender, "invalidArgs");
        } else {
            TeamGuild guild = manager.fetchTeam(args.first());
            if (guild != null) sender.sendMessage(guild.getHover());
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
                    manager.disband(guild);
                    manager.sendKey(sender, "disbandGuild", guild.getDisplay());
                } else manager.sendKey(sender, "ownNoGuild");
            } else manager.sendKey(sender, "invalidArgs");
        } else {
            TeamGuild guild = manager.getGuild(args.first());
            if (guild != null) {
                if (sender.hasPermission(manager.defAdminPerm()) || guild.isLeader(sender.getName())) {
                    manager.disband(guild);
                    manager.sendKey(sender, "disbandGuild", guild.getDisplay());
                } else manager.sendKey(sender, "noOpPermOrNotLeader");
            } else manager.sendKey(sender, "guildNotExist");
        }
    }

    @Sub(paths = {"eco", "give"}, perm = "admin")
    public static void eco_give(SpigotCommand self, CommandSender sender, Paths args) {
        TeamManager manager = (TeamManager) self.manager;
        if (args.notEmpty()) {
            if (args.size() == 2) {
                TeamGuild guild = manager.getGuild(args.first());
                if (guild != null) {
                    try {
                        float amount = Float.valueOf(args.get(1));
                        manager.updateGuild(guild, g -> g.addEco(amount));
                        manager.sendKey(sender, "addEco", amount, guild.getDisplay());
                    } catch (Throwable ignored) {
                        manager.sendKey(sender, "invalidFloat");
                    }
                } else manager.sendKey(sender, "guildNotExist");
            } else manager.sendKey(sender, "invalidArgs");
        } else manager.sendKey(sender, "emptyArgs");
    }

    @Sub(paths = {"eco", "take"}, perm = "admin")
    public static void eco_take(SpigotCommand self, CommandSender sender, Paths args) {
        TeamManager manager = (TeamManager) self.manager;
        if (args.notEmpty()) {
            if (args.size() == 2) {
                TeamGuild guild = manager.getGuild(args.first());
                if (guild != null) {
                    try {
                        float amount = Float.valueOf(args.get(1));
                        if (guild.hasEnough(amount)) {
                            manager.updateGuild(guild, g -> g.takeEco(-1 * amount));
                            manager.sendKey(sender, "takeEco", guild.getDisplay(), amount);
                        } else manager.sendKey(sender, "notEnough", guild.getEco());
                    } catch (Throwable ignored) {
                        manager.sendKey(sender, "invalidFloat");
                    }
                } else manager.sendKey(sender, "guildNotExist");
            } else manager.sendKey(sender, "invalidArgs");
        } else manager.sendKey(sender, "emptyArgs");
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
                        manager.sendKey(sender, "addFrame", frame, guild.getDisplay());
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
                        manager.sendKey(sender, "takeFrame", guild.getDisplay(), frame);
                    } catch (Throwable ignored) {
                        manager.sendKey(sender, "invalidInt");
                    }
                } else manager.sendKey(sender, "guildNotExist");
            } else manager.sendKey(sender, "invalidArgs");
        } else manager.sendKey(sender, "emptyArgs");
    }

    @Sub(paths = {"topjoin"}, onlyPlayer = true)
    public static void showTopJoin(SpigotCommand self, CommandSender sender, Paths args) {
        if (args.notEmpty()) {
            TeamManager manager = (TeamManager) self.manager;
            Player player = (Player) sender;
            TeamGuild guild = manager.getGuild(player.getName());
            if (guild != null) {
                guild.setShowTopJoin(Boolean.valueOf(args.first()));
                manager.sendKey(player, guild.isShowTopJoin() ? "showRankJoin" : "notShowRankJoin");
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

    @Sub(onlyPlayer = true)
    public static void donate(SpigotCommand self, CommandSender sender, Paths args) {
        TeamManager manager = (TeamManager) self.manager;
        Player player = (Player) sender;
        if (args.notEmpty()) {
            TeamGuild guild = manager.fetchTeam(player);
            if (guild != null) {
                try {
                    int amount = Integer.valueOf(args.first());
                    if (Economy.hasEnough(player, amount)) {
                        Economy.takeEco(player, amount);
                        guild.addEco(amount);
                        manager.sendKey(player, "donateSuccess", amount);
                    } else manager.sendKey(player, "notEnough");
                } catch (Throwable e) {
                    manager.sendKey(player, "invalidInt");
                }
            } else manager.sendKey(player, "notInAnyTeam");
        } else manager.sendKey(player, "emptyArgs");
    }

    @Sub
    public static void chat() {
        // [Display][Name] meaasge
        // TODO test when args contains space or with "xxx     sdsd  x"
    }

    @Sub(onlyPlayer = true)
    public static void home(SpigotCommand self, CommandSender sender, Paths args) {
        TeamManager manager = (TeamManager) self.manager;
        Player player = (Player) sender;
        if (residenceApi) {
            TeamGuild guild = manager.fetchTeam(player);
            if (guild != null) {
                ClaimedResidence res = Residence.getInstance().getResidenceManager().getByName(guild.getHomeName());
                if (res != null) {
                    Location loc = res.getTeleportLocation();
                    if (loc != null && player.teleport(loc)) manager.sendKey(player, "tpGuildSuccess");
                    else manager.sendKey(player, "tpGuildFailed");
                } else manager.sendKey(player, "guildResNotCreate");
            } else manager.sendKey(player, "notInAnyTeam");
        } else manager.sendKey(player, "noResidenceApi");
    }

    @Sub(onlyPlayer = true)
    public static void sethome(SpigotCommand self, CommandSender sender, Paths args) {
        TeamManager manager = (TeamManager) self.manager;
        Player player = (Player) sender;
        if (residenceApi) {
            TeamGuild guild = manager.getGuild(player.getName());
            if (guild != null) {
                ResidenceManager apiR = Residence.getInstance().getResidenceManager();
                SelectionManager apiS = Residence.getInstance().getSelectionManager();
                ClaimedResidence res = apiR.getByName(guild.getHomeName());
                if (res == null) {
                    if (apiS.hasPlacedBoth(player)) {
                        double amount = apiS.getSelectionCuboid(player).getSize() * manager.residencePrice;
                        if (guild.hasEnough(amount)) {
                            String serverOwner = Residence.getInstance().getServerLandname();
                            String serverUUID = Residence.getInstance().getServerLandUUID();
                            if (apiR.addResidence(player, serverOwner, guild.getHomeName(), apiS.getPlayerLoc1(player), apiS.getPlayerLoc2(player), true)) {
                                res = apiR.getByName(guild.getHomeName());
                                res.getPermissions().setOwnerUUID(UUID.fromString(serverUUID));
                                //res.getPermissions().setOwner(serverOwner, false);
                                res.getPermissions().setPlayerFlag(player.getName(), "admin", FlagPermissions.FlagState.TRUE);
                                guild.takeEco(amount);
                                manager.sendKey(player, "guildResCreateSuccess");
                            } else manager.sendKey(player, "guildResCreateFailed");
                        } else manager.sendKey(player, "guildEcoNotEnough");
                    } else manager.sendKey(player, "selectFirst");
                } else manager.sendKey(player, "GuildResExist");
            } else manager.sendKey(player, "ownNoGuild");
        } else manager.sendKey(player, "noResidenceApi");
    }

    @Sub(onlyPlayer = true)
    public static void delhome(SpigotCommand self, CommandSender sender, Paths args) {
        TeamManager manager = (TeamManager) self.manager;
        Player player = (Player) sender;
        if (residenceApi) {
            TeamGuild guild = manager.fetchTeam(player);
            if (guild != null) {
                ResidenceManager apiR = Residence.getInstance().getResidenceManager();
                ClaimedResidence res = apiR.getByName(guild.getHomeName());
                if (res != null) {
                    apiR.removeResidence(res);
                    manager.sendKey(player, "GuildResRemove");
                } else manager.sendKey(player, "guildResNotCreate");
            } else manager.sendKey(player, "notInAnyTeam");
        } else manager.sendKey(player, "noResidenceApi");
    }

    @Sub(onlyPlayer = true)
    public static void join(SpigotCommand self, CommandSender sender, Paths args) {
        TeamManager manager = (TeamManager) self.manager;
        Player player = (Player) sender;
        if (args.empty()) manager.sendKey(player, "emptyArgs");
        else manager.joinGuild(player, args.first());
    }

    @Sub(paths = {"accept", "join"}, onlyPlayer = true)
    public static void accept_join(SpigotCommand self, CommandSender sender, Paths args) {
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
        if (guild.isManager(player)) {
            String applicant = args.first();
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

    @Sub(paths = {"reject", "join"}, onlyPlayer = true)
    public static void reject_join(SpigotCommand self, CommandSender sender, Paths args) {
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
        if (guild.isManager(player)) {
            String applicant = args.first();
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
    public static void attorn(SpigotCommand self, CommandSender sender, Paths args) {
        TeamManager manager = (TeamManager) self.manager;
        Player player = (Player) sender;
        if (args.notEmpty()) {
            TeamGuild guild = manager.getGuild(player.getName());
            if (guild != null) {
                Player target = Bukkit.getPlayer(args.first());
                if (target != null) {
                    if (!player.equals(target)) {
                        guild.sendAttorn(target);
                        manager.sendKey(player, "sendAttorn", target.getName());
                    } else manager.sendKey(player, "cantAttornToSelf");
                } else manager.sendKey(player, "playerIsOffline", args.first());
            } else manager.sendKey(player, "ownNoGuild");
        } else manager.sendKey(player, "emptyArgs");
    }

    @Sub(onlyPlayer = true)
    public static void unattorn(SpigotCommand self, CommandSender sender, Paths args) {
        TeamManager manager = (TeamManager) self.manager;
        Player player = (Player) sender;
        TeamGuild guild = manager.getGuild(player.getName());
        if (guild != null) {
            guild.resetAttorn();
            manager.sendKey(player, "unAttorn");
        } else manager.sendKey(player, "ownNoGuild");
    }

    @Sub(paths = {"accept", "attorn"}, onlyPlayer = true)
    public static void accept_attorn(SpigotCommand self, CommandSender sender, Paths args) {
        TeamManager manager = (TeamManager) self.manager;
        Player player = (Player) sender;
        if (args.notEmpty()) {
            TeamGuild team = manager.fetchTeam(player);
            TeamGuild guild = manager.getGuild(args.first());
            if (team == null || team.equals(guild)) {
                if (guild != null) {
                    Player oldLeader = guild.getLeader();
                    if (manager.attornTo(guild, player)) {
                        manager.sendKey(player, "attornAccept", guild.getDisplay());
                        if (oldLeader != null) manager.sendKey(oldLeader, "attornAcceptBy", player.getName());
                    } else manager.sendKey(player, "notAttorn", guild.getDisplay());
                } else manager.sendKey(player, "guildNotExist");
            } else manager.sendKey(player, "inAnotherGuild");
        } else manager.sendKey(player, "emptyArgs");
    }

    @Sub(paths = {"reject", "attorn"}, onlyPlayer = true)
    public static void reject_attorn(SpigotCommand self, CommandSender sender, Paths args) {
        TeamManager manager = (TeamManager) self.manager;
        Player player = (Player) sender;
        if (args.notEmpty()) {
            TeamGuild guild = manager.getGuild(args.first());
            if (guild != null) {
                if (guild.rejectAttorn(player)) {
                    manager.sendKey(player, "attornReject", guild.getDisplay());
                    Player oldLeader = guild.getLeader();
                    if (oldLeader != null) manager.sendKey(oldLeader, "attornRejectBy", player.getName());
                } else manager.sendKey(player, "notAttorn", guild.getDisplay());
            } else manager.sendKey(player, "guildNotExist");
        } else manager.sendKey(player, "emptyArgs");
    }

    @Sub(onlyPlayer = true)
    public static void invite(SpigotCommand self, CommandSender sender, Paths args) {
        TeamManager manager = (TeamManager) self.manager;
        Player player = (Player) sender;
        if (args.notEmpty()) {
            TeamGuild guild = manager.fetchTeam(player);
            if (guild != null) {
                if (guild.isManager(player)) {
                    Player target = Bukkit.getPlayer(args.first());
                    if (target != null) {
                        guild.sendInvite(player, target);
                        manager.sendKey(player, "sendInvite");
                    } else manager.sendKey(player, "playerIsOffline", args.first());
                } else manager.sendKey(player, "notManager");
            } else manager.sendKey(player, "guildNotExist");
        } else manager.sendKey(player, "emptyArgs");
    }

    @Sub(onlyPlayer = true)
    public static void uninvite(SpigotCommand self, CommandSender sender, Paths args) {
        TeamManager manager = (TeamManager) self.manager;
        Player player = (Player) sender;
        if (args.notEmpty()) {
            TeamGuild guild = manager.fetchTeam(player);
            if (guild != null) {
                if (guild.isManager(player)) {
                    if (args.first().equals("@ALL") && guild.isLeader(player)) {
                        guild.unInviteAll();
                        manager.sendKey(player, "unInvitedAll");
                        return;
                    }
                    UUID target = Bukkit.getOfflinePlayer(args.first()).getUniqueId();
                    guild.unInvite(target);
                    manager.sendKey(player, "unInvited", args.first());
                } else manager.sendKey(player, "notManager");
            } else manager.sendKey(player, "guildNotExist");
        } else manager.sendKey(player, "emptyArgs");
    }

    @Sub(paths = {"accept", "invite"}, onlyPlayer = true)
    public static void accept_invite(SpigotCommand self, CommandSender sender, Paths args) {
        TeamManager manager = (TeamManager) self.manager;
        Player player = (Player) sender;
        if (args.notEmpty()) {
            TeamGuild team = manager.fetchTeam(player);
            TeamGuild guild = manager.getGuild(args.first());
            if (team == null) {
                if (guild != null) {
                    if (guild.isInvited(player)) {
                        if (guild.acceptInvite(player)) {
                            manager.sendKey(player, "inviteAccepted", guild.getDisplay());
                        } else manager.sendKey(player, "upMaxMembers");
                    } else manager.sendKey(player, "notInvited");
                } else manager.sendKey(player, "guildNotExist");
            } else if (team.equals(guild)) {
                guild.acceptInvite(player);
                manager.sendKey(player, "alreadyJoined");
            } else manager.sendKey(player, "inAnotherGuild");
        } else manager.sendKey(player, "emptyArgs");
    }

    @Sub(paths = {"reject", "invite"}, onlyPlayer = true)
    public static void reject_invite(SpigotCommand self, CommandSender sender, Paths args) {
        TeamManager manager = (TeamManager) self.manager;
        Player player = (Player) sender;
        if (args.notEmpty()) {
            TeamGuild guild = manager.getGuild(args.first());
            if (guild != null) {
                guild.unInvite(player.getUniqueId());
                manager.sendKey(player, "rejectInvite");
            } else manager.sendKey(player, "guildNotExist");
        } else manager.sendKey(player, "emptyArgs");
    }

    @Sub(onlyPlayer = true)
    public static void convoke(SpigotCommand self, CommandSender sender, Paths args) {
        TeamManager manager = (TeamManager) self.manager;
        Player player = (Player) sender;
        TeamGuild guild = manager.getGuild(player.getName());
        if (guild != null) {
            guild.convoke(args.first());
        } else manager.sendKey(player, "ownNoGuild");
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
        if (args.notEmpty()) {
            TeamGuild guild = manager.fetchTeam(player);
            if (guild != null) {
                if (guild.isManager(player)) {
                    String beKick = args.first();
                    if (!guild.isLeader(beKick)) {
                        if (guild.hasMember(beKick)) {
                            if (!guild.isLeader(player) && guild.isManager(beKick)) {
                                manager.sendKey(player, "cantKickAnotherManager");
                                return;
                            }
                            manager.leaveGuild(beKick, guild);
                            manager.sendKey(player, "kickSuccess", beKick);
                            Player mmp = Bukkit.getPlayer(beKick);
                            if (mmp != null) manager.sendKey(mmp, "beKicked", guild.getDisplay());
                        } else manager.sendKey(player, "noSuchMember", beKick);
                    } else manager.sendKey(player, "cantKickLeader");
                } else manager.sendKey(player, "notManager");
            } else manager.sendKey(player, "notInAnyTeam");
        } else manager.sendKey(player, "emptyArgs");
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
            String text = args.first();
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
            String text = args.first();
            try {
                if (text.getBytes("GB2312").length <= manager.maxDescription) {
                    guild.setDescription(text);
                    manager.sendKey(player, "setDescription", guild.getDisplay());
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
        if (args.notEmpty()) {
            TeamGuild guild = manager.getGuild(player.getName());
            if (guild != null) {
                String name = args.first();
                if (guild.hasMember(name)) {
                    if (guild.getManSize() < guild.getTeamLevel().mans) {
                        guild.setManager(name);
                        manager.saveGuild();
                        manager.sendKey(player, "setManager", name);
                    } else manager.sendKey(player, "upMaxManagers");
                } else manager.sendKey(player, "noSuchMember", name);
            } else manager.sendKey(player, "ownNoGuild");
        } else manager.sendKey(player, "emptyArgs");
    }

    @Sub(onlyPlayer = true)
    public static void unsetman(SpigotCommand self, CommandSender sender, Paths args) {
        TeamManager manager = (TeamManager) self.manager;
        Player player = (Player) sender;
        if (args.notEmpty()) {
            TeamGuild guild = manager.getGuild(player.getName());
            if (guild != null) {
                String name = args.first();
                if (guild.isManager(name)) {
                    if (guild.isLeader(name)) {
                        manager.sendKey(player, "cantUnsetLeader");
                    } else {
                        guild.unsetManager(name);
                        manager.saveGuild();
                        manager.sendKey(player, "unsetManager", name);
                    }
                } else manager.sendKey(player, "isNotManager", name);
            } else manager.sendKey(player, "ownNoGuild");
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
        manager.upgrade((Player) sender);
    }
}
