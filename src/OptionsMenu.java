// Daniel Chen
// 26 May 2020
// gui configuration menu

import java.awt.event.ActionListener;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;

public class OptionsMenu extends JDialog { // how to dialogs work I think I'll just use frames and timers instead
	private JPanel main;
	JButton ok;
	public OptionsMenu(ActionListener eventHandler) {
		main = new JPanel();
		ok = new JButton("OK");
		ok.addActionListener(eventHandler);

		main.setLayout(new BoxLayout(main, BoxLayout.PAGE_AXIS));
		main.add(ok);
		
		add(main);
		setModalityType(ModalityType.DOCUMENT_MODAL);
		setModal(true);
		setTitle("Options");
		setVisible(true);
	}
}