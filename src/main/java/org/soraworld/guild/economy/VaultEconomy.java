package org.soraworld.guild.economy;

import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.economy.EconomyResponse;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.soraworld.guild.manager.TeamManager;

public class VaultEconomy implements IEconomy {

    private final Economy vaultEco;

    VaultEconomy(TeamManager config) throws Exception {
        Economy economy = null;
        RegisteredServiceProvider<Economy> provider = Bukkit.getServicesManager().getRegistration(Economy.class);
        if (provider != null) economy = provider.getProvider();
        if (economy == null) {
            throw new Exception("noVaultImpl");
        }
        config.consoleKey("vaultImpl", provider.getPlugin().getName(), economy.getName());
        vaultEco = economy;
    }

    public boolean setEco(OfflinePlayer player, double amount) {
        if (takeEco(player, getEco(player))) {
            return addEco(player, amount);
        }
        return false;
    }

    public boolean addEco(OfflinePlayer player, double amount) {
        return vaultEco.depositPlayer(player, amount).type == EconomyResponse.ResponseType.SUCCESS;
    }

    public double getEco(OfflinePlayer player) {
        return vaultEco.getBalance(player);
    }

    public boolean hasEnough(OfflinePlayer player, double amount) {
        return vaultEco.has(player, amount);
    }

    public boolean takeEco(OfflinePlayer player, double amount) {
        return vaultEco.withdrawPlayer(player, amount).type == EconomyResponse.ResponseType.SUCCESS;
    }
}
