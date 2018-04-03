package org.soraworld.guild.economy;

import org.soraworld.guild.config.Config;

public class Economy implements IEconomy {

    private final IEconomy economy;

    public Economy(Config config) {
        IEconomy economy = null;
        if (config.checkEcoType("Vault")) {
            try {
                economy = new VaultEconomy();
                config.console("vaultSupport");
            } catch (Throwable e) {
                if (e.getMessage().equals("noVaultImpl")) {
                    config.console("noVaultImpl");
                }
                config.console("vaultNotSupport");
            }
        } else if (config.checkEcoType("Essentials")) {
            try {
                economy = new EssEconomy();
                config.console("essSupport");
            } catch (Throwable ignored) {
                ignored.printStackTrace();
                config.console("essNotSupport");
            }
        }
        this.economy = economy;
    }

    public boolean setEco(String player, double amount) {
        return economy == null || economy.setEco(player, amount);
    }

    public boolean addEco(String player, double amount) {
        return economy == null || economy.addEco(player, amount);
    }

    public double getEco(String player) {
        return economy == null ? 0 : economy.getEco(player);
    }

    public boolean hasEnough(String player, double amount) {
        return economy == null || economy.hasEnough(player, amount);
    }

    public boolean takeEco(String player, double amount) {
        return economy == null || economy.takeEco(player, amount);
    }

}
