package org.soraworld.guild;

import org.bukkit.event.Listener;
import org.soraworld.guild.command.CommandGuild;
import org.soraworld.guild.config.TeamManager;
import org.soraworld.guild.listener.ChatListener;
import org.soraworld.guild.listener.EventListener;
import org.soraworld.guild.listener.PvPListener;
import org.soraworld.violet.command.SpigotBaseSubs;
import org.soraworld.violet.command.SpigotCommand;
import org.soraworld.violet.manager.SpigotManager;
import org.soraworld.violet.plugin.SpigotPlugin;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class TeamGuild extends SpigotPlugin {
    @Nonnull
    public String assetsId() {
        return "guild";
    }

    @Nonnull
    protected SpigotManager registerManager(Path path) {
        return new TeamManager(this, path);
    }

    @Nullable
    protected List<Listener> registerListeners() {
        ArrayList<Listener> listeners = new ArrayList<>();
        if (manager instanceof TeamManager) {
            TeamManager manager = (TeamManager) this.manager;
            listeners.add(new EventListener(manager));
            if (!manager.isTeamPvP()) listeners.add(new PvPListener(manager));
            listeners.add(new ChatListener(manager));
        }
        return listeners;
    }

    protected void registerCommands() {
        SpigotCommand command = new SpigotCommand(getId(), manager.defAdminPerm(), false, manager, "team");
        command.extractSub(SpigotBaseSubs.class, "lang");
        command.extractSub(SpigotBaseSubs.class, "debug");
        command.extractSub(SpigotBaseSubs.class, "save");
        command.extractSub(SpigotBaseSubs.class, "reload");
        command.extractSub(SpigotBaseSubs.class, "help");
        command.extractSub(CommandGuild.class);
        register(this, command);
    }
}
