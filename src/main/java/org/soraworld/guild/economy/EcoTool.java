package org.soraworld.guild.economy;

public class EcoTool {

    private final IEconomy iEconomy;

    public EcoTool() {
        VaultEco vaultEco = null;
        EssEco essEco = null;
        try {
            vaultEco = new VaultEco();
            essEco = new EssEco();
        } catch (Throwable ignored) {
            //System.out.println("Vault Eco 加载失败.");
            System.out.println("Essentials Economy 加载失败.");
        }
        //this.iEconomy = vaultEco;
        this.iEconomy = essEco;
    }

}
