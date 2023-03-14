import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.io.*;
import java.awt.*;
import java.awt.event.*;
import java.nio.file.Path;

public class MailClient extends Frame {
    /* The stuff for the GUI. */
    private Button btSend = new Button("Send");
    private Button btClear = new Button("Clear");
    private Button btQuit = new Button("Quit");
    private Button btPickImage = new Button("Attach IMG");
    private Label serverLabel = new Label("Mail-server:");
    private TextField serverField = new TextField("smtp.gmail.com", 90);
    private Label portLabel = new Label("Port:");
    private TextField portField = new TextField("465", 90);
    //    smtp.gmail.com:465
    private Label fromLabel = new Label("From:");
    private TextField fromField = new TextField("", 90);
    private Label toLabel = new Label("To:");
    private TextField toField = new TextField("", 90);
    private Label passwordLabel = new Label("Password(Create APP PASSWORD, not normal password, in your Google Account or select a different mail server):");
    private TextField passwordField = new TextField("", 90);
    private Label subjectLabel = new Label("Subject:");
    private TextField subjectField = new TextField("default subject", 90);
    private Label messageLabel = new Label("Message:");
    private TextArea messageText = new TextArea(16, 90);

    private Label choosenImageLabel = new Label("No attached image");

    /**
     * Create a new MailClient window with fields for entering all
     * the relevant information (From, To, Subject, and message).
     */
    public MailClient() {
        super("Java Mail-client");
        messageText.setText("This is some content.\n" +
                "Attach an image if you want to.\n" +
                "Port 465 for TLS, port 25 for no TLS\n" +
                "\n" +
                "If you want to send email through smtp.gmail.com,\n" +
                "you need to create an APP password.\n" +
                "Your normal Gmail password won't work.\n" +
                "\n" +
                "You can also send an email with\nmail server datacomm.bhsi.xyz, on port 25.\n" +
                "Use from address info@comit.dev.\n");

	/* Create panels for holding the fields. To make it look nice,
	   create an extra panel for holding all the child panels. */
        Panel serverPanel = new Panel(new BorderLayout());
        Panel portPanel = new Panel(new BorderLayout());
        Panel fromPanel = new Panel(new BorderLayout());
        Panel toPanel = new Panel(new BorderLayout());
        Panel subjectPanel = new Panel(new BorderLayout());
        Panel messagePanel = new Panel(new BorderLayout());
        Panel imagePanel = new Panel(new BorderLayout());
        Panel passwordPanel = new Panel(new BorderLayout());
        imagePanel.add(choosenImageLabel, BorderLayout.CENTER);
        serverPanel.add(serverLabel, BorderLayout.NORTH);
        serverPanel.add(serverField, BorderLayout.SOUTH);
        portPanel.add(portLabel, BorderLayout.NORTH);
        portPanel.add(portField, BorderLayout.SOUTH);
        fromPanel.add(fromLabel, BorderLayout.NORTH);
        fromPanel.add(fromField, BorderLayout.SOUTH);
        toPanel.add(toLabel, BorderLayout.NORTH);
        toPanel.add(toField, BorderLayout.SOUTH);
        passwordPanel.add(passwordLabel, BorderLayout.NORTH);
        passwordPanel.add(passwordField, BorderLayout.SOUTH);
        subjectPanel.add(subjectLabel, BorderLayout.NORTH);
        subjectPanel.add(subjectField, BorderLayout.SOUTH);
        messagePanel.add(messageLabel, BorderLayout.NORTH);
        messagePanel.add(messageText, BorderLayout.CENTER);
        messagePanel.add(imagePanel, BorderLayout.SOUTH);
        Panel fieldPanel = new Panel(new GridLayout(0, 1));
        fieldPanel.add(serverPanel);
        fieldPanel.add(portPanel);
        fieldPanel.add(fromPanel);
        fieldPanel.add(toPanel);
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

    static public void main(String[] argv) {
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
                mailMessage = new Message(fromField.getText(), toField.getText(), subjectField.getText(), messageText.getText());
            } else {
                mailMessage = new Message(fromField.getText(), toField.getText(), subjectField.getText(), messageText.getText(), Path.of(choosenImageLabel.getText()));
            }

            /* Check that the message is valid, i.e., sender and recipient addresses look ok. */
            if (!mailMessage.isValid()) {
                return;
            }

            /* Create the envelope, open the connection and try to send the message. */
            try {
                Envelope envelope = new Envelope(mailMessage, serverField.getText(), Integer.parseInt(portField.getText()), fromField.getText(), passwordField.getText());
                SMTPConnection connection = new SMTPConnection(envelope);
                connection.send(envelope);
                connection.close();
            } catch (IOException error) {
                throw new RuntimeException("Sending failed: " + error);
            }
            System.out.println("Mail sent successfully!");
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