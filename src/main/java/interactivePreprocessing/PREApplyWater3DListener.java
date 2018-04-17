package interactivePreprocessing;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.SwingUtilities;

import org.scijava.command.Interactive;

import snakeSegmentation.SingleAllTrack;
import watershed3D.WatershedAll;

public class PREApplyWater3DListener implements ActionListener {

	final InteractiveMethods parent;
	
	public PREApplyWater3DListener(final InteractiveMethods parent) {
		

		this.parent = parent;
	}
	
	
	
	@Override
	public void actionPerformed(ActionEvent e) {
		

		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {

				goWater();

			}

		});
		
	}
	
	
	public void goWater() {


		WatershedAll dowater = new WatershedAll(parent);
		dowater.execute();

	}
	

}
