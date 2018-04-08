package org.soraworld.guild.core;

import net.minecraft.server.v1_7_R4.EnumClickAction;
import net.minecraft.server.v1_7_R4.EnumHoverAction;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.MemorySection;
import org.bukkit.entity.Player;
import org.soraworld.guild.config.Config;
import org.soraworld.guild.event.JoinApplicationEvent;
import org.soraworld.violet.chat.IIChat;
import org.soraworld.violet.chat.IILang;

import javax.annotation.Nonnull;
import java.util.HashSet;
import java.util.LinkedHashSet;

public class TeamGuild implements Comparable<TeamGuild> {

    private int size;
    private int balance = 0;
    private String display;
    private String description;
    private final String leader;
    private final HashSet<String> members = new HashSet<>();
    private final HashSet<String> managers = new HashSet<>();
    private final LinkedHashSet<String> applications = new LinkedHashSet<>();

    public TeamGuild(String leader, int size) {
        this.leader = leader;
        this.size = size;
    }

    public TeamGuild(String leader, MemorySection section) {
        this.leader = leader;
        this.display = section.getString("display", leader);
        if (!display.endsWith("&r")) display += "&r";
        this.size = section.getInt("size", 0);
        this.balance = section.getInt("balance", 0);
        this.description = section.getString("description", leader + "'s Team.");
        this.managers.addAll(section.getStringList("managers"));
        this.members.addAll(section.getStringList("members"));
        this.applications.addAll(section.getStringList("applications"));
    }

    public void write(ConfigurationSection section) {
        section.set("display", display);
        section.set("size", size);
        section.set("balance", balance);
        section.set("description", description);
        section.set("managers", managers.toArray());
        section.set("members", members.toArray());
        section.set("applications", applications.toArray());
    }

    public boolean isLeader(String player) {
        return leader.equals(player);
    }

    // TODO 外界设置，以输出超过管理员数量的提示信息
    public void setManager(String player, TeamManager manager) {
        if (managers.size() < manager.getLevel(this).mans) {
            managers.add(player);
            members.remove(player);
        }
    }

    public boolean hasManager(String player) {
        return isLeader(player) || managers.contains(player);
    }

    public void unsetManager(String player) {
        members.add(player);
        managers.remove(player);
    }

    @Nonnull
    public String getLeader() {
        return leader;
    }

    @Nonnull
    public String getDisplay() {
        return display == null ? "" : display;
    }

    public void setDisplay(String display) {
        if (!display.endsWith("&r")) display += "&r";
        this.display = display;
    }

    public boolean addMember(String player) {
        if (members.size() + managers.size() < size) {
            members.add(player);
            return true;
        }
        return false;
    }

    public boolean hasMember(String player) {
        return isLeader(player) || hasManager(player) || members.contains(player);
    }

    public void delMember(String player) {
        managers.remove(player);
        members.remove(player);
    }

    public int getCount() {
        return members.size() + managers.size();
    }

    public String getDescription() {
        return description == null ? "" : description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void addJoinApplication(String username) {
        applications.add(username);
        Bukkit.getPluginManager().callEvent(new JoinApplicationEvent(leader, username));
    }

    public void notifyApplication(Player player, Config config) {
        for (String applicant : applications) {
            handleApplication(player, applicant, config);
        }
    }

    public void handleApplication(Player handler, String applicant, Config config) {
        if (handler != null) {
            sendHandleMessage(handler, applicant, config);
            return;
        }
        handler = Bukkit.getPlayer(leader);
        if (handler != null) {
            sendHandleMessage(handler, applicant, config);
        }
        for (String manager : managers) {
            handler = Bukkit.getPlayer(manager);
            if (handler != null) {
                sendHandleMessage(handler, applicant, config);
            }
        }
    }

    private void sendHandleMessage(Player handler, String applicant, Config config) {
        IILang lang = config.iiLang;
        config.iiChat.sendMessage(handler,
                IIChat.format(lang.format("handleText", applicant)),
                IIChat.format(lang.format("acceptText"),
                        EnumClickAction.RUN_COMMAND, "/guild accept " + applicant,
                        EnumHoverAction.SHOW_TEXT, lang.format("acceptHover")),
                IIChat.format(lang.format("rejectText"),
                        EnumClickAction.RUN_COMMAND, "/guild reject " + applicant,
                        EnumHoverAction.SHOW_TEXT, lang.format("rejectHover"))
        );
    }

    public boolean hasApplication(String applicant) {
        return applications.contains(applicant);
    }

    public void closeApplication(String applicant) {
        applications.remove(applicant);
    }

    public int size() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public void notifyLeave(String username, final Config config) {
        Player handler = Bukkit.getPlayer(leader);
        if (handler != null) {
            config.send(handler, "notifyLeave", username);
        }
        for (String manager : managers) {
            handler = Bukkit.getPlayer(manager);
            if (handler != null) {
                config.send(handler, "notifyLeave", username);
            }
        }
    }

    public void showMemberList(CommandSender sender, Config config) {
        config.send(sender, "listHead", display);
        config.send(sender, "listLeader", leader);
        for (String manager : managers) {
            config.send(sender, "listManager", manager);
        }
        for (String member : members) {
            config.send(sender, "listMember", member);
        }
        config.send(sender, "listFoot");
    }

    public void showGuildInfo(CommandSender sender, Config config, TeamManager manager) {
        config.send(sender, "infoDisplay", display);
        config.send(sender, "infoLeader", leader);
        if (sender instanceof Player && hasMember(sender.getName()) || sender instanceof ConsoleCommandSender) {
            config.send(sender, "infoBalance", balance);
            config.send(sender, "maxManagers", manager.getLevel(this).mans);
        }
        config.send(sender, "infoMembers", getCount(), size);
        config.send(sender, "infoDescription", description);
    }

    @Override
    public int compareTo(@Nonnull TeamGuild other) {
        // Descending
        if (this.leader.equals(other.leader)) return 0;
        return this.size > other.size ? -1 : 1;
    }

    @Override
    public boolean equals(Object obj) {
        return this == obj || obj instanceof TeamGuild && this.leader.equals(((TeamGuild) obj).leader);
    }

}
