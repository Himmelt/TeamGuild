package org.soraworld.guild.core;

import org.bukkit.command.CommandSender;
import org.soraworld.guild.config.Config;

import javax.annotation.Nonnull;
import java.util.HashSet;
import java.util.LinkedHashSet;

public class TeamGuild {

    private int balance = 0;
    private String display;
    private String description;
    private TeamLevel level;
    private final String leader;
    private final HashSet<String> members = new HashSet<>();
    private final HashSet<String> managers = new HashSet<>();
    private final LinkedHashSet<String> applications = new LinkedHashSet<>();

    public TeamGuild(@Nonnull String leader, @Nonnull TeamLevel level) {
        this.leader = leader;
        this.level = level;
    }

    public boolean isLeader(String player) {
        return leader.equals(player);
    }

    public void addManager(String player) {
        managers.add(player);
    }

    public boolean hasManager(String player) {
        return isLeader(player) || managers.contains(player);
    }

    public void delManager(String player) {
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
        this.display = display;
    }

    public boolean addMember(String player) {
        if (members.size() + managers.size() < level.size) {
            members.add(player);
            return true;
        }
        return false;
    }

    public boolean hasMember(String player) {
        return isLeader(player) || hasManager(player) || members.contains(player);
    }

    public void delMember(String player) {
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

    public void showMemberList(CommandSender sender, Config config) {
        config.send(sender, "listHead");
        config.send(sender, "listLeader", leader);
        for (String manager : managers) {
            config.send(sender, "listManager", manager);
        }
        for (String member : members) {
            config.send(sender, "listMember", member);
        }
        config.send(sender, "listFoot");
    }

    public TeamLevel getLevel() {
        return level;
    }

    public void addJoinApplication(String username) {
        applications.add(username);
    }

}
