package interactivePreprocessing;

import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import interactivePreprocessing.InteractiveMethods.ValueChange;

public class FindMinimaMserListener implements ItemListener {
	
	final InteractiveMethods  parent;
	
	public FindMinimaMserListener( InteractiveMethods parent) {
		
		this.parent = parent;
		
	}
	
	
	@Override
	public void itemStateChanged(final ItemEvent arg0) {
		
		
		
		
		if (arg0.getStateChange() == ItemEvent.DESELECTED) {
			parent.darktobright = false;
			parent.brighttodark = false;
		} else if (arg0.getStateChange() == ItemEvent.SELECTED) {
			parent.darktobright = false;
			parent.brighttodark = true;
			parent.updatePreview(ValueChange.MSER);
		}

	}
	

}

