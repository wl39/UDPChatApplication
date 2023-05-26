package utility;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MessageChecker {
    private static final Pattern DATE = Pattern.compile("^[0-9]{4}(1[0-2]|0[1-9])(3[01]|[12][0-9]|0[1-9])-(?:2[0-3]|[01][0-9])[0-5][0-9][0-5][0-9].[0-9][0-9][0-9]+$");
    private static final Pattern USERNAME = Pattern.compile("^[a-z0-9]+$");
    private static final Pattern TYPE = Pattern.compile("^[a-z0-9_]+$");
    private static final Pattern FQDN = Pattern.compile("^[a-zA-Z0-9-_.]+$");

    public static boolean check(String toCheck, String type) {
        if (toCheck == null) {
            return false;
        }

        Matcher matcher;

        switch (type) {
            case "date":
                matcher = DATE.matcher(toCheck);
                return matcher.find();
            case "username":
                matcher = USERNAME.matcher(toCheck);
                return matcher.find();
            case "type":
                matcher = TYPE.matcher(toCheck);
                return matcher.find();
            case "fqdn":
                matcher = FQDN.matcher(toCheck);
                return matcher.find();
            case "portnumber":
                int portnumber = Integer.parseInt(toCheck);
                return (portnumber >= 0 && portnumber <= 65535);
            default:
                break;
        }
        return false;
    }
}
