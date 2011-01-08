import javax.swing.*;
import java.awt.*;
import java.util.*;
import java.lang.*;
import java.text.*;
import java.awt.event.*;
import java.io.*;

public class Facelimiter extends JApplet {

    public static String hostsPath = System.getenv("windir")+"\\system32\\drivers\\etc\\hosts";

    public JLabel statusLabel;
    public JButton enableButton;
    public JButton disableButton;

	public void createGUI() {
		JPanel p = new JPanel();
        p.add(new JLabel("Facebook is"));

        statusLabel = new JLabel();
        p.add(statusLabel);

        enableButton = new JButton("Enable");
        enableButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent ae) {
                deleteFromHosts("www.facebook.com");
                refreshStatus();
            }
        });
		p.add(enableButton);

        disableButton = new JButton("Disable");
        disableButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent ae) {
                addToHosts("www.facebook.com");
                refreshStatus();
            }
        });
        p.add(disableButton);

        getContentPane().add(p);
	}

	public void init() {

        //Execute a job on the event-dispatching thread:
        //creating this applet's GUI.
        try {
            SwingUtilities.invokeAndWait(new Runnable() {
                public void run() {
                    createGUI();
                }
            });
        } catch (Exception e) {
            System.err.println("createGUI didn't successfully complete");
            e.printStackTrace();
        }
    }

	public void start() {
		System.out.println("start()");
        refreshStatus();
	}

    public void refreshStatus() {
        if (checkInHosts("www.facebook.com")) {
            statusLabel.setText("disabled");
            statusLabel.setForeground(new Color(82, 163, 0));

            enableButton.setEnabled(true);
            disableButton.setEnabled(false);
        }
        else {
            statusLabel.setText("enabled");
            statusLabel.setForeground(new Color(102, 0, 0));

            enableButton.setEnabled(false);
            disableButton.setEnabled(true);
        }
    }

    /**
      Read hosts file line by line and look for matching hostnames
    */
    public boolean checkInHosts(String hostname) {
         boolean found = false;
        File file = new File(hostsPath);

        try {
            //use buffering, reading one line at a time
            //FileReader always assumes default encoding is OK!
            BufferedReader input = new BufferedReader(new FileReader(file));
            try {
                String line = null; //not declared within while loop
                /*
                * readLine is a bit quirky :
                * it returns the content of a line MINUS the newline.
                * it returns null only for the END of the stream.
                * it returns an empty String if two newlines appear in a row.
                */
                while ((line = input.readLine()) != null && !found) {
                    if (line.lastIndexOf(hostname) != -1) {
                        found = true;
                    }
                }
            }
            finally {
                input.close();
            }
        }
        catch (IOException ex){
            ex.printStackTrace();
        }

        return found;
    }

    public void deleteFromHosts(String hostname) {
        if (!checkInHosts(hostname)) {
            return;
        }

        StringBuilder contents = new StringBuilder();
        File file = new File(hostsPath);

        try {
            //use buffering, reading one line at a time
            //FileReader always assumes default encoding is OK!
            BufferedReader input = new BufferedReader(new FileReader(file));
            try {
                String line = null; //not declared within while loop
                /*
                * readLine is a bit quirky :
                * it returns the content of a line MINUS the newline.
                * it returns null only for the END of the stream.
                * it returns an empty String if two newlines appear in a row.
                */
                while ((line = input.readLine()) != null) {
                    // save the line only if it does not contain hostname to be deleted
                    if (line.lastIndexOf(hostname) == -1) {
                        contents.append(line);
                        contents.append(System.getProperty("line.separator"));
                    }
                }
            }
            finally {
                input.close();
            }
        }
        catch (IOException ex){
            ex.printStackTrace();
        }

        try {
            //use buffering
            Writer output = new BufferedWriter(new FileWriter(file));

            try {
                //FileWriter always assumes default encoding is OK!
                output.write(contents.toString());
            }
            finally {
                output.close();
            }
        }
        catch (IOException ex){
            ex.printStackTrace();
        }
    }

    public void addToHosts(String hostname) {
        if (checkInHosts(hostname)) {
            return;
        }

        StringBuilder contents = new StringBuilder();
        File file = new File(hostsPath);

        try {
            //use buffering, reading one line at a time
            //FileReader always assumes default encoding is OK!
            BufferedReader input = new BufferedReader(new FileReader(file));
            try {
                String line = null; //not declared within while loop
                /*
                * readLine is a bit quirky :
                * it returns the content of a line MINUS the newline.
                * it returns null only for the END of the stream.
                * it returns an empty String if two newlines appear in a row.
                */
                while ((line = input.readLine()) != null) {
                    contents.append(line);
                    contents.append(System.getProperty("line.separator"));
                }
            }
            finally {
                input.close();
            }
        }
        catch (IOException ex){
            ex.printStackTrace();
        }

        contents.append("127.0.0.1 "+hostname);
        contents.append(System.getProperty("line.separator"));

        try {
            //use buffering
            Writer output = new BufferedWriter(new FileWriter(file));

            try {
                //FileWriter always assumes default encoding is OK!
                output.write(contents.toString());
            }
            finally {
                output.close();
            }
        }
        catch (IOException ex){
            ex.printStackTrace();
        }
    }

	public void stop() {
		System.out.println("stop()");
	}

	public void destroy() {
		System.out.println("destroy()");
        Runtime.getRuntime().halt(1);
	}

}