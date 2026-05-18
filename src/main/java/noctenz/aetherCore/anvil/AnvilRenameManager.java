package noctenz.aetherCore.anvil;

import noctenz.aetherCore.utils.ColorUtil;
import noctenz.aetherCore.Module;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class AnvilRenameManager {

    private final JavaPlugin plugin;
    private final Set<String> blacklist = new HashSet<>();

    private File anvilFile;
    private FileConfiguration anvilConfig;

    public AnvilRenameManager(JavaPlugin plugin) {
        this.plugin = plugin;
        setupFile();
        load();
    }

    private void setupFile() {
        anvilFile = new File(plugin.getDataFolder(), "anvil.yml");
        if (!anvilFile.exists()) {
            try {
                plugin.getDataFolder().mkdirs();
                anvilFile.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        anvilConfig = YamlConfiguration.loadConfiguration(anvilFile);
    }

    public void load() {
        anvilConfig = YamlConfiguration.loadConfiguration(anvilFile);
        blacklist.clear();
        List<String> list = anvilConfig.getStringList("anvilRenameBlacklist");
        for (String s : list) {
            blacklist.add(ColorUtil.color(s));
        }
    }

    public void save() {
        anvilConfig.set("anvilRenameBlacklist", blacklist.stream().toList());
        try {
            anvilConfig.save(anvilFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public boolean isBlacklisted(String name) {
        String strippedInput = stripAll(name);
        for (String entry : blacklist) {
            String strippedEntry = stripAll(entry);
            if (strippedInput.equalsIgnoreCase(strippedEntry)) return true;
            if (strippedInput.toLowerCase().startsWith(strippedEntry.toLowerCase())) return true;
        }
        return false;
    }

    private String stripAll(String text) {
        if (text == null) return "";
        String s = ChatColor.stripColor(text);
        s = s.replaceAll("[§&][0-9a-fk-orA-FK-OR]", "");
        s = s.replaceAll("\\[.*?]", "").trim();
        return s;
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