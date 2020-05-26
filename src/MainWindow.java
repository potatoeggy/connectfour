// Daniel Chen
// 13 May 2020
// main window for user interaction

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

public class MainWindow extends JFrame implements ActionListener {
    private NewGameMenu newGameMenu;
    public MainWindow() {
        newGameMenu = new NewGameMenu(this);

        add(newGameMenu);
        newGameMenu.setVisible(true);
        setLayout(new BoxLayout(getContentPane(), BoxLayout.PAGE_AXIS));
        getRootPane().setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20)); // make margins so it's nicer to use
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setTitle("Connect 4");
        setSize(800, 300);
    }

    public void actionPerformed (ActionEvent event) {
        Object e = event.getSource();
        if (e.equals(newGameMenu.buttons[0])) {
            System.out.println(0);
        } else if (e.equals(newGameMenu.buttons[1])) {
            System.out.println("1");
        } else if (e.equals(newGameMenu.buttons[2])) {
            dispose();
        }
    }

    public static void main(String[] args) {
        MainWindow win = new MainWindow();
        win.setVisible(true);
    }
}
