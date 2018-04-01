package org.soraworld.guild.economy;

import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.economy.EconomyResponse;
import org.bukkit.Bukkit;
import org.bukkit.plugin.RegisteredServiceProvider;

public class VaultEco implements IEconomy {

    private final Economy vaultEco;

    public VaultEco() {
        RegisteredServiceProvider<Economy> provider = Bukkit.getServicesManager().getRegistration(Economy.class);
        if (provider != null) {
            vaultEco = provider.getProvider();
        } else {
            vaultEco = null;
        }
        if (vaultEco != null) {
            System.out.println("Vault 经济系统 对接完成.");
        } else {
            System.out.println("没有发现Vault经济系统 对接失败.");
        }
    }


    public boolean setEco(String player, double amount) {
        if (vaultEco != null && vaultEco.isEnabled()) {
            if (takeEco(player, getEco(player))) {
                return addEco(player, amount);
            }
        }
        return false;
    }

    public boolean addEco(String player, double amount) {
        if (vaultEco != null && vaultEco.isEnabled()) {
            return vaultEco.depositPlayer(Bukkit.getOfflinePlayer(player), amount).type == EconomyResponse.ResponseType.SUCCESS;
        }
        return false;
    }

    public double getEco(String player) {
        if (vaultEco != null && vaultEco.isEnabled()) {
            return vaultEco.getBalance(Bukkit.getOfflinePlayer(player));
        }
        return 0;
    }

    public boolean hasEnough(String player, double amount) {
        if (vaultEco != null && vaultEco.isEnabled()) {
            return vaultEco.has(Bukkit.getOfflinePlayer(player), amount);
        }
        return false;
    }

    public boolean takeEco(String player, double amount) {
        if (vaultEco != null && vaultEco.isEnabled()) {
            return vaultEco.withdrawPlayer(Bukkit.getOfflinePlayer(player), amount).type == EconomyResponse.ResponseType.SUCCESS;
        }
        return false;
    }

}
