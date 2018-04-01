package org.soraworld.guild.command;

import org.soraworld.violet.command.IICommand;
import org.soraworld.violet.config.IIConfig;

public class CommandTeam extends IICommand {

    public CommandTeam(String name, String perm, IIConfig config, String... aliases) {
        super(name, perm, config, aliases);
    }

}
