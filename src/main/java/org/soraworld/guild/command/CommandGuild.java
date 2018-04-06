package org.soraworld.guild.command;

import net.minecraft.server.v1_7_R4.*;
import org.bukkit.command.CommandSender;
import org.bukkit.craftbukkit.v1_7_R4.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.soraworld.guild.config.Config;
import org.soraworld.guild.core.TeamGuild;
import org.soraworld.guild.core.TeamManager;
import org.soraworld.violet.command.CommandViolet;
import org.soraworld.violet.command.IICommand;
import org.soraworld.violet.constant.Violets;

import java.util.ArrayList;
import java.util.List;

public class CommandGuild extends CommandViolet {

    public CommandGuild(String name, String perm, final Config config, Plugin plugin) {
        super(name, perm, config, plugin);
        final TeamManager manager = config.getTeamManager();

        addSub(new CommandTeam("team", null, config));
        addSub(new IICommand("create", null, config, true) {
            @Override
            public boolean execute(Player player, ArrayList<String> args) {
                manager.createGuild(player);
                return true;
            }
        });
        addSub(new IICommand("join", null, config, true) {
            @Override
            public boolean execute(Player player, ArrayList<String> args) {
                if (args.isEmpty()) config.sendV(player, Violets.KEY_INVALID_ARG);
                else manager.joinGuild(player, args.get(0));
                return true;
            }

            @Override
            public List<String> getTabCompletions(ArrayList<String> args) {
                return config.getTeamManager().getGuilds();
            }
        });
        addSub(new IICommand("leave", null, config, true) {
            @Override
            public boolean execute(Player player, ArrayList<String> args) {
                if (player instanceof CraftPlayer) {
                    CraftPlayer craftPlayer = (CraftPlayer) player;
                    ChatModifier style = new ChatModifier();
                    style.setColor(EnumChatFormat.DARK_PURPLE).setBold(true);
                    style.setChatClickable(new ChatClickable(EnumClickAction.RUN_COMMAND, "/guild accept Shiki"));
                    craftPlayer.getHandle().b(new ChatComponentText("Click Me").setChatModifier(style));
                }
                return true;
            }
        });
        addSub(new IICommand("kick", null, config, true) {
            @Override
            public boolean execute(CommandSender sender, ArrayList<String> args) {
                return super.execute(sender, args);
            }
        });
        addSub(new IICommand("accept", null, config, true) {
            @Override
            public boolean execute(Player player, ArrayList<String> args) {
                System.out.println("Command accept:" + player);
                if (args.isEmpty()) {
                    config.send(player, Violets.KEY_INVALID_ARG);
                    return true;
                }
                TeamGuild guild = manager.getTeam(player.getName());
                if (guild == null) {
                    config.send(player, "notInAnyTeam");
                    return true;
                }
                if (guild.hasManager(player.getName())) {
                    String applicant = args.get(0);
                    if (guild.hasApplication(applicant)) {
                        if (guild.addMember(applicant)) {
                            config.send(player, "acceptMember", applicant);
                        } else {
                            config.send(player, "acceptFailed");
                        }
                        guild.closeApplication(applicant);
                    } else {
                        config.send(player, "noJoinApplication", applicant);
                    }
                } else {
                    config.send(player, "notManager");
                }
                return true;
            }
        });
        addSub(new IICommand("list", null, config, true) {
            @Override
            public boolean execute(Player player, ArrayList<String> args) {
                TeamGuild team = manager.getTeam(player.getName());
                if (team != null) {
                    team.showMemberList(player, config);
                } else {
                    config.send(player, "notInTeam");
                }
                return true;
            }
        });
        addSub(new IICommand("upgrade", null, config, true) {
            @Override
            public boolean execute(Player player, ArrayList<String> args) {
                TeamGuild guild = manager.getGuild(player.getName());
                if (guild != null) {
                    if (!guild.getLevel().guild) {

                    } else {
                        config.send(player, "guildCantUpgrade");
                    }
                } else {
                    config.send(player, "ownNoGuild");
                }
                return true;
            }
        });
    }

}
