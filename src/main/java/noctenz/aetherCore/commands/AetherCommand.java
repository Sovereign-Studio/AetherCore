package noctenz.aetherCore.commands;

import noctenz.aetherCore.AetherCore;
import noctenz.aetherCore.utils.ColorUtil;
import noctenz.aetherCore.utils.MessagesPrefix;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import java.util.Arrays;
import java.util.stream.Collectors;

public class AetherCommand implements CommandExecutor {

    private final AetherCore plugin;

    public AetherCommand(AetherCore plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if (!sender.hasPermission("aethercore.admin")) {
            sender.sendMessage(ColorUtil.color(MessagesPrefix.PREFIX + ChatColor.RED + "You do not have permission."));
            return true;
        }

        if (args.length == 0) {
            sender.sendMessage("§6§lAetherCore Commands");
            sender.sendMessage("§e/aethercore reload");
            sender.sendMessage("§e/aethercore check");
            sender.sendMessage("§e/aethercore actionbar <on/off>");
            sender.sendMessage("§e/aethercore anvilrename blacklist <name>");
            sender.sendMessage("§e/aethercore anvilrename remove <name>");
            sender.sendMessage("§e/aethercore anvilrename list");
            return true;
        }

        if (args[0].equalsIgnoreCase("reload")) {
            plugin.reloadConfig();
            plugin.getAnvilRenameManager().load();
            plugin.getModuleLoader().reloadModules();
            sender.sendMessage(ColorUtil.color(MessagesPrefix.PREFIX + "§aModules and config reloaded."));
            return true;
        }

        if (args[0].equalsIgnoreCase("check")) {
            String modules = plugin.getModuleLoader()
                    .getLoadedModules()
                    .stream()
                    .map(m -> m.getModule().getName())
                    .collect(Collectors.joining("§7, §a"));
            sender.sendMessage(ColorUtil.color(MessagesPrefix.PREFIX + "§6Loaded Modules: §a" + modules));
            return true;
        }

        if (args[0].equalsIgnoreCase("actionbar")) {
            if (args.length < 2) {
                sender.sendMessage("§e/aethercore actionbar <on/off>");
                sender.sendMessage("§6Current status: §a" + (plugin.getActionBar().isActionBarEnabled() ? "ON" : "OFF"));
                return true;
            }

            if (args[1].equalsIgnoreCase("on")) {
                plugin.getActionBar().setActionBarEnabled(true);
                sender.sendMessage(ColorUtil.color(MessagesPrefix.PREFIX + "§aActionBar enabled."));
                return true;
            }

            if (args[1].equalsIgnoreCase("off")) {
                plugin.getActionBar().setActionBarEnabled(false);
                sender.sendMessage(ColorUtil.color(MessagesPrefix.PREFIX + "§cActionBar disabled."));
                return true;
            }

            sender.sendMessage(ColorUtil.color(MessagesPrefix.PREFIX + "§cInvalid option. Use §e/on §cor §e/off"));
            return true;
        }

        if (args[0].equalsIgnoreCase("anvilrename")) {

            if (args.length < 2) {
                sender.sendMessage("§e/aethercore anvilrename blacklist <name>");
                sender.sendMessage("§e/aethercore anvilrename remove <name>");
                sender.sendMessage("§e/aethercore anvilrename list");
                return true;
            }

            if (args[1].equalsIgnoreCase("blacklist")) {
                if (args.length < 3) {
                    sender.sendMessage(ColorUtil.color(MessagesPrefix.PREFIX + "§cYou must enter a name."));
                    return true;
                }
                String name = String.join(" ", Arrays.copyOfRange(args, 2, args.length));
                plugin.getAnvilRenameManager().add(name);
                sender.sendMessage(ColorUtil.color(MessagesPrefix.PREFIX + "§aAdded to blacklist: §f" + name));
                return true;
            }

            if (args[1].equalsIgnoreCase("remove")) {
                if (args.length < 3) {
                    sender.sendMessage(ColorUtil.color(MessagesPrefix.PREFIX + "§cYou must enter a name."));
                    return true;
                }
                String name = String.join(" ", Arrays.copyOfRange(args, 2, args.length));
                plugin.getAnvilRenameManager().remove(name);
                sender.sendMessage(ColorUtil.color(MessagesPrefix.PREFIX + "§cRemoved from blacklist: §f" + name));
                return true;
            }

            if (args[1].equalsIgnoreCase("list")) {
                String list = plugin.getAnvilRenameManager()
                        .getBlacklist()
                        .stream()
                        .collect(Collectors.joining("§7, §c"));
                sender.sendMessage(ColorUtil.color(MessagesPrefix.PREFIX + "§6Anvil Rename Blacklist: §c" + list));
                return true;
            }
        }
        return true;
    }
}