package message;

import java.security.InvalidParameterException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * This class will parse a message that a user sent or received to right format.
 */
public class Message {

    //Regular expression that check the brackets
    private static final Pattern BRACKETS = Pattern.compile("\\[(.*?)\\]");

    private static final String ONLINE = "online";
    private static final String OFFLINE = "offline";
    private static final String TEXT = "text";

    private static final int USERNAME = 0;
    private static final int MESSAGE_TO_SEND = 1;
    private static final int FQDN = 1;
    private static final int PORT_NUMBER = 2;

    /**
     * Encode the message to right format.
     * @param userInfo message that user sent to a server
     * @param type type of the message
     * @return encoded message
     */
    public static String encode(String[] userInfo, String type) {
        if (userInfo.length <= 0) {
            throw new InvalidParameterException();
        }

        StringBuilder result = new StringBuilder();

        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd-HHmmss.SSS");
        String now = sdf.format(new Date());

        result.append("[").append(now).append("]");

        switch (type) {
            case ONLINE:
                result.append("[").append(userInfo[USERNAME]).append("]");
                result.append("[").append(ONLINE).append("]");
                result.append("[").append(userInfo[FQDN]).append("]");
                result.append("[").append(userInfo[PORT_NUMBER]).append("]");
                break;
            case OFFLINE:
                result.append("[").append(userInfo[USERNAME]).append("]");
                result.append("[").append(OFFLINE).append("]");
                result.append("[").append(userInfo[FQDN]).append("]");
                result.append("[").append(userInfo[PORT_NUMBER]).append("]");
                break;
            case TEXT:
                result.append("[").append(userInfo[USERNAME]).append("]");
                result.append("[").append(TEXT).append("]");
                result.append("[").append(userInfo[MESSAGE_TO_SEND]).append("]");
                break;

            default:
                break;

        }

        return result.toString();
    }

    /**
     * Override encode method, do same as the encode method above
     * @param message message that user received
     * @return encoded message
     */
    public static String encode(String message) {
        StringBuilder result = new StringBuilder();

        String[] data = message.split(", ");

        for (String s : data) {
            result.append("[").append(s).append("]");
        }

        return result.toString();
    }

    /**
     * Decode the message that a user received.
     * @param messageToDecode message to decode
     * @return decoded message
     */
    public static String decode(String messageToDecode) {

        Matcher data = BRACKETS.matcher(messageToDecode);
        ArrayList<String> message = new ArrayList<>();

        while (data.find()) {
            message.add(data.group(1));
        }

        String result = message.toString().replace("[", "").replace("]", "");

        return result;
    }
}
