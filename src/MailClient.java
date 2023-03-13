import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.io.*;
import java.net.*;
import java.awt.*;
import java.awt.event.*;
import java.nio.file.Path;

public class MailClient extends Frame {
    /* The stuff for the GUI. */
    private Button btSend = new Button("Send");
    private Button btClear = new Button("Clear");
    private Button btQuit = new Button("Quit");
    private Button btPickImage = new Button("Attach IMG");
    private Label serverLabel = new Label("Local mailserver:");
    private TextField serverField = new TextField("datacomm.bhsi.xyz", 40);
    private Label fromLabel = new Label("From:");
    private TextField fromField = new TextField("info@comit.dev", 40);
    private Label toLabel = new Label("To:");
    private TextField toField = new TextField("jfeldthus@gmail.com", 40);
    private Label usernameLabel = new Label("Username:");
    private TextField usernameField = new TextField("jakoblnhansen", 40);
    private Label passwordLabel = new Label("Password:");
    private TextField passwordField = new TextField("Jako2213yfm85bch", 40);
    private Label subjectLabel = new Label("Subject:");
    private TextField subjectField = new TextField("Hej", 40);
    private Label messageLabel = new Label("Message:");
    private TextArea messageText = new TextArea(10, 40);

    private Label choosenImageLabel = new Label("No attached image");

    /**
     * Create a new MailClient window with fields for entering all
     * the relevant information (From, To, Subject, and message).
     */
    public MailClient() {
        super("Java Mailclient");
	
	/* Create panels for holding the fields. To make it look nice,
	   create an extra panel for holding all the child panels. */
        Panel serverPanel = new Panel(new BorderLayout());
        Panel fromPanel = new Panel(new BorderLayout());
        Panel toPanel = new Panel(new BorderLayout());
        Panel subjectPanel = new Panel(new BorderLayout());
        Panel messagePanel = new Panel(new BorderLayout());
        Panel imagePanel = new Panel(new BorderLayout());
        Panel usernamePanel = new Panel(new BorderLayout());
        Panel passwordPanel = new Panel(new BorderLayout());
        imagePanel.add(choosenImageLabel, BorderLayout.CENTER);
        serverPanel.add(serverLabel, BorderLayout.NORTH);
        serverPanel.add(serverField, BorderLayout.SOUTH);
        fromPanel.add(fromLabel, BorderLayout.NORTH);
        fromPanel.add(fromField, BorderLayout.SOUTH);
        toPanel.add(toLabel, BorderLayout.NORTH);
        toPanel.add(toField, BorderLayout.SOUTH);
        usernamePanel.add(usernameLabel, BorderLayout.NORTH);
        usernamePanel.add(usernameField, BorderLayout.SOUTH);
        passwordPanel.add(passwordLabel, BorderLayout.NORTH);
        passwordPanel.add(passwordField, BorderLayout.SOUTH);
        subjectPanel.add(subjectLabel, BorderLayout.NORTH);
        subjectPanel.add(subjectField, BorderLayout.SOUTH);
        messagePanel.add(messageLabel, BorderLayout.NORTH);
        messagePanel.add(messageText, BorderLayout.CENTER);
        messagePanel.add(imagePanel, BorderLayout.SOUTH);
        Panel fieldPanel = new Panel(new GridLayout(0, 1));
        fieldPanel.add(serverPanel);
        fieldPanel.add(fromPanel);
        fieldPanel.add(toPanel);
        fieldPanel.add(usernamePanel);
        fieldPanel.add(passwordPanel);
        fieldPanel.add(subjectPanel);

	/* Create a panel for the buttons and add listeners to the
	   buttons. */
        Panel buttonPanel = new Panel(new GridLayout(1, 0));
        btSend.addActionListener(new SendListener());
        btPickImage.addActionListener(new PickImageListener());
        btClear.addActionListener(new ClearListener());
        btQuit.addActionListener(new QuitListener());
        buttonPanel.add(btSend);
        buttonPanel.add(btPickImage);
        buttonPanel.add(btClear);
        buttonPanel.add(btQuit);

        /* Add, pack, and show. */
        add(fieldPanel, BorderLayout.NORTH);
        add(messagePanel, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);
        pack();
        show();
    }

    public MailClient getOuter() {
        return this;
    }

    /* Quit. */
    class PickImageListener implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            JFileChooser filePicker = new JFileChooser();
            // We allow only jpg images to be sent.
            filePicker.addChoosableFileFilter(new FileNameExtensionFilter("Images jpg", "jpg", "jpeg"));
            filePicker.setAcceptAllFileFilterUsed(false);
            int option = filePicker.showOpenDialog(getOuter());
            if (option == JFileChooser.APPROVE_OPTION) {
                File file = filePicker.getSelectedFile();
                choosenImageLabel.setText(file.getAbsolutePath());
            } else {
                choosenImageLabel.setText("No attached image");
            }

        }
    }

    static public void main(String argv[]) {
        new MailClient();
    }

    /* Handler for the Send-button. */
    class SendListener implements ActionListener {
        public void actionPerformed(ActionEvent event) {
            System.out.println("Sending mail");

            /* Check that we have the local mailserver */
            if ((serverField.getText()).equals("")) {
                System.out.println("Need name of local mailserver!");
                return;
            }

            /* Check that we have the sender and recipient. */
            if ((fromField.getText()).equals("")) {
                System.out.println("Need sender!");
                return;
            }
            if ((toField.getText()).equals("")) {
                System.out.println("Need recipient!");
                return;
            }

            /* Create the message */
            Message mailMessage;
            if (choosenImageLabel.getText().trim().equals("No attached image")) {
                mailMessage = new Message(fromField.getText(), toField.getText(), usernameField.getText(), passwordField.getText(), subjectField.getText(), messageText.getText());
            } else {
                mailMessage = new Message(fromField.getText(), toField.getText(), usernameField.getText(), passwordField.getText(), subjectField.getText(), messageText.getText(), Path.of(choosenImageLabel.getText()));
            }

	    /* Check that the message is valid, i.e., sender and
	       recipient addresses look ok. */
            if (!mailMessage.isValid()) {
                return;
            }

//	    /* Create the envelope, open the connection and try to send
//	       the message. */
            try {
                Envelope envelope = new Envelope(mailMessage, serverField.getText());
            } catch (UnknownHostException e) {
                /* If there is an error, do not go further */
                return;
            }
            try {
                Envelope envelope = new Envelope(mailMessage, serverField.getText());
                SMTPConnection connection = new SMTPConnection(envelope);
                connection.send(envelope);
                connection.close();
            } catch (IOException error) {
                System.out.println("Sending failed: " + error);
                return;
            }
            System.out.println("Mail sent succesfully!");
        }
    }

    /* Clear the fields on the GUI. */
    class ClearListener implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            System.out.println("Clearing fields");
            fromField.setText("");
            toField.setText("");
            subjectField.setText("");
            messageText.setText("");
        }
    }

    /* Quit. */
    class QuitListener implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            System.exit(0);
        }
    }
}