
import multicast.HeartBeat;

import java.awt.*;
import java.awt.event.*;
import java.io.*;

public class MessageCheckerGUI extends Frame implements  ActionListener, WindowListener {

    public static java.awt.List fileList = new List(8, false);
    public static java.awt.TextArea history = new TextArea("", 10, 60,TextArea.SCROLLBARS_VERTICAL_ONLY);

    public MessageCheckerGUI(String name) {
        super(name + " : notifications and users"); // call the Frame constructor

        setLayout(new FlowLayout());
        setBounds(0, 0, 800, 420); // Size of Frame

        Panel p;

        p = new Panel();
        p.add(new Label("Notifications"));
        TextArea n = new TextArea("", 4, 80, TextArea.SCROLLBARS_VERTICAL_ONLY);
        p.add(n);
        add(p); // to this Frame
        // Where general information and notifications are displayed.
        Notifications notifications = new Notifications(n);

        p = new Panel();
        p.add(new Label("Users"));
        java.awt.List u = new List(4, false);
        java.awt.Button button = new Button("Offline");

        button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                HeartBeat.changeStatus();
                if (HeartBeat.getOnline()) {
                    button.setLabel("Offline");
                } else {
                    button.setLabel("Online");
                }
            }
        });


        p.add(u);
        p.add(button);
        // Where new users will be listed.
        Users users = new Users(u, notifications, name);
        Thread t = new Thread(users); // let it look after itself
        t.start();
        add(p);  // to this Frame

        p = new Panel();
        p.add(new Label("Messages"));

        //listing files
        setList();

        //When click the file
        fileList.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                //will show a history
                readFile(fileList.getSelectedItem());
            }
        });
        p.add(fileList);
        p.add(history);
        add(p);

        // This is a separate Frame
        // For outgoing and incoming messages.
        Messages messages = new Messages(name, notifications);
        messages.setVisible(true);
        t = new Thread(messages); // let it look after itself
        t.start();

    } // MessageCheckerGUI()


    /**
     * Setting a list of files into awt.list
     */
    public static void setList() {
        fileList.removeAll();
        File folder = new File("./text/");
        if (folder.exists()) {
            File[] files = folder.listFiles();

            for (File listOfFiles : files) {
                fileList.add(listOfFiles.getName());
            }
        }
    }

    /**
     * Read a file and the history will represent in TextArea.
     */
    public static void readFile(String path) {
        path = "./text/" + path;
        File file = new File(path);

        try {
            BufferedReader br = new BufferedReader(new FileReader(file));
            StringBuilder sb = new StringBuilder();
            while (br.ready()) {
                sb.append(br.readLine()).append("\n");
            }

            history.setText(sb.toString());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void windowClosing(WindowEvent we)
    {
        Window w = we.getWindow();
        w.dispose();
        System.exit(0);
    }

    /*
     * These are required for WindowListener, but we are
     * not interested in them, so they are empty methods.
     */
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
     */
    @Override
    public void actionPerformed(ActionEvent ae) { } // empty

} // class MessageCheckerGUI
