
import utility.MessageChecker;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

class MessageCheckerCommon {

    private static final String rx_messages = "rx_messages.txt";
    private static final String users_list = "users_list.txt";

    private static final int DATE = 0;
    private static final int USERNAME = 1;
    private static final int TYPE = 2;
    private static final int FQDN = 3;
    private static final int PORT_NUMBER = 4;
    private static final int SIZE = 5;

    static HashMap<String, String> user_info = new HashMap<>();

    /**
     * Get the current date.
     * @return simple date
     */
    static String timestamp() {
        final SimpleDateFormat s = new SimpleDateFormat("yyyyMMdd-HH:mm:ss.SSS");
        return s.format(new Date());
    }

    /**
     * Read all messages and return the messages.
     * @return messages
     */
    static ArrayList<String> rx_messages()
    {
        ArrayList<String> messages = new ArrayList<>();

        File f = new File(rx_messages);
        if (!f.exists()) { return messages; }

        try {
            BufferedReader r = new BufferedReader(new FileReader(rx_messages));
            String s;
            while ((s = r.readLine()) != null) { // multiple messages?
                s = s.trim();
                if (s.length() > 0) { messages.add(s); }
            }
            r.close();
            r = null; // remove possible reference to file
            f.delete();
            f = null; // remove possible reference to file
        }
        catch (IOException e) {
            System.err.println("rx_messages() : " + e.getMessage());
        }

        return messages;
    } // rx_messages()

    /**
     * Read a list of users and return the list of users.
     * This method will use for listing the users in the GUI.
     * @return list of users
     */
    static ArrayList<String> users_list()
    {
        Pattern brackets = Pattern.compile("\\[(.*?)\\]");
        ArrayList<String> message = new ArrayList<>();

        ArrayList<String> users = new ArrayList<>();
        user_info = new HashMap<>();

        File f = new File(users_list);
        if (!f.exists()) { return users; }

        try {
            BufferedReader u = new BufferedReader(new FileReader(users_list));
            String s;

            while ((s = u.readLine()) != null) {
                s = s.trim();
                if (s.length() > 0) {
                    Matcher data = brackets.matcher(s);

                    while (data.find()) {
                        message.add(data.group(1));
                    }
                    if (message.size() == SIZE) {
                        String date = message.get(DATE);
                        String username = message.get(USERNAME);
                        String fqdn = message.get(FQDN);
                        String portnumber = message.get(PORT_NUMBER);
                        switch(message.get(TYPE)) {
                            case "online":

                                if (MessageChecker.check(date, "date") && MessageChecker.check(username, "username") &&
                                        MessageChecker.check(fqdn, "fqdn") && MessageChecker.check(portnumber, "portnumber")) {
                                    users.add(username);
                                    user_info.put(username, fqdn + ":" + portnumber);
                                } else {
                                    System.out.println("\u001B[31m[WARN] INVALID FORMAT HAS BEEN DETECTED ---> \u001B[0m" + message.toString());
                                }
                                break;
                            case "offline":
                                users.remove(message.get(USERNAME));
                                user_info.remove(message.get(USERNAME));
                                break;
                            default:
                                break;
                        }
                    }
                    message.clear();
                }
            }
            u.close();
        } catch (IOException e) {
            System.err.println("users_list() : " + e.getMessage());
            return null;
        }
        return users;
    } // users_list()
} // class MessageCheckerCommon
