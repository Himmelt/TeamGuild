package org.soraworld.guild;

import net.minecraft.server.v1_7_R4.ChatClickable;
import net.minecraft.server.v1_7_R4.ChatComponentText;
import net.minecraft.server.v1_7_R4.ChatMessage;
import net.minecraft.server.v1_7_R4.ChatModifier;
import org.bukkit.event.Listener;
import org.soraworld.guild.command.CommandGuild;
import org.soraworld.guild.config.Config;
import org.soraworld.guild.constant.Constant;
import org.soraworld.guild.listener.EventListener;
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
    protected List<Listener> registerEvents(IIConfig config) {
        ArrayList<Listener> listeners = new ArrayList<>();
        if (config instanceof Config) {
            Config cfg = (Config) config;
            listeners.add(new EventListener(cfg));
            if (!cfg.isTeamPvP()) listeners.add(new EventListener(cfg));
        }
        return listeners;
    }

    @Nullable
    protected IICommand registerCommand(IIConfig config) {
        if (config instanceof Config) return new CommandGuild(Constant.PLUGIN_ID, null, (Config) config, this);
        return null;
    }

    protected void afterEnable() {
        // TODO Click Chat
        System.out.println(ChatModifier.class);
        System.out.println(ChatMessage.class);
        System.out.println(ChatComponentText.class);
        System.out.println(ChatClickable.class);
        /*
        *  class net.minecraft.util.ChatStyle
           class net.minecraft.util.ChatComponentTranslation
           class net.minecraft.util.ChatComponentText
           class net.minecraft.event.ClickEvent
         */
    }

    protected void beforeDisable() {

    }

}
