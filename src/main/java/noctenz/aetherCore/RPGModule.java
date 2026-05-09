package noctenz.aetherCore;

public interface RPGModule {

    void onEnable(AetherCore core);

    void onDisable();

    String getName();

}