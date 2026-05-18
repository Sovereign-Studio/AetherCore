package noctenz.aetherCore.listeners;

import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class LevelListener implements Listener {

    public static final int MAX_LEVEL = 5;

    private static final char ACTIVE_SYMBOL   = '◆';
    private static final char INACTIVE_SYMBOL = '◇';

    public static int getItemLevel(ItemStack item) {
        if (item == null || !item.hasItemMeta()) return 0;
        ItemMeta meta = item.getItemMeta();
        if (meta == null || !meta.hasDisplayName()) return 0;
        return parseLevelFromDisplayName(meta.getDisplayName());
    }

    public static boolean isLeveledRPGItem(ItemStack item) {
        return getItemLevel(item) >= 1;
    }

    public static boolean isMaxLevel(ItemStack item) {
        return getItemLevel(item) == MAX_LEVEL;
    }

    public static int parseLevelFromDisplayName(String displayName) {
        if (displayName == null || displayName.isEmpty()) return 0;

        String stripped = stripColor(displayName);

        int bracketStart = stripped.indexOf('[');
        int bracketEnd   = stripped.indexOf(']');
        if (bracketStart == -1 || bracketEnd == -1 || bracketEnd <= bracketStart) return 0;

        String inside = stripped.substring(bracketStart + 1, bracketEnd);

        int activeCount   = 0;
        int inactiveCount = 0;
        for (char c : inside.toCharArray()) {
            if (c == ACTIVE_SYMBOL)        activeCount++;
            else if (c == INACTIVE_SYMBOL) inactiveCount++;
        }

        int total = activeCount + inactiveCount;
        if (total != MAX_LEVEL || activeCount < 1) return 0;

        return activeCount;
    }

    public static String stripColor(String text) {
        if (text == null) return "";
        return text.replaceAll("[§&][0-9a-fk-orA-FK-OR]", "");
    }
}