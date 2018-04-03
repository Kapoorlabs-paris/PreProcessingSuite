package interactivePreprocessing;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.SwingUtilities;

import interactivePreprocessing.InteractiveMethods.ValueChange;
import snakeSegmentation.SingleAllTrack;
import snakeSegmentation.SingleZTrack;
import snakeSegmentation.ThreeDTimetrack;

public class PRE3DTListener implements ActionListener {
	
	final InteractiveMethods parent;
	
	public PRE3DTListener(final InteractiveMethods parent) {
		
		this.parent = parent;
	}
	
	@Override
	public void actionPerformed(final ActionEvent arg0) {

		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {

				go();

			}

		});

	}

	public void go() {

		SingleAllTrack dosnakeZ = new SingleAllTrack(parent);
		dosnakeZ.execute();
		
		

	}

}
