package org.soraworld.guild;

import org.bukkit.event.Listener;
import org.soraworld.guild.command.CommandGuild;
import org.soraworld.guild.config.Config;
import org.soraworld.guild.constant.Constant;
import org.soraworld.violet.VioletPlugin;
import org.soraworld.violet.command.IICommand;
import org.soraworld.violet.config.IIConfig;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class TeamGuild extends VioletPlugin {

    @Nonnull
    protected IIConfig registerConfig(File path) {
        return new Config(path, this);
    }

    @Nonnull
    protected List<Listener> registerEvents(IIConfig iiConfig) {
        return new ArrayList<>();
    }

    @Nullable
    protected IICommand registerCommand(IIConfig config) {
        return new CommandGuild(Constant.PLUGIN_ID, null, config, this);
    }

}
