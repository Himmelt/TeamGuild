package org.soraworld.guild.config;

import org.bukkit.ChatColor;
import org.bukkit.plugin.Plugin;
import org.soraworld.guild.constant.Constant;
import org.soraworld.guild.core.TeamGuild;
import org.soraworld.guild.economy.EcoTool;
import org.soraworld.guild.economy.IEconomy;
import org.soraworld.guild.flans.Flans;
import org.soraworld.violet.config.IIConfig;

import javax.annotation.Nonnull;
import java.io.File;
import java.util.HashMap;
import java.util.Set;

public class Config extends IIConfig {

    private int createCost = 0;
    private int createSize = 5;

    private Flans flans;

    private final IEconomy iEconomy;
    private final HashMap<String, TeamGuild> teams = new HashMap<>();
    private final HashMap<String, TeamGuild> guilds = new HashMap<>();

    public Config(File path, Plugin plugin) {
        super(path, plugin);
        iEconomy = new EcoTool().getEconomy();
    }

    public TeamGuild getGuild(String leader) {
        return guilds.get(leader);
    }

    public TeamGuild getTeam(String player) {
        return teams.get(player);
    }

    protected void loadOptions() {
        getFlans();
        TeamGuild.setConfig(this);
    }

    protected void saveOptions() {

    }

    @Nonnull
    protected ChatColor defaultChatColor() {
        return ChatColor.BLUE;
    }

    @Nonnull
    protected String defaultChatHead() {
        return "[" + Constant.PLUGIN_NAME + "]";
    }

    public String defaultAdminPerm() {
        return Constant.PERM_ADMIN;
    }

    public void createGuild(@Nonnull String player) {
        TeamGuild guild = getTeam(player);
        if (guild != null) {
            System.out.println("你已在一个队伍中");
        } else {
            guild = new TeamGuild(player);
            if (iEconomy != null && iEconomy.takeEco(player, 1000)) {
                System.out.println("付款成功");
                teams.put(player, guild);
                guilds.put(player, guild);
            } else {
                System.out.println("资金不足或其他错误");
            }
        }
        save();
    }

    public boolean joinGuild(String player, TeamGuild guild) {
        return guild.addMember(player);
    }

    public void leaveGuild(String player, TeamGuild guild) {
        guild.delMember(player);
        teams.remove(player);
    }

    public Set<String> getGuilds() {
        return guilds.keySet();
    }

    public Flans getFlans() {
        if (flans == null) flans = new Flans(this);
        return flans;
    }

}
