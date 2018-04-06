package org.soraworld.guild.core;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.MemorySection;
import org.bukkit.entity.Player;
import org.soraworld.guild.config.Config;
import org.soraworld.violet.yaml.IYamlConfiguration;

import javax.annotation.Nonnull;
import java.io.File;
import java.util.*;

public class TeamManager {

    private final Config config;
    private final TreeSet<TeamLevel> levels = new TreeSet<>();
    private final HashMap<String, TeamGuild> teams = new HashMap<>();
    private final HashMap<String, TeamGuild> guilds = new HashMap<>();

    private final File guild_file;
    private final IYamlConfiguration guild_yaml = new IYamlConfiguration();

    public TeamManager(Config config, File path) {
        this.config = config;
        this.guild_file = new File(path, "guild.yml");
    }

    public void saveGuild() {
        try {
            for (Map.Entry<String, TeamGuild> entry : guilds.entrySet()) {
                ConfigurationSection sec = guild_yaml.createSection(entry.getKey());
                entry.getValue().write(sec);
            }
            guild_yaml.save(guild_file);
        } catch (Throwable e) {
            if (config.debug()) e.printStackTrace();
            config.console("&cGuild file save exception !!!");
        }
    }

    public void loadGuild() {
        if (!guild_file.exists()) {
            saveGuild();
            return;
        }
        try {
            guilds.clear();
            teams.clear();
            guild_yaml.load(guild_file);
            for (String key : guild_yaml.getKeys(false)) {
                Object obj = guild_yaml.get(key);
                if (obj instanceof MemorySection) {
                    guilds.put(key, new TeamGuild(key, (MemorySection) obj));
                }
            }
        } catch (Throwable e) {
            if (config.debug()) e.printStackTrace();
            config.console("&cGuild file load exception !!!");
        }
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
        guild = new TeamGuild(username, levels.first().size);
        if (config.getEconomy().takeEco(username, getLevel(guild).cost)) {
            teams.put(username, guild);
            guilds.put(username, guild);
            config.save();
            config.send(player, "createTeamSuccess", getLevel(guild).cost);
        } else {
            config.send(player, "createTeamFailed");
        }
    }

    private TeamLevel getLevel(int size) {
        for (TeamLevel level : levels) {
            if (size <= level.size) return level;
        }
        return levels.last();
    }

    public TeamLevel getLevel(TeamGuild guild) {
        for (TeamLevel level : levels) {
            if (guild.size() <= level.size) {
                guild.setSize(level.size);
                return level;
            }
        }
        guild.setSize(levels.last().size);
        return levels.last();
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

    public TeamGuild fetchTeam(String player) {
        TeamGuild team = teams.get(player);
        if (team == null) {
            for (TeamGuild guild : guilds.values()) {
                if (guild.hasMember(player)) {
                    team = guild;
                    teams.put(player, team);
                    break;
                }
            }
        }
        return team;
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

    public void upgrade(TeamGuild guild) {
        System.out.println("upgrade");
    }

}
