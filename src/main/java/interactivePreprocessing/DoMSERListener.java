package interactivePreprocessing;

import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import interactivePreprocessing.InteractiveMethods.ValueChange;

public class DoMSERListener implements ItemListener {
	
	final InteractiveMethods  parent;
	
	public DoMSERListener( InteractiveMethods parent) {
		
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
			parent.showMSER = true;
			parent.showWatershed = false;
			parent.sigmaslider.setVisible(false);
			parent.thresholdslider.setVisible(false);
			parent.findmaxima.setVisible(false);
			parent.findminima.setVisible(false);
			
			
			
			
			parent.deltaS.setVisible(true);
			parent.Unstability_ScoreS.setVisible(true);
			parent.minDiversityS.setVisible(true);
			parent.minSizeS.setVisible(true);
			parent.maxSizeS.setVisible(true);
			parent.findminimaMser.setVisible(true);
			parent.findmaximaMser.setVisible(true);
			
			
			parent.displayWater.setVisible(false);
			parent.displayBinary.setVisible(false);
			parent.displayDist.setVisible(false);
			parent.autothreshold.setVisible(false);
			parent.thresholdWaterslider.setVisible(false);
			
			parent.SnakePanel.setVisible(true);
			
		}

	}
	

}
