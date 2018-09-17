package org.soraworld.guild.core;

import org.bukkit.Bukkit;
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

public class TeamGuild implements Comparable<TeamGuild> {

    private String leader;
    @Setting
    private int size;
    @Setting
    private int balance = 0;
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

    public TeamGuild(String leader, int size) {
        this.leader = leader;
        this.size = size;
    }

    public boolean isLeader(String player) {
        return leader.equals(player);
    }

    public void setManager(String player) {
        managers.add(player);
        members.remove(player);
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
        return leader;
    }

    @Nonnull
    public String getDisplay() {
        return display == null ? "" : display;
    }

    public void setDisplay(String display) {
        if (!display.endsWith("&r")) display += "&r";
        this.display = display;
    }

    public boolean addMember(String player) {
        if (members.size() + managers.size() < size) {
            members.add(player);
            return true;
        }
        return false;
    }

    public boolean hasMember(String player) {
        return isLeader(player) || hasManager(player) || members.contains(player);
    }

    public void delMember(String player) {
        managers.remove(player);
        members.remove(player);
    }

    public int getCount() {
        return members.size() + managers.size();
    }

    public String getDescription() {
        return description == null ? "" : description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void addJoinApplication(String username) {
        applications.add(username);
        Bukkit.getPluginManager().callEvent(new JoinApplicationEvent(leader, username));
    }

    public void notifyApplication(Player player, TeamManager config) {
        for (String applicant : applications) {
            handleApplication(player, applicant, config);
        }
    }

    public void handleApplication(Player handler, String applicant, TeamManager manager) {
        if (handler != null) {
            manager.sendHandleMessage(handler, applicant);
            return;
        }
        handler = Bukkit.getPlayer(leader);
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

    public int size() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public void notifyLeave(String username, final TeamManager config) {
        Player handler = Bukkit.getPlayer(leader);
        if (handler != null) {
            config.sendKey(handler, "notifyLeave", username);
        }
        for (String manager : managers) {
            handler = Bukkit.getPlayer(manager);
            if (handler != null) {
                config.sendKey(handler, "notifyLeave", username);
            }
        }
    }

    public void showMemberList(CommandSender sender, TeamManager config) {
        config.sendKey(sender, "listHead", display);
        config.sendKey(sender, "listLeader", leader);
        for (String manager : managers) {
            config.sendKey(sender, "listManager", manager);
        }
        for (String member : members) {
            config.sendKey(sender, "listMember", member);
        }
        config.send(sender, "listFoot");
    }

    public void showGuildInfo(CommandSender sender, TeamManager manager) {
        manager.sendKey(sender, "infoDisplay", display);
        manager.sendKey(sender, "infoLeader", leader);
        if (sender instanceof Player && hasMember(sender.getName()) || sender instanceof ConsoleCommandSender) {
            manager.sendKey(sender, "infoBalance", balance);
            manager.sendKey(sender, "maxManagers", manager.getLevel(this).mans);
        }
        manager.sendKey(sender, "infoMembers", getCount(), size);
        manager.sendKey(sender, "infoDescription", description);
    }

    @Override
    public int compareTo(@Nonnull TeamGuild other) {
        // Descending
        if (this.leader.equals(other.leader)) return 0;
        return this.size > other.size ? -1 : 1;
    }

    @Override
    public boolean equals(Object obj) {
        return this == obj || obj instanceof TeamGuild && this.leader.equals(((TeamGuild) obj).leader);
    }

    public int getManSize() {
        return managers.size();
    }

    public static TeamGuild deserialize(Node node, String leader) {
        if (node instanceof NodeMap) {
            TeamGuild guild = new TeamGuild(leader, 0);
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
}
