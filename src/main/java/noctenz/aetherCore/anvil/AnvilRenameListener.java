package noctenz.aetherCore.anvil;

import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.PrepareAnvilEvent;
import org.bukkit.inventory.ItemStack;

public class AnvilRenameListener implements Listener {

    private final AnvilRenameManager manager;

    public AnvilRenameListener(AnvilRenameManager manager) {
        this.manager = manager;
    }

    @EventHandler
    public void onPrepareAnvil(PrepareAnvilEvent event) {
        ItemStack result = event.getResult();

        if (result == null || !result.hasItemMeta()) return;
        if (!result.getItemMeta().hasDisplayName()) return;

        String name = result.getItemMeta().getDisplayName();

        if (manager.isBlacklisted(name)) {
            event.setResult(null);
            Player player = (Player) event.getView().getPlayer();
            player.sendMessage(ChatColor.RED + "Rename Blacklist for this name");
            player.playSound(
                    player.getLocation(),
                    Sound.ENTITY_VILLAGER_NO,
                    1.0f,
                    1.0f
            );
        }
    }
}