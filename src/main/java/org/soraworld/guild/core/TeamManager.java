package org.soraworld.guild.core;

import org.bukkit.entity.Player;
import org.soraworld.guild.config.Config;

import javax.annotation.Nonnull;
import java.util.*;

public class TeamManager {

    private final Config config;
    private final TreeSet<TeamLevel> levels = new TreeSet<>();
    private final HashMap<String, TeamGuild> teams = new HashMap<>();
    private final HashMap<String, TeamGuild> guilds = new HashMap<>();

    public TeamManager(Config config) {
        this.config = config;
    }

    public void clearPlayer(String player) {
        teams.remove(player);
    }

    public List<String> getGuilds() {
        return new ArrayList<>(guilds.keySet());
    }

    public void createGuild(@Nonnull Player player) {
        String username = player.getName();
        TeamGuild guild = teams.get(username);
        if (guild != null) {
            if (guild.isLeader(username)) config.send(player, "alreadyLeader");
            else config.send(player, "alreadyInTeam");
            return;
        }
        guild = new TeamGuild(username, levels.first());
        if (config.getEconomy().takeEco(username, guild.getLevel().cost)) {
            teams.put(username, guild);
            guilds.put(username, guild);
            config.save();
            config.send(player, "createTeamSuccess", guild.getLevel().cost);
        } else {
            config.send(player, "createTeamFailed");
        }
    }

    public void joinGuild(Player player, String leader) {
        String username = player.getName();
        TeamGuild guild = teams.get(username);
        if (guild != null) {
            config.send(player, "alreadyInTeam");
            return;
        }
        guild = guilds.get(leader);
        if (guild == null) {
            config.send(player, "guildNotExist");
        } else {
            if (guild.hasMember(username)) {
                config.send(player, "alreadyJoined");
            } else {
                guild.addJoinApplication(username);
                config.send(player, "sendApplication");
            }
        }
    }

    public void leaveGuild(String player, TeamGuild guild) {
        guild.delMember(player);
        teams.remove(player);
    }

    public TeamGuild getGuild(String leader) {
        return guilds.get(leader);
    }

    public TeamGuild getTeam(String player) {
        return teams.get(player);
    }

    public void readLevels(List<?> list) {
        levels.clear();
        if (list != null) {
            for (Object obj : list) {
                if (obj instanceof Map) {
                    Map map = (Map) obj;
                    Object size = map.get("size");
                    Object cost = map.get("cost");
                    Object guild = map.get("guild");
                    if (size instanceof Integer && cost instanceof Integer && guild instanceof Boolean) {
                        TeamLevel level = new TeamLevel((Integer) size, (Integer) cost, (Boolean) guild);
                        levels.add(level);
                    }
                }
            }
        }
        if (levels.isEmpty()) levels.add(new TeamLevel(5, 50, false));
    }

    public List<?> writeLevels() {
        if (levels.isEmpty()) levels.add(new TeamLevel(5, 50, false));
        List<Map> list = new ArrayList<>();
        for (TeamLevel level : levels) {
            Map<String, Object> sec = new LinkedHashMap<>();
            sec.put("size", level.size);
            sec.put("cost", level.cost);
            sec.put("guild", level.guild);
            list.add(sec);
        }
        return list;
    }

}
