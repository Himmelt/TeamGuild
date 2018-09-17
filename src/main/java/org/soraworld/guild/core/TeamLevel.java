package org.soraworld.guild.core;

import org.soraworld.hocon.node.Node;
import org.soraworld.hocon.node.NodeMap;
import org.soraworld.hocon.node.Options;
import org.soraworld.hocon.node.Setting;
import org.soraworld.hocon.serializer.TypeSerializer;

import javax.annotation.Nonnull;
import java.lang.reflect.Type;

public class TeamLevel implements Comparable<TeamLevel>, TypeSerializer<TeamLevel> {

    @Setting
    public final int size;
    @Setting
    public final int cost;
    @Setting
    public final int mans;
    @Setting
    public final boolean guild;

    public TeamLevel(int size, int cost, int mans, boolean guild) {
        this.size = size < 0 ? 0 : size;
        this.cost = cost < 0 ? 0 : cost;
        this.mans = mans > 0 ? mans < size ? mans : size : 0;
        this.guild = guild;
    }

    public TeamLevel() {
        this.size = 0;
        this.cost = 0;
        this.mans = 0;
        this.guild = false;
    }

    public int compareTo(@Nonnull TeamLevel level) {
        return this.size - level.size;
    }

    public TeamLevel deserialize(@Nonnull Type type, @Nonnull Node node) {
        if (node instanceof NodeMap) {
            TeamLevel level = new TeamLevel();
            ((NodeMap) node).modify(level);
            if (level.size > 0) return level;
        }
        return null;
    }

    public Node serialize(@Nonnull Type type, TeamLevel value, @Nonnull Options options) {
        NodeMap map = new NodeMap(options);
        if (value != null) map.extract(value);
        return map;
    }

    @Nonnull
    public Type getRegType() {
        return TeamLevel.class;
    }
}
