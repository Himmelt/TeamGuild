package org.soraworld.guild.economy;

import org.bukkit.OfflinePlayer;

public interface IEconomy {
    boolean setEco(OfflinePlayer player, double amount);

    boolean addEco(OfflinePlayer player, double amount);

    double getEco(OfflinePlayer player);

    boolean hasEnough(OfflinePlayer player, double amount);

    boolean takeEco(OfflinePlayer player, double amount);
}
