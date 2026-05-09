package noctenz.aetherCore.utils;

import io.lumine.mythic.lib.api.event.PlayerAttackEvent;
import noctenz.aetherCore.AetherCore;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.attribute.Attribute;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityCombustEvent;
import org.bukkit.event.entity.EntityTeleportEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.*;

public class DummyHandler implements Listener, CommandExecutor {

    private final AetherCore plugin;

    private static final String DUMMY_NAME =
            "§aDamage Dummy";

    private final Set<UUID> cooldown = new HashSet<>();

    public DummyHandler(AetherCore plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage(ColorUtil.color(MessagesPrefix.PREFIX + "§cOnly players can use this command."));
            return true;
        }
        if (args.length == 0) {
            player.sendMessage("§cUsage: /spawndummy <type>");
            return true;
        }

        DummyType type;

        try {
            type = DummyType.valueOf(args[0].toUpperCase());
        } catch (IllegalArgumentException ex) {
            player.sendMessage(ColorUtil.color(MessagesPrefix.PREFIX + "§cInvalid dummy type."));
            return true;
        }
        spawnDummy(player, type);
        return true;
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onMythicDamage(PlayerAttackEvent event) {
        if (!(event.getEntity() instanceof Monster monster)) return;

        String name = monster.getCustomName();
        if (name == null || !name.contains("Damage Dummy")) return;

        Player player = event.getPlayer();
        double damage = event.getDamage().getDamage();

        String dmg = (damage % 1 == 0)
                ? String.valueOf((int) damage)
                : String.format("%.1f", damage);

        player.sendActionBar("§c⚔ " + dmg);
        monster.setCustomName("§aDamage Dummy §7( §c" + dmg + " §7)");

        Bukkit.getScheduler().runTaskLater(plugin, () -> {
            if (!monster.isValid()) return;
            double maxHealth = monster.getAttribute(Attribute.MAX_HEALTH).getValue();
            monster.setHealth(maxHealth);
        }, 2L);
    }

    @EventHandler
    public void onEntityCombust(EntityCombustEvent e) {
        if (!(e.getEntity() instanceof Monster monster)) return;
        String name = monster.getCustomName();
        if (name != null && name.contains("Damage Dummy")) {
            e.setCancelled(true);
        }
    }

    @EventHandler
    public void onDummyInteract(PlayerInteractAtEntityEvent e) {
        if (!(e.getRightClicked() instanceof Monster monster)) return;

        String name = monster.getCustomName();
        if (name == null || !name.contains("Damage Dummy")) return;
        e.setCancelled(true);

        openDummyGUI(e.getPlayer());
    }

    private void openDummyGUI(Player player) {
        Inventory gui = Bukkit.createInventory(null, 54, "Select Dummy Type");
        for (DummyType type : DummyType.values()) {
            ItemStack egg = new ItemStack(type.spawnEgg);
            ItemMeta meta = egg.getItemMeta();
            if (meta != null) {
                meta.setDisplayName(type.displayName);
                meta.setLore(type.lore);
                egg.setItemMeta(meta);
            }
            gui.setItem(type.slot, egg);
        }

        if (player.hasPermission("aethercore.admin")) {
            ItemStack barrier = new ItemStack(Material.BARRIER);
            ItemMeta meta = barrier.getItemMeta();
            meta.setDisplayName("§cDelete Dummy");
            barrier.setItemMeta(meta);
            gui.setItem(49, barrier);
        }
        player.openInventory(gui);
    }

    @EventHandler
    public void onGUIInteract(InventoryClickEvent e) {
        if (!(e.getWhoClicked() instanceof Player player)) return;
        if (!e.getView().getTitle().contains("Select Dummy Type"))
            return;
        e.setCancelled(true);

        int slot = e.getRawSlot();
        if (slot >= e.getInventory().getSize()) return;
        if (slot >= 0 && slot < 45) {
            DummyType type = DummyType.getBySlot(slot);
            if (type != null) {
                setDummyType(player, type);
            }
        }
        if (slot == 49 && player.hasPermission("aethercore.admin")) {
            setDummyType(player, null);
        }
    }

    private void setDummyType(Player player, DummyType type) {
        if (cooldown.contains(player.getUniqueId())) return;
        Entity dummy = getDummyNearby(player);

        if (dummy == null) return;
        cooldown.add(player.getUniqueId());

        Location loc = dummy.getLocation();

        dummy.remove();

        if (type != null) {
            Monster newDummy =
                    (Monster) loc.getWorld().spawn(loc, type.entityClass);
            setupDummy(newDummy);
            plugin.entityList.add(newDummy);
            player.sendMessage(ColorUtil.color(MessagesPrefix.PREFIX + "§aSuccessfully set Dummy type to " + type.displayName));
        } else {
            player.sendMessage(ColorUtil.color(MessagesPrefix.PREFIX + "§cDummy removed."));
        }
        player.closeInventory();
        Bukkit.getScheduler().runTaskLater(plugin,
                () -> cooldown.remove(player.getUniqueId()), 200L);
    }

    private Entity getDummyNearby(Player player) {
        for (Entity entity : player.getNearbyEntities(5, 5, 5)) {
            if (entity instanceof Monster monster) {
                String name = monster.getCustomName();
                if (name != null && name.contains("Damage Dummy")) {
                    return monster;
                }
            }
        }
        return null;
    }

    @EventHandler
    private void onEndermanTeleport(EntityTeleportEvent e) {
        if (!(e.getEntity() instanceof Enderman enderman)) return;
        String name = enderman.getCustomName();
        if (name != null && name.contains("Damage Dummy")) {
            e.setCancelled(true);
        }
    }

    private void spawnDummy(Player player, DummyType type) {
        if (cooldown.contains(player.getUniqueId())) {
            player.sendMessage(ColorUtil.color(MessagesPrefix.PREFIX + "§cYou are currently on cooldown."));
            return;
        }

        Location location = player.getLocation();
        Monster dummy =
                (Monster) location.getWorld().spawn(location, type.entityClass);

        setupDummy(dummy);
        plugin.entityList.add(dummy);
        player.sendMessage(ColorUtil.color(MessagesPrefix.PREFIX + "§aDummy spawned: " + type.displayName));

        cooldown.add(player.getUniqueId());
        Bukkit.getScheduler().runTaskLater(plugin,
                () -> cooldown.remove(player.getUniqueId()), 200L);
    }

    private void setupDummy(Monster dummy) {
        dummy.setCustomName("§aDamage Dummy");
        dummy.setCustomNameVisible(true);

        dummy.getAttribute(Attribute.MAX_HEALTH).setBaseValue(1024);
        dummy.setHealth(1024);

        dummy.setAI(false);
        dummy.setAware(false);
        dummy.setSilent(true);
        dummy.setRemoveWhenFarAway(false);
    }

    private enum DummyType {
        ZOMBIE(10, Material.ZOMBIE_SPAWN_EGG, "§aZombie", Zombie.class),
        WITHER_SKELETON(11, Material.WITHER_SKELETON_SPAWN_EGG, "§aWither Skeleton", WitherSkeleton.class),
        EVOKER(12, Material.EVOKER_SPAWN_EGG, "§aEvoker", Evoker.class),
        STRAY(13, Material.STRAY_SPAWN_EGG, "§aStray", Stray.class),
        SPIDER(14, Material.SPIDER_SPAWN_EGG, "§aSpider", Spider.class),
        ENDERMAN(15, Material.ENDERMAN_SPAWN_EGG, "§aEnderman", Enderman.class),
        ENDERMITE(16, Material.ENDERMITE_SPAWN_EGG, "§aEndermite", Endermite.class),
        CREEPER(19, Material.CREEPER_SPAWN_EGG, "§aCreeper", Creeper.class),
        SKELETON(20, Material.SKELETON_SPAWN_EGG, "§aSkeleton", Skeleton.class),
        WITCH(21, Material.WITCH_SPAWN_EGG, "§aWitch", Witch.class),
        BLAZE(22, Material.BLAZE_SPAWN_EGG, "§aBlaze", Blaze.class),
        DROWNED(23, Material.DROWNED_SPAWN_EGG, "§aDrowned", Drowned.class),
        ELDER_GUARDIAN(24, Material.ELDER_GUARDIAN_SPAWN_EGG, "§aElder Guardian", ElderGuardian.class),
        HUSK(25, Material.HUSK_SPAWN_EGG, "§aHusk", Husk.class),
        ILLUSIONER(28, Material.EVOKER_SPAWN_EGG, "§aIllusioner", Illusioner.class),
        PILLAGER(29, Material.PILLAGER_SPAWN_EGG, "§aPillager", Pillager.class),
        SILVERFISH(30, Material.SILVERFISH_SPAWN_EGG, "§aSilverfish", Silverfish.class);

        int slot;
        Material spawnEgg;
        String displayName;
        List<String> lore;
        Class<? extends Monster> entityClass;

        DummyType(int slot, Material spawnEgg, String name, Class<? extends Monster> entityClass) {

            this.slot = slot;
            this.spawnEgg = spawnEgg;
            this.displayName = name;
            this.entityClass = entityClass;

            this.lore = List.of(
                    "",
                    "§7Set the dummy to §3" + name,
                    "",
                    "§e§lCLICK!"
            );
        }

        public static DummyType getBySlot(int slot) {
            for (DummyType type : values()) {
                if (type.slot == slot) {
                    return type;
                }
            }
            return null;
        }
    }
}