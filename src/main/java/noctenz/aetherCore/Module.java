package noctenz.aetherCore;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;

public interface Module {
    void onEnable(AetherCore plugin);
    void onDisable();

    default void initConfig(File configFolder) {}
}