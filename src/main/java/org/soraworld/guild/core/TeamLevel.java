package org.soraworld.guild.core;

import javax.annotation.Nonnull;

public class TeamLevel implements Comparable<TeamLevel> {

    public final int size;
    public final int cost;
    public final boolean guild;

    public TeamLevel(int size, int cost, boolean guild) {
        this.size = size;
        this.cost = cost;
        this.guild = guild;
    }

    public int compareTo(@Nonnull TeamLevel level) {
        return this.size - level.size;
    }

}
