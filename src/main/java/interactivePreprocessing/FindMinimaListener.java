package interactivePreprocessing;

import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import interactivePreprocessing.InteractiveMethods.ValueChange;

public class FindMinimaListener implements ItemListener {
	
	final InteractiveMethods  parent;
	
	public FindMinimaListener( InteractiveMethods parent) {
		
		this.parent = parent;
		
	}
	
	
	@Override
	public void itemStateChanged(final ItemEvent arg0) {
		
		
		
		
		if (arg0.getStateChange() == ItemEvent.DESELECTED) {
			parent.lookForMinima = false;
			parent.lookForMaxima = false;
		} else if (arg0.getStateChange() == ItemEvent.SELECTED) {
			parent.lookForMinima = true;
			parent.lookForMaxima = false;
			parent.updatePreview(ValueChange.DOG);
		}

	}
	

}

