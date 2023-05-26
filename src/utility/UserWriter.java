package utility;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

/**
 * Save a list of users into a file.
 */
public class UserWriter {
    private static final String USER_LIST_FILE = "users_list.txt";

    /**
     * Save a list of users into a file.
     * @param userInfo user information
     */
    public static void write(String userInfo) {
        try {
            File file = new File(USER_LIST_FILE);

            if (!file.exists()) {
                file.createNewFile();
            }

            FileWriter fw = new FileWriter(USER_LIST_FILE, false);

            BufferedWriter writer = new BufferedWriter(fw);

            writer.write(userInfo );
            writer.flush();
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
