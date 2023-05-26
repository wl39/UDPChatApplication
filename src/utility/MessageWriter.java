package utility;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

/**
 * Write a message in a file
 */
public class MessageWriter {
    private final String PATH = "./text/";
    private final int TIME = 0;
    private final int USERNAME = 1;
    private final int MESSAGE = 3;

    private String fileName = "";

    public MessageWriter(String username) {
        File file = new File(PATH);
        if (!file.exists()) {
            file.mkdir();
        }

        fileName = new StringBuilder().append(PATH).append(username).append(".txt").toString();
    }

    /**
     * Write a message to a file.
     * @param info information of the message
     */
    public void write(String[] info) {
        try {
            File file = new File(fileName);

            if (!file.exists()) {
                file.createNewFile();
                System.out.println("\n\u001B[32m[Created a new File]\u001B[0m\n");
            }

            BufferedWriter bw = new BufferedWriter(new FileWriter(file, true));

            bw.write(new StringBuilder().append(info[TIME]).append(" - ").append(info[USERNAME])
                    .append(": ").append(info[MESSAGE]).append("\n").toString());
            bw.flush();
            bw.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
