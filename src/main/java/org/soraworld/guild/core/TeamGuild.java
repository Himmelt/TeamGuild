package org.soraworld.guild.core;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.soraworld.guild.event.JoinApplicationEvent;
import org.soraworld.guild.manager.TeamManager;
import org.soraworld.hocon.node.Node;
import org.soraworld.hocon.node.NodeMap;
import org.soraworld.hocon.node.Options;
import org.soraworld.hocon.node.Setting;

import javax.annotation.Nonnull;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.UUID;

import static org.soraworld.violet.util.ChatColor.COLOR_CHAR;
import static org.soraworld.violet.util.ChatColor.RESET;

public class TeamGuild implements Comparable<TeamGuild> {

    @Setting
    private int level;
    @Setting
    private int frame = 0;
    @Setting
    // TODO balance
    private int balance = 0;
    @Setting
    private boolean showTopJoin = true;
    @Setting
    private String display;
    @Setting
    private String description;
    @Setting
    private HashSet<String> members = new HashSet<>();
    @Setting
    private HashSet<String> managers = new HashSet<>();
    @Setting
    private LinkedHashSet<String> applications = new LinkedHashSet<>();

    private UUID attorn;
    private OfflinePlayer leader;
    private final TeamManager manager;
    private final HashSet<UUID> invites = new HashSet<>();

    public TeamGuild(OfflinePlayer leader, int level, TeamManager manager) {
        this.leader = leader;
        this.level = level;
        this.manager = manager;
    }

    public boolean isLeader(Player player) {
        return player.getUniqueId().equals(leader.getUniqueId());
    }

    public boolean isLeader(String player) {
        return player.equals(leader.getName());
    }

    public void setManager(String player) {
        managers.add(player);
        members.remove(player);
    }

    public boolean isManager(Player player) {
        return isLeader(player) || managers.contains(player.getName());
    }

    public boolean isManager(String player) {
        return isLeader(player) || managers.contains(player);
    }

    public void unsetManager(String player) {
        members.add(player);
        managers.remove(player);
    }

    public Player getLeader() {
        return leader.getPlayer();
    }

    @Nonnull
    public String getTeamLeader() {
        return leader.getName();
    }

    @Nonnull
    public String getDisplay() {
        return display == null ? "" : display.replace('&', ChatColor.COLOR_CHAR);
    }

    public void setDisplay(String display) {
        if (!display.endsWith("&r")) display += "&r";
        this.display = display;
    }

    public boolean addMember(String player) {
        if (members.size() + managers.size() < manager.getLevel(level).size) {
            members.add(player);
            return true;
        }
        return false;
    }

    public boolean hasMember(String player) {
        return isLeader(player) || isManager(player) || members.contains(player);
    }

    public boolean hasMember(Player player) {
        return isLeader(player) || isManager(player) || members.contains(player.getName());
    }

    public void delMember(String player) {
        managers.remove(player);
        members.remove(player);
    }

    public int getCount() {
        return members.size() + managers.size();
    }

    public String getDescription() {
        return description == null ? "" : description.replace('&', COLOR_CHAR);
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void addJoinApplication(String username) {
        applications.add(username);
        Bukkit.getPluginManager().callEvent(new JoinApplicationEvent(leader.getName(), username));
    }

    public void notifyApplication(Player player) {
        for (String applicant : applications) {
            handleApplication(player, applicant);
        }
    }

    public void handleApplication(Player handler, String applicant) {
        if (handler != null) {
            manager.sendHandleMessage(handler, applicant);
            return;
        }
        handler = leader.getPlayer();
        if (handler != null) {
            manager.sendHandleMessage(handler, applicant);
        }
        for (String man : managers) {
            handler = Bukkit.getPlayer(man);
            if (handler != null) {
                manager.sendHandleMessage(handler, applicant);
            }
        }
    }

    public boolean hasApplication(String applicant) {
        return applications.contains(applicant);
    }

    public void closeApplication(String applicant) {
        applications.remove(applicant);
    }

    public TeamLevel getTeamLevel() {
        return manager.getLevel(level);
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public void notifyLeave(String username) {
        Player handler = leader.getPlayer();
        if (handler != null) {
            manager.sendKey(handler, "notifyLeave", username);
        }
        for (String man : managers) {
            handler = Bukkit.getPlayer(man);
            if (handler != null) {
                manager.sendKey(handler, "notifyLeave", username);
            }
        }
    }

    public void showMemberList(CommandSender sender) {
        manager.sendKey(sender, "listHead", display);
        manager.sendKey(sender, "listLeader", leader);
        for (String man : managers) {
            manager.sendKey(sender, "listManager", man);
        }
        for (String member : members) {
            manager.sendKey(sender, "listMember", member);
        }
        manager.send(sender, "listFoot");
    }

    public void showGuildInfo(CommandSender sender) {
        manager.sendKey(sender, "infoDisplay", display);
        manager.sendKey(sender, "infoLeader", leader.getName());
        if (sender instanceof Player && hasMember(sender.getName()) || sender instanceof ConsoleCommandSender) {
            manager.sendKey(sender, "infoBalance", balance);
            manager.sendKey(sender, "maxManagers", manager.getLevel(level).mans);
        }
        manager.sendKey(sender, "infoMembers", getCount(), level);
        manager.sendKey(sender, "infoDescription", description);
    }

    public int compareTo(@Nonnull TeamGuild other) {
        if (other.frame == this.frame) return other.level - this.level;
        return other.frame - this.frame;
    }

    public boolean equals(Object obj) {
        return this == obj || obj instanceof TeamGuild && this.leader.equals(((TeamGuild) obj).leader);
    }

    public int getManSize() {
        return managers.size();
    }

    public static TeamGuild deserialize(Node node, String leader, TeamManager manager) {
        if (node instanceof NodeMap) {
            TeamGuild guild = new TeamGuild(Bukkit.getOfflinePlayer(leader), 0, manager);
            ((NodeMap) node).modify(guild);
            if (!guild.display.endsWith("&r") && !guild.display.endsWith(RESET.toString())) guild.display += "&r";
            return guild;
        }
        return null;
    }

    public static NodeMap serialize(TeamGuild guild, Options options) {
        NodeMap node = new NodeMap(options);
        if (guild != null) node.extract(guild);
        return node;
    }

    public void addFrame(int frame) {
        this.frame += frame;
    }

    public boolean isShowTopJoin() {
        return showTopJoin;
    }

    public void setShowTopJoin(boolean show) {
        this.showTopJoin = show;
    }

    public int getFrame() {
        return frame;
    }

    public void sendAttorn(Player target) {
        this.attorn = target.getUniqueId();
        manager.sendAttornMessage(target, this);
    }

    public boolean isAttorn(Player player) {
        return attorn != null && player.getUniqueId().equals(attorn);
    }

    public void setLeader(Player player) {
        this.leader = player;
        managers.remove(player.getName());
        members.remove(player.getName());
    }

    public void resetAttorn() {
        this.attorn = null;
    }

    public boolean rejectAttorn(Player player) {
        if (isAttorn(player)) {
            attorn = null;
            return true;
        }
        return false;
    }

    public void sendInvite(Player man, Player target) {
        invites.add(target.getUniqueId());
        manager.sendInviteMessage(man, target, this);
    }

    public boolean acceptInvite(Player player) {
        if (invites.contains(player.getUniqueId())) {
            invites.remove(player.getUniqueId());
            return addMember(player.getName());
        } else return false;
    }

    public boolean isInvited(Player player) {
        return invites.contains(player.getUniqueId());
    }

    public void unInvite(UUID uuid) {
        invites.remove(uuid);
    }

    public void unInviteAll() {
        invites.clear();
    }

    public String getHover() {
        return manager.trans("hover.display", getDisplay()) + '\n' + manager.trans("hover.level", level) + '\n' +
                manager.trans("hover.frame", frame) + '\n' +
                manager.trans("hover.balance", balance) + '\n' +
                manager.trans("hover.members", members.size(), getTeamLevel().size) + '\n' +
                manager.trans("hover.managers", managers.size(), getTeamLevel().mans) + '\n' +
                manager.trans("hover.description", description);
    }
}
