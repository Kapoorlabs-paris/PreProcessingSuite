package utility;

import java.awt.Color;

import javax.swing.JProgressBar;

public class ProgressBar {

	
	public static void SetProgressBar(JProgressBar jpb, double percent, String message) {

		jpb.setValue((int) Math.round(percent));
		jpb.setOpaque(true);
		jpb.setStringPainted(true);
		jpb.setString(message);

	}
	
	public static void SetProgressBar(JProgressBar jpb, String message) {

		
		
		jpb.setOpaque(true);
		jpb.setStringPainted(true);
		jpb.setString(message);
		
	}
	
	
	
}
