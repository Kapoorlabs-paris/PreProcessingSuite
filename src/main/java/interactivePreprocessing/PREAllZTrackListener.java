package interactivePreprocessing;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.SwingUtilities;

import interactivePreprocessing.InteractiveMethods.ValueChange;
import snakeSegmentation.SingleAllTrack;
import snakeSegmentation.SingleZTrack;

public class PREAllZTrackListener implements ActionListener {
	
	final InteractiveMethods parent;
	
	public PREAllZTrackListener(final InteractiveMethods parent) {
		
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


		SingleAllTrack dosnake = new SingleAllTrack(parent);
		dosnake.execute();

	}

}
