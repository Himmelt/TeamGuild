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

import static org.soraworld.violet.util.ChatColor.COLOR_CHAR;

public class TeamGuild implements Comparable<TeamGuild> {

    private OfflinePlayer leader;
    @Setting
    private int frame = 0;
    @Setting
    private int level;
    @Setting
    private int balance = 0;
    @Setting
    private boolean showRankJoin = true;
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

    private final TeamManager manager;

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

    public boolean hasManager(Player player) {
        return isLeader(player) || managers.contains(player.getName());
    }

    public boolean hasManager(String player) {
        return isLeader(player) || managers.contains(player);
    }

    public void unsetManager(String player) {
        members.add(player);
        managers.remove(player);
    }

    @Nonnull
    public String getLeader() {
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
        if (members.size() + managers.size() < level) {
            members.add(player);
            return true;
        }
        return false;
    }

    public boolean hasMember(String player) {
        return isLeader(player) || hasManager(player) || members.contains(player);
    }

    public boolean hasMember(Player player) {
        return isLeader(player) || hasManager(player) || members.contains(player.getName());
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
        manager.sendKey(sender, "infoLeader", leader);
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
            if (!guild.display.endsWith("&r")) guild.display += "&r";
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

    public boolean isShowRankJoin() {
        return showRankJoin;
    }

    public void setShowRankJoin(boolean show) {
        this.showRankJoin = show;
    }

    public int getFrame() {
        return frame;
    }
}
