package rpg.utils;

public class StringUtils {

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

    public static String buildStringFromArgs(String[] args) {
        return buildStringFromArgs(args, 0, args.length);
    }

    public static int findKeywordIndex(String[] args, String keyword) {
        for (int i = 0; i < args.length; i++) {
            if (args[i].equalsIgnoreCase(keyword)) {
                return i;
            }
        }
        return -1;
    }

    public static boolean isNullOrEmpty(String str) {
        return str == null || str.trim().isEmpty();
    }

    public static String safeTrim(String str) {
        return str == null ? "" : str.trim();
    }

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

    public static String removeArticles(String input) {
        if (input == null || input.trim().isEmpty()) {
            return input;
        }

        String trimmed = input.trim();
        String lower = trimmed.toLowerCase();

        if (lower.startsWith("the ")) {
            return trimmed.substring(4);
        } else if (lower.startsWith("a ")) {
            return trimmed.substring(2);
        } else if (lower.startsWith("an ")) {
            return trimmed.substring(3);
        }

        return trimmed;
    }

    public static String cleanInput(String input) {
        return removeArticles(safeTrim(input));
    }
}