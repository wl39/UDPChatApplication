
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Generate an extra frame for a particular user.
 */
public class WindowMessage extends Frame implements ActionListener {
    private String id;
    private String username;
    public String fqdn;
    public String portNumber;

    private TextField input;
    private TextArea messages;

    private String t = "";

    WindowMessage(String id, String username, String fqdn, String portNumber) {
        super(username);
        this.id = id;
        this.username = username;
        this.fqdn = fqdn;
        this.portNumber = portNumber;

        setLayout(new FlowLayout());
        setBounds(0, 0, 600, 350);

        Panel p;

        input = new TextField(50);
        p = new Panel();
        p.add(new Label("To " + username + ": "));
        p.add(input);
        add(p);

        messages = new TextArea("", 15, 60, TextArea.SCROLLBARS_VERTICAL_ONLY);
        p = new Panel();
        p.add(new Label(username));
        p.add(messages);
        add(p);

        input.addActionListener(this);

        setVisible(true);
    }


    @Override
    public void actionPerformed(ActionEvent actionEvent) {
        t = input.getText();
        String s = "<- You  : " + t + "\n";

        if (Messages.sendMessage(t, username)) {
            messages.insert(s, 0);
            MessageCheckerGUI.setList();
            MessageCheckerGUI.readFile(username + ".txt");
        } else {
            messages.insert("Something wrong happens", 0);
        }

        input.setText("");
    }

    /**
     * Insert message into a TextArea.
     * @param message message
     */
    public void insertTx(String message) {
        String s = "<- You " + id + " : " + message + "\n";
        messages.insert(s, 0);
    }

    /**
     * Insert message into a TextArea.
     * @param rx message
     */
    public void insertRx(String rx) {
        messages.insert(rx, 0);
    }
}

