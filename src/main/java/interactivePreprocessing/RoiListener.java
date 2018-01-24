package interactivePreprocessing;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import interactivePreprocessing.InteractiveMethods.ValueChange;


public class RoiListener implements ActionListener {
	
	final InteractiveMethods parent;
	
	public RoiListener(final InteractiveMethods parent) {
		
		this.parent = parent;
	}

	
	@Override
	public void actionPerformed(ActionEvent e) {
		
		parent.updatePreview(ValueChange.ROI);
		
	}
	
	
	
}
