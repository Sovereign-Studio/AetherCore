package noctenz.aetherCore;

import noctenz.aetherCore.commands.AetherCommand;
import noctenz.aetherCore.commands.AetherTabComplete;
import noctenz.aetherCore.listeners.HealthScaleListener;
import noctenz.aetherCore.listeners.MMOItemUpdateListener;
import noctenz.aetherCore.utils.ActionBar;
import noctenz.aetherCore.utils.DummyHandler;
import org.bukkit.entity.Entity;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.List;

public class AetherCore extends JavaPlugin {

    private static AetherCore instance;
    private ActionBar actionBar;
    private ModuleLoader moduleLoader;

    public final List<Entity> entityList = new ArrayList<>();

    @Override
    public void onEnable() {
        getLogger().info("Starting AetherCore...");

        instance = this;
        this.actionBar = new ActionBar(this);
        this.getServer().getPluginManager().registerEvents(this.actionBar, this);
        this.getServer().getPluginManager().registerEvents(new HealthScaleListener(), this);

        DummyHandler dummyHandler = new DummyHandler(this);
        getServer().getPluginManager().registerEvents(dummyHandler, this);
        if (getCommand("spawndummy") != null) {
            getCommand("spawndummy").setExecutor(dummyHandler);
        }

        if (getServer().getPluginManager().getPlugin("MMOItems") != null) {
            getServer().getPluginManager().registerEvents(new MMOItemUpdateListener(this), this);
            getLogger().info("MMOItems found — MMOItemUpdateListener registered.");
        } else {
            getLogger().warning("MMOItems not found — MMOItemUpdateListener skipped.");
        }

        moduleLoader = new ModuleLoader(this);
        moduleLoader.loadModules();

        if (getCommand("ac") != null) {
            getCommand("ac").setExecutor(new AetherCommand(this));
            getCommand("ac").setTabCompleter(new AetherTabComplete(this));
        }

        getLogger().info("AetherCore started successfully.");
    }

    @Override
    public void onDisable() {
        getLogger().info("Shutting down AetherCore...");
        if (moduleLoader != null) moduleLoader.unloadModules();
        for (Entity entity : entityList) {
            if (entity != null && entity.isValid()) entity.remove();
        }
        entityList.clear();
    }

    public static AetherCore getInstance() { return instance; }
    public ActionBar getActionBar() { return this.actionBar; }
    public ModuleLoader getModuleLoader() { return moduleLoader; }
    public void registerListener(Listener listener) { getServer().getPluginManager().registerEvents(listener, this); }
}