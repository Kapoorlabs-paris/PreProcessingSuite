package interactivePreprocessing;

import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import interactivePreprocessing.InteractiveMethods.ValueChange;
import preProcessing.GlobalThresholding;

public class PREauto implements ItemListener {
	
final InteractiveMethods parent;
	
	public PREauto (final InteractiveMethods parent) {
		
		this.parent = parent;
		
	}
	
	
	@Override
	public void itemStateChanged(final ItemEvent arg0) {

		if (arg0.getStateChange() == ItemEvent.DESELECTED)
			parent.autothreshwater = false;
		else if (arg0.getStateChange() == ItemEvent.SELECTED) {
			parent.autothreshwater = true;
			parent.thresholdWater = (float) ( GlobalThresholding.AutomaticThresholding(parent.CurrentView));
			parent.thresholdWaterslider.setValue(utility.ScrollbarUtils.computeScrollbarPositionFromValue(parent.thresholdWater, parent.thresholdMinWater, parent.thresholdMaxWater, parent.scrollbarSize));
		    parent.watertext.setText(parent.waterstring +  " = "  + parent.thresholdWater );
			parent.thresholdslider.validate();
			parent.thresholdslider.repaint();
			
			
		}

	}


}
