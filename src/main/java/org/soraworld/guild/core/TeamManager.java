package org.soraworld.guild.core;

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

    public void createGuild(@Nonnull String player) {
        TeamGuild guild = getTeam(player);
        if (guild != null) {
            System.out.println("你已在一个队伍中");
        } else {
            guild = new TeamGuild(player, levels.first());
            if (config.getEconomy().takeEco(player, 1000)) {
                System.out.println("付款成功");
                teams.put(player, guild);
                guilds.put(player, guild);
            } else {
                System.out.println("资金不足或其他错误");
            }
        }
        config.save();
    }

    public boolean joinGuild(String player, TeamGuild guild) {
        return guild.addMember(player);
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
        defaultLevel();
    }

    public List<?> writeLevels() {
        defaultLevel();
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

    private void defaultLevel() {
        if (levels.isEmpty()) {
            levels.add(new TeamLevel(5, 50, false));
        }
    }

}
