package noctenz.aetherCore.anvil;

import noctenz.aetherCore.utils.ColorUtil;
import noctenz.aetherCore.Module;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class AnvilRenameManager {

    private final JavaPlugin plugin;
    private final Set<String> blacklist = new HashSet<>();

    public AnvilRenameManager(JavaPlugin plugin) {
        this.plugin = plugin;
        load();
    }

    public void load() {
        plugin.saveDefaultConfig();
        FileConfiguration config = plugin.getConfig();

        blacklist.clear();

        List<String> list = config.getStringList("anvilRenameBlacklist");

        for (String s : list) {
            blacklist.add(ColorUtil.color(s));
        }
    }

    public void save() {
        plugin.getConfig().set("anvilRenameBlacklist", blacklist.stream().toList());
        plugin.saveConfig();
    }

    public boolean isBlacklisted(String name) {
        for (String s : blacklist) {
            if (ChatColor.stripColor(name)
                    .equalsIgnoreCase(ChatColor.stripColor(s))) {
                return true;
            }
        }
        return false;
    }

    public void registerFromModule(Module module) {
        for (String name : module.getNames()) {
            if (name == null || name.isBlank()) continue;
            blacklist.add(ColorUtil.color(name));
            plugin.getLogger().info("[AnvilRename] Auto-registered: " + name);
        }
        save();
    }

    public void add(String name) {
        blacklist.add(ColorUtil.color(name));
        save();
    }

    public void remove(String name) {
        blacklist.removeIf(s ->
                ChatColor.stripColor(s)
                        .equalsIgnoreCase(ChatColor.stripColor(name)));
        save();
    }
    public Set<String> getBlacklist() {
        return blacklist;
    }
}