package interactivePreprocessing;

import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import interactivePreprocessing.InteractiveMethods.ValueChange;

public class DoWatershedListener implements ItemListener {
	
	final InteractiveMethods  parent;
	
	public DoWatershedListener( InteractiveMethods parent) {
		
		this.parent = parent;
		
	}
	
	
	@Override
	public void itemStateChanged(final ItemEvent arg0) {
		
		
		
		
		if (arg0.getStateChange() == ItemEvent.DESELECTED) {
			parent.showDOG = false;
			parent.showMSER = false;
			parent.showWatershed = false;
		} else if (arg0.getStateChange() == ItemEvent.SELECTED) {
			parent.showDOG = false;
			parent.showMSER = false;
			parent.showWatershed = true;
			
			parent.sigmaslider.setVisible(false);
			parent.thresholdslider.setVisible(false);
			parent.findmaxima.setVisible(false);
			parent.findminima.setVisible(false);
			
			parent.deltaS.setVisible(false);
			parent.Unstability_ScoreS.setVisible(false);
			parent.minDiversityS.setVisible(false);
			parent.minSizeS.setVisible(false);
			parent.maxSizeS.setVisible(false);
			parent.findminimaMser.setVisible(false);
			parent.findmaximaMser.setVisible(false);
			
			
			
			parent.displayWater.setVisible(true);
			parent.displayBinary.setVisible(true);
			parent.displayDist.setVisible(true);
			parent.autothreshold.setVisible(true);
			parent.thresholdWaterslider.setVisible(true);
			
			parent.SnakePanel.setVisible(false);
		
			
		}

	}
	

}
