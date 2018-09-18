package org.soraworld.guild.economy;

import org.black_ixx.playerpoints.PlayerPoints;
import org.black_ixx.playerpoints.PlayerPointsAPI;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
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

    public boolean setEco(OfflinePlayer player, double amount) {
        return pointsAPI.set(player.getUniqueId(), (int) amount);
    }

    public boolean addEco(OfflinePlayer player, double amount) {
        return pointsAPI.give(player.getUniqueId(), (int) amount);
    }

    public double getEco(OfflinePlayer player) {
        return pointsAPI.look(player.getUniqueId());
    }

    public boolean hasEnough(OfflinePlayer player, double amount) {
        return pointsAPI.look(player.getUniqueId()) >= amount;
    }

    public boolean takeEco(OfflinePlayer player, double amount) {
        return pointsAPI.take(player.getUniqueId(), (int) amount);
    }
}
