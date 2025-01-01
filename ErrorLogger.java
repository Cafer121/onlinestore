package MainCode;

import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;

public class ErrorLogger {

    private static final String LOG_FILE = "error_log.txt";

    public static void logError(String message, Exception e) {
        try (FileWriter writer = new FileWriter(LOG_FILE, true)) {
            writer.write(LocalDateTime.now() + " - " + message + "\n");
            writer.write("Error: " + e.getMessage() + "\n");
            writer.write("-----------------------------\n");
        } catch (IOException ioException) {
            System.out.println("Error writing to log file: " + ioException.getMessage());
        }
    }
}
