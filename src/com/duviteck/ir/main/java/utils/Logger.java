package utils;

/**
 * Created by duviteck. 02 Oct 2014.
 */
public class Logger {
    private static final boolean LOGS_ENABLED = true;

    public static void log(String message) {
        if (LOGS_ENABLED) {
            System.out.println(message);
        }
    }
}
