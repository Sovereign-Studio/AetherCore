package noctenz.aetherCore;

import java.net.URLClassLoader;

public class LoadedModule {

    private final Module module;
    private final URLClassLoader classLoader;

    public LoadedModule(Module module, URLClassLoader classLoader) {
        this.module = module;
        this.classLoader = classLoader;
    }

    public Module getModule() {
        return module;
    }

    public URLClassLoader getClassLoader() {
        return classLoader;
    }
}