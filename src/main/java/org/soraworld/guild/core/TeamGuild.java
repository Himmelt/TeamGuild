package org.soraworld.guild.core;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.MemorySection;
import org.bukkit.entity.Player;
import org.soraworld.guild.config.Config;
import org.soraworld.guild.event.JoinApplicationEvent;

import javax.annotation.Nonnull;
import java.util.HashSet;
import java.util.LinkedHashSet;

public class TeamGuild {

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
        this.size = section.getInt("size", 0);
        this.balance = section.getInt("balance", 0);
        this.description = section.getString("description", leader + "'s Team.");
        this.managers.addAll(section.getStringList("managers"));
        this.members.addAll(section.getStringList("members"));
    }

    public void write(ConfigurationSection section) {
        section.set("display", display);
        section.set("size", size);
        section.set("balance", balance);
        section.set("description", description);
        section.set("managers", managers.toArray());
        section.set("members", members.toArray());
    }

    public boolean isLeader(String player) {
        return leader.equals(player);
    }

    public void addManager(String player) {
        managers.add(player);
        members.remove(player);
    }

    public boolean hasManager(String player) {
        return isLeader(player) || managers.contains(player);
    }

    public void delManager(String player) {
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

    public void showMemberList(CommandSender sender, Config config) {
        config.send(sender, "listHead");
        config.send(sender, "listLeader", leader);
        for (String manager : managers) {
            config.send(sender, "listManager", manager);
        }
        for (String member : members) {
            config.send(sender, "listMember", member);
        }
        config.send(sender, "listFoot");
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
            config.send(handler, "1 handleApplication");
            return;
        }
        handler = Bukkit.getPlayer(leader);
        if (handler != null) {
            /// send message
            config.send(handler, "2 handleApplication");
            return;
        }
        for (String manager : managers) {
            handler = Bukkit.getPlayer(manager);
            if (handler != null) {
                /// send message
                config.send(handler, "3 handleApplication");
                return;
            }
        }
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

}
