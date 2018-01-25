package interactivePreprocessing;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.SwingUtilities;

import snakeSegmentation.SingleSnake;
import snakeSegmentation.TSnake;
import snakeSegmentation.ZSnake;

public class PRETSnakeListener implements ActionListener {
	
	final InteractiveMethods parent;
	
	public PRETSnakeListener(final InteractiveMethods parent) {
		
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

	

		TSnake dosnake = new TSnake(parent);
		dosnake.execute();

	}

}
