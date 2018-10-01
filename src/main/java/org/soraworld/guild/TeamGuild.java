package org.soraworld.guild;

import com.bekvon.bukkit.residence.Residence;
import com.bekvon.bukkit.residence.api.ResidenceApi;
import me.clip.placeholderapi.PlaceholderAPI;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.event.Listener;
import org.soraworld.guild.command.CommandGuild;
import org.soraworld.guild.expansion.GuildExpansion;
import org.soraworld.guild.listener.ChatListener;
import org.soraworld.guild.listener.EventListener;
import org.soraworld.guild.listener.PvPListener;
import org.soraworld.guild.manager.TeamManager;
import org.soraworld.hocon.node.Paths;
import org.soraworld.violet.command.SpigotBaseSubs;
import org.soraworld.violet.command.SpigotCommand;
import org.soraworld.violet.manager.SpigotManager;
import org.soraworld.violet.plugin.SpigotPlugin;
import org.soraworld.violet.util.ChatColor;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class TeamGuild extends SpigotPlugin {

    private static final boolean placeholderApi;
    public static final boolean residenceApi;

    static {
        boolean residence = false, placeholder = false;
        try {
            PlaceholderAPI.class.getName();
            PlaceholderExpansion.class.getName();
            placeholder = true;
        } catch (Throwable ignored) {
        }
        try {
            Residence.class.getName();
            ResidenceApi.class.getName();
            residence = true;
        } catch (Throwable ignored) {
        }
        placeholderApi = placeholder;
        residenceApi = residence;
    }

    @Nonnull
    public String assetsId() {
        return "guild";
    }

    public void afterEnable() {
        if (placeholderApi) {
            try {
                PlaceholderExpansion expansion = GuildExpansion.class.getConstructor(TeamManager.class).newInstance(manager);
                if (PlaceholderAPI.registerExpansion(expansion)) {
                    manager.consoleKey("registerExpansionSuccess");
                } else manager.consoleKey("registerExpansionFailed");
            } catch (Throwable ignored) {
                manager.console(ChatColor.RED + "GuildExpansion Construct Instance failed !!!");
            }
        } else manager.consoleKey("noPlaceholderAPI");
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
        manager.getDisableCmds().forEach(s -> command.removeSub(new Paths(s)));
        register(this, command);
    }
}
