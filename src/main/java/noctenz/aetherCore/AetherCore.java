package noctenz.aetherCore;

import noctenz.aetherCore.anvil.AnvilRenameListener;
import noctenz.aetherCore.anvil.AnvilRenameManager;
import noctenz.aetherCore.command.AetherCommand;
import noctenz.aetherCore.command.AetherTabComplete;
import noctenz.aetherCore.util.DummyHandler;
import org.bukkit.entity.Entity;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.List;

public class AetherCore extends JavaPlugin {

    private ModuleLoader moduleLoader;
    private AnvilRenameManager anvilRenameManager;

    public final List<Entity> entityList = new ArrayList<>();

    @Override
    public void onEnable() {
        getLogger().info("Starting AetherCore...");

        /* Dummy System */
        DummyHandler dummyHandler = new DummyHandler(this);
        getServer().getPluginManager().registerEvents(dummyHandler, this);
        if (getCommand("spawndummy") != null) {
            getCommand("spawndummy").setExecutor(dummyHandler);
        }

        /* Anvil Rename System */
        anvilRenameManager = new AnvilRenameManager(this);
        getServer().getPluginManager().registerEvents(
                new AnvilRenameListener(anvilRenameManager),
                this
        );

        /* Module Loader */
        moduleLoader = new ModuleLoader(this);
        moduleLoader.loadModules();

        /* Main Command */
        if (getCommand("ac") != null) {
            getCommand("ac").setExecutor(new AetherCommand(this));
            getCommand("ac").setTabCompleter(new AetherTabComplete(this));
        }
        getLogger().info("AetherCore started successfully.");
    }
    public AnvilRenameManager getAnvilRenameManager() {
        return anvilRenameManager;
    }

    @Override
    public void onDisable() {
        getLogger().info("Shutting down AetherCore...");

        if (moduleLoader != null) {
            moduleLoader.unloadModules();
        }
        for (Entity entity : entityList) {
            if (entity != null && entity.isValid()) {
                entity.remove();
            }
        }
        entityList.clear();
    }
    public ModuleLoader getModuleLoader() {
        return moduleLoader;
    }
    public void registerListener(Listener listener) {
        getServer().getPluginManager().registerEvents(listener, this);
    }
}