package noctenz.aetherCore;

import java.util.List;

public interface Module {
    List<String> getNames();
    void onEnable(AetherCore plugin);
    void onDisable();

    default String getName() {
        return getNames().isEmpty() ? "" : getNames().get(0);
    }
}