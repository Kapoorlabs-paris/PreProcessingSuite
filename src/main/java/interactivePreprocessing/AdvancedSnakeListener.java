package interactivePreprocessing;

import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import ij.gui.GenericDialog;

public class AdvancedSnakeListener implements ItemListener {
	
	
	final InteractiveMethods parent;
	
	
	public AdvancedSnakeListener(final InteractiveMethods parent) {
		
		this.parent = parent;
		
	}


	@Override
	public void itemStateChanged(ItemEvent e) {
		
		if (e.getStateChange() == ItemEvent.DESELECTED) {
			
		parent.advancedSnake = false;	
			
		}
		
       if (e.getStateChange() == ItemEvent.SELECTED) {
			
    	   parent.advancedSnake = true;
			AdvancedDialog();
			
		}
		
}
	
	public boolean AdvancedDialog() {

		// dialog
		GenericDialog gd = new GenericDialog("Snake Advanced");
		gd.addNumericField("Distance_Search", parent.DistMax, 0);
		gd.addNumericField("Displacement_min", parent.Displacement_min, 2);
		gd.addNumericField("Displacement_max", parent.Displacement_max, 2);
		gd.addNumericField("Threshold_dist_positive", parent.Threshold_dist_positive, 0);
		gd.addNumericField("Threshold_dist_negative", parent.Threshold_dist_negative, 0);
		gd.addNumericField("Inv_alpha_min", parent.Inv_alpha_min, 2);
		gd.addNumericField("Inv_alpha_max", parent.Inv_alpha_max, 2);
		gd.addNumericField("Reg_min", parent.regmin, 2);
		gd.addNumericField("Reg_max", parent.regmax, 2);
		gd.addNumericField("Mul_factor", parent.Mul_factor, 4);
		// show dialog
		gd.showDialog();

		parent.DistMax = (int) gd.getNextNumber();
		parent.Displacement_min = gd.getNextNumber();
		parent.Displacement_max = gd.getNextNumber();
		parent.Threshold_dist_positive = gd.getNextNumber();
		parent.Threshold_dist_negative = gd.getNextNumber();
		parent.Inv_alpha_min = gd.getNextNumber();
		parent.Inv_alpha_max = gd.getNextNumber();
		parent.regmin = gd.getNextNumber();
		parent.regmax = gd.getNextNumber();
		parent.Mul_factor = gd.getNextNumber();

		return !gd.wasCanceled();

	}
	
	
	

}
