package org.soraworld.guild;

import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.event.Listener;
import org.soraworld.guild.command.CommandGuild;
import org.soraworld.guild.expansion.GuildExpansion;
import org.soraworld.guild.listener.ChatListener;
import org.soraworld.guild.listener.EventListener;
import org.soraworld.guild.listener.PvPListener;
import org.soraworld.guild.manager.TeamManager;
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

    public void afterEnable() {
        try {
            if (PlaceholderAPI.registerExpansion(new GuildExpansion((TeamManager) manager))) {
                manager.consoleKey("registerExpansionSuccess");
            } else manager.consoleKey("registerExpansionFailed");
        } catch (Throwable e) {
            manager.consoleKey("placeholderAPIException");
        }
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
            if (!manager.teamPvP) listeners.add(new PvPListener(manager));
            listeners.add(new ChatListener(manager));
        }
        return listeners;
    }

    protected void registerCommands() {
        SpigotCommand command = new SpigotCommand(getId(), null, false, manager, "team");
        command.extractSub(SpigotBaseSubs.class, "lang");
        command.extractSub(SpigotBaseSubs.class, "debug");
        command.extractSub(SpigotBaseSubs.class, "save");
        command.extractSub(SpigotBaseSubs.class, "reload");
        command.extractSub(SpigotBaseSubs.class, "help");
        command.extractSub(SpigotBaseSubs.class, "rextract");
        command.extractSub(CommandGuild.class);
        register(this, command);
    }
}
