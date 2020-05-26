// Daniel Chen
// 13 May 2020
// main window for user interaction

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

public class MainWindow extends JFrame implements ActionListener {
    private JPanel verticalBox, playerBox;
    private JLabel title;
    private JLabel[] nameLabels, playerLabels;
    private JComboBox<String>[] playerSelect;
    private JTextField[] nameSelect;
    private JButton[] buttons;

    MainWindow() {
        verticalBox = new JPanel();
        playerBox = new JPanel();
        title = new JLabel("Connect 4");
        nameLabels = new JLabel[] {new JLabel("Name:"), new JLabel("Name:")};
        playerLabels = new JLabel[] {new JLabel("Player type:"), new JLabel("Player type:")};
        playerSelect = new JComboBox[] {new JComboBox<String>(), new JComboBox<String>()};
        nameSelect = new JTextField[] {new JTextField("Player 1"), new JTextField("Player 2")};
        buttons = new JButton[] {new JButton("Start game"), new JButton("Options"), new JButton("Exit")};

        verticalBox.setLayout(new BoxLayout(verticalBox, BoxLayout.PAGE_AXIS));
        verticalBox.add(title);
        verticalBox.add(Box.createVerticalStrut(10));
        title.setAlignmentX(JLabel.CENTER_ALIGNMENT);
        verticalBox.add(playerBox);
        verticalBox.add(Box.createVerticalStrut(10));
        for (JButton butt : buttons) {
            butt.setAlignmentX(JButton.CENTER_ALIGNMENT);
            butt.addActionListener(this);
            verticalBox.add(butt);
            verticalBox.add(Box.createVerticalStrut(10));
        }

        playerBox.setLayout(new GridLayout(2, 4, 5, 5));
        for (int i = 0; i < nameSelect.length; i++) {
            playerBox.add(nameLabels[i]);
            playerBox.add(nameSelect[i]);
            playerBox.add(playerLabels[i]);
            playerBox.add(playerSelect[i]);
        }

        add(verticalBox);
        setLayout(new BoxLayout(getContentPane(), BoxLayout.PAGE_AXIS));
        getRootPane().setBorder(BorderFactory.createLineBorder(Color.WHITE, 10));
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setBackground(Color.WHITE);
        setTitle("Connect 4");
        setSize(500, 300);
    }

    public void actionPerformed(ActionEvent event) {

    }
    public static void main(String[] args) {
        UIManager.getLookAndFeelDefaults().put("Button.font", new Font("sans-serif", Font.BOLD, 14));
        MainWindow win = new MainWindow();
        win.setVisible(true);
    }
}
