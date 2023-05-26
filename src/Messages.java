/*
 * CS2003 coursework Net2 demo
 * Saleem Bhatti, Oct 2018
 */

import message.Message;
import multicast.HeartBeat;
import utility.MessageChecker;
import utility.MessageWriter;
import utility.RxWriter;

import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.*;

public class Messages extends Frame implements  ActionListener, WindowListener, Runnable {

    private static HashMap<String, WindowMessage> window = new HashMap<>();
    private static HashMap<String, String> userList = MessageCheckerCommon.user_info;

    private static final int DATE = 0;
    private static final int USERNAME = 1;
    private static final int MESSAGE = 3;
    private static final int F_USERNAME = 0;
    private static final int F_MESSAGE = 1;
    private static final int FQDN = 0;
    private static final int PORT_NUMBER = 1;

    private static String id;

    private static MessageWriter writer = null;
    // Where you will type messages.
    private static TextField input;

    // Where you will see incoming messages
    private static TextArea messages;

    // Notifications for the application
    private static Notifications notifications;

    private static ServerSocket server;

    Messages(String id, Notifications n) {
        super(id + " : messages"); // call the Frame constructor
        Messages.id = id;

        notifications = n;

        setLayout(new FlowLayout());
        setBounds(0, 0, 800, 425); // size of Frame

        Panel p;

        input = new TextField(80);
        p = new Panel();
        p.add(new Label("Type here: "));
        p.add(input);
        add(p); // to this Frame

        // This is a separate Frame -- appears in a separate OS window
        messages = new TextArea("", 20, 80, TextArea.SCROLLBARS_VERTICAL_ONLY);
        p = new Panel();
        p.add(new Label(id));
        p.add(messages);
        add(p); // to this Frame

        // This obect handles window events (clicks) ...
        addWindowListener(this);
        // ... and actions for input (typing)
        input.addActionListener(this);
    }

    @Override
    public void windowClosing(WindowEvent we) { }
    @Override
    public void windowClosed(WindowEvent we) { }
    @Override
    public void windowActivated(WindowEvent we) { }
    @Override
    public void windowDeactivated(WindowEvent we) { }
    @Override
    public void windowIconified(WindowEvent we) { }
    @Override
    public void windowDeiconified(WindowEvent we) { }
    @Override
    public void windowOpened(WindowEvent we) { }

    /*
     * ActionListener method - required.
     * Text input from user - to transmit on the network.
     */
    @Override
    public void actionPerformed(ActionEvent ae) {
        String t = input.getText();

        if (t == null) { return; }
        t = t.trim();
        if (t.length() < 1) { return; }

        // message format is
        // sender:message
        String[] f = t.split(":", 2);

        // This is not the best way to check the message format!
        // For demo purposes only.
        if (!MessageChecker.check(f[F_USERNAME], "username")) {
            notifications.notify("tx: Bad message format.");
            return ;
        }

        String s = "<- You : " + f[1] + "\n"; // mark outgoing messages - for demo purposes

        userList = MessageCheckerCommon.user_info;

        //Check the user exists
        if (userList.containsKey(f[0])) {
            //value -> fqdn:port_number
            String value = userList.get(f[0]);
            String[] address = value.split(":");
            if (!(MessageChecker.check(address[FQDN], "fqdn") && MessageChecker.check(address[PORT_NUMBER], "portnumber"))) {
                notifications.notify("Invalid FQDN and or Number"); // for demo purposes
                return;
            }
            //if the window does not exist making a new
            if (!window.containsKey(f[F_USERNAME])) {
                window.put(f[F_USERNAME], new WindowMessage(id, f[F_USERNAME], address[FQDN], address[PORT_NUMBER]));
                window.get(f[F_USERNAME]).insertTx(f[F_MESSAGE]);
            } else {
                //if the user even send a message in the main messenger.
                //Text area has to be inserted
                window.get(f[F_USERNAME]).insertTx(f[F_MESSAGE]);
            }

            try {
                //Open the cline socket
                Socket client = new Socket(address[FQDN], Integer.parseInt(address[PORT_NUMBER]));

                DataOutputStream tx = new DataOutputStream(client.getOutputStream());

                String[] newFormat = {id, f[F_MESSAGE]};
                String line = Message.encode(newFormat, "text");
                newFormat = Message.decode(line).split(", ");

                //Write text file
                writer = new MessageWriter(f[0]);
                writer.write(newFormat);

                tx.writeBytes(line + "\n");
            } catch (IOException e) {
                e.printStackTrace();
            }
            messages.insert(s, 0); // top of TextArea
            notifications.notify("Message sent to: " + f[F_USERNAME]); // for demo purposes

            //dynamically show the list of files and history
            MessageCheckerGUI.setList();
            MessageCheckerGUI.readFile(f[F_USERNAME] + ".txt");

            input.setText(""); // make sure TextField is empty
        } else {
            notifications.notify("There is no certain user: " + f[F_USERNAME]); // for demo purposes
            input.setText(""); // make sure TextField is empty
        }
    }

    /*
     * Runnable method - required.
     * Incoming messages - received from the network.
     */
    @Override
    public void run() {
        //Open the socket in here

        try {
            int portNumber = 21005;
            server = new ServerSocket(portNumber);
        } catch (IOException e) {
            e.printStackTrace();
        }

        while (true) {
            try {
                Socket client = server.accept();
                if (HeartBeat.getOnline()) {
                    BufferedReader br = new BufferedReader(new InputStreamReader(client.getInputStream()));
                    RxWriter.write(br.readLine());
                } else {
                    client.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            if (HeartBeat.getOnline()) {
                ArrayList<String> rx = MessageCheckerCommon.rx_messages();

                userList = MessageCheckerCommon.user_info;

                for (int r = 0; r < rx.size(); ++r) {

                    String m = rx.get(r);

                    m = m.trim();

                    String line = Message.decode(m);
                    String[] info = line.split(", ");

                    if (!(MessageChecker.check(info[DATE], "date") && MessageChecker.check(info[USERNAME], "username"))) {
                        notifications.notify("rx: Bad string received.");
                        continue;
                    }

                    String s = "-> " + info[USERNAME] + " : " + info[MESSAGE] + "\n";

                    if (!window.containsKey(info[USERNAME])) {
                        String value = userList.get(info[USERNAME]);
                        String[] address = value.split(":");
                        if (!(MessageChecker.check(address[FQDN], "fqdn") && MessageChecker.check(address[PORT_NUMBER], "portnumber"))) {
                            continue;
                        }
                        window.put(info[USERNAME], new WindowMessage(id, info[USERNAME], address[FQDN], address[PORT_NUMBER]));
                        window.get(info[USERNAME]).insertRx(s);
                    } else {
                        window.get(info[USERNAME]).insertRx(s);
                    }

                    writer = new MessageWriter(info[USERNAME]);
                    writer.write(info);

                    notifications.notify("Received a message from: " + info[USERNAME]);

                    MessageCheckerGUI.setList();
                    MessageCheckerGUI.readFile(info[USERNAME] + ".txt");
                    messages.insert(s, 0);

                } // for (r < rx.size())
            }
        } // while(true)
    }

    /**
     * This method is for WindowMessage it will send a message.
     * @param message message
     * @param username username
     * @return send whether successfully or not
     */
    static boolean sendMessage(String message, String username) {
        boolean send = false;
        if (window.containsKey(username)) {
            WindowMessage windowMessage = window.get(username);
            try {
                Socket client = new Socket(windowMessage.fqdn, Integer.parseInt(windowMessage.portNumber));

                DataOutputStream tx = new DataOutputStream(client.getOutputStream());
                String[] newFormat = {id, message};
                String line = Message.encode(newFormat, "text");
                newFormat = Message.decode(line).split(", ");
                writer = new MessageWriter(username);
                writer.write(newFormat);
                tx.writeBytes(line + "\n");
                send = true;
                String s = "<- You  : " + message + "\n";
                messages.insert(s, 0); // top of TextArea
                notifications.notify("Message sent to: " + username);

                input.setText(""); // make sure TextField is empty
                return send;
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            notifications.notify("There is no certain user: " + username);
            input.setText(""); // make sure TextField is empty
        }
        return send;
    }

} // class Messages
