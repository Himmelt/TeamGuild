package org.soraworld.guild.config;

import org.bukkit.ChatColor;
import org.bukkit.plugin.Plugin;
import org.soraworld.guild.constant.Constant;
import org.soraworld.guild.core.TeamManager;
import org.soraworld.guild.economy.Economy;
import org.soraworld.guild.flans.Flans;
import org.soraworld.violet.config.IIConfig;

import javax.annotation.Nonnull;
import java.io.File;

public class Config extends IIConfig {

    private String ecoType = "Vault";
    private boolean teamPvP = false;
    private int maxDisplay = 10;
    private int maxDescription = 100;
    private TeamManager teamManager;

    public Config(File path, Plugin plugin) {
        super(path, plugin);
    }

    protected void loadOptions() {
        ecoType = config_yaml.getString("ecoType", "Vault");
        if (ecoType == null || ecoType.isEmpty()) ecoType = "Vault";
        teamPvP = config_yaml.getBoolean("teamPvP", false);
        maxDisplay = config_yaml.getInt("maxDisplay", 10);
        maxDescription = config_yaml.getInt("maxDescription", 100);
        getTeamManager().readLevels(config_yaml.getList("levels"));
        getTeamManager().loadGuild();
    }

    protected void saveOptions() {
        config_yaml.set("ecoType", ecoType);
        config_yaml.set("teamPvP", teamPvP);
        config_yaml.set("maxDisplay", maxDisplay);
        config_yaml.set("maxDescription", maxDescription);
        config_yaml.set("levels", getTeamManager().writeLevels());
        getTeamManager().saveGuild();
    }

    public void afterLoad() {
        Economy.checkEconomy(this);
        Flans.checkFlans(this);
    }

    @Nonnull
    protected ChatColor defaultChatColor() {
        return ChatColor.AQUA;
    }

    @Nonnull
    protected String defaultChatHead() {
        return "[" + Constant.PLUGIN_NAME + "] ";
    }

    public String defaultAdminPerm() {
        return Constant.PERM_ADMIN;
    }

    public boolean checkEcoType(String type) {
        return ecoType.equals(type);
    }

    public boolean isTeamPvP() {
        return teamPvP;
    }

    public TeamManager getTeamManager() {
        if (teamManager == null) teamManager = new TeamManager(this, config_file.getParentFile());
        return teamManager;
    }

    public int maxDisplay() {
        return maxDisplay;
    }

    public int maxDescription() {
        return maxDescription;
    }

}
