package org.soraworld.guild.manager;

import net.minecraft.server.v1_7_R4.*;
import org.bukkit.Bukkit;
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
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.function.Consumer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static net.minecraft.server.v1_7_R4.EnumClickAction.RUN_COMMAND;
import static net.minecraft.server.v1_7_R4.EnumHoverAction.SHOW_TEXT;
import static org.soraworld.guild.core.TeamGuild.deserialize;
import static org.soraworld.guild.core.TeamGuild.serialize;

public class TeamManager extends SpigotManager {

    @Setting(comment = "comment.ecoType")
    private String ecoType = "Vault";
    @Setting(comment = "comment.ignoreNoEco")
    public boolean ignoreNoEco = false;
    @Setting(comment = "comment.teamPvP")
    public boolean teamPvP = false;
    @Setting(comment = "comment.attornLeave")
    private boolean attornLeave = false;
    @Setting(comment = "comment.maxDisplay")
    public int maxDisplay = 10;
    @Setting(comment = "comment.maxDescription")
    public int maxDescription = 100;
    @Setting(comment = "comment.residencePrice")
    public float residencePrice = 1.0F;
    @Setting(comment = "comment.textCommand")
    private String textCommand = "/team";
    @Setting(comment = "comment.dailyBonus")
    private HashMap<Integer, ArrayList<String>> dailyBonus = new HashMap<>();
    @Setting(comment = "comment.levels")
    private TreeMap<Integer, TeamLevel> levels = new TreeMap<>();

    private final Path guildFile;
    private final HashMap<UUID, TeamGuild> teams = new HashMap<>();
    private final HashMap<String, TeamGuild> guilds = new HashMap<>();
    private final TreeSet<TeamGuild> rank = new TreeSet<>();

    private static final Pattern FORMAT = Pattern.compile("((?<![&|\\u00A7])[&|\\u00A7][0-9a-fk-or])+");
    private static final TeamLevel defaultLevel = new TeamLevel(5, 10, 1);

    public TeamManager(SpigotPlugin plugin, Path path) {
        super(plugin, path);
        options.registerType(defaultLevel);
        guildFile = path.resolve("guilds.conf");
    }

    public boolean save() {
        if (!levels.containsKey(0)) levels.put(0, defaultLevel);
        saveGuild();
        return super.save();
    }

    @Nonnull
    public ChatColor defChatColor() {
        return ChatColor.AQUA;
    }

    public void afterLoad() {
        loadGuild();
        if (!levels.containsKey(0)) levels.put(0, defaultLevel);
        Economy.checkEconomy(this, ecoType, ignoreNoEco);
        Flans.checkFlans(this);
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
        if (Files.notExists(guildFile)) {
            saveGuild();
            return;
        }
        FileNode node = new FileNode(guildFile.toFile(), options);
        try {
            node.load(false);
            rank.clear();
            teams.clear();
            guilds.clear();
            for (String leader : node.keys()) {
                TeamGuild guild = deserialize(node.get(leader), leader, this);
                if (guild != null) {
                    rank.add(guild);
                    guilds.put(leader, guild);
                }
            }
        } catch (Exception e) {
            if (isDebug()) e.printStackTrace();
            console(ChatColor.RED + "Guild file load exception !!!");
        }
    }

    public void clearPlayer(Player player) {
        teams.remove(player.getUniqueId());
    }

    public void createGuild(Player player, String display) {
        TeamGuild guild = teams.get(player.getUniqueId());
        if (guild == null) {
            guild = new TeamGuild(player, 0, this);
            guild.setDisplay(display);
            guild.setDescription(player.getName() + "'s Team.");
            int cost = levels.firstEntry().getValue().cost;
            if (Economy.takeEco(player, cost)) {
                rank.add(guild);
                teams.put(player.getUniqueId(), guild);
                guilds.put(player.getName(), guild);
                sendKey(player, "create.success", cost);
                saveGuild();
            } else sendKey(player, "create.noEco", cost);
        } else sendKey(player, "create.inTeam");
    }

    public TeamLevel getLevel(int level) {
        return levels.getOrDefault(level, levels.firstEntry().getValue());
    }

    public void joinGuild(Player player, String leader) {
        TeamGuild guild = teams.get(player.getUniqueId());
        if (guild == null) {
            guild = guilds.get(leader);
            if (guild != null) {
                if (!guild.hasMember(player)) {
                    if (!guild.isBlack(player.getName())) {
                        guild.addJoinApplication(player.getName());
                        sendKey(player, "application.send", guild.getDisplay());
                        saveGuild();
                    } else send(player, "You are in the Team's Blacklist.");
                } else sendKey(player, "player.alreadyJoined");
            } else sendKey(player, "guild.notExist");
        } else if (guild.equals(guilds.get(leader))) {
            sendKey(player, "player.alreadyJoined");
        } else sendKey(player, "player.inAnother");
    }

    public void leaveGuild(Player player, TeamGuild guild) {
        guild.delMember(player.getName());
        teams.remove(player.getUniqueId());
        saveGuild();
    }

    public void leaveGuild(String player, TeamGuild guild) {
        guild.delMember(player);
        teams.remove(Bukkit.getOfflinePlayer(player).getUniqueId());
        saveGuild();
    }

    public TeamGuild getGuild(String leader) {
        return guilds.get(leader);
    }

    public TeamGuild fetchTeam(Player player) {
        TeamGuild team = teams.get(player.getUniqueId());
        if (team == null) {
            for (TeamGuild guild : guilds.values()) {
                if (guild.hasMember(player)) {
                    team = guild;
                    teams.put(player.getUniqueId(), team);
                    break;
                }
            }
        }
        return team;
    }

    public TeamGuild fetchTeam(String player) {
        TeamGuild team = teams.get(Bukkit.getOfflinePlayer(player).getUniqueId());
        if (team == null) {
            for (TeamGuild guild : guilds.values()) {
                if (guild.hasMember(player)) {
                    team = guild;
                    teams.put(Bukkit.getOfflinePlayer(player).getUniqueId(), team);
                    break;
                }
            }
        }
        return team;
    }

    public void upgrade(Player player) {
        TeamGuild guild = guilds.get(player.getName());
        if (guild != null) {
            Map.Entry<Integer, TeamLevel> entry = levels.higherEntry(guild.getLevel());
            if (entry != null) {
                TeamLevel next = entry.getValue();
                if (Economy.takeEco(player, next.cost)) {
                    updateGuild(guild, g -> g.setLevel(entry.getKey()));
                    sendKey(player, "guild.upgrade", next.cost);
                    saveGuild();
                } else sendKey(player, "upgrade.noEco", next.cost);
            } else sendKey(player, "guild.topLevel");
        } else sendKey(player, "player.ownNone");
    }

    public void showRank(CommandSender sender, int page) {
        if (page < 1) page = 1;
        sendKey(sender, "top.head");
        Iterator<TeamGuild> it = rank.iterator();
        for (int i = 1; i <= page * 10 && it.hasNext(); i++) {
            TeamGuild guild = it.next();
            if (i >= page * 10 - 9) {
                if (sender instanceof Player) {
                    IChatBaseComponent component = format(trans("top.line1", i));
                    component.addSibling(format(guild.getDisplay(), null, null, SHOW_TEXT, guild.getHover()));
                    component.addSibling(format(trans("top.line2", guild.getFame(), guild.getTeamLeader())));
                    if (guild.isShowTopJoin()) {
                        component.addSibling(format(trans("top.join"),
                                RUN_COMMAND, textCommand + " join " + guild.getTeamLeader(), null, null));
                    }
                    sendMessage((Player) sender, component);
                } else sendKey(sender, "top.line", i, guild.getDisplay(), guild.getFame(), guild.getTeamLeader());
            }
        }
        sendKey(sender, "top.foot", page, rank.size() / 10 + 1);
    }

    public void disband(final TeamGuild guild) {
        guilds.remove(guild.getTeamLeader());
        final String display = guild.getDisplay();
        teams.entrySet().removeIf(entry -> {
            if (guild.equals(entry.getValue())) {
                Player player = Bukkit.getPlayer(entry.getKey());
                if (player != null) sendKey(player, "guild.disband", display);
                return true;
            } else return false;
        });
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
                format(trans("application.receive", applicant)),
                format(trans("acceptText"),
                        RUN_COMMAND, textCommand + " accept join " + applicant,
                        SHOW_TEXT, trans("acceptHover")),
                format(trans("rejectText"),
                        RUN_COMMAND, textCommand + " reject join " + applicant,
                        SHOW_TEXT, trans("rejectHover"))
        );
    }

    public void updateGuild(TeamGuild guild, Consumer<TeamGuild> consumer) {
        rank.remove(guild);
        consumer.accept(guild);
        rank.add(guild);
    }

    public void sendAttornMessage(Player target, TeamGuild guild) {
        sendMessage(target,
                format(trans("attorn.receive", guild.getTeamLeader(), guild.getDisplay())),
                format(trans("acceptText"),
                        RUN_COMMAND, textCommand + " accept attorn " + guild.getTeamLeader(),
                        SHOW_TEXT, trans("acceptHover")),
                format(trans("rejectText"),
                        RUN_COMMAND, textCommand + " reject attorn " + guild.getTeamLeader(),
                        SHOW_TEXT, trans("rejectHover"))
        );
    }

    public boolean attornTo(TeamGuild guild, Player player) {
        if (guild.isAttorn(player)) {
            rank.remove(guild);
            guilds.remove(guild.getTeamLeader());
            guild.attornTo(player, attornLeave);
            guilds.put(guild.getTeamLeader(), guild);
            rank.add(guild);
            return true;
        } else return false;
    }

    public void sendInviteMessage(Player man, Player target, TeamGuild guild) {
        sendMessage(target,
                format(trans("invite.receive", man.getName(), guild.getDisplay())),
                format(trans("acceptText"),
                        RUN_COMMAND, textCommand + " accept invite " + guild.getTeamLeader(),
                        SHOW_TEXT, trans("acceptHover")),
                format(trans("rejectText"),
                        RUN_COMMAND, textCommand + " reject invite " + guild.getTeamLeader(),
                        SHOW_TEXT, trans("rejectHover"))
        );
    }

    public void sendConvoke(Player player, String message) {
        sendMessage(player,
                format(trans("convoke.message", message)),
                format(trans("gotoHome"),
                        RUN_COMMAND, textCommand + " home",
                        SHOW_TEXT, trans("gotoHome"))
        );
    }

    public int getRank(TeamGuild guild) {
        int i = 0;
        for (TeamGuild team : rank) {
            i++;
            if (team.equals(guild)) return i;
        }
        return -1;
    }

    public List<String> getDailyBonus(TeamGuild guild) {
        return dailyBonus.getOrDefault(getRank(guild), new ArrayList<>());
    }

    public void topkit(Player player) {
        TeamGuild guild = guilds.get(player.getName());
        if (guild != null) {
            if (guild.canGetBonus()) {
                for (String cmd : getDailyBonus(guild)) {
                    Bukkit.dispatchCommand(Bukkit.getConsoleSender(), cmd.replace("{leader}", player.getName()));
                }
                guild.updateLastBonus();
                saveGuild();
                sendKey(player, "dailyBonus.claim");
            } else sendKey(player, "dailyBonus.claimed");
        } else sendKey(player, "player.ownNone");
    }
}
