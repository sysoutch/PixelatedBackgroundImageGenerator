package ch.sysout.pixelatedbackgroundimagegenerator;

import javax.swing.UIManager;

import com.formdev.flatlaf.FlatLightLaf;

public class Main {

	public Main() {
		MainFrame frame = new MainFrame();
		frame.setSize(800, 600);
		frame.setLocationRelativeTo(null);
		frame.setVisible(true);
	}

	public static void main(String[] args) {
		try {
			UIManager.setLookAndFeel(new FlatLightLaf());
		} catch (Exception ex) {
			System.err.println("Failed to initialize LaF");
		}
		new Main();
	}
}
