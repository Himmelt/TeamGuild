package org.soraworld.guild.economy;

public interface IEconomy {

    boolean setEco(String player, double amount);

    boolean addEco(String player, double amount);

    double getEco(String player);

    boolean hasEnough(String player, double amount);

    boolean takeEco(String player, double amount);

}
