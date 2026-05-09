package noctenz.aetherCore.command;

import noctenz.aetherCore.AetherCore;
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
            sender.sendMessage(ChatColor.RED + "You do not have permission.");
            return true;
        }

        if (args.length == 0) {
            sender.sendMessage("§6AetherCore Commands");
            sender.sendMessage("§e/ac reload");
            sender.sendMessage("§e/ac check");
            sender.sendMessage("§e/ac anvilrename blacklist <name>");
            sender.sendMessage("§e/ac anvilrename remove <name>");
            sender.sendMessage("§e/ac anvilrename list");
            return true;
        }

        if (args[0].equalsIgnoreCase("reload")) {
            plugin.reloadConfig();
            plugin.getAnvilRenameManager().load();
            plugin.getModuleLoader().reloadModules();
            sender.sendMessage("§aModules and config reloaded.");
            return true;
        }

        if (args[0].equalsIgnoreCase("check")) {
            String modules = plugin.getModuleLoader()
                    .getLoadedModules()
                    .stream()
                    .map(m -> m.getModule().getName())
                    .collect(Collectors.joining("§7, §a"));
            sender.sendMessage("§6Loaded Modules: §a" + modules);
            return true;
        }

        if (args[0].equalsIgnoreCase("anvilrename")) {

            if (args.length < 2) {
                sender.sendMessage("§e/ac anvilrename blacklist <name>");
                sender.sendMessage("§e/ac anvilrename remove <name>");
                sender.sendMessage("§e/ac anvilrename list");
                return true;
            }

            if (args[1].equalsIgnoreCase("blacklist")) {
                if (args.length < 3) {
                    sender.sendMessage("§cYou must enter a name.");
                    return true;
                }
                String name = String.join(" ", Arrays.copyOfRange(args, 2, args.length));
                plugin.getAnvilRenameManager().add(name);
                sender.sendMessage("§aAdded to blacklist: §f" + name);
                return true;
            }

            if (args[1].equalsIgnoreCase("remove")) {
                if (args.length < 3) {
                    sender.sendMessage("§cYou must enter a name.");
                    return true;
                }
                String name = String.join(" ", Arrays.copyOfRange(args, 2, args.length));
                plugin.getAnvilRenameManager().remove(name);
                sender.sendMessage("§cRemoved from blacklist: §f" + name);
                return true;
            }

            if (args[1].equalsIgnoreCase("list")) {
                String list = plugin.getAnvilRenameManager()
                        .getBlacklist()
                        .stream()
                        .collect(Collectors.joining("§7, §c"));
                sender.sendMessage("§6Anvil Rename Blacklist: §c" + list);
                return true;
            }
        }
        return true;
    }
}