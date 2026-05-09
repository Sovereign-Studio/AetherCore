package noctenz.aetherCore;

public interface Module {

    void onEnable(AetherCore core);

    void onDisable();

    String getName();

}