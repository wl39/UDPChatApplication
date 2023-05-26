package multicast;

import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * Configuration for the UDP and TCP.
 */
public class Config
{
    public String mAddr= "239.42.42.42"; // CS2003 whole class gorup
    public int mPort = 10101; // random(ish)
    public int ttl = 4;
    public int soTimeout = 1; // ms
    public boolean loopbackOff = false; // ignore my own transmissions
    public boolean reuseAddr = true; // allow address use by otehr apps

    //This port number is for TCP
    public int port = 21005;

    // application config
    public int msgSize = 512;
    public int sleepTime = 5000; // ms, 5s

    // these should not be loaded from a config file, of course
    public InetAddress mGroup;
    public String fqdn;

    /**
     * Initialize host name (fqdn).
     */
    Config()
    {
        InetAddress i;

        try {
            i = InetAddress.getLocalHost();
            fqdn = i.getHostName();
        }
        catch (UnknownHostException e) {
            System.out.println("Problem: " + e.getMessage());
        }
    }
}
