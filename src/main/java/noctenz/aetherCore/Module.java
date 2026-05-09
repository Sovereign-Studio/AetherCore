package noctenz.aetherCore;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;

public interface Module {
    List<String> getNames();
    void onEnable(AetherCore plugin);
    void onDisable();

    default String getName() {
        return getNames().isEmpty() ? "" : getNames().get(0);
    }

    default void initConfig(File configFolder) {}
}