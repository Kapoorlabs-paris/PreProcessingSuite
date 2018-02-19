package interactivePreprocessing;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.SwingUtilities;

import interactivePreprocessing.InteractiveMethods.ValueChange;
import snakeSegmentation.SingleZTrack;

public class PRESingleZTrackListener implements ActionListener {
	
	final InteractiveMethods parent;
	
	public PRESingleZTrackListener(final InteractiveMethods parent) {
		
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


		SingleZTrack dosnake = new SingleZTrack(parent);
		dosnake.execute();

	}

}
