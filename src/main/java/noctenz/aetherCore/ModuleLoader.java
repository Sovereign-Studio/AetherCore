package noctenz.aetherCore;

import java.io.File;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.List;
import java.util.ServiceLoader;

public class ModuleLoader {

    private final AetherCore plugin;
    private final List<LoadedModule> loadedModules = new ArrayList<>();

    public ModuleLoader(AetherCore plugin) {
        this.plugin = plugin;
    }

    public List<LoadedModule> getLoadedModules() {
        return loadedModules;
    }

    public void loadModules() {
        File folder = new File(plugin.getDataFolder(), "Module");

        if (!folder.exists()) folder.mkdirs();

        File[] files = folder.listFiles((dir, name) -> name.endsWith(".jar"));

        if (files == null) {
            plugin.getLogger().warning("No module folder found.");
            return;
        }

        for (File file : files) {
            try {
                plugin.getLogger().info("Loading module jar: " + file.getName());
                URLClassLoader loader = new URLClassLoader(
                        new URL[]{file.toURI().toURL()},
                        plugin.getClass().getClassLoader()
                );
                ServiceLoader<Module> serviceLoader =
                        ServiceLoader.load(Module.class, loader);
                int count = 0;

                for (Module module : serviceLoader) {
                    plugin.getLogger().info("Found module class: " + module.getClass().getName());

                    module.onEnable(plugin);
                    loadedModules.add(new LoadedModule(module, loader));

                    plugin.getAnvilRenameManager().registerFromModule(module);

                    plugin.getLogger().info("Module enabled: " + module.getNames());
                    count++;
                }

                if (count == 0) {
                    plugin.getLogger().warning("No Module found in " + file.getName());
                    loader.close();
                }

            } catch (Exception e) {
                plugin.getLogger().severe("Failed to load module: " + file.getName());
                e.printStackTrace();
            }
        }
    }

    public void reloadModules() {
        plugin.getLogger().info("Reloading modules...");
        for (LoadedModule loaded : loadedModules) {
            try {
                loaded.getModule().onDisable();
                loaded.getClassLoader().close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        loadedModules.clear();
        loadModules();
        plugin.getLogger().info("Modules reloaded.");
    }

    public void unloadModules() {
        plugin.getLogger().info("Unloading modules...");
        for (LoadedModule loaded : loadedModules) {
            try {
                loaded.getModule().onDisable();
                loaded.getClassLoader().close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        loadedModules.clear();
    }
}