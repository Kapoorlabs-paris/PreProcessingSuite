package interactivePreprocessing;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.SwingUtilities;

import snake3D.SnakeAll;

public class PREApplySnake3DListener implements ActionListener {
	
	final InteractiveMethods parent;
	
	public PREApplySnake3DListener(final InteractiveMethods parent) {
		
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

	

		SnakeAll dosnake = new SnakeAll(parent);
		dosnake.execute();

	}

}
