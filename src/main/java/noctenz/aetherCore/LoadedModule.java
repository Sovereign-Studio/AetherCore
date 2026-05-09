package noctenz.aetherCore;

import java.net.URLClassLoader;

public class LoadedModule {

    private final RPGModule module;
    private final URLClassLoader classLoader;

    public LoadedModule(RPGModule module, URLClassLoader classLoader) {
        this.module = module;
        this.classLoader = classLoader;
    }

    public RPGModule getModule() {
        return module;
    }

    public URLClassLoader getClassLoader() {
        return classLoader;
    }
}