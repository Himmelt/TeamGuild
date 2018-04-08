package org.soraworld.guild.economy;

import org.black_ixx.playerpoints.PlayerPoints;
import org.black_ixx.playerpoints.PlayerPointsAPI;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;

public class PointsEconomy implements IEconomy {

    private final PlayerPointsAPI pointsAPI;

    PointsEconomy() throws Exception {
        PlayerPointsAPI api = null;
        Plugin plugin = Bukkit.getPluginManager().getPlugin("PlayerPoints");
        if (plugin instanceof PlayerPoints) {
            api = ((PlayerPoints) plugin).getAPI();
        }
        if (api == null) throw new Exception();
        pointsAPI = api;
    }

    public boolean setEco(String player, double amount) {
        return pointsAPI.set(player, (int) amount);
    }

    public boolean addEco(String player, double amount) {
        return pointsAPI.give(player, (int) amount);
    }

    public double getEco(String player) {
        return pointsAPI.look(player);
    }

    public boolean hasEnough(String player, double amount) {
        return pointsAPI.look(player) >= amount;
    }

    public boolean takeEco(String player, double amount) {
        return pointsAPI.take(player, (int) amount);
    }

}
