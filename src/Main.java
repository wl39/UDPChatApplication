/**
 * Main class to run the programme execute this class.
 */
public class Main {

    /**
     * Main method.
     * @param args arguments can only get one argument which is a custom user name.
     */
    public static void main(String[] args)
    {
        String id;

        if (args.length == 1) { id = args[0]; }
        else { id = System.getProperty("user.name"); }

        System.out.println("Using id: " + id);

        MessageCheckerGUI m = new MessageCheckerGUI(id);
        m.setVisible(true); // Make GUI visible.
    }
}
