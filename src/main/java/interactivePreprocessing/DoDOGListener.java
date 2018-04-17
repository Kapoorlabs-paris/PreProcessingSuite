package interactivePreprocessing;

import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import interactivePreprocessing.InteractiveMethods.ValueChange;

public class DoDOGListener implements ItemListener {
	
	final InteractiveMethods  parent;
	
	public DoDOGListener( InteractiveMethods parent) {
		
		this.parent = parent;
		
	}
	
	
	@Override
	public void itemStateChanged(final ItemEvent arg0) {
		
		
		
		
		if (arg0.getStateChange() == ItemEvent.DESELECTED) {
			parent.showDOG = false;
			parent.showMSER = false;
			parent.showWatershed = false;
		} else if (arg0.getStateChange() == ItemEvent.SELECTED) {
			parent.showDOG = true;
			parent.showMSER = false;
			parent.showWatershed = false;
			
			parent.sigmaslider.setVisible(true);
			parent.thresholdslider.setVisible(true);
			parent.findmaxima.setVisible(true);
			parent.findminima.setVisible(true);
			
			parent.deltaS.setVisible(false);
			parent.Unstability_ScoreS.setVisible(false);
			parent.minDiversityS.setVisible(false);
			parent.minSizeS.setVisible(false);
			parent.maxSizeS.setVisible(false);
			parent.findminimaMser.setVisible(false);
			parent.findmaximaMser.setVisible(false);
			
			parent.displayWater.setVisible(false);
			parent.displayBinary.setVisible(false);
			parent.displayDist.setVisible(false);
			parent.autothreshold.setVisible(false);
			parent.thresholdWaterslider.setVisible(false);
			
			parent.SnakePanel.setVisible(true);
		
		}

	}
	

}
