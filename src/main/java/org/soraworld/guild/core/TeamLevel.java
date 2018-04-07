package org.soraworld.guild.core;

import javax.annotation.Nonnull;

public class TeamLevel implements Comparable<TeamLevel> {

    public final int size;
    public final int cost;
    public final int mans;
    public final boolean guild;

    public TeamLevel(int size, int cost, int mans, boolean guild) {
        this.size = size < 0 ? 0 : size;
        this.cost = cost < 0 ? 0 : cost;
        this.mans = mans > 0 ? mans < size ? mans : size : 0;
        this.guild = guild;
    }

    public int compareTo(@Nonnull TeamLevel level) {
        return this.size - level.size;
    }

}
