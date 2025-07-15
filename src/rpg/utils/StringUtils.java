package rpg.utils;

public class StringUtils {

    /**
     * Build a string from array arguments within specified range
     */
    public static String buildStringFromArgs(String[] args, int start, int end) {
        if (args == null || start < 0 || end > args.length || start >= end) {
            return "";
        }

        StringBuilder builder = new StringBuilder();
        for (int i = start; i < end; i++) {
            if (i > start) {
                builder.append(" ");
            }
            builder.append(args[i]);
        }
        return builder.toString();
    }

    /**
     * Build a string from all arguments
     */
    public static String buildStringFromArgs(String[] args) {
        return buildStringFromArgs(args, 0, args.length);
    }

    /**
     * Find the index of a keyword in arguments array
     */
    public static int findKeywordIndex(String[] args, String keyword) {
        for (int i = 0; i < args.length; i++) {
            if (args[i].equalsIgnoreCase(keyword)) {
                return i;
            }
        }
        return -1;
    }

    /**
     * Check if string is null or empty after trimming
     */
    public static boolean isNullOrEmpty(String str) {
        return str == null || str.trim().isEmpty();
    }

    /**
     * Safe trim that handles null strings
     */
    public static String safeTrim(String str) {
        return str == null ? "" : str.trim();
    }

    /**
     * Check if a string is a valid number
     */
    public static boolean isNumber(String str) {
        if (isNullOrEmpty(str)) {
            return false;
        }
        try {
            Integer.parseInt(str);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }
}