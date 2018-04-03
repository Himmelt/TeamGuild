package org.soraworld.guild.core;

import org.soraworld.guild.config.Config;

import javax.annotation.Nonnull;
import java.util.HashMap;
import java.util.HashSet;

public class TeamGuild {

    private int size = 5;
    private int balance = 0;
    private String display;
    private String description;
    private final String leader;
    private final HashSet<String> members = new HashSet<>();
    private final HashSet<String> managers = new HashSet<>();

    private static Config config;
    private static final HashMap<String, TeamGuild> teams = new HashMap<>();

    public TeamGuild(@Nonnull String leader) {
        this.leader = leader;
    }

    public static void setConfig(Config config) {
        TeamGuild.config = config;
    }

    public boolean isLeader(String player) {
        return leader.equals(player);
    }

    public void addManager(String player) {
        managers.add(player);
        teams.put(player, this);
    }

    public boolean hasManager(String player) {
        return managers.contains(player);
    }

    public void delManager(String player) {
        managers.remove(player);
        teams.remove(player);
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
        if (members.size() < this.size) {
            members.add(player);
            teams.put(player, this);
            return true;
        }
        return false;
    }

    public boolean hasMember(String player) {
        return members.contains(player);
    }

    public void delMember(String player) {
        members.remove(player);
        teams.remove(player);
    }

    public int getSize() {
        return size < 1 ? 1 : size;
    }

    public void setSize(int size) {
        this.size = size < 1 ? 1 : size;
    }

    public String getDescription() {
        return description == null ? "" : description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public static TeamGuild getTeam(String player) {
        return teams.get(player);
    }

    public static void setTeam(String player, TeamGuild team) {
        teams.put(player, team);
    }

    public static void clearPlayer(String player) {
        teams.remove(player);
    }

    public void upgrade() {

    }

}
