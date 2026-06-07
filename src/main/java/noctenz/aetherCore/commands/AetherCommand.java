package noctenz.aetherCore.commands;

import noctenz.aetherCore.AetherCore;
import noctenz.aetherCore.listeners.MMOItemUpdateListener;
import noctenz.aetherCore.utils.ColorUtil;
import noctenz.aetherCore.utils.MessagesPrefix;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.concurrent.atomic.AtomicInteger;
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
            sender.sendMessage("§e/ac reload");
            sender.sendMessage("§e/ac check");
            sender.sendMessage("§e/ac actionbar <on/off>");
            sender.sendMessage("§e/ac updateitem [player]");
            return true;
        }

        if (args[0].equalsIgnoreCase("reload")) {
            plugin.reloadConfig();
            plugin.getModuleLoader().reloadModules();
            sender.sendMessage(ColorUtil.color(MessagesPrefix.PREFIX + "§aModules and config reloaded."));
            return true;
        }

        if (args[0].equalsIgnoreCase("check")) {
            String modules = plugin.getModuleLoader()
                    .getLoadedModules()
                    .stream()
                    .map(m -> m.getModule().getClass().getSimpleName())
                    .collect(Collectors.joining("§7, §a"));
            sender.sendMessage(ColorUtil.color(MessagesPrefix.PREFIX + "§6Loaded Modules: §a" + modules));
            return true;
        }

        if (args[0].equalsIgnoreCase("actionbar")) {
            if (args.length < 2) {
                sender.sendMessage("§e/ac actionbar <on/off>");
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

        if (args[0].equalsIgnoreCase("updateitem")) {
            if (args.length >= 2) {
                Player target = Bukkit.getPlayer(args[1]);
                if (target == null) {
                    sender.sendMessage(ColorUtil.color(MessagesPrefix.PREFIX + "§cPlayer §f" + args[1] + " §cnot found or offline."));
                    return true;
                }
                MMOItemUpdateListener.updateAllItems(target);
                sender.sendMessage(ColorUtil.color(MessagesPrefix.PREFIX + "§aUpdated MMOItems for §f" + target.getName() + "§a."));
                return true;
            }

            sender.sendMessage(ColorUtil.color(MessagesPrefix.PREFIX + "§6Updating MMOItems for all online players..."));
            AtomicInteger count = new AtomicInteger(0);

            Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
                for (Player online : Bukkit.getOnlinePlayers()) {
                    Bukkit.getScheduler().runTask(plugin, () -> {
                        MMOItemUpdateListener.updateAllItems(online);
                        count.incrementAndGet();
                    });
                }
                Bukkit.getScheduler().runTaskLater(plugin, () ->
                        sender.sendMessage(ColorUtil.color(
                                MessagesPrefix.PREFIX + "§aDone! Updated §f" + count.get()
                                        + " §aonline player(s). §7Offline players update on next login."
                        )), 20L);
            });
            return true;
        }

        return true;
    }
}