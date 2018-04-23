package interactivePreprocessing;

import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import dogGUI.CovistoDogPanel;
import interactivePreprocessing.InteractiveMethods.ValueChange;
import mserGUI.CovistoMserPanel;

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
			CovistoDogPanel.findmaxima.setVisible(true);
			CovistoDogPanel.findminima.setVisible(true);
			
			CovistoMserPanel.deltaS.setVisible(false);
			CovistoMserPanel.Unstability_ScoreS.setVisible(false);
			CovistoMserPanel.minDiversityS.setVisible(false);
			CovistoMserPanel.minSizeS.setVisible(false);
			CovistoMserPanel.maxSizeS.setVisible(false);
			CovistoMserPanel.findminimaMser.setVisible(false);
			CovistoMserPanel.findmaximaMser.setVisible(false);
			
			parent.displayWater.setVisible(false);
			parent.displayBinary.setVisible(false);
			parent.displayDist.setVisible(false);
			parent.autothreshold.setVisible(false);
			parent.thresholdWaterslider.setVisible(false);
			
			parent.SnakePanel.setVisible(true);
		
		}

	}
	

}
