package org.soraworld.guild.economy;

import org.soraworld.guild.manager.TeamManager;

public class Economy {

    private static IEconomy economy;

    public static void checkEconomy(final TeamManager config) {
        if (config.checkEcoType("Vault")) {
            try {
                economy = new VaultEconomy(config);
                config.consoleKey("EcoSupport", "Vault");
            } catch (Throwable e) {
                if (e.getMessage().equals("noVaultImpl")) {
                    config.console("noVaultImpl");
                } else {
                    config.consoleKey("EcoNotSupport", "Vault");
                }
            }
        } else if (config.checkEcoType("Essentials")) {
            try {
                economy = new EssEconomy();
                config.consoleKey("EcoSupport", "Essentials");
            } catch (Throwable ignored) {
                config.consoleKey("EcoNotSupport", "Essentials");
            }
        } else if (config.checkEcoType("PlayerPoints")) {
            try {
                economy = new PointsEconomy();
                config.consoleKey("EcoSupport", "PlayerPoints");
            } catch (Throwable ignored) {
                config.consoleKey("EcoNotSupport", "PlayerPoints");
            }
        } else {
            config.console("InvalidEcoSupport");
        }
    }

    public static boolean setEco(String player, double amount) {
        return economy == null || economy.setEco(player, amount);
    }

    public static boolean addEco(String player, double amount) {
        return economy == null || economy.addEco(player, amount);
    }

    public static double getEco(String player) {
        return economy == null ? 0 : economy.getEco(player);
    }

    public static boolean hasEnough(String player, double amount) {
        return economy == null || economy.hasEnough(player, amount);
    }

    public static boolean takeEco(String player, double amount) {
        return economy == null || economy.takeEco(player, amount);
    }
}
