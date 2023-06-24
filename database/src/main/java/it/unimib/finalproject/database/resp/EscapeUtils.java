package it.unimib.finalproject.database.resp;

public class EscapeUtils {
    public static String escape(String string) {
        if (string == null) {
            return null;
        }
        return string.replace("\r", "\\r").replace("\n", "\\n");
    }

    public static String unescape(String string) {
        if (string == null) {
            return null;
        }
        return string.replace("\\r", "\r").replace("\\n", "\n");
    }
}
