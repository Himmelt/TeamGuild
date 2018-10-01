package org.soraworld.guild.economy;

import org.bukkit.OfflinePlayer;
import org.soraworld.guild.manager.TeamManager;

public class Economy {

    private static IEconomy eco;
    private static boolean ignore = false;

    public static void checkEconomy(final TeamManager manager, final String ecoType, boolean ignore) {
        Economy.ignore = ignore;
        if ("Vault".equalsIgnoreCase(ecoType)) {
            try {
                eco = new VaultEconomy(manager);
                manager.consoleKey("vault.ecoImpl", "Vault");
            } catch (Throwable e) {
                if (e.getMessage().equals("vault.noEcoImpl")) {
                    manager.consoleKey("vault.noEcoImpl");
                } else {
                    manager.consoleKey("economy.notHook", "Vault");
                }
            }
        } else if ("Essentials".equalsIgnoreCase(ecoType)) {
            try {
                eco = new EssEconomy();
                manager.consoleKey("economy.hook", "Essentials");
            } catch (Throwable ignored) {
                manager.consoleKey("economy.notHook", "Essentials");
            }
        } else if ("PlayerPoints".equalsIgnoreCase(ecoType)) {
            try {
                eco = new PointsEconomy();
                manager.consoleKey("economy.hook", "PlayerPoints");
            } catch (Throwable ignored) {
                manager.consoleKey("economy.notHook", "PlayerPoints");
            }
        } else {
            manager.consoleKey("economy.invalid", ecoType);
        }
    }

    public static boolean setEco(OfflinePlayer player, double amount) {
        return eco == null ? ignore : eco.setEco(player, amount);
    }

    public static boolean addEco(OfflinePlayer player, double amount) {
        return eco == null ? ignore : eco.addEco(player, amount);
    }

    public static double getEco(OfflinePlayer player) {
        return eco == null ? 0 : eco.getEco(player);
    }

    public static boolean hasEnough(OfflinePlayer player, double amount) {
        return eco == null ? ignore : eco.hasEnough(player, amount);
    }

    public static boolean takeEco(OfflinePlayer player, double amount) {
        return eco == null ? ignore : eco.takeEco(player, amount);
    }
}
