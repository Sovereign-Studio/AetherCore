package noctenz.aetherCore.utils;

import net.md_5.bungee.api.ChatColor;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ColorUtil {

    private static final Pattern HEX_PATTERN = Pattern.compile("&#([A-Fa-f0-9]{6})");
    public static String color(String text) {
        Matcher matcher = HEX_PATTERN.matcher(text);
        while (matcher.find()) {
            String hex = matcher.group(1);
            text = text.replace("&#" + hex, ChatColor.of("#" + hex).toString());
        }
        return ChatColor.translateAlternateColorCodes('&', text);
    }
}