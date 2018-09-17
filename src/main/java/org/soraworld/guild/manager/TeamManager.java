package org.soraworld.guild.manager;

import net.minecraft.server.v1_7_R4.*;
import org.bukkit.command.CommandSender;
import org.bukkit.craftbukkit.v1_7_R4.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.soraworld.guild.core.TeamGuild;
import org.soraworld.guild.core.TeamLevel;
import org.soraworld.guild.economy.Economy;
import org.soraworld.guild.flans.Flans;
import org.soraworld.hocon.node.FileNode;
import org.soraworld.hocon.node.Setting;
import org.soraworld.violet.manager.SpigotManager;
import org.soraworld.violet.plugin.SpigotPlugin;
import org.soraworld.violet.util.ChatColor;

import javax.annotation.Nonnull;
import java.nio.file.Path;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.soraworld.guild.core.TeamGuild.deserialize;
import static org.soraworld.guild.core.TeamGuild.serialize;

public class TeamManager extends SpigotManager {

    @Setting(comment = "comment.ecoType")
    private String ecoType = "Vault";
    @Setting(comment = "comment.teamPvP")
    private boolean teamPvP = false;
    @Setting(comment = "comment.maxDisplay")
    private int maxDisplay = 10;
    @Setting(comment = "comment.maxDescription")
    private int maxDescription = 100;
    @Setting(comment = "comment.levels")
    private TreeSet<TeamLevel> levels = new TreeSet<>();

    private final Path guildFile;
    private final HashMap<String, TeamGuild> teams = new HashMap<>();
    private final HashMap<String, TeamGuild> guilds = new HashMap<>();
    private final TreeSet<TeamGuild> rank = new TreeSet<>();

    private static final Pattern FORMAT = Pattern.compile("((?<![&|\\u00A7])[&|\\u00A7][0-9a-fk-or])+");

    public TeamManager(SpigotPlugin plugin, Path path) {
        super(plugin, path);
        options.registerType(new TeamLevel());
        guildFile = path.resolve("guild.conf");
    }

    public boolean save() {
        if (levels.isEmpty()) levels.add(new TeamLevel(5, 10, 1, false));
        saveGuild();
        return super.save();
    }

    @Nonnull
    public ChatColor defChatColor() {
        return ChatColor.AQUA;
    }

    public void afterLoad() {
        loadGuild();
        if (levels.isEmpty()) levels.add(new TeamLevel(5, 10, 1, false));
        Economy.checkEconomy(this);
        Flans.checkFlans(this);
    }

    public boolean checkEcoType(String type) {
        return ecoType.equals(type);
    }

    public boolean isTeamPvP() {
        return teamPvP;
    }

    public int maxDisplay() {
        return maxDisplay;
    }

    public int maxDescription() {
        return maxDescription;
    }

    public void saveGuild() {
        FileNode node = new FileNode(guildFile.toFile(), options);
        for (Map.Entry<String, TeamGuild> entry : guilds.entrySet()) {
            node.set(entry.getKey(), serialize(entry.getValue(), options));
        }
        try {
            node.save();
        } catch (Exception e) {
            if (isDebug()) e.printStackTrace();
            console(ChatColor.RED + "&cGuild file save exception !!!");
        }
    }

    public void loadGuild() {
        FileNode node = new FileNode(guildFile.toFile(), options);
        try {
            node.load(false);
            rank.clear();
            teams.clear();
            guilds.clear();
            for (String leader : node.keys()) {
                TeamGuild guild = deserialize(node.get(leader), leader);
                if (guild != null) {
                    getLevel(guild);
                    rank.add(guild);
                    guilds.put(leader, guild);
                }
            }
        } catch (Exception e) {
            if (isDebug()) e.printStackTrace();
            console(ChatColor.RED + "Guild file load exception !!!");
        }
    }

    public void clearPlayer(String player) {
        teams.remove(player);
    }

    public List<String> getGuilds() {
        return new ArrayList<>(guilds.keySet());
    }

    public void createGuild(Player player, String display) {
        String username = player.getName();
        TeamGuild guild = teams.get(username);
        if (guild != null) {
            if (guild.isLeader(username)) sendKey(player, "alreadyLeader");
            else sendKey(player, "alreadyInTeam");
            return;
        }
        guild = new TeamGuild(username, levels.first().size);
        guild.setDisplay(display);
        guild.setDescription(username + "'s Team.");
        if (Economy.takeEco(username, getLevel(guild).cost)) {
            rank.add(guild);
            teams.put(username, guild);
            guilds.put(username, guild);
            sendKey(player, "createTeamSuccess", getLevel(guild).cost);
            saveGuild();
        } else {
            sendKey(player, "noEnoughEco", getLevel(guild).cost);
        }
    }

    public TeamLevel getLevel(TeamGuild guild) {
        for (TeamLevel level : levels) {
            if (guild.size() <= level.size) {
                guild.setSize(level.size);
                return level;
            }
        }
        guild.setSize(levels.last().size);
        return levels.last();
    }

    public void joinGuild(Player player, String leader) {
        String username = player.getName();
        TeamGuild guild = teams.get(username);
        if (guild != null) {
            sendKey(player, "alreadyInTeam");
            return;
        }
        guild = guilds.get(leader);
        if (guild == null) {
            sendKey(player, "guildNotExist");
        } else {
            if (guild.hasMember(username)) {
                sendKey(player, "alreadyJoined");
            } else {
                guild.addJoinApplication(username);
                sendKey(player, "sendApplication");
                saveGuild();
            }
        }
    }

    public void leaveGuild(String player, TeamGuild guild) {
        guild.delMember(player);
        teams.remove(player);
        saveGuild();
    }

    public TeamGuild getGuild(String leader) {
        return guilds.get(leader);
    }

    public TeamGuild fetchTeam(String player) {
        TeamGuild team = teams.get(player);
        if (team == null) {
            for (TeamGuild guild : guilds.values()) {
                if (guild.hasMember(player)) {
                    team = guild;
                    teams.put(player, team);
                    break;
                }
            }
        }
        return team;
    }

    public void upgrade(Player player) {
        TeamGuild guild = guilds.get(player.getName());
        if (guild == null) {
            sendKey(player, "ownNoGuild");
            return;
        }
        TeamLevel next = levels.higher(getLevel(guild));
        if (next != null) {
            if (Economy.takeEco(player.getName(), next.cost)) {
                rank.remove(guild);
                guild.setSize(next.size);
                rank.add(guild);
                sendKey(player, "guildUpgraded", next.cost);
                saveGuild();
            } else sendKey(player, "noEnoughEco", next.cost);
        } else sendKey(player, "guildIsTopLevel");
    }

    public void showRank(CommandSender sender, int page) {
        if (page < 1) page = 1;
        sendKey(sender, "rankHead");
        Iterator<TeamGuild> it = rank.iterator();
        for (int i = 1; i <= page * 10 && it.hasNext(); i++) {
            TeamGuild guild = it.next();
            if (i >= page * 10 - 9) {
                sendKey(sender, "rankLine", i, guild.getDisplay(), guild.getLeader());
            }
        }
        sendKey(sender, "rankFoot", page, rank.size() / 10 + 1);
    }

    public void disband(final TeamGuild guild, String leader) {
        rank.remove(guild);
        guilds.remove(leader);
        // TODO 通知在线的成员
        teams.entrySet().removeIf(next -> guild == next.getValue());
        saveGuild();
    }

    private static ChatModifier parseStyle(String text) {
        ChatModifier style = new ChatModifier();
        int length = text.length();
        for (int i = 1; i < length; i += 2) {
            switch (text.charAt(i)) {
                case '0':
                    style.setColor(EnumChatFormat.BLACK);
                    break;
                case '1':
                    style.setColor(EnumChatFormat.DARK_BLUE);
                    break;
                case '2':
                    style.setColor(EnumChatFormat.DARK_GREEN);
                    break;
                case '3':
                    style.setColor(EnumChatFormat.DARK_AQUA);
                    break;
                case '4':
                    style.setColor(EnumChatFormat.DARK_RED);
                    break;
                case '5':
                    style.setColor(EnumChatFormat.DARK_PURPLE);
                    break;
                case '6':
                    style.setColor(EnumChatFormat.GOLD);
                    break;
                case '7':
                    style.setColor(EnumChatFormat.GRAY);
                    break;
                case '8':
                    style.setColor(EnumChatFormat.DARK_GRAY);
                    break;
                case '9':
                    style.setColor(EnumChatFormat.BLUE);
                    break;
                case 'a':
                    style.setColor(EnumChatFormat.GREEN);
                    break;
                case 'b':
                    style.setColor(EnumChatFormat.AQUA);
                    break;
                case 'c':
                    style.setColor(EnumChatFormat.RED);
                    break;
                case 'd':
                    style.setColor(EnumChatFormat.LIGHT_PURPLE);
                    break;
                case 'e':
                    style.setColor(EnumChatFormat.YELLOW);
                    break;
                case 'f':
                    style.setColor(EnumChatFormat.WHITE);
                    break;
                case 'k':
                    style.setRandom(true);
                    break;
                case 'l':
                    style.setBold(true);
                    break;
                case 'm':
                    style.setStrikethrough(true);
                    break;
                case 'n':
                    style.setUnderline(true);
                    break;
                case 'o':
                    style.setItalic(true);
                    break;
                default:
                    style = new ChatModifier();
            }
        }
        return style;
    }

    public static IChatBaseComponent format(String text) {
        return format(text, null, null, null, null);
    }

    public static IChatBaseComponent format(String text, EnumClickAction ca, String cv, EnumHoverAction ha, String hv) {
        Matcher matcher = FORMAT.matcher(text);
        IChatBaseComponent component = new ChatComponentText("");
        int head = 0;
        ChatModifier style = new ChatModifier();
        while (matcher.find()) {
            component.addSibling(new ChatComponentText(text.substring(head, matcher.start()).replaceAll("&&", "&")).setChatModifier(style));
            style = parseStyle(matcher.group());
            head = matcher.end();
        }
        component.addSibling(new ChatComponentText(text.substring(head).replaceAll("&&", "&")).setChatModifier(style));
        if (ca != null && cv != null) {
            component.getChatModifier().setChatClickable(new ChatClickable(ca, cv));
        }
        if (ha != null && hv != null) {
            component.getChatModifier().a(new ChatHoverable(ha, format(hv)));
        }
        return component;
    }

    public void sendMessage(Player player, IChatBaseComponent... siblings) {
        EntityPlayer handle = ((CraftPlayer) player).getHandle();
        IChatBaseComponent text = new ChatComponentText(colorHead);
        for (IChatBaseComponent component : siblings) text.addSibling(component);
        handle.b(text);
    }

    public void sendHandleMessage(Player handler, String applicant) {
        sendMessage(handler,
                format(trans("handleText", applicant)),
                format(trans("acceptText"),
                        EnumClickAction.RUN_COMMAND, "/guild accept " + applicant,
                        EnumHoverAction.SHOW_TEXT, trans("acceptHover")),
                format(trans("rejectText"),
                        EnumClickAction.RUN_COMMAND, "/guild reject " + applicant,
                        EnumHoverAction.SHOW_TEXT, trans("rejectHover"))
        );
    }
}
