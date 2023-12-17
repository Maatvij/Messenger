package kursach.chat.client;

import kursach.network.TCPConnection;
import kursach.network.TCPConnectionListener;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class ClientWindow extends JFrame implements ActionListener, TCPConnectionListener {

    private static final String IP_ADDR = "localhost";
    private static final int PORT = 8189;
    private static final int WIDTH = 600;
    private static final int HEIGHT = 400;

    private final JTextArea log = new JTextArea();
    private final JTextField fieldInput = new JTextField();
    private final JLabel usernameLabel = new JLabel();


    private TCPConnection connection;
    private String username;

    public static void main(String[] args) {
        SwingUtilities.invokeLater(ClientWindow::new);
    }

    private ClientWindow() {
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setSize(WIDTH, HEIGHT);
        setLocationRelativeTo(null);
        setAlwaysOnTop(true);

        log.setEditable(false);
        log.setLineWrap(true);
        add(new JScrollPane(log), BorderLayout.CENTER);

        fieldInput.addActionListener(this);
        add(usernameLabel, BorderLayout.NORTH);
        add(fieldInput, BorderLayout.SOUTH);

        JButton sendButton = new JButton("Send");
        sendButton.addActionListener(this);
        add(sendButton, BorderLayout.EAST);

        setVisible(false);

        LoginForm loginForm = new LoginForm();
        loginForm.getLoginButton().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                username = loginForm.getUsername();
                if (!username.equals("")) {
                    setVisible(true);
                    loginForm.dispose();
                    try {
                        connection = new TCPConnection(ClientWindow.this, IP_ADDR, PORT);
                    } catch (Exception ex) {
                        printMsg("Connection exception: " + ex);
                    }
                    usernameLabel.setText(username);
                } else {
                    JOptionPane.showMessageDialog(null, "Please enter a username");
                }
            }
        });
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == fieldInput) {
            String msg = fieldInput.getText();
            if (msg.equals("")) return;
            fieldInput.setText(null);
            connection.sendString(username + ": " + msg);
        } else {
            String msg = fieldInput.getText();
            if (msg.equals("")) return;
            fieldInput.setText(null);
            connection.sendString(username + ": " + msg);
        }
    }

    @Override
    public void onConnectionReady(TCPConnection tcpConnection) {
        printMsg("Connection ready...");
    }

    @Override
    public void onReceiveString(TCPConnection tcpConnection, String value) {
        printMsg(value);
    }

    @Override
    public void onDisconnect(TCPConnection tcpConnection) {
        printMsg("Connection close");
    }

    @Override
    public void onException(TCPConnection tcpConnection, Exception e) {
        printMsg("Connection exception: " + e);
    }

    private synchronized void printMsg(String msg) {
        SwingUtilities.invokeLater(() -> {
            log.append(msg + "\n");
            log.setCaretPosition(log.getDocument().getLength());
        });
    }
}

class LoginForm extends JFrame {

    private JLabel label1;
    private JTextField usernameField;
    private JButton loginButton;

    public LoginForm() {
        setTitle("Login Form");
        setSize(400, 300);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        Container c = getContentPane();
        c.setLayout(null);

        label1 = new JLabel("Username");

        label1.setBounds(10, 50, 100, 20);

        c.add(label1);

        usernameField = new JTextField();
        usernameField.setBounds(120, 50, 120, 20);
        c.add(usernameField);

        loginButton = new JButton("Login");
        loginButton.setBounds(100, 150, 70, 20);
        c.add(loginButton);

        setVisible(true);
    }

    public String getUsername() {
        return usernameField.getText();
    }

    public JButton getLoginButton() {
        return loginButton;
    }
}