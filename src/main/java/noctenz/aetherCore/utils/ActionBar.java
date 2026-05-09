package noctenz.aetherCore.utils;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;

import noctenz.aetherCore.AetherCore;
import org.bukkit.Bukkit;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

public class ActionBar implements Listener {

    private final AetherCore plugin;

    private final Map<UUID, Double> mana = new HashMap<>();
    private final Map<UUID, Double> maxMana = new HashMap<>();
    private final Map<UUID, Double> stamina = new HashMap<>();
    private final Map<UUID, Double> maxStamina = new HashMap<>();
    private final Map<UUID, Boolean> exhaustedStatus = new HashMap<>();
    private boolean actionBarEnabled = true;

    public ActionBar(AetherCore plugin) {
        this.plugin = plugin;
        startTask();
    }

    private void startTask() {
        new BukkitRunnable() {
            @Override
            public void run() {
                for (Player player : Bukkit.getOnlinePlayers()) {
                    UUID uuid = player.getUniqueId();

                    double currentMana = mana.getOrDefault(uuid, 100D);
                    double maxManaVal = maxMana.getOrDefault(uuid, 100D);

                    if (currentMana < maxManaVal) {
                        mana.put(uuid, Math.min(maxManaVal, currentMana + 2D));
                    }

                    double currentStamina = stamina.getOrDefault(uuid, 100D);
                    double maxStaminaVal = maxStamina.getOrDefault(uuid, 100D);

                    if (currentStamina < maxStaminaVal) {
                        stamina.put(uuid, Math.min(maxStaminaVal, currentStamina + 0.5D));
                    }

                    update(player);
                }
            }
        }.runTaskTimer(plugin, 0L, 20L);
    }

    public void resetPlayer(Player player) {
        UUID uuid = player.getUniqueId();

        maxMana.put(uuid, 100D);
        mana.put(uuid, 100D);

        maxStamina.put(uuid, 100D);
        stamina.put(uuid, 100D);

        exhaustedStatus.put(uuid, false);
        update(player);
    }

    public void update(Player player) {
        UUID uuid = player.getUniqueId();

        double manaVal = mana.getOrDefault(uuid, 100D);
        double maxManaVal = maxMana.getOrDefault(uuid, 100D);

        double staminaVal = stamina.getOrDefault(uuid, 100D);
        double maxStaminaVal = maxStamina.getOrDefault(uuid, 100D);

        double health = player.getHealth();
        double maxHealth = player.getAttribute(Attribute.MAX_HEALTH).getValue();

        Component bar =
                Component.text(String.format("❤ %.0f/%.0f", health, maxHealth))
                        .color(TextColor.fromHexString("#ff0000"))
                        .append(Component.text("  |  ").color(TextColor.color(120,120,120)))
                        .append(Component.text(
                                        String.format("✦ %.0f/%.0f", manaVal, maxManaVal))
                                .color(TextColor.fromHexString("#00a2ff")))
                        .append(Component.text("  |  ").color(TextColor.color(120,120,120)))
                        .append(Component.text(
                                        String.format("⚡ %.0f%%",
                                                staminaVal / maxStaminaVal * 100))
                                .color(TextColor.fromHexString("#e5ff00")));

        // Adventure ActionBar
        if (actionBarEnabled) {
            player.sendActionBar(bar);
        }

        boolean wasExhausted = exhaustedStatus.getOrDefault(uuid, false);
        boolean isNowExhausted = staminaVal <= maxStaminaVal * 0.16;

        if (isNowExhausted) {
            if (!wasExhausted) {
                player.sendMessage(ColorUtil.color(MessagesPrefix.PREFIX + "§cYou are exhausted"));
            }

            exhaustedStatus.put(uuid, true);

            player.addPotionEffect(
                    new PotionEffect(PotionEffectType.NAUSEA, 60, 1, true, false, false));
            player.addPotionEffect(
                    new PotionEffect(PotionEffectType.SLOWNESS, 60, 1, true, false, false));

        } else if (wasExhausted) {
            exhaustedStatus.put(uuid, false);
            player.sendMessage(ColorUtil.color(MessagesPrefix.PREFIX + "§aStamina recovered"));
        }
    }

    /* ---------- Mana ---------- */

    public void useMana(Player player, double amount) {
        UUID uuid = player.getUniqueId();
        mana.put(uuid, Math.max(0D, mana.getOrDefault(uuid, 100D) - amount));
        update(player);
    }

    public double getMana(Player player) {
        return mana.getOrDefault(player.getUniqueId(), 100D);
    }

    public void setMana(Player player, double amount) {
        UUID uuid = player.getUniqueId();
        double max = maxMana.getOrDefault(uuid, 100D);
        mana.put(uuid, Math.min(amount, max));
        update(player);
    }

    public void setMaxMana(Player player, double amount) {
        UUID uuid = player.getUniqueId();
        maxMana.put(uuid, amount);
        mana.put(uuid, Math.min(mana.getOrDefault(uuid, amount), amount));
        update(player);
    }

    public double getMaxMana(Player player) {
        return maxMana.getOrDefault(player.getUniqueId(), 100D);
    }

    /* ---------- Stamina ---------- */

    public double getStamina(Player player) {
        return stamina.getOrDefault(player.getUniqueId(), 100D);
    }

    public void useStamina(Player player, double amount) {
        UUID uuid = player.getUniqueId();
        stamina.put(uuid, Math.max(0D, stamina.getOrDefault(uuid, 100D) - amount));
        update(player);
    }

    public void setStamina(Player player, double amount) {
        UUID uuid = player.getUniqueId();
        double max = maxStamina.getOrDefault(uuid, 100D);
        stamina.put(uuid, Math.min(amount, max));
        update(player);
    }

    public void setMaxStamina(Player player, double amount) {
        UUID uuid = player.getUniqueId();
        maxStamina.put(uuid, amount);
        stamina.put(uuid, Math.min(stamina.getOrDefault(uuid, amount), amount));
        update(player);
    }

    public double getMaxStamina(Player player) {
        return maxStamina.getOrDefault(player.getUniqueId(), 100D);
    }

    public boolean isActionBarEnabled() {
        return actionBarEnabled;
    }

    public void setActionBarEnabled(boolean enabled) {
        this.actionBarEnabled = enabled;
    }
}