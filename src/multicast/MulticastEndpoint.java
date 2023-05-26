package multicast;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.SocketTimeoutException;

/**
 * Simplify the method of MulticastSocket.
 */
public class MulticastEndpoint
{
    private MulticastSocket multicastSocket;
    private Config config;

    MulticastEndpoint(Config config)
    {
        InetAddress mGroup;
        this.config = config;

        try {
            mGroup   = InetAddress.getByName(this.config.mAddr);
            multicastSocket = new MulticastSocket(this.config.mPort);

            multicastSocket.setLoopbackMode(this.config.loopbackOff);
            multicastSocket.setReuseAddress(this.config.reuseAddr);
            multicastSocket.setTimeToLive(this.config.ttl);
            multicastSocket.setSoTimeout(this.config.soTimeout); // non-blocking

            config.mGroup = mGroup;
        }

        catch (IOException e) {
            System.out.println("MulticastEndpoint() problem: " + e.getMessage());
        }
    }

    /**
     * Join a multicast group.
     */
    void join()
    {
        try {
            multicastSocket.joinGroup(config.mGroup);
        }
        catch (IOException e) {
            System.out.println("join() problem: " + e.getMessage());
        }
    }

    /**
     * Leave a multicast group.
     */
    void leave()
    {
        try {
            multicastSocket.leaveGroup(config.mGroup);
            multicastSocket.close();
        }
        catch (IOException e) {
            System.out.println("leave() problem: " + e.getMessage());
        }
    }

    /**
     * Read a datagram packet from the socket.
     * @param b result of reading
     * @return whether reading is finish or not
     */
    boolean rx(byte b[])
    {
        boolean done;
        DatagramPacket d;

        done = false;
        d = new DatagramPacket(b, b.length);
        try {
            multicastSocket.receive(d);
            //To get members socket address (which contain ip address and port number)
            done = true;
        } catch (SocketTimeoutException e) {
            // do nothing
        } catch (IOException e) {
            System.out.println("rx() problem: " + e.getMessage());
        }

        return done;
    }

    /**
     * Write a datagram packet and send it to the server.
     * @param b bytes to send
     */
    void tx(byte b[])
    {
        DatagramPacket d;

        try {
            d = new DatagramPacket(b, b.length, config.mGroup, config.mPort);
            multicastSocket.send(d);
        }

        catch (SocketTimeoutException e) {
            System.out.println("tx() problem: could not send - " + e.getMessage());
        }
        catch (IOException e) {
            System.out.println("tx() problem: " + e.getMessage());
        }
    }
}
