package interactivePreprocessing;

import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import interactivePreprocessing.InteractiveMethods.ValueChange;

public class FindMaximaMserListener implements ItemListener {
	
	final InteractiveMethods  parent;
	
	public FindMaximaMserListener( InteractiveMethods parent) {
		
		this.parent = parent;
		
	}
	
	
	@Override
	public void itemStateChanged(final ItemEvent arg0) {
		
		
		
		
		if (arg0.getStateChange() == ItemEvent.DESELECTED) {
			parent.darktobright = false;
			parent.brighttodark = false;
		} else if (arg0.getStateChange() == ItemEvent.SELECTED) {
			parent.darktobright = true;
			parent.brighttodark = false;
			parent.updatePreview(ValueChange.MSER);
		}

	}
	

}

