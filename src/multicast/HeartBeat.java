package multicast;

import java.nio.charset.StandardCharsets;

import message.Message;
import utility.UserWriter;

/**
 * This class is for get beacon from users
 */
public class HeartBeat
{
    private static Config config;
    private static MulticastEndpoint endpoint;
    private static String username;
    private static boolean online = true;
    private static byte[] bytes;
    /**
     * Setting a username.
     * @param username username
     */
    public static void setUsername(String username) {
        HeartBeat.username = username;
    }

    /**
     * Checking the number of users.
     */
    public static void userCheck() {

        config = new Config();
        endpoint = new MulticastEndpoint(config);

        endpoint.join();

        txHeartBeat();
        rxHeartBeat();

    }

    /**
     * Get the signal from a server.
     */
    private static void rxHeartBeat() {
        StringBuilder userList = new StringBuilder();

//        byte[] b = new byte[config.msgSize];

        //Accumulate all beacons
        try {
            Thread.sleep(config.sleepTime);
        }
        catch (InterruptedException e) {
            // do nothing
        }

        while (endpoint.rx(reinitializeBytes())) {
            if (getBytes().length > 0) {
                //trim method to erase the empty bytes space.
                String data = new String(getBytes()).trim();
                data = Message.encode(Message.decode(data));
                userList.append(data).append("\n");
            }
        }

        System.out.println("\u001B[33m[USER-ADDED]\u001B[0m");
        System.out.println(userList.toString()); //For testing
        UserWriter.write(userList.toString()); //Write a list of users into a file.
    }

    /**
     * Has to reinitialize bytes.
     * Otherwise some information will be overwritten to the bytes.
     * @return bytes
     */
    private static byte[] reinitializeBytes() {
        HeartBeat.bytes = new byte[config.msgSize];
        return bytes;
    }

    private static byte[] getBytes() {
        return bytes;
    }
    /**
     * Send a signal to a server.
     */
    private static void txHeartBeat() {
        if (online) {
            byte[] b;
            String h = heartBeat();

            b = h.getBytes(StandardCharsets.US_ASCII);

            endpoint.tx(b);
        }
    }

    /**
     * This is for the extension.
     * Change the user status online to offline or offline to online.
     */
    public static void changeStatus() {
        online = !online;
    }

    /**
     * Getter of variable "online".
     * @return online status
     */
    public static boolean getOnline() {
        return online;
    }

    /**
     * Formatting a signal.
     * @return formatted signal
     */
    private static String heartBeat() {
        String[] message = {username, config.fqdn, Integer.toString(config.port)};
        return Message.encode(message, "online");
    }

}
