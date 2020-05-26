// Daniel Chen
// 13 May 2020
// main window for user interaction

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

public class MainMenu extends JFrame implements ActionListener {
    private JPanel verticalBox, playerBox; // panels used to hold things
    private JLabel title; // "Connect 4" header
    private JLabel[] nameLabels, playerLabels; // instructions for user interaction
    private JComboBox<String>[] playerSelect; // select player
    private JTextField[] nameSelect; // select name
    private JButton[] buttons; // continue or quit

    public MainMenu() {
        // create all objects
        verticalBox = new JPanel();
        playerBox = new JPanel();
        title = new JLabel("Connect 4");
        nameLabels = new JLabel[] {new JLabel("Name: "), new JLabel("Name: ")};
        playerLabels = new JLabel[] {new JLabel("Player type: "), new JLabel("Player type: ")};
        playerSelect = new JComboBox[] {new JComboBox<String>(), new JComboBox<String>()};
        nameSelect = new JTextField[] {new JTextField("Player 1"), new JTextField("Player 2")};
        buttons = new JButton[] {new JButton("Start game"), new JButton("Options"), new JButton("Exit")};

        verticalBox.setLayout(new BoxLayout(verticalBox, BoxLayout.PAGE_AXIS));
        verticalBox.add(title);
        verticalBox.add(Box.createVerticalStrut(10)); // add spacing
        title.setAlignmentX(JLabel.CENTER_ALIGNMENT);
        verticalBox.add(playerBox);
        verticalBox.add(Box.createVerticalStrut(10));
        for (JButton butt : buttons) { // add all buttons and make them do something
            butt.setAlignmentX(JButton.CENTER_ALIGNMENT);
            butt.addActionListener(this);
            verticalBox.add(butt);
            verticalBox.add(Box.createVerticalStrut(10));
        }

        playerBox.setLayout(new GridLayout(2, 4, 5, 5));
        for (int i = 0; i < nameSelect.length; i++) {
            for (Component c : new Component[] {nameLabels[i], nameSelect[i], playerLabels[i], playerSelect[i]}) {
                playerBox.add(c);
            }
        }

        for (String s : new String[] {"Human", "Computer (easy)", "Computer (hard)", "Computer (impossible)"}) {
            for (JComboBox<String> cb : playerSelect) {
                cb.addItem(s);
            }
        }

        add(verticalBox);
        setLayout(new BoxLayout(getContentPane(), BoxLayout.PAGE_AXIS));
        getRootPane().setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20)); // make margins so it's nicer to use
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setTitle("Connect 4");
        setSize(800, 300);
    }

    public void actionPerformed(ActionEvent event) {
        Object e = event.getSource();
        if (e.equals(buttons[0])) {

        } else if (e.equals(buttons[1])) {

        } else if (e.equals(buttons[2])) {
            dispose(); // quit
            // TODO: make sure to implement every other panel too
            // TODO: make sure to dispose frames (only modal dialogs should be frames)
        }
    }

    public static void main(String[] args) {
        MainMenu win = new MainMenu();
        win.setVisible(true);
    }
}
