package noctenz.aetherCore.listeners;

import io.lumine.mythic.lib.api.item.NBTItem;
import net.Indyuce.mmoitems.api.ReforgeOptions;
import net.Indyuce.mmoitems.api.util.MMOItemReforger;
import noctenz.aetherCore.AetherCore;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

public class MMOItemUpdateListener implements Listener {

    private final AetherCore plugin;

    private static final ReforgeOptions REFORGE_OPTIONS = new ReforgeOptions();

    public MMOItemUpdateListener(AetherCore plugin) {
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.LOW)
    public void onPlayerJoin(PlayerJoinEvent event) {
        plugin.getServer().getScheduler().runTaskLater(plugin, () ->
                updateAllItems(event.getPlayer()), 1L);
    }

    @EventHandler(priority = EventPriority.LOW)
    public void onInventoryClick(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player player)) return;
        if (event.getClickedInventory() == null) return;
        if (!event.getClickedInventory().equals(player.getInventory())) return;

        ItemStack clicked = event.getCurrentItem();
        if (clicked == null || clicked.getType().isAir()) return;

        ItemStack updated = tryUpdate(clicked);
        if (updated != null) {
            event.setCurrentItem(updated);
            player.updateInventory();
        }
    }

    @EventHandler(priority = EventPriority.LOW)
    public void onItemPickup(EntityPickupItemEvent event) {
        if (!(event.getEntity() instanceof Player player)) return;

        plugin.getServer().getScheduler().runTaskLater(plugin, () -> {
            PlayerInventory inv = player.getInventory();
            for (int i = 0; i < inv.getSize(); i++) {
                ItemStack item = inv.getItem(i);
                if (item == null || item.getType().isAir()) continue;
                ItemStack updated = tryUpdate(item);
                if (updated != null) {
                    inv.setItem(i, updated);
                    player.updateInventory();
                }
            }
        }, 1L);
    }

    public static void updateAllItems(Player player) {
        PlayerInventory inv = player.getInventory();
        boolean dirty = false;
        for (int i = 0; i < inv.getSize(); i++) {
            ItemStack item = inv.getItem(i);
            if (item == null || item.getType().isAir()) continue;
            ItemStack updated = tryUpdate(item);
            if (updated != null) {
                inv.setItem(i, updated);
                dirty = true;
            }
        }
        if (dirty) player.updateInventory();
    }

    private static ItemStack tryUpdate(ItemStack item) {
        try {
            MMOItemReforger reforger = new MMOItemReforger(item);
            if (!reforger.canReforge()) return null;

            NBTItem nbt = reforger.getNBTItem();
            int itemRevId = nbt.hasTag("MMOITEMS_REVISION_ID") ? nbt.getInteger("MMOITEMS_REVISION_ID") : 1;
            int templateRevId = reforger.getTemplate().getRevisionId();
            if (templateRevId <= itemRevId) return null;

            boolean success = reforger.reforge(REFORGE_OPTIONS);
            if (!success) return null;

            return reforger.getResult();
        } catch (Exception ignored) {
            return null;
        }
    }
}