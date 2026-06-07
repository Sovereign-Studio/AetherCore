package noctenz.aetherCore.commands;

import noctenz.aetherCore.AetherCore;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

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
            list.add("actionbar");
            list.add("updateitem");
        }

        if (args.length == 2 && args[0].equalsIgnoreCase("actionbar")) {
            list.add("on");
            list.add("off");
        }

        if (args.length == 2 && args[0].equalsIgnoreCase("updateitem")) {
            list.addAll(Bukkit.getOnlinePlayers().stream().map(Player::getName).collect(Collectors.toList()));
        }

        return list;
    }
}