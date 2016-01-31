package toast.utilityMobs;

import java.util.HashMap;
import java.util.Random;

import net.minecraftforge.common.config.Configuration;

/**
    This helper class automatically creates, stores, and retrieves properties.
    Supported data types:
        String, boolean, int, double

    Any property can be retrieved as an Object or String.
    Any non-String property can also be retrieved as any other non-String property.
    Retrieving a number as a boolean will produce a randomized output depending on the value.
 */
public abstract class Properties
{
    // Mapping of all properties in the mod to their values.
    private static final HashMap<String, Object> map = new HashMap();
    // Common category names.
    public static final String GENERAL = "_general";

    // Initializes these properties.
    public static void init(Configuration config) {
        config.load();

        Properties.add(config, Properties.GENERAL, "alternate_manuals", false, "If this is true, manual recipes will require a book and quill instead of just a book.");
        Properties.add(config, Properties.GENERAL, "creeper_head_rarity", 80, "The rarity for a creeper to drop its head when killed. Setting this to 0 disables skull drops. Drop chance is 1/(rarity - looting).");
        Properties.add(config, Properties.GENERAL, "hostile", false, "If this is true, all utility mobs added by this mod will be hostile towards players.");
        Properties.add(config, Properties.GENERAL, "wither_conversion", true, "Setting this to false disables the wither skull to skeleton skull recipe.");
        Properties.add(config, Properties.GENERAL, "skull_rarity", 60, "The rarity for a skeleton to drop its skull when killed. Setting this to 0 disables skull drops. Drop chance is 1/(rarity - looting).");

        String category;
        for (int i = _UtilityMobs.UTILITY_TYPES.length; i-- > 0;) {
            category = _UtilityMobs.UTILITY_TYPES[i].toLowerCase() + "s";

            Properties.add(config, category, "_all", true, "If false, " + (category == "golems" ? "standard" : _UtilityMobs.UTILITY_TYPES[i].toLowerCase()) + " golems will not be buildable.");
            for (int j = _UtilityMobs.UTILITY_NAMES[i].length; j-- > 0;) {
                Properties.add(config, category, _UtilityMobs.UTILITY_NAMES[i][j], true);
            }

            config.addCustomCategoryComment(category, "Options to disable the building of specific golems or all golems of this type.");
        }

        config.addCustomCategoryComment(Properties.GENERAL, "General and/or miscellaneous options.");
        config.save();
    }

    // Gets the mod's random number generator.
    public static Random random() {
        return _UtilityMobs.random;
    }

    // Passes to the mod.
    public static void debugException(String message) {
        _UtilityMobs.debugException(message);
    }

    // Loads the property as the specified value.
    public static void add(Configuration config, String category, String field, String defaultValue, String comment) {
        Properties.map.put(category + "@" + field, config.get(category, field, defaultValue, comment).getString());
    }
    public static void add(Configuration config, String category, String field, int defaultValue, String comment) {
        Properties.map.put(category + "@" + field, Integer.valueOf(config.get(category, field, defaultValue, comment).getInt(defaultValue)));
    }
    public static void add(Configuration config, String category, String field, boolean defaultValue) {
        Properties.map.put(category + "@" + field, Boolean.valueOf(config.get(category, field, defaultValue).getBoolean(defaultValue)));
    }
    public static void add(Configuration config, String category, String field, boolean defaultValue, String comment) {
        Properties.map.put(category + "@" + field, Boolean.valueOf(config.get(category, field, defaultValue, comment).getBoolean(defaultValue)));
    }
    public static void add(Configuration config, String category, String field, double defaultValue, String comment) {
        Properties.map.put(category + "@" + field, Double.valueOf(config.get(category, field, defaultValue, comment).getDouble(defaultValue)));
    }

    // Gets the Object property.
    public static Object getProperty(String category, String field) {
        return Properties.map.get(category + "@" + field);
    }

    // Gets the value of the property (instead of an Object representing it).
    public static String getString(String category, String field) {
        return Properties.getProperty(category, field).toString();
    }
    public static boolean getBoolean(String category, String field) {
        Object property = Properties.getProperty(category, field);
        if (property instanceof Boolean)
            return ((Boolean)property).booleanValue();
        if (property instanceof Integer)
            return Properties.random().nextInt(((Number)property).intValue()) == 0;
        if (property instanceof Double)
            return Properties.random().nextDouble() < ((Number)property).doubleValue();
        Properties.debugException("Tried to get boolean for invalid property! @" + property == null ? "(null)" : property.getClass().getName());
        return false;
    }
    public static int getInt(String category, String field) {
        Object property = Properties.getProperty(category, field);
        if (property instanceof Number)
            return ((Number)property).intValue();
        if (property instanceof Boolean)
            return ((Boolean)property).booleanValue() ? 1 : 0;
        Properties.debugException("Tried to get int for invalid property! @" + property == null ? "(null)" : property.getClass().getName());
        return 0;
    }
    public static double getDouble(String category, String field) {
        Object property = Properties.getProperty(category, field);
        if (property instanceof Number)
            return ((Number)property).doubleValue();
        if (property instanceof Boolean)
            return ((Boolean)property).booleanValue() ? 1.0 : 0.0;
        Properties.debugException("Tried to get double for invalid property! @" + property == null ? "(null)" : property.getClass().getName());
        return 0.0;
    }
}