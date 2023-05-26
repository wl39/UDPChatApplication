package utility;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

/**
 * Allows the programme to read messages more than one.
 */
public class RxWriter {
    private static final String FILE = "rx_messages.txt";

    /**
     * Write a message to a file.
     * @param message message
     */
    public static void write(String message) {
        try {
            File file = new File(FILE);
            if (!file.exists()) {
                file.createNewFile();
            }

            FileWriter fw = new FileWriter(FILE, false);

            BufferedWriter writer = new BufferedWriter(fw);

            writer.write(message);
            writer.flush();
            writer.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
