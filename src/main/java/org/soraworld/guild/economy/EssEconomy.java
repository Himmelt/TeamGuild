package org.soraworld.guild.economy;

import com.earth2me.essentials.Essentials;
import net.ess3.api.Economy;

import java.math.BigDecimal;

public class EssEconomy implements IEconomy {

    public EssEconomy() {
        Essentials.class.getName();
        Economy.class.getName();
    }

    public boolean setEco(String player, double amount) {
        try {
            Economy.setMoney(player, BigDecimal.valueOf(amount));
            return true;
        } catch (Throwable ignored) {
            return false;
        }
    }

    public boolean addEco(String player, double amount) {
        try {
            Economy.add(player, BigDecimal.valueOf(amount));
            return true;
        } catch (Throwable ignored) {
            return false;
        }
    }

    public double getEco(String player) {
        try {
            return Economy.getMoneyExact(player).doubleValue();
        } catch (Throwable ignored) {
            return 0;
        }
    }

    public boolean hasEnough(String player, double amount) {
        try {
            return Economy.hasEnough(player, BigDecimal.valueOf(amount));
        } catch (Throwable ignored) {
            return false;
        }
    }

    public boolean takeEco(String player, double amount) {
        try {
            Economy.substract(player, BigDecimal.valueOf(amount));
            return true;
        } catch (Throwable ignored) {
            return false;
        }
    }

}
