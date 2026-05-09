package noctenz.aetherCore;

import java.util.List;

public interface Module {
    String getName();
    List<String> getNames();
    void onEnable(AetherCore plugin);
    void onDisable();
}