package org.soraworld.guild.command;

import org.bukkit.command.CommandSender;
import org.bukkit.plugin.Plugin;
import org.soraworld.guild.constant.Constant;
import org.soraworld.violet.command.CommandViolet;
import org.soraworld.violet.command.IICommand;
import org.soraworld.violet.config.IIConfig;

import java.util.ArrayList;

public class CommandGuild extends CommandViolet {

    public CommandGuild(String name, String perm, IIConfig config, Plugin plugin) {
        super(name, perm, config, plugin);
        addSub(new IICommand("create", Constant.PERM_ADMIN, config) {
            @Override
            public boolean execute(CommandSender sender, ArrayList<String> args) {
                return super.execute(sender, args);
            }
        });
        addSub(new IICommand("join", null, config) {
            @Override
            public boolean execute(CommandSender sender, ArrayList<String> args) {
                return super.execute(sender, args);
            }
        });
        addSub(new IICommand("leave", null, config) {
            @Override
            public boolean execute(CommandSender sender, ArrayList<String> args) {
                return super.execute(sender, args);
            }
        });
        addSub(new IICommand("kick", null, config) {
            @Override
            public boolean execute(CommandSender sender, ArrayList<String> args) {
                return super.execute(sender, args);
            }
        });
        addSub(new IICommand("list", null, config) {
            @Override
            public boolean execute(CommandSender sender, ArrayList<String> args) {
                return super.execute(sender, args);
            }
        });
    }

}
