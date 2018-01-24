package interactivePreprocessing;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.SwingUtilities;

import snakeSegmentation.SingleSnake;

public class PRESinglesnakeListener implements ActionListener {
	
	final InteractiveMethods parent;
	
	public PRESinglesnakeListener(final InteractiveMethods parent) {
		
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

	

		SingleSnake dosnake = new SingleSnake(parent);
		dosnake.execute();

	}

}
