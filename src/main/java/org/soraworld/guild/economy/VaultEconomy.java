package org.soraworld.guild.economy;

import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.economy.EconomyResponse;
import org.bukkit.Bukkit;
import org.bukkit.plugin.RegisteredServiceProvider;

public class VaultEconomy implements IEconomy {

    private final Economy vaultEco;

    public VaultEconomy() throws Exception {
        Economy economy = null;
        RegisteredServiceProvider<Economy> provider = Bukkit.getServicesManager().getRegistration(Economy.class);
        if (provider != null) economy = provider.getProvider();
        if (economy == null) {
            throw new Exception("noVaultImpl");
        }
        vaultEco = economy;
    }

    public boolean setEco(String player, double amount) {
        if (takeEco(player, getEco(player))) {
            return addEco(player, amount);
        }
        return false;
    }

    public boolean addEco(String player, double amount) {
        return vaultEco.depositPlayer(Bukkit.getOfflinePlayer(player), amount).type == EconomyResponse.ResponseType.SUCCESS;
    }

    public double getEco(String player) {
        return vaultEco.getBalance(Bukkit.getOfflinePlayer(player));
    }

    public boolean hasEnough(String player, double amount) {
        return vaultEco.has(Bukkit.getOfflinePlayer(player), amount);
    }

    public boolean takeEco(String player, double amount) {
        return vaultEco.withdrawPlayer(Bukkit.getOfflinePlayer(player), amount).type == EconomyResponse.ResponseType.SUCCESS;
    }

}