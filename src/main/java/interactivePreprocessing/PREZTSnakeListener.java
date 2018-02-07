package interactivePreprocessing;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.SwingUtilities;

import snakeSegmentation.TSnake;
import snakeSegmentation.ZTSnake;

public class PREZTSnakeListener implements ActionListener {

	
	final InteractiveMethods parent;
	
	public PREZTSnakeListener(final InteractiveMethods parent) {
		
		this.parent = parent;
		
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {

				go();

			}

		});

		
		
		
	}
	
	public void go() {

		

		ZTSnake dosnake = new ZTSnake(parent);
		dosnake.execute();

	}
	
	
	
}
