package org.soraworld.guild.core;

import com.bekvon.bukkit.residence.Residence;
import com.bekvon.bukkit.residence.protection.ClaimedResidence;
import com.bekvon.bukkit.residence.protection.FlagPermissions;
import com.bekvon.bukkit.residence.protection.ResidenceManager;
import com.bekvon.bukkit.residence.protection.ResidencePermissions;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.soraworld.guild.event.JoinApplicationEvent;
import org.soraworld.guild.manager.TeamManager;
import org.soraworld.hocon.node.Node;
import org.soraworld.hocon.node.NodeMap;
import org.soraworld.hocon.node.Options;
import org.soraworld.hocon.node.Setting;

import java.lang.reflect.Field;
import java.util.*;

import static org.soraworld.guild.GuildPlugin.residenceApi;
import static org.soraworld.violet.util.ChatColor.COLOR_CHAR;
import static org.soraworld.violet.util.ChatColor.RESET;

public class TeamGuild implements Comparable<TeamGuild> {

    @Setting
    private int level;
    @Setting
    private int fame = 0;
    @Setting
    private double balance = 0;
    @Setting
    private boolean showTopJoin = true;
    @Setting
    private String display;
    @Setting
    private String description;
    @Setting
    private long lastBonus = 0;
    @Setting
    private HashSet<String> members = new HashSet<>();
    @Setting
    private HashSet<String> managers = new HashSet<>();
    @Setting
    private HashSet<String> blackList = new HashSet<>();
    @Setting
    private LinkedHashSet<String> applications = new LinkedHashSet<>();
    @Setting
    private HashMap<String, Integer> fameMap = new HashMap<>();

    private UUID attorn;
    private OfflinePlayer leader;
    private final TeamManager manager;
    private final HashSet<UUID> invites = new HashSet<>();

    public TeamGuild(OfflinePlayer leader, int level, TeamManager manager) {
        this.leader = leader;
        this.level = level;
        this.manager = manager;
    }

    public boolean isLeader(Player player) {
        return player.getUniqueId().equals(leader.getUniqueId());
    }

    public boolean isLeader(String player) {
        return player.equals(leader.getName());
    }

    public void setManager(String player) {
        managers.add(player);
        members.remove(player);
    }

    public boolean isManager(Player player) {
        return isLeader(player) || managers.contains(player.getName());
    }

    public boolean isManager(String player) {
        return isLeader(player) || managers.contains(player);
    }

    public void unsetManager(String player) {
        members.add(player);
        managers.remove(player);
    }

    public Player getLeader() {
        return Bukkit.getPlayer(leader.getUniqueId());
    }

    public String getTeamLeader() {
        return leader.getName();
    }

    public String getDisplay() {
        return display == null ? "" : display.replace('&', ChatColor.COLOR_CHAR);
    }

    public void setDisplay(String display) {
        if (!display.endsWith("&r")) display += "&r";
        this.display = display;
    }

    public String getDescription() {
        return description == null ? "" : description.replace('&', COLOR_CHAR);
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public boolean addMember(String player) {
        if (blackList.contains(player)) return false;
        if (members.size() + managers.size() < getTeamLevel().size) {
            members.add(player);
            Player app = Bukkit.getPlayer(player);
            if (app != null) manager.cacheTeam(app, this);
            return true;
        }
        return false;
    }

    public boolean addMember(Player player) {
        if (blackList.contains(player.getName())) return false;
        if (members.size() + managers.size() < getTeamLevel().size) {
            members.add(player.getName());
            manager.cacheTeam(player, this);
            return true;
        }
        return false;
    }

    public boolean hasMember(String name) {
        return isLeader(name) || isManager(name) || members.contains(name);
    }

    public boolean hasMember(Player player) {
        return isLeader(player) || isManager(player) || members.contains(player.getName());
    }

    public void delMember(String player) {
        managers.remove(player);
        members.remove(player);
        fameMap.remove(player);
    }

    public boolean isBlack(String name) {
        return blackList.contains(name);
    }

    public void addBlack(String player) {
        if (!members.contains(player) && !leader.getName().equals(player)) {
            blackList.add(player);
        }
    }

    public void removeBlack(String player) {
        blackList.remove(player);
    }

    public int getAmount() {
        return members.size() + managers.size();
    }

    public void addJoinApplication(String username) {
        applications.add(username);
        Bukkit.getPluginManager().callEvent(new JoinApplicationEvent(leader.getName(), username));
    }

    public void notifyApplication(Player player) {
        for (String applicant : applications) {
            handleApplication(player, applicant);
        }
    }

    public void handleApplication(Player handler, String applicant) {
        if (handler != null) {
            manager.sendHandleMessage(handler, applicant);
            return;
        }
        handler = getLeader();
        if (handler != null) {
            manager.sendHandleMessage(handler, applicant);
        }
        for (String man : managers) {
            handler = Bukkit.getPlayer(man);
            if (handler != null) {
                manager.sendHandleMessage(handler, applicant);
            }
        }
    }

    public boolean hasApplication(String applicant) {
        return applications.contains(applicant);
    }

    public void closeApplication(String applicant) {
        applications.remove(applicant);
    }

    public TeamLevel getTeamLevel() {
        return manager.getLevel(level);
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public void notifyLeave(String username) {
        Player handler = getLeader();
        if (handler != null) {
            manager.sendKey(handler, "guild.notifyLeave", username);
        }
        for (String man : managers) {
            handler = Bukkit.getPlayer(man);
            if (handler != null) {
                manager.sendKey(handler, "guild.notifyLeave", username);
            }
        }
    }

    public void showMemberList(CommandSender sender) {
        manager.sendKey(sender, "list.head", getDisplay());
        manager.sendKey(sender, "list.leader", leader.getName());
        for (String man : managers) {
            manager.sendKey(sender, "list.manager", man);
        }
        for (String member : members) {
            manager.sendKey(sender, "list.member", member);
        }
        manager.sendKey(sender, "list.foot");
    }

    public int compareTo(TeamGuild other) {
        if (other.getFame() == this.getFame()) {
            if (other.level == this.level) {
                return (int) (other.balance - this.balance);
            }
            return other.level - this.level;
        }
        return other.getFame() - this.getFame();
    }

    public boolean equals(Object obj) {
        return this == obj || obj instanceof TeamGuild && this.leader.equals(((TeamGuild) obj).leader);
    }

    public int getManSize() {
        return managers.size();
    }

    public static TeamGuild deserialize(Node node, String leader, TeamManager manager) {
        if (node instanceof NodeMap) {
            TeamGuild guild = new TeamGuild(Bukkit.getOfflinePlayer(leader), 0, manager);
            ((NodeMap) node).modify(guild);
            if (!guild.display.endsWith("&r") && !guild.display.endsWith(RESET.toString())) guild.display += "&r";
            return guild;
        }
        return null;
    }

    public static NodeMap serialize(TeamGuild guild, Options options) {
        NodeMap node = new NodeMap(options);
        if (guild != null) node.extract(guild);
        return node;
    }

    public boolean isShowTopJoin() {
        return showTopJoin;
    }

    public void setShowTopJoin(boolean show) {
        this.showTopJoin = show;
    }

    public void sendAttorn(Player target) {
        this.attorn = target.getUniqueId();
        manager.sendAttornMessage(target, this);
    }

    public boolean isAttorn(Player player) {
        return attorn != null && player.getUniqueId().equals(attorn);
    }

    private void setLeader(Player player) {
        this.leader = player;
        managers.remove(player.getName());
        members.remove(player.getName());
    }

    public void resetAttorn() {
        this.attorn = null;
    }

    public boolean rejectAttorn(Player player) {
        if (isAttorn(player)) {
            attorn = null;
            return true;
        }
        return false;
    }

    public void sendInvite(Player man, Player target) {
        invites.add(target.getUniqueId());
        manager.sendInviteMessage(man, target, this);
    }

    public boolean acceptInvite(Player player) {
        if (invites.contains(player.getUniqueId())) {
            invites.remove(player.getUniqueId());
            return addMember(player);
        } else return false;
    }

    public boolean isInvited(Player player) {
        return invites.contains(player.getUniqueId());
    }

    public void unInvite(UUID uuid) {
        invites.remove(uuid);
    }

    public void unInviteAll() {
        invites.clear();
    }

    public String getHover() {
        return manager.trans("info.display", getDisplay()) + '\n' +
                manager.trans("info.leader", leader.getName()) + '\n' +
                manager.trans("info.level", level) + '\n' +
                manager.trans("info.fame", getFame()) + '\n' +
                manager.trans("info.balance", balance) + '\n' +
                manager.trans("info.members", getAmount(), getTeamLevel().size) + '\n' +
                manager.trans("info.managers", managers.size(), getTeamLevel().mans) + '\n' +
                manager.trans("info.description", getDescription());
    }

    public String getHomeName() {
        return "GuildHome_" + leader.getName();
    }

    public String getVariable(String params) {
        switch (params) {
            case "leader":
                return leader.getName();
            case "display":
                return getDisplay();
            case "level":
                return String.valueOf(level);
            case "fame":
                return String.valueOf(getFame());
            case "balance":
                return String.valueOf(balance);
            case "mem_amount":
                return String.valueOf(getAmount());
            case "mem_max":
                return String.valueOf(getTeamLevel().size);
            case "man_amount":
                return String.valueOf(managers.size());
            case "man_max":
                return String.valueOf(getTeamLevel().mans);
            default:
                return "no variable";
        }
    }

    public int getFame() {
        return fame + fameMap.values().stream().mapToInt(i -> i).sum();
    }

    public void setFame(int amount) {
        fame = amount;
    }

    public boolean hasFame(int amount) {
        return fame >= amount;
    }

    public void giveFame(int amount) {
        fame += amount;
    }

    public boolean takeFame(int amount) {
        return fame >= amount && (fame -= amount) >= 0;
    }

    public void setMemFame(String member, int amount) {
        fameMap.put(member, amount);
    }

    public void getMemFame(String member) {
        fameMap.getOrDefault(member, 0);
    }

    public void giveMemFame(String member, int amount) {
        fameMap.put(member, amount + fameMap.getOrDefault(member, 0));
    }

    public boolean takeMemFame(String member, int amount) {
        int fame = fameMap.getOrDefault(member, 0) - amount;
        fameMap.put(member, fame < 0 ? 0 : fame);
        return fame >= 0;
    }

    public double getEco() {
        return balance;
    }

    public void setEco(double amount) {
        balance = amount;
    }

    public boolean hasEco(double amount) {
        return manager.ignoreNoEco || balance >= amount;
    }

    public void giveEco(double amount) {
        balance += amount;
    }

    public boolean takeEco(double amount) {
        return balance >= amount ? (balance -= amount) >= 0 : manager.ignoreNoEco;
    }

    public void attornTo(Player player, boolean leave) {
        String oldHome = getHomeName();
        String oldLeader = getTeamLeader();
        setLeader(player);
        resetAttorn();
        if (leave) {
            unsetManager(oldLeader);
            delMember(oldLeader);
        } else addMember(oldLeader);
        renameHome(oldHome, getHomeName(), oldLeader, getTeamLeader());
    }

    public void teamChat(Player source, String message) {
        if (source != null) message = "[" + getDisplay() + "][" + source.getName() + "] " + message;
        else message = "[" + getDisplay() + "] " + message;
        Player player = Bukkit.getPlayer(leader.getUniqueId());
        if (player != null) player.sendMessage(message);
        for (String mem : managers) {
            player = Bukkit.getPlayer(mem);
            if (player != null) player.sendMessage(message);
        }
        for (String mem : members) {
            player = Bukkit.getPlayer(mem);
            if (player != null) player.sendMessage(message);
        }
    }

    private void renameHome(String oldName, String newName, String oldLeader, String newLeader) {
        if (residenceApi) {
            Residence plugin = Residence.getInstance();
            ResidenceManager apiR = plugin.getResidenceManager();
            ClaimedResidence home = apiR.getByName(oldName);
            if (home != null) {
                //ResidenceRenameEvent event = new ResidenceRenameEvent(home, newName, oldName);
                //Bukkit.getPluginManager().callEvent(event);
                apiR.removeChunkList(oldName);
                home.setName(newName);
                try {
                    Field residences = ResidenceManager.class.getDeclaredField("residences");
                    residences.setAccessible(true);
                    Map map = (Map) residences.get(apiR);
                    map.put(newName.toLowerCase(), home);
                    map.remove(oldName.toLowerCase());
                    apiR.calculateChunks(newName);
                    plugin.getSignUtil().updateSignResName(home);
                } catch (Throwable e) {
                    e.printStackTrace();
                }
                ResidencePermissions perm = home.getPermissions();
                perm.removeAllPlayerFlags(oldLeader);
                perm.removeAllPlayerFlags(newLeader);
                perm.setPlayerFlag(newLeader, "admin", FlagPermissions.FlagState.TRUE);
            }
        }
    }

    public void convoke(String message) {
        for (String man : managers) {
            Player player = Bukkit.getPlayer(man);
            if (player != null) manager.sendConvoke(player, message);
        }
        for (String mem : members) {
            Player player = Bukkit.getPlayer(mem);
            if (player != null) manager.sendConvoke(player, message);
        }
    }

    public UUID getUUID() {
        return leader.getUniqueId();
    }

    public boolean canGetBonus() {
        return System.currentTimeMillis() / 86400000 > lastBonus;
    }

    public void updateLastBonus() {
        lastBonus = System.currentTimeMillis() / 86400000;
    }
}
