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
    private final HashMap<String, TeamGuild> guilds = new HashMap<>();

    public Config(File path, Plugin plugin) {
        super(path, plugin);
        iEconomy = new EcoTool().getEconomy();
    }

    public TeamGuild getTheGuild(String leader) {
        return guilds.get(leader);
    }

    public TeamGuild getGuild(String player) {
        TeamGuild team = guilds.get(player);
        if (team != null) return team;
        for (TeamGuild guild : guilds.values()) {
            if (guild.hasMember(player)) return guild;
        }
        return null;
    }

    protected void loadOptions() {
        getFlans();
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
        TeamGuild guild = getGuild(player);
        if (guild != null) {
            System.out.println("你已在一个队伍中");
        } else {
            guild = new TeamGuild(player);
            if (iEconomy != null && iEconomy.takeEco(player, 1000)) {
                System.out.println("付款成功");
                guilds.put(player, guild);
            } else {
                System.out.println("资金不足或其他错误");
            }
        }
        save();
    }

    public Set<String> getGuilds() {
        return guilds.keySet();
    }

    public Flans getFlans() {
        if (flans == null) flans = new Flans(this);
        return flans;
    }

}
