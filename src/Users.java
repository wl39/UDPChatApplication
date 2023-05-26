/*
 * CS2003 coursework Net2 demo
 * Saleem Bhatti, Oct 2018
 */

import multicast.HeartBeat;
import utility.UserWriter;

import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.*;

public class Users implements Runnable {

    private java.awt.List users;
    private Notifications notifications;
    private static String username;

    Users(java.awt.List u, Notifications n, String username) {
        users = u;
        notifications = n;
        Users.username = username;
    }

    @Override
    public void run() {
        while (true) { // forever
            //UDP
            HeartBeat.setUsername(username);
            HeartBeat.userCheck();
            // Check the list of users
            ArrayList<String> checklist = MessageCheckerCommon.users_list();

            /*
            ** If any of the currently listed users are no longer on the checklist,
            ** they have now gone offline.
            */
            for (int u = 0; u < users.getItemCount(); ++u) {
                String s_u = users.getItem(u);
                boolean found = false;

                for (int c = 0; c < checklist.size(); ++c) {
                    String s_c = checklist.get(c);
                    if (s_u.equals(s_c)) {
                        found = true;
                        checklist.remove(c); // finished checking this one
                        break;
                    }
                }

                if (!found) { // user has gone offline
                    notifications.notify(s_u + " - offline.");
                    users.remove(u);
                }
            }

            /*
            ** If the checklist contains users not on the list of current users,
            ** they must have just come online.
            */
            for (String s_c : checklist) {
                notifications.notify(s_c + " - online.");
                users.add(s_c);
            }
            checklist.clear(); // not strictly necessary

        } // while(true)

    } // run()

} // class Users
