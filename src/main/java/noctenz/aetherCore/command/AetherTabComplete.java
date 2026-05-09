package noctenz.aetherCore.command;

import noctenz.aetherCore.AetherCore;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;

import java.util.ArrayList;
import java.util.List;

public class AetherTabComplete implements TabCompleter {

    private final AetherCore plugin;

    public AetherTabComplete(AetherCore plugin) {
        this.plugin = plugin;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        List<String> list = new ArrayList<>();

        if (args.length == 1) {
            list.add("reload");
            list.add("check");
            list.add("anvilrename");
        }

        if (args.length == 2 && args[0].equalsIgnoreCase("anvilrename")) {
            list.add("blacklist");
            list.add("remove");
            list.add("list");
        }

        if (args.length >= 3
                && args[0].equalsIgnoreCase("anvilrename")
                && args[1].equalsIgnoreCase("remove")) {
            plugin.getAnvilRenameManager()
                    .getBlacklist()
                    .forEach(s -> list.add(s));
        }
        return list;
    }
}