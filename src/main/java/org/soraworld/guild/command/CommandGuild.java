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
import org.soraworld.violet.command.Args;
import org.soraworld.violet.command.SpigotCommand;
import org.soraworld.violet.command.Sub;

import java.util.UUID;

import static org.soraworld.guild.TeamGuild.residenceApi;

public final class CommandGuild {
    @Sub
    public static void top(SpigotCommand self, CommandSender sender, Args args) {
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
    public static void info(SpigotCommand self, CommandSender sender, Args args) {
        TeamManager manager = (TeamManager) self.manager;
        if (args.empty()) {
            if (sender instanceof Player) {
                TeamGuild guild = manager.fetchTeam((Player) sender);
                if (guild != null) sender.sendMessage(guild.getHover());
                else manager.sendKey(sender, "player.notInAny");
            } else manager.sendKey(sender, "invalidArgs");
        } else {
            TeamGuild guild = manager.fetchTeam(args.first());
            if (guild != null) sender.sendMessage(guild.getHover());
            else manager.sendKey(sender, "guild.notExist");
        }
    }

    @Sub
    public static void disband(SpigotCommand self, CommandSender sender, Args args) {
        TeamManager manager = (TeamManager) self.manager;
        if (args.empty()) {
            if (sender instanceof Player) {
                TeamGuild guild = manager.getGuild(sender.getName());
                if (guild != null) {
                    manager.disband(guild);
                    manager.sendKey(sender, "guild.disband", guild.getDisplay());
                } else manager.sendKey(sender, "player.ownNone");
            } else manager.sendKey(sender, "emptyArgs");
        } else {
            TeamGuild guild = manager.getGuild(args.first());
            if (guild != null) {
                if (sender.hasPermission(manager.defAdminPerm()) || guild.isLeader(sender.getName())) {
                    manager.disband(guild);
                    manager.sendKey(sender, "guild.disband", guild.getDisplay());
                } else manager.sendKey(sender, "player.notLeaderAdmin");
            } else manager.sendKey(sender, "guild.notExist");
        }
    }

    @Sub(path = "eco.give", perm = "admin")
    public static void eco_give(SpigotCommand self, CommandSender sender, Args args) {
        TeamManager manager = (TeamManager) self.manager;
        if (args.notEmpty()) {
            if (args.size() == 2) {
                TeamGuild guild = manager.getGuild(args.first());
                if (guild != null) {
                    try {
                        float amount = Float.valueOf(args.get(1));
                        manager.updateGuild(guild, g -> g.giveEco(amount));
                        manager.sendKey(sender, "guild.eco.give", guild.getDisplay(), amount);
                    } catch (Throwable ignored) {
                        manager.sendKey(sender, "invalidFloat");
                    }
                } else manager.sendKey(sender, "guild.notExist");
            } else manager.sendKey(sender, "invalidArgs");
        } else manager.sendKey(sender, "emptyArgs");
    }

    @Sub(path = "eco.take", perm = "admin")
    public static void eco_take(SpigotCommand self, CommandSender sender, Args args) {
        TeamManager manager = (TeamManager) self.manager;
        if (args.notEmpty()) {
            if (args.size() == 2) {
                TeamGuild guild = manager.getGuild(args.first());
                if (guild != null) {
                    try {
                        float amount = Float.valueOf(args.get(1));
                        if (guild.hasEco(amount)) {
                            manager.updateGuild(guild, g -> g.takeEco(-1 * amount));
                            manager.sendKey(sender, "guild.eco.take", guild.getDisplay(), amount);
                        } else manager.sendKey(sender, "guild.eco.none", guild.getDisplay(), guild.getEco());
                    } catch (Throwable ignored) {
                        manager.sendKey(sender, "invalidFloat");
                    }
                } else manager.sendKey(sender, "guild.notExist");
            } else manager.sendKey(sender, "invalidArgs");
        } else manager.sendKey(sender, "emptyArgs");
    }

    @Sub(path = "frame.give", perm = "admin")
    public static void frame_give(SpigotCommand self, CommandSender sender, Args args) {
        TeamManager manager = (TeamManager) self.manager;
        if (args.notEmpty()) {
            if (args.size() == 2) {
                TeamGuild guild = manager.getGuild(args.first());
                if (guild != null) {
                    try {
                        int frame = Integer.valueOf(args.get(1));
                        manager.updateGuild(guild, g -> g.giveFrame(frame));
                        manager.sendKey(sender, "guild.frame.give", guild.getDisplay(), frame);
                    } catch (Throwable ignored) {
                        manager.sendKey(sender, "invalidInt");
                    }
                } else manager.sendKey(sender, "guild.notExist");
            } else manager.sendKey(sender, "invalidArgs");
        } else manager.sendKey(sender, "emptyArgs");
    }

    @Sub(path = "frame.take", perm = "admin")
    public static void frame_take(SpigotCommand self, CommandSender sender, Args args) {
        TeamManager manager = (TeamManager) self.manager;
        if (args.notEmpty()) {
            if (args.size() == 2) {
                TeamGuild guild = manager.getGuild(args.first());
                if (guild != null) {
                    try {
                        int frame = Integer.valueOf(args.get(1));
                        manager.updateGuild(guild, g -> g.giveFrame(-1 * frame));
                        manager.sendKey(sender, "guild.frame.take", guild.getDisplay(), frame);
                    } catch (Throwable ignored) {
                        manager.sendKey(sender, "invalidInt");
                    }
                } else manager.sendKey(sender, "guild.notExist");
            } else manager.sendKey(sender, "invalidArgs");
        } else manager.sendKey(sender, "emptyArgs");
    }

    @Sub(path = "topjoin", onlyPlayer = true)
    public static void showTopJoin(SpigotCommand self, CommandSender sender, Args args) {
        if (args.notEmpty()) {
            TeamManager manager = (TeamManager) self.manager;
            Player player = (Player) sender;
            TeamGuild guild = manager.getGuild(player.getName());
            if (guild != null) {
                guild.setShowTopJoin(Boolean.valueOf(args.first()));
                manager.sendKey(player, guild.isShowTopJoin() ? "showRankJoin" : "notShowRankJoin");
            } else manager.sendKey(player, "guild.notExist");
        } else self.manager.sendKey(sender, "emptyArgs");
    }

    @Sub(onlyPlayer = true)
    public static void create(SpigotCommand self, CommandSender sender, Args args) {
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
    public static void donate(SpigotCommand self, CommandSender sender, Args args) {
        TeamManager manager = (TeamManager) self.manager;
        Player player = (Player) sender;
        if (args.notEmpty()) {
            TeamGuild guild = manager.fetchTeam(player);
            if (guild != null) {
                try {
                    int amount = Integer.valueOf(args.first());
                    if (Economy.hasEnough(player, amount)) {
                        Economy.takeEco(player, amount);
                        guild.giveEco(amount);
                        manager.sendKey(player, "guild.donate", amount);
                    } else manager.sendKey(player, "player.ecoNotEnough");
                } catch (Throwable e) {
                    manager.sendKey(player, "invalidInt");
                }
            } else manager.sendKey(player, "player.notInAny");
        } else manager.sendKey(player, "emptyArgs");
    }

    @Sub
    public static void chat() {
        // [Display][Name] meaasge
        // TODO test when args contains space or with "xxx     sdsd  x"
    }

    @Sub(onlyPlayer = true)
    public static void home(SpigotCommand self, CommandSender sender, Args args) {
        TeamManager manager = (TeamManager) self.manager;
        Player player = (Player) sender;
        if (residenceApi) {
            TeamGuild guild = manager.fetchTeam(player);
            if (guild != null) {
                ClaimedResidence res = Residence.getInstance().getResidenceManager().getByName(guild.getHomeName());
                if (res != null) {
                    Location loc = res.getTeleportLocation();
                    if (loc != null && player.teleport(loc)) manager.sendKey(player, "tpGuildSuccess");
                    else manager.sendKey(player, "home.tpFailed");
                } else manager.sendKey(player, "home.notExist");
            } else manager.sendKey(player, "player.notInAny");
        } else manager.sendKey(player, "residence.notHook");
    }

    @Sub(onlyPlayer = true)
    public static void sethome(SpigotCommand self, CommandSender sender, Args args) {
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
                        if (guild.hasEco(amount)) {
                            String serverOwner = Residence.getInstance().getServerLandname();
                            String serverUUID = Residence.getInstance().getServerLandUUID();
                            if (apiR.addResidence(player, serverOwner, guild.getHomeName(), apiS.getPlayerLoc1(player), apiS.getPlayerLoc2(player), true)) {
                                res = apiR.getByName(guild.getHomeName());
                                res.getPermissions().setOwnerUUID(UUID.fromString(serverUUID));
                                //res.getPermissions().setOwner(serverOwner, false);
                                res.getPermissions().setPlayerFlag(player.getName(), "admin", FlagPermissions.FlagState.TRUE);
                                guild.takeEco(amount);
                                manager.sendKey(player, "home.created");
                            } else manager.sendKey(player, "home.createFailed");
                        } else manager.sendKey(player, "home.ecoNone");
                    } else manager.sendKey(player, "home.selectFirst");
                } else manager.sendKey(player, "home.alreadyExist");
            } else manager.sendKey(player, "player.ownNone");
        } else manager.sendKey(player, "residence.notHook");
    }

    @Sub(onlyPlayer = true)
    public static void delhome(SpigotCommand self, CommandSender sender, Args args) {
        TeamManager manager = (TeamManager) self.manager;
        Player player = (Player) sender;
        if (residenceApi) {
            TeamGuild guild = manager.fetchTeam(player);
            if (guild != null) {
                ResidenceManager apiR = Residence.getInstance().getResidenceManager();
                ClaimedResidence res = apiR.getByName(guild.getHomeName());
                if (res != null) {
                    apiR.removeResidence(res);
                    manager.sendKey(player, "home.remove");
                } else manager.sendKey(player, "home.notExist");
            } else manager.sendKey(player, "player.notInAny");
        } else manager.sendKey(player, "residence.notHook");
    }

    @Sub(onlyPlayer = true)
    public static void join(SpigotCommand self, CommandSender sender, Args args) {
        TeamManager manager = (TeamManager) self.manager;
        Player player = (Player) sender;
        if (args.empty()) manager.sendKey(player, "emptyArgs");
        else manager.joinGuild(player, args.first());
    }

    @Sub(path = "accept.join", onlyPlayer = true)
    public static void accept_join(SpigotCommand self, CommandSender sender, Args args) {
        TeamManager manager = (TeamManager) self.manager;
        Player player = (Player) sender;
        if (args.empty()) {
            manager.sendKey(player, "emptyArgs");
            return;
        }
        TeamGuild guild = manager.fetchTeam(player);
        if (guild == null) {
            manager.sendKey(player, "player.notInAny");
            return;
        }
        if (guild.isManager(player)) {
            String applicant = args.first();
            TeamGuild team = manager.fetchTeam(applicant);
            if (team != null) {
                manager.sendKey(player, "application.alreadyIn", applicant, team.getDisplay());
                guild.closeApplication(applicant);
                manager.saveGuild();
                return;
            }
            if (guild.hasApplication(applicant)) {
                if (guild.addMember(applicant)) {
                    manager.sendKey(player, "application.accept", applicant);
                    Player app = Bukkit.getPlayer(applicant);
                    if (app != null) {
                        manager.sendKey(app, "application.accepted", player.getName(), guild.getDisplay());
                    }
                } else {
                    manager.sendKey(player, "application.acceptMax");
                    Player app = Bukkit.getPlayer(applicant);
                    if (app != null) {
                        manager.sendKey(app, "application.acceptedMax", player.getName(), guild.getDisplay());
                    }
                }
                guild.closeApplication(applicant);
                manager.saveGuild();
            } else manager.sendKey(player, "application.none", applicant);
        } else manager.sendKey(player, "manager.notManager");
    }

    @Sub(path = "reject.join", onlyPlayer = true)
    public static void reject_join(SpigotCommand self, CommandSender sender, Args args) {
        TeamManager manager = (TeamManager) self.manager;
        Player player = (Player) sender;
        if (args.empty()) {
            manager.sendKey(player, "emptyArgs");
            return;
        }
        TeamGuild guild = manager.fetchTeam(player);
        if (guild == null) {
            manager.sendKey(player, "player.notInAny");
            return;
        }
        if (guild.isManager(player)) {
            String applicant = args.first();
            if (guild.hasApplication(applicant)) {
                manager.sendKey(player, "application.reject", applicant);
                guild.closeApplication(applicant);
                manager.saveGuild();
                Player app = Bukkit.getPlayer(applicant);
                if (app != null) manager.sendKey(app, "application.rejected");
            } else manager.sendKey(player, "application.none", applicant);
        } else manager.sendKey(player, "manager.notManager");
    }

    @Sub(onlyPlayer = true)
    public static void attorn(SpigotCommand self, CommandSender sender, Args args) {
        TeamManager manager = (TeamManager) self.manager;
        Player player = (Player) sender;
        if (args.notEmpty()) {
            TeamGuild guild = manager.getGuild(player.getName());
            if (guild != null) {
                Player target = Bukkit.getPlayer(args.first());
                if (target != null) {
                    if (!player.equals(target)) {
                        guild.sendAttorn(target);
                        manager.sendKey(player, "attorn.send", target.getName());
                    } else manager.sendKey(player, "attorn.notSelf");
                } else manager.sendKey(player, "playerIsOffline", args.first());
            } else manager.sendKey(player, "player.ownNone");
        } else manager.sendKey(player, "emptyArgs");
    }

    @Sub(onlyPlayer = true)
    public static void unattorn(SpigotCommand self, CommandSender sender, Args args) {
        TeamManager manager = (TeamManager) self.manager;
        Player player = (Player) sender;
        TeamGuild guild = manager.getGuild(player.getName());
        if (guild != null) {
            guild.resetAttorn();
            manager.sendKey(player, "attorn.cancel");
        } else manager.sendKey(player, "player.ownNone");
    }

    @Sub(path = "accept.attorn", onlyPlayer = true)
    public static void accept_attorn(SpigotCommand self, CommandSender sender, Args args) {
        TeamManager manager = (TeamManager) self.manager;
        Player player = (Player) sender;
        if (args.notEmpty()) {
            TeamGuild team = manager.fetchTeam(player);
            TeamGuild guild = manager.getGuild(args.first());
            if (team == null || team.equals(guild)) {
                if (guild != null) {
                    Player oldLeader = guild.getLeader();
                    if (manager.attornTo(guild, player)) {
                        manager.sendKey(player, "attorn.accept", guild.getDisplay());
                        if (oldLeader != null) manager.sendKey(oldLeader, "attorn.acceptBy", player.getName());
                    } else manager.sendKey(player, "attorn.notAttorn", guild.getDisplay());
                } else manager.sendKey(player, "guild.notExist");
            } else manager.sendKey(player, "player.inAnother");
        } else manager.sendKey(player, "emptyArgs");
    }

    @Sub(path = "reject.attorn", onlyPlayer = true)
    public static void reject_attorn(SpigotCommand self, CommandSender sender, Args args) {
        TeamManager manager = (TeamManager) self.manager;
        Player player = (Player) sender;
        if (args.notEmpty()) {
            TeamGuild guild = manager.getGuild(args.first());
            if (guild != null) {
                if (guild.rejectAttorn(player)) {
                    manager.sendKey(player, "attorn.reject", guild.getDisplay());
                    Player oldLeader = guild.getLeader();
                    if (oldLeader != null) manager.sendKey(oldLeader, "attorn.rejectBy", player.getName());
                } else manager.sendKey(player, "attorn.notAttorn", guild.getDisplay());
            } else manager.sendKey(player, "guild.notExist");
        } else manager.sendKey(player, "emptyArgs");
    }

    @Sub(onlyPlayer = true)
    public static void invite(SpigotCommand self, CommandSender sender, Args args) {
        TeamManager manager = (TeamManager) self.manager;
        Player player = (Player) sender;
        if (args.notEmpty()) {
            TeamGuild guild = manager.fetchTeam(player);
            if (guild != null) {
                if (guild.isManager(player)) {
                    Player target = Bukkit.getPlayer(args.first());
                    if (target != null) {
                        guild.sendInvite(player, target);
                        manager.sendKey(player, "invite.send");
                    } else manager.sendKey(player, "playerIsOffline", args.first());
                } else manager.sendKey(player, "manager.notManager");
            } else manager.sendKey(player, "guild.notExist");
        } else manager.sendKey(player, "emptyArgs");
    }

    @Sub(onlyPlayer = true)
    public static void uninvite(SpigotCommand self, CommandSender sender, Args args) {
        TeamManager manager = (TeamManager) self.manager;
        Player player = (Player) sender;
        if (args.notEmpty()) {
            TeamGuild guild = manager.fetchTeam(player);
            if (guild != null) {
                if (guild.isManager(player)) {
                    if (args.first().equals("@ALL") && guild.isLeader(player)) {
                        guild.unInviteAll();
                        manager.sendKey(player, "invite.cancelAll");
                        return;
                    }
                    UUID target = Bukkit.getOfflinePlayer(args.first()).getUniqueId();
                    guild.unInvite(target);
                    manager.sendKey(player, "invite.cancel", args.first());
                } else manager.sendKey(player, "manager.notManager");
            } else manager.sendKey(player, "guild.notExist");
        } else manager.sendKey(player, "emptyArgs");
    }

    @Sub(path = "accept.invite", onlyPlayer = true)
    public static void accept_invite(SpigotCommand self, CommandSender sender, Args args) {
        TeamManager manager = (TeamManager) self.manager;
        Player player = (Player) sender;
        if (args.notEmpty()) {
            TeamGuild team = manager.fetchTeam(player);
            TeamGuild guild = manager.getGuild(args.first());
            if (team == null) {
                if (guild != null) {
                    if (guild.isInvited(player)) {
                        if (guild.acceptInvite(player)) {
                            manager.sendKey(player, "invite.accept", guild.getDisplay());
                        } else manager.sendKey(player, "invite.max");
                    } else manager.sendKey(player, "invite.notInvited");
                } else manager.sendKey(player, "guild.notExist");
            } else if (team.equals(guild)) {
                guild.acceptInvite(player);
                manager.sendKey(player, "player.alreadyJoined");
            } else manager.sendKey(player, "player.inAnother");
        } else manager.sendKey(player, "emptyArgs");
    }

    @Sub(path = "reject.invite", onlyPlayer = true)
    public static void reject_invite(SpigotCommand self, CommandSender sender, Args args) {
        TeamManager manager = (TeamManager) self.manager;
        Player player = (Player) sender;
        if (args.notEmpty()) {
            TeamGuild guild = manager.getGuild(args.first());
            if (guild != null) {
                guild.unInvite(player.getUniqueId());
                manager.sendKey(player, "invite.reject");
            } else manager.sendKey(player, "guild.notExist");
        } else manager.sendKey(player, "emptyArgs");
    }

    @Sub(onlyPlayer = true)
    public static void convoke(SpigotCommand self, CommandSender sender, Args args) {
        TeamManager manager = (TeamManager) self.manager;
        Player player = (Player) sender;
        TeamGuild guild = manager.getGuild(player.getName());
        if (guild != null) {
            guild.convoke(args.first());
        } else manager.sendKey(player, "player.ownNone");
    }

    @Sub(onlyPlayer = true)
    public static void leave(SpigotCommand self, CommandSender sender, Args args) {
        TeamManager manager = (TeamManager) self.manager;
        Player player = (Player) sender;
        String username = player.getName();
        TeamGuild guild = manager.fetchTeam(player);
        if (guild != null) {
            if (guild.isLeader(username)) {
                manager.sendKey(player, "leader.cantLeave");
            } else {
                manager.leaveGuild(player, guild);
                manager.sendKey(player, "member.leave", guild.getDisplay());
                guild.notifyLeave(username);
            }
        } else manager.sendKey(player, "player.notInAny");
    }

    @Sub(onlyPlayer = true)
    public static void kick(SpigotCommand self, CommandSender sender, Args args) {
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
                                manager.sendKey(player, "manager.cantKickManager");
                                return;
                            }
                            manager.leaveGuild(beKick, guild);
                            manager.sendKey(player, "manager.kickSuccess", beKick);
                            Player mmp = Bukkit.getPlayer(beKick);
                            if (mmp != null) manager.sendKey(mmp, "member.beKicked", guild.getDisplay());
                        } else manager.sendKey(player, "manager.noMember", beKick);
                    } else manager.sendKey(player, "manager.cantKickLeader");
                } else manager.sendKey(player, "manager.notManager");
            } else manager.sendKey(player, "player.notInAny");
        } else manager.sendKey(player, "emptyArgs");
    }

    @Sub(onlyPlayer = true)
    public static void display(SpigotCommand self, CommandSender sender, Args args) {
        TeamManager manager = (TeamManager) self.manager;
        Player player = (Player) sender;
        TeamGuild guild = manager.getGuild(player.getName());
        if (guild == null) {
            manager.sendKey(player, "player.ownNone");
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
    public static void describe(SpigotCommand self, CommandSender sender, Args args) {
        TeamManager manager = (TeamManager) self.manager;
        Player player = (Player) sender;
        TeamGuild guild = manager.getGuild(player.getName());
        if (guild == null) {
            manager.sendKey(player, "player.ownNone");
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
    public static void setman(SpigotCommand self, CommandSender sender, Args args) {
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
                        manager.sendKey(player, "guild.setMan", name);
                    } else manager.sendKey(player, "guild.maxMan");
                } else manager.sendKey(player, "guild.noMember", name);
            } else manager.sendKey(player, "player.ownNone");
        } else manager.sendKey(player, "emptyArgs");
    }

    @Sub(onlyPlayer = true)
    public static void unsetman(SpigotCommand self, CommandSender sender, Args args) {
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
                        manager.sendKey(player, "guild.unsetMan", name);
                    }
                } else manager.sendKey(player, "guild.isNotManager", name);
            } else manager.sendKey(player, "player.ownNone");
        } else manager.sendKey(player, "emptyArgs");
    }

    @Sub(onlyPlayer = true)
    public static void list(SpigotCommand self, CommandSender sender, Args args) {
        TeamManager manager = (TeamManager) self.manager;
        Player player = (Player) sender;
        TeamGuild team = manager.fetchTeam(player);
        if (team != null) {
            team.showMemberList(player);
        } else manager.sendKey(player, "player.notInAny");
    }

    @Sub(onlyPlayer = true)
    public static void upgrade(SpigotCommand self, CommandSender sender, Args args) {
        TeamManager manager = (TeamManager) self.manager;
        manager.upgrade((Player) sender);
    }
}
